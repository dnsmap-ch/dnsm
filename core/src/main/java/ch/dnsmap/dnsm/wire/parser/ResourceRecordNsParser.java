package ch.dnsmap.dnsm.wire.parser;

import static ch.dnsmap.dnsm.wire.bytes.ReadableWriteableByteBuffer.UINT_16;

import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.record.type.Ns;
import ch.dnsmap.dnsm.wire.bytes.NetworkByteBuffer;
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
    int offset = wireData.getPosition() + UINT_16;
    WriteableByteBuffer domainBuffer = NetworkByteBuffer.of(256, offset);
    int nsSize = domainParser.toWire(domainBuffer, data.ns());
    return wireData.writeBuffer16(domainBuffer, nsSize);
  }
}
