package ch.dnsmap.dnsm.wire.parser;


import ch.dnsmap.dnsm.Domain;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class DomainCompression {

  private final Map<Domain, Position> domainPositionMap;
  private final int offset;

  public DomainCompression() {
    this(0);
  }

  public DomainCompression(int offset) {
    this.domainPositionMap = new HashMap<>();
    this.offset = offset;
  }

  public void addDomain(Domain domain, int bytePosition) {
    if (domain.equals(Domain.root())) {
      return;
    }

    putDomain(domain, bytePosition + offset);
  }

  private void putDomain(Domain domain, int bytePosition) {
    if (domainPositionMap.containsKey(domain)) {
      return;
    }

    domainPositionMap.put(domain, new Position(bytePosition));
  }

  public Optional<Position> getPointer(Domain domain) {
    if (domainPositionMap.containsKey(domain)) {
      return Optional.of(domainPositionMap.get(domain));
    }
    return Optional.empty();
  }

  public record Position(int value) {

  }
}
