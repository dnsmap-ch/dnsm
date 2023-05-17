package ch.dnsmap.dnsm.domain.model.query

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.AnswerResultType
import kotlin.time.Duration

data class QueryResult(
    val query: Domain,
    val ips: List<String>,
    val queryType: String,
    val answerResultType: AnswerResultType,
)

data class QueryResultTimed(
    val queryResults: List<QueryResult>,
    val duration: Duration
)
