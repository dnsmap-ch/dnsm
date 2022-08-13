package ch.dnsmap.dnsm;

import java.util.Locale;
import java.util.Objects;

/**
 * Smallest valid part of a domain name.
 */
public class Label {

  private static final int MIN_LENGTH = 1;

  /**
   * Labels must be 63 characters or less, as specified in RFC 1035, 2.3.4. Size limits.
   */
  private static final int MAX_LENGTH = 63;
  private static final char DASH = '-';

  private final String label;

  private Label(String label) {
    Objects.requireNonNull(label, "label must not be null");

    if (label.length() < MIN_LENGTH) {
      throw new IllegalArgumentException("label fail to meet min length of " + MIN_LENGTH);
    }

    if (label.length() > MAX_LENGTH) {
      throw new IllegalArgumentException("label exceeds max length of " + MAX_LENGTH);
    }

    if (!isAlphaCharacter(label.charAt(0))) {
      throw new IllegalArgumentException("label must start with alpha character");
    }

    if (!label.chars().allMatch(c -> isValidCharacter((char) c))) {
      throw new IllegalArgumentException("label contains invalid character");
    }

    this.label = label;
  }

  /**
   * Create a {@link Label} from a string.
   *
   * @param label string to make a label of
   * @return new {@link Label} of {@code label}
   * @throws IllegalArgumentException if {@code label} does not fulfill RFC 1035 requirements
   * @throws NullPointerException     if {@code label} is null
   */
  public static Label of(String label) {
    return new Label(label);
  }

  /**
   * Create a {@link Label} from a byte array.
   *
   * @param label byte array to make a label of
   * @return new {@link Label} of {@code label}
   * @throws IllegalArgumentException if {@code label} does not fulfill RFC 1035 requirements
   * @throws NullPointerException     if {@code label} is null
   */
  public static Label of(byte[] label) {
    return of(new String(label));
  }

  /**
   * Get label as string.
   *
   * @return label as string
   */
  public String getLabel() {
    return label;
  }

  /**
   * Get length of label.
   *
   * @return length of label
   */
  public int length() {
    return label.length();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Label label1 = (Label) o;

    return label != null ? label.equalsIgnoreCase(label1.label) : label1.label == null;
  }

  @Override
  public int hashCode() {
    return label != null ? label.toLowerCase(Locale.ROOT).hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Label{label='" + label + '\'' + '}';
  }

  private static boolean isAlphaCharacter(char character) {
    if (character >= 'A' && character <= 'Z') {
      return true;
    }
    return character >= 'a' && character <= 'z';
  }

  private static boolean isValidCharacter(char character) {
    if (character == DASH) {
      return true;
    }
    if (character >= '0' && character <= '9') {
      return true;
    }
    return isAlphaCharacter(character);
  }
}
