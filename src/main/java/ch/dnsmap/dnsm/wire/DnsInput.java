package ch.dnsmap.dnsm.wire;

import ch.dnsmap.dnsm.Header;
import ch.dnsmap.dnsm.Question;
import ch.dnsmap.dnsm.wire.record.ResourceRecordParser;
import ch.dnsmap.dnsm.record.ResourceRecord;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.IntStream.range;

public final class DnsInput {

    private final ReadableByte networkByte;
    private final QuestionDomainParser questionDomainParser;
    private final ResourceRecordParser resourceRecordParser;

    private Header header;
    private List<Question> question;
    private List<ResourceRecord> answer;
    private List<ResourceRecord> authority;
    private List<ResourceRecord> additional;

    private DnsInput(ReadableByte networkByte) {
        this.networkByte = networkByte;
        this.questionDomainParser = new QuestionDomainParser();
        this.resourceRecordParser = new ResourceRecordParser();
    }

    public static DnsInput fromWire(byte[] inputData) {
        return new DnsInput(NetworkByte.of(inputData));
    }

    public Header getHeader() {
        if (header != null) {
            return header;
        }
        int id = networkByte.readUInt16();
        byte[] flags = networkByte.readByte16();
        int qdCount = networkByte.readUInt16();
        int anCount = networkByte.readUInt16();
        int nsCount = networkByte.readUInt16();
        int arCount = networkByte.readUInt16();

        question = new ArrayList<>(qdCount);
        answer = new ArrayList<>(anCount);
        authority = new ArrayList<>(nsCount);
        additional = new ArrayList<>(arCount);

        header = new Header(id, flags, qdCount, anCount, nsCount, arCount);
        return header;
    }

    public List<Question> getQuestion() {
        if (question.isEmpty()) {
            range(0, getHeader().qdCount()).forEach(element -> question.add(questionDomainParser.fromWire(networkByte)));
        }
        return question;
    }

    public List<ResourceRecord> getAnswer() {
        if (answer.isEmpty()) {
            range(0, getHeader().anCount()).forEach(element -> answer.add(resourceRecordParser.fromWire(networkByte)));
        }
        return answer;
    }

    public List<ResourceRecord> getAuthority() {
        if (authority.isEmpty()) {
            range(0, getHeader().nsCount()).forEach(element -> authority.add(resourceRecordParser.fromWire(networkByte)));
        }
        return authority;
    }

    public List<ResourceRecord> getAdditional() {
        if (additional.isEmpty()) {
            range(0, getHeader().arCount()).forEach(element -> additional.add(resourceRecordParser.fromWire(networkByte)));
        }
        return additional;
    }
}
