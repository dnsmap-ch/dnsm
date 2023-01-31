package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.PlainSettings
import ch.dnsmap.dnsm.domain.model.QueryResponse
import ch.dnsmap.dnsm.domain.model.Result

class Printer {

    fun header(settings: PlainSettings): String {
        val protocol = settings.resolverPort.protocol
        return "Query ${settings.resolverHost.hostAddress}:${settings.resolverPort.value}/${protocol.printName}"
    }

    fun answer(settings: PlainSettings, answers: List<QueryResponse>): List<String> {
        return answers.flatMap { queryResponse ->
            listOf(
                "H: ${queryResponse.status}",
                "Q: ${settings.name.canonical} ${queryResponse.queryType} -> " +
                    "${settings.resolverHost.hostAddress}:${settings.resolverPort.asString()}",
                "A: " + queryResponse.ips.joinToString(separator = ", ")
            )
        }.toList()
    }

    fun summary(result: Result): String {
        val queryTotal = result.tasks.size
        val answerTotal = result.responses.size
        val elapsed = result.duration
        return "Total queries sent/answers received $queryTotal/$answerTotal in $elapsed"
    }
}
