package ch.dnsmap.dnsm.record;

import static ch.dnsmap.dnsm.DnsType.AAAA;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Ttl;
import ch.dnsmap.dnsm.record.type.Ip6;

public record ResourceRecordAaaa(Domain name, DnsClass dnsClass, Ttl ttl, Ip6 ip6)
    implements ResourceRecord {

  @Override
  public DnsType getDnsType() {
    return AAAA;
  }
}
