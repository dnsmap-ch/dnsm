package ch.dnsmap.dnsm.domain.service.dot

import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.query.ConnectionResult
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.settings.ClientSettings
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsDot
import ch.dnsmap.dnsm.domain.service.QueryService
import ch.dnsmap.dnsm.domain.service.logging.Output
import ch.dnsmap.dnsm.domain.service.network.TcpService
import ch.dnsmap.dnsm.domain.service.parser.DnsMessageParserImpl
import ch.dnsmap.dnsm.wire.ParserOptions
import java.net.InetAddress
import java.net.Socket
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class DotQueryService(private val out: Output) : QueryService {

    private var socket: Socket? = null
    private var settings: ClientSettingsDot? = null
    private val parserOutput = DnsMessageParserImpl(ParserOptions.Builder.builder().setTcp().build())
    private val parserInput = DnsMessageParserImpl(ParserOptions.Builder.builder().build())

    override
    fun connect(settings: ClientSettings): ConnectionResult {
        this.settings = settings as ClientSettingsDot
        socket = createSocket(settings.resolverIp(), settings.resolverPort())
        return ConnectionResult(settings.resolverIp(), settings.resolverPort())
    }

    override
    fun query(queries: List<QueryTask>): List<QueryResult> {
        requireNotNull(socket)
        val tcp = TcpService(settings!!, socket!!, parserOutput, parserInput, out)
        return tcp.query(queries)
    }

    private fun createSocket(host: InetAddress, port: Port): SSLSocket {
        return SSLSocketFactory.getDefault().createSocket(host, port.port) as SSLSocket
    }
}
