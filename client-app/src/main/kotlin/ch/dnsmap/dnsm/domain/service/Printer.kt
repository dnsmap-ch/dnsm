package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.PlainSettings
import ch.dnsmap.dnsm.domain.model.QueryResponse
import ch.dnsmap.dnsm.domain.model.Result

class Printer {

    fun header(settings: PlainSettings): String {
        val protocol = settings.resolverPort.protocol
        return "Query ${settings.resolverHost.hostAddress}:${settings.resolverPort.port}/${protocol.printName}"
    }

    fun answer(settings: PlainSettings, answers: List<QueryResponse>): List<String> {
        if (answers.isEmpty()) {
            return listOf("no answers available")
        }

        return answers.flatMap { queryResponse ->
            if (queryResponse.ips.isNotEmpty()) {
                constructHeaderAndQuery(queryResponse, settings) +
                    listOf(
                        "A: " + queryResponse.ips.joinToString(separator = ", ")
                    )
            } else {
                constructHeaderAndQuery(queryResponse, settings)
            }
        }.toList()
    }

    private fun constructHeaderAndQuery(
        queryResponse: QueryResponse,
        settings: PlainSettings
    ) = listOf(
        "H: ${queryResponse.status}",
        "Q: ${settings.name.canonical} ${queryResponse.queryType} -> " +
            "${settings.resolverHost.hostAddress}:${settings.resolverPort.asString()}"
    )

    fun summary(result: Result): String {
        val queryTotal = result.tasks.size
        val answerTotal = result.responses.size
        val elapsed = result.duration
        return "Total queries sent/answers received $queryTotal/$answerTotal in $elapsed"
    }
}