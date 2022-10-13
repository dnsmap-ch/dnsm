package ch.dnsmap.dnsm.wire.bytes;

public interface ReadableWriteableByteBuffer {

  /**
   * Length of an unsigned int 8 in bytes.
   */
  int UINT_8 = 1;

  /**
   * Length of an unsigned int 16 in bytes.
   */
  int UINT_16 = 2;

  /**
   * Get number of remaining bytes in the buffer from current position.
   *
   * @return remaining bytes in the buffer
   */
  int getRemaining();

  /**
   * Absolut position within buffer considering offset.
   *
   * @return current position in the buffer
   */
  int getPosition();

  /**
   * Offset used for positioning data correctly.
   *
   * @return amount of bytes of the offset
   */
  int getOffset();

  /**
   * Data within {@code from} to {@code to} including value on position {@code to}.
   *
   * @param from start position in the buffer
   * @param to   end position in the buffer
   * @return copy of opaque data of size {@code to} - {@code from}
   */
  byte[] range(int from, int to);

  /**
   * Return the current position to be saved outside.
   *
   * @return the position of the current byte reading
   */
  int createRestorePosition();

  /**
   * Restore the position to the {@code restorePosition} point.
   *
   * @param restorePosition position to restore
   */
  void restorePosition(int restorePosition);

  /**
   * Jump to the specified position. The first possible position is 0.
   *
   * @param position where to jump to in the byte input
   */
  void jumpToPosition(int position);
}
