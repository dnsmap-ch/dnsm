package ch.dnsmap.dnsm.domain.model.networking

const val PORT_MIN = 1
const val PORT_MAX = 65535

enum class Protocol(val printName: String) {
    UDP("udp"),
    TCP("tcp"),
}

data class Port(val port: Int, val protocol: Protocol) {

    init {
        require(port in PORT_MIN..PORT_MAX) { "Invalid port $port" }
    }

    fun asString(): String {
        return "$port/$protocol".lowercase()
    }
}
