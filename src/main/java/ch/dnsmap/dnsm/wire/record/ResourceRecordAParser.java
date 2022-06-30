package ch.dnsmap.dnsm.wire.record;

import ch.dnsmap.dnsm.wire.ByteParser;
import ch.dnsmap.dnsm.wire.ReadableByte;
import ch.dnsmap.dnsm.record.type.Ip4;

public final class ResourceRecordAParser implements ByteParser<Ip4> {

    private static final int IPV4_BYTE_LENGTH = 4;

    public Ip4 fromWire(ReadableByte wireData) {
        int rdLength = wireData.readUInt16();
        byte[] ip4Bytes = wireData.readByte(rdLength);
        return Ip4.of(ip4Bytes);
    }
}
