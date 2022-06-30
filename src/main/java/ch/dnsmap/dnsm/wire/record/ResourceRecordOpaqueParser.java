package ch.dnsmap.dnsm.wire.record;

import ch.dnsmap.dnsm.wire.ByteParser;
import ch.dnsmap.dnsm.wire.ReadableByte;
import ch.dnsmap.dnsm.record.type.OpaqueData;

public final class ResourceRecordOpaqueParser implements ByteParser<OpaqueData> {

    @Override
    public OpaqueData fromWire(ReadableByte wireData) {
        int rdLength = wireData.readUInt16();
        byte[] opaqueBytes = wireData.readByte(rdLength);
        return new OpaqueData(opaqueBytes);
    }
}
