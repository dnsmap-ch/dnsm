package ch.dnsmap.dnsm.wire;


import ch.dnsmap.dnsm.Domain;
import java.util.HashMap;
import java.util.Map;

public final class DomainCompression {

  private Map<Domain, Pointer> domainPositionMap;

  public DomainCompression() {
    this.domainPositionMap = new HashMap<>();
  }

  public void addDomain(Domain domain, int startPosition) {
    if (domain.equals(Domain.root())) {
      return;
    }

    if (!domainPositionMap.containsKey(domain)) {
      Pointer pointer = new Pointer(startPosition);

      domainPositionMap.put(domain, pointer);

      Domain parentDomain = domain.getDomainWithoutFirstLabel();
      startPosition += domain.getLabels().get(0).length() + 1;

      if (parentDomain.getLabelCount() > 0) {
        addDomain(parentDomain, startPosition);
      }
    }
  }

  public boolean contains(Domain domain) {
    return domainPositionMap.containsKey(domain);
  }

  public int getPointer(Domain domain) {
    if (contains(domain)) {
      Pointer pointer = domainPositionMap.get(domain);
      return createCompressionPointer(pointer.position());
    }
    return -1;
  }

  private static int createCompressionPointer(int pointerValue) {
    return pointerValue | 0xC000;
  }

  private record Pointer(int position) {
  }
}
