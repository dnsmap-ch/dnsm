package ch.dnsmap.dnsm.wire;

public interface ReadableByte {

  /**
   * Return the current byte reading position to be safed outside.
   *
   * @return the position of the current byte reading
   */
  int createRestorePosition();

  /**
   * Restore the position to the restorePosition point.
   *
   * @param restorePosition position to restore
   */
  void restorePosition(int restorePosition);

  /**
   * Jump to the specified position.
   *
   * @param newPosition where to jump to in the byte input
   */
  void jumpToPosition(int newPosition);

  /**
   * Read next byte as an unsigned integer value but stay at current reading position.
   *
   * @return integer value from current byte
   */
  int peakUInt8();

  /**
   * Read next byte as an unsigned integer value. Moves the reading position one byte further.
   *
   * @return integer value from current byte
   */
  int readUInt8();

  /**
   * Read next two bytes as an unsigned integer value. Moves the reading position two bytes further.
   *
   * @return integer value from two byte
   */
  int readUInt16();

  /**
   * Read next four bytes as a signed integer value. Moves the reading position four bytes further.
   *
   * @return integer value from two byte
   */
  int readInt32();

  /**
   * Read next two bytes as an opaque byte array. Moves the reading position two bytes further.
   *
   * @return byte array of opaque data
   */
  byte[] readByte16();

  /**
   * Read next x byte as an opaque byte array, where x is a one byte length value of the current
   * position. If x is 0 an empty array of size 0 is returned. Moves the reading position x bytes
   * further.
   *
   * @return byte array of opaque data
   */
  byte[] readByteFromLength8();

  /**
   * Read next nofBytes as an opaque byte array. Moves the reading position nofBytes bytes further.
   *
   * @param nofBytes number of bytes to read
   * @return byte array of opaque data and specified size
   */
  byte[] readByte(int nofBytes);
}
