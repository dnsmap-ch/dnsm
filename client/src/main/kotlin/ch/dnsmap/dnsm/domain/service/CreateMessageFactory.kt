package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.DnsQueryClass
import ch.dnsmap.dnsm.DnsQueryType
import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.Message
import ch.dnsmap.dnsm.Question
import ch.dnsmap.dnsm.domain.model.QueryTask
import ch.dnsmap.dnsm.domain.model.QueryType
import ch.dnsmap.dnsm.header.Header
import ch.dnsmap.dnsm.header.HeaderBitFlags
import ch.dnsmap.dnsm.header.HeaderCount
import ch.dnsmap.dnsm.header.HeaderFlags
import ch.dnsmap.dnsm.header.HeaderId
import ch.dnsmap.dnsm.header.HeaderOpcode
import ch.dnsmap.dnsm.header.HeaderRcode
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

private fun createQuery(name: Domain, type: QueryType): Message {
    return when (type) {
        QueryType.A -> createAQuery(name)
        QueryType.AAAA -> createAaaaQuery(name)
    }
}

private fun createAQuery(name: Domain): Message {
    return createMessage(Question(name, DnsQueryType.A, DnsQueryClass.IN))
}

private fun createAaaaQuery(name: Domain): Message {
    return createMessage(Question(name, DnsQueryType.AAAA, DnsQueryClass.IN))
}

private fun createMessage(question: Question): Message {
    val header = Header(
        HeaderId.ofRandom(),
        HeaderFlags(HeaderOpcode.QUERY, HeaderRcode.NO_ERROR, HeaderBitFlags.RD),
        HeaderCount.of(1, 0, 0, 0)
    )
    return Message(header, question, null, null, null)
}
