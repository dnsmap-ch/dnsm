package ch.dnsmap.dnsm.record.type;

import ch.dnsmap.dnsm.Domain;
import java.util.Objects;

/**
 * MX specific properties.
 */
public final class Mx {

  private final Preference preference;
  private final Domain exchange;

  private Mx(Preference preference, Domain exchange) {
    this.preference = preference;
    this.exchange = exchange;
  }

  /**
   * Create a {@link Mx} from {@link Preference} and {@link Domain}.
   *
   * @param preference preference of this MX entry
   * @param exchange   name of this MX entry
   * @return new {@link Mx}
   * @throws IllegalArgumentException if {@link Preference} does not fulfill RFC 1035 requirements
   */
  public static Mx of(int preference, Domain exchange) {
    return new Mx(Preference.of(preference), exchange);
  }

  /**
   * Get Preference.
   *
   * @return Preference of this MX
   */
  public Preference getPreference() {
    return preference;
  }

  /**
   * Get Name.
   *
   * @return Name of this MX
   */
  public Domain getExchange() {
    return exchange;
  }

  public record Preference(int value) {

    private static final double MAX_VALUE = Math.pow(2, 16) - 1;

    public static Preference of(int value) {
      if (value < 0 || value > MAX_VALUE) {
        throw new IllegalArgumentException("invalid preference value: " + value);
      }
      return new Preference(value);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Mx mx = (Mx) o;

    if (!Objects.equals(preference, mx.preference)) {
      return false;
    }
    return Objects.equals(exchange, mx.exchange);
  }

  @Override
  public int hashCode() {
    int result = preference != null ? preference.hashCode() : 0;
    result = 31 * result + (exchange != null ? exchange.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Mx{preference=" + preference + ", exchange=" + exchange + '}';
  }
}
