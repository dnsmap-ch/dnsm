package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.PlainSettings
import ch.dnsmap.dnsm.domain.model.QueryResponse
import ch.dnsmap.dnsm.domain.model.QueryTask
import ch.dnsmap.dnsm.domain.model.Result
import ch.dnsmap.dnsm.domain.model.Status
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol.UDP
import ch.dnsmap.dnsm.domain.service.QueryType.A
import ch.dnsmap.dnsm.domain.service.QueryType.AAAA
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.InetAddress
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class PrinterTest {

    private val domainExampleCom = Domain.of("example.com")

    @Test
    fun testHeader() {
        val result = Printer().header(settings())
        assertThat(result).isEqualTo("Query 127.0.0.1:53/udp")
    }

    @Test
    fun testEmptyResponseAnswer() {
        val result = Printer().answer(settings(), emptyList())
        assertThat(result.size).isEqualTo(1)
        assertThat(result[0]).isEqualTo("no answers available")
    }

    @Test
    fun testResponseAnswer() {
        val result = Printer().answer(settings(), listOf(queryResponse()))
        assertThat(result.size).isEqualTo(3)
        assertThat(result[0]).isEqualTo("H: NO_ERROR")
        assertThat(result[1]).isEqualTo("Q: example.com. A -> 127.0.0.1:53/udp")
        assertThat(result[2]).isEqualTo("A: 127.0.0.2")
    }

    @Test
    fun testMultiResponseAnswer() {
        val result = Printer().answer(settings(), listOf(queryResponse(), queryResponse()))
        assertThat(result.size).isEqualTo(6)
        assertThat(result[0]).isEqualTo("H: NO_ERROR")
        assertThat(result[1]).isEqualTo("Q: example.com. A -> 127.0.0.1:53/udp")
        assertThat(result[2]).isEqualTo("A: 127.0.0.2")
        assertThat(result[3]).isEqualTo("H: NO_ERROR")
        assertThat(result[4]).isEqualTo("Q: example.com. A -> 127.0.0.1:53/udp")
        assertThat(result[5]).isEqualTo("A: 127.0.0.2")
    }

    @Test
    fun testEmptyIpResponseAnswer() {
        val result = Printer().answer(settings(), listOf(emptyIpQueryResponse()))
        assertThat(result.size).isEqualTo(2)
        assertThat(result[0]).isEqualTo("H: NO_ERROR")
        assertThat(result[1]).isEqualTo("Q: example.com. A -> 127.0.0.1:53/udp")
    }

    @Test
    fun testEmptySummary() {
        val result = Printer().summary(Result.emptyResult())
        assertThat(result).isEqualTo("Total queries sent/answers received 0/0 in 0s")
    }

    @Test
    fun testNonEmptySummary() {
        val result = Printer().summary(result())
        assertThat(result).isEqualTo("Total queries sent/answers received 1/2 in 23.00s")
    }

    private fun settings() = PlainSettings(
        InetAddress.getByName("127.0.0.1"),
        Port(53, UDP),
        domainExampleCom,
        listOf(AAAA, A)
    )

    private fun emptyIpQueryResponse(): QueryResponse {
        return QueryResponse(emptyList(), emptyList(), "A", Status.NO_ERROR)
    }

    private fun queryResponse(): QueryResponse {
        return QueryResponse(listOf("127.0.0.2"), emptyList(), "A", Status.NO_ERROR)
    }

    private fun result(): Result {
        return Result(
            23.toDuration(DurationUnit.SECONDS),
            listOf(queryResponse(), queryResponse()),
            listOf(QueryTask(Domain.of("example.com"), A))
        )
    }
}
