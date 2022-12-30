package ch.dnsmap.dnsm.dnsm.wire.util;

import ch.dnsmap.dnsm.Question;
import ch.dnsmap.dnsm.header.Header;
import ch.dnsmap.dnsm.record.ResourceRecord;
import ch.dnsmap.dnsm.wire.DnsInput;
import ch.dnsmap.dnsm.wire.DnsOutput;
import ch.dnsmap.dnsm.wire.ParserOptions;
import java.io.ByteArrayOutputStream;
import java.util.List;

public final class Utils {

  public static DnsInput udpDnsInput(ByteArrayOutputStream dnsBytes) {
    ParserOptions options = ParserOptions.Builder.builder().build();
    return dnsInput(options, dnsBytes);
  }

  private static DnsInput dnsInput(ParserOptions options, ByteArrayOutputStream dnsBytes) {
    return DnsInput.fromWire(options, dnsBytes.toByteArray());
  }

  public static DnsOutput udpDnsOutput(Header header,
                                       Question question,
                                       List<ResourceRecord> answer,
                                       List<ResourceRecord> authoritative,
                                       List<ResourceRecord> additional) {
    ParserOptions options = ParserOptions.Builder.builder().unsetTcp().build();
    return dnsOutput(options, header, question, answer, authoritative, additional);
  }

  private static DnsOutput dnsOutput(ParserOptions options,
                                     Header header,
                                     Question question,
                                     List<ResourceRecord> answer,
                                     List<ResourceRecord> authoritative,
                                     List<ResourceRecord> additional) {
    return DnsOutput.toWire(options, header, question, answer, authoritative, additional);
  }

  public static List<Question> jumpToQuestionSection(DnsInput dnsInput) {
    dnsInput.getHeader();
    return dnsInput.getQuestion();
  }

  public static List<ResourceRecord> jumpToAnswerSection(DnsInput dnsInput) {
    jumpToQuestionSection(dnsInput);
    return dnsInput.getAnswers();
  }

  public static List<ResourceRecord> jumpToAuthoritySection(DnsInput dnsInput) {
    jumpToAnswerSection(dnsInput);
    return dnsInput.getAuthority();
  }

  public static List<ResourceRecord> jumpToAdditionalSection(DnsInput dnsInput) {
    jumpToAuthoritySection(dnsInput);
    return dnsInput.getAdditional();
  }
}
