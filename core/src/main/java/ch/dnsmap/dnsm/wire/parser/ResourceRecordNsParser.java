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
    int bytesWritten = 0;
    bytesWritten += wireData.writeUInt16(domainParser.bytesToWrite(data.ns()));
    bytesWritten += domainParser.toWire(wireData, data.ns());
    return bytesWritten;
  }

  @Override
  public int bytesToWrite(Ns data) {
    int bytesToWrite = 0;
    bytesToWrite += data.ns().getLabelCount();
    bytesToWrite += domainParser.bytesToWrite(data.ns());
    return bytesToWrite;
  }
}
