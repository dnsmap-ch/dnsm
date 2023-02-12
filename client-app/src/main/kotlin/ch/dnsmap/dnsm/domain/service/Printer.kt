package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.PlainSettings
import ch.dnsmap.dnsm.domain.model.QueryResponse
import ch.dnsmap.dnsm.domain.model.Result
import kotlin.time.Duration
import kotlin.time.DurationUnit

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
        val elapsed = formatDuration(result.duration)
        return "Total queries sent/answers received $queryTotal/$answerTotal in $elapsed"
    }

    private fun formatDuration(duration: Duration): String {
        return when {
            duration.inWholeHours > 0 -> duration.toString(DurationUnit.HOURS, 2)
            duration.inWholeMinutes > 0 -> duration.toString(DurationUnit.MINUTES, 2)
            duration.inWholeSeconds > 0 -> duration.toString(DurationUnit.SECONDS, 2)
            duration.inWholeMilliseconds > 0 -> duration.toString(DurationUnit.MILLISECONDS, 2)
            duration.inWholeMicroseconds > 0 -> duration.toString(DurationUnit.MICROSECONDS, 2)
            duration.inWholeNanoseconds > 0 -> duration.toString(DurationUnit.NANOSECONDS, 2)
            else -> duration.toString()
        }
    }
}
