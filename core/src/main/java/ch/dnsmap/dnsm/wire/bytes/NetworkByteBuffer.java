package ch.dnsmap.dnsm.wire.bytes;

import static java.nio.ByteBuffer.wrap;

import java.nio.ByteBuffer;

/**
 * Network byte buffer to read from and write into network bytes.
 */
public final class NetworkByteBuffer implements ReadableByteBuffer, WriteableByteBuffer {

  private final ByteBuffer byteBuffer;
  private final int offset;

  private NetworkByteBuffer(ByteBuffer byteBuffer, int offset) {
    this.byteBuffer = byteBuffer;
    this.offset = offset;
  }

  /**
   * Create buffer from network bytes in {@code data}.
   *
   * @param data network bytes to parse
   * @return buffer of passable network bytes
   */
  public static NetworkByteBuffer of(byte[] data) {
    return new NetworkByteBuffer(wrap(data), 0);
  }

  /**
   * Create empty buffer of size {@code capacity}.
   *
   * @param capacity size of the buffer to write into
   * @return empty buffer to write network bytes into
   */
  public static NetworkByteBuffer of(int capacity) {
    return new NetworkByteBuffer(ByteBuffer.allocate(capacity), 0);
  }

  public static NetworkByteBuffer of(int capacity, int offset) {
    return new NetworkByteBuffer(ByteBuffer.allocate(capacity), offset);
  }

  @Override
  public int getRemaining() {
    return byteBuffer.remaining();
  }

  @Override
  public int getPosition() {
    return byteBuffer.position() + offset;
  }

  @Override
  public int getOffset() {
    return offset;
  }

  @Override
  public byte[] range(int from, int to) {
    int rangeSize = to - from;
    if (rangeSize == 0) {
      return new byte[0];
    }
    byte[] data = new byte[rangeSize];
    byteBuffer.get(from, data);
    return data;
  }

  @Override
  public int createRestorePosition() {
    return byteBuffer.position();
  }

  @Override
  public void restorePosition(int restorePosition) {
    jumpToPosition(restorePosition);
  }

  @Override
  public void jumpToPosition(int position) {
    byteBuffer.position(position);
  }

  @Override
  public int peakUInt8() {
    int pos = byteBuffer.position();
    int ret = readUInt8();
    byteBuffer.position(pos);
    return ret;
  }

  @Override
  public int peakUInt16() {
    int pos = byteBuffer.position();
    int ret = readUInt16();
    byteBuffer.position(pos);
    return ret;
  }

  @Override
  public long peakUInt32() {
    int pos = byteBuffer.position();
    long ret = readUInt32();
    byteBuffer.position(pos);
    return ret;
  }

  @Override
  public int readUInt8() {
    return ((short) (byteBuffer.get() & 0xFF));
  }

  @Override
  public int readUInt16() {
    return byteBuffer.getShort() & 0xFFFF;
  }

  @Override
  public long readUInt32() {
    return ((long) byteBuffer.getInt() & 0xFFFF_FFFFL);
  }

  @Override
  public byte[] readData(int length) {
    if (length > byteBuffer.capacity()) {
      throw new IllegalArgumentException("length longer than buffer capacity");
    }
    byte[] data = new byte[length];
    byteBuffer.get(data);
    return data;
  }

  @Override
  public byte[] readData8() {
    int nofBytes = readUInt8();
    if (nofBytes == 0) {
      return new byte[0];
    }
    return readData(nofBytes);
  }

  @Override
  public byte[] readData16() {
    int nofBytes = readUInt16();
    if (nofBytes == 0) {
      return new byte[0];
    }
    return readData(nofBytes);
  }

  @Override
  public int writeUInt8(int value) {
    byteBuffer.put((byte) (value & 0xFF));
    return 1;
  }

  @Override
  public int writeUInt16(int value) {
    byteBuffer.putShort((short) (value & 0xFFFF));
    return 2;
  }

  @Override
  public int writeUInt32(long value) {
    byteBuffer.putInt((int) (value & 0xFFFF_FFFFL));
    return 4;
  }

  @Override
  public int writeData(byte[] data) {
    byteBuffer.put(data);
    return data.length;
  }

  @Override
  public int writeData8(byte[] data) {
    int length = data.length;
    writeUInt8(length);
    byteBuffer.put(data);
    return 1 + length;
  }

  @Override
  public int writeData16(byte[] data) {
    int length = data.length;
    writeUInt16(length);
    byteBuffer.put(data);
    return 2 + length;
  }

  @Override
  public int writeBuffer(WriteableByteBuffer buffer, int size) {
    return appendBuffer((ReadableByteBuffer) buffer, size);
  }

  @Override
  public int writeBuffer16(WriteableByteBuffer buffer, int size) {
    int bytesWritten = writeUInt16(size);
    bytesWritten += appendBuffer((ReadableByteBuffer) buffer, size);
    return bytesWritten;
  }

  private int appendBuffer(ReadableByteBuffer buffer, int size) {
    buffer.jumpToPosition(0);
    byte[] bufferData = buffer.readData(size);
    byteBuffer.put(bufferData);
    return bufferData.length;
  }
}
