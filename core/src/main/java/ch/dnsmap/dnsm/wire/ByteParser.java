package ch.dnsmap.dnsm.wire;

public interface ByteParser<T> {

  /**
   * Parse wire format data of dynamic length into DNS objects.
   *
   * @param wireData bytes in wire format
   * @return a DNS object of type T
   */
  T fromWire(ReadableByte wireData);

  /**
   * Parse wire format data of fix length into DNS objects.
   *
   * @param wireData bytes in wire format
   * @param length   number of bytes to read from {@code wireData} buffer
   * @return a DNS object of type T
   */
  T fromWire(ReadableByte wireData, int length);

  /**
   * Parse a DNS object into wire data.
   *
   * @param wireData byte wire data
   * @param data     DNS object of type T
   * @return amount of bytes written
   */
  int toWire(WriteableByte wireData, T data);

  /**
   * Get the number of bytes of the {@code data} to write into wire format.
   *
   * @param data to be written into wire format
   * @return number of bytes the write operation will need
   */
  int bytesToWrite(T data);
}
