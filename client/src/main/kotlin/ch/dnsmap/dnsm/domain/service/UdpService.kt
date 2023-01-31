package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.QueryResponse
import ch.dnsmap.dnsm.domain.model.QueryTask
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.wire.DnsInput
import ch.dnsmap.dnsm.wire.ParserOptions
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.CountDownLatch

private const val BUFFER_SIZE = 4096

class UdpService : QueryService {

    override
    fun query(
        resolverHost: InetAddress,
        resolverPort: Port,
        queries: List<QueryTask>
    ): List<QueryResponse> {
        val socket = DatagramSocket()
        val resultList = mutableListOf<QueryResponse>()
        val latch = CountDownLatch(1)
        val receiver = startListener(socket, queries, resultList, latch)
        val sender = startSender(resolverHost, resolverPort, socket, queries)

        latch.await()
        receiver.dispose()
        sender.dispose()
        socket.close()
        return resultList
    }

    private fun startListener(
        socket: DatagramSocket,
        queries: List<QueryTask>,
        resultList: MutableList<QueryResponse>,
        latch: CountDownLatch
    ): Disposable {
        val parserOptionsIn = ParserOptions.Builder.builder().setDomainLabelTolerant().build()
        var counter = 0
        val disposable = Observable.create { emitter ->
            while (true) {
                val buf = ByteArray(BUFFER_SIZE)
                val packetIn = DatagramPacket(buf, buf.size)
                socket.receive(packetIn)
                emitter.onNext(packetIn.data)
                counter++
                if (counter == queries.size) {
                    emitter.onComplete()
                    break
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
                DatagramPacket(rawBytes, rawBytes.size, resolverHost, resolverPort.value)
            }
            .subscribe { msg -> socket.send(msg) }
    }
}

fun queryResponse(parserOptionsIn: ParserOptions, rawDns: ByteArray?): QueryResponse {
    val response = DnsInput.fromWire(parserOptionsIn, rawDns)
    val resMsg = response.message
    val ips = ipResults(resMsg)
    val logs = parserOptionsIn.log.map { it.formatted() }
    val status = status(resMsg)
    return QueryResponse(ips, logs, resMsg.answer[0].dnsType.name, status)
}
