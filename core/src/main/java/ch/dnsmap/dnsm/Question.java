package ch.dnsmap.dnsm;

public record Question(Domain questionName, DnsQueryType questionType, DnsQueryClass questionClass) {
}
