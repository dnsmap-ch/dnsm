package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol

fun parseInputName(input: String): Domain {
    return Domain.of(input)
}

fun parseInputType(input: String): List<QueryType> {
    return input.split(",")
        .map { it.uppercase() }
        .map { QueryType.valueOf(it) }
        .toList()
}

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
                contains("udp") -> Port(portNumber, Protocol.UDP)
                contains("tcp") -> Port(portNumber, Protocol.TCP)
                else -> throw IllegalArgumentException("invalid input $sanitized")
            }
        }
    } else {
        Port(stringToInt(sanitized), Protocol.UDP)
    }
}

private fun stringToInt(portString: String): Int {
    try {
        return Integer.parseInt(portString)
    } catch (e: NumberFormatException) {
        throw IllegalArgumentException("invalid or no port specified")
    }
}
