package ch.dnsmap.dnsm.infrastructure

import ch.dnsmap.dnsm.domain.model.QueryResponse
import ch.dnsmap.dnsm.domain.model.QueryTask
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.service.UdpService
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.options.*
import java.net.InetAddress
import kotlin.system.measureTimeMillis

enum class QueryType { A, AAAA }

class PlainCommand : CliktCommand(
        name = "plain",
        help = "Query DNS over UDP"
) {

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
    ).convert { Port(Integer.parseInt(it)) }.default(Port(53))
    private val name by option(
            "-n", "--name",
            help = "DNS name to resolve"
    ).required()
    private val types by option(
            "-t", "--type",
            help = "DNS type to resolve the name"
    ).split(",").default(listOf("A", "AAAA"))

    override fun run() {
        val udpService = UdpService(resolverHost, resolverPort)
        val taskScheduler = ReactiveTaskScheduler(udpService)

        types.map { it.uppercase() }
                .map { each -> QueryType.valueOf(each) }
                .forEach { type -> taskScheduler.addTask(QueryTask(name, type)) }

        val elapsed = measureTimeMillis {

            val result = taskScheduler.start()
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