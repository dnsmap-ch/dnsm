package ch.dnsmap.dnsm.record;

import static ch.dnsmap.dnsm.DnsType.NS;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Ttl;
import ch.dnsmap.dnsm.record.type.Ns;

public final class ResourceRecordNs extends ResourceRecord {

  private final Ns ns;

  public ResourceRecordNs(Domain name, DnsClass dnsClass, Ttl ttl, Ns ns) {
    super(name, NS, dnsClass, ttl);
    this.ns = ns;
  }

  public Ns getNs() {
    return ns;
  }
}
