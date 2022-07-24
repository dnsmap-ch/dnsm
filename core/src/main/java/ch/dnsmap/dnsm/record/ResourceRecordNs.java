package ch.dnsmap.dnsm.record;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.record.type.Ns;

public final class ResourceRecordNs extends ResourceRecord {

  private final int rdLength;
  private final Ns ns;

  public ResourceRecordNs(Domain name, DnsType dnsType, DnsClass dnsClass, long ttl,
                          int rdLength, Ns ns) {
    super(name, dnsType, dnsClass, ttl);
    this.rdLength = rdLength;
    this.ns = ns;
  }

  public Ns getNs() {
    return ns;
  }
}
