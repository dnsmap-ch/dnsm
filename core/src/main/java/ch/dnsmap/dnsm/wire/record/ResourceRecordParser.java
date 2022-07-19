package ch.dnsmap.dnsm.wire.record;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.record.ResourceRecord;
import ch.dnsmap.dnsm.record.ResourceRecordA;
import ch.dnsmap.dnsm.record.ResourceRecordCname;
import ch.dnsmap.dnsm.record.ResourceRecordOpaque;
import ch.dnsmap.dnsm.record.type.Cname;
import ch.dnsmap.dnsm.record.type.Ip4;
import ch.dnsmap.dnsm.record.type.OpaqueData;
import ch.dnsmap.dnsm.wire.ByteParser;
import ch.dnsmap.dnsm.wire.DomainCompression;
import ch.dnsmap.dnsm.wire.DomainParser;
import ch.dnsmap.dnsm.wire.ReadableByte;
import ch.dnsmap.dnsm.wire.WriteableByte;

public final class ResourceRecordParser implements ByteParser<ResourceRecord> {

  private static final int DNS_TYPE_FIELD_LENGTH = 2;
  private static final int DNS_CLASS_FIELD_LENGTH = 2;
  private static final int DNS_TTL_FIELD_LENGTH = 4;

  private final DomainParser domainParser;
  private final ResourceRecordAParser rrAParser;
  private final ResourceRecordCnameParser rrCnameParser;
  private final ResourceRecordOpaqueParser rrOpaqueParser;

  public ResourceRecordParser() {
    domainParser = new DomainParser();
    rrAParser = new ResourceRecordAParser();
    rrCnameParser = new ResourceRecordCnameParser(domainParser);
    rrOpaqueParser = new ResourceRecordOpaqueParser();
  }

  public void setDomainPositionMap(DomainCompression domainCompression) {
    domainParser.setDomainPositionMap(domainCompression);
  }

  @Override
  public ResourceRecord fromWire(ReadableByte wireData) {
    Domain name = domainParser.fromWire(wireData);
    DnsType dnsType = DnsType.of(wireData.readUInt16());
    DnsClass dnsClass = DnsClass.of(wireData.readUInt16());
    int ttl = wireData.readInt32();

    switch (dnsType) {
      case A -> {
        ResourceRecordAParser parser = new ResourceRecordAParser();
        Ip4 ip4 = parser.fromWire(wireData);
        return new ResourceRecordA(name, dnsType, dnsClass, ttl, ip4);
      }
      case CNAME -> {
        ResourceRecordCnameParser parser = new ResourceRecordCnameParser(new DomainParser());
        int rdLength = wireData.readUInt16();
        Cname cname = parser.fromWire(wireData);
        return new ResourceRecordCname(name, dnsType, dnsClass, ttl, rdLength, cname);
      }
      default -> {
        ResourceRecordOpaqueParser parser = new ResourceRecordOpaqueParser();
        OpaqueData opaqueData = parser.fromWire(wireData);
        return new ResourceRecordOpaque(name, dnsType, dnsClass, ttl, opaqueData);
      }
    }
  }

  @Override
  public int toWire(WriteableByte wireData, ResourceRecord data) {
    int bytesWritten = 0;

    bytesWritten += domainParser.toWire(wireData, data.getName());
    bytesWritten += wireData.writeUInt16(data.getDnsType().getValue());
    bytesWritten += wireData.writeUInt16(data.getDnsClass().getValue());
    bytesWritten += wireData.writeInt32((int) data.getTtl());

    switch (data.getDnsType()) {
      case A -> {
        ResourceRecordA aData = (ResourceRecordA) data;
        bytesWritten += rrAParser.toWire(wireData, aData.getIp4());
        return bytesWritten;
      }
      case CNAME -> {
        ResourceRecordCname cnameData = (ResourceRecordCname) data;
        bytesWritten += rrCnameParser.toWire(wireData, cnameData.getCname());
        return bytesWritten;
      }
      default -> {
        ResourceRecordOpaque opaqueData = (ResourceRecordOpaque) data;
        bytesWritten += rrOpaqueParser.toWire(wireData, opaqueData.getOpaqueData());
        return bytesWritten;
      }
    }
  }

  @Override
  public int bytesToWrite(ResourceRecord data) {
    int bytesToWrite = 0;

    bytesToWrite += domainParser.bytesToWrite(data.getName());
    bytesToWrite += DNS_TYPE_FIELD_LENGTH;
    bytesToWrite += DNS_CLASS_FIELD_LENGTH;
    bytesToWrite += DNS_TTL_FIELD_LENGTH;

    switch (data.getDnsType()) {
      case A -> {
        ResourceRecordA rrA = (ResourceRecordA) data;
        bytesToWrite += rrAParser.bytesToWrite(rrA.getIp4());
        return bytesToWrite;
      }
      case CNAME -> {
        ResourceRecordCname rrCname = (ResourceRecordCname) data;
        bytesToWrite += rrCnameParser.bytesToWrite(rrCname.getCname());
        return bytesToWrite;
      }
      default -> {
        ResourceRecordOpaque rrOpaque = (ResourceRecordOpaque) data;
        bytesToWrite += rrOpaqueParser.bytesToWrite(rrOpaque.getOpaqueData());
        return bytesToWrite;
      }
    }
  }
}
