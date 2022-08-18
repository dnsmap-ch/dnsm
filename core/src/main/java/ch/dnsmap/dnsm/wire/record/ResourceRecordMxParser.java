package ch.dnsmap.dnsm.wire.record;

import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.record.type.Mx;
import ch.dnsmap.dnsm.wire.ByteParser;
import ch.dnsmap.dnsm.wire.DomainParser;
import ch.dnsmap.dnsm.wire.ReadableByte;
import ch.dnsmap.dnsm.wire.WriteableByte;

public final class ResourceRecordMxParser implements ByteParser<Mx> {

  private final DomainParser domainParser;

  public ResourceRecordMxParser(DomainParser domainParser) {
    this.domainParser = domainParser;
  }

  @Override
  public Mx fromWire(ReadableByte wireData) {
    int preference = wireData.readUInt16();
    Domain domain = domainParser.fromWire(wireData);
    return Mx.of(preference, domain);
  }

  public Mx fromWire(ReadableByte wireData, int length) {
    int preference = wireData.readUInt16();
    Domain domain = domainParser.fromWire(wireData, length - 2);
    return Mx.of(preference, domain);
  }

  @Override
  public int toWire(WriteableByte wireData, Mx data) {
    int bytesWritten = wireData.writeUInt16(domainParser.bytesToWrite(data.getExchange()) + 2);
    bytesWritten += wireData.writeUInt16(data.getPreference().value());
    bytesWritten += domainParser.toWire(wireData, data.getExchange());
    return bytesWritten;
  }

  @Override
  public int bytesToWrite(Mx data) {
    int bytesToWrite = 0;
    bytesToWrite += 2;
    bytesToWrite += domainParser.bytesToWrite(data.getExchange());
    return bytesToWrite;
  }
}
