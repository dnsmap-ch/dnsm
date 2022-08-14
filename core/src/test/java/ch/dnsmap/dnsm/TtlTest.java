package ch.dnsmap.dnsm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class TtlTest {

  @Test
  void testValidTtl() {
    var ttl = Ttl.of(0);
    assertThat(ttl.getTtl()).isEqualTo(0);
  }

  @Test
  void testInvalidTooLowTtl() {
    assertThatThrownBy(() -> Ttl.of(-1))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("invalid TTL value: -1");
  }

  @Test
  void testInvalidTooHighTtl() {
    assertThatThrownBy(() -> Ttl.of((long) Math.pow(2, 32)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("invalid TTL value: 4294967296");
  }
}
