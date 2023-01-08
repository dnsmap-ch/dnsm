package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.QueryResponse
import ch.dnsmap.dnsm.domain.model.Status.*
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.infrastructure.QueryType
import ch.dnsmap.dnsm.record.ResourceRecordA
import ch.dnsmap.dnsm.record.ResourceRecordAaaa
import ch.dnsmap.dnsm.wire.DnsInput
import ch.dnsmap.dnsm.wire.DnsOutput
import ch.dnsmap.dnsm.wire.ParserOptions
import java.lang.IllegalStateException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UdpService(private val resolverHost: InetAddress, private val resolverPort: Port) : SimpleService {

    override
    fun query(name: String, type: QueryType): QueryResponse {
        val datagramSocket = DatagramSocket()

        val msg = createQuery(name, type)
        val parserOptionsOut = ParserOptions.Builder.builder().build()
        val dnsOutput =
                DnsOutput.toWire(parserOptionsOut, msg.header, msg.question, emptyList(), emptyList(), emptyList())
        val msg2 = dnsOutput.message
        val packetOut = DatagramPacket(msg2, msg2.size, resolverHost, resolverPort.value)
        datagramSocket.send(packetOut)

        val buf = ByteArray(4096)
        val packetIn = DatagramPacket(buf, buf.size)
        datagramSocket.receive(packetIn)
        val parserOptionsIn = ParserOptions.Builder.builder().setDomainLabelTolerant().build()
        val response = DnsInput.fromWire(parserOptionsIn, packetIn.data)
        val resMsg = response.message
        val status = when (resMsg.header.flags.rcode.ordinal) {
            0 -> NO_ERROR
            1 -> FORMAT_ERROR
            2 -> SERVER_FAILURE
            3 -> NAME_ERROR
            4 -> NOT_IMPLEMENTED
            5 -> REFUSED
            else -> {
                throw IllegalStateException("Invalid return code")
            }
        }

        val ips = mutableListOf<String>()
        resMsg.answer.forEach {
            val ip = when (it) {
                is ResourceRecordA -> it.ip4.ip.hostAddress
                is ResourceRecordAaaa -> it.ip6.ip.hostAddress
                else -> {
                    throw NotImplementedError("Missing implementation")
                }
            }
            ips.add(ip)
        }
        val logs = parserOptionsIn.log.map { it.formatted() }
        return QueryResponse(ips, logs, type.name, status)
    }
}