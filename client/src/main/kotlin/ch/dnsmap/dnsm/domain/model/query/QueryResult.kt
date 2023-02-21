package ch.dnsmap.dnsm.domain.model.query

import ch.dnsmap.dnsm.domain.model.AnswerResultType

data class QueryResult(
    val ips: List<String>,
    val logs: List<String>,
    val queryType: String,
    val answerResultType: AnswerResultType,
)
