package ch.dnsmap.dnsm.wire;

import static ch.dnsmap.dnsm.DnsType.NS;
import static ch.dnsmap.dnsm.wire.util.DnsAssert.assertDnsHeader;
import static ch.dnsmap.dnsm.wire.util.DnsAssert.assertDnsQuestion;
import static ch.dnsmap.dnsm.wire.util.Utils.jumpToAnswerSection;
import static ch.dnsmap.dnsm.wire.util.Utils.jumpToAuthoritySection;
import static ch.dnsmap.dnsm.wire.util.Utils.jumpToQuestionSection;
import static org.assertj.core.api.Assertions.assertThat;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsQueryClass;
import ch.dnsmap.dnsm.DnsQueryType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class AdditionalParsingGoogleComTest {

  private static final byte[] DNS_BYTES_HEADER = new byte[] {
      (byte) 0x9f, (byte) 0x81, (byte) 0x81, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00,
      (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x09
  };
  private static final byte[] DNS_BYTES_QUESTION = new byte[] {
      (byte) 0x06, (byte) 0x67, (byte) 0x6f, (byte) 0x6f, (byte) 0x67, (byte) 0x6c, (byte) 0x65,
      (byte) 0x03, (byte) 0x63, (byte) 0x6f, (byte) 0x6d, (byte) 0x00, (byte) 0x00, (byte) 0x02,
      (byte) 0x00, (byte) 0x01
  };
  private static final byte[] DNS_BYTES_ANSWER = new byte[] {
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
      (byte) 0xc0, (byte) 0x0c, (byte) 0xc0, (byte) 0x28, (byte) 0x00, (byte) 0x1c, (byte) 0x00,
      (byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0xa3, (byte) 0x00, (byte) 0x00, (byte) 0x10,
      (byte) 0x20, (byte) 0x01, (byte) 0x48, (byte) 0x60, (byte) 0x48, (byte) 0x02, (byte) 0x00,
      (byte) 0x34, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
      (byte) 0x00, (byte) 0x0a
  };
  private static final byte[] DNS_BYTES_ADDITIONAL = new byte[] {
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
      (byte) 0x0a, (byte) 0x00, (byte) 0x00, (byte) 0x29, (byte) 0x10, (byte) 0x00, (byte) 0x00,
      (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
  };
  private static final int MESSAGE_ID = 40833;
  private static final byte[] FLAGS = {(byte) 0x81, (byte) 0x00};
  private static final String HOST_NAME = "google.com.";
  private static final long TTL = 172800;

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
    assertDnsHeader(header, MESSAGE_ID, FLAGS, 1, 0, 4, 9);
  }

  @Test
  void testDnsQuestionInputParsing() {
    var dnsInput = DnsInput.fromWire(dnsBytes.toByteArray());
    var questions = jumpToQuestionSection(dnsInput);
    assertDnsQuestion(questions, HOST_NAME, DnsQueryType.NS, DnsQueryClass.IN);
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
    assertThat(authorities.get(0)).satisfies(authority -> {
      assertThat(authority.getName().getCanonical()).isEqualTo(HOST_NAME);
      assertThat(authority.getDnsType()).isEqualTo(NS);
      assertThat(authority.getDnsClass()).isEqualTo(DnsClass.IN);
      assertThat(authority.getTtl()).isEqualTo(TTL);
    });
    assertThat(authorities.get(1)).satisfies(authority -> {
      assertThat(authority.getName().getCanonical()).isEqualTo(HOST_NAME);
      assertThat(authority.getDnsType()).isEqualTo(NS);
      assertThat(authority.getDnsClass()).isEqualTo(DnsClass.IN);
      assertThat(authority.getTtl()).isEqualTo(TTL);
    });
    assertThat(authorities.get(2)).satisfies(authority -> {
      assertThat(authority.getName().getCanonical()).isEqualTo(HOST_NAME);
      assertThat(authority.getDnsType()).isEqualTo(NS);
      assertThat(authority.getDnsClass()).isEqualTo(DnsClass.IN);
      assertThat(authority.getTtl()).isEqualTo(TTL);
    });
    assertThat(authorities.get(3)).satisfies(authority -> {
      assertThat(authority.getName().getCanonical()).isEqualTo(HOST_NAME);
      assertThat(authority.getDnsType()).isEqualTo(NS);
      assertThat(authority.getDnsClass()).isEqualTo(DnsClass.IN);
      assertThat(authority.getTtl()).isEqualTo(TTL);
    });
  }
}
