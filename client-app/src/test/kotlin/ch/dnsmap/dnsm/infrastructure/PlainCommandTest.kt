package ch.dnsmap.dnsm.infrastructure

import ch.dnsmap.dnsm.domain.model.AnswerResultType
import ch.dnsmap.dnsm.domain.model.Result
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol
import ch.dnsmap.dnsm.domain.model.query.ConnectionResult
import ch.dnsmap.dnsm.domain.model.query.ConnectionResultTimed
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryResultTimed
import ch.dnsmap.dnsm.domain.service.Printer
import ch.dnsmap.dnsm.domain.service.QueryService
import ch.dnsmap.dnsm.domain.service.QueryServiceTest
import ch.dnsmap.dnsm.domain.service.ResultService
import ch.dnsmap.dnsm.domain.service.StubResolverService
import ch.dnsmap.dnsm.infrastructure.modules.MODULE_PLAIN
import com.github.ajalt.clikt.core.BadParameterValue
import com.github.ajalt.clikt.core.IncorrectOptionValueCount
import com.github.ajalt.clikt.core.MissingOption
import com.github.ajalt.clikt.core.PrintHelpMessage
import com.github.ajalt.clikt.core.UsageError
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension
import java.net.InetAddress
import kotlin.time.Duration

class PlainCommandTest : KoinTest {

    class TestResultService : ResultService {
        override fun run(): Result {
            return emptyResult()
        }

        private fun emptyResult(): Result {
            val connectionResult =
                ConnectionResult(InetAddress.getByName("127.0.0.1"), Port(53, Protocol.UDP))
            val queryResult = QueryResult(emptyList(), emptyList(), "", AnswerResultType.NO_ERROR)
            return Result(
                emptyList(),
                ConnectionResultTimed(connectionResult, Duration.ZERO),
                QueryResultTimed(listOf(queryResult), Duration.ZERO)
            )
        }
    }

    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                singleOf(::Printer)
                singleOf(::QueryServiceTest) { bind<QueryService>() }
                singleOf(::StubResolverService)
                single(named(MODULE_PLAIN)) { TestResultService() } bind ResultService::class
            }
        )
    }

    @Test
    fun testEmptyArgList() {
        assertThatThrownBy { PlainCommand().parse(emptyList()) }
            .isInstanceOf(MissingOption::class.java)
            .hasMessage("Missing option \"--resolver\"")
    }

    @Test
    fun testNonsenseArgList() {
        assertThatThrownBy { PlainCommand().parse(listOf("foo", "bar")) }
            .isInstanceOf(UsageError::class.java)
            .hasMessage("Got unexpected extra arguments (foo bar)")
    }

    @Test
    fun testHelp() {
        assertThatThrownBy { PlainCommand().parse(listOf("--help")) }
            .isInstanceOf(PrintHelpMessage::class.java)
    }

    @Test
    fun testResolverButMissingName() {
        assertThatThrownBy { PlainCommand().parse(listOf("--resolver", "localhost")) }
            .isInstanceOf(MissingOption::class.java)
            .hasMessage("Missing option \"--name\"")
    }

    @Test
    fun testMinimalArgSet() {
        PlainCommand().parse(
            listOf("-r", "localhost", "-n", "example.com")
        )
        PlainCommand().parse(
            listOf("--resolver", "localhost", "--name", "example.com")
        )
    }

    @Test
    fun testInvalidLabelName() {
        assertThatThrownBy { PlainCommand().parse(listOf("-r", "localhost", "-n", "111")) }
            .isInstanceOf(BadParameterValue::class.java)
            .hasMessage("Invalid value for \"-n\": label must start with alpha character")
    }

    @Test
    fun testValidPorts() {
        PlainCommand().parse(
            listOf("-r", "localhost", "-n", "example.com", "-p", "53")
        )
        PlainCommand().parse(
            listOf("-r", "localhost", "-n", "example.com", "--port", "53")
        )
        PlainCommand().parse(
            listOf("-r", "localhost", "-n", "example.com", "-p", "53/udp")
        )
        PlainCommand().parse(
            listOf("-r", "localhost", "-n", "example.com", "-p", "53/tcp")
        )
    }

    @Test
    fun testInvalidZeroPort() {
        assertThatThrownBy {
            PlainCommand().parse(
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
            PlainCommand().parse(
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
            PlainCommand().parse(
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
            PlainCommand().parse(
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
        PlainCommand().parse(
            listOf("-r", "localhost", "-n", "example.com", "-t", "a,A,AAAA")
        )
        PlainCommand().parse(
            listOf("-r", "localhost", "-n", "example.com", "--type", "a,A,AAAA")
        )
    }

    @Test
    fun testMissingValueForType() {
        assertThatThrownBy {
            PlainCommand().parse(
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
            PlainCommand().parse(
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

    @Test
    fun testInvalidNegativeTimeout() {
        assertThatThrownBy {
            PlainCommand().parse(
                listOf(
                    "-r",
                    "localhost",
                    "-n",
                    "example.com",
                    "--timeout",
                    "-1"
                )
            )
        }
            .isInstanceOf(BadParameterValue::class.java)
            .hasMessageStartingWith("Invalid value for \"--timeout\": -1 is smaller than the minimum valid value of 1.")
    }

    @Test
    fun testInvalidNonNumberTimeout() {
        assertThatThrownBy {
            PlainCommand().parse(
                listOf(
                    "-r",
                    "localhost",
                    "-n",
                    "example.com",
                    "--timeout",
                    "NaN"
                )
            )
        }
            .isInstanceOf(BadParameterValue::class.java)
            .hasMessageStartingWith("Invalid value for \"--timeout\": NaN is not a valid integer")
    }
}
