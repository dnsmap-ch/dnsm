package ch.dnsmap.dnsm.domain.service.plain

import ch.dnsmap.dnsm.domain.infrastructure.messageBytes
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.query.queryResponse
import ch.dnsmap.dnsm.domain.model.settings.ClientSettings
import ch.dnsmap.dnsm.domain.service.QueryService
import ch.dnsmap.dnsm.wire.ParserOptions
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException
import java.util.concurrent.CountDownLatch

private const val BUFFER_SIZE = 4096

class PlainUdpService(private val settings: ClientSettings) : QueryService {

    override
    fun query(
        resolverHost: InetAddress,
        resolverPort: Port,
        queries: List<QueryTask>
    ): List<QueryResult> {
        val resultList = mutableListOf<QueryResult>()
        val latch = CountDownLatch(1)
        val pairOfDisposable = DatagramSocket().use { s ->
            val receiver = startListener(s, queries, resultList, latch)
            val sender = startSender(resolverHost, resolverPort, s, queries)
            latch.await(settings.timeout().first, settings.timeout().second)
            Pair(receiver, sender)
        }

        pairOfDisposable.first.dispose()
        pairOfDisposable.second.dispose()
        return resultList
    }

    private fun startListener(
        socket: DatagramSocket,
        queries: List<QueryTask>,
        resultList: MutableList<QueryResult>,
        latch: CountDownLatch
    ): Disposable {
        val parserOptionsIn = ParserOptions.Builder.builder().setDomainLabelTolerant().build()
        var counter = 0
        var stayInLoop = true
        val disposable = Observable.create { emitter ->
            while (stayInLoop) {
                val buf = ByteArray(BUFFER_SIZE)
                val packetIn = DatagramPacket(buf, buf.size)
                try {
                    socket.receive(packetIn)
                } catch (_: SocketException) {
                    stayInLoop = false
                }
                emitter.onNext(packetIn.data)
                counter++
                if (counter == queries.size) {
                    emitter.onComplete()
                    stayInLoop = false
                }
            }
        }
            .map { rawDns -> queryResponse(parserOptionsIn, rawDns) }
            .subscribeOn(Schedulers.io())
            .subscribe { msg ->
                resultList.add(msg)
                if (resultList.size == queries.size) {
                    latch.countDown()
                }
            }
        return disposable
    }

    private fun startSender(
        resolverHost: InetAddress,
        resolverPort: Port,
        socket: DatagramSocket,
        queries: List<QueryTask>
    ): Disposable {
        val parserOptionsOut = ParserOptions.Builder.builder().build()
        return Observable.fromIterable(queries)
            .map { messageBytes(it, parserOptionsOut) }
            .map { rawBytes ->
                DatagramPacket(rawBytes, rawBytes.size, resolverHost, resolverPort.port)
            }
            .subscribe { msg -> socket.send(msg) }
    }
}
