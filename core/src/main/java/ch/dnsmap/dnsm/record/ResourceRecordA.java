package ch.dnsmap.dnsm.record;

import static ch.dnsmap.dnsm.DnsType.A;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Ttl;
import ch.dnsmap.dnsm.record.type.Ip4;

public record ResourceRecordA(Domain name, DnsClass dnsClass, Ttl ttl, Ip4 ip4)
    implements ResourceRecord {

  @Override
  public DnsType getDnsType() {
    return A;
  }
}
