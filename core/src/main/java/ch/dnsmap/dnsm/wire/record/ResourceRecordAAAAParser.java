package ch.dnsmap.dnsm.wire.record;

import ch.dnsmap.dnsm.record.type.Ip6;
import ch.dnsmap.dnsm.wire.ByteParser;
import ch.dnsmap.dnsm.wire.ReadableByte;
import ch.dnsmap.dnsm.wire.WriteableByte;

public final class ResourceRecordAAAAParser implements ByteParser<Ip6> {

  private static final int IPV6_BYTE_LENGTH = 16;

  @Override
  public Ip6 fromWire(ReadableByte wireData) {
    int rdLength = wireData.readUInt16();
    byte[] ip6Bytes = wireData.readByte(rdLength);
    return Ip6.of(ip6Bytes);
  }

  @Override
  public int toWire(WriteableByte wireData, Ip6 data) {
    int bytesWritten = wireData.writeUInt16(IPV6_BYTE_LENGTH);
    bytesWritten += wireData.writeByte(data.getIp().getAddress());
    return bytesWritten;
  }


  @Override
  public int bytesToWrite(Ip6 data) {
    return data.getIp().getAddress().length;
  }
}
