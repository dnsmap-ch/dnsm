package ch.dnsmap.dnsm.wire.record;

import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.record.type.Cname;
import ch.dnsmap.dnsm.wire.ByteParser;
import ch.dnsmap.dnsm.wire.DomainParser;
import ch.dnsmap.dnsm.wire.ReadableByte;
import ch.dnsmap.dnsm.wire.WriteableByte;

public final class ResourceRecordCnameParser implements ByteParser<Cname> {

  private final DomainParser domainParser;

  public ResourceRecordCnameParser(DomainParser domainParser) {
    this.domainParser = domainParser;
  }

  @Override
  public Cname fromWire(ReadableByte wireData) {
    Domain domain = domainParser.fromWire(wireData);
    return new Cname(domain);
  }

  public Cname fromWire(ReadableByte wireData, int length) {
    Domain domain = domainParser.fromWire(wireData, length);
    return new Cname(domain);
  }

  @Override
  public int toWire(WriteableByte wireData, Cname data) {
    int bytesWritten = 0;
    bytesWritten += wireData.writeUInt16(data.cname().getLabelCount());
    bytesWritten += domainParser.toWire(wireData, data.cname());
    return bytesWritten;
  }

  @Override
  public int bytesToWrite(Cname data) {
    int bytesToWrite = 0;
    bytesToWrite += data.cname().getLabelCount();
    bytesToWrite += domainParser.bytesToWrite(data.cname());
    return bytesToWrite;
  }
}
