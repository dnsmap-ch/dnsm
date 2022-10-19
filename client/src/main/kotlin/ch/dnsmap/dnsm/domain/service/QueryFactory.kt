package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.*
import ch.dnsmap.dnsm.DnsQueryClass.IN
import ch.dnsmap.dnsm.DnsQueryType.*
import ch.dnsmap.dnsm.header.*
import ch.dnsmap.dnsm.header.HeaderBitFlags.RD
import ch.dnsmap.dnsm.header.HeaderOpcode.QUERY
import ch.dnsmap.dnsm.header.HeaderRcode.NO_ERROR
import ch.dnsmap.dnsm.infrastructure.PlainCommand
import ch.dnsmap.dnsm.infrastructure.QueryType

fun createQuery(name: String, type: QueryType): Message {
    return when (type) {
        QueryType.A -> createAQuery(name)
        QueryType.AAAA -> createAaaaQuery(name)
    }
}

private fun createAQuery(name: String): Message {
    return createMessage(Question(Domain.of(name), A, IN))
}

private fun createAaaaQuery(name: String): Message {
    return createMessage(Question(Domain.of(name), AAAA, IN))
}

private fun createMessage(question: Question): Message {
    val header = Header(
        HeaderId.ofRandom(), HeaderFlags(QUERY, NO_ERROR, RD),
        HeaderCount.of(1, 0, 0, 0)
    )
    return Message(header, question, null, null, null)
}
