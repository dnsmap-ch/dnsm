package ch.dnsmap.dnsm.wire.parser;

import ch.dnsmap.dnsm.record.type.Ip6;
import ch.dnsmap.dnsm.wire.bytes.ReadableByte;
import ch.dnsmap.dnsm.wire.bytes.WriteableByte;

public final class ResourceRecordAAAAParser implements WireWritable<Ip6>, WireTypeReadable<Ip6> {

  private static final int IPV6_BYTE_LENGTH = 16;

  @Override
  public Ip6 fromWire(ReadableByte wireData, int length) {
    byte[] ip6Bytes = wireData.readByte(length);
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
