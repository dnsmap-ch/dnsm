package ch.dnsmap.dnsm.header;

import static ch.dnsmap.dnsm.header.HeaderBitFlags.QR;
import static ch.dnsmap.dnsm.header.HeaderOpcode.QUERY;
import static ch.dnsmap.dnsm.header.HeaderRcode.REFUSED;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HeaderFlagsTest {

  @Test
  void testIsQueryIsTrueWithoutAnyFlags() {
    var headerFlags = new HeaderFlags(QUERY, REFUSED);
    var isQuery = headerFlags.isQuery();
    assertThat(isQuery).isTrue();
  }

  @Test
  void testIsResponseIsTrueIfQRIsSet() {
    var headerFlags = new HeaderFlags(QUERY, REFUSED, QR);
    var isQuery = headerFlags.isQuery();
    assertThat(isQuery).isFalse();
  }
}
