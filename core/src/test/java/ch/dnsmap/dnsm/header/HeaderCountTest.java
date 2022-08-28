package ch.dnsmap.dnsm.header;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.dnsmap.dnsm.header.HeaderCount;
import org.junit.jupiter.api.Test;

class HeaderCountTest {

  @Test
  void testOfCounts() {
    var count = HeaderCount.of(1, 2, 3, 4);
    assertThat(count.getQdCount()).isEqualTo(1);
    assertThat(count.getAnCount()).isEqualTo(2);
    assertThat(count.getNsCount()).isEqualTo(3);
    assertThat(count.getArCount()).isEqualTo(4);
  }

  @Test
  void testInvalidNegativeQuestion() {
    assertThatThrownBy(() -> HeaderCount.of(-1, 2, 3, 4))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("invalid header question count value: -1");
  }

  @Test
  void testInvalidNegativeAnswer() {
    assertThatThrownBy(() -> HeaderCount.of(1, -2, 3, 4))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("invalid header answer count value: -2");
  }

  @Test
  void testInvalidNegativeNameServer() {
    assertThatThrownBy(() -> HeaderCount.of(1, 2, -3, 4))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("invalid header name server count value: -3");
  }

  @Test
  void testInvalidNegativeAdditionalRecord() {
    assertThatThrownBy(() -> HeaderCount.of(1, 2, 3, -4))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("invalid header additional record count value: -4");
  }

  @Test
  void testInvalidTooLargeQuestion() {
    assertThatThrownBy(() -> HeaderCount.of((int) Math.pow(2, 16), 2, 3, 4))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("invalid header question count value: 65536");
  }

  @Test
  void testInvalidTooLargeAnswer() {
    assertThatThrownBy(() -> HeaderCount.of(1, (int) Math.pow(2, 16), 3, 4))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("invalid header answer count value: 65536");
  }

  @Test
  void testInvalidTooLargeNameServer() {
    assertThatThrownBy(() -> HeaderCount.of(1, 2, (int) Math.pow(2, 16), 4))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("invalid header name server count value: 65536");
  }

  @Test
  void testInvalidTooLargeAdditionalRecord() {
    assertThatThrownBy(() -> HeaderCount.of(1, 2, 3, (int) Math.pow(2, 16)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("invalid header additional record count value: 65536");
  }

  @Test
  void testEqualityOfIds() {
    var count1 = HeaderCount.of(1, 2, 3, 4);
    var count2 = HeaderCount.of(1, 2, 3, 4);
    assertThat(count1).isEqualTo(count2);
    assertThat(count1).hasSameHashCodeAs(count2);
  }

  @Test
  void testInequalityOfIds() {
    var count1 = HeaderCount.of(42, 42, 42, 42);
    var count2 = HeaderCount.of(23, 23, 23, 23);
    assertThat(count1).isNotEqualTo(count2);
    assertThat(count1).doesNotHaveSameHashCodeAs(count2);
  }
}
