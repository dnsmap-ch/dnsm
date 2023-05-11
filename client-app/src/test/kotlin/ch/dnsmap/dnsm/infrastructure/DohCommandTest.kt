package ch.dnsmap.dnsm.infrastructure

import ch.dnsmap.dnsm.domain.model.AnswerResultType
import ch.dnsmap.dnsm.domain.model.Result
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol
import ch.dnsmap.dnsm.domain.model.query.ConnectionResult
import ch.dnsmap.dnsm.domain.model.query.ConnectionResultTimed
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryResultTimed
import ch.dnsmap.dnsm.domain.service.DummyStubResolverServiceImpl
import ch.dnsmap.dnsm.domain.service.Printer
import ch.dnsmap.dnsm.domain.service.QueryService
import ch.dnsmap.dnsm.domain.service.QueryServiceTest
import ch.dnsmap.dnsm.domain.service.ResultService
import ch.dnsmap.dnsm.domain.service.StubResolverService
import ch.dnsmap.dnsm.infrastructure.modules.MODULE_DOH
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

private const val DOH_URL = "https://example.org"

class DohCommandTest : KoinTest {

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
                singleOf(::DummyStubResolverServiceImpl) { bind<StubResolverService>() }
                single(named(MODULE_DOH)) { TestResultService() } bind ResultService::class
            }
        )
    }

    @Test
    fun testEmptyArgList() {
        assertThatThrownBy { DohCommand().parse(emptyList()) }
            .isInstanceOf(MissingOption::class.java)
            .hasMessage("Missing option \"--url\"")
    }

    @Test
    fun testNonsenseArgList() {
        assertThatThrownBy { DohCommand().parse(listOf("foo", "bar")) }
            .isInstanceOf(UsageError::class.java)
            .hasMessage("Got unexpected extra arguments (foo bar)")
    }

    @Test
    fun testHelp() {
        assertThatThrownBy { DohCommand().parse(listOf("--help")) }
            .isInstanceOf(PrintHelpMessage::class.java)
    }

    @Test
    fun testUrlArgumentButMissingName() {
        assertThatThrownBy { DohCommand().parse(listOf("--url", DOH_URL)) }
            .isInstanceOf(MissingOption::class.java)
            .hasMessage("Missing option \"--name\"")
    }

    @Test
    fun testMinimalArgSet() {
        DohCommand().parse(listOf("-u", DOH_URL, "-n", "example.com"))
        DohCommand().parse(listOf("--url", DOH_URL, "--name", "example.com"))
    }

    @Test
    fun testInvalidLabelName() {
        assertThatThrownBy { DohCommand().parse(listOf("-u", DOH_URL, "-n", "111")) }
            .isInstanceOf(BadParameterValue::class.java)
            .hasMessage("Invalid value for \"-n\": label must start with alpha character")
    }

    @Test
    fun testValidType() {
        DohCommand().parse(listOf("-u", DOH_URL, "-n", "example.com", "-t", "a,A,AAAA"))
        DohCommand().parse(listOf("-u", DOH_URL, "-n", "example.com", "--type", "a,A,AAAA"))
    }

    @Test
    fun testMissingValueForType() {
        assertThatThrownBy { DohCommand().parse(listOf("-u", DOH_URL, "-n", "example.com", "-t")) }
            .isInstanceOf(IncorrectOptionValueCount::class.java)
            .hasMessage("option -t requires a value")
    }

    @Test
    fun testInvalidType() {
        assertThatThrownBy { DohCommand().parse(listOf("-u", DOH_URL, "-n", "example.com", "-t", "asdf")) }
            .isInstanceOf(BadParameterValue::class.java)
            .hasMessageStartingWith("Invalid value for \"-t\"")
    }

    @Test
    fun testInvalidNegativeTimeout() {
        assertThatThrownBy { DohCommand().parse(listOf("-u", DOH_URL, "-n", "example.com", "--timeout", "-1")) }
            .isInstanceOf(BadParameterValue::class.java)
            .hasMessageStartingWith("Invalid value for \"--timeout\": -1 is smaller than the minimum valid value of 1.")
    }

    @Test
    fun testInvalidNonNumberTimeout() {
        assertThatThrownBy { DohCommand().parse(listOf("-u", DOH_URL, "-n", "example.com", "--timeout", "NaN")) }
            .isInstanceOf(BadParameterValue::class.java)
            .hasMessageStartingWith("Invalid value for \"--timeout\": NaN is not a valid integer")
    }
}
