package ch.dnsmap.dnsm.infrastructure

import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import kotlin.IllegalArgumentException

internal class PortFactoryTest {

    private val PORT_53_UDP = Port(53, Protocol.UDP)
    private val PORT_53_TCP = Port(53, Protocol.TCP)
    private val PORT_53_UDP_TCP = Port(53, Protocol.UDP_TCP)

    @Test
    fun testPortNumberOnly() {
        val input = "53"
        val result = parsePort(input)
        assertThat(result).isEqualTo(PORT_53_UDP)
    }

    @Test
    fun testPortNumberUdp() {
        val input = "53/udp"
        val result = parsePort(input)
        assertThat(result).isEqualTo(PORT_53_UDP)
    }

    @Test
    fun testPortNumberTcp() {
        val input = "53/tcp"
        val result = parsePort(input)
        assertThat(result).isEqualTo(PORT_53_TCP)
    }

    @Test
    fun testPortNumberUdpTcp() {
        val input = "53/udp/tcp"
        val result = parsePort(input)
        assertThat(result).isEqualTo(PORT_53_UDP_TCP)
    }

    @Test
    fun testPortNumberUdpTcpFunkyCapitalisation() {
        val input = "53/tCp/UdP/"
        val result = parsePort(input)
        assertThat(result).isEqualTo(PORT_53_UDP_TCP)
    }

    @Test
    fun testThrowOnEmpty() {
        val input = ""
        assertThatThrownBy { parsePort(input) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("must not be blank or empty")
    }

    @Test
    fun testThrowOnBlank() {
        val input = "       "
        assertThatThrownBy { parsePort(input) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("must not be blank or empty")
    }

    @Test
    fun testThrowIfNonsense() {
        val input = "nonsense"
        assertThatThrownBy { parsePort(input) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("invalid or no port specified")
    }

    @Test
    fun testThrowIfNoPort() {
        val input = "/tcp"
        assertThatThrownBy { parsePort(input) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("invalid or no port specified")
    }

    @Test
    fun testThrowIfUnknownProtocol() {
        val input = "53/unknown"
        assertThatThrownBy { parsePort(input) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("invalid input 53/unknown")
    }
}