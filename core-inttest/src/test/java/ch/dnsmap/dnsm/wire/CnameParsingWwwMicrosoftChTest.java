package ch.dnsmap.dnsm.wire;

import ch.dnsmap.dnsm.*;
import ch.dnsmap.dnsm.record.ResourceRecord;
import ch.dnsmap.dnsm.record.ResourceRecordA;
import ch.dnsmap.dnsm.record.ResourceRecordCname;
import ch.dnsmap.dnsm.record.ResourceRecordOpaque;
import ch.dnsmap.dnsm.record.type.Cname;
import ch.dnsmap.dnsm.record.type.Ip4;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ch.dnsmap.dnsm.DnsClass.IN;
import static ch.dnsmap.dnsm.DnsType.A;
import static ch.dnsmap.dnsm.DnsType.CNAME;
import static org.assertj.core.api.Assertions.assertThat;

class CnameParsingWwwMicrosoftChTest {

    private static final byte[] CNAME_BYTES_HEADER = new byte[]{
            (byte) 0x9a, (byte) 0xb0, (byte) 0x81, (byte) 0x80, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x06,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01
    };
    private static final byte[] CNAME_BYTES_QUESTION = new byte[]{
            (byte) 0x03, (byte) 0x77, (byte) 0x77, (byte) 0x77, (byte) 0x09, (byte) 0x6d, (byte) 0x69, (byte) 0x63,
            (byte) 0x72, (byte) 0x6f, (byte) 0x73, (byte) 0x6f, (byte) 0x66, (byte) 0x74, (byte) 0x02, (byte) 0x63,
            (byte) 0x68, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01
    };
    private static final byte[] CNAME_BYTES_ANSWER = new byte[]{
            (byte) 0xc0, (byte) 0x0c, (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
            (byte) 0x0e, (byte) 0x10, (byte) 0x00, (byte) 0x02, (byte) 0xc0, (byte) 0x10, (byte) 0xc0, (byte) 0x10,
            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x0e, (byte) 0x10,
            (byte) 0x00, (byte) 0x04, (byte) 0x14, (byte) 0x67, (byte) 0x55, (byte) 0x21, (byte) 0xc0, (byte) 0x10,
            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x0e, (byte) 0x10,
            (byte) 0x00, (byte) 0x04, (byte) 0x14, (byte) 0x70, (byte) 0x34, (byte) 0x1d, (byte) 0xc0, (byte) 0x10,
            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x0e, (byte) 0x10,
            (byte) 0x00, (byte) 0x04, (byte) 0x14, (byte) 0x35, (byte) 0xcb, (byte) 0x32, (byte) 0xc0, (byte) 0x10,
            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x0e, (byte) 0x10,
            (byte) 0x00, (byte) 0x04, (byte) 0x14, (byte) 0x51, (byte) 0x6f, (byte) 0x55, (byte) 0xc0, (byte) 0x10,
            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x0e, (byte) 0x10,
            (byte) 0x00, (byte) 0x04, (byte) 0x14, (byte) 0x54, (byte) 0xb5, (byte) 0x3e
    };
    private static final byte[] CNAME_BYTES_ADDITIONAL = new byte[]{
            (byte) 0x00, (byte) 0x00, (byte) 0x29, (byte) 0x04, (byte) 0xd0, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    private static final int MESSAGE_ID = 39600;
    private static final byte[] FLAGS = {(byte) 0x81, (byte) 0x80};
    private static final String ROOT = " ";
    private static final String HOST_NAME = "www.microsoft.ch.";
    private static final String DOMAIN_NAME = "microsoft.ch.";
    private static final Label LABEL_WWW = new Label("www");
    private static final Label LABEL_MICROSOFT = new Label("microsoft");
    private static final Label LABEL_CH = new Label("ch");
    private static final Domain HOST = Domain.of(LABEL_WWW, LABEL_MICROSOFT, LABEL_CH);
    private static final Domain DOMAIN = Domain.of(LABEL_MICROSOFT, LABEL_CH);
    private static final int TTL = 3600;

    @Test
    void testCnameInputParsing() throws IOException {
        var cnameBytes = new ByteArrayOutputStream();
        cnameBytes.write(CNAME_BYTES_HEADER);
        cnameBytes.write(CNAME_BYTES_QUESTION);
        cnameBytes.write(CNAME_BYTES_ANSWER);
        cnameBytes.write(CNAME_BYTES_ADDITIONAL);

        var dnsInput = DnsInput.fromWire(cnameBytes.toByteArray());

        assertThat(dnsInput.getHeader()).satisfies(header -> {
            assertThat(header.id()).isEqualTo(MESSAGE_ID);
            assertThat(header.flags()).isEqualTo(FLAGS);
            assertThat(header.qdCount()).isEqualTo(1);
            assertThat(header.anCount()).isEqualTo(6);
            assertThat(header.nsCount()).isEqualTo(0);
            assertThat(header.arCount()).isEqualTo(1);
        });

        assertThat(dnsInput.getQuestion().size()).isEqualTo(1);
        assertThat(dnsInput.getQuestion().get(0)).satisfies(question -> {
            assertThat(question.questionName().getCanonical()).isEqualTo(HOST_NAME);
            assertThat(question.questionType().asText()).isEqualTo("A");
            assertThat(question.questionClass().asText()).isEqualTo("IN");
        });

        assertThat(dnsInput.getAnswer().size()).isEqualTo(6);
        assertThat(dnsInput.getAnswer().get(0)).satisfies(answer -> {
            assertThat(answer.getName().getCanonical()).isEqualTo(HOST_NAME);
            assertThat(answer.getDnsType()).isEqualTo(CNAME);
            assertThat(answer.getDnsClass()).isEqualTo(IN);
            assertThat(answer.getTtl()).isEqualTo(TTL);
            assertThat(((ResourceRecordCname) answer).getCname().cname().getCanonical()).isEqualTo(DOMAIN_NAME);
        });
        assertThat(dnsInput.getAnswer().get(1)).satisfies(answer -> {
            assertThat(answer.getName().getCanonical()).isEqualTo(DOMAIN_NAME);
            assertThat(answer.getDnsType()).isEqualTo(A);
            assertThat(answer.getDnsClass()).isEqualTo(IN);
            assertThat(answer.getTtl()).isEqualTo(TTL);
            assertThat(((ResourceRecordA) answer).getIp4().getIp().getHostAddress()).isEqualTo("20.103.85.33");
        });
        assertThat(dnsInput.getAnswer().get(2)).satisfies(answer -> {
            assertThat(answer.getName().getCanonical()).isEqualTo(DOMAIN_NAME);
            assertThat(answer.getDnsType()).isEqualTo(A);
            assertThat(answer.getDnsClass()).isEqualTo(IN);
            assertThat(answer.getTtl()).isEqualTo(TTL);
            assertThat(((ResourceRecordA) answer).getIp4().getIp().getHostAddress()).isEqualTo("20.112.52.29");
        });
        assertThat(dnsInput.getAnswer().get(3)).satisfies(answer -> {
            assertThat(answer.getName().getCanonical()).isEqualTo(DOMAIN_NAME);
            assertThat(answer.getDnsType()).isEqualTo(A);
            assertThat(answer.getDnsClass()).isEqualTo(IN);
            assertThat(answer.getTtl()).isEqualTo(TTL);
            assertThat(((ResourceRecordA) answer).getIp4().getIp().getHostAddress()).isEqualTo("20.53.203.50");
        });
        assertThat(dnsInput.getAnswer().get(4)).satisfies(answer -> {
            assertThat(answer.getName().getCanonical()).isEqualTo(DOMAIN_NAME);
            assertThat(answer.getDnsType()).isEqualTo(A);
            assertThat(answer.getDnsClass()).isEqualTo(IN);
            assertThat(answer.getTtl()).isEqualTo(TTL);
            assertThat(((ResourceRecordA) answer).getIp4().getIp().getHostAddress()).isEqualTo("20.81.111.85");
        });
        assertThat(dnsInput.getAnswer().get(5)).satisfies(answer -> {
            assertThat(answer.getName().getCanonical()).isEqualTo(DOMAIN_NAME);
            assertThat(answer.getDnsType()).isEqualTo(A);
            assertThat(answer.getDnsClass()).isEqualTo(IN);
            assertThat(answer.getTtl()).isEqualTo(TTL);
            assertThat(((ResourceRecordA) answer).getIp4().getIp().getHostAddress()).isEqualTo("20.84.181.62");
        });

        assertThat(dnsInput.getAuthority().size()).isEqualTo(0);

        assertThat(dnsInput.getAdditional().size()).isEqualTo(1);
        assertThat(dnsInput.getAdditional().get(0)).satisfies(answer -> {
            assertThat(answer.getName().getCanonical()).isEqualTo(ROOT);
            assertThat(answer.getDnsType()).isEqualTo(DnsType.UNKNOWN);
            assertThat(answer.getDnsClass()).isEqualTo(DnsClass.UNKNOWN);
            assertThat(answer.getTtl()).isEqualTo(0L);
            assertThat(((ResourceRecordOpaque) answer).getOpaqueData().opaque()).isEqualTo(new byte[0]);
        });
    }


    @Test
    void testCnameOutputParsing() {
        var header = composeHeader();
        var question = composeQuestion();
        var answer = composeAnswer();

        var dnsOutput = DnsOutput.toWire(header, question, answer);

        assertThat(dnsOutput.getHeader()).isEqualTo(CNAME_BYTES_HEADER);
        assertThat(dnsOutput.getQuestion()).isEqualTo(CNAME_BYTES_QUESTION);
        assertThat(dnsOutput.getAnswers()).isEqualTo(CNAME_BYTES_ANSWER);
    }

    private static Header composeHeader() {
        return new Header(MESSAGE_ID, FLAGS, 1, 6, 0, 1);
    }

    private static Question composeQuestion() {
        return new Question(HOST, DnsQueryType.A, DnsQueryClass.IN);
    }

    private static List<ResourceRecord> composeAnswer() {
        List<ResourceRecord> answers = new ArrayList<>(6);
        answers.add(new ResourceRecordCname(Domain.of(LABEL_WWW, LABEL_MICROSOFT, LABEL_CH), CNAME, IN, TTL, 2, new Cname(Domain.of(LABEL_MICROSOFT, LABEL_CH))));
        answers.add(new ResourceRecordA(DOMAIN, A, IN, TTL, Ip4.of("20.103.85.33")));
        answers.add(new ResourceRecordA(DOMAIN, A, IN, TTL, Ip4.of("20.112.52.29")));
        answers.add(new ResourceRecordA(DOMAIN, A, IN, TTL, Ip4.of("20.53.203.50")));
        answers.add(new ResourceRecordA(DOMAIN, A, IN, TTL, Ip4.of("20.81.111.85")));
        answers.add(new ResourceRecordA(DOMAIN, A, IN, TTL, Ip4.of("20.84.181.62")));
        return answers;
    }
}
