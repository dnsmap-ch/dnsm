package ch.dnsmap.dnsm.wire.parser;

import ch.dnsmap.dnsm.wire.bytes.WriteableByteBuffer;

public interface WireWritable<T> {

  /**
   * Parse a DNS object into wire data.
   *
   * @param wireData byte wire data
   * @param data     DNS object of type T
   * @return amount of bytes written
   */
  int toWire(WriteableByteBuffer wireData, T data);
}
