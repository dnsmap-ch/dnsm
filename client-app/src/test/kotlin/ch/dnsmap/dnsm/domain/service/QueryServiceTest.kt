package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.AnswerResultType
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import java.net.InetAddress

class QueryServiceTest : QueryService {

    override fun query(
        resolverHost: InetAddress,
        resolverPort: Port,
        queries: List<QueryTask>
    ): List<QueryResult> {
        return listOf(
            QueryResult(listOf("127.0.0.1"), emptyList(), "A", AnswerResultType.NO_ERROR),
            QueryResult(listOf("::1"), emptyList(), "AAAA", AnswerResultType.NO_ERROR)
        )
    }
}