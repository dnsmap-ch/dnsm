package ch.dnsmap.dnsm.wire.bytes;

/**
 * Buffered network byte output parser.
 */
public interface WriteableByteBuffer extends ReadableWriteableByteBuffer {

  /**
   * Append an 8-bit unsigned integer to the buffer.
   *
   * @param value value to append
   * @return bytes written to the buffer, always 1
   */
  int writeUInt8(int value);

  /**
   * Append an 16-bit unsigned integer to the buffer.
   *
   * @param value value to append
   * @return bytes written to the buffer, always 2
   */
  int writeUInt16(int value);

  /**
   * Append an 32-bit unsigned integer to the buffer.
   *
   * @param value value to append
   * @return bytes written to the buffer, always 4
   */
  int writeUInt32(long value);

  /**
   * Append bytes of data without a length header.
   *
   * @param data data to append
   * @return bytes written to the buffer
   */
  int writeData(byte[] data);

  /**
   * Append bytes of data with an 8-bit length header.
   *
   * @param data data to append
   * @return bytes written to the buffer
   */
  int writeData8(byte[] data);

  /**
   * Append bytes of data with an 16-bit length header.
   *
   * @param data data to append
   * @return bytes written to the buffer
   */
  int writeData16(byte[] data);

  /**
   * Append buffered bytes of {@code buffer} without a length header.
   *
   * @param buffer buffer to append
   * @param size   size of buffered data to append
   * @return bytes written to the buffer
   */
  int writeBuffer(WriteableByteBuffer buffer, int size);

  /**
   * Append buffered bytes of {@code buffer} with an 16-bit length header.
   *
   * @param buffer buffer to append
   * @param size   size of buffered data to append
   * @return bytes written to the buffer
   */
  int writeBuffer16(WriteableByteBuffer buffer, int size);
}
