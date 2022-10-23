package ch.dnsmap.dnsm.wire.parser;

import static ch.dnsmap.dnsm.wire.bytes.ReadableWriteableByteBuffer.UINT_16;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Ttl;
import ch.dnsmap.dnsm.record.ResourceRecord;
import ch.dnsmap.dnsm.record.ResourceRecordA;
import ch.dnsmap.dnsm.record.ResourceRecordAaaa;
import ch.dnsmap.dnsm.record.ResourceRecordCname;
import ch.dnsmap.dnsm.record.ResourceRecordMx;
import ch.dnsmap.dnsm.record.ResourceRecordNs;
import ch.dnsmap.dnsm.record.ResourceRecordOpaque;
import ch.dnsmap.dnsm.record.ResourceRecordSoa;
import ch.dnsmap.dnsm.record.ResourceRecordTxt;
import ch.dnsmap.dnsm.record.type.Cname;
import ch.dnsmap.dnsm.record.type.Ip4;
import ch.dnsmap.dnsm.record.type.Ip6;
import ch.dnsmap.dnsm.record.type.Mx;
import ch.dnsmap.dnsm.record.type.Ns;
import ch.dnsmap.dnsm.record.type.OpaqueData;
import ch.dnsmap.dnsm.record.type.Soa;
import ch.dnsmap.dnsm.record.type.Txt;
import ch.dnsmap.dnsm.wire.bytes.NetworkByteBuffer;
import ch.dnsmap.dnsm.wire.bytes.ReadableByteBuffer;
import ch.dnsmap.dnsm.wire.bytes.WriteableByteBuffer;

