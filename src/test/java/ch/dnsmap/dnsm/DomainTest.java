package ch.dnsmap.dnsm;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainTest {

    private static final Label LABEL_1 = new Label("Baz");
    private static final Label LABEL_2 = new Label("bAr");
    private static final Label LABEL_3 = new Label("foO");
    private static final Domain DOMAIN_1 = Domain.of(LABEL_2);
    private static final Domain DOMAIN_2 = Domain.of(DOMAIN_1, LABEL_3);

    @Test
    void testCanonicalOnEmpty() {
        var domain = Domain.empty();
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
    void testCanonicalOnLongerDomainAndLabel() {
        var domain = Domain.of(DOMAIN_2, LABEL_1);
        assertThat(domain.getCanonical()).isEqualTo("bar.foo.baz.");
    }
}
