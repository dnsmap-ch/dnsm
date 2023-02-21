package ch.dnsmap.dnsm.domain.model

import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import kotlin.time.Duration

class Result(
    val duration: Duration,
    val responses: List<QueryResult>,
    val tasks: List<QueryTask>
) {
    companion object {
        fun emptyResult() = Result(Duration.ZERO, emptyList(), emptyList())
    }
}
