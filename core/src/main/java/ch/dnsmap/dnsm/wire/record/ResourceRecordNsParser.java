package ch.dnsmap.dnsm.wire.record;

import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.record.type.Ns;
import ch.dnsmap.dnsm.wire.ByteParser;
import ch.dnsmap.dnsm.wire.DomainParser;
import ch.dnsmap.dnsm.wire.ReadableByte;
import ch.dnsmap.dnsm.wire.WriteableByte;

public final class ResourceRecordNsParser implements ByteParser<Ns> {

  private final DomainParser domainParser;

  public ResourceRecordNsParser(DomainParser domainParser) {
    this.domainParser = domainParser;
  }

  @Override
  public Ns fromWire(ReadableByte wireData) {
    Domain domain = domainParser.fromWire(wireData);
    return new Ns(domain);
  }

  public Ns fromWire(ReadableByte wireData, int length) {
    Domain domain = domainParser.fromWire(wireData, length);
    return new Ns(domain);
  }

  @Override
  public int toWire(WriteableByte wireData, Ns data) {
    int bytesWritten = 0;
    bytesWritten += wireData.writeUInt16(domainParser.bytesToWrite(data.ns()));
    bytesWritten += domainParser.toWire(wireData, data.ns());
    return bytesWritten;
  }

  public int bytesToWrite(Ns data) {
    int bytesToWrite = 0;
    bytesToWrite += data.ns().getLabelCount();
    bytesToWrite += domainParser.bytesToWrite(data.ns());
    return bytesToWrite;
  }
}
