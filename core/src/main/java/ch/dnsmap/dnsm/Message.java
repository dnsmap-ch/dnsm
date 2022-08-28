package ch.dnsmap.dnsm;

import ch.dnsmap.dnsm.header.Header;
import ch.dnsmap.dnsm.record.ResourceRecord;

public record Message(
    Header header,
    Question question,
    ResourceRecord answer,
    ResourceRecord authority,
    ResourceRecord additional
) {
}
