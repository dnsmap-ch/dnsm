package ch.dnsmap.dnsm.wire.parser;

import ch.dnsmap.dnsm.record.type.Ip6;
import ch.dnsmap.dnsm.wire.bytes.ReadableByteBuffer;
import ch.dnsmap.dnsm.wire.bytes.WriteableByteBuffer;

public final class ResourceRecordAAAAParser implements WireWritable<Ip6>, WireTypeReadable<Ip6> {

  private static final int IPV6_BYTE_LENGTH = 16;

  @Override
  public Ip6 fromWire(ReadableByteBuffer wireData, int length) {
    byte[] ip6Bytes = wireData.readData(length);
    return Ip6.of(ip6Bytes);
  }

  @Override
  public int toWire(WriteableByteBuffer wireData, Ip6 data) {
    int bytesWritten = wireData.writeUInt16(IPV6_BYTE_LENGTH);
    bytesWritten += wireData.writeData(data.getIp().getAddress());
    return bytesWritten;
  }
}
