package ch.dnsmap.dnsm.wire;

import ch.dnsmap.dnsm.Question;
import ch.dnsmap.dnsm.header.Header;
import ch.dnsmap.dnsm.record.ResourceRecord;
import ch.dnsmap.dnsm.wire.bytes.NetworkByteBuffer;
import ch.dnsmap.dnsm.wire.bytes.WriteableByteBuffer;
import ch.dnsmap.dnsm.wire.parser.DomainParser;
import ch.dnsmap.dnsm.wire.parser.HeaderFlagsParser;
import ch.dnsmap.dnsm.wire.parser.QuestionDomainParser;
import ch.dnsmap.dnsm.wire.parser.ResourceRecordParser;
import ch.dnsmap.dnsm.wire.parser.WireWritable;
import java.util.List;

public final class DnsOutput {

  private final Header header;
  private final Question question;
  private final List<ResourceRecord> answers;
  private final List<ResourceRecord> authoritatives;
  private final List<ResourceRecord> additionals;
  private final HeaderFlagsParser headerFlagsParser;
  private final WireWritable<Question> questionDomainParser;
  private final WireWritable<ResourceRecord> resourceRecordParser;
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
    DomainParser domainParser = new DomainParser();
    this.questionDomainParser = new QuestionDomainParser(domainParser);
    this.resourceRecordParser = new ResourceRecordParser(domainParser);
    networkByte = NetworkByteBuffer.of(1024);
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
}
