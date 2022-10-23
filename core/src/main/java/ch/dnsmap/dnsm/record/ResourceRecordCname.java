package ch.dnsmap.dnsm.record;

import static ch.dnsmap.dnsm.DnsType.CNAME;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Ttl;
import ch.dnsmap.dnsm.record.type.Cname;

public record ResourceRecordCname(Domain name, DnsClass dnsClass, Ttl ttl, Cname cname)
    implements ResourceRecord {
  @Override
  public DnsType getDnsType() {
    return CNAME;
  }
}
