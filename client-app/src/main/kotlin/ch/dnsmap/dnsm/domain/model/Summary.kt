package ch.dnsmap.dnsm.domain.model

import ch.dnsmap.dnsm.domain.model.query.ConnectionResultTimed
import ch.dnsmap.dnsm.domain.model.query.QueryResultTimed

data class Summary(
    val queryTotal: Int,
    val answerTotal: Int,
    val connectionResultTimed: ConnectionResultTimed,
    val queryResultTimed: QueryResultTimed
)
