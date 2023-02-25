package ch.dnsmap.dnsm.domain.service.dot

import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.query.ConnectionResult
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.settings.ClientSettings
import ch.dnsmap.dnsm.domain.service.QueryService
import ch.dnsmap.dnsm.domain.service.network.TcpService
import java.net.InetAddress
import java.net.Socket
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class DotService(private val settings: ClientSettings) : QueryService {

    private var socket: Socket? = null

    override
    fun connect(resolverHost: InetAddress, resolverPort: Port): ConnectionResult {
        socket = createSocket(resolverHost, resolverPort)
        return ConnectionResult(resolverHost, resolverPort)
    }

    override
    fun query(queries: List<QueryTask>): List<QueryResult> {
        requireNotNull(socket)
        val tcp = TcpService(settings, socket!!)
        return tcp.query(queries)
    }

    private fun createSocket(host: InetAddress, port: Port): SSLSocket {
        return SSLSocketFactory.getDefault().createSocket(host, port.port) as SSLSocket
    }
}
