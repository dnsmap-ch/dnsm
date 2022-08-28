package ch.dnsmap.dnsm;

public record Header(HeaderId id, byte[] flags, int qdCount, int anCount, int nsCount,
                     int arCount) {
}
