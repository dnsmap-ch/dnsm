package ch.dnsmap.dnsm.record;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.record.type.Ip6;

public final class ResourceRecordAaaa extends ResourceRecord {

  private final Ip6 ip6;

  public ResourceRecordAaaa(Domain name, DnsType dnsType, DnsClass dnsClass, long ttl, Ip6 ip6) {
    super(name, dnsType, dnsClass, ttl);
    this.ip6 = ip6;
  }

  public Ip6 getIp6() {
    return ip6;
  }
}
