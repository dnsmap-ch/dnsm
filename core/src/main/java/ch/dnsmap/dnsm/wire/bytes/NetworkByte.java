package ch.dnsmap.dnsm.wire.bytes;

import static java.nio.ByteBuffer.wrap;

import java.nio.ByteBuffer;

public final class NetworkByte implements ReadableByte, WriteableByte {

  private final ByteBuffer byteBuffer;

  private NetworkByte(ByteBuffer byteBuffer) {
    this.byteBuffer = byteBuffer;
  }

  public static NetworkByte of(byte[] data) {
    return new NetworkByte(wrap(data));
  }

  public static NetworkByte of(int capacity) {
    return new NetworkByte(ByteBuffer.allocate(capacity));
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
  public void jumpToPosition(int newPosition) {
    byteBuffer.position(newPosition);
  }

  @Override
  public int peakUInt8() {
    int pos = byteBuffer.position();
    int ret = readUInt8();
    byteBuffer.position(pos);
    return ret;
  }

  @Override
  public int readUInt8() {
    return byteBuffer.get() & 0xFF;
  }

  @Override
  public int readUInt16() {
    return byteBuffer.getShort() & 0xFFFF;
  }

  @Override
  public int readInt32() {
    return byteBuffer.getInt();
  }

  @Override
  public byte[] readByte16() {
    return readByte(2);
  }

  @Override
  public byte[] readByteFromLength8() {
    int nofBytes = readUInt8();
    if (nofBytes == 0) {
      return new byte[0];
    }
    return readByte(nofBytes);
  }

  @Override
  public byte[] readByte(int nofBytes) {
    byte[] data = new byte[nofBytes];
    byteBuffer.get(data);
    return data;
  }

  @Override
  public int getPosition() {
    return byteBuffer.position();
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
  public int writeUInt8(int value) {
    byteBuffer.put((byte) value);
    return 1;
  }

  @Override
  public int writeUInt16(int value) {
    byteBuffer.putShort((short) value);
    return 2;
  }

  @Override
  public int writeInt32(int value) {
    byteBuffer.putInt(value);
    return 4;
  }

  @Override
  public int writeByte16(byte[] value) {
    byteBuffer.put(value);
    return 2;
  }

  @Override
  public int writeByte(byte[] bytes) {
    byteBuffer.put(bytes);
    return bytes.length;
  }
}