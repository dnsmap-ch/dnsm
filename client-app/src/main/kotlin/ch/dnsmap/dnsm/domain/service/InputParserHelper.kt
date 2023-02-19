package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.QueryType
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol

fun parseInputType(rawTypeInput: String): List<QueryType> {
    return rawTypeInput.split(",")
        .asSequence()
        .filter { it.isNotBlank() }
        .map { it.uppercase() }
        .map { QueryType.valueOf(it) }
        .toList()
}

fun parsePort(rawPortInput: String): Port {
    require(rawPortInput.isNotBlank()) { "must not be blank or empty" }
    return parseSanitizedInput(rawPortInput.trim().lowercase())
}

private fun parseSanitizedInput(rawPortInput: String): Port {
    return if (rawPortInput.contains("/")) {
        val portString = rawPortInput.substringBefore("/")
        val portNumber = stringToInt(portString)
        with(rawPortInput) {
            when {
                contains("udp") -> Port(portNumber, Protocol.UDP)
                contains("tcp") -> Port(portNumber, Protocol.TCP)
                else -> throw IllegalArgumentException("invalid input $rawPortInput")
            }
        }
    } else {
        Port(stringToInt(rawPortInput), Protocol.UDP)
    }
}

private fun stringToInt(portString: String): Int {
    try {
        return Integer.parseInt(portString)
    } catch (e: NumberFormatException) {
        throw IllegalArgumentException("invalid or no port specified")
    }
}
