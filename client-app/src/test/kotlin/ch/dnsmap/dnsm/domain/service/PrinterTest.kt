package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.PlainSettings
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol.UDP
import ch.dnsmap.dnsm.domain.service.QueryType.A
import ch.dnsmap.dnsm.domain.service.QueryType.AAAA
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.InetAddress

class PrinterTest {

    private val domainExampleCom = Domain.of("example.com")

    @Test
    fun testHeader() {
        val result = Printer().header(settings())
        assertThat(result).isEqualTo("Query 127.0.0.1:53/udp")
    }

    private fun settings() = PlainSettings(
        InetAddress.getByName("127.0.0.1"),
        Port(53, UDP),
        domainExampleCom,
        listOf(AAAA, A)
    )
}