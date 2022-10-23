package ch.dnsmap.dnsm.record;

import static ch.dnsmap.dnsm.DnsType.NS;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Ttl;
import ch.dnsmap.dnsm.record.type.Ns;

public record ResourceRecordNs(Domain name, DnsClass dnsClass, Ttl ttl, Ns ns)
    implements ResourceRecord {

  @Override
  public DnsType getDnsType() {
    return NS;
  }
}
