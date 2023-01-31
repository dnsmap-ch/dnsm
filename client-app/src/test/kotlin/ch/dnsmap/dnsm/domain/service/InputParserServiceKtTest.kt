package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InputParserServiceKtTest {

    private val port53Udp = Port(53, Protocol.UDP)
    private val port53Tcp = Port(53, Protocol.TCP)

    @Test
    fun testPortNumberOnly() {
        val input = "53"
        val result = parsePort(input)
        assertThat(result).isEqualTo(port53Udp)
    }

    @Test
    fun testPortNumberUdp() {
        val input = "53/udp"
        val result = parsePort(input)
        assertThat(result).isEqualTo(port53Udp)
    }

    @Test
    fun testPortNumberTcp() {
        val input = "53/tcp"
        val result = parsePort(input)
        assertThat(result).isEqualTo(port53Tcp)
    }

    @Test
    fun testThrowOnEmpty() {
        val input = ""
        Assertions.assertThatThrownBy { parsePort(input) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("must not be blank or empty")
    }

    @Test
    fun testThrowOnBlank() {
        val input = "       "
        Assertions.assertThatThrownBy { parsePort(input) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("must not be blank or empty")
    }

    @Test
    fun testThrowIfNonsense() {
        val input = "nonsense"
        Assertions.assertThatThrownBy { parsePort(input) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("invalid or no port specified")
    }

    @Test
    fun testThrowIfNoPort() {
        val input = "/tcp"
        Assertions.assertThatThrownBy { parsePort(input) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("invalid or no port specified")
    }

    @Test
    fun testThrowIfUnknownProtocol() {
        val input = "53/unknown"
        Assertions.assertThatThrownBy { parsePort(input) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("invalid input 53/unknown")
    }
}
