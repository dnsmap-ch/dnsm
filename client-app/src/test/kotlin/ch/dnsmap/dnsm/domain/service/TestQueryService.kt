package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.QueryResponse
import ch.dnsmap.dnsm.domain.model.QueryTask
import ch.dnsmap.dnsm.domain.model.Status
import ch.dnsmap.dnsm.domain.model.networking.Port
import java.net.InetAddress

class TestQueryService : QueryService {

    override fun query(
        resolverHost: InetAddress,
        resolverPort: Port,
        queries: List<QueryTask>
    ): List<QueryResponse> {
        return listOf(
            QueryResponse(listOf("127.0.0.1"), emptyList(), "A", Status.NO_ERROR),
            QueryResponse(listOf("::1"), emptyList(), "AAAA", Status.NO_ERROR)
        )
    }
}
