package ch.dnsmap.dnsm.domain.service.doh

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.query.QueryType.A
import ch.dnsmap.dnsm.domain.model.query.QueryType.AAAA
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsDohImpl
import ch.dnsmap.dnsm.domain.service.logging.SilentOutput
import ch.dnsmap.dnsm.domain.service.parser.DnsMessageParserImpl
import ch.dnsmap.dnsm.wire.ParserOptions
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI

private val QUERY_EXAMPLE_ORG_A = QueryTask(Domain.of("example.org"), A)
private val QUERY_EXAMPLE_ORG_AAAA = QueryTask(Domain.of("example.org"), AAAA)
private const val QUERY_URL = "https://doh.example.org/"

class DohPostQueryExecutorTest {

    private val settings = ClientSettingsDohImpl.ClientSettingsDohImplBuilder()
        .url(URI.create(QUERY_URL))
        .build()
    private val parser = DnsMessageParserImpl(ParserOptions.Builder.builder().build())
    private val dohPostQueryExtractor =
        DohPostQueryExecutor(settings, parser, SilentOutput(::println))

    @Test
    fun testEmptyQueryTask() {
        val result = dohPostQueryExtractor.execute(emptyList())
        assertThat(result).isEmpty()
    }

    @Test
    fun testSingleQueryTask() {
        val result = dohPostQueryExtractor.execute(listOf(QUERY_EXAMPLE_ORG_AAAA))
        assertThat(result).hasSize(1)
        assertRequest(result[0].build())
    }

    @Test
    fun testMultiQueryTask() {
        val result =
            dohPostQueryExtractor.execute(listOf(QUERY_EXAMPLE_ORG_AAAA, QUERY_EXAMPLE_ORG_A))
        assertThat(result).hasSize(2)
        assertRequest(result[0].build())
        assertRequest(result[1].build())
    }

    private fun assertRequest(result: Request) {
        assertThat(result).satisfies({
            assertThat(it.url.toUrl()).isEqualTo(
                URI.create("https://doh.example.org/").toURL()
            )
            assertThat(it.method).isEqualTo("POST")
            assertThat(it.body!!.contentType()!!).isEqualTo("application/dns-message".toMediaType())
            assertThat(it.body!!.contentLength()).isEqualTo(29L)
        })
    }
}
