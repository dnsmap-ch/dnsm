package ch.dnsmap.dnsm;

/**
 * Represents a unsigned 32-bit integer value.
 */
public final class Uint32 {

  private static final long MAX_VALUE = (long) (Math.pow(2, 31) - 1);

  private final long value;

  private Uint32(long value) {
    if (value < 0 || value > MAX_VALUE) {
      throw new IllegalArgumentException("invalid unsigned integer value: " + value);
    }
    this.value = value;
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

  /**
   * Get 32-bit unsigned integer value.
   *
   * @return 32-bit unsigned integer as long
   */
  public long getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Uint32 uint = (Uint32) o;

    return value == uint.value;
  }

  @Override
  public int hashCode() {
    return (int) (value ^ (value >>> 32));
  }

  @Override
  public String toString() {
    return "Uint{value=" + value + '}';
  }
}
