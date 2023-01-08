package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.Message
import ch.dnsmap.dnsm.domain.model.Status

fun status(resMsg: Message): Status {
    val status = when (resMsg.header.flags.rcode.ordinal) {
        0 -> Status.NO_ERROR
        1 -> Status.FORMAT_ERROR
        2 -> Status.SERVER_FAILURE
        3 -> Status.NAME_ERROR
        4 -> Status.NOT_IMPLEMENTED
        5 -> Status.REFUSED
        else -> {
            throw IllegalStateException("Invalid return code")
        }
    }
    return status
}