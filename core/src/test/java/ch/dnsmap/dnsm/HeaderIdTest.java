package ch.dnsmap.dnsm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class HeaderIdTest {

  @Test
  void testOfZeroId() {
    var id = HeaderId.ofZero();
    assertThat(id.getId()).isEqualTo(0);
  }

  @Test
  void testOfId() {
    var id = HeaderId.of(42);
    assertThat(id.getId()).isEqualTo(42);
  }

  @Test
  void testOfRandom() {
    var id = HeaderId.ofRandom();
    assertThat(id.getId()).isBetween(0, (int) Math.pow(2, 16) - 1);
  }

  @Test
  void testInvalidNegativeId() {
    assertThatThrownBy(() -> HeaderId.of(-1))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("invalid header ID value: -1");
  }

  @Test
  void testInvalidTooLargeId() {
    assertThatThrownBy(() -> HeaderId.of((int) Math.pow(2, 16)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("invalid header ID value: 65536");
  }

  @Test
  void testEqualityOfIds() {
    var id1 = HeaderId.ofZero();
    var id2 = HeaderId.ofZero();
    assertThat(id1).isEqualTo(id2);
    assertThat(id1).hasSameHashCodeAs(id2);
  }

  @Test
  void testInequalityOfIds() {
    var id1 = HeaderId.ofZero();
    var id2 = HeaderId.of(23);
    assertThat(id1).isNotEqualTo(id2);
    assertThat(id1).doesNotHaveSameHashCodeAs(id2);
  }
}
