package ch.dnsmap.dnsm.record;

import static ch.dnsmap.dnsm.DnsType.CNAME;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.record.type.Cname;

public final class ResourceRecordCname extends ResourceRecord {

  private final int rdLength;
  private final Cname cname;

  public ResourceRecordCname(Domain name, DnsClass dnsClass, long ttl,
                             int rdLength, Cname cname) {
    super(name, CNAME, dnsClass, ttl);
    this.rdLength = rdLength;
    this.cname = cname;
  }

  public Cname getCname() {
    return cname;
  }
}
