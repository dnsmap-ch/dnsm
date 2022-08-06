package ch.dnsmap.dnsm;

public class Label {

  private final String label;

  private Label(String label) {
    this.label = label;
  }

  public static Label of(String label) {
    return new Label(label);
  }

  public static Label of(byte[] label) {
    return of(new String(label));
  }

  public String getLabel() {
    return label;
  }

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
    return label != null ? label.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Label{label='" + label + '\'' + '}';
  }
}
