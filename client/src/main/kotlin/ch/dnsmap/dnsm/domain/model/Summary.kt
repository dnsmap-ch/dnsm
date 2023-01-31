package ch.dnsmap.dnsm.domain.model

import kotlin.time.Duration

class Summary(
    private val elapsedTotal: Duration,
    private val queryTasks: List<QueryTask>,
    private val queryResponses: List<QueryResponse>
) {
    fun asString(): String {
        val queryTotal = queryTasks.size
        val answerTotal = queryResponses.size
        val elapsed = elapsedTotal
        return "Total queries sent/answers received $queryTotal/$answerTotal in $elapsed"
    }
}
