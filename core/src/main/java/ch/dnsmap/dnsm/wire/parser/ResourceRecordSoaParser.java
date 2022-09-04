package ch.dnsmap.dnsm.wire.parser;

import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Uint32;
import ch.dnsmap.dnsm.record.type.Soa;
import ch.dnsmap.dnsm.wire.bytes.ReadableByteBuffer;
import ch.dnsmap.dnsm.wire.bytes.WriteableByteBuffer;

public final class ResourceRecordSoaParser implements WireWritable<Soa>, WireTypeReadable<Soa> {

  private final DomainParser domainParser;

  public ResourceRecordSoaParser(DomainParser domainParser) {
    this.domainParser = domainParser;
  }

  @Override
  public Soa fromWire(ReadableByteBuffer wireData, int length) {
    Domain mName = domainParser.fromWire(wireData);
    Domain rName = domainParser.fromWire(wireData);
    Uint32 serial = Uint32.of(wireData.readUInt32());
    Uint32 refresh = Uint32.of(wireData.readUInt32());
    Uint32 retry = Uint32.of(wireData.readUInt32());
    Uint32 expire = Uint32.of(wireData.readUInt32());
    Uint32 minimum = Uint32.of(wireData.readUInt32());
    return new Soa(mName, rName, serial, refresh, retry, expire, minimum);
  }

  @Override
  public int toWire(WriteableByteBuffer wireData, Soa data) {
    int bytesWritten = domainParser.toWire(wireData, data.mname());
    bytesWritten += domainParser.toWire(wireData, data.rname());
    bytesWritten += wireData.writeUInt32(data.serial().value());
    bytesWritten += wireData.writeUInt32(data.refresh().value());
    bytesWritten += wireData.writeUInt32(data.retry().value());
    bytesWritten += wireData.writeUInt32(data.expire().value());
    bytesWritten += wireData.writeUInt32(data.minimum().value());
    return bytesWritten;
  }
}
