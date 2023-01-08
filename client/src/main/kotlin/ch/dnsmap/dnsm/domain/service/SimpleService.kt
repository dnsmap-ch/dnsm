package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.QueryResponse
import ch.dnsmap.dnsm.infrastructure.QueryType

interface SimpleService {

    fun query(name: String, type: QueryType): QueryResponse
}
