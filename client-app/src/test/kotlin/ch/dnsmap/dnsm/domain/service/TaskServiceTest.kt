package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.ClientSettingsPlain
import ch.dnsmap.dnsm.domain.model.QueryTask
import ch.dnsmap.dnsm.domain.model.QueryType.A
import ch.dnsmap.dnsm.domain.model.QueryType.AAAA
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol.UDP
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.InetAddress
import java.util.concurrent.TimeUnit.SECONDS

class TaskServiceTest {

    private val domainExampleCom = Domain.of("example.com")

    @Test
    fun testQueryTasks() {
        val settings = settings()
        val result = TaskService().queryTasks(settings)
        assertThat(result).containsExactlyInAnyOrder(
            QueryTask(domainExampleCom, A),
            QueryTask(domainExampleCom, AAAA)
        )
    }

    private fun settings() = ClientSettingsPlain(
        InetAddress.getLocalHost(),
        Port(53, UDP),
        domainExampleCom,
        listOf(AAAA, A),
        Pair(5, SECONDS)
    )
}
