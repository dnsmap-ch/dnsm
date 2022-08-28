package ch.dnsmap.dnsm;

public record Header(HeaderId id, byte[] flags, HeaderCount count) {
}
