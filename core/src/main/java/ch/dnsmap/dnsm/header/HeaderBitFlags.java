package ch.dnsmap.dnsm.header;

/**
 * One bit flags as of RFC 1035 4.1.1. Header section format.
 */
public enum HeaderBitFlags {

  /**
   * Whether this message is a query (0), or a response (1). If {Code QR} is set the message is a
   * response message.
   */
  QR,
  /**
   * Authoritative Answer - this bit is valid in responses, and specifies that the responding name
   * server is an authority for the domain name in question section.
   */
  AA,
  /**
   * TrunCation - specifies that this message was truncated due to length greater than that
   * permitted on the transmission channel.
   */
  TC,
  /**
   * Recursion Desired - this bit may be set in a query and is copied into the response.  If RD is
   * set, it directs the name server to pursue the query recursively. Recursive query support is
   * optional.
   */
  RD,
  /**
   * Recursion Available - this be is set or cleared in a response, and denotes whether recursive
   * query support is available in the name server.
   */
  RA
}
