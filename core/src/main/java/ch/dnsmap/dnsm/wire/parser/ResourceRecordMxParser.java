package ch.dnsmap.dnsm.wire.parser;

import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.record.type.Mx;
import ch.dnsmap.dnsm.wire.bytes.ReadableByteBuffer;
import ch.dnsmap.dnsm.wire.bytes.WriteableByteBuffer;

public final class ResourceRecordMxParser implements WireWritable<Mx>, WireTypeReadable<Mx> {

  private static final int PREFERENCE_SIZE = 2;

  private final DomainParser domainParser;

  public ResourceRecordMxParser(DomainParser domainParser) {
    this.domainParser = domainParser;
  }

  @Override
  public Mx fromWire(ReadableByteBuffer wireData, int length) {
    int preference = wireData.readUInt16();
    Domain domain = domainParser.fromWire(wireData, length - PREFERENCE_SIZE);
    return Mx.of(preference, domain);
  }

  @Override
  public int toWire(WriteableByteBuffer wireData, Mx data) {
    int bytesWritten = wireData.writeUInt16(data.getPreference().value());
    bytesWritten += domainParser.toWire(wireData, data.getExchange());
    return bytesWritten;
  }
}
