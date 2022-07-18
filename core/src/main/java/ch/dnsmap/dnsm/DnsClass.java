package ch.dnsmap.dnsm;

import static java.util.Arrays.stream;

public enum DnsClass {

    UNKNOWN(0),

    /**
     * RFC 1035 3.2.4 CLASS values
     */
    IN(1),
    CS(2),
    CH(3),
    HS(4);

    private final int value;

    DnsClass(int value) {
        this.value = value;
    }

    public static DnsClass of(int dnsClassValue) {
        return stream(DnsClass.values()).filter(dnsClass -> dnsClass.value == dnsClassValue)
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
