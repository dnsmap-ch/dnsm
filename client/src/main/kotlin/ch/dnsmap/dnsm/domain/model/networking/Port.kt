package ch.dnsmap.dnsm.domain.model.networking

const val min = 1
const val max = 65535

class Port(val value: Int) {

    init {
        if (value < min || value > max) {
            throw IllegalArgumentException("Invalid port $value")
        }
    }
}