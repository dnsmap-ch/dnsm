package ch.dnsmap.dnsm.wire;


import ch.dnsmap.dnsm.DnsQueryClass;
import ch.dnsmap.dnsm.DnsQueryType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Question;

public final class QuestionDomainParser implements ByteParser<Question> {

    private final DomainParser domainParser;

    public QuestionDomainParser() {
        domainParser = new DomainParser();
    }

    public Question fromWire(ReadableByte wireData) {
        Domain qName = domainParser.fromWire(wireData);
        DnsQueryType qType = DnsQueryType.of(wireData.readUInt16());
        DnsQueryClass qClass = DnsQueryClass.of(wireData.readUInt16());
        return new Question(qName, qType, qClass);
    }
}
