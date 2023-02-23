package ch.dnsmap.dnsm.infrastructure

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol
import ch.dnsmap.dnsm.domain.model.query.QueryType
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsDot
import ch.dnsmap.dnsm.domain.service.Printer
import ch.dnsmap.dnsm.domain.service.ResultService
import ch.dnsmap.dnsm.domain.service.parseInputType
import ch.dnsmap.dnsm.domain.service.parsePort
import ch.dnsmap.dnsm.infrastructure.modules.MODULE_DOT
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
import org.koin.core.qualifier.named
import java.net.InetAddress
import java.util.concurrent.TimeUnit.SECONDS

private const val DEFAULT_PORT_NUMBER = 853
private const val DEFAULT_TIMEOUT_SECOND: Long = 3

class DotCommand(private val printer: Printer) :
    CliktCommand(
        name = "dot",
        help = "Send DNS query over DoT to DNS server."
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
            Possible values are: '853'
        """.trimIndent()
    )
        .convert { parsePort(it) }
        .default(
            Port(DEFAULT_PORT_NUMBER, Protocol.TCP),
            defaultForHelp = Port(DEFAULT_PORT_NUMBER, Protocol.TCP).asString()
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
            listOf(QueryType.A, QueryType.AAAA),
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
            ClientSettingsDot(resolverHost, resolverPort, name, types, Pair(timeout, SECONDS))
        echo(printer.header(settings))
        val resultService: ResultService by inject(qualifier = named(MODULE_DOT)) {
            parametersOf(
                settings
            )
        }
        val result = resultService.run()
        printer.answer(settings, result.responses).forEach { echo(it) }
        echo(printer.summary(result))
    }
}