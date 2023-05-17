package ch.dnsmap.dnsm.domain.service.doh

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.HttpMethod.GET
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.query.QueryType.A
import ch.dnsmap.dnsm.domain.model.query.QueryType.AAAA
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsDohImpl
import ch.dnsmap.dnsm.domain.service.logging.SilentOutput
import ch.dnsmap.dnsm.domain.service.parser.DnsMessageParserImpl
import ch.dnsmap.dnsm.wire.ParserOptions
import okhttp3.Request
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI

private val QUERY_EXAMPLE_ORG_A = QueryTask(Domain.of("example.org"), A)
private val QUERY_EXAMPLE_ORG_AAAA = QueryTask(Domain.of("example.org"), AAAA)

private const val QUERY_URL = "https://doh.example.org/dns-query?dns="

class DohGetQueryExecutorTest {

    private val settings = ClientSettingsDohImpl.ClientSettingsDohImplBuilder()
        .method(GET)
        .url(URI.create(QUERY_URL))
        .build()
    private val parser = DnsMessageParserImpl(ParserOptions.Builder.builder().build())
    private val dohGetQueryExtractor =
        DohGetQueryExecutor(settings, parser, SilentOutput(::println))

    @Test
    fun testEmptyQueryTask() {
        val result = dohGetQueryExtractor.execute(emptyList())
        assertThat(result).isEmpty()
    }

    @Test
    fun testSingleQueryTask() {
        val result = dohGetQueryExtractor.execute(listOf(QUERY_EXAMPLE_ORG_AAAA))
        assertThat(result).hasSize(1)
        assertRequest(result[0].build(), "AAABAAABAAAAAAAAB2V4YW1wbGUDb3JnAAAcAAE=")
    }

    @Test
    fun testMultiQueryTask() {
        val result =
            dohGetQueryExtractor.execute(listOf(QUERY_EXAMPLE_ORG_AAAA, QUERY_EXAMPLE_ORG_A))
        assertThat(result).hasSize(2)
        assertRequest(result[0].build(), "AAABAAABAAAAAAAAB2V4YW1wbGUDb3JnAAAcAAE=")
        assertRequest(result[1].build(), "AAABAAABAAAAAAAAB2V4YW1wbGUDb3JnAAABAAE=")
    }

    private fun assertRequest(result: Request, base64Query: String) {
        assertThat(result).satisfies({
            assertThat(it.url.toUrl()).isEqualTo(URI.create("$QUERY_URL$base64Query").toURL())
            assertThat(it.method).isEqualTo("GET")
            assertThat(it.body).isNull()
        })
    }
}
