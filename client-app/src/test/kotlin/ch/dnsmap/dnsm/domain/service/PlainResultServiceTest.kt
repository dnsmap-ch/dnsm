package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.PlainSettings
import ch.dnsmap.dnsm.domain.model.QueryResponse
import ch.dnsmap.dnsm.domain.model.QueryTask
import ch.dnsmap.dnsm.domain.model.Status
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol
import ch.dnsmap.dnsm.domain.service.QueryType.A
import ch.dnsmap.dnsm.domain.service.QueryType.AAAA
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

class PlainResultServiceTest : KoinTest {

    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single { TestQueryService() } bind QueryService::class
            }
        )
    }

    @Test
    fun testResultService() {
        val service: QueryService by inject()
        val resultService = PlainResultService(settings(), service, TaskService())

        val result = resultService.run()

        assertThat(result.responses).containsExactlyInAnyOrder(
            QueryResponse(listOf("127.0.0.1"), emptyList(), "A", Status.NO_ERROR),
            QueryResponse(listOf("::1"), emptyList(), "AAAA", Status.NO_ERROR)
        )
        assertThat(result.tasks).containsExactlyInAnyOrder(
            QueryTask(Domain.of("example.com"), A),
            QueryTask(Domain.of("example.com"), AAAA),
        )
    }

    private fun settings() = PlainSettings(
        InetAddress.getByName("127.0.0.1"),
        Port(53, Protocol.UDP),
        Domain.of("example.com"),
        listOf(AAAA, A),
        Pair(5, SECONDS)
    )
}
