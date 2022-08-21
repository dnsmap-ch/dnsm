package ch.dnsmap.dnsm.wire.record;

import ch.dnsmap.dnsm.record.type.Ip4;
import ch.dnsmap.dnsm.wire.ByteParser;
import ch.dnsmap.dnsm.wire.ReadableByte;
import ch.dnsmap.dnsm.wire.WriteableByte;

public final class ResourceRecordAParser implements ByteParser<Ip4> {

  private static final int IPV4_BYTE_LENGTH = 4;

  @Override
  public Ip4 fromWire(ReadableByte wireData) {
    int rdLength = wireData.readUInt16();
    byte[] ip4Bytes = wireData.readByte(rdLength);
    return Ip4.of(ip4Bytes);
  }

  @Override
  public Ip4 fromWire(ReadableByte wireData, int length) {
    return null;
  }

  @Override
  public int toWire(WriteableByte wireData, Ip4 data) {
    int bytesWritten = wireData.writeUInt16(IPV4_BYTE_LENGTH);
    bytesWritten += wireData.writeByte(data.getIp().getAddress());
    return bytesWritten;
  }


  @Override
  public int bytesToWrite(Ip4 data) {
    return data.getIp().getAddress().length;
  }
}
