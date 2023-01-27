package ch.dnsmap.dnsm.infrastructure

import ch.dnsmap.dnsm.domain.model.QueryResponse
import ch.dnsmap.dnsm.domain.model.QueryTask
import ch.dnsmap.dnsm.domain.model.Summary
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol
import ch.dnsmap.dnsm.domain.model.networking.Protocol.TCP
import ch.dnsmap.dnsm.domain.model.networking.Protocol.UDP
import ch.dnsmap.dnsm.domain.service.TcpService
import ch.dnsmap.dnsm.domain.service.UdpService
import ch.dnsmap.dnsm.infrastructure.ErrorCode.NETWORK_CONNECTION_ERROR
import ch.dnsmap.dnsm.infrastructure.ErrorCode.SUCCESSFUL
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.options.*
import java.io.IOException
import java.net.InetAddress
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

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
    )
        .convert { InetAddress.getByName(it) }
        .required()

    private val resolverPort by option(
        "-p", "--port",
        help = " Port and protocol to query on resolver side. Possible values are '53', '53/udp' '53/tcp' or '53/udp/tcp/",
    )
        .convert { parsePort(it) }
        .default(Port(53, UDP), defaultForHelp = Port(53, UDP).asString())

    private val name by option(
        "-n", "--name",
        help = "DNS name to resolve"
    )
        .required()

    private val types by option(
        "-t", "--type",
        help = "DNS type to resolve the name"
    )
        .split(",")
        .default(listOf("A", "AAAA"))

    @OptIn(ExperimentalTime::class)
    override fun run() {
        val tasks = types.map { it.uppercase() }
            .map { each -> QueryType.valueOf(each) }
            .map { type -> QueryTask(name, type) }
            .toList()

        val result = measureTimedValue {
            try {
                if (resolverPort.protocol == UDP) {
                    runUdp(tasks)
                } else {
                    runTcp(tasks)
                }
            } catch (e: IOException) {
                echoError("While connecting to ${resolverHost.hostName}:${resolverPort.asString()}: ${e.message}")
                exitProcess(NETWORK_CONNECTION_ERROR.ordinal)
            }
        }
        result.value.forEach { echoAnswer(it) }
        val summary = Summary(result.duration, tasks, result.value)
        echoAppSummary(summary)
        exitProcess(SUCCESSFUL.ordinal)
    }


    private fun runUdp(tasks: List<QueryTask>): List<QueryResponse> {
        echoAppHeader(UDP)
        val udpService = UdpService(resolverHost, resolverPort)
        return udpService.query(tasks)
    }

    private fun runTcp(tasks: List<QueryTask>): List<QueryResponse> {
        echoAppHeader(TCP)
        val tcpService = TcpService(resolverHost, resolverPort)
        return tcpService.query(tasks)
    }

    private fun echoError(msg: String) {
        echo("error: $msg", err = true)
    }

    private fun echoAppHeader(protocol: Protocol) {
        echo("Query ${resolverHost.hostAddress}:${resolverPort.value}/${protocol.printName}")
    }

    private fun echoAppSummary(summary: Summary) {
        echo(summary.asString())
    }

    private fun echoAnswer(queryResponse: QueryResponse) {
        queryResponse.logs.forEach { log -> echo(log, err = true) }
        echo("H: ${queryResponse.status}")
        echo("Q: $name ${queryResponse.queryType} -> ${resolverHost.hostAddress}:${resolverPort.asString()}")
        echo("A: " + queryResponse.ips.joinToString(separator = ", "))
    }
}