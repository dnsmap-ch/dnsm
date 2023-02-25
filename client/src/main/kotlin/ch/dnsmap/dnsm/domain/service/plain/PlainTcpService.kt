package ch.dnsmap.dnsm.domain.service.plain

import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.settings.ClientSettings
import ch.dnsmap.dnsm.domain.service.QueryService
import ch.dnsmap.dnsm.domain.service.network.TcpService
import java.net.InetAddress
import java.net.Socket

class PlainTcpService(private val settings: ClientSettings) : QueryService {

    override
    fun query(
        resolverHost: InetAddress,
        resolverPort: Port,
        queries: List<QueryTask>
    ): List<QueryResult> {
        val socket = Socket(resolverHost, resolverPort.port)
        val tcp = TcpService(settings, socket)
        return tcp.query(queries)
    }
}
