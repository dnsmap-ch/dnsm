package ch.dnsmap.dnsm.wire.util;

import static org.assertj.core.api.Assertions.assertThat;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsQueryClass;
import ch.dnsmap.dnsm.DnsQueryType;
import ch.dnsmap.dnsm.DnsType;
import ch.dnsmap.dnsm.Header;
import ch.dnsmap.dnsm.Question;
import ch.dnsmap.dnsm.record.ResourceRecord;
import ch.dnsmap.dnsm.record.ResourceRecordA;
import ch.dnsmap.dnsm.record.ResourceRecordCname;
import ch.dnsmap.dnsm.record.type.Cname;
import ch.dnsmap.dnsm.record.type.Ip4;
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

  public static void assertDnsQuestion(List<Question> questions, String questionName,
                                       DnsQueryType dnsQueryType, DnsQueryClass dnsQueryClass) {
    assertThat(questions.size()).isEqualTo(1);
    assertThat(questions.get(0)).satisfies(question -> {
      assertThat(question.questionName().getCanonical()).isEqualTo(questionName);
      assertThat(question.questionType()).isEqualTo(dnsQueryType);
      assertThat(question.questionClass()).isEqualTo(dnsQueryClass);
    });
  }

  public static void assertDnsRecordCname(ResourceRecord resourceRecord, String domainName,
                                          DnsType dnsType, DnsClass dnsClass, long ttl,
                                          Cname cname) {
    assertDnsRecord(resourceRecord, domainName, dnsType, dnsClass, ttl);
    assertThat(((ResourceRecordCname) resourceRecord).getCname()).isEqualTo(cname);
  }

  public static void assertDnsRecordIp4(ResourceRecord resourceRecord, String domainName,
                                        DnsType dnsType, DnsClass dnsClass, long ttl,
                                        Ip4 ip4) {
    assertDnsRecord(resourceRecord, domainName, dnsType, dnsClass, ttl);
    assertThat(((ResourceRecordA) resourceRecord).getIp4()).isEqualTo(ip4);
  }

  private static void assertDnsRecord(ResourceRecord resourceRecord, String domainName,
                                      DnsType dnsType, DnsClass dnsClass, long ttl) {
    assertThat(resourceRecord.getName().getCanonical()).isEqualTo(domainName);
    assertThat(resourceRecord.getDnsType()).isEqualTo(dnsType);
    assertThat(resourceRecord.getDnsClass()).isEqualTo(dnsClass);
    assertThat(resourceRecord.getTtl()).isEqualTo(ttl);
  }
}
