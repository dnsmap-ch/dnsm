package ch.dnsmap.dnsm.record;

import static ch.dnsmap.dnsm.DnsType.CNAME;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Ttl;
import ch.dnsmap.dnsm.record.type.Cname;

public final class ResourceRecordCname extends ResourceRecord {

  private final Cname cname;

  public ResourceRecordCname(Domain name, DnsClass dnsClass, Ttl ttl, Cname cname) {
    super(name, CNAME, dnsClass, ttl);
    this.cname = cname;
  }

  public Cname getCname() {
    return cname;
  }
}
