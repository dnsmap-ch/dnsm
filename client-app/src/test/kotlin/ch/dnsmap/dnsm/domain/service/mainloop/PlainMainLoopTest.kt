package ch.dnsmap.dnsm.domain.service.mainloop

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.AnswerResultType.NO_ERROR
import ch.dnsmap.dnsm.domain.model.OptionFlags
import ch.dnsmap.dnsm.domain.model.Result
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol
import ch.dnsmap.dnsm.domain.model.networking.Protocol.UDP
import ch.dnsmap.dnsm.domain.model.query.ConnectionResult
import ch.dnsmap.dnsm.domain.model.query.ConnectionResultTimed
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryResultTimed
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.query.QueryType.A
import ch.dnsmap.dnsm.domain.model.query.QueryType.AAAA
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsPlain
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
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.time.Duration

private const val LOCALHOST_STRING = "127.0.0.1"
private const val EXAMPLE_ORG_STRING = "example.org"

class PlainMainLoopTest {

    private val stubResolverService = DummyStubResolverServiceImpl()
    private val queryService = DummyQueryService()
    private val resultService = ResultServiceImpl(queryService)
    private val plainMainLoop =
        PlainMainLoop(stubResolverService, resultService, Formatter(), SilentOutput(::println))

    @Test
    fun testRun() {
        val optionFlags = OptionFlags.Builder()
            .name(Domain.of("example.org"))
            .resolverHost("127.0.0.1")
            .resolverPort(Port(53, UDP))
            .timeout(1337L)
            .types(listOf(AAAA, A))
            .build()

        assertDoesNotThrow { plainMainLoop.run(optionFlags) }
    }

    @Test
    fun testPrepare() {
        val optionFlags = OptionFlags.Builder()
            .name(Domain.of("example.org"))
            .resolverHost("127.0.0.1")
            .resolverPort(Port(53, UDP))
            .timeout(1337L)
            .types(listOf(AAAA, A))
            .build()

        val settings = plainMainLoop.prepare(optionFlags)

        assertThat(settings as ClientSettingsPlain).satisfies({
            assertThat(it.name()).isEqualTo(Domain.of("example.org"))
            assertThat(it.resolverHost()).isEqualTo("127.0.0.1")
            assertThat(it.resolverPort()).isEqualTo(Port(53, UDP))
            assertThat(it.timeout()).isEqualTo(Pair(1337L, SECONDS))
            assertThat(it.types()).containsExactly(AAAA, A)
        })
    }

    @Test
    fun testAct() {
        val settings = plainSettings()
        val result = plainMainLoop.act(settings)
        assertThat(result).satisfies({
            assertThat(result.tasks).hasSize(2)
        })
    }

    @Test
    fun testComplete() {
        val connectionResult =
            ConnectionResult(InetAddress.getByName(LOCALHOST_STRING), Port(443, Protocol.TCP))
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
            plainMainLoop.complete(result)

        assertThat(summary.queryTotal).isEqualTo(1)
        assertThat(summary.answerTotal).isEqualTo(1)
        assertThat(summary.connectionResultTimed).isEqualTo(connectionResultTimed)
        assertThat(summary.queryResultTimed).isEqualTo(queryResultTimed)
    }
}
