package ch.dnsmap.dnsm.domain.model.networking

const val PORT_MIN = 1
const val PORT_MAX = 65535

enum class Protocol(val printName: String) {
    UDP("udp"),
    TCP("tcp"),
    UDP_TCP("udp/tcp")
}

class Port(val value: Int, val protocol: Protocol) {

    init {
        require(value in PORT_MIN until PORT_MAX) { "Invalid port $value" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Port

        if (value != other.value) return false
        if (protocol != other.protocol) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value
        result = 31 * result + protocol.hashCode()
        return result
    }

    override fun toString(): String {
        return "Port(value=$value, protocol=$protocol)"
    }

    fun asString(): String {
        return "$value/$protocol".lowercase()
    }
}
