package ch.dnsmap.dnsm.dnsm.wire;

import static ch.dnsmap.dnsm.DnsClass.IN;
import static ch.dnsmap.dnsm.dnsm.wire.util.DnsAssert.assertDnsHeader;
import static ch.dnsmap.dnsm.dnsm.wire.util.DnsAssert.assertDnsQuestion;
import static ch.dnsmap.dnsm.dnsm.wire.util.DnsAssert.assertDnsRecordTxt;
import static ch.dnsmap.dnsm.dnsm.wire.util.Utils.jumpToAdditionalSection;
import static ch.dnsmap.dnsm.dnsm.wire.util.Utils.jumpToAnswerSection;
import static ch.dnsmap.dnsm.dnsm.wire.util.Utils.jumpToAuthoritySection;
import static ch.dnsmap.dnsm.dnsm.wire.util.Utils.jumpToQuestionSection;
import static ch.dnsmap.dnsm.dnsm.wire.util.Utils.udpDnsInput;
import static ch.dnsmap.dnsm.dnsm.wire.util.Utils.udpDnsOutput;
import static ch.dnsmap.dnsm.header.HeaderBitFlags.QR;
import static ch.dnsmap.dnsm.header.HeaderBitFlags.RA;
import static ch.dnsmap.dnsm.header.HeaderBitFlags.RD;
import static ch.dnsmap.dnsm.header.HeaderOpcode.QUERY;
import static ch.dnsmap.dnsm.header.HeaderRcode.NO_ERROR;
import static org.assertj.core.api.Assertions.assertThat;

