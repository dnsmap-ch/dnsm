package ch.dnsmap.dnsm;

import static java.util.Arrays.stream;

public enum DnsQueryClass {

    /**
     * RFC 1035 3.2.4 CLASS values
     */
    IN(1),
    CS(2),
    CH(3),
    HS(4),
    /**
     * RFC 1035 3.2.4 QCLASS values
     */
    WILDCARD(255);

    private final int value;

    DnsQueryClass(int value) {
        this.value = value;
    }

    public static DnsQueryClass of(int dnsQueryClassValue) {
        return stream(DnsQueryClass.values()).filter(dnsQueryClass -> dnsQueryClass.value == dnsQueryClassValue)
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
