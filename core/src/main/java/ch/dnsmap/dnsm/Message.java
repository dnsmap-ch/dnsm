package ch.dnsmap.dnsm;

import ch.dnsmap.dnsm.record.ResourceRecord;

public final class Message {

    private Header header;
    private Question question;
    private ResourceRecord answer;
    private ResourceRecord authority;
    private ResourceRecord additional;
}
