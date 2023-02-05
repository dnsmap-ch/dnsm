package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.QueryResponse
import ch.dnsmap.dnsm.wire.DnsInput
import ch.dnsmap.dnsm.wire.ParserOptions

fun queryResponse(parserOptionsIn: ParserOptions, rawDns: ByteArray?): QueryResponse {
    val response = DnsInput.fromWire(parserOptionsIn, rawDns)
    val resMsg = response.message
    val ips = ipResults(resMsg)
    val logs = parserOptionsIn.log.map { it.formatted() }
    val status = status(resMsg)
    return QueryResponse(ips, logs, resMsg.question.questionType.name, status)
}