import ch.dnsmap.dnsm.DnsQueryClass;
import ch.dnsmap.dnsm.DnsQueryType;
import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Question;
import ch.dnsmap.dnsm.Ttl;
import ch.dnsmap.dnsm.header.Header;
import ch.dnsmap.dnsm.header.HeaderCount;
import ch.dnsmap.dnsm.header.HeaderFlags;
import ch.dnsmap.dnsm.header.HeaderId;
import ch.dnsmap.dnsm.record.ResourceRecord;
import ch.dnsmap.dnsm.record.ResourceRecordTxt;
import ch.dnsmap.dnsm.record.type.Txt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class TxtParsingTest {

  public static final String GOOGLE_COM = "google.com.";

  private static final HeaderId MESSAGE_ID = HeaderId.of(57546);
  private static final HeaderFlags FLAGS = new HeaderFlags(QUERY, NO_ERROR, QR, RA, RD);
  private static final HeaderCount COUNT = HeaderCount.of(1, 11, 0, 0);
  private static final Header HEADER = new Header(MESSAGE_ID, FLAGS, COUNT);
  private static final Domain DOMAIN = Domain.of(GOOGLE_COM);
  private static final Domain QUESTION_DOMAIN = DOMAIN;
  private static final Domain ANSWER_DOMAIN = DOMAIN;
  private static final Ttl TTL = Ttl.of(3600);

  private static final String TXT_01 = "v=spf1 include:_spf.google.com ~all";
  private static final String TXT_02 =
      "webexdomainverification.8YX6G=6e6922db-e3e6-4a36-904e-a805c28087fa";
  private static final String TXT_03 = "apple-domain-verification=30afIBcvSuDV2PLX";
  private static final String TXT_04 =
      "google-site-verification=TV9-DBe4R80X4v0M4U_bd_J9cpOJM0nikft0jAgjmsQ";
  private static final String TXT_05 =
      "atlassian-domain-verification=5YjTmWmjI92ewqkx2oXmBaD60Td9zWon9r6eakvHX6B77zzkFQto8PQ9Qs"
          + "Knbf4I";
  private static final String TXT_06 =
      "google-site-verification=wD8N7i1JTNTkezJ49swvWW48f8_9xveREV4oB-0Hf5o";
  private static final String TXT_07 = "MS=E4A68B9AB2BB9670BCE15412F62916164C0B20BB";
  private static final String TXT_08 =
      "facebook-domain-verification=22rm551cu4k0ab0bxsw536tlds4h95";
  private static final String TXT_09 = "docusign=1b0a6754-49b1-4db5-8540-d2c12664b289";
  private static final String TXT_10 = "docusign=05958488-4752-4ef2-95eb-aa7ba8a3bd0e";
  private static final String TXT_11 =
      "globalsign-smime-dv=CDYX+XFHUw2wml6/Gb8+59BsH31KzUr6c1l2BPvqKX8=";

  private ByteArrayOutputStream dnsBytes;

  @BeforeEach
  void setupDnsBytes() throws IOException {
    dnsBytes = new ByteArrayOutputStream();
    dnsBytes.write(DNS_BYTES_HEADER);
    dnsBytes.write(DNS_BYTES_QUESTION);
    dnsBytes.write(DNS_BYTES_ANSWER);
  }

  @Test
  void testDnsHeaderInputParsing() {
    var dnsInput = udpDnsInput(dnsBytes);
    var header = dnsInput.getHeader();
    assertDnsHeader(HEADER, header);
  }

  @Test
  void testDnsQuestionInputParsing() {
    var dnsInput = udpDnsInput(dnsBytes);
    var questions = jumpToQuestionSection(dnsInput);
    assertDnsQuestion(questions, QUESTION_DOMAIN, DnsQueryType.TXT, DnsQueryClass.IN);
  }

  @Test
  void testDnsAnswerInputParsing() {
    var dnsInput = udpDnsInput(dnsBytes);

    var answers = jumpToAnswerSection(dnsInput);

    assertThat(answers.size()).isEqualTo(11);
    assertDnsRecordTxt(answers.get(0), ANSWER_DOMAIN, IN, TTL, new Txt(TXT_01));
    assertDnsRecordTxt(answers.get(1), ANSWER_DOMAIN, IN, TTL, new Txt(TXT_02));
    assertDnsRecordTxt(answers.get(2), ANSWER_DOMAIN, IN, TTL, new Txt(TXT_03));
    assertDnsRecordTxt(answers.get(3), ANSWER_DOMAIN, IN, TTL, new Txt(TXT_04));
    assertDnsRecordTxt(answers.get(4), ANSWER_DOMAIN, IN, TTL, new Txt(TXT_05));
    assertDnsRecordTxt(answers.get(5), ANSWER_DOMAIN, IN, TTL, new Txt(TXT_06));
    assertDnsRecordTxt(answers.get(6), ANSWER_DOMAIN, IN, TTL, new Txt(TXT_07));
    assertDnsRecordTxt(answers.get(7), ANSWER_DOMAIN, IN, TTL, new Txt(TXT_08));
    assertDnsRecordTxt(answers.get(8), ANSWER_DOMAIN, IN, TTL, new Txt(TXT_09));
    assertDnsRecordTxt(answers.get(9), ANSWER_DOMAIN, IN, TTL, new Txt(TXT_10));
    assertDnsRecordTxt(answers.get(10), ANSWER_DOMAIN, IN, TTL, new Txt(TXT_11));
  }

  @Test
  void testDnsAuthorityInputParsing() {
    var dnsInput = udpDnsInput(dnsBytes);
    var authorities = jumpToAuthoritySection(dnsInput);
    assertThat(authorities.size()).isEqualTo(0);
  }

  @Test
  void testDnsAdditionalInputParsing() {
    var dnsInput = udpDnsInput(dnsBytes);
    var additional = jumpToAdditionalSection(dnsInput);
    assertThat(additional.size()).isEqualTo(0);
  }

  @Test
  void testOutputParsing() {
    var question = composeQuestion();
    var answer = composeAnswer();
    List<ResourceRecord> authoritative = List.of();
    List<ResourceRecord> additional = List.of();

    var dnsOutput = udpDnsOutput(HEADER, question, answer, authoritative, additional);

    assertThat(dnsOutput.getHeader()).isEqualTo(DNS_BYTES_HEADER);
    assertThat(dnsOutput.getQuestion()).isEqualTo(DNS_BYTES_QUESTION);
    assertThat(dnsOutput.getAnswers()).isEqualTo(DNS_BYTES_ANSWER);
  }

  private static Question composeQuestion() {
    return new Question(QUESTION_DOMAIN, DnsQueryType.TXT, DnsQueryClass.IN);
  }

  private static List<ResourceRecord> composeAnswer() {
    List<ResourceRecord> answers = new ArrayList<>(11);
    answers.add(new ResourceRecordTxt(ANSWER_DOMAIN, IN, TTL, new Txt(TXT_01)));
    answers.add(new ResourceRecordTxt(ANSWER_DOMAIN, IN, TTL, new Txt(TXT_02)));
    answers.add(new ResourceRecordTxt(ANSWER_DOMAIN, IN, TTL, new Txt(TXT_03)));
    answers.add(new ResourceRecordTxt(ANSWER_DOMAIN, IN, TTL, new Txt(TXT_04)));
    answers.add(new ResourceRecordTxt(ANSWER_DOMAIN, IN, TTL, new Txt(TXT_05)));
    answers.add(new ResourceRecordTxt(ANSWER_DOMAIN, IN, TTL, new Txt(TXT_06)));
    answers.add(new ResourceRecordTxt(ANSWER_DOMAIN, IN, TTL, new Txt(TXT_07)));
    answers.add(new ResourceRecordTxt(ANSWER_DOMAIN, IN, TTL, new Txt(TXT_08)));
    answers.add(new ResourceRecordTxt(ANSWER_DOMAIN, IN, TTL, new Txt(TXT_09)));
    answers.add(new ResourceRecordTxt(ANSWER_DOMAIN, IN, TTL, new Txt(TXT_10)));
    answers.add(new ResourceRecordTxt(ANSWER_DOMAIN, IN, TTL, new Txt(TXT_11)));
    return answers;
  }

  private static final byte[] DNS_BYTES_HEADER = new byte[]{
      (byte) 0xe0, (byte) 0xca, (byte) 0x81, (byte) 0x80, (byte) 0x00, (byte) 0x01, (byte) 0x00,
      (byte) 0x0b, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
  };

  private static final byte[] DNS_BYTES_QUESTION = new byte[]{
      (byte) 0x06, (byte) 0x67, (byte) 0x6f, (byte) 0x6f, (byte) 0x67, (byte) 0x6c, (byte) 0x65,
      (byte) 0x03, (byte) 0x63, (byte) 0x6f, (byte) 0x6d, (byte) 0x00, (byte) 0x00, (byte) 0x10,
      (byte) 0x00, (byte) 0x01
  };

  private static final byte[] DNS_BYTES_ANSWER = new byte[]{
      (byte) 0xc0, (byte) 0x0c, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x01, (byte) 0x00,
      (byte) 0x00, (byte) 0x0e, (byte) 0x10, (byte) 0x00, (byte) 0x24, (byte) 0x23, (byte) 0x76,
      (byte) 0x3d, (byte) 0x73, (byte) 0x70, (byte) 0x66, (byte) 0x31, (byte) 0x20, (byte) 0x69,
      (byte) 0x6e, (byte) 0x63, (byte) 0x6c, (byte) 0x75, (byte) 0x64, (byte) 0x65, (byte) 0x3a,
      (byte) 0x5f, (byte) 0x73, (byte) 0x70, (byte) 0x66, (byte) 0x2e, (byte) 0x67, (byte) 0x6f,
      (byte) 0x6f, (byte) 0x67, (byte) 0x6c, (byte) 0x65, (byte) 0x2e, (byte) 0x63, (byte) 0x6f,
      (byte) 0x6d, (byte) 0x20, (byte) 0x7e, (byte) 0x61, (byte) 0x6c, (byte) 0x6c, (byte) 0xc0,
      (byte) 0x0c, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
      (byte) 0x0e, (byte) 0x10, (byte) 0x00, (byte) 0x43, (byte) 0x42, (byte) 0x77, (byte) 0x65,
      (byte) 0x62, (byte) 0x65, (byte) 0x78, (byte) 0x64, (byte) 0x6f, (byte) 0x6d, (byte) 0x61,
      (byte) 0x69, (byte) 0x6e, (byte) 0x76, (byte) 0x65, (byte) 0x72, (byte) 0x69, (byte) 0x66,
      (byte) 0x69, (byte) 0x63, (byte) 0x61, (byte) 0x74, (byte) 0x69, (byte) 0x6f, (byte) 0x6e,
      (byte) 0x2e, (byte) 0x38, (byte) 0x59, (byte) 0x58, (byte) 0x36, (byte) 0x47, (byte) 0x3d,
      (byte) 0x36, (byte) 0x65, (byte) 0x36, (byte) 0x39, (byte) 0x32, (byte) 0x32, (byte) 0x64,
      (byte) 0x62, (byte) 0x2d, (byte) 0x65, (byte) 0x33, (byte) 0x65, (byte) 0x36, (byte) 0x2d,
      (byte) 0x34, (byte) 0x61, (byte) 0x33, (byte) 0x36, (byte) 0x2d, (byte) 0x39, (byte) 0x30,
      (byte) 0x34, (byte) 0x65, (byte) 0x2d, (byte) 0x61, (byte) 0x38, (byte) 0x30, (byte) 0x35,
      (byte) 0x63, (byte) 0x32, (byte) 0x38, (byte) 0x30, (byte) 0x38, (byte) 0x37, (byte) 0x66,
      (byte) 0x61, (byte) 0xc0, (byte) 0x0c, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x01,
      (byte) 0x00, (byte) 0x00, (byte) 0x0e, (byte) 0x10, (byte) 0x00, (byte) 0x2b, (byte) 0x2a,
      (byte) 0x61, (byte) 0x70, (byte) 0x70, (byte) 0x6c, (byte) 0x65, (byte) 0x2d, (byte) 0x64,
      (byte) 0x6f, (byte) 0x6d, (byte) 0x61, (byte) 0x69, (byte) 0x6e, (byte) 0x2d, (byte) 0x76,
      (byte) 0x65, (byte) 0x72, (byte) 0x69, (byte) 0x66, (byte) 0x69, (byte) 0x63, (byte) 0x61,
      (byte) 0x74, (byte) 0x69, (byte) 0x6f, (byte) 0x6e, (byte) 0x3d, (byte) 0x33, (byte) 0x30,
      (byte) 0x61, (byte) 0x66, (byte) 0x49, (byte) 0x42, (byte) 0x63, (byte) 0x76, (byte) 0x53,
      (byte) 0x75, (byte) 0x44, (byte) 0x56, (byte) 0x32, (byte) 0x50, (byte) 0x4c, (byte) 0x58,
      (byte) 0xc0, (byte) 0x0c, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x01, (byte) 0x00,
      (byte) 0x00, (byte) 0x0e, (byte) 0x10, (byte) 0x00, (byte) 0x45, (byte) 0x44, (byte) 0x67,
      (byte) 0x6f, (byte) 0x6f, (byte) 0x67, (byte) 0x6c, (byte) 0x65, (byte) 0x2d, (byte) 0x73,
      (byte) 0x69, (byte) 0x74, (byte) 0x65, (byte) 0x2d, (byte) 0x76, (byte) 0x65, (byte) 0x72,
      (byte) 0x69, (byte) 0x66, (byte) 0x69, (byte) 0x63, (byte) 0x61, (byte) 0x74, (byte) 0x69,
      (byte) 0x6f, (byte) 0x6e, (byte) 0x3d, (byte) 0x54, (byte) 0x56, (byte) 0x39, (byte) 0x2d,
      (byte) 0x44, (byte) 0x42, (byte) 0x65, (byte) 0x34, (byte) 0x52, (byte) 0x38, (byte) 0x30,
      (byte) 0x58, (byte) 0x34, (byte) 0x76, (byte) 0x30, (byte) 0x4d, (byte) 0x34, (byte) 0x55,
      (byte) 0x5f, (byte) 0x62, (byte) 0x64, (byte) 0x5f, (byte) 0x4a, (byte) 0x39, (byte) 0x63,
      (byte) 0x70, (byte) 0x4f, (byte) 0x4a, (byte) 0x4d, (byte) 0x30, (byte) 0x6e, (byte) 0x69,
      (byte) 0x6b, (byte) 0x66, (byte) 0x74, (byte) 0x30, (byte) 0x6a, (byte) 0x41, (byte) 0x67,
      (byte) 0x6a, (byte) 0x6d, (byte) 0x73, (byte) 0x51, (byte) 0xc0, (byte) 0x0c, (byte) 0x00,
      (byte) 0x10, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x0e, (byte) 0x10,
      (byte) 0x00, (byte) 0x5f, (byte) 0x5e, (byte) 0x61, (byte) 0x74, (byte) 0x6c, (byte) 0x61,
      (byte) 0x73, (byte) 0x73, (byte) 0x69, (byte) 0x61, (byte) 0x6e, (byte) 0x2d, (byte) 0x64,
      (byte) 0x6f, (byte) 0x6d, (byte) 0x61, (byte) 0x69, (byte) 0x6e, (byte) 0x2d, (byte) 0x76,
      (byte) 0x65, (byte) 0x72, (byte) 0x69, (byte) 0x66, (byte) 0x69, (byte) 0x63, (byte) 0x61,
      (byte) 0x74, (byte) 0x69, (byte) 0x6f, (byte) 0x6e, (byte) 0x3d, (byte) 0x35, (byte) 0x59,
      (byte) 0x6a, (byte) 0x54, (byte) 0x6d, (byte) 0x57, (byte) 0x6d, (byte) 0x6a, (byte) 0x49,
      (byte) 0x39, (byte) 0x32, (byte) 0x65, (byte) 0x77, (byte) 0x71, (byte) 0x6b, (byte) 0x78,
      (byte) 0x32, (byte) 0x6f, (byte) 0x58, (byte) 0x6d, (byte) 0x42, (byte) 0x61, (byte) 0x44,
      (byte) 0x36, (byte) 0x30, (byte) 0x54, (byte) 0x64, (byte) 0x39, (byte) 0x7a, (byte) 0x57,
      (byte) 0x6f, (byte) 0x6e, (byte) 0x39, (byte) 0x72, (byte) 0x36, (byte) 0x65, (byte) 0x61,
      (byte) 0x6b, (byte) 0x76, (byte) 0x48, (byte) 0x58, (byte) 0x36, (byte) 0x42, (byte) 0x37,
      (byte) 0x37, (byte) 0x7a, (byte) 0x7a, (byte) 0x6b, (byte) 0x46, (byte) 0x51, (byte) 0x74,
      (byte) 0x6f, (byte) 0x38, (byte) 0x50, (byte) 0x51, (byte) 0x39, (byte) 0x51, (byte) 0x73,
      (byte) 0x4b, (byte) 0x6e, (byte) 0x62, (byte) 0x66, (byte) 0x34, (byte) 0x49, (byte) 0xc0,
      (byte) 0x0c, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
      (byte) 0x0e, (byte) 0x10, (byte) 0x00, (byte) 0x45, (byte) 0x44, (byte) 0x67, (byte) 0x6f,
      (byte) 0x6f, (byte) 0x67, (byte) 0x6c, (byte) 0x65, (byte) 0x2d, (byte) 0x73, (byte) 0x69,
      (byte) 0x74, (byte) 0x65, (byte) 0x2d, (byte) 0x76, (byte) 0x65, (byte) 0x72, (byte) 0x69,
      (byte) 0x66, (byte) 0x69, (byte) 0x63, (byte) 0x61, (byte) 0x74, (byte) 0x69, (byte) 0x6f,
      (byte) 0x6e, (byte) 0x3d, (byte) 0x77, (byte) 0x44, (byte) 0x38, (byte) 0x4e, (byte) 0x37,
      (byte) 0x69, (byte) 0x31, (byte) 0x4a, (byte) 0x54, (byte) 0x4e, (byte) 0x54, (byte) 0x6b,
      (byte) 0x65, (byte) 0x7a, (byte) 0x4a, (byte) 0x34, (byte) 0x39, (byte) 0x73, (byte) 0x77,
      (byte) 0x76, (byte) 0x57, (byte) 0x57, (byte) 0x34, (byte) 0x38, (byte) 0x66, (byte) 0x38,
      (byte) 0x5f, (byte) 0x39, (byte) 0x78, (byte) 0x76, (byte) 0x65, (byte) 0x52, (byte) 0x45,
      (byte) 0x56, (byte) 0x34, (byte) 0x6f, (byte) 0x42, (byte) 0x2d, (byte) 0x30, (byte) 0x48,
      (byte) 0x66, (byte) 0x35, (byte) 0x6f, (byte) 0xc0, (byte) 0x0c, (byte) 0x00, (byte) 0x10,
      (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x0e, (byte) 0x10, (byte) 0x00,
      (byte) 0x2c, (byte) 0x2b, (byte) 0x4d, (byte) 0x53, (byte) 0x3d, (byte) 0x45, (byte) 0x34,
      (byte) 0x41, (byte) 0x36, (byte) 0x38, (byte) 0x42, (byte) 0x39, (byte) 0x41, (byte) 0x42,
      (byte) 0x32, (byte) 0x42, (byte) 0x42, (byte) 0x39, (byte) 0x36, (byte) 0x37, (byte) 0x30,
      (byte) 0x42, (byte) 0x43, (byte) 0x45, (byte) 0x31, (byte) 0x35, (byte) 0x34, (byte) 0x31,
      (byte) 0x32, (byte) 0x46, (byte) 0x36, (byte) 0x32, (byte) 0x39, (byte) 0x31, (byte) 0x36,
      (byte) 0x31, (byte) 0x36, (byte) 0x34, (byte) 0x43, (byte) 0x30, (byte) 0x42, (byte) 0x32,
      (byte) 0x30, (byte) 0x42, (byte) 0x42, (byte) 0xc0, (byte) 0x0c, (byte) 0x00, (byte) 0x10,
      (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x0e, (byte) 0x10, (byte) 0x00,
      (byte) 0x3c, (byte) 0x3b, (byte) 0x66, (byte) 0x61, (byte) 0x63, (byte) 0x65, (byte) 0x62,
      (byte) 0x6f, (byte) 0x6f, (byte) 0x6b, (byte) 0x2d, (byte) 0x64, (byte) 0x6f, (byte) 0x6d,
      (byte) 0x61, (byte) 0x69, (byte) 0x6e, (byte) 0x2d, (byte) 0x76, (byte) 0x65, (byte) 0x72,
      (byte) 0x69, (byte) 0x66, (byte) 0x69, (byte) 0x63, (byte) 0x61, (byte) 0x74, (byte) 0x69,
      (byte) 0x6f, (byte) 0x6e, (byte) 0x3d, (byte) 0x32, (byte) 0x32, (byte) 0x72, (byte) 0x6d,
      (byte) 0x35, (byte) 0x35, (byte) 0x31, (byte) 0x63, (byte) 0x75, (byte) 0x34, (byte) 0x6b,
      (byte) 0x30, (byte) 0x61, (byte) 0x62, (byte) 0x30, (byte) 0x62, (byte) 0x78, (byte) 0x73,
      (byte) 0x77, (byte) 0x35, (byte) 0x33, (byte) 0x36, (byte) 0x74, (byte) 0x6c, (byte) 0x64,
      (byte) 0x73, (byte) 0x34, (byte) 0x68, (byte) 0x39, (byte) 0x35, (byte) 0xc0, (byte) 0x0c,
      (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x0e,
      (byte) 0x10, (byte) 0x00, (byte) 0x2e, (byte) 0x2d, (byte) 0x64, (byte) 0x6f, (byte) 0x63,
      (byte) 0x75, (byte) 0x73, (byte) 0x69, (byte) 0x67, (byte) 0x6e, (byte) 0x3d, (byte) 0x31,
      (byte) 0x62, (byte) 0x30, (byte) 0x61, (byte) 0x36, (byte) 0x37, (byte) 0x35, (byte) 0x34,
      (byte) 0x2d, (byte) 0x34, (byte) 0x39, (byte) 0x62, (byte) 0x31, (byte) 0x2d, (byte) 0x34,
      (byte) 0x64, (byte) 0x62, (byte) 0x35, (byte) 0x2d, (byte) 0x38, (byte) 0x35, (byte) 0x34,
      (byte) 0x30, (byte) 0x2d, (byte) 0x64, (byte) 0x32, (byte) 0x63, (byte) 0x31, (byte) 0x32,
      (byte) 0x36, (byte) 0x36, (byte) 0x34, (byte) 0x62, (byte) 0x32, (byte) 0x38, (byte) 0x39,
      (byte) 0xc0, (byte) 0x0c, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x01, (byte) 0x00,
      (byte) 0x00, (byte) 0x0e, (byte) 0x10, (byte) 0x00, (byte) 0x2e, (byte) 0x2d, (byte) 0x64,
      (byte) 0x6f, (byte) 0x63, (byte) 0x75, (byte) 0x73, (byte) 0x69, (byte) 0x67, (byte) 0x6e,
      (byte) 0x3d, (byte) 0x30, (byte) 0x35, (byte) 0x39, (byte) 0x35, (byte) 0x38, (byte) 0x34,
      (byte) 0x38, (byte) 0x38, (byte) 0x2d, (byte) 0x34, (byte) 0x37, (byte) 0x35, (byte) 0x32,
      (byte) 0x2d, (byte) 0x34, (byte) 0x65, (byte) 0x66, (byte) 0x32, (byte) 0x2d, (byte) 0x39,
      (byte) 0x35, (byte) 0x65, (byte) 0x62, (byte) 0x2d, (byte) 0x61, (byte) 0x61, (byte) 0x37,
      (byte) 0x62, (byte) 0x61, (byte) 0x38, (byte) 0x61, (byte) 0x33, (byte) 0x62, (byte) 0x64,
      (byte) 0x30, (byte) 0x65, (byte) 0xc0, (byte) 0x0c, (byte) 0x00, (byte) 0x10, (byte) 0x00,
      (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x0e, (byte) 0x10, (byte) 0x00, (byte) 0x41,
      (byte) 0x40, (byte) 0x67, (byte) 0x6c, (byte) 0x6f, (byte) 0x62, (byte) 0x61, (byte) 0x6c,
      (byte) 0x73, (byte) 0x69, (byte) 0x67, (byte) 0x6e, (byte) 0x2d, (byte) 0x73, (byte) 0x6d,
      (byte) 0x69, (byte) 0x6d, (byte) 0x65, (byte) 0x2d, (byte) 0x64, (byte) 0x76, (byte) 0x3d,
      (byte) 0x43, (byte) 0x44, (byte) 0x59, (byte) 0x58, (byte) 0x2b, (byte) 0x58, (byte) 0x46,
      (byte) 0x48, (byte) 0x55, (byte) 0x77, (byte) 0x32, (byte) 0x77, (byte) 0x6d, (byte) 0x6c,
      (byte) 0x36, (byte) 0x2f, (byte) 0x47, (byte) 0x62, (byte) 0x38, (byte) 0x2b, (byte) 0x35,
      (byte) 0x39, (byte) 0x42, (byte) 0x73, (byte) 0x48, (byte) 0x33, (byte) 0x31, (byte) 0x4b,
      (byte) 0x7a, (byte) 0x55, (byte) 0x72, (byte) 0x36, (byte) 0x63, (byte) 0x31, (byte) 0x6c,
      (byte) 0x32, (byte) 0x42, (byte) 0x50, (byte) 0x76, (byte) 0x71, (byte) 0x4b, (byte) 0x58,
      (byte) 0x38, (byte) 0x3d
  };
}
