package ch.dnsmap.dnsm.domain.model

import ch.dnsmap.dnsm.domain.model.query.ConnectionResultTimed
import ch.dnsmap.dnsm.domain.model.query.QueryResultTimed
import ch.dnsmap.dnsm.domain.model.query.QueryTask

class Result(
    val tasks: List<QueryTask>,
    val connectionResultTimed: ConnectionResultTimed,
    val queryResultTimed: QueryResultTimed
)
