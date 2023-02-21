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
                single { QueryServiceTest() } bind QueryService::class
            }
        )
    }

    @Test
    fun testResultService() {
        val service: QueryService by inject()
        val resultService = ResultServiceImpl(settings(), service, TaskService())

        val result = resultService.run()

        assertThat(result.responses).containsExactlyInAnyOrder(
            QueryResult(listOf("127.0.0.1"), emptyList(), "A", AnswerResultType.NO_ERROR),
            QueryResult(listOf("::1"), emptyList(), "AAAA", AnswerResultType.NO_ERROR)
        )
        assertThat(result.tasks).containsExactlyInAnyOrder(
            QueryTask(Domain.of("example.com"), A),
            QueryTask(Domain.of("example.com"), AAAA),
        )
    }

    private fun settings() = ClientSettingsPlain(
        InetAddress.getByName("127.0.0.1"),
        Port(53, Protocol.UDP),
        Domain.of("example.com"),
        listOf(AAAA, A),
        Pair(5, SECONDS)
    )
}
