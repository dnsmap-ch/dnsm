package ch.dnsmap.dnsm.infrastructure

import ch.dnsmap.dnsm.domain.model.PlainSettings
import ch.dnsmap.dnsm.domain.model.PrintableAnswer
import ch.dnsmap.dnsm.domain.model.PrintableHeader
import ch.dnsmap.dnsm.domain.model.QueryTask
import ch.dnsmap.dnsm.domain.model.Summary
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol
import ch.dnsmap.dnsm.domain.model.networking.Protocol.UDP
import ch.dnsmap.dnsm.domain.service.QueryType
import ch.dnsmap.dnsm.domain.service.TcpService
import ch.dnsmap.dnsm.domain.service.UdpService
import ch.dnsmap.dnsm.infrastructure.ErrorCode.SUCCESSFUL
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.split
import java.io.IOException
import java.net.InetAddress
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

private const val DEFAULT_PORT_NUMBER = 53

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
        "-r",
        "--resolver",
        help = "UDP DNS resolver to query"
    )
        .convert { InetAddress.getByName(it) }
        .required()

    private val resolverPort by option(
        "-p",
        "--port",
        help = " Port and protocol to query on resolver side. Possible values are '53', '53/udp' " +
            "'53/tcp' or '53/udp/tcp/'",
    )
        .convert { parsePort(it) }
        .default(
            Port(DEFAULT_PORT_NUMBER, UDP),
            defaultForHelp = Port(DEFAULT_PORT_NUMBER, UDP).asString()
        )

    private val name by option(
        "-n",
        "--name",
        help = "DNS name to resolve"
    )
        .required()

    private val types by option(
        "-t",
        "--type",
        help = "DNS type to resolve the name"
    )
        .split(",")
        .default(listOf("A", "AAAA"))

    @OptIn(ExperimentalTime::class)
    override fun run() {
        val settings = PlainSettings(resolverHost, resolverPort, name, types)

        val tasks = settings.types.map { it.uppercase() }
            .map { each -> QueryType.valueOf(each) }
            .map { type -> QueryTask(settings.name, type) }
            .toList()

        val result = measureTimedValue {
            try {
                if (settings.resolverPort.protocol == UDP) {
                    val header = PrintableHeader(settings, UDP)
                    echo(header.asString())
                    val udpService = UdpService(settings.resolverHost, settings.resolverPort)
                    udpService.query(tasks)
                } else {
                    val header = PrintableHeader(settings, Protocol.TCP)
                    echo(header.asString())
                    val tcpService = TcpService(settings.resolverHost, settings.resolverPort)
                    tcpService.query(tasks)
                }
            } catch (e: IOException) {
                echo(
                    "error: " +
                        (
                            "While connecting to ${settings.resolverHost.hostName}:" +
                                "${settings.resolverPort.asString()}: ${e.message}"
                            ),
                    err = true
                )
                throw ProgramResult(ErrorCode.NETWORK_CONNECTION_ERROR.ordinal)
            }
        }
        val answer = PrintableAnswer(settings, result.value)
        val summary = Summary(result.duration, tasks, result.value)
        answer.asStrings().forEach { echo(it) }
        echo(summary.asString())

        exitProcess(SUCCESSFUL.ordinal)
    }
}
