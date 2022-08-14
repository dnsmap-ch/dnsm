package ch.dnsmap.dnsm;

/**
 * Time-to-live within DNS as specified in RFC 1035.
 */
public final class Ttl {

  /**
   * TTL must be a positive value of a singed 32-bit number, as specified in RFC 1035, 2.3.4. Size
   * limits.
   */
  public static final double MAX_VALUE = Math.pow(2, 31) - 1;
  private final long ttl;

  private Ttl(long ttl) {
    if (ttl < 0 || ttl > MAX_VALUE) {
      throw new IllegalArgumentException("invalid TTL value: " + ttl);
    }
    this.ttl = ttl;
  }

  /**
   * Create a {@link Ttl} from a long value.
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
    return ttl;
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

    return ttl == ttl1.ttl;
  }

  @Override
  public int hashCode() {
    return (int) (ttl ^ (ttl >>> 32));
  }

  @Override
  public String toString() {
    return "Ttl{ttl=" + ttl + '}';
  }
}
