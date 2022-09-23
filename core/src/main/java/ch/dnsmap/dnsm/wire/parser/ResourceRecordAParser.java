package ch.dnsmap.dnsm.wire.parser;

import ch.dnsmap.dnsm.record.type.Ip4;
import ch.dnsmap.dnsm.wire.bytes.ReadableByteBuffer;
import ch.dnsmap.dnsm.wire.bytes.WriteableByteBuffer;

public final class ResourceRecordAParser implements WireWritable<Ip4>, WireTypeReadable<Ip4> {

  private static final int IPV4_BYTE_LENGTH = 4;

  @Override
  public Ip4 fromWire(ReadableByteBuffer wireData, int length) {
    byte[] ip4Bytes = wireData.readData(length);
    return Ip4.of(ip4Bytes);
  }

  @Override
  public int toWire(WriteableByteBuffer wireData, Ip4 data) {
    int bytesWritten = wireData.writeUInt16(IPV4_BYTE_LENGTH);
    bytesWritten += wireData.writeData(data.getIp().getAddress());
    return bytesWritten;
  }

  @Override
  public int bytesToWrite(Ip4 data) {
    return data.getIp().getAddress().length;
  }
}
