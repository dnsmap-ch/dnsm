package ch.dnsmap.dnsm.wire;

import ch.dnsmap.dnsm.Header;
import ch.dnsmap.dnsm.Label;
import ch.dnsmap.dnsm.Question;
import ch.dnsmap.dnsm.record.ResourceRecord;
import ch.dnsmap.dnsm.wire.record.ResourceRecordParser;
import java.util.List;

public final class DnsOutput {

  private static final int DNS_HEADER_LENGTH = 12;
  private static final int DNS_TYPE_FIELD_LENGTH = 2;
  private static final int DNS_CLASS_FIELD_LENGTH = 2;
  private static final int DOMAIN_NULL_TERMINATION_LENGTH = 1;
  private static final int DNS_TTL_FIELD_LENGTH = 4;

  private final Header header;
  private final Question question;
  private final List<ResourceRecord> answers;
  private final QuestionDomainParser questionDomainParser;
  private final ResourceRecordParser resourceRecordParser;
  private final WriteableByte networkByte;

  private int headerTo;
  private int questionTo;
  private int answerTo;

  private DnsOutput(Header header, Question question, List<ResourceRecord> answers) {
    this.header = header;
    this.question = question;
    this.answers = answers;
    this.questionDomainParser = new QuestionDomainParser();
    this.resourceRecordParser = new ResourceRecordParser();
    int capacity = getCapacity(question, answers);
    networkByte = NetworkByte.of(capacity);
  }

  public static DnsOutput toWire(Header header, Question question, List<ResourceRecord> answers) {
    return new DnsOutput(header, question, answers);
  }

  public byte[] getHeader() {
    int headerFrom = 0;
    if (headerTo == 0) {
      headerTo += networkByte.writeUInt16(header.id());
      headerTo += networkByte.writeByte16(header.flags());
      headerTo += networkByte.writeUInt16(header.qdCount());
      headerTo += networkByte.writeUInt16(header.anCount());
      headerTo += networkByte.writeUInt16(header.nsCount());
      headerTo += networkByte.writeUInt16(header.arCount());
    }
    return networkByte.range(headerFrom, headerTo);
  }

  public byte[] getQuestion() {
    if (questionTo == 0) {
      questionTo += headerTo;
      int questionByteLength = questionDomainParser.toWire(networkByte, question);
      questionTo += questionByteLength;

      int startPositionOfDomainName = questionTo - questionByteLength;
      DomainCompression domainCompression = new DomainCompression();
      domainCompression.addDomain(question.questionName(), startPositionOfDomainName);
      resourceRecordParser.setDomainPositionMap(domainCompression);
    }
    return networkByte.range(headerTo, questionTo);
  }

  public byte[] getAnswers() {
    if (answerTo == 0) {
      answerTo += questionTo;
      answerTo += answers.stream()
          .map(answerElement -> resourceRecordParser.toWire(networkByte, answerElement))
          .reduce(0, Integer::sum);
    }
    return networkByte.range(questionTo, answerTo);
  }

  private int getCapacity(Question question, List<ResourceRecord> answers) {
    return getHeaderByteSize() + getQuestionByteSize(question) + getAnswerByteSize(answers);
  }

  private static int getHeaderByteSize() {
    return DNS_HEADER_LENGTH;
  }

  private static int getQuestionByteSize(Question question) {
    int domainLength = question.questionName().getLabelCount()
        + question.questionName().getLabels().stream().map(Label::length).reduce(0, Integer::sum)
        + DOMAIN_NULL_TERMINATION_LENGTH;
    return domainLength + DNS_TYPE_FIELD_LENGTH + DNS_CLASS_FIELD_LENGTH;
  }

  private int getAnswerByteSize(List<ResourceRecord> resourceRecords) {
    return resourceRecords.stream()
        .map(resourceRecordParser::bytesToWrite)
        .reduce(0, Integer::sum);
  }
}
