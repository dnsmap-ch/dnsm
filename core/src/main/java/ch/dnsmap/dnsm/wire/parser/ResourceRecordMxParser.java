package ch.dnsmap.dnsm.wire.parser;

import static ch.dnsmap.dnsm.wire.bytes.ReadableWriteableByteBuffer.UINT_16;

import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.record.type.Mx;
import ch.dnsmap.dnsm.wire.bytes.NetworkByteBuffer;
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
    int offset = wireData.getPosition() + UINT_16 + UINT_16;
    WriteableByteBuffer domainBuffer = NetworkByteBuffer.of(256, offset);
    int mxSize = domainParser.toWire(domainBuffer, data.getExchange());

    int bytesWritten = wireData.writeUInt16(mxSize + PREFERENCE_SIZE);
    bytesWritten += wireData.writeUInt16(data.getPreference().value());
    bytesWritten += wireData.writeBuffer(domainBuffer, mxSize);
    return bytesWritten;
  }
}
