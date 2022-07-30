package ch.dnsmap.dnsm.wire;

public interface WriteableByte {

  int getPosition();

  byte[] range(int from, int to);

  int writeUInt8(int value);

  int writeUInt16(int value);

  int writeInt32(int value);

  int writeByte16(byte[] value);

  int writeByte(byte[] bytes);
}
