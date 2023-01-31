package ch.dnsmap.dnsm.infrastructure

import ch.dnsmap.dnsm.domain.model.QueryResponse
import ch.dnsmap.dnsm.domain.model.QueryTask
import ch.dnsmap.dnsm.domain.model.Result
import ch.dnsmap.dnsm.domain.model.Status
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.service.Printer
import ch.dnsmap.dnsm.domain.service.QueryService
import ch.dnsmap.dnsm.domain.service.ResultService
import com.github.ajalt.clikt.core.BadParameterValue
import com.github.ajalt.clikt.core.IncorrectOptionValueCount
import com.github.ajalt.clikt.core.MissingOption
import com.github.ajalt.clikt.core.PrintHelpMessage
import com.github.ajalt.clikt.core.UsageError
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension
import java.net.InetAddress

class PlainCommandTest : KoinTest {

    class DummyService : QueryService {
        override fun query(
            resolverHost: InetAddress,
            resolverPort: Port,
            queries: List<QueryTask>
        ): List<QueryResponse> {
            return listOf(
                QueryResponse(listOf("127.0.0.1"), emptyList(), "A", Status.NO_ERROR),
                QueryResponse(listOf("::1"), emptyList(), "AAAA", Status.NO_ERROR)
            )
        }
    }

    class DummyResultService2 : ResultService {
        override fun run(): Result {
            return Result.emptyResult()
        }
    }

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single { DummyService() } bind QueryService::class
                single { DummyResultService2() } bind ResultService::class
            }
        )
    }

    @Test
    fun testEmptyArgList() {
        assertThatThrownBy { PlainCommand(Printer()).parse(emptyList()) }
            .isInstanceOf(MissingOption::class.java)
            .hasMessage("Missing option \"--resolver\"")
    }

    @Test
    fun testNonsenseArgList() {
        assertThatThrownBy { PlainCommand(Printer()).parse(listOf("foo", "bar")) }
            .isInstanceOf(UsageError::class.java)
            .hasMessage("Got unexpected extra arguments (foo bar)")
    }

    @Test
    fun testHelp() {
        assertThatThrownBy { PlainCommand(Printer()).parse(listOf("--help")) }
            .isInstanceOf(PrintHelpMessage::class.java)
    }

    @Test
    fun testResolverButMissingName() {
        assertThatThrownBy { PlainCommand(Printer()).parse(listOf("--resolver", "localhost")) }
            .isInstanceOf(MissingOption::class.java)
            .hasMessage("Missing option \"--name\"")
    }

    @Test
    fun testMinimalArgSet() {
        PlainCommand(Printer()).parse(
            listOf("-r", "localhost", "-n", "example.com")
        )
        PlainCommand(Printer()).parse(
            listOf("--resolver", "localhost", "--name", "example.com")
        )
    }

    @Test
    fun testInvalidLabelName() {
        assertThatThrownBy { PlainCommand(Printer()).parse(listOf("-r", "localhost", "-n", "111")) }
            .isInstanceOf(BadParameterValue::class.java)
            .hasMessage("Invalid value for \"-n\": label must start with alpha character")
    }

    @Test
    fun testValidPorts() {
        PlainCommand(Printer()).parse(
            listOf("-r", "localhost", "-n", "example.com", "-p", "53")
        )
        PlainCommand(Printer()).parse(
            listOf("-r", "localhost", "-n", "example.com", "--port", "53")
        )
        PlainCommand(Printer()).parse(
            listOf("-r", "localhost", "-n", "example.com", "-p", "53/udp")
        )
        PlainCommand(Printer()).parse(
            listOf("-r", "localhost", "-n", "example.com", "-p", "53/tcp")
        )
    }

    @Test
    fun testInvalidZeroPort() {
        assertThatThrownBy {
            PlainCommand(Printer()).parse(
                listOf(
                    "-r",
                    "localhost",
                    "-n",
                    "example.com",
                    "-p",
                    "0"
                )
            )
        }
            .isInstanceOf(BadParameterValue::class.java)
            .hasMessage("Invalid value for \"-p\": Invalid port 0")
    }

    @Test
    fun testInvalidNegativePort() {
        assertThatThrownBy {
            PlainCommand(Printer()).parse(
                listOf(
                    "-r",
                    "localhost",
                    "-n",
                    "example.com",
                    "-p",
                    "-1"
                )
            )
        }
            .isInstanceOf(BadParameterValue::class.java)
            .hasMessage("Invalid value for \"-p\": Invalid port -1")
    }

    @Test
    fun testInvalidToHighPort() {
        assertThatThrownBy {
            PlainCommand(Printer()).parse(
                listOf(
                    "-r",
                    "localhost",
                    "-n",
                    "example.com",
                    "-p",
                    "1_000_000"
                )
            )
        }
            .isInstanceOf(BadParameterValue::class.java)
            .hasMessage("Invalid value for \"-p\": invalid or no port specified")
    }

    @Test
    fun testInvalidPort() {
        assertThatThrownBy {
            PlainCommand(Printer()).parse(
                listOf(
                    "-r",
                    "localhost",
                    "-n",
                    "example.com",
                    "-p",
                    "foo"
                )
            )
        }
            .isInstanceOf(BadParameterValue::class.java)
            .hasMessage("Invalid value for \"-p\": invalid or no port specified")
    }

    @Test
    fun testValidType() {
        PlainCommand(Printer()).parse(
            listOf("-r", "localhost", "-n", "example.com", "-t", "a,A,AAAA")
        )
        PlainCommand(Printer()).parse(
            listOf("-r", "localhost", "-n", "example.com", "--type", "a,A,AAAA")
        )
    }

    @Test
    fun testMissingValueForType() {
        assertThatThrownBy {
            PlainCommand(Printer()).parse(
                listOf(
                    "-r",
                    "localhost",
                    "-n",
                    "example.com",
                    "-t",
                )
            )
        }
            .isInstanceOf(IncorrectOptionValueCount::class.java)
            .hasMessage("option -t requires a value")
    }

    @Test
    fun testInvalidType() {
        assertThatThrownBy {
            PlainCommand(Printer()).parse(
                listOf(
                    "-r",
                    "localhost",
                    "-n",
                    "example.com",
                    "-t",
                    "asdf"
                )
            )
        }
            .isInstanceOf(BadParameterValue::class.java)
            .hasMessageStartingWith("Invalid value for \"-t\"")
    }
}
