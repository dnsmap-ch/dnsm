package ch.dnsmap.dnsm.record.type;

public record Txt(String txt) {

  public int getLength() {
    return txt.length();
  }
}
