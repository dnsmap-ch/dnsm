package ch.dnsmap.dnsm.wire.parser;

import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.record.type.Mx;
import ch.dnsmap.dnsm.wire.bytes.ReadableByteBuffer;
import ch.dnsmap.dnsm.wire.bytes.WriteableByteBuffer;

public final class ResourceRecordMxParser implements WireWritable<Mx>, WireTypeReadable<Mx> {

  private final DomainParser domainParser;

  public ResourceRecordMxParser(DomainParser domainParser) {
    this.domainParser = domainParser;
  }

  @Override
  public Mx fromWire(ReadableByteBuffer wireData, int length) {
    int preference = wireData.readUInt16();
    Domain domain = domainParser.fromWire(wireData, length - 2);
    return Mx.of(preference, domain);
  }

  @Override
  public int toWire(WriteableByteBuffer wireData, Mx data) {
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
