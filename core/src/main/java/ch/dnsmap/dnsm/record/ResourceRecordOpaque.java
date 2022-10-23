package ch.dnsmap.dnsm.record;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Ttl;
import ch.dnsmap.dnsm.record.type.OpaqueData;

public record ResourceRecordOpaque(Domain name, DnsType dnsType, DnsClass dnsClass, Ttl ttl,
                                   OpaqueData opaqueData)
    implements ResourceRecord {

  @Override
  public DnsType getDnsType() {
    return dnsType;
  }
}
