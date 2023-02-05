package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.PlainSettings
import ch.dnsmap.dnsm.domain.model.QueryTask
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol.UDP
import ch.dnsmap.dnsm.domain.service.QueryType.A
import ch.dnsmap.dnsm.domain.service.QueryType.AAAA
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.InetAddress

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

    private fun settings() = PlainSettings(
        InetAddress.getLocalHost(),
        Port(53, UDP),
        domainExampleCom,
        listOf(AAAA, A)
    )
}
