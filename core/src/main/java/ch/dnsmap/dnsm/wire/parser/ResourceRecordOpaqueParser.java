package ch.dnsmap.dnsm.wire.parser;

import ch.dnsmap.dnsm.record.type.OpaqueData;
import ch.dnsmap.dnsm.wire.bytes.ReadableByte;
import ch.dnsmap.dnsm.wire.bytes.WriteableByte;

public final class ResourceRecordOpaqueParser
    implements WireWritable<OpaqueData>, WireTypeReadable<OpaqueData> {

  @Override
  public OpaqueData fromWire(ReadableByte wireData, int length) {
    byte[] opaqueBytes = wireData.readByte(length);
    return new OpaqueData(opaqueBytes);
  }

  @Override
  public int toWire(WriteableByte wireData, OpaqueData data) {
    int rdLength = data.opaque().length;
    wireData.writeUInt16(rdLength);
    return wireData.writeByte(data.opaque());
  }

  @Override
  public int bytesToWrite(OpaqueData data) {
    return data.opaque().length;
  }
}
