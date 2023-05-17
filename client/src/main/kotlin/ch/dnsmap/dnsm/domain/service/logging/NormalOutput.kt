package ch.dnsmap.dnsm.domain.service.logging

import ch.dnsmap.dnsm.Message

open class NormalOutput(printerFunction: (msg: String) -> Unit) : Output(printerFunction) {

    override
    fun printNormal(msg: String) {
        print(msg)
    }

    override
    fun printSizeIn(bytes: Long) {
        // noop
    }

    override
    fun printSizeOut(bytes: Long) {
        // noop
    }

    override
    fun printMessage(msg: String) {
        // noop
    }

    override
    fun printMessage(msg: Message) {
        // noop
    }

    override
    fun printDebug(msg: String) {
        // noop
    }

    override
    fun printRawMessage(msg: ByteArray) {
        // noop
    }
}
