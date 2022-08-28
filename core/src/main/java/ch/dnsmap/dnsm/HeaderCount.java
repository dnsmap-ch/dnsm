package ch.dnsmap.dnsm;

/**
 * Message count value object for question-, anser-, name server and additional-count. Each value
 * represents a 16-bit value as of RFC 1053 4.1.1. Header section format.
 */
public final class HeaderCount {

  private static final int MAX_VALUE = (int) (Math.pow(2, 16) - 1);
  private static final int MIN_VALUE = 0;

  private final int qdCount;
  private final int anCount;
  private final int nsCount;
  private final int arCount;

  private HeaderCount(int qdCount, int anCount, int nsCount, int arCount) {
    if (qdCount < MIN_VALUE || qdCount > MAX_VALUE) {
      throw new IllegalArgumentException("invalid header question count value: " + qdCount);
    }
    if (anCount < MIN_VALUE || anCount > MAX_VALUE) {
      throw new IllegalArgumentException("invalid header answer count value: " + anCount);
    }
    if (nsCount < MIN_VALUE || nsCount > MAX_VALUE) {
      throw new IllegalArgumentException("invalid header name server count value: " + nsCount);
    }
    if (arCount < MIN_VALUE || arCount > MAX_VALUE) {
      throw new IllegalArgumentException(
          "invalid header additional record count value: " + arCount);
    }
    this.qdCount = qdCount;
    this.anCount = anCount;
    this.nsCount = nsCount;
    this.arCount = arCount;
  }

  /**
   * Create header fields for question, answer, name server and additional record counts.
   *
   * @param qdCount amount of questions, 16-bit integer between 0 and 65535
   * @param anCount amount of answers, 16-bit integer between 0 and 65535
   * @param nsCount amount of name servers, 16-bit integer between 0 and 65535
   * @param arCount amount of additional records, 16-bit integer between 0 and 65535
   * @return {@link HeaderCount} with count values
   */
  public static HeaderCount of(int qdCount, int anCount, int nsCount, int arCount) {
    return new HeaderCount(qdCount, anCount, nsCount, arCount);
  }

  public int getQdCount() {
    return qdCount;
  }

  public int getAnCount() {
    return anCount;
  }

  public int getNsCount() {
    return nsCount;
  }

  public int getArCount() {
    return arCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    HeaderCount that = (HeaderCount) o;

    if (qdCount != that.qdCount) {
      return false;
    }
    if (anCount != that.anCount) {
      return false;
    }
    if (nsCount != that.nsCount) {
      return false;
    }
    return arCount == that.arCount;
  }

  @Override
  public int hashCode() {
    int result = qdCount;
    result = 31 * result + anCount;
    result = 31 * result + nsCount;
    result = 31 * result + arCount;
    return result;
  }

  @Override
  public String toString() {
    return "HeaderCount{qdCount=" + qdCount
        + ", anCount=" + anCount
        + ", nsCount=" + nsCount
        + ", arCount=" + arCount + '}';
  }
}
