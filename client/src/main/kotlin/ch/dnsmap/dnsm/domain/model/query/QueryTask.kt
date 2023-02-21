package ch.dnsmap.dnsm.domain.model.query

import ch.dnsmap.dnsm.Domain

data class QueryTask(val name: Domain, val type: QueryType)
