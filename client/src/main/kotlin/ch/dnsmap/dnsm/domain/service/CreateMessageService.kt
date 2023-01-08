package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.QueryTask
import ch.dnsmap.dnsm.wire.DnsOutput
import ch.dnsmap.dnsm.wire.ParserOptions

fun messageBytes(task: QueryTask, parserOptions: ParserOptions): ByteArray {
    val msg = createQuery(task.name, task.type)
    val dnsOutput =
        DnsOutput.toWire(
            parserOptions,
            msg.header,
            msg.question,
            emptyList(),
            emptyList(),
            emptyList()
        )
    return dnsOutput.message
}
