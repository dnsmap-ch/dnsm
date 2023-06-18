package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.AnswerResultType.NO_ERROR
import ch.dnsmap.dnsm.domain.model.HttpMethod.GET
import ch.dnsmap.dnsm.domain.model.Result
import ch.dnsmap.dnsm.domain.model.Summary
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol.UDP
import ch.dnsmap.dnsm.domain.model.query.ConnectionResult
import ch.dnsmap.dnsm.domain.model.query.ConnectionResultTimed
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryResultTimed
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.query.QueryType.A
import ch.dnsmap.dnsm.domain.model.query.QueryType.AAAA
import ch.dnsmap.dnsm.domain.model.settings.ClientSettings
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsDohImpl.ClientSettingsDohImplBuilder
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsDot
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsPlain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.InetAddress
import java.net.URI
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class FormatterTest {

    private val domainExampleCom = Domain.of("example.com")

    @Test
    fun testPlainSettingsHeaderWithIpResolver() {
        val result = Formatter().header(settingsPlain("127.0.0.1"))
        assertThat(result).isEqualTo("\nQuery DNS server 127.0.0.1 over 53/udp (plain)")
    }

    @Test
    fun testPlainSettingsHeaderWithHostnameResolver() {
        val result = Formatter().header(settingsPlain())
        assertThat(result).isEqualTo("\nQuery DNS server localhost/127.0.0.1 over 53/udp (plain)")
    }

    @Test
    fun testDotSettingsHeaderWithIpResolver() {
        val result = Formatter().header(settingsDot("127.0.0.1"))
        assertThat(result).isEqualTo("\nQuery DNS server 127.0.0.1 over 853/tcp (DoT)")
    }

    @Test
    fun testDotSettingsHeaderWithHostnameResolver() {
        val result = Formatter().header(settingsDot())
        assertThat(result).isEqualTo("\nQuery DNS server localhost/127.0.0.1 over 853/tcp (DoT)")
    }

    @Test
    fun testDohPostSettingsHeaderWithIpResolver() {
        val result = Formatter().header(settingsDohPost("127.0.0.1"))
        assertThat(result).isEqualTo("\nQuery DNS server 127.0.0.1 over 443/tcp (DoH/POST)")
    }

    @Test
    fun testDohGetSettingsHeaderWithIpResolver() {
        val result = Formatter().header(settingsDohGet("127.0.0.1"))
        assertThat(result).isEqualTo("\nQuery DNS server 127.0.0.1 over 443/tcp (DoH/GET)")
    }

    @Test
    fun testDohPostSettingsHeaderWithHostnameResolver() {
        val result = Formatter().header(settingsDohPost())
        assertThat(result).isEqualTo("\nQuery DNS server localhost/127.0.0.1 over 443/tcp (DoH/POST)")
    }

    @Test
    fun testDohGetSettingsHeaderWithHostnameResolver() {
        val result = Formatter().header(settingsDohGet())
        assertThat(result).isEqualTo("\nQuery DNS server localhost/127.0.0.1 over 443/tcp (DoH/GET)")
    }

    @Test
    fun testEmptyResponseAnswer() {
        val result = Formatter().result(emptyResult())
        assertThat(result).isEqualTo("no answers available")
    }

    @Test
    fun testResponseAnswer() {
        val result = Formatter().result(result())
        assertThat(result).isEqualTo(
            """
            H: NO_ERROR
            Q: example.com. A
            A: 127.0.0.2
            H: NO_ERROR
            Q: example.com. 
            """.trimIndent()
        )
    }

    @Test
    fun testMultiResponseAnswer() {
        val result = Formatter().result(resultMulti())
        assertThat(result).isEqualTo(
            """
            H: NO_ERROR
            Q: example.com. A
            A: 127.0.0.2
            H: NO_ERROR
            Q: example.com. A
            A: 127.0.0.2
            """.trimIndent()
        )
    }

    @Test
    fun testEmptySummary() {
        val result = Formatter().summary(noAnswer())
        assertThat(result).isEqualTo("Total queries sent/answers received 1/0 in 0s")
    }

    @Test
    fun testSummary() {
        val result = Formatter().summary(answer())
        assertThat(result).isEqualTo("Total queries sent/answers received 1/1 in 1.08m")
    }

    private fun settingsPlain(): ClientSettings {
        return settingsPlain("localhost")
    }

    private fun settingsPlain(ip: String) = ClientSettingsPlain.ClientSettingsPlainBuilder()
        .resolverHost(ip)
        .resolverIp(InetAddress.getByName(ip))
        .name(domainExampleCom)
        .types(listOf(AAAA, A))
        .timeout(Pair(5, SECONDS))
        .build()

    private fun settingsDot(): ClientSettings {
        return settingsDot("localhost")
    }

    private fun settingsDot(ip: String) =
        ClientSettingsDot.ClientSettingsDotBuilder()
            .resolverHost(ip)
            .resolverIp(InetAddress.getByName(ip))
            .name(domainExampleCom)
            .types(listOf(AAAA, A))
            .timeout(Pair(5, SECONDS))
            .build()

    private fun settingsDohPost(): ClientSettings {
        return settingsDohPost("localhost")
    }

    private fun settingsDohGet(): ClientSettings {
        return settingsDohGet("localhost")
    }

    private fun settingsDohPost(ip: String) = ClientSettingsDohImplBuilder()
        .resolverHost(ip)
        .resolverIp(InetAddress.getByName(ip))
        .name(domainExampleCom)
        .types(listOf(AAAA, A))
        .timeout(Pair(5, SECONDS))
        .url(URI.create("https://example.org"))
        .build()

    private fun settingsDohGet(ip: String) = ClientSettingsDohImplBuilder()
        .resolverHost(ip)
        .resolverIp(InetAddress.getByName(ip))
        .name(domainExampleCom)
        .types(listOf(AAAA, A))
        .timeout(Pair(5, SECONDS))
        .url(URI.create("https://example.org"))
        .method(GET)
        .build()

    private fun queryResponse(): QueryResult {
        return QueryResult(domainExampleCom, listOf("127.0.0.2"), "A", NO_ERROR)
    }

    private fun emptyResult(): Result {
        val connectionResult = ConnectionResult(InetAddress.getByName("127.0.0.1"), Port(53, UDP))
        return Result(
            emptyList(),
            ConnectionResultTimed(connectionResult, Duration.ZERO),
            QueryResultTimed(emptyList(), Duration.ZERO)
        )
    }

    private fun result(): Result {
        val connectionResult = ConnectionResult(InetAddress.getByName("127.0.0.1"), Port(53, UDP))
        val queryResult = QueryResult(domainExampleCom, emptyList(), "", NO_ERROR)
        return Result(
            listOf(QueryTask(Domain.of("example.com"), A)),
            ConnectionResultTimed(connectionResult, 23.toDuration(DurationUnit.SECONDS)),
            QueryResultTimed(
                listOf(queryResponse(), queryResult),
                42.toDuration(DurationUnit.SECONDS)
            )
        )
    }

    private fun noAnswer(): Summary {
        val connectionResult = ConnectionResult(InetAddress.getByName("127.0.0.1"), Port(53, UDP))
        return Summary(
            1,
            0,
            ConnectionResultTimed(connectionResult, Duration.ZERO),
            QueryResultTimed(emptyList(), Duration.ZERO)
        )
    }

    private fun answer(): Summary {
        val connectionResult = ConnectionResult(InetAddress.getByName("127.0.0.1"), Port(53, UDP))
        val queryResult = QueryResult(domainExampleCom, emptyList(), "", NO_ERROR)
        return Summary(
            1,
            1,
            ConnectionResultTimed(connectionResult, 23.toDuration(DurationUnit.SECONDS)),
            QueryResultTimed(
                listOf(queryResponse(), queryResult),
                42.toDuration(DurationUnit.SECONDS)
            )
        )
    }

    private fun resultMulti(): Result {
        val connectionResult = ConnectionResult(InetAddress.getByName("127.0.0.1"), Port(53, UDP))
        return Result(
            listOf(
                QueryTask(Domain.of("example.com"), A),
                QueryTask(Domain.of("example.com"), AAAA)
            ),
            ConnectionResultTimed(connectionResult, 23.toDuration(DurationUnit.SECONDS)),
            QueryResultTimed(
                listOf(queryResponse(), queryResponse()),
                42.toDuration(DurationUnit.SECONDS)
            )
        )
    }
}
