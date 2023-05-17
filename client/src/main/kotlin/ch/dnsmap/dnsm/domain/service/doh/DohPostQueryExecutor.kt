package ch.dnsmap.dnsm.domain.service.doh

import ch.dnsmap.dnsm.domain.infrastructure.messageBytesZeroId
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsDoh
import ch.dnsmap.dnsm.domain.service.logging.Output
import ch.dnsmap.dnsm.domain.service.parser.DnsMessageParser
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okio.BufferedSink

class DohPostQueryExecutor(
    private val settings: ClientSettingsDoh,
    private val parser: DnsMessageParser,
    private val out: Output
) : DohQueryExecutor {

    override
    fun execute(queries: List<QueryTask>): List<Request.Builder> {
        return queries.stream()
            .map { messageBytesZeroId(it) }
            .map {
                val dnsMessage = parser.parseMessageToBytes(it)

                out.printSizeOut(dnsMessage.size.toLong())
                out.printMessage(it)
                out.printRawMessage(dnsMessage)

                dnsMessage
            }
            .map { Request.Builder().url(settings.url().toURL()).post(requestBody(it)) }
            .toList()
    }

    private fun requestBody(it: ByteArray) = object : RequestBody() {

        override fun contentLength() = it.size.toLong()

        override fun contentType() = "application/dns-message".toMediaType()

        override fun writeTo(sink: BufferedSink) {
            sink.write(it)
        }
    }
}
