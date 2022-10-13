package ch.dnsmap.dnsm.wire.parser;


import ch.dnsmap.dnsm.DnsQueryClass;
import ch.dnsmap.dnsm.DnsQueryType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Question;
import ch.dnsmap.dnsm.wire.bytes.ReadableByteBuffer;
import ch.dnsmap.dnsm.wire.bytes.WriteableByteBuffer;

public final class QuestionDomainParser implements WireWritable<Question>, WireReadable<Question> {

  private final DomainParser domainParser;

  public QuestionDomainParser(DomainParser domainParser) {
    this.domainParser = domainParser;
  }

  @Override
  public Question fromWire(ReadableByteBuffer wireData) {
    Domain qName = domainParser.fromWire(wireData);
    DnsQueryType qType = DnsQueryType.of(wireData.readUInt16());
    DnsQueryClass qClass = DnsQueryClass.of(wireData.readUInt16());
    return new Question(qName, qType, qClass);
  }

  @Override
  public int toWire(WriteableByteBuffer wireData, Question data) {
    int bytesWritten = domainParser.toWire(wireData, data.questionName());
    bytesWritten += wireData.writeUInt16(data.questionType().getValue());
    bytesWritten += wireData.writeUInt16(data.questionClass().getValue());
    return bytesWritten;
  }
}
