package ch.dnsmap.dnsm.domain.model

class PrintableAnswer(
    private val settings: PlainSettings,
    private val answers: List<QueryResponse>
) {

    fun asStrings(): List<String> {
        return answers.flatMap { queryResponse ->
            listOf(
                "H: ${queryResponse.status}",
                "Q: ${settings.name} ${queryResponse.queryType} -> " +
                        "${settings.resolverHost.hostAddress}:${settings.resolverPort.asString()}",
                "A: " + queryResponse.ips.joinToString(separator = ", ")
            )
        }.toList()
    }
}
