package ch.dnsmap.dnsm.record;

import static ch.dnsmap.dnsm.DnsType.SOA;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Ttl;
import ch.dnsmap.dnsm.record.type.Soa;

public record ResourceRecordSoa(Domain name, DnsClass dnsClass, Ttl ttl, Soa soa)
    implements ResourceRecord {

  @Override
  public DnsType getDnsType() {
    return SOA;
  }
}
