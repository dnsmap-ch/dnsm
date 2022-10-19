package ch.dnsmap.dnsm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class Uint32Test {

  @Test
  void testValidTtl() {
    var uint = Uint32.of(0);
    assertThat(uint.value()).isEqualTo(0);
  }

  @Test
  void testInvalidTooLowTtl() {
    assertThatThrownBy(() -> Uint32.of(-1))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("invalid unsigned integer value: -1");
  }

  @Test
  void testInvalidTooHighTtl() {
    assertThatThrownBy(() -> Uint32.of((long) Math.pow(2, 32)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("invalid unsigned integer value: 4294967296");
  }
}
