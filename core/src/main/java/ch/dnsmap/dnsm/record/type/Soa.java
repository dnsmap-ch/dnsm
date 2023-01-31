package ch.dnsmap.dnsm.record.type;

import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Uint32;

public record Soa(
    Domain mname,
    Domain rname,
    Uint32 serial,
    Uint32 refresh,
    Uint32 retry,
    Uint32 expire,
    Uint32 minimum
) {

}
