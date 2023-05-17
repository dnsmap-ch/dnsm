package ch.dnsmap.dnsm.infrastructure

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.AnswerResultType
import ch.dnsmap.dnsm.domain.model.Result
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol
import ch.dnsmap.dnsm.domain.model.query.ConnectionResult
import ch.dnsmap.dnsm.domain.model.query.ConnectionResultTimed
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryResultTimed
import ch.dnsmap.dnsm.domain.model.settings.ClientSettings
import ch.dnsmap.dnsm.domain.service.DummyQueryService
import ch.dnsmap.dnsm.domain.service.DummyStubResolverServiceImpl
import ch.dnsmap.dnsm.domain.service.Formatter
import ch.dnsmap.dnsm.domain.service.QueryService
import ch.dnsmap.dnsm.domain.service.ResultService
import ch.dnsmap.dnsm.domain.service.StubResolverService
import com.github.ajalt.clikt.core.BadParameterValue
import com.github.ajalt.clikt.core.IncorrectOptionValueCount
import com.github.ajalt.clikt.core.MissingOption
import com.github.ajalt.clikt.core.PrintHelpMessage
import com.github.ajalt.clikt.core.UsageError
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.After
import org.junit.Before
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.koin.core.context.GlobalContext.loadKoinModules
import org.koin.core.context.GlobalContext.unloadKoinModules
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.test.KoinTest
import java.net.InetAddress
import kotlin.time.Duration

private const val DOH_URL = "https://example.org"

class DohCommandTest : KoinTest {

    class TestResultService : ResultService {

        override fun run(settings: ClientSettings): Result {
            val connectionResult =
                ConnectionResult(InetAddress.getByName("127.0.0.1"), Port(53, Protocol.UDP))
            val queryResult = QueryResult(Domain.root(), emptyList(), "", AnswerResultType.NO_ERROR)
            return Result(
                emptyList(),
                ConnectionResultTimed(connectionResult, Duration.ZERO),
                QueryResultTimed(listOf(queryResult), Duration.ZERO)
            )
        }
    }

    private val modules = module {
        single { DummyStubResolverServiceImpl() }.bind(StubResolverService::class)
        single { Formatter() }
        single { DummyQueryService() }.bind(QueryService::class)
        singleOf(::TestResultService) { bind<ResultService>() }
    }

    @Before
    fun setUp() {
        loadKoinModules(modules)
    }

    @After
    fun tearDown() {
        unloadKoinModules(modules)
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

    @ParameterizedTest
    @ValueSource(strings = ["-u", "--url"])
    fun testUrlArgumentButMissingName(urlParameter: String) {
        assertThatThrownBy { DohCommand().parse(listOf(urlParameter, DOH_URL)) }
            .isInstanceOf(MissingOption::class.java)
            .hasMessage("Missing option \"--name\"")
    }

    @ParameterizedTest
    @ValueSource(strings = ["-n", "--name"])
    @Disabled
    fun testMinimalArgSet(nameParameter: String) {
        assertThatThrownBy { DohCommand().parse(listOf("-u", DOH_URL, nameParameter, "example.com")) }
            .doesNotThrowAnyException()
    }

    @ParameterizedTest
    @ValueSource(strings = ["-n", "--name"])
    fun testInvalidLabelName(nameParameter: String) {
        assertThatThrownBy { DohCommand().parse(listOf("-u", DOH_URL, nameParameter, "111")) }
            .isInstanceOf(BadParameterValue::class.java)
            .hasMessage("Invalid value for \"$nameParameter\": label must start with alpha character")
    }

    @ParameterizedTest
    @ValueSource(strings = ["-t", "--type"])
    @Disabled
    fun testValidType(typeParameter: String) {
        assertThatThrownBy { DohCommand().parse(listOf("-u", DOH_URL, "-n", "example.com", typeParameter, "a,A,AAAA")) }
            .doesNotThrowAnyException()
    }

    @ParameterizedTest
    @ValueSource(strings = ["-t", "--type"])
    fun testMissingValueForType(typeParameter: String) {
        assertThatThrownBy { DohCommand().parse(listOf("-u", DOH_URL, "-n", "example.com", typeParameter)) }
            .isInstanceOf(IncorrectOptionValueCount::class.java)
            .hasMessage("option $typeParameter requires a value")
    }

    @ParameterizedTest
    @ValueSource(strings = ["-t", "--type"])
    fun testInvalidType(typeParameter: String) {
        assertThatThrownBy { DohCommand().parse(listOf("-u", DOH_URL, "-n", "example.com", typeParameter, "asdf")) }
            .isInstanceOf(BadParameterValue::class.java)
            .hasMessageStartingWith("Invalid value for \"$typeParameter\"")
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
