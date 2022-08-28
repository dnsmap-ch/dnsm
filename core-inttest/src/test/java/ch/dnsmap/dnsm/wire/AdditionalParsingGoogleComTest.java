package ch.dnsmap.dnsm.wire;

import static ch.dnsmap.dnsm.DnsClass.IN;
import static ch.dnsmap.dnsm.wire.util.DnsAssert.assertDnsHeader;
import static ch.dnsmap.dnsm.wire.util.DnsAssert.assertDnsQuestion;
import static ch.dnsmap.dnsm.wire.util.DnsAssert.assertDnsRecordIp4;
import static ch.dnsmap.dnsm.wire.util.DnsAssert.assertDnsRecordIp6;
import static ch.dnsmap.dnsm.wire.util.DnsAssert.assertDnsRecordNs;
import static ch.dnsmap.dnsm.wire.util.Utils.jumpToAdditionalSection;
import static ch.dnsmap.dnsm.wire.util.Utils.jumpToAnswerSection;
import static ch.dnsmap.dnsm.wire.util.Utils.jumpToAuthoritySection;
import static ch.dnsmap.dnsm.wire.util.Utils.jumpToQuestionSection;
import static org.assertj.core.api.Assertions.assertThat;

import ch.dnsmap.dnsm.DnsQueryClass;
import ch.dnsmap.dnsm.DnsQueryType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Header;
import ch.dnsmap.dnsm.HeaderId;
import ch.dnsmap.dnsm.Question;
import ch.dnsmap.dnsm.Ttl;
import ch.dnsmap.dnsm.record.ResourceRecord;
import ch.dnsmap.dnsm.record.ResourceRecordA;
import ch.dnsmap.dnsm.record.ResourceRecordAaaa;
import ch.dnsmap.dnsm.record.ResourceRecordNs;
import ch.dnsmap.dnsm.record.type.Ip4;
import ch.dnsmap.dnsm.record.type.Ip6;
import ch.dnsmap.dnsm.record.type.Ns;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class AdditionalParsingGoogleComTest {

  private static final String GOOGLE_COM = "google.com.";
  private static final String NS_1_GOOGLE_COM = "ns1.google.com";
  private static final String NS_2_GOOGLE_COM = "ns2.google.com";
  private static final String NS_3_GOOGLE_COM = "ns3.google.com";
  private static final String NS_4_GOOGLE_COM = "ns4.google.com";

  private static final HeaderId MESSAGE_ID = HeaderId.of(40833);
  private static final byte[] FLAGS = {(byte) 0x81, (byte) 0x00};
  private static final Domain DOMAIN = Domain.of(GOOGLE_COM);
  private static final Domain QUESTION_DOMAIN = DOMAIN;
  private static final Domain AUTHORITY_DOMAIN = DOMAIN;
  private static final Ttl TTL = Ttl.of(172800);

  private static final Domain NS1_DOMAIN = Domain.of(NS_1_GOOGLE_COM);
  private static final Domain NS2_DOMAIN = Domain.of(NS_2_GOOGLE_COM);
  private static final Domain NS3_DOMAIN = Domain.of(NS_3_GOOGLE_COM);
  private static final Domain NS4_DOMAIN = Domain.of(NS_4_GOOGLE_COM);
  private static final Ns NS1 = new Ns(NS1_DOMAIN);
  private static final Ns NS2 = new Ns(NS2_DOMAIN);
  private static final Ns NS3 = new Ns(NS3_DOMAIN);
  private static final Ns NS4 = new Ns(NS4_DOMAIN);

  private ByteArrayOutputStream dnsBytes;

  @BeforeEach
  void setupDnsBytes() throws IOException {
    dnsBytes = new ByteArrayOutputStream();
    dnsBytes.write(DNS_BYTES_HEADER);
    dnsBytes.write(DNS_BYTES_QUESTION);
    dnsBytes.write(DNS_BYTES_AUTHORITATIVE_NAMESERVERS);
    dnsBytes.write(DNS_BYTES_ADDITIONAL);
  }

  @Test
  void testDnsHeaderInputParsing() {
    var dnsInput = DnsInput.fromWire(dnsBytes.toByteArray());
    var header = dnsInput.getHeader();
    assertDnsHeader(header, MESSAGE_ID, FLAGS, 1, 0, 4, 8);
  }

  @Test
  void testDnsQuestionInputParsing() {
    var dnsInput = DnsInput.fromWire(dnsBytes.toByteArray());
    var questions = jumpToQuestionSection(dnsInput);
    assertDnsQuestion(questions, QUESTION_DOMAIN, DnsQueryType.NS, DnsQueryClass.IN);
  }

  @Test
  void testDnsAnswerInputParsing() {
    var dnsInput = DnsInput.fromWire(dnsBytes.toByteArray());
    var answers = jumpToAnswerSection(dnsInput);
    assertThat(answers.size()).isEqualTo(0);
  }

  @Test
  void testDnsAuthorityInputParsing() {
    var dnsInput = DnsInput.fromWire(dnsBytes.toByteArray());

    var authorities = jumpToAuthoritySection(dnsInput);

    assertThat(authorities.size()).isEqualTo(4);
    assertDnsRecordNs(authorities.get(0), AUTHORITY_DOMAIN, IN, TTL, NS2);
    assertDnsRecordNs(authorities.get(1), AUTHORITY_DOMAIN, IN, TTL, NS1);
    assertDnsRecordNs(authorities.get(2), AUTHORITY_DOMAIN, IN, TTL, NS3);
    assertDnsRecordNs(authorities.get(3), AUTHORITY_DOMAIN, IN, TTL, NS4);
  }

  @Test
  void testDnsAdditionalInputParsing() {
    var dnsInput = DnsInput.fromWire(dnsBytes.toByteArray());

    var additional = jumpToAdditionalSection(dnsInput);

    assertThat(additional.size()).isEqualTo(8);
    assertDnsRecordIp6(additional.get(0), NS2_DOMAIN, IN, TTL,
        Ip6.of("2001:4860:4802:34::a"));
    assertDnsRecordIp4(additional.get(1), NS2_DOMAIN, IN, TTL, Ip4.of("216.239.34.10"));
    assertDnsRecordIp6(additional.get(2), NS1_DOMAIN, IN, TTL,
        Ip6.of("2001:4860:4802:32::a"));
    assertDnsRecordIp4(additional.get(3), NS1_DOMAIN, IN, TTL, Ip4.of("216.239.32.10"));
    assertDnsRecordIp6(additional.get(4), NS3_DOMAIN, IN, TTL,
        Ip6.of("2001:4860:4802:36::a"));
    assertDnsRecordIp4(additional.get(5), NS3_DOMAIN, IN, TTL, Ip4.of("216.239.36.10"));
    assertDnsRecordIp6(additional.get(6), NS4_DOMAIN, IN, TTL,
        Ip6.of("2001:4860:4802:38::a"));
    assertDnsRecordIp4(additional.get(7), NS4_DOMAIN, IN, TTL, Ip4.of("216.239.38.10"));
  }

  @Test
  void testOutputParsing() {
    var header = composeHeader();
    var question = composeQuestion();
    var answer = new LinkedList<ResourceRecord>();
    var authoritative = composeAuthoritative();
    var additional = composeAdditional();

    var dnsOutput = DnsOutput.toWire(header, question, answer, authoritative, additional);

    assertThat(dnsOutput).satisfies(output -> {
      assertThat(output.getHeader()).isEqualTo(DNS_BYTES_HEADER);
      assertThat(output.getQuestion()).isEqualTo(DNS_BYTES_QUESTION);
      assertThat(output.getAnswers()).isEqualTo(new byte[0]);
      assertThat(output.getAuthoritatives()).isEqualTo(DNS_BYTES_AUTHORITATIVE_NAMESERVERS);
      assertThat(output.getAdditional()).isEqualTo(DNS_BYTES_ADDITIONAL);
    });
  }

  private static Header composeHeader() {
    return new Header(MESSAGE_ID, FLAGS, 1, 0, 4, 8);
  }

  private static Question composeQuestion() {
    return new Question(QUESTION_DOMAIN, DnsQueryType.NS, DnsQueryClass.IN);
  }

  private static List<ResourceRecord> composeAuthoritative() {
    List<ResourceRecord> authorities = new ArrayList<>(4);
    authorities.add(new ResourceRecordNs(AUTHORITY_DOMAIN, IN, TTL, NS2));
    authorities.add(new ResourceRecordNs(AUTHORITY_DOMAIN, IN, TTL, NS1));
    authorities.add(new ResourceRecordNs(AUTHORITY_DOMAIN, IN, TTL, NS3));
    authorities.add(new ResourceRecordNs(AUTHORITY_DOMAIN, IN, TTL, NS4));
    return authorities;
  }

  private static List<ResourceRecord> composeAdditional() {
    List<ResourceRecord> authorities = new ArrayList<>(8);
    authorities.add(new ResourceRecordAaaa(NS2.ns(), IN, TTL, Ip6.of("2001:4860:4802:34::a")));
    authorities.add(new ResourceRecordA(NS2.ns(), IN, TTL, Ip4.of("216.239.34.10")));
    authorities.add(new ResourceRecordAaaa(NS1.ns(), IN, TTL, Ip6.of("2001:4860:4802:32::a")));
    authorities.add(new ResourceRecordA(NS1.ns(), IN, TTL, Ip4.of("216.239.32.10")));
    authorities.add(new ResourceRecordAaaa(NS3.ns(), IN, TTL, Ip6.of("2001:4860:4802:36::a")));
    authorities.add(new ResourceRecordA(NS3.ns(), IN, TTL, Ip4.of("216.239.36.10")));
    authorities.add(new ResourceRecordAaaa(NS4.ns(), IN, TTL, Ip6.of("2001:4860:4802:38::a")));
    authorities.add(new ResourceRecordA(NS4.ns(), IN, TTL, Ip4.of("216.239.38.10")));
    return authorities;
  }

  private static final byte[] DNS_BYTES_HEADER = new byte[] {
      (byte) 0x9f, (byte) 0x81, (byte) 0x81, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00,
      (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x08
  };

  private static final byte[] DNS_BYTES_QUESTION = new byte[] {
      (byte) 0x06, (byte) 0x67, (byte) 0x6f, (byte) 0x6f, (byte) 0x67, (byte) 0x6c, (byte) 0x65,
      (byte) 0x03, (byte) 0x63, (byte) 0x6f, (byte) 0x6d, (byte) 0x00, (byte) 0x00, (byte) 0x02,
      (byte) 0x00, (byte) 0x01
  };

  private static final byte[] DNS_BYTES_AUTHORITATIVE_NAMESERVERS = new byte[] {
      (byte) 0xc0, (byte) 0x0c, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0x00,
      (byte) 0x02, (byte) 0xa3, (byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x03, (byte) 0x6e,
      (byte) 0x73, (byte) 0x32, (byte) 0xc0, (byte) 0x0c, (byte) 0xc0, (byte) 0x0c, (byte) 0x00,
      (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0xa3, (byte) 0x00,
      (byte) 0x00, (byte) 0x06, (byte) 0x03, (byte) 0x6e, (byte) 0x73, (byte) 0x31, (byte) 0xc0,
      (byte) 0x0c, (byte) 0xc0, (byte) 0x0c, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x01,
      (byte) 0x00, (byte) 0x02, (byte) 0xa3, (byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x03,
      (byte) 0x6e, (byte) 0x73, (byte) 0x33, (byte) 0xc0, (byte) 0x0c, (byte) 0xc0, (byte) 0x0c,
      (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0xa3,
      (byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x03, (byte) 0x6e, (byte) 0x73, (byte) 0x34,
      (byte) 0xc0, (byte) 0x0c
  };

  private static final byte[] DNS_BYTES_ADDITIONAL = new byte[] {
      (byte) 0xc0, (byte) 0x28, (byte) 0x00, (byte) 0x1c, (byte) 0x00, (byte) 0x01, (byte) 0x00,
      (byte) 0x02, (byte) 0xa3, (byte) 0x00, (byte) 0x00, (byte) 0x10, (byte) 0x20, (byte) 0x01,
      (byte) 0x48, (byte) 0x60, (byte) 0x48, (byte) 0x02, (byte) 0x00, (byte) 0x34, (byte) 0x00,
      (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0a,
      (byte) 0xc0, (byte) 0x28, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00,
      (byte) 0x02, (byte) 0xa3, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0xd8, (byte) 0xef,
      (byte) 0x22, (byte) 0x0a, (byte) 0xc0, (byte) 0x3a, (byte) 0x00, (byte) 0x1c, (byte) 0x00,
      (byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0xa3, (byte) 0x00, (byte) 0x00, (byte) 0x10,
      (byte) 0x20, (byte) 0x01, (byte) 0x48, (byte) 0x60, (byte) 0x48, (byte) 0x02, (byte) 0x00,
      (byte) 0x32, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
      (byte) 0x00, (byte) 0x0a, (byte) 0xc0, (byte) 0x3a, (byte) 0x00, (byte) 0x01, (byte) 0x00,
      (byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0xa3, (byte) 0x00, (byte) 0x00, (byte) 0x04,
      (byte) 0xd8, (byte) 0xef, (byte) 0x20, (byte) 0x0a, (byte) 0xc0, (byte) 0x4c, (byte) 0x00,
      (byte) 0x1c, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0xa3, (byte) 0x00,
      (byte) 0x00, (byte) 0x10, (byte) 0x20, (byte) 0x01, (byte) 0x48, (byte) 0x60, (byte) 0x48,
      (byte) 0x02, (byte) 0x00, (byte) 0x36, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
      (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0a, (byte) 0xc0, (byte) 0x4c, (byte) 0x00,
      (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0xa3, (byte) 0x00,
      (byte) 0x00, (byte) 0x04, (byte) 0xd8, (byte) 0xef, (byte) 0x24, (byte) 0x0a, (byte) 0xc0,
      (byte) 0x5e, (byte) 0x00, (byte) 0x1c, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x02,
      (byte) 0xa3, (byte) 0x00, (byte) 0x00, (byte) 0x10, (byte) 0x20, (byte) 0x01, (byte) 0x48,
      (byte) 0x60, (byte) 0x48, (byte) 0x02, (byte) 0x00, (byte) 0x38, (byte) 0x00, (byte) 0x00,
      (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0a, (byte) 0xc0,
      (byte) 0x5e, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x02,
      (byte) 0xa3, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0xd8, (byte) 0xef, (byte) 0x26,
      (byte) 0x0a
  };
}
