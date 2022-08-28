package ch.dnsmap.dnsm.header;

/**
 * Response code - this 4 bit field is set as part of responses.  The values have the following
 * interpretation.
 */
public enum HeaderRcode {

  /**
   * No error condition.
   */
  NO_ERROR,

  /**
   * The name server was unable to interpret the query.
   */
  FORMAT_ERROR,

  /**
   * The name server was unable to process this query due to a problem with the name server.
   */
  SERVER_FAILURE,

  /**
   * Meaningful only for responses from an authoritative name server, this code signifies that the
   * domain name referenced in the query does not exist.
   */
  NAME_ERROR,

  /**
   * The name server does not support the requested kind of query.
   */
  NOT_IMPLEMENTED,

  /**
   * The name server refuses to perform the specified operation for policy reasons.  For example, a
   * name server may not wish to provide the information to the particular requester, or a name
   * server may not wish to perform a particular operation (e.g., zone transfer) for particular
   * data.
   */
  REFUSED
}
