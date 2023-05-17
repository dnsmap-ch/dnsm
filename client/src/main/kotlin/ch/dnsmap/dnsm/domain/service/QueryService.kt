package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.query.ConnectionResult
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.settings.ClientSettings

interface QueryService {

    fun connect(settings: ClientSettings): ConnectionResult

    fun query(queries: List<QueryTask>): List<QueryResult>
}
