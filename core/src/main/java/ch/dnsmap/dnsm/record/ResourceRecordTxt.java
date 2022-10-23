package ch.dnsmap.dnsm.record;

import static ch.dnsmap.dnsm.DnsType.TXT;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Ttl;
import ch.dnsmap.dnsm.record.type.Txt;

public record ResourceRecordTxt(Domain name, DnsClass dnsClass, Ttl ttl, Txt txt)
    implements ResourceRecord {

  @Override
  public DnsType getDnsType() {
    return TXT;
  }
}
