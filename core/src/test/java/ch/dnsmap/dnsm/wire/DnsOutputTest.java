package ch.dnsmap.dnsm.wire;

import static ch.dnsmap.dnsm.header.HeaderId.ofZero;
import static ch.dnsmap.dnsm.header.HeaderOpcode.QUERY;
import static ch.dnsmap.dnsm.header.HeaderRcode.NO_ERROR;
import static org.assertj.core.api.Assertions.assertThat;

import ch.dnsmap.dnsm.DnsQueryClass;
import ch.dnsmap.dnsm.DnsQueryType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Question;
import ch.dnsmap.dnsm.header.Header;
import ch.dnsmap.dnsm.header.HeaderCount;
import ch.dnsmap.dnsm.header.HeaderFlags;
import java.util.List;
import org.junit.jupiter.api.Test;

class DnsOutputTest {

  private static final HeaderFlags QUERY_FLAGS = new HeaderFlags(QUERY, NO_ERROR);
  private static final HeaderCount QUERY_COUNT = HeaderCount.of(1, 0, 0, 0);
  private static final Header HEADER = new Header(ofZero(), QUERY_FLAGS, QUERY_COUNT);
  public static final Domain DOMAIN_EXAMPLE = Domain.of("www.example.com");
  private static final Question QUESTION =
      new Question(DOMAIN_EXAMPLE, DnsQueryType.A, DnsQueryClass.IN);
  private static final byte[] HEADER_BYTES = new byte[]{0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0};
  private static final byte[] QUESTION_BYTES =
      new byte[]{3, 119, 119, 119, 7, 101, 120, 97, 109, 112, 108, 101, 3, 99, 111, 109, 0, 0, 1,
          0, 1};
  private static final byte[] UDP_HEADER_QUESTION_BYTES = new byte[]{
      0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 3, 119, 119, 119, 7, 101, 120, 97, 109, 112, 108, 101, 3,
      99, 111, 109, 0, 0, 1, 0, 1};
  private static final byte[] TCP_HEADER_QUESTION_BYTES = new byte[]{
      0, 33, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 3, 119, 119, 119, 7, 101, 120, 97, 109, 112, 108,
      101, 3, 99, 111, 109, 0, 0, 1, 0, 1};
  private static final byte[] EMPTY_BYTES = new byte[0];

  @Test
  void testExampleUdpQuery() {
    var dnsOutput = DnsOutput.toWire(
        ParserOptions.Builder.builder().unsetTcp().build(),
        HEADER,
        QUESTION,
        List.of(),
        List.of(),
        List.of());

    assertThat(dnsOutput.getHeader()).isEqualTo(HEADER_BYTES);
    assertThat(dnsOutput.getQuestion()).isEqualTo(QUESTION_BYTES);
    assertThat(dnsOutput.getAnswers()).isEqualTo(EMPTY_BYTES);
    assertThat(dnsOutput.getAuthoritatives()).isEqualTo(EMPTY_BYTES);
    assertThat(dnsOutput.getAdditional()).isEqualTo(EMPTY_BYTES);
    assertThat(dnsOutput.getMessage()).isEqualTo(UDP_HEADER_QUESTION_BYTES);
    assertThat(dnsOutput.getMessage()).isEqualTo(UDP_HEADER_QUESTION_BYTES);
  }

  @Test
  void testExampleTcpQuery() {
    var dnsOutput = DnsOutput.toWire(
        ParserOptions.Builder.builder().setTcp().build(),
        HEADER,
        QUESTION,
        List.of(),
        List.of(),
        List.of());

    assertThat(dnsOutput.getHeader()).isEqualTo(HEADER_BYTES);
    assertThat(dnsOutput.getQuestion()).isEqualTo(QUESTION_BYTES);
    assertThat(dnsOutput.getAnswers()).isEqualTo(EMPTY_BYTES);
    assertThat(dnsOutput.getAuthoritatives()).isEqualTo(EMPTY_BYTES);
    assertThat(dnsOutput.getAdditional()).isEqualTo(EMPTY_BYTES);
    assertThat(dnsOutput.getMessage()).isEqualTo(TCP_HEADER_QUESTION_BYTES);
    assertThat(dnsOutput.getMessage()).isEqualTo(TCP_HEADER_QUESTION_BYTES);
  }
}
