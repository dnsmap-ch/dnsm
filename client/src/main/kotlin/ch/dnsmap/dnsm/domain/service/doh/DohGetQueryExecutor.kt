package ch.dnsmap.dnsm.domain.service.doh

import ch.dnsmap.dnsm.domain.infrastructure.messageBytesZeroId
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsDoh
import ch.dnsmap.dnsm.wire.ParserOptions
import okhttp3.Request
import java.net.URL
import java.util.Base64.getUrlEncoder

class DohGetQueryExecutor(private val settings: ClientSettingsDoh) : DohQueryExecutor {

    override
    fun execute(queries: List<QueryTask>): List<Request.Builder> {
        val parserOptionsOut = ParserOptions.Builder.builder().build()
        val encoder = getUrlEncoder()

        return queries.stream()
            .map { messageBytesZeroId(it, parserOptionsOut) }
            .map {
                val data = encoder.encodeToString(it)
                println(data)
                data
            }
            .map { Request.Builder().url(configureUrl(it)).get() }
            .toList()
    }

    private fun configureUrl(it: String): URL = settings.url().resolve("?dns=$it").toURL()
}
