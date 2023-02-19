package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.QueryType
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class InputParserHelperKtTest {

    @Test
    fun testEmptyQueryType() {
        val input = ""
        val result = parseInputType(input)
        assertThat(result).isEmpty()
    }

    @Test
    fun testBlankQueryType() {
        val input = "   "
        val result = parseInputType(input)
        assertThat(result).isEmpty()
    }

    @Test
    fun testMultiBlankQueryType() {
        val input = " ,  "
        val result = parseInputType(input)
        assertThat(result).isEmpty()
    }

    @Test
    fun testMultiAQueryType() {
        val input = "a,AAAA,a,A,A,aaaa,a"
        val result = parseInputType(input)
        assertThat(result).containsExactly(
            QueryType.A,
            QueryType.AAAA,
            QueryType.A,
            QueryType.A,
            QueryType.A,
            QueryType.AAAA,
            QueryType.A,
        )
    }

    @Test
    fun testEmptyPortParsing() {
        val input = ""
        assertThatThrownBy { parsePort(input) }
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
    fun testInvalidPort() {
        val input = "not a port"
        assertThatThrownBy { parsePort(input) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("invalid or no port specified")
    }

    @Test
    fun testInvalidProtocol() {
        val input = "53/invalid"
        assertThatThrownBy { parsePort(input) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("invalid input 53/invalid")
    }

    @Test
    fun testThrowIfNoPort() {
        val input = "/tcp"
        assertThatThrownBy { parsePort(input) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("invalid or no port specified")
    }

    @Test
    fun testValidPort53() {
        val input = "53"
        val result = parsePort(input)
        assertThat(result).isEqualTo(Port(53, Protocol.UDP))
    }

    @Test
    fun testValidPort53Udp() {
        val input = "53/udp"
        val result = parsePort(input)
        assertThat(result).isEqualTo(Port(53, Protocol.UDP))
    }

    @Test
    fun testValidPort53Tcp() {
        val input = "53/tcp"
        val result = parsePort(input)
        assertThat(result).isEqualTo(Port(53, Protocol.TCP))
    }
}
