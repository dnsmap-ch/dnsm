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

  /**
   * Get the number of bytes of the {@code data} to write into wire format.
   *
   * @param data to be written into wire format
   * @return number of bytes the write operation will need
   */
  int bytesToWrite(T data);
}
