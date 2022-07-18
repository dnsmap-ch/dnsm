package ch.dnsmap.dnsm;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

public final class Domain {

    private final List<Label> labels;

    private Domain() {
        labels = new ArrayList<>();
    }

    public static Domain empty() {
        return new Domain();
    }

    public static Domain of(Label label) {
        Domain domain = new Domain();
        domain.labels.add(label);
        return domain;
    }

    public static Domain of(Domain domain, Label label) {
        Domain newDomain = new Domain();
        newDomain.labels.addAll(domain.getLabels());
        newDomain.labels.add(label);
        return newDomain;
    }

    public static Domain of(Label... labels) {
        Domain newDomain = new Domain();
        newDomain.labels.addAll(asList(labels));
        return newDomain;
    }

    public static Domain of(List<Label> labels) {
        return of(labels.toArray(new Label[0]));
    }

    public static Domain root() {
        return new Domain();
    }

    public List<Label> getLabels() {
        return labels.stream().toList();
    }

    public String getCanonical() {
        if (labels.isEmpty()) {
            return " ";
        }
        return labels.stream()
                .map(Label::label)
                .map(String::toLowerCase)
                .collect(joining(".", "", "."));
    }

    public int getLabelCount() {
        return labels.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Domain domain = (Domain) o;

        return labels != null ? labels.equals(domain.labels) : domain.labels == null;
    }

    @Override
    public int hashCode() {
        return labels != null ? labels.hashCode() : 0;
    }
}
