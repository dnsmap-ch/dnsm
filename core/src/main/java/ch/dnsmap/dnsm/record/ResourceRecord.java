package ch.dnsmap.dnsm.record;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsType;
import ch.dnsmap.dnsm.Domain;

public abstract class ResourceRecord {

  private Domain name;
  private DnsType dnsType;
  private DnsClass dnsClass;
  long ttl;

  protected ResourceRecord(Domain name, DnsType dnsType, DnsClass dnsClass, long ttl) {
    this.name = name;
    this.dnsType = dnsType;
    this.dnsClass = dnsClass;
    this.ttl = ttl;
  }

  public Domain getName() {
    return name;
  }

  public DnsType getDnsType() {
    return dnsType;
  }

  public DnsClass getDnsClass() {
    return dnsClass;
  }

  public long getTtl() {
    return ttl;
  }
}
