package ch.dnsmap.dnsm.domain.model

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.service.QueryType

data class QueryTask(val name: Domain, val type: QueryType)
