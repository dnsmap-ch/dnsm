package ch.dnsmap.dnsm.wire.parser;

import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.record.type.Ns;
import ch.dnsmap.dnsm.wire.bytes.ReadableByteBuffer;
import ch.dnsmap.dnsm.wire.bytes.WriteableByteBuffer;

public final class ResourceRecordNsParser implements WireWritable<Ns>, WireTypeReadable<Ns> {

  private final DomainParser domainParser;

  public ResourceRecordNsParser(DomainParser domainParser) {
    this.domainParser = domainParser;
  }

  @Override
  public Ns fromWire(ReadableByteBuffer wireData, int length) {
    Domain domain = domainParser.fromWire(wireData, length);
    return new Ns(domain);
  }

  @Override
  public int toWire(WriteableByteBuffer wireData, Ns data) {
    return domainParser.toWire(wireData, data.ns());
  }
}
