package ch.dnsmap.dnsm.domain.infrastructure

import ch.dnsmap.dnsm.DnsQueryClass
import ch.dnsmap.dnsm.DnsQueryType
import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.Message
import ch.dnsmap.dnsm.Question
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.query.QueryType
import ch.dnsmap.dnsm.header.Header
import ch.dnsmap.dnsm.header.HeaderBitFlags
import ch.dnsmap.dnsm.header.HeaderCount
import ch.dnsmap.dnsm.header.HeaderFlags
import ch.dnsmap.dnsm.header.HeaderId
import ch.dnsmap.dnsm.header.HeaderOpcode
import ch.dnsmap.dnsm.header.HeaderRcode

fun messageBytes(task: QueryTask): Message {
    return createQuery(task.name, task.type, header(HeaderId.ofRandom()))
}

fun messageBytesZeroId(task: QueryTask): Message {
    return createQuery(task.name, task.type, header(HeaderId.ofZero()))
}

private fun createQuery(name: Domain, type: QueryType, header: Header): Message {
    return when (type) {
        QueryType.A -> createAQuery(name, header)
        QueryType.AAAA -> createAaaaQuery(name, header)
    }
}

private fun createAQuery(name: Domain, header: Header): Message {
    return createMessage(
        header,
        Question(name, DnsQueryType.A, DnsQueryClass.IN)
    )
}

private fun createAaaaQuery(name: Domain, header: Header): Message {
    return createMessage(
        header,
        Question(name, DnsQueryType.AAAA, DnsQueryClass.IN)
    )
}

private fun createMessage(header: Header, question: Question): Message {
    return Message(header, question, null, null, null)
}

private fun header(headerId: HeaderId): Header {
    return Header(
        headerId,
        HeaderFlags(HeaderOpcode.QUERY, HeaderRcode.NO_ERROR, HeaderBitFlags.RD),
        HeaderCount.of(1, 0, 0, 0)
    )
}
