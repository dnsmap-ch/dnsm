package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.QueryResponse
import ch.dnsmap.dnsm.domain.model.QueryTask
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.wire.ParserOptions
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.util.concurrent.CountDownLatch

class TcpService(private val resolverHost: InetAddress, private val resolverPort: Port) :
    SimpleService {

    override
    fun query(queries: List<QueryTask>): List<QueryResponse> {
        val socket = Socket(resolverHost, resolverPort.value)
        val input = DataInputStream(socket.getInputStream())
        val output = DataOutputStream(socket.getOutputStream())

        val resultList = mutableListOf<QueryResponse>()
        val latch = CountDownLatch(1)
        val receiver = startListener(input, queries, resultList, latch)
        val sender = startSender(output, queries)

        latch.await()
        receiver.dispose()
        sender.dispose()

        input.close()
        output.close()
        socket.close()
        return resultList
    }

    private fun startListener(
        input: DataInputStream,
        queries: List<QueryTask>,
        resultList: MutableList<QueryResponse>,
        latch: CountDownLatch
    ): Disposable {
        val parserOptionsIn = ParserOptions.Builder.builder().setDomainLabelTolerant().build()
        var counter = 0
        return Observable.create { emitter ->
            while (input.available() != -1) {
                val length = ByteBuffer.wrap(input.readNBytes(2)).short
                val dataBuffer = ByteArray(length.toInt())
                input.readNBytes(dataBuffer, 0, length.toInt())
                emitter.onNext(dataBuffer)
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
    }

    private fun startSender(output: DataOutputStream, queries: List<QueryTask>): Disposable {
        val parserOptionsOut = ParserOptions.Builder.builder().setTcp().build()
        return Observable.fromIterable(queries)
            .map { messageBytes(it, parserOptionsOut) }
            .subscribe { msg -> output.write(msg) }
    }
}