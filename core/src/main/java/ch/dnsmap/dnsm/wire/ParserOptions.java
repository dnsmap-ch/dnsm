package ch.dnsmap.dnsm.wire;

public record ParserOptions(boolean isTcp, boolean isDomainLabelTolerant) {

  public static class Builder {

    private boolean isTcp = false;
    private boolean isDomainLabelTolerant = false;

    public static Builder builder() {
      return new Builder();
    }

    public Builder setTcp() {
      isTcp = true;
      return this;
    }

    public Builder unsetTcp() {
      isTcp = false;
      return this;
    }

    public Builder setDomainLabelTolerant() {
      isDomainLabelTolerant = true;
      return this;
    }

    public Builder unsetDomainLabelTolerant() {
      isDomainLabelTolerant = false;
      return this;
    }

    public ParserOptions build() {
      return new ParserOptions(isTcp, isDomainLabelTolerant);
    }
  }
}
