package ch.dnsmap.dnsm;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Message identifier value object. Represents a 16-bit value as of RFC 1053 4.1.1. Header section
 * format.
 */
public final class HeaderId {

  private static final int MAX_VALUE = (int) (Math.pow(2, 16) - 1);
  private static final int MIN_VALUE = 0;

  private final int id;

  private HeaderId(int id) {
    if (id < MIN_VALUE || id > MAX_VALUE) {
      throw new IllegalArgumentException("invalid header ID value: " + id);
    }
    this.id = id;
  }

  /**
   * Create header ID with value zero.
   *
   * @return {@link HeaderId} with ID value 0
   */
  public static HeaderId ofZero() {
    return new HeaderId(0);
  }

  /**
   * Create header ID with specific 16-bit value.
   *
   * @param id 16-bit integer between 0 and 65535
   * @return {@link HeaderId} with ID value {@code id}
   */
  public static HeaderId of(int id) {
    return new HeaderId(id);
  }

  /**
   * Create header ID with random 16-bit value.
   *
   * @return {@link HeaderId} with random ID value
   */
  public static HeaderId ofRandom() {
    return new HeaderId(generateRandomId());
  }

  private static int generateRandomId() {
    return ThreadLocalRandom.current().nextInt(MIN_VALUE, MAX_VALUE + 1);
  }

  public int getId() {
    return id;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    var that = (HeaderId) obj;
    return this.id == that.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "HeaderId[id=" + id + ']';
  }
}
