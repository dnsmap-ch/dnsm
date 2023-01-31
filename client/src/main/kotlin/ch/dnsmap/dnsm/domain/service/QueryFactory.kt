package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.DnsQueryClass.IN
import ch.dnsmap.dnsm.DnsQueryType.A
import ch.dnsmap.dnsm.DnsQueryType.AAAA
import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.Message
import ch.dnsmap.dnsm.Question
import ch.dnsmap.dnsm.header.Header
import ch.dnsmap.dnsm.header.HeaderBitFlags.RD
import ch.dnsmap.dnsm.header.HeaderCount
import ch.dnsmap.dnsm.header.HeaderFlags
import ch.dnsmap.dnsm.header.HeaderId
import ch.dnsmap.dnsm.header.HeaderOpcode.QUERY
import ch.dnsmap.dnsm.header.HeaderRcode.NO_ERROR

fun createQuery(name: Domain, type: QueryType): Message {
    return when (type) {
        QueryType.A -> createAQuery(name)
        QueryType.AAAA -> createAaaaQuery(name)
    }
}

private fun createAQuery(name: Domain): Message {
    return createMessage(Question(name, A, IN))
}

private fun createAaaaQuery(name: Domain): Message {
    return createMessage(Question(name, AAAA, IN))
}

private fun createMessage(question: Question): Message {
    val header = Header(
        HeaderId.ofRandom(),
        HeaderFlags(QUERY, NO_ERROR, RD),
        HeaderCount.of(1, 0, 0, 0)
    )
    return Message(header, question, null, null, null)
}
