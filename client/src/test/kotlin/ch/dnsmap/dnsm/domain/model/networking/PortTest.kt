package ch.dnsmap.dnsm.domain.model.networking

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.math.pow

class PortTest {

    @Test
    fun testInvalidNegativePort() {
        Assertions.assertThatThrownBy { Port(-1, Protocol.TCP) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Invalid port -1")
    }

    @Test
    fun testInvalidZeroPort() {
        Assertions.assertThatThrownBy { Port(0, Protocol.TCP) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Invalid port 0")
    }

    @Test
    fun testValidMinPort() {
        assertThat(Port(1, Protocol.UDP)).satisfies({
            assertThat(it.port).isEqualTo(1)
            assertThat(it.protocol).isEqualTo(Protocol.UDP)
            assertThat(it.asString()).isEqualTo("1/udp")
        })
    }

    @Test
    fun testValidMaxPort() {
        assertThat(Port(2.0.pow(16.0).toInt() - 1, Protocol.TCP)).satisfies({
            assertThat(it.port).isEqualTo(65535)
            assertThat(it.protocol).isEqualTo(Protocol.TCP)
            assertThat(it.asString()).isEqualTo("65535/tcp")
        })
    }

    @Test
    fun testInvalidTooHighPort() {
        Assertions.assertThatThrownBy { Port(65536, Protocol.TCP) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Invalid port 65536")
    }
}
