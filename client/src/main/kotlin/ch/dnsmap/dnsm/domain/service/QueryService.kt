package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import java.net.InetAddress

interface QueryService {

    fun query(resolverHost: InetAddress, resolverPort: Port, queries: List<QueryTask>): List<QueryResult>
}
