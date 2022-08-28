package ch.dnsmap.dnsm.header;

/**
 * DNS message opcode.
 */
public enum HeaderOpcode {

  /**
   * Standard query.
   */
  QUERY,

  /**
   * Inverse query.
   */
  IQUERY,

  /**
   * Server status request.
   */
  STATUS
}
