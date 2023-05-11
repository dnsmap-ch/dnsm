package ch.dnsmap.dnsm.domain.service.doh

import ch.dnsmap.dnsm.domain.infrastructure.messageBytesZeroId
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsDoh
import ch.dnsmap.dnsm.wire.ParserOptions
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okio.BufferedSink

class DohPostQueryExecutor(private val settings: ClientSettingsDoh) : DohQueryExecutor {

    override
    fun execute(queries: List<QueryTask>): List<Request.Builder> {
        val parserOptionsOut = ParserOptions.Builder.builder().build()

        return queries.stream()
            .map { messageBytesZeroId(it, parserOptionsOut) }
            .map { Request.Builder().url(settings.url().toURL()).post(requestBody(it)) }
            .toList()
    }

    private fun requestBody(it: ByteArray) = object : RequestBody() {

        override fun contentLength() = it.size.toLong()

        override fun contentType() = "application/dns-message".toMediaType()

        override fun writeTo(sink: BufferedSink) {
            sink.write(it)
            println(it.joinToString(" ") { eachByte -> "%02x".format(eachByte) })
        }
    }
}
