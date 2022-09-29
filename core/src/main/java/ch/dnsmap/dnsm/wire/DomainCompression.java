package ch.dnsmap.dnsm.wire;


import ch.dnsmap.dnsm.Domain;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class DomainCompression {

  private final Map<Domain, AbsolutePosition> domainPositionMap;

  public DomainCompression() {
    this.domainPositionMap = new HashMap<>();
  }

  public void addDomain(Domain domain, int startPosition) {
    if (domain.equals(Domain.root())) {
      return;
    }

    if (!domainPositionMap.containsKey(domain)) {
      AbsolutePosition absolutePosition = new AbsolutePosition(startPosition);

      domainPositionMap.put(domain, absolutePosition);
    }
  }

  public Optional<AbsolutePosition> getPointer(Domain domain) {
    if (domainPositionMap.containsKey(domain)) {
      return Optional.of(domainPositionMap.get(domain));
    }
    return Optional.empty();
  }

  public record AbsolutePosition(int position) {
  }
}
