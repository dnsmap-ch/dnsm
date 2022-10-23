package ch.dnsmap.dnsm.wire;

import static ch.dnsmap.dnsm.DnsClass.IN;
import static ch.dnsmap.dnsm.Domain.root;
import static ch.dnsmap.dnsm.header.HeaderBitFlags.QR;
import static ch.dnsmap.dnsm.header.HeaderBitFlags.RA;
import static ch.dnsmap.dnsm.header.HeaderBitFlags.RD;
import static ch.dnsmap.dnsm.header.HeaderOpcode.QUERY;
import static ch.dnsmap.dnsm.header.HeaderRcode.NO_ERROR;
import static ch.dnsmap.dnsm.wire.util.DnsAssert.assertDnsHeader;
import static ch.dnsmap.dnsm.wire.util.DnsAssert.assertDnsQuestion;
import static ch.dnsmap.dnsm.wire.util.DnsAssert.assertDnsRecordCname;
import static ch.dnsmap.dnsm.wire.util.DnsAssert.assertDnsRecordIp4;
import static ch.dnsmap.dnsm.wire.util.Utils.jumpToAdditionalSection;
import static ch.dnsmap.dnsm.wire.util.Utils.jumpToAnswerSection;
import static ch.dnsmap.dnsm.wire.util.Utils.jumpToAuthoritySection;
import static ch.dnsmap.dnsm.wire.util.Utils.jumpToQuestionSection;
import static org.assertj.core.api.Assertions.assertThat;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsQueryClass;
import ch.dnsmap.dnsm.DnsQueryType;
import ch.dnsmap.dnsm.DnsType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Question;
import ch.dnsmap.dnsm.Ttl;
import ch.dnsmap.dnsm.header.Header;
import ch.dnsmap.dnsm.header.HeaderCount;
import ch.dnsmap.dnsm.header.HeaderFlags;
import ch.dnsmap.dnsm.header.HeaderId;
import ch.dnsmap.dnsm.record.ResourceRecord;
import ch.dnsmap.dnsm.record.ResourceRecordA;
import ch.dnsmap.dnsm.record.ResourceRecordCname;
import ch.dnsmap.dnsm.record.ResourceRecordOpaque;
import ch.dnsmap.dnsm.record.type.Cname;
import ch.dnsmap.dnsm.record.type.Ip4;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class CnameParsingTest {

  private static final String MICROSOFT_CH = "microsoft.ch.";
  private static final String WWW_MICROSOFT_CH = "www.microsoft.ch.";

  private static final HeaderId MESSAGE_ID = HeaderId.of(39600);
  private static final HeaderFlags FLAGS = new HeaderFlags(QUERY, NO_ERROR, RD, RA, QR);
  private static final HeaderCount COUNT = HeaderCount.of(1, 6, 0, 1);
  private static final Header HEADER = new Header(MESSAGE_ID, FLAGS, COUNT);
  private static final Domain DOMAIN = Domain.of(WWW_MICROSOFT_CH);
  private static final Domain QUESTION_DOMAIN = DOMAIN;
  private static final Domain ANSWER_DOMAIN = Domain.of(MICROSOFT_CH);
  private static final Cname ANSWER_CNAME = new Cname(Domain.of(MICROSOFT_CH));
  private static final Ttl TTL = Ttl.of(3600);

  private static final Ip4 IP_V4_1 = Ip4.of("20.103.85.33");
  private static final Ip4 IP_V4_2 = Ip4.of("20.112.52.29");
  private static final Ip4 IP_V4_3 = Ip4.of("20.53.203.50");
  private static final Ip4 IP_V4_4 = Ip4.of("20.81.111.85");
  private static final Ip4 IP_V4_5 = Ip4.of("20.84.181.62");

  private ByteArrayOutputStream dnsBytes;

  @BeforeEach
  void setupDnsBytes() throws IOException {
    dnsBytes = new ByteArrayOutputStream();
    dnsBytes.write(DNS_BYTES_HEADER);
    dnsBytes.write(DNS_BYTES_QUESTION);
    dnsBytes.write(DNS_BYTES_ANSWER);
    dnsBytes.write(DNS_BYTES_ADDITIONAL);
  }

  @Test
  void testDnsHeaderInputParsing() {
    var dnsInput = DnsInput.fromWire(dnsBytes.toByteArray());
    var header = dnsInput.getHeader();
    assertDnsHeader(HEADER, header);
  }

  @Test
  void testDnsQuestionInputParsing() {
    var dnsInput = DnsInput.fromWire(dnsBytes.toByteArray());
    var questions = jumpToQuestionSection(dnsInput);
    assertDnsQuestion(questions, QUESTION_DOMAIN, DnsQueryType.A, DnsQueryClass.IN);
  }

  @Test
  void testDnsAnswerInputParsing() {
    var dnsInput = DnsInput.fromWire(dnsBytes.toByteArray());

    var answers = jumpToAnswerSection(dnsInput);

    assertThat(answers.size()).isEqualTo(6);
    assertDnsRecordCname(answers.get(0), QUESTION_DOMAIN, IN, TTL, ANSWER_CNAME);
    assertDnsRecordIp4(answers.get(1), ANSWER_DOMAIN, IN, TTL, IP_V4_1);
    assertDnsRecordIp4(answers.get(2), ANSWER_DOMAIN, IN, TTL, IP_V4_2);
    assertDnsRecordIp4(answers.get(3), ANSWER_DOMAIN, IN, TTL, IP_V4_3);
    assertDnsRecordIp4(answers.get(4), ANSWER_DOMAIN, IN, TTL, IP_V4_4);
    assertDnsRecordIp4(answers.get(5), ANSWER_DOMAIN, IN, TTL, IP_V4_5);
  }

  @Test
  void testDnsAuthorityInputParsing() {
    var dnsInput = DnsInput.fromWire(dnsBytes.toByteArray());
    var authorities = jumpToAuthoritySection(dnsInput);
    assertThat(authorities.size()).isEqualTo(0);
  }

  @Test
  void testDnsAdditionalInputParsing() {
    var dnsInput = DnsInput.fromWire(dnsBytes.toByteArray());

    var additional = jumpToAdditionalSection(dnsInput);

    assertThat(additional.size()).isEqualTo(1);
    assertThat(additional.get(0)).satisfies(answer -> {
      assertThat(answer.name()).isEqualTo(root());
      assertThat(answer.getDnsType()).isEqualTo(DnsType.UNKNOWN);
      assertThat(answer.dnsClass()).isEqualTo(DnsClass.UNKNOWN);
      assertThat(answer.ttl()).isEqualTo(Ttl.of(0));
      assertThat(((ResourceRecordOpaque) answer).opaqueData().opaque()).isEqualTo(new byte[0]);
    });
  }

  @Test
  void testCnameOutputParsing() {
    var header = composeHeader();
    var question = composeQuestion();
    var answer = composeAnswer();
    var authoritative = new LinkedList<ResourceRecord>();
    var additional = new LinkedList<ResourceRecord>();

    var dnsOutput = DnsOutput.toWire(header, question, answer, authoritative, additional);

    assertThat(dnsOutput.getHeader()).isEqualTo(DNS_BYTES_HEADER);
    assertThat(dnsOutput.getQuestion()).isEqualTo(DNS_BYTES_QUESTION);
    assertThat(dnsOutput.getAnswers()).isEqualTo(DNS_BYTES_ANSWER);
  }

  private static Header composeHeader() {
    return new Header(MESSAGE_ID, FLAGS, COUNT);
  }

  private static Question composeQuestion() {
    return new Question(QUESTION_DOMAIN, DnsQueryType.A, DnsQueryClass.IN);
  }

  private static List<ResourceRecord> composeAnswer() {
    List<ResourceRecord> answers = new ArrayList<>(6);
    answers.add(new ResourceRecordCname(QUESTION_DOMAIN, IN, TTL, new Cname(ANSWER_DOMAIN)));
    answers.add(new ResourceRecordA(ANSWER_DOMAIN, IN, TTL, IP_V4_1));
    answers.add(new ResourceRecordA(ANSWER_DOMAIN, IN, TTL, IP_V4_2));
    answers.add(new ResourceRecordA(ANSWER_DOMAIN, IN, TTL, IP_V4_3));
    answers.add(new ResourceRecordA(ANSWER_DOMAIN, IN, TTL, IP_V4_4));
    answers.add(new ResourceRecordA(ANSWER_DOMAIN, IN, TTL, IP_V4_5));
    return answers;
  }

  private static final byte[] DNS_BYTES_HEADER = new byte[] {
      (byte) 0x9a, (byte) 0xb0, (byte) 0x81, (byte) 0x80, (byte) 0x00, (byte) 0x01, (byte) 0x00,
      (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01
  };

  private static final byte[] DNS_BYTES_QUESTION = new byte[] {
      (byte) 0x03, (byte) 0x77, (byte) 0x77, (byte) 0x77, (byte) 0x09, (byte) 0x6d, (byte) 0x69,
      (byte) 0x63, (byte) 0x72, (byte) 0x6f, (byte) 0x73, (byte) 0x6f, (byte) 0x66, (byte) 0x74,
      (byte) 0x02, (byte) 0x63, (byte) 0x68, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00,
      (byte) 0x01
  };

  private static final byte[] DNS_BYTES_ANSWER = new byte[] {
      (byte) 0xc0, (byte) 0x0c, (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x01, (byte) 0x00,
      (byte) 0x00, (byte) 0x0e, (byte) 0x10, (byte) 0x00, (byte) 0x02, (byte) 0xc0, (byte) 0x10,
      (byte) 0xc0, (byte) 0x10, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00,
      (byte) 0x00, (byte) 0x0e, (byte) 0x10, (byte) 0x00, (byte) 0x04, (byte) 0x14, (byte) 0x67,
      (byte) 0x55, (byte) 0x21, (byte) 0xc0, (byte) 0x10, (byte) 0x00, (byte) 0x01, (byte) 0x00,
      (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x0e, (byte) 0x10, (byte) 0x00, (byte) 0x04,
      (byte) 0x14, (byte) 0x70, (byte) 0x34, (byte) 0x1d, (byte) 0xc0, (byte) 0x10, (byte) 0x00,
      (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x0e, (byte) 0x10,
      (byte) 0x00, (byte) 0x04, (byte) 0x14, (byte) 0x35, (byte) 0xcb, (byte) 0x32, (byte) 0xc0,
      (byte) 0x10, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
      (byte) 0x0e, (byte) 0x10, (byte) 0x00, (byte) 0x04, (byte) 0x14, (byte) 0x51, (byte) 0x6f,
      (byte) 0x55, (byte) 0xc0, (byte) 0x10, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01,
      (byte) 0x00, (byte) 0x00, (byte) 0x0e, (byte) 0x10, (byte) 0x00, (byte) 0x04, (byte) 0x14,
      (byte) 0x54, (byte) 0xb5, (byte) 0x3e
  };

  private static final byte[] DNS_BYTES_ADDITIONAL = new byte[] {
      (byte) 0x00, (byte) 0x00, (byte) 0x29, (byte) 0x04, (byte) 0xd0, (byte) 0x00, (byte) 0x00,
      (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
  };
}
