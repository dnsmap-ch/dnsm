package ch.dnsmap.dnsm.record;

import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsType;
import ch.dnsmap.dnsm.record.type.OpaqueData;

public final class ResourceRecordOpaque extends ResourceRecord {

    private final OpaqueData opaqueData;

    public ResourceRecordOpaque(Domain name, DnsType dnsType, DnsClass dnsClass, long ttl, OpaqueData opaqueData) {
        super(name, dnsType, dnsClass, ttl);
        this.opaqueData = opaqueData;
    }

    public OpaqueData getOpaqueData() {
        return opaqueData;
    }
}
