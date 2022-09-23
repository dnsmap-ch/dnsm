package ch.dnsmap.dnsm.wire.parser;

import ch.dnsmap.dnsm.record.type.OpaqueData;
import ch.dnsmap.dnsm.wire.bytes.ReadableByteBuffer;
import ch.dnsmap.dnsm.wire.bytes.WriteableByteBuffer;

public final class ResourceRecordOpaqueParser
    implements WireWritable<OpaqueData>, WireTypeReadable<OpaqueData> {

  @Override
  public OpaqueData fromWire(ReadableByteBuffer wireData, int length) {
    byte[] opaqueBytes = wireData.readData(length);
    return new OpaqueData(opaqueBytes);
  }

  @Override
  public int toWire(WriteableByteBuffer wireData, OpaqueData data) {
    int rdLength = data.opaque().length;
    wireData.writeUInt16(rdLength);
    return wireData.writeData(data.opaque());
  }

  @Override
  public int bytesToWrite(OpaqueData data) {
    return data.opaque().length;
  }
}
