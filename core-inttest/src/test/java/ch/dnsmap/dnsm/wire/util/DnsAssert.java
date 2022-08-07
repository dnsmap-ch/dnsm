package ch.dnsmap.dnsm.wire.util;

import static ch.dnsmap.dnsm.DnsType.A;
import static ch.dnsmap.dnsm.DnsType.AAAA;
import static ch.dnsmap.dnsm.DnsType.CNAME;
import static ch.dnsmap.dnsm.DnsType.NS;
import static ch.dnsmap.dnsm.DnsType.TXT;
import static org.assertj.core.api.Assertions.assertThat;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsQueryClass;
import ch.dnsmap.dnsm.DnsQueryType;
import ch.dnsmap.dnsm.DnsType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Header;
import ch.dnsmap.dnsm.Question;
import ch.dnsmap.dnsm.record.ResourceRecord;
import ch.dnsmap.dnsm.record.ResourceRecordA;
import ch.dnsmap.dnsm.record.ResourceRecordAaaa;
import ch.dnsmap.dnsm.record.ResourceRecordCname;
import ch.dnsmap.dnsm.record.ResourceRecordNs;
import ch.dnsmap.dnsm.record.ResourceRecordTxt;
import ch.dnsmap.dnsm.record.type.Cname;
import ch.dnsmap.dnsm.record.type.Ip4;
import ch.dnsmap.dnsm.record.type.Ip6;
import ch.dnsmap.dnsm.record.type.Ns;
import ch.dnsmap.dnsm.record.type.Txt;
import java.util.List;

public final class DnsAssert {

  public static void assertDnsHeader(Header header, int msgId, byte[] flags, int qd, int an, int ns,
                                     int ar) {
    assertThat(header).satisfies(headerField -> {
      assertThat(headerField.id()).isEqualTo(msgId);
      assertThat(headerField.flags()).isEqualTo(flags);
      assertThat(headerField.qdCount()).isEqualTo(qd);
      assertThat(headerField.anCount()).isEqualTo(an);
      assertThat(headerField.nsCount()).isEqualTo(ns);
      assertThat(headerField.arCount()).isEqualTo(ar);
    });
  }

  public static void assertDnsQuestion(List<Question> questions, Domain questionName,
                                       DnsQueryType dnsQueryType, DnsQueryClass dnsQueryClass) {
    assertThat(questions.size()).isEqualTo(1);
    assertThat(questions.get(0)).satisfies(question -> {
      assertThat(question.questionName()).isEqualTo(questionName);
      assertThat(question.questionType()).isEqualTo(dnsQueryType);
      assertThat(question.questionClass()).isEqualTo(dnsQueryClass);
    });
  }

  public static void assertDnsRecordCname(ResourceRecord resourceRecord, Domain domainName,
                                          DnsClass dnsClass, long ttl, Cname cname) {
    assertDnsRecord(resourceRecord, domainName, CNAME, dnsClass, ttl);
    assertThat(((ResourceRecordCname) resourceRecord).getCname()).isEqualTo(cname);
  }

  public static void assertDnsRecordIp4(ResourceRecord resourceRecord, Domain domainName,
                                        DnsClass dnsClass, long ttl, Ip4 ip4) {
    assertDnsRecord(resourceRecord, domainName, A, dnsClass, ttl);
    assertThat(((ResourceRecordA) resourceRecord).getIp4()).isEqualTo(ip4);
  }

  public static void assertDnsRecordIp6(ResourceRecord resourceRecord, Domain domainName,
                                        DnsClass dnsClass, long ttl, Ip6 ip6) {
    assertDnsRecord(resourceRecord, domainName, AAAA, dnsClass, ttl);
    assertThat(((ResourceRecordAaaa) resourceRecord).getIp6()).isEqualTo(ip6);
  }

  public static void assertDnsRecordNs(ResourceRecord resourceRecord, Domain domainName,
                                       DnsClass dnsClass, long ttl, Ns ns) {
    assertDnsRecord(resourceRecord, domainName, NS, dnsClass, ttl);
    assertThat(((ResourceRecordNs) resourceRecord).getNs()).isEqualTo(ns);
  }

  public static void assertDnsRecordTxt(ResourceRecord resourceRecord, Domain domainName,
                                       DnsClass dnsClass, long ttl, Txt txt) {
    assertDnsRecord(resourceRecord, domainName, TXT, dnsClass, ttl);
    assertThat(((ResourceRecordTxt) resourceRecord).getTxt()).isEqualTo(txt);
  }

  private static void assertDnsRecord(ResourceRecord resourceRecord, Domain domainName,
                                      DnsType dnsType, DnsClass dnsClass, long ttl) {
    assertThat(resourceRecord.getName()).isEqualTo(domainName);
    assertThat(resourceRecord.getDnsType()).isEqualTo(dnsType);
    assertThat(resourceRecord.getDnsClass()).isEqualTo(dnsClass);
    assertThat(resourceRecord.getTtl()).isEqualTo(ttl);
  }
}
