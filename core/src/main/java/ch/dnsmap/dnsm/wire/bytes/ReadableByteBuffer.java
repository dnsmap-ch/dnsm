package ch.dnsmap.dnsm.wire.bytes;

/**
 * Buffered network byte input parser.
 */
public interface ReadableByteBuffer extends ReadableWriteableByteBuffer {

  /**
   * Peek an 8-bit unsigned integer from the buffer.
   *
   * @return 8-bit unsigned integer from the buffer
   */
  int peakUInt8();

  /**
   * Peek an 16-bit unsigned integer from the buffer.
   *
   * @return 16-bit unsigned integer from the buffer
   */
  int peakUInt16();

  /**
   * Peek an 32-bit unsigned integer from the buffer.
   *
   * @return 32-bit unsigned integer from the buffer
   */
  long peakUInt32();

  /**
   * Read an 8-bit unsigned integer from the buffer, advance.
   *
   * @return 8-bit unsigned integer from the buffer
   */
  int readUInt8();

  /**
   * Read an 16-bit unsigned integer from the buffer, advance.
   *
   * @return 16-bit unsigned integer from the buffer
   */
  int readUInt16();

  /**
   * Read an 32-bit unsigned integer from the buffer, advance.
   *
   * @return 32-bit unsigned integer from the buffer
   */
  long readUInt32();

  /**
   * Read bytes of {@code length}, advance.
   *
   * @param length number of bytes to read
   * @return copy of opaque data of size {@code length}
   */
  byte[] readData(int length);

  /**
   * Read bytes with an 8-bit length header, advance.
   *
   * @return copy of opaque data with size of the 8-bit length header value
   */
  byte[] readData8();

  /**
   * Read bytes with an 16-bit length header, advance.
   *
   * @return copy of opaque data with size of the 16-bit length header value
   */
  byte[] readData16();
}
