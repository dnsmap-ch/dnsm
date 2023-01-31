package ch.dnsmap.dnsm.dnsm.wire;

import static ch.dnsmap.dnsm.DnsClass.IN;
import static ch.dnsmap.dnsm.dnsm.wire.util.DnsAssert.assertDnsHeader;
import static ch.dnsmap.dnsm.dnsm.wire.util.DnsAssert.assertDnsQuestion;
import static ch.dnsmap.dnsm.dnsm.wire.util.DnsAssert.assertDnsRecordMx;
import static ch.dnsmap.dnsm.dnsm.wire.util.Utils.jumpToAnswerSection;
import static ch.dnsmap.dnsm.dnsm.wire.util.Utils.jumpToQuestionSection;
import static ch.dnsmap.dnsm.dnsm.wire.util.Utils.udpDnsInput;
import static ch.dnsmap.dnsm.dnsm.wire.util.Utils.udpDnsOutput;
import static ch.dnsmap.dnsm.header.HeaderBitFlags.QR;
import static ch.dnsmap.dnsm.header.HeaderBitFlags.RA;
import static ch.dnsmap.dnsm.header.HeaderBitFlags.RD;
import static ch.dnsmap.dnsm.header.HeaderOpcode.QUERY;
import static ch.dnsmap.dnsm.header.HeaderRcode.NO_ERROR;
import static org.assertj.core.api.Assertions.assertThat;

import ch.dnsmap.dnsm.DnsQueryClass;
import ch.dnsmap.dnsm.DnsQueryType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Question;
import ch.dnsmap.dnsm.Ttl;
import ch.dnsmap.dnsm.header.Header;
import ch.dnsmap.dnsm.header.HeaderCount;
import ch.dnsmap.dnsm.header.HeaderFlags;
import ch.dnsmap.dnsm.header.HeaderId;
import ch.dnsmap.dnsm.record.ResourceRecord;
import ch.dnsmap.dnsm.record.ResourceRecordMx;
import ch.dnsmap.dnsm.record.type.Mx;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class MxParsingTest {

  private static final String ADDERE_CH = "addere.ch.";
  private static final String MX_ADDERE_CH = "mx1.addere.ch.";

  private static final HeaderId MESSAGE_ID = HeaderId.of(50614);
  private static final HeaderFlags FLAGS = new HeaderFlags(QUERY, NO_ERROR, RA, QR, RD);
  private static final HeaderCount COUNT = HeaderCount.of(1, 1, 0, 0);
  private static final Header HEADER = new Header(MESSAGE_ID, FLAGS, COUNT);
  private static final Domain DOMAIN = Domain.of(ADDERE_CH);
  private static final Domain QUESTION_DOMAIN = DOMAIN;
  private static final Domain ANSWER_DOMAIN = Domain.of(MX_ADDERE_CH);
  private static final Ttl TTL = Ttl.of(10800);

  private static final Domain MX_DOMAIN = Domain.of(MX_ADDERE_CH);
  private static final Mx MX = Mx.of(5, MX_DOMAIN);

  private ByteArrayOutputStream dnsBytes;

  @BeforeEach
  void setupDnsBytes() throws IOException {
    dnsBytes = new ByteArrayOutputStream();
    dnsBytes.write(DNS_BYTES_HEADER);
    dnsBytes.write(DNS_BYTES_QUESTION);
    dnsBytes.write(DNS_BYTES_ANSWER);
  }

  @Test
  void testDnsHeaderInputParsing() {
    var dnsInput = udpDnsInput(dnsBytes);
    var header = dnsInput.getHeader();
    assertDnsHeader(HEADER, header);
  }

  @Test
  void testDnsQuestionInputParsing() {
    var dnsInput = udpDnsInput(dnsBytes);
    var questions = jumpToQuestionSection(dnsInput);
    assertDnsQuestion(questions, QUESTION_DOMAIN, DnsQueryType.MX, DnsQueryClass.IN);
  }

  @Test
  void testDnsAnswerInputParsing() {
    var dnsInput = udpDnsInput(dnsBytes);

    var answers = jumpToAnswerSection(dnsInput);

    assertThat(answers.size()).isEqualTo(1);
    assertDnsRecordMx(answers.get(0), QUESTION_DOMAIN, IN, TTL, MX);
  }

  @Test
  void testOutputParsing() {
    var header = new Header(MESSAGE_ID, FLAGS, COUNT);
    var question = new Question(QUESTION_DOMAIN, DnsQueryType.MX, DnsQueryClass.IN);
    var answer = List.<ResourceRecord>of(
        new ResourceRecordMx(QUESTION_DOMAIN, IN, TTL, Mx.of(5, ANSWER_DOMAIN)));
    List<ResourceRecord> authoritative = List.of();
    List<ResourceRecord> additional = List.of();

    var dnsOutput = udpDnsOutput(header, question, answer, authoritative, additional);

    assertThat(dnsOutput).satisfies(output -> {
      assertThat(output.getHeader()).isEqualTo(DNS_BYTES_HEADER);
      assertThat(output.getQuestion()).isEqualTo(DNS_BYTES_QUESTION);
      assertThat(output.getAnswers()).isEqualTo(DNS_BYTES_ANSWER);
    });
  }

  private static final byte[] DNS_BYTES_HEADER = new byte[]{
      (byte) 0xc5, (byte) 0xb6, (byte) 0x81, (byte) 0x80, (byte) 0x00, (byte) 0x01, (byte) 0x00,
      (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
  };

  private static final byte[] DNS_BYTES_QUESTION = new byte[]{
      (byte) 0x06, (byte) 0x61, (byte) 0x64, (byte) 0x64, (byte) 0x65, (byte) 0x72, (byte) 0x65,
      (byte) 0x02, (byte) 0x63, (byte) 0x68, (byte) 0x00, (byte) 0x00, (byte) 0x0f, (byte) 0x00,
      (byte) 0x01,
  };

  private static final byte[] DNS_BYTES_ANSWER = new byte[]{
      (byte) 0xc0, (byte) 0x0c, (byte) 0x00, (byte) 0x0f, (byte) 0x00, (byte) 0x01, (byte) 0x00,
      (byte) 0x00, (byte) 0x2a, (byte) 0x30, (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x05,
      (byte) 0x03, (byte) 0x6d, (byte) 0x78, (byte) 0x31, (byte) 0xc0, (byte) 0x0c,
  };
}
