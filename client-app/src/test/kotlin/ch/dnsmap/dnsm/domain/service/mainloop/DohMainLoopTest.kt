package ch.dnsmap.dnsm.domain.service.mainloop

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.AnswerResultType.NO_ERROR
import ch.dnsmap.dnsm.domain.model.HttpMethod.GET
import ch.dnsmap.dnsm.domain.model.OptionFlags
import ch.dnsmap.dnsm.domain.model.Result
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol.TCP
import ch.dnsmap.dnsm.domain.model.query.ConnectionResult
import ch.dnsmap.dnsm.domain.model.query.ConnectionResultTimed
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryResultTimed
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.query.QueryType.A
import ch.dnsmap.dnsm.domain.model.query.QueryType.AAAA
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsDoh
import ch.dnsmap.dnsm.domain.model.settings.plainSettings
import ch.dnsmap.dnsm.domain.service.DummyQueryService
import ch.dnsmap.dnsm.domain.service.DummyStubResolverServiceImpl
import ch.dnsmap.dnsm.domain.service.Formatter
import ch.dnsmap.dnsm.domain.service.ResultServiceImpl
import ch.dnsmap.dnsm.domain.service.logging.SilentOutput
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.net.InetAddress
import java.net.URI
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.time.Duration

private const val LOCALHOST_STRING = "127.0.0.1"
private const val EXAMPLE_ORG_STRING = "example.org"

class DohMainLoopTest {

    private val stubResolverService = DummyStubResolverServiceImpl()
    private val queryService = DummyQueryService()
    private val resultService = ResultServiceImpl(queryService, SilentOutput(System.out::println))
    private val dohMainLoop =
        DohMainLoop(stubResolverService, resultService, Formatter(), SilentOutput(::println))

    @Test
    fun testRun() {
        val optionFlags = OptionFlags.Builder()
            .method(GET)
            .name(Domain.of(EXAMPLE_ORG_STRING))
            .resolverHost(LOCALHOST_STRING)
            .timeout(1337L)
            .types(listOf(AAAA, A))
            .resolverUrl("https://dns.example.org:8443/")
            .build()

        assertDoesNotThrow { dohMainLoop.run(optionFlags) }
    }

    @Test
    fun testPrepare() {
        val optionFlags = OptionFlags.Builder()
            .method(GET)
            .name(Domain.of(EXAMPLE_ORG_STRING))
            .resolverHost(LOCALHOST_STRING)
            .timeout(1337L)
            .types(listOf(AAAA, A))
            .resolverUrl("https://dns.example.org:8443/")
            .build()

        val settings = dohMainLoop.prepare(optionFlags)

        assertThat(settings as ClientSettingsDoh).satisfies({
            assertThat(it.method()).isEqualTo(GET)
            assertThat(it.name()).isEqualTo(Domain.of(EXAMPLE_ORG_STRING))
            assertThat(it.resolverHost()).isEqualTo("dns.example.org")
            assertThat(it.resolverPort()).isEqualTo(Port(8443, TCP))
            assertThat(it.timeout()).isEqualTo(Pair(1337L, SECONDS))
            assertThat(it.types()).containsExactly(AAAA, A)
            assertThat(it.url()).isEqualTo(URI.create("https://dns.example.org:8443/dns-query"))
        })
    }

    @Test
    fun testDefaultPortPrepare() {
        val optionFlags = OptionFlags.Builder()
            .method(GET)
            .name(Domain.of(EXAMPLE_ORG_STRING))
            .resolverHost(LOCALHOST_STRING)
            .timeout(1337L)
            .types(listOf(AAAA, A))
            .resolverUrl("https://dns.example.org/")
            .build()

        val settings = dohMainLoop.prepare(optionFlags)

        assertThat(settings as ClientSettingsDoh).satisfies({
            assertThat(it.method()).isEqualTo(GET)
            assertThat(it.name()).isEqualTo(Domain.of(EXAMPLE_ORG_STRING))
            assertThat(it.resolverHost()).isEqualTo("dns.example.org")
            assertThat(it.resolverPort()).isEqualTo(Port(443, TCP))
            assertThat(it.timeout()).isEqualTo(Pair(1337L, SECONDS))
            assertThat(it.types()).containsExactly(AAAA, A)
            assertThat(it.url()).isEqualTo(URI.create("https://dns.example.org/dns-query"))
        })
    }

    @Test
    fun testAct() {
        val settings = plainSettings()
        val result = dohMainLoop.act(settings)
        assertThat(result).satisfies({
            assertThat(result.tasks).hasSize(2)
        })
    }

    @Test
    fun testComplete() {
        val connectionResult =
            ConnectionResult(InetAddress.getByName(LOCALHOST_STRING), Port(443, TCP))
        val connectionResultTimed = ConnectionResultTimed(connectionResult, Duration.ZERO)
        val queryResultTimed = QueryResultTimed(
            listOf(
                QueryResult(
                    Domain.of(EXAMPLE_ORG_STRING),
                    listOf(
                        LOCALHOST_STRING
                    ),
                    "A",
                    NO_ERROR
                )
            ),
            Duration.ZERO
        )
        val result = Result(
            listOf(QueryTask(Domain.of(EXAMPLE_ORG_STRING), A)),
            connectionResultTimed,
            queryResultTimed
        )

        val summary =
            dohMainLoop.complete(result)

        assertThat(summary.queryTotal).isEqualTo(1)
        assertThat(summary.answerTotal).isEqualTo(1)
        assertThat(summary.connectionResultTimed).isEqualTo(connectionResultTimed)
        assertThat(summary.queryResultTimed).isEqualTo(queryResultTimed)
    }
}
