package ch.dnsmap.dnsm.domain.model

data class QueryResponse(
    val ips: List<String>,
    val logs: List<String>,
    val queryType: String,
    val status: Status,
)
