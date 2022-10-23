package ch.dnsmap.dnsm.record;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Ttl;

/**
 * Common interface for all resource records.
 */
public interface ResourceRecord {

  /**
   * Domain name of this resource record.
   *
   * @return domain name of record
   */
  Domain name();

  /**
   * DNS type of this resource record.
   *
   * @return DNS type of record
   */
  DnsType getDnsType();

  /**
   * DNS class of this resource record.
   *
   * @return DNS class of record
   */
  DnsClass dnsClass();

  /**
   * DNS time to live of this resource record.
   *
   * @return TTL of record
   */
  Ttl ttl();
}
