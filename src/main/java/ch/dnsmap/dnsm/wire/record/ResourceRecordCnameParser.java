package ch.dnsmap.dnsm.wire.record;

import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.wire.ByteParser;
import ch.dnsmap.dnsm.wire.DomainParser;
import ch.dnsmap.dnsm.wire.ReadableByte;
import ch.dnsmap.dnsm.record.type.Cname;

public final class ResourceRecordCnameParser implements ByteParser<Cname> {

    private final DomainParser domainParser;

    public ResourceRecordCnameParser(DomainParser domainParser1) {
        this.domainParser = domainParser1;
    }

    @Override
    public Cname fromWire(ReadableByte wireData) {
        Domain domain = domainParser.fromWire(wireData);
        return new Cname(domain);
    }
}
