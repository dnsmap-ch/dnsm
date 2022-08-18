package ch.dnsmap.dnsm.record;

import static ch.dnsmap.dnsm.DnsType.MX;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Ttl;
import ch.dnsmap.dnsm.record.type.Mx;

/**
 * MX resource record type.
 */
public final class ResourceRecordMx extends ResourceRecord {

  private final Mx mx;

  public ResourceRecordMx(Domain name, DnsClass dnsClass, Ttl ttl, Mx mx) {
    super(name, MX, dnsClass, ttl);
    this.mx = mx;
  }

  /**
   * Get MX specific data.
   *
   * @return MX data of this record
   */
  public Mx getMx() {
    return mx;
  }
}
