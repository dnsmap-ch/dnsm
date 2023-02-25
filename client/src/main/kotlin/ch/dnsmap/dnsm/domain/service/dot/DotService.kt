package ch.dnsmap.dnsm.domain.service.dot

import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.settings.ClientSettings
import ch.dnsmap.dnsm.domain.service.QueryService
import ch.dnsmap.dnsm.domain.service.network.TcpService
import java.net.InetAddress
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class DotService(private val settings: ClientSettings) : QueryService {

    override
    fun query(
        resolverHost: InetAddress,
        resolverPort: Port,
        queries: List<QueryTask>
    ): List<QueryResult> {
        val socket = createSocket(resolverHost, resolverPort)
        val tcp = TcpService(settings, socket)
        return tcp.query(queries)
    }

    private fun createSocket(host: InetAddress, port: Port): SSLSocket {
        return SSLSocketFactory.getDefault().createSocket(host, port.port) as SSLSocket
    }
}
