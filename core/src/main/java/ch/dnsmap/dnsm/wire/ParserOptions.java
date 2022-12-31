package ch.dnsmap.dnsm.wire;

import java.util.ArrayList;
import java.util.List;

public record ParserOptions(boolean isTcp, boolean isDomainLabelTolerant, List<ParsingLog> log) {

  public void log(ParsingLog log) {
    this.log.add(log);
  }

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
      return new ParserOptions(isTcp, isDomainLabelTolerant, new ArrayList<>());
    }
  }
}
