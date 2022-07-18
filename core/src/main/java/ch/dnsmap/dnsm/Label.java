package ch.dnsmap.dnsm;

public record Label(String label) {

    public int length() {
        return label.length();
    }
}
