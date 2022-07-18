package ch.dnsmap.dnsm;

import static java.util.Arrays.stream;

public enum DnsQueryType {

    /**
     * RFC 1035 3.2.2 TYPE values
     */
    A(1),
    NS(2),
    MD(3),
    MF(4),
    CNAME(5),
    SOA(6),
    MB(7),
    MG(8),
    MR(9),
    NULL(10),
    WKS(11),
    PTR(12),
    HINFO(13),
    MINFO(14),
    MX(15),
    TXT(16),

    /**
     * RFC 1035 3.2.2 QTYPE values
     */
    AXFR(252),
    MAILB(253),
    MAILA(254),
    WILDCARD(255);

    private final int value;

    DnsQueryType(int value) {
        this.value = value;
    }

    public static DnsQueryType of(int dnsQueryTypeValue) {
        return stream(DnsQueryType.values()).filter(dnsQueryType -> dnsQueryType.value == dnsQueryTypeValue)
                .findFirst()
                .orElseThrow();
    }

    public String asText() {
        return this.name();
    }

    public int getValue() {
        return value;
    }
}
