package ch.dnsmap.dnsm.record;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Ttl;

public abstract class ResourceRecord {

  private Domain name;
  private DnsType dnsType;
  private DnsClass dnsClass;
  private Ttl ttl;

  protected ResourceRecord(Domain name, DnsType dnsType, DnsClass dnsClass, Ttl ttl) {
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

  public Ttl getTtl() {
    return ttl;
  }
}
