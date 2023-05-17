package ch.dnsmap.dnsm.domain.service.logging

import ch.dnsmap.dnsm.Message
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

abstract class Output(private val printerFunction: (msg: String) -> Unit) {

    private val mutex = Mutex()

    abstract fun printNormal(msg: String)

    abstract fun printSizeIn(bytes: Long)
    abstract fun printSizeOut(bytes: Long)

    abstract fun printMessage(msg: String)
    abstract fun printMessage(msg: Message)

    abstract fun printDebug(msg: String)
    abstract fun printRawMessage(msg: ByteArray)

    fun print(msg: String) = runBlocking {
        mutex.withLock {
            printerFunction(msg)
        }
    }
}
