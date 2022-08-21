package ch.dnsmap.dnsm.wire.parser;


import ch.dnsmap.dnsm.DnsQueryClass;
import ch.dnsmap.dnsm.DnsQueryType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Question;
import ch.dnsmap.dnsm.wire.bytes.ReadableByte;
import ch.dnsmap.dnsm.wire.bytes.WriteableByte;

public final class QuestionDomainParser implements ByteParser<Question> {

  private final DomainParser domainParser;

  public QuestionDomainParser() {
    domainParser = new DomainParser();
  }

  @Override
  public Question fromWire(ReadableByte wireData) {
    Domain qName = domainParser.fromWire(wireData);
    DnsQueryType qType = DnsQueryType.of(wireData.readUInt16());
    DnsQueryClass qClass = DnsQueryClass.of(wireData.readUInt16());
    return new Question(qName, qType, qClass);
  }

  @Override
  public Question fromWire(ReadableByte wireData, int length) {
    return null;
  }

  @Override
  public int toWire(WriteableByte wireData, Question data) {
    int length = domainParser.toWire(wireData, data.questionName());
    length += wireData.writeUInt16(data.questionType().getValue());
    length += wireData.writeUInt16(data.questionClass().getValue());
    return length;
  }

  @Override
  public int bytesToWrite(Question data) {
    int byteToWrite = domainParser.bytesToWrite(data.questionName());
    byteToWrite += 2;
    byteToWrite += 2;
    return byteToWrite;
  }
}