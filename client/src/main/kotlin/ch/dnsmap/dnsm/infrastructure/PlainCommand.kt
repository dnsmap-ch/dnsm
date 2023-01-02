package ch.dnsmap.dnsm.infrastructure

import ch.dnsmap.dnsm.domain.model.Answer
import ch.dnsmap.dnsm.domain.service.UdpService
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.restrictTo
import java.net.InetAddress
import kotlin.math.pow

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
    private val type: QueryType by option(
            "-t", "--type",
            help = "DNS type to resolve the name"
    ).enum<QueryType>().default(QueryType.A)

    override fun run() {
        val udpService = UdpService(resolverHost, resolverPort)
        try {
            echoAnswer(udpService.query(name, type))
        } catch (e: IllegalArgumentException) {
            echo(e.message)
        }
    }

    private fun echoAnswer(answer: Answer) {
        answer.logs.forEach { log -> echo(log, err = true) }
        echo("H: ${answer.status}")
        echo("Q: $name $type -> ${resolverHost.hostAddress}:$resolverPort/udp")
        echo("A: " + answer.ips.joinToString(separator = ", "))
    }
}