package ch.dnsmap.dnsm.domain.model.query

import ch.dnsmap.dnsm.Message
import ch.dnsmap.dnsm.domain.model.AnswerResultType
import ch.dnsmap.dnsm.header.HeaderRcode
import ch.dnsmap.dnsm.record.ResourceRecordA
import ch.dnsmap.dnsm.record.ResourceRecordAaaa

fun queryResponse(message: Message): QueryResult {
    val query = message.question.questionName
    val ips = ipResults(message)
    val status = status(message)
    return QueryResult(query, ips, message.question.questionType.name, status)
}

private fun ipResults(msg: Message): List<String> {
    return msg.answer.map {
        when (it) {
            is ResourceRecordA -> it.ip4.ip.hostAddress
            is ResourceRecordAaaa -> it.ip6.ip.hostAddress
            else -> {
                throw NotImplementedError("Missing implementation")
            }
        }
    }.toList()
}

private fun status(resMsg: Message): AnswerResultType {
    return when (resMsg.header.flags.rcode!!) {
        HeaderRcode.NO_ERROR -> AnswerResultType.NO_ERROR
        HeaderRcode.FORMAT_ERROR -> AnswerResultType.FORMAT_ERROR
        HeaderRcode.SERVER_FAILURE -> AnswerResultType.SERVER_FAILURE
        HeaderRcode.NAME_ERROR -> AnswerResultType.NAME_ERROR
        HeaderRcode.NOT_IMPLEMENTED -> AnswerResultType.NOT_IMPLEMENTED
        HeaderRcode.REFUSED -> AnswerResultType.REFUSED
    }
}
