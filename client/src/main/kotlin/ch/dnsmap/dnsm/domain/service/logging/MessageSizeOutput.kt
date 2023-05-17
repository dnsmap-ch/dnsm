package ch.dnsmap.dnsm.domain.service.logging

open class MessageSizeOutput(printerFunction: (msg: String) -> Unit) : NormalOutput(printerFunction) {

    override
    fun printSizeIn(bytes: Long) {
        print("Received $bytes bytes")
    }

    override
    fun printSizeOut(bytes: Long) {
        print("Sent $bytes bytes")
    }
}
