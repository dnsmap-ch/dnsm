package ch.dnsmap.dnsm.domain.service.doh

import ch.dnsmap.dnsm.domain.infrastructure.messageBytesZeroId
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsDoh
import ch.dnsmap.dnsm.domain.service.logging.Output
import ch.dnsmap.dnsm.domain.service.parser.DnsMessageParser
import okhttp3.Request
import java.net.URI
import java.util.Base64.getUrlEncoder

class DohGetQueryExecutor(
    private val settings: ClientSettingsDoh,
    private val parser: DnsMessageParser,
    private val out: Output
) : DohQueryExecutor {

    override
    fun execute(queries: List<QueryTask>): List<Request.Builder> {
        val encoder = getUrlEncoder()

        return queries.stream()
            .map { messageBytesZeroId(it) }
            .map {
                val dnsMessage = parser.parseMessageToBytes(it)

                out.printSizeOut(dnsMessage.size.toLong())
                out.printMessage(it)
                out.printRawMessage(dnsMessage)

                dnsMessage
            }
            .map { encoder.encodeToString(it) }
            .map { buildUrl(it) }
            .toList()
    }

    private fun buildUrl(it: String?): Request.Builder {
        val uri = URI.create(settings.url().toString() + "?dns=$it")
        return Request.Builder().url(uri.toURL()).get()
    }
}
