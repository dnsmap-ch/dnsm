package ch.dnsmap.dnsm.header;

/**
 * DNS header data.
 *
 * @param id    ID of the DNS message
 * @param flags DNS flags of this DNS message
 * @param count Number of query, anser, name server and additional data in this DNS message
 */
public record Header(HeaderId id, HeaderFlags flags, HeaderCount count) {

}
