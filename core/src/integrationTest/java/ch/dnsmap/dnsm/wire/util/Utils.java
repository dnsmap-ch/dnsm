package ch.dnsmap.dnsm.wire.util;

import ch.dnsmap.dnsm.Question;
import ch.dnsmap.dnsm.record.ResourceRecord;
import ch.dnsmap.dnsm.wire.DnsInput;
import java.util.List;

public final class Utils {

  public static List<Question> jumpToQuestionSection(DnsInput dnsInput) {
    dnsInput.getHeader();
    return dnsInput.getQuestion();
  }

  public static List<ResourceRecord> jumpToAnswerSection(DnsInput dnsInput) {
    jumpToQuestionSection(dnsInput);
    return dnsInput.getAnswer();
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
