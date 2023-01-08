package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.QueryResponse
import ch.dnsmap.dnsm.domain.model.QueryTask

interface SimpleService {

    fun query(queries: List<QueryTask>): List<QueryResponse>
}
