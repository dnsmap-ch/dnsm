package ch.dnsmap.dnsm.infrastructure

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.PlainSettings
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol.UDP
import ch.dnsmap.dnsm.domain.service.Printer
import ch.dnsmap.dnsm.domain.service.QueryType.A
import ch.dnsmap.dnsm.domain.service.QueryType.AAAA
import ch.dnsmap.dnsm.domain.service.ResultService
import ch.dnsmap.dnsm.domain.service.parseInputType
import ch.dnsmap.dnsm.domain.service.parsePort
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.long
import com.github.ajalt.clikt.parameters.types.restrictTo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import java.net.InetAddress
import java.util.concurrent.TimeUnit.SECONDS

private const val DEFAULT_PORT_NUMBER = 53

private const val DEFAULT_TIMEOUT_SECOND: Long = 3

class PlainCommand(
    private val printer: Printer,
) :
    CliktCommand(
        name = "plain",
        help = "Send DNS query over plaintext UDP/TCP to DNS server."
    ),
    KoinComponent {

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
        help = "DNS server to send the messages to."
    )
        .convert { InetAddress.getByName(it) }
        .required()

    private val resolverPort by option(
        "-p",
        "--port",
        help = """
            Query a resolver on this port number and protocol.
            Possible values are: '53', '53/udp' '53/tcp'
        """.trimIndent()
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
        .convert { Domain.of(it) }
        .required()

    private val types by option(
        "-t",
        "--type",
        help = "DNS type to resolve the name"
    )
        .convert { parseInputType(it) }
        .default(
            listOf(A, AAAA),
            defaultForHelp = "Type A and AAAA query"
        )
    private val timeout: Long by option(
        "--timeout",
        help = "Timeout in seconds"
    )
        .long()
        .restrictTo(1)
        .default(DEFAULT_TIMEOUT_SECOND)

    override fun run() {
        val settings =
            PlainSettings(resolverHost, resolverPort, name, types, Pair(timeout, SECONDS))
        echo(printer.header(settings))
        val resultService: ResultService by inject { parametersOf(settings) }
        val result = resultService.run()
        printer.answer(settings, result.responses).forEach { echo(it) }
        echo(printer.summary(result))
    }
}
