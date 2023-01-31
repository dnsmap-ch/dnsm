package ch.dnsmap.dnsm.domain.model

import ch.dnsmap.dnsm.domain.model.networking.Protocol

class PrintableHeader(private val settings: PlainSettings, private val protocol: Protocol) {

    fun asString(): String {
        return "Query ${settings.resolverHost.hostAddress}:${settings.resolverPort.value}/${protocol.printName}"
    }
}
