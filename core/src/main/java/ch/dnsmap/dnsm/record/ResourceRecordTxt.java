package ch.dnsmap.dnsm.record;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.record.type.Txt;

public final class ResourceRecordTxt extends ResourceRecord {

  private final Txt txt;

  public ResourceRecordTxt(Domain name, DnsClass dnsClass, long ttl, Txt txt) {
    super(name, DnsType.TXT, dnsClass, ttl);
    this.txt = txt;
  }

  public Txt getTxt() {
    return txt;
  }

  public int getLength() {
    return txt.getLength();
  }
}
