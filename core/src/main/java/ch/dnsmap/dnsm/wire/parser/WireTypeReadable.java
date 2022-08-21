package ch.dnsmap.dnsm.wire.parser;

import ch.dnsmap.dnsm.wire.bytes.ReadableByte;

public interface WireTypeReadable<T> {

  /**
   * Parse wire format data of fix length into DNS objects.
   *
   * @param wireData bytes in wire format
   * @param length   number of bytes to read from {@code wireData} buffer
   * @return a DNS object of type T
   */
  T fromWire(ReadableByte wireData, int length);
}
