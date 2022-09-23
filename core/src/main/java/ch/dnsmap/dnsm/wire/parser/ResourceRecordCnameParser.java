package ch.dnsmap.dnsm.wire.parser;

import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.record.type.Cname;
import ch.dnsmap.dnsm.wire.bytes.ReadableByteBuffer;
import ch.dnsmap.dnsm.wire.bytes.WriteableByteBuffer;

public final class ResourceRecordCnameParser
    implements WireWritable<Cname>, WireTypeReadable<Cname> {

  private final DomainParser domainParser;

  public ResourceRecordCnameParser(DomainParser domainParser) {
    this.domainParser = domainParser;
  }

  @Override
  public Cname fromWire(ReadableByteBuffer wireData, int length) {
    Domain domain = domainParser.fromWire(wireData, length);
    return new Cname(domain);
  }

  @Override
  public int toWire(WriteableByteBuffer wireData, Cname data) {
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
