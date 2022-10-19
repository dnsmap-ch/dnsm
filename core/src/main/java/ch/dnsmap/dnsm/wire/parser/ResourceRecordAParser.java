package ch.dnsmap.dnsm.wire.parser;

import ch.dnsmap.dnsm.record.type.Ip4;
import ch.dnsmap.dnsm.wire.bytes.ReadableByteBuffer;
import ch.dnsmap.dnsm.wire.bytes.WriteableByteBuffer;

public final class ResourceRecordAParser implements WireWritable<Ip4>, WireTypeReadable<Ip4> {

  @Override
  public Ip4 fromWire(ReadableByteBuffer wireData, int length) {
    byte[] ip4Bytes = wireData.readData(length);
    return Ip4.of(ip4Bytes);
  }

  @Override
  public int toWire(WriteableByteBuffer wireData, Ip4 data) {
    return wireData.writeData(data.getIp().getAddress());
  }
}
