package ch.dnsmap.dnsm.domain.model

import kotlin.time.Duration

class Result(
    val duration: Duration,
    val responses: List<QueryResponse>,
    val tasks: List<QueryTask>
) {
    companion object {
        fun emptyResult() = Result(Duration.ZERO, emptyList(), emptyList())
    }
}
