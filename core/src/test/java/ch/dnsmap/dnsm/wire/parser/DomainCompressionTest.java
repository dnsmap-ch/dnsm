package ch.dnsmap.dnsm.wire.parser;

import static org.assertj.core.api.Assertions.assertThat;

import ch.dnsmap.dnsm.Domain;
import org.junit.jupiter.api.Test;

class DomainCompressionTest {

  private static final Domain DOMAIN_A = Domain.of("a.domain.example");
  private static final Domain DOMAIN_B = Domain.of("b.domain.example");
  private static final Domain DOMAIN_C = Domain.of("c.domain.example");

  DomainCompression domainCompression = new DomainCompression();

  @Test
  void testEmptyDomainCompression() {
    assertThat(domainCompression.getPointer(DOMAIN_A)).isEmpty();
    assertThat(domainCompression.getPointer(DOMAIN_B)).isEmpty();
    assertThat(domainCompression.getPointer(DOMAIN_C)).isEmpty();
  }

  @Test
  void testRootIsNotAddable() {
    domainCompression.addDomain(Domain.root(), 23);

    assertThat(domainCompression.getPointer(Domain.root())).isEmpty();
  }

  @Test
  void testNegativePosition() {
    domainCompression.addDomain(DOMAIN_A, -1);

    assertThat(domainCompression.getPointer(DOMAIN_A))
        .contains(new DomainCompression.Position(-1));
  }

  @Test
  void testUnableToOverwriteEntryWithANegativePosition() {
    domainCompression.addDomain(DOMAIN_A, 23);
    domainCompression.addDomain(DOMAIN_A, -1);

    assertThat(domainCompression.getPointer(DOMAIN_A))
        .contains(new DomainCompression.Position(23));
  }

  @Test
  void testInsertOneDomain() {
    domainCompression.addDomain(DOMAIN_A, 23);

    assertThat(domainCompression.getPointer(DOMAIN_A))
        .contains(new DomainCompression.Position(23));
    assertThat(domainCompression.getPointer(DOMAIN_B)).isEmpty();
    assertThat(domainCompression.getPointer(DOMAIN_C)).isEmpty();
  }

  @Test
  void testInsertTwoDomain() {
    domainCompression.addDomain(DOMAIN_A, 23);
    domainCompression.addDomain(DOMAIN_B, 42);

    assertThat(domainCompression.getPointer(DOMAIN_A))
        .contains(new DomainCompression.Position(23));
    assertThat(domainCompression.getPointer(DOMAIN_B))
        .contains(new DomainCompression.Position(42));
    assertThat(domainCompression.getPointer(DOMAIN_C)).isEmpty();
  }

  @Test
  void testInsertTwoDomainWithOffset() {
    DomainCompression domainCompression = new DomainCompression(2);

    domainCompression.addDomain(DOMAIN_A, 23);
    domainCompression.addDomain(DOMAIN_B, 42);

    assertThat(domainCompression.getPointer(DOMAIN_A))
        .contains(new DomainCompression.Position(25));
    assertThat(domainCompression.getPointer(DOMAIN_B))
        .contains(new DomainCompression.Position(44));
    assertThat(domainCompression.getPointer(DOMAIN_C)).isEmpty();
  }
}
