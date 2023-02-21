package ch.dnsmap.dnsm.domain.service.dot

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
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetAddress
import java.net.SocketException
import java.nio.ByteBuffer
import java.util.concurrent.CountDownLatch
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class DotService(private val settings: ClientSettings) : QueryService {

    override
    fun query(
        resolverHost: InetAddress,
        resolverPort: Port,
        queries: List<QueryTask>
    ): List<QueryResult> {
        val resultList = mutableListOf<QueryResult>()
        val latch = CountDownLatch(1)
        val pairOfDisposable = createSocket(resolverHost, resolverPort).use { s ->
            val input = DataInputStream(s.inputStream)
            val output = DataOutputStream(s.outputStream)

            val receiver = startListener(input, queries, resultList, latch)
            val sender = startSender(output, queries)
            latch.await(settings.timeout().first, settings.timeout().second)
            Pair(receiver, sender)
        }
        pairOfDisposable.first.dispose()
        pairOfDisposable.second.dispose()
        return resultList
    }

    private fun createSocket(host: InetAddress, port: Port): SSLSocket {
        val socket: SSLSocket =
            SSLSocketFactory.getDefault().createSocket(host, port.port) as SSLSocket
//        socket.setEnabledProtocols(protocols);
//        socket.setEnabledCipherSuites(cipher_suites);
        return socket
    }

    private fun startListener(
        input: DataInputStream,
        queries: List<QueryTask>,
        resultList: MutableList<QueryResult>,
        latch: CountDownLatch
    ): Disposable {
        val parserOptionsIn = ParserOptions.Builder.builder().setDomainLabelTolerant().build()
        var counter = 0
        var stayInLoop = true
        return Observable.create { emitter ->
            while (stayInLoop && input.available() != -1) {
                try {
                    val length = ByteBuffer.wrap(input.readNBytes(2)).short
                    val dataBuffer = ByteArray(length.toInt())
                    input.readNBytes(dataBuffer, 0, length.toInt())
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
            .map { rawDns -> queryResponse(parserOptionsIn, rawDns) }
            .subscribeOn(Schedulers.io())
            .subscribe({ msg ->
                resultList.add(msg)
                if (resultList.size == queries.size) {
                    latch.countDown()
                }
            }, { e -> println(e.message) })
    }

    private fun startSender(output: DataOutputStream, queries: List<QueryTask>): Disposable {
        val parserOptionsOut = ParserOptions.Builder.builder().setTcp().build()
        return Observable.fromIterable(queries)
            .map { messageBytes(it, parserOptionsOut) }
            .subscribe { msg -> output.write(msg) }
    }
}
