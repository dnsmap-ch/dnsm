package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.Message
import ch.dnsmap.dnsm.domain.model.Status
import ch.dnsmap.dnsm.header.HeaderRcode

fun status(resMsg: Message): Status {
    return when (resMsg.header.flags.rcode!!) {
        HeaderRcode.NO_ERROR -> Status.NO_ERROR
        HeaderRcode.FORMAT_ERROR -> Status.FORMAT_ERROR
        HeaderRcode.SERVER_FAILURE -> Status.SERVER_FAILURE
        HeaderRcode.NAME_ERROR -> Status.NAME_ERROR
        HeaderRcode.NOT_IMPLEMENTED -> Status.NOT_IMPLEMENTED
        HeaderRcode.REFUSED -> Status.REFUSED
    }
}
