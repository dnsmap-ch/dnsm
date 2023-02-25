package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.query.ConnectionResult
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import java.net.InetAddress

interface QueryService {

    fun connect(resolverHost: InetAddress, resolverPort: Port): ConnectionResult

    fun query(queries: List<QueryTask>): List<QueryResult>
}
