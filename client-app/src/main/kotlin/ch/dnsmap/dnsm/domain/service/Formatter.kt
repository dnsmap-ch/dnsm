package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.Result
import ch.dnsmap.dnsm.domain.model.Summary
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.settings.ClientSettings
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsDoh
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsDot
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsPlain
import java.io.Serializable
import java.net.InetAddress
import kotlin.time.Duration
import kotlin.time.DurationUnit

class Formatter {

    fun header(settings: ClientSettings): String {
        val server = resolveServerAddress(settings.resolverHost(), settings.resolverIp())
        val protocol = inferProtocol(settings)
        return "\nQuery DNS server $server over ${settings.resolverPort().asString()} ($protocol)"
    }

    private fun resolveServerAddress(host: String, address: InetAddress): String {
        return if (host == address.hostAddress) {
            host
        } else {
            host + '/' + address.hostAddress
        }
    }

    private fun inferProtocol(settings: ClientSettings): Serializable {
        return when (settings) {
            is ClientSettingsDoh -> "DoH"
            is ClientSettingsDot -> "DoT"
            is ClientSettingsPlain -> "plain"
        }
    }

    fun result(result: Result): String {
        if (result.queryResultTimed.queryResults.isEmpty()) {
            return "no answers available"
        }

        return result.queryResultTimed.queryResults.stream()
            .map { queryResponse ->
                if (queryResponse.ips.isNotEmpty()) {
                    constructHeaderAndQuery(queryResponse) + listOf(
                        "A: " + queryResponse.ips.joinToString(
                            separator = ", "
                        )
                    )
                } else {
                    constructHeaderAndQuery(queryResponse)
                }
            }
            .toList()
            .flatten()
            .joinToString(separator = "\n")
    }

    private fun constructHeaderAndQuery(queryResult: QueryResult) = listOf(
        "H: ${queryResult.answerResultType}",
        "Q: ${queryResult.query.canonical} ${queryResult.queryType}"
    )

    fun summary(summary: Summary): String {
        val elapsed =
            formatDuration(summary.connectionResultTimed.duration + summary.queryResultTimed.duration)
        return "Total queries sent/answers received ${summary.queryTotal}/${summary.answerTotal} in $elapsed"
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
