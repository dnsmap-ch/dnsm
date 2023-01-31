package ch.dnsmap.dnsm;

import ch.dnsmap.dnsm.header.Header;
import ch.dnsmap.dnsm.record.ResourceRecord;
import java.util.List;

public record Message(
    Header header,
    Question question,
    List<ResourceRecord> answer,
    List<ResourceRecord> authority,
    List<ResourceRecord> additional
) {

}
