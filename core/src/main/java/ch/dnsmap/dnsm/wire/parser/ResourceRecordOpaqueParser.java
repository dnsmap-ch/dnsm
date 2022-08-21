package ch.dnsmap.dnsm.wire.parser;

import ch.dnsmap.dnsm.record.type.OpaqueData;
import ch.dnsmap.dnsm.wire.bytes.ReadableByte;
import ch.dnsmap.dnsm.wire.bytes.WriteableByte;

public final class ResourceRecordOpaqueParser implements ByteParser<OpaqueData> {

  @Override
  public OpaqueData fromWire(ReadableByte wireData) {
    int rdLength = wireData.readUInt16();
    byte[] opaqueBytes = wireData.readByte(rdLength);
    return new OpaqueData(opaqueBytes);
  }

  @Override
  public OpaqueData fromWire(ReadableByte wireData, int length) {
    return null;
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