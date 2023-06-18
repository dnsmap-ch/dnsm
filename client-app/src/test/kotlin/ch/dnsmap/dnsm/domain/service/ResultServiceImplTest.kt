package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.AnswerResultType
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.query.QueryType.A
import ch.dnsmap.dnsm.domain.model.query.QueryType.AAAA
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsPlain
import ch.dnsmap.dnsm.domain.service.logging.SilentOutput
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import java.net.InetAddress
import java.util.concurrent.TimeUnit.SECONDS

class ResultServiceImplTest : KoinTest {

    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single { DummyQueryService() } bind QueryService::class
            }
        )
    }

    @Test
    fun testResultService() {
        val service: QueryService by inject()
        val resultService = ResultServiceImpl(service, SilentOutput(System.out::println))

        val result = resultService.run(settings())

        assertThat(result.queryResultTimed.queryResults).containsExactlyInAnyOrder(
            QueryResult(Domain.root(), listOf("127.0.0.1"), "A", AnswerResultType.NO_ERROR),
            QueryResult(Domain.root(), listOf("::1"), "AAAA", AnswerResultType.NO_ERROR)
        )
        assertThat(result.tasks).containsExactlyInAnyOrder(
            QueryTask(Domain.of("example.com"), A),
            QueryTask(Domain.of("example.com"), AAAA),
        )
    }

    private fun settings() = ClientSettingsPlain.ClientSettingsPlainBuilder()
        .resolverHost("localhost")
        .resolverIp(InetAddress.getByName("127.0.0.1"))
        .resolverPort(Port(53, Protocol.UDP))
        .name(Domain.of("example.com"))
        .types(listOf(AAAA, A))
        .timeout(Pair(5, SECONDS))
        .build()
}
