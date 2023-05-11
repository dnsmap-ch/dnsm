package ch.dnsmap.dnsm.domain.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StubResolverServiceTest {

    @Test
    fun testIpResolution() {
        val result = StubResolverServiceImpl().resolve("127.0.0.1")
        assertThat(result.hostAddress).isEqualTo("127.0.0.1")
    }
}