public final class ResourceRecordParser
    implements WireWritable<ResourceRecord>, WireReadable<ResourceRecord> {

  private final DomainParser domainParser;
  private final ResourceRecordAAAAParser rrAaaaParser;
  private final ResourceRecordAParser rrAParser;
  private final ResourceRecordCnameParser rrCnameParser;
  private final ResourceRecordMxParser rrMxParser;
  private final ResourceRecordNsParser rrNsParser;
  private final ResourceRecordOpaqueParser rrOpaqueParser;
  private final ResourceRecordSoaParser rrSoaParser;
  private final ResourceRecordTxtParser rrTxtParser;

  public ResourceRecordParser(DomainParser domainParser) {
    this.domainParser = domainParser;
    rrAParser = new ResourceRecordAParser();
    rrAaaaParser = new ResourceRecordAAAAParser();
    rrCnameParser = new ResourceRecordCnameParser(domainParser);
    rrMxParser = new ResourceRecordMxParser(domainParser);
    rrNsParser = new ResourceRecordNsParser(domainParser);
    rrOpaqueParser = new ResourceRecordOpaqueParser();
    rrSoaParser = new ResourceRecordSoaParser(domainParser);
    rrTxtParser = new ResourceRecordTxtParser();
  }

  @Override
  public ResourceRecord fromWire(ReadableByteBuffer wireData) {
    Domain name = domainParser.fromWire(wireData);
    DnsType dnsType = DnsType.of(wireData.readUInt16());
    DnsClass dnsClass = DnsClass.of(wireData.readUInt16());
    Ttl ttl = Ttl.of(wireData.readUInt32());

    switch (dnsType) {
      case A -> {
        int rdLength = wireData.readUInt16();
        Ip4 ip4 = rrAParser.fromWire(wireData, rdLength);
        return new ResourceRecordA(name, dnsClass, ttl, ip4);
      }
      case AAAA -> {
        int rdLength = wireData.readUInt16();
        Ip6 ip6 = rrAaaaParser.fromWire(wireData, rdLength);
        return new ResourceRecordAaaa(name, dnsClass, ttl, ip6);
      }
      case CNAME -> {
        int rdLength = wireData.readUInt16();
        Cname cname = rrCnameParser.fromWire(wireData, rdLength);
        return new ResourceRecordCname(name, dnsClass, ttl, cname);
      }
      case MX -> {
        int rdLength = wireData.readUInt16();
        Mx mx = rrMxParser.fromWire(wireData, rdLength);
        return new ResourceRecordMx(name, dnsClass, ttl, mx);
      }
      case NS -> {
        int rdLength = wireData.readUInt16();
        Ns ns = rrNsParser.fromWire(wireData, rdLength);
        return new ResourceRecordNs(name, dnsClass, ttl, ns);
      }
      case SOA -> {
        int rdLength = wireData.readUInt16();
        Soa soa = rrSoaParser.fromWire(wireData, rdLength);
        return new ResourceRecordSoa(name, dnsClass, ttl, soa);
      }
      case TXT -> {
        int rdLength = wireData.readUInt16();
        Txt txt = rrTxtParser.fromWire(wireData, rdLength);
        return new ResourceRecordTxt(name, dnsClass, ttl, txt);
      }
      default -> {
        int rdLength = wireData.readUInt16();
        OpaqueData opaqueData = rrOpaqueParser.fromWire(wireData, rdLength);
        return new ResourceRecordOpaque(name, dnsType, dnsClass, ttl, opaqueData);
      }
    }
  }

  @Override
  public int toWire(WriteableByteBuffer wireData, ResourceRecord data) {
    int bytesWritten = domainParser.toWire(wireData, data.name());
    bytesWritten += wireData.writeUInt16(data.getDnsType().getValue());
    bytesWritten += wireData.writeUInt16(data.dnsClass().getValue());
    bytesWritten += wireData.writeUInt32((int) data.ttl().getTtl());

    int offset = wireData.getPosition() + UINT_16;
    WriteableByteBuffer rrBuffer = NetworkByteBuffer.of(256, offset);

    switch (data.getDnsType()) {
      case A -> {
        ResourceRecordA aData = (ResourceRecordA) data;
        int aSize = rrAParser.toWire(rrBuffer, aData.ip4());
        bytesWritten += wireData.writeBuffer16(rrBuffer, aSize);
        return bytesWritten;
      }
      case AAAA -> {
        ResourceRecordAaaa aaaaData = (ResourceRecordAaaa) data;
        int aaaaSize = rrAaaaParser.toWire(rrBuffer, aaaaData.ip6());
        bytesWritten += wireData.writeBuffer16(rrBuffer, aaaaSize);
        return bytesWritten;
      }
      case CNAME -> {
        ResourceRecordCname cnameData = (ResourceRecordCname) data;
        int cnameSize = rrCnameParser.toWire(rrBuffer, cnameData.cname());
        bytesWritten += wireData.writeBuffer16(rrBuffer, cnameSize);
        return bytesWritten;
      }
      case MX -> {
        ResourceRecordMx mxData = (ResourceRecordMx) data;
        int mxSize = rrMxParser.toWire(rrBuffer, mxData.mx());
        bytesWritten += wireData.writeBuffer16(rrBuffer, mxSize);
        return bytesWritten;
      }
      case NS -> {
        ResourceRecordNs nsData = (ResourceRecordNs) data;
        int nsSize = rrNsParser.toWire(rrBuffer, nsData.ns());
        bytesWritten += wireData.writeBuffer16(rrBuffer, nsSize);
        return bytesWritten;
      }
      case SOA -> {
        ResourceRecordSoa soaData = (ResourceRecordSoa) data;
        int soaSize = rrSoaParser.toWire(rrBuffer, soaData.soa());
        bytesWritten += wireData.writeBuffer16(rrBuffer, soaSize);
        return bytesWritten;
      }
      case TXT -> {
        ResourceRecordTxt txtData = (ResourceRecordTxt) data;
        int txtSize = rrTxtParser.toWire(rrBuffer, txtData.txt());
        bytesWritten += wireData.writeBuffer16(rrBuffer, txtSize);
        return bytesWritten;
      }
      default -> {
        ResourceRecordOpaque opaqueData = (ResourceRecordOpaque) data;
        int opaqueSize = rrOpaqueParser.toWire(rrBuffer, opaqueData.opaqueData());
        bytesWritten += wireData.writeBuffer16(rrBuffer, opaqueSize);
        return bytesWritten;
      }
    }
  }
}
