package ch.dnsmap.dnsm;

import static ch.dnsmap.dnsm.Domain.root;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DomainTest {

  private static final Label LABEL_1 = new Label("Baz");
  private static final Label LABEL_2 = new Label("bAr");
  private static final Label LABEL_3 = new Label("foO");
  private static final Domain DOMAIN_1 = Domain.of(LABEL_2);
  private static final Domain DOMAIN_2 = Domain.of(DOMAIN_1, LABEL_3);

  @Test
  void testCanonicalOnEmpty() {
    var domain = root();
    assertThat(domain.getCanonical()).isEqualTo(" ");
  }

  @Test
  void testCanonicalOnSingleLabel() {
    var domain = Domain.of(LABEL_1);
    assertThat(domain.getCanonical()).isEqualTo("baz.");
  }

  @Test
  void testCanonicalOnDomainAndLabel() {
    var domain = Domain.of(DOMAIN_1, LABEL_1);
    assertThat(domain.getCanonical()).isEqualTo("bar.baz.");
  }

  @Test
  void testCanonicalOnLabelAndDomain() {
    var domain = Domain.of(LABEL_1, DOMAIN_1);
    assertThat(domain.getCanonical()).isEqualTo("baz.bar.");
  }

  @Test
  void testCanonicalOnLongerDomainAndLabel() {
    var domain = Domain.of(DOMAIN_2, LABEL_1);
    assertThat(domain.getCanonical()).isEqualTo("bar.foo.baz.");
  }

  @Test
  void testCanonicalOnStringDomain() {
    var domain = Domain.of("bar.foo.baz.");
    assertThat(domain.getCanonical()).isEqualTo("bar.foo.baz.");
  }

  @Test
  void testFirstLabelOfDomain() {
    var domain = Domain.of("bar.foo.baz.");
    assertThat(domain.getFirstLabel()).isEqualTo(LABEL_2);
  }

  @Test
  void testLastLabelOfDomain() {
    var domain = Domain.of("bar.foo.baz.");
    assertThat(domain.getLastLabel()).isEqualTo(LABEL_1);
  }

  @Test
  void testDomainWithoutFirstLabel() {
    var domain = Domain.of("bar.foo.baz.");
    assertThat(domain.getDomainWithoutFirstLabel()).isEqualTo(Domain.of("foo.baz."));
  }

  @Test
  void testDomainWithoutLastLabel() {
    var domain = Domain.of("bar.foo.baz.");
    assertThat(domain.getDomainWithoutLastLabel()).isEqualTo(Domain.of("bar.foo."));
  }

  @Test
  void testCompareSameDomains() {
    var domain1 = Domain.of("bar.foo.baz.");
    var domain2 = Domain.of("bar.foo.baz.");
    assertThat(domain1).isEqualTo(domain2);
  }

  @Test
  void testCompareDifferentDomains() {
    var domain1 = Domain.of("bar.baz.");
    var domain2 = Domain.of("foo.baz.");
    assertThat(domain1).isNotEqualTo(domain2);
  }
}
