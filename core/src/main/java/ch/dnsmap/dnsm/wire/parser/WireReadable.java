package ch.dnsmap.dnsm.wire.parser;

import ch.dnsmap.dnsm.wire.bytes.ReadableByte;

public interface WireReadable<T> {

  /**
   * Parse wire format data of dynamic length into DNS objects.
   *
   * @param wireData bytes in wire format
   * @return a DNS object of type T
   */
  T fromWire(ReadableByte wireData);
}
