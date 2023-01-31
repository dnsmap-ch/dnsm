package ch.dnsmap.dnsm.dnsm.wire.util;

import static ch.dnsmap.dnsm.DnsType.A;
import static ch.dnsmap.dnsm.DnsType.AAAA;
import static ch.dnsmap.dnsm.DnsType.CNAME;
import static ch.dnsmap.dnsm.DnsType.MX;
import static ch.dnsmap.dnsm.DnsType.NS;
import static ch.dnsmap.dnsm.DnsType.SOA;
import static ch.dnsmap.dnsm.DnsType.TXT;
import static org.assertj.core.api.Assertions.assertThat;

import ch.dnsmap.dnsm.DnsClass;
import ch.dnsmap.dnsm.DnsQueryClass;
import ch.dnsmap.dnsm.DnsQueryType;
import ch.dnsmap.dnsm.DnsType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Question;
import ch.dnsmap.dnsm.Ttl;
import ch.dnsmap.dnsm.header.Header;
import ch.dnsmap.dnsm.header.HeaderBitFlags;
import ch.dnsmap.dnsm.record.ResourceRecord;
import ch.dnsmap.dnsm.record.ResourceRecordA;
import ch.dnsmap.dnsm.record.ResourceRecordAaaa;
import ch.dnsmap.dnsm.record.ResourceRecordCname;
import ch.dnsmap.dnsm.record.ResourceRecordMx;
import ch.dnsmap.dnsm.record.ResourceRecordNs;
import ch.dnsmap.dnsm.record.ResourceRecordSoa;
import ch.dnsmap.dnsm.record.ResourceRecordTxt;
import ch.dnsmap.dnsm.record.type.Cname;
import ch.dnsmap.dnsm.record.type.Ip4;
import ch.dnsmap.dnsm.record.type.Ip6;
import ch.dnsmap.dnsm.record.type.Mx;
import ch.dnsmap.dnsm.record.type.Ns;
import ch.dnsmap.dnsm.record.type.Soa;
import ch.dnsmap.dnsm.record.type.Txt;
import java.util.List;

public final class DnsAssert {

  public static void assertDnsHeader(Header expected, Header result) {
    assertThat(expected.id()).isEqualTo(result.id());
    assertThat(expected.count()).isEqualTo(result.count());
    assertThat(expected.flags().getFlags()).containsExactlyInAnyOrder(
        result.flags().getFlags().toArray(new HeaderBitFlags[0]));
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
      DnsClass dnsClass, Ttl ttl, Cname cname) {
    assertDnsRecord(resourceRecord, domainName, CNAME, dnsClass, ttl);
    assertThat(((ResourceRecordCname) resourceRecord).cname()).isEqualTo(cname);
  }

  public static void assertDnsRecordIp4(ResourceRecord resourceRecord, Domain domainName,
      DnsClass dnsClass, Ttl ttl, Ip4 ip4) {
    assertDnsRecord(resourceRecord, domainName, A, dnsClass, ttl);
    assertThat(((ResourceRecordA) resourceRecord).ip4()).isEqualTo(ip4);
  }

  public static void assertDnsRecordIp6(ResourceRecord resourceRecord, Domain domainName,
      DnsClass dnsClass, Ttl ttl, Ip6 ip6) {
    assertDnsRecord(resourceRecord, domainName, AAAA, dnsClass, ttl);
    assertThat(((ResourceRecordAaaa) resourceRecord).ip6()).isEqualTo(ip6);
  }

  public static void assertDnsRecordNs(ResourceRecord resourceRecord, Domain domainName,
      DnsClass dnsClass, Ttl ttl, Ns ns) {
    assertDnsRecord(resourceRecord, domainName, NS, dnsClass, ttl);
    assertThat(((ResourceRecordNs) resourceRecord).ns()).isEqualTo(ns);
  }

  public static void assertDnsRecordMx(ResourceRecord resourceRecord, Domain domainName,
      DnsClass dnsClass, Ttl ttl, Mx mx) {
    assertDnsRecord(resourceRecord, domainName, MX, dnsClass, ttl);
    assertThat(((ResourceRecordMx) resourceRecord).mx()).isEqualTo(mx);
  }

  public static void assertDnsRecordSoa(ResourceRecord resourceRecord, Domain domainName,
      DnsClass dnsClass, Ttl ttl, Soa soa) {
    assertDnsRecord(resourceRecord, domainName, SOA, dnsClass, ttl);
    assertThat(((ResourceRecordSoa) resourceRecord).soa()).isEqualTo(soa);
  }

  public static void assertDnsRecordTxt(ResourceRecord resourceRecord, Domain domainName,
      DnsClass dnsClass, Ttl ttl, Txt txt) {
    assertDnsRecord(resourceRecord, domainName, TXT, dnsClass, ttl);
    assertThat(((ResourceRecordTxt) resourceRecord).txt()).isEqualTo(txt);
  }

  private static void assertDnsRecord(ResourceRecord resourceRecord, Domain domainName,
      DnsType dnsType, DnsClass dnsClass, Ttl ttl) {
    assertThat(resourceRecord.name()).isEqualTo(domainName);
    assertThat(resourceRecord.getDnsType()).isEqualTo(dnsType);
    assertThat(resourceRecord.dnsClass()).isEqualTo(dnsClass);
    assertThat(resourceRecord.ttl()).isEqualTo(ttl);
  }
}
