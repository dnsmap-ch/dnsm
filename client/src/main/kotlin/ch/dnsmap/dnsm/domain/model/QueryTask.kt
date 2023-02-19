package ch.dnsmap.dnsm.domain.model

import ch.dnsmap.dnsm.Domain

data class QueryTask(val name: Domain, val type: QueryType)
