package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.Message
import ch.dnsmap.dnsm.record.ResourceRecordA
import ch.dnsmap.dnsm.record.ResourceRecordAaaa

fun ipResults(msg: Message): List<String> {
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