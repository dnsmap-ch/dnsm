package ch.dnsmap.dnsm;

import static java.util.Arrays.stream;

public enum DnsType {

    UNKNOWN(0),

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
    TXT(16);

    private final int value;

    DnsType(int value) {
        this.value = value;
    }

    public static DnsType of(int dnsTypeValue) {
        return stream(DnsType.values()).filter(dnsType -> dnsType.value == dnsTypeValue)
                .findFirst()
                .orElse(UNKNOWN);
    }

    public String asText() {
        return this.name();
    }

    public int getValue() {
        return value;
    }
}
