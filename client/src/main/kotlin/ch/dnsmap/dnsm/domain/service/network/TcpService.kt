package ch.dnsmap.dnsm.domain.service.network

import ch.dnsmap.dnsm.domain.infrastructure.messageBytes
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.query.queryResponse
import ch.dnsmap.dnsm.domain.model.settings.ClientSettings
import ch.dnsmap.dnsm.domain.service.logging.Output
import ch.dnsmap.dnsm.domain.service.parser.DnsMessageParser
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.net.SocketException
import java.util.concurrent.CountDownLatch

class TcpService(
    private val settings: ClientSettings,
    private val socket: Socket,
    private val parserOutput: DnsMessageParser,
    private val parserInput: DnsMessageParser,
    private val out: Output
) {

    fun query(queries: List<QueryTask>): List<QueryResult> {
        val resultList = mutableListOf<QueryResult>()
        val latch = CountDownLatch(1)
        val pairOfDisposable = socket.use { s ->
            val input = DataInputStream(s.getInputStream())
            val output = DataOutputStream(s.getOutputStream())

            val receiver = startListener(input, queries, resultList, latch)
            val sender = startSender(output, queries)
            latch.await(settings.timeout().first, settings.timeout().second)
            Pair(receiver, sender)
        }
        pairOfDisposable.first.dispose()
        pairOfDisposable.second.dispose()
        return resultList
    }

    private fun startListener(
        input: DataInputStream,
        queries: List<QueryTask>,
        resultList: MutableList<QueryResult>,
        latch: CountDownLatch
    ): Disposable {
        var counter = 0
        var stayInLoop = true
        return Observable.create { emitter ->
            while (stayInLoop && input.available() != -1) {
                try {
                    val length = input.readShort()
                    val dataBuffer = input.readNBytes(length.toInt())
                    emitter.onNext(dataBuffer)
                } catch (_: SocketException) {
                    stayInLoop = false
                }
                counter++
                if (counter == queries.size) {
                    emitter.onComplete()
                    stayInLoop = false
                }
            }
        }
            .map { rawDnsMessage ->
                val parsedMessage = parserInput.parseBytesToMessage(rawDnsMessage)

                out.printSizeIn(rawDnsMessage.size.toLong())
                out.printMessage(parsedMessage.first)
                out.printRawMessage(rawDnsMessage)

                queryResponse(parsedMessage.first)
            }
            .subscribeOn(Schedulers.io())
            .subscribe { msg ->
                resultList.add(msg)
                if (resultList.size == queries.size) {
                    latch.countDown()
                }
            }
    }

    private fun startSender(output: DataOutputStream, queries: List<QueryTask>): Disposable {
        return Observable.fromIterable(queries)
            .map { messageBytes(it) }
            .map {
                val dnsMessage = parserOutput.parseMessageToBytes(it)

                out.printSizeOut(dnsMessage.size.toLong())
                out.printMessage(it)
                out.printRawMessage(dnsMessage)

                dnsMessage
            }
            .subscribe { msg -> output.write(msg) }
    }
}
