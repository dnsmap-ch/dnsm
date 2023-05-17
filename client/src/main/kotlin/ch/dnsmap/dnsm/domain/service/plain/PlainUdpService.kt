package ch.dnsmap.dnsm.domain.service.plain

import ch.dnsmap.dnsm.domain.infrastructure.messageBytes
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.query.ConnectionResult
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.query.queryResponse
import ch.dnsmap.dnsm.domain.model.settings.ClientSettings
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsPlain
import ch.dnsmap.dnsm.domain.service.QueryService
import ch.dnsmap.dnsm.domain.service.logging.Output
import ch.dnsmap.dnsm.domain.service.parser.DnsMessageParserImpl
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

class PlainUdpService(private val out: Output) : QueryService {

    private var socket: DatagramSocket? = null
    private var resolverHost: InetAddress? = null
    private var resolverPort: Port? = null
    private var settings: ClientSettingsPlain? = null
    private val parser = DnsMessageParserImpl(ParserOptions.Builder.builder().build())

    override
    fun connect(settings: ClientSettings): ConnectionResult {
        this.settings = settings as ClientSettingsPlain
        socket = DatagramSocket()
        this.resolverHost = settings.resolverIp()
        this.resolverPort = settings.resolverPort()
        return ConnectionResult(settings.resolverIp(), settings.resolverPort())
    }

    override
    fun query(queries: List<QueryTask>): List<QueryResult> {
        val resultList = mutableListOf<QueryResult>()
        val latch = CountDownLatch(1)
        requireNotNull(socket)
        val pairOfDisposable = socket!!.use { s ->
            val receiver = startListener(s, queries, resultList, latch)
            val sender = startSender(s, queries)
            latch.await(settings!!.timeout().first, settings!!.timeout().second)
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
            .map { rawDnsMessage ->
                val message = parser.parseBytesToMessage(rawDnsMessage)

                out.printSizeIn(rawDnsMessage.size.toLong())
                out.printMessage(message)
                out.printRawMessage(rawDnsMessage)

                queryResponse(message)
            }
            .subscribeOn(Schedulers.io())
            .subscribe { msg ->
                resultList.add(msg)
                if (resultList.size == queries.size) {
                    latch.countDown()
                }
            }
        return disposable
    }

    private fun startSender(socket: DatagramSocket, queries: List<QueryTask>): Disposable {
        requireNotNull(resolverHost)
        requireNotNull(resolverPort)
        return Observable.fromIterable(queries)
            .map { messageBytes(it) }
            .map {
                val dnsMessage = parser.parseMessageToBytes(it)

                out.printSizeOut(dnsMessage.size.toLong())
                out.printMessage(it)
                out.printRawMessage(dnsMessage)

                dnsMessage
            }
            .map { DatagramPacket(it, it.size, resolverHost!!, resolverPort!!.port) }
            .subscribe { socket.send(it) }
    }
}
