package ch.dnsmap.dnsm.record;

import static ch.dnsmap.dnsm.DnsType.MX;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Ttl;
import ch.dnsmap.dnsm.record.type.Mx;

/**
 * MX resource record type.
 */
public record ResourceRecordMx(Domain name, DnsClass dnsClass, Ttl ttl, Mx mx)
    implements ResourceRecord {

  @Override
  public DnsType getDnsType() {
    return MX;
  }
}
