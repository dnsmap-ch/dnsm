package ch.dnsmap.dnsm;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Domain name consists of an empty or multiple labels.
 */
public final class Domain {

  /**
   * Domain name must be 255 characters or less, as specified in RFC 1035, 2.3.4. Size limits.
   */
  private static final int MAX_LENGTH = 255;

  private final List<Label> labels;

  private Domain(Label... labels) {
    Objects.requireNonNull(labels, "labels must not be null");

    int totalLength = stream(labels).map(label -> label.length() + 1).reduce(0, Integer::sum);
    if (totalLength > MAX_LENGTH) {
      throw new IllegalArgumentException("domain name exceeds max length of " + MAX_LENGTH);
    }

    this.labels = List.of(labels);
  }

  /**
   * Root domain containing 0 labels.
   *
   * @return domain without any label
   */
  public static Domain root() {
    return new Domain();
  }

  /**
   * Domain of a single label.
   *
   * @param label label to create a domain of
   * @return domain containing single label
   * @throws IllegalArgumentException if {@code label} does not fulfill RFC 1035 requirements
   * @throws NullPointerException     if {@code label} is null
   */
  public static Domain of(Label label) {
    return new Domain(label);
  }

  /**
   * Domain of multiple labels.
   *
   * @param labels labels to create a domain of
   * @return domain of the labels
   * @throws IllegalArgumentException if {@code labels} does not fulfill RFC 1035 requirements
   * @throws NullPointerException     if {@code labels} is null
   */
  public static Domain of(Label... labels) {
    return new Domain(labels);
  }

  /**
   * Domain of list of labels.
   *
   * @param labels labels to create a domain of
   * @return domain of the labels
   * @throws IllegalArgumentException if {@code labels} does not fulfill RFC 1035 requirements
   * @throws NullPointerException     if {@code labels} is null
   */
  public static Domain of(List<Label> labels) {
    Objects.requireNonNull(labels, "labels must not be null");
    return of(labels.toArray(new Label[0]));
  }

  /**
   * Append label to existing domain.
   *
   * @param domain domain before label
   * @param label  label after domain
   * @return domain with appended label
   * @throws IllegalArgumentException if {@code label} or {@code domain} does not fulfill RFC 1035
   *                                  requirements
   * @throws NullPointerException     if {@code label} or {@code domain} is null
   */
  public static Domain of(Domain domain, Label label) {
    Objects.requireNonNull(label, "label must not be null");
    Objects.requireNonNull(domain, "domain name must not be null");

    List<Label> labels = new ArrayList<>(domain.getLabels());
    labels.add(label);
    return of(labels);
  }

  /**
   * Append existing domain to label.
   *
   * @param label  label before domain
   * @param domain domain after label
   * @return domain with prepended label
   * @throws IllegalArgumentException if {@code label} or {@code domain}  does not fulfill RFC 1035
   *                                  requirements
   * @throws NullPointerException     if {@code label} or {@code domain} is null
   */
  public static Domain of(Label label, Domain domain) {
    Objects.requireNonNull(label, "label must not be null");
    Objects.requireNonNull(domain, "domain name must not be null");

    List<Label> labels = new ArrayList<>(1 + domain.getLabelCount());
    labels.add(label);
    labels.addAll(domain.getLabels());
    return of(labels);
  }

  /**
   * Domain of a string of labels.
   *
   * @param domainString domain name in form of a string
   * @return domain of the string labels
   * @throws IllegalArgumentException if {@code domainString } does not fulfill RFC 1035
   *                                  requirements
   * @throws NullPointerException     if {@code domainString} is null
   */
  public static Domain of(String domainString) {
    Objects.requireNonNull(domainString, "domain string must not be null");

    List<Label> labels = stream(domainString.split("\\.")).map(Label::of).toList();
    return of(labels);
  }

  /**
   * Get immutable list of all labels in this domain.
   *
   * @return unmodifiable list of labels
   */
  public List<Label> getLabels() {
    return labels.stream().toList();
  }

  /**
   * Get first label of domain.
   *
   * @return label at the start of domain
   */
  public Label getFirstLabel() {
    return labels.get(0);
  }

  /**
   * Get last label of domain.
   *
   * @return label at the end of domain
   */
  public Label getLastLabel() {
    return labels.get(getLabelCount() - 1);
  }

  /**
   * Get domain without first label.
   *
   * @return domain minus the first label
   */
  public Domain getDomainWithoutFirstLabel() {
    List<Label> withoutFirst = this.labels.stream().skip(1).toList();
    return of(withoutFirst);
  }

  /**
   * Get domain without last label.
   *
   * @return domain minus the last label
   */
  public Domain getDomainWithoutLastLabel() {
    List<Label> withoutLast = range(0, getLabelCount() - 1).mapToObj(labels::get).toList();
    return of(withoutLast);
  }

  /**
   * Cannibalised representation of the domain. Labels are joined by a single dot including ending
   * dot in lower case.
   *
   * @return canonical representation of this domain
   */
  public String getCanonical() {
    if (labels.isEmpty()) {
      return " ";
    }
    return labels.stream()
        .map(Label::getLabel)
        .map(String::toLowerCase)
        .collect(joining(".", "", "."));
  }

  /**
   * Amount of labels within this domain.
   *
   * @return number of labels in domain
   */
  public int getLabelCount() {
    return labels.size();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Domain domain = (Domain) o;

    return Objects.equals(labels, domain.labels);
  }

  @Override
  public int hashCode() {
    return labels != null ? labels.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Domain{labels=" + labels + '}';
  }
}
