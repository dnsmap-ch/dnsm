package ch.dnsmap.dnsm.infrastructure

import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol.TCP
import ch.dnsmap.dnsm.domain.model.networking.Protocol.UDP
import ch.dnsmap.dnsm.domain.model.networking.Protocol.UDP_TCP
import java.lang.Integer.parseInt

fun parsePort(portString: String): Port {
    require(portString.isNotBlank()) { "must not be blank or empty" }
    return parseSanitizedInput(portString.trim().lowercase())
}

private fun parseSanitizedInput(sanitized: String): Port {
    return if (sanitized.contains("/")) {
        val portString = sanitized.substringBefore("/")
        val portNumber = stringToInt(portString)
        with(sanitized) {
            when {
                contains("udp") && contains("tcp") -> Port(portNumber, UDP_TCP)
                contains("udp") -> Port(portNumber, UDP)
                contains("tcp") -> Port(portNumber, TCP)
                else -> throw IllegalArgumentException("invalid input $sanitized")
            }
        }
    } else {
        Port(stringToInt(sanitized), UDP)
    }
}

private fun stringToInt(portString: String): Int {
    try {
        return parseInt(portString)
    } catch (e: NumberFormatException) {
        throw IllegalArgumentException("invalid or no port specified")
    }
}
