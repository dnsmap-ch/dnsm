package ch.dnsmap.dnsm.wire;

import static java.util.stream.IntStream.range;

import ch.dnsmap.dnsm.Message;
import ch.dnsmap.dnsm.Question;
import ch.dnsmap.dnsm.header.Header;
import ch.dnsmap.dnsm.header.HeaderCount;
import ch.dnsmap.dnsm.header.HeaderFlags;
import ch.dnsmap.dnsm.header.HeaderId;
import ch.dnsmap.dnsm.record.ResourceRecord;
import ch.dnsmap.dnsm.wire.bytes.NetworkByteBuffer;
import ch.dnsmap.dnsm.wire.bytes.ReadableByteBuffer;
import ch.dnsmap.dnsm.wire.parser.DomainParser;
import ch.dnsmap.dnsm.wire.parser.HeaderFlagsParser;
import ch.dnsmap.dnsm.wire.parser.QuestionDomainParser;
import ch.dnsmap.dnsm.wire.parser.ResourceRecordParser;
import ch.dnsmap.dnsm.wire.parser.WireReadable;
import java.util.ArrayList;
import java.util.List;

public final class DnsInput {

  private final ReadableByteBuffer networkByte;
  private final HeaderFlagsParser headerFlagsParser;
  private final WireReadable<Question> questionDomainParser;
  private final WireReadable<ResourceRecord> resourceRecordParser;
  private final ParserOptions parserOptions;

  private Header header;
  private List<Question> question;
  private List<ResourceRecord> answer;
  private List<ResourceRecord> authority;
  private List<ResourceRecord> additional;

  private DnsInput(ParserOptions parserOptions, ReadableByteBuffer networkByte) {
    this.parserOptions = parserOptions;
    this.networkByte = networkByte;
    this.headerFlagsParser = new HeaderFlagsParser();
    DomainParser domainParser = new DomainParser(parserOptions);
    this.questionDomainParser = new QuestionDomainParser(domainParser);
    this.resourceRecordParser = new ResourceRecordParser(domainParser);
  }

  public static DnsInput fromWire(ParserOptions parserOptions, byte[] inputData) {
    return new DnsInput(parserOptions, NetworkByteBuffer.of(inputData));
  }

  public Message getMessage() {
    return new Message(getHeader(), getQuestion().get(0), getAnswers(), getAuthority(),
        getAdditional());
  }

  public Header getHeader() {
    if (header != null) {
      return header;
    }
    if (parserOptions.isTcp()) {
      networkByte.readUInt16();
    }
    HeaderId id = HeaderId.of(networkByte.readUInt16());
    HeaderFlags flags = headerFlagsParser.fromWire(networkByte, 2);
    int qdCount = networkByte.readUInt16();
    int anCount = networkByte.readUInt16();
    int nsCount = networkByte.readUInt16();
    int arCount = networkByte.readUInt16();
    HeaderCount count = HeaderCount.of(qdCount, anCount, nsCount, arCount);

    question = new ArrayList<>(qdCount);
    answer = new ArrayList<>(anCount);
    authority = new ArrayList<>(nsCount);
    additional = new ArrayList<>(arCount);

    header = new Header(id, flags, count);
    return header;
  }

  public List<Question> getQuestion() {
    if (question.isEmpty()) {
      range(0, getHeader().count().getQdCount()).forEach(
          element -> question.add(questionDomainParser.fromWire(networkByte)));
    }
    return question;
  }

  public List<ResourceRecord> getAnswers() {
    if (answer.isEmpty()) {
      range(0, getHeader().count().getAnCount()).forEach(
          element -> answer.add(resourceRecordParser.fromWire(networkByte)));
    }
    return answer;
  }

  public List<ResourceRecord> getAuthority() {
    if (authority.isEmpty()) {
      range(0, getHeader().count().getNsCount()).forEach(
          element -> authority.add(resourceRecordParser.fromWire(networkByte)));
    }
    return authority;
  }

  public List<ResourceRecord> getAdditional() {
    if (additional.isEmpty()) {
      range(0, getHeader().count().getArCount()).forEach(
          element -> additional.add(resourceRecordParser.fromWire(networkByte)));
    }
    return additional;
  }
}
