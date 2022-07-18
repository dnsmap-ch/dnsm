package ch.dnsmap.dnsm;

public record Header(int id, byte[] flags, int qdCount, int anCount, int nsCount, int arCount) {
}
