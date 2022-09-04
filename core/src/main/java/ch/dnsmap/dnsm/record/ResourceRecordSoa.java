package ch.dnsmap.dnsm.record;

import static ch.dnsmap.dnsm.DnsType.SOA;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Ttl;
import ch.dnsmap.dnsm.record.type.Soa;

public final class ResourceRecordSoa extends ResourceRecord {

  private final Soa soa;

  public ResourceRecordSoa(Domain name, DnsClass dnsClass, Ttl ttl, Soa soa) {
    super(name, SOA, dnsClass, ttl);
    this.soa = soa;
  }

  public Soa getSoa() {
    return soa;
  }
}
