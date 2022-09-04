package ch.dnsmap.dnsm;

import java.util.Objects;

/**
 * Time-to-live within DNS as specified in RFC 1035.
 */
public final class Ttl {

  private final Uint32 ttl;

  private Ttl(long ttl) {
    try {
      this.ttl = Uint32.of(ttl);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("invalid TTL value", e);
    }
  }

  /**
   * Create a {@link Ttl} from a long value. TTL must be a positive value of a singed 32-bit number,
   * as specified in RFC 1035, 2.3.4. Size limits.
   *
   * @param ttl value to make a TTL of
   * @return new {@link Ttl}
   * @throws IllegalArgumentException if {@code ttl} does not fulfill RFC 1035 requirements
   */
  public static Ttl of(long ttl) {
    return new Ttl(ttl);
  }

  /**
   * Get TTL as long.
   *
   * @return TTL as long
   */
  public long getTtl() {
    return ttl.getValue();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Ttl ttl1 = (Ttl) o;

    return Objects.equals(ttl, ttl1.ttl);
  }

  @Override
  public int hashCode() {
    return ttl != null ? ttl.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Ttl{ttl=" + ttl.getValue() + '}';
  }
}
