package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.AnswerResultType
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol.UDP
import ch.dnsmap.dnsm.domain.model.query.ConnectionResult
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.settings.ClientSettings
import java.net.InetAddress

class DummyQueryService : QueryService {

    override fun connect(settings: ClientSettings): ConnectionResult {
        return ConnectionResult(InetAddress.getByName("127.0.0.1"), Port(53, UDP))
    }

    override
    fun query(queries: List<QueryTask>): List<QueryResult> {
        return listOf(
            QueryResult(Domain.root(), listOf("127.0.0.1"), "A", AnswerResultType.NO_ERROR),
            QueryResult(Domain.root(), listOf("::1"), "AAAA", AnswerResultType.NO_ERROR)
        )
    }
}
