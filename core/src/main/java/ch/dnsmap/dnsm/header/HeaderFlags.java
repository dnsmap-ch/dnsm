package ch.dnsmap.dnsm.header;

import static ch.dnsmap.dnsm.header.HeaderBitFlags.QR;

import java.util.Objects;
import java.util.Set;

/**
 * Header flags.
 */
public final class HeaderFlags {

  private final HeaderOpcode opcode;
  private final HeaderRcode rcode;
  private final Set<HeaderBitFlags> flags;

  /**
   * Create {@link HeaderFlags}.
   *
   * @param opcode {@link HeaderOpcode} of DNS message
   * @param rcode  {@link HeaderRcode} of DNS message
   * @param flags  {@link HeaderBitFlags} of DNS message if any
   */
  public HeaderFlags(HeaderOpcode opcode, HeaderRcode rcode, HeaderBitFlags... flags) {
    this.opcode = opcode;
    this.rcode = rcode;
    this.flags = Set.of(flags);
  }

  /**
   * Get DNS message Opcode.
   *
   * @return Opcode of DNS message
   */
  public HeaderOpcode getOpcode() {
    return opcode;
  }

  /**
   * Get DNS message return code.
   *
   * @return Return code of DNS message
   */
  public HeaderRcode getRcode() {
    return rcode;
  }

  /**
   * Get DNS message flags.
   *
   * @return Flags of DNS message
   */
  public Set<HeaderBitFlags> getFlags() {
    return flags;
  }

  /**
   * Whether this header is a DNS query or response header.
   *
   * @return true, if this is a query, false if it is a DNS response
   */
  public boolean isQuery() {
    return !flags.contains(QR);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    HeaderFlags that = (HeaderFlags) o;

    if (opcode != that.opcode) {
      return false;
    }
    if (rcode != that.rcode) {
      return false;
    }
    return Objects.equals(flags, that.flags);
  }

  @Override
  public int hashCode() {
    int result = opcode != null ? opcode.hashCode() : 0;
    result = 31 * result + (rcode != null ? rcode.hashCode() : 0);
    result = 31 * result + (flags != null ? flags.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "HeaderFlags{opcode=" + opcode + ", rcode=" + rcode + ", flags=" + flags + '}';
  }
}
