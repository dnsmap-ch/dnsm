package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.QueryResponse
import ch.dnsmap.dnsm.domain.model.QueryTask
import ch.dnsmap.dnsm.domain.model.networking.Port
import java.net.InetAddress

interface QueryService {

    fun query(resolverHost: InetAddress, resolverPort: Port, queries: List<QueryTask>): List<QueryResponse>
}
