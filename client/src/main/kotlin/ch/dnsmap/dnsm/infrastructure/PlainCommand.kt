package ch.dnsmap.dnsm.infrastructure

import ch.dnsmap.dnsm.domain.model.QueryResponse
import ch.dnsmap.dnsm.domain.model.QueryTask
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.restrictTo
import java.net.InetAddress
import kotlin.math.pow
import kotlin.system.measureTimeMillis

enum class QueryType { A, AAAA }

class PlainCommand : CliktCommand(
        name = "plain",
        help = "Query DNS over UDP"
) {
    private val portMin = 1
    private val portMax = (2.0.pow(16.0) - 1).toInt()

    init {
        context {
            helpFormatter = CliktHelpFormatter(
                    showDefaultValues = true,
                    showRequiredTag = true
            )
        }
    }

    private val resolverHost by option(
            "-r", "--resolver",
            help = "UDP DNS resolver to query"
    ).convert { InetAddress.getByName(it) }.required()
    private val resolverPort by option(
            "-p", "--port",
            help = " Port to query on resolver side"
    ).int().restrictTo(portMin, portMax).default(53)
    private val name by option(
            "-n", "--name",
            help = "DNS name to resolve"
    ).required()
    private val types by option(
            "-t", "--type",
            help = "DNS type to resolve the name"
    ).split(",").default(listOf("A", "AAAA"))

    override fun run() {
        val connector = UdpConnector(resolverHost, resolverPort)

        types.map { it.uppercase() }
                .map { each -> QueryType.valueOf(each) }
                .forEach { type -> connector.addTask(QueryTask(name, type)) }

        val elapsed = measureTimeMillis {
            val result = connector.start()
            result.forEach { echoAnswer(it) }
        }
        echo("completed in ${elapsed}ms")
    }

    private fun echoAnswer(queryResponse: QueryResponse) {
        queryResponse.logs.forEach { log -> echo(log, err = true) }
        echo("H: ${queryResponse.status}")
        echo("Q: $name ${queryResponse.queryType} -> ${resolverHost.hostAddress}:$resolverPort/udp")
        echo("A: " + queryResponse.ips.joinToString(separator = ", "))
    }
}