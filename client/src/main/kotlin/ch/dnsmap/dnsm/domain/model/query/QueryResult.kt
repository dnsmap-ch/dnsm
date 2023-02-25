package ch.dnsmap.dnsm.domain.model.query

import ch.dnsmap.dnsm.domain.model.AnswerResultType
import kotlin.time.Duration

data class QueryResult(
    val ips: List<String>,
    val logs: List<String>,
    val queryType: String,
    val answerResultType: AnswerResultType,
)

data class QueryResultTimed(
    val queryResults: List<QueryResult>,
    val duration: Duration
)
