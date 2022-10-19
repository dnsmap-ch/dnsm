package ch.dnsmap.dnsm;

/**
 * Represents a unsigned 32-bit integer value.
 */
public record Uint32(long value) {

  private static final long MAX_VALUE = (long) (Math.pow(2, 31) - 1);


  public Uint32 {
    if (value < 0 || value > MAX_VALUE) {
      throw new IllegalArgumentException("invalid unsigned integer value: " + value);
    }
  }

  /**
   * Create a {@link Uint32} from a long value.
   *
   * @param value value to make a Uint of
   * @return new {@link Uint32}
   */
  public static Uint32 of(long value) {
    return new Uint32(value);
  }
}
