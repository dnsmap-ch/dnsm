package ch.dnsmap.dnsm.wire;

import ch.dnsmap.dnsm.Label;
import ch.dnsmap.dnsm.Question;
import ch.dnsmap.dnsm.header.Header;
import ch.dnsmap.dnsm.record.ResourceRecord;
import ch.dnsmap.dnsm.wire.bytes.NetworkByteBuffer;
import ch.dnsmap.dnsm.wire.bytes.WriteableByteBuffer;
import ch.dnsmap.dnsm.wire.parser.HeaderFlagsParser;
import ch.dnsmap.dnsm.wire.parser.QuestionDomainParser;
import ch.dnsmap.dnsm.wire.parser.ResourceRecordParser;
import java.util.List;

public final class DnsOutput {

  private static final int DNS_HEADER_LENGTH = 12;
  private static final int DNS_TYPE_FIELD_LENGTH = 2;
  private static final int DNS_CLASS_FIELD_LENGTH = 2;
  private static final int DOMAIN_NULL_TERMINATION_LENGTH = 1;

  private final Header header;
  private final Question question;
  private final List<ResourceRecord> answers;
  private final List<ResourceRecord> authoritatives;
  private final List<ResourceRecord> additionals;
  private final HeaderFlagsParser headerFlagsParser;
  private final QuestionDomainParser questionDomainParser;
  private final ResourceRecordParser resourceRecordParser;
  private final WriteableByteBuffer networkByte;

  private int headerTo;
  private int questionTo;
  private int answerTo;
  private int authoritativeTo;
  private int additionalTo;

  private DnsOutput(Header header, Question question, List<ResourceRecord> answers,
                    List<ResourceRecord> authoritatives, List<ResourceRecord> additionals) {
    this.header = header;
    this.question = question;
    this.answers = answers;
    this.authoritatives = authoritatives;
    this.additionals = additionals;
    this.headerFlagsParser = new HeaderFlagsParser();
    this.questionDomainParser = new QuestionDomainParser();
    this.resourceRecordParser = new ResourceRecordParser();
    int capacity = getCapacity(question, answers, authoritatives, additionals);
    networkByte = NetworkByteBuffer.of(capacity);
  }

  public static DnsOutput toWire(Header header, Question question, List<ResourceRecord> answers,
                                 List<ResourceRecord> authoritatives,
                                 List<ResourceRecord> additionals) {
    return new DnsOutput(header, question, answers, authoritatives, additionals);
  }

  public byte[] getHeader() {
    if (headerTo == 0) {
      headerTo += networkByte.writeUInt16(header.id().getId());
      headerTo += headerFlagsParser.toWire(networkByte, header.flags());
      headerTo += networkByte.writeUInt16(header.count().getQdCount());
      headerTo += networkByte.writeUInt16(header.count().getAnCount());
      headerTo += networkByte.writeUInt16(header.count().getNsCount());
      headerTo += networkByte.writeUInt16(header.count().getArCount());
    }
    return networkByte.range(0, headerTo);
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

  public byte[] getAuthoritatives() {
    if (authoritativeTo == 0) {
      authoritativeTo += answerTo;
      authoritativeTo += authoritatives.stream()
          .map(authoritativeElement -> resourceRecordParser.toWire(networkByte,
              authoritativeElement))
          .reduce(0, Integer::sum);
    }
    return networkByte.range(answerTo, authoritativeTo);
  }

  public byte[] getAdditional() {
    if (additionalTo == 0) {
      additionalTo += authoritativeTo;
      additionalTo += additionals.stream()
          .map(additionalElement -> resourceRecordParser.toWire(networkByte, additionalElement))
          .reduce(0, Integer::sum);
    }
    return networkByte.range(authoritativeTo, additionalTo);
  }

  private int getCapacity(Question question, List<ResourceRecord> answers,
                          List<ResourceRecord> authoritatives,
                          List<ResourceRecord> additionals) {
    return getHeaderByteSize()
        + getQuestionByteSize(question)
        + getAnswerByteSize(answers)
        + getResourceRecordByteSize(authoritatives)
        + getResourceRecordByteSize(additionals);
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

  private int getResourceRecordByteSize(List<ResourceRecord> resourceRecords) {
    return resourceRecords.stream()
        .map(resourceRecordParser::bytesToWrite)
        .reduce(0, Integer::sum);
  }
}
