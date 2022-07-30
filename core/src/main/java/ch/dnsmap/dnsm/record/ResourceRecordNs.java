package ch.dnsmap.dnsm.record;

import static ch.dnsmap.dnsm.DnsType.NS;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.record.type.Ns;

public final class ResourceRecordNs extends ResourceRecord {

  private final int rdLength;
  private final Ns ns;

  public ResourceRecordNs(Domain name, DnsClass dnsClass, long ttl,
                          int rdLength, Ns ns) {
    super(name, NS, dnsClass, ttl);
    this.rdLength = rdLength;
    this.ns = ns;
  }

  public Ns getNs() {
    return ns;
  }
}
