package ch.dnsmap.dnsm.record;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.record.type.Ip4;

public final class ResourceRecordA extends ResourceRecord {

  private final Ip4 ip4;

  public ResourceRecordA(Domain name, DnsType dnsType, DnsClass dnsClass, long ttl, Ip4 ip4) {
    super(name, dnsType, dnsClass, ttl);
    this.ip4 = ip4;
  }

  public Ip4 getIp4() {
    return ip4;
  }
}
