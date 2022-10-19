package ch.dnsmap.dnsm.domain.model

data class Answer(val ips: List<String>, val status: Status, val logs: List<String>) {
}