package ch.dnsmap.dnsm.infrastructure

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.HttpMethod
import ch.dnsmap.dnsm.domain.model.HttpMethod.POST
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol.TCP
import ch.dnsmap.dnsm.domain.model.query.QueryType
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsDohImpl
import ch.dnsmap.dnsm.domain.service.Printer
import ch.dnsmap.dnsm.domain.service.ResultService
import ch.dnsmap.dnsm.domain.service.StubResolverService
import ch.dnsmap.dnsm.domain.service.parseInputType
import ch.dnsmap.dnsm.infrastructure.modules.MODULE_DOH
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.long
import com.github.ajalt.clikt.parameters.types.restrictTo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import java.net.URI
import java.util.concurrent.TimeUnit

private const val DEFAULT_PORT_NUMBER = 443
private const val DEFAULT_TIMEOUT_SECOND: Long = 3

class DohCommand :
    CliktCommand(
        name = "doh",
        help = "DNS-over-HTTPS (DoH) client. Send DNS query to a DoH server."
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

    private val resolverUrl by option(
        names = arrayOf("-u", "--url"),
        help = """
            DNS server to send the messages to
            """.trimIndent()
    )
        .required()

    private val method by option(
        names = arrayOf("-m", "--method"),
        help = """
            HTTP method to use to resolve the domain name
        """.trimIndent()
    ).enum<HttpMethod>()
        .default(POST)

    private val name by option(
        names = arrayOf("-n", "--name"),
        help = "DNS name to resolve"
    )
        .convert { Domain.of(it) }
        .required()

    private val types by option(
        names = arrayOf("-t", "--type"),
        help = "DNS type to resolve the name"
    )
        .convert { parseInputType(it) }
        .default(
            listOf(QueryType.A, QueryType.AAAA),
            defaultForHelp = "Type A and AAAA query"
        )
    private val timeout: Long by option(
        names = arrayOf("--timeout"),
        help = "Timeout in seconds"
    )
        .long()
        .restrictTo(1)
        .default(DEFAULT_TIMEOUT_SECOND)

    private val stubResolverService: StubResolverService by inject()
    private val printer: Printer by inject()

    override fun run() {
        val url = URI.create(resolverUrl)
        val hostname = url.host
        val resolverIp = stubResolverService.resolve(hostname)
        val parsePort = if (url.port == -1) { DEFAULT_PORT_NUMBER } else { url.port }
        val resPort = Port(parsePort, TCP)

        val settings =
            ClientSettingsDohImpl(
                resolverHost = hostname,
                resolverIp = resolverIp,
                resolverPort = resPort,
                name = name,
                types = types,
                timeout = Pair(timeout, TimeUnit.SECONDS),
                url = url,
                method = method
            )
        echo(printer.header(settings))

        val resultService: ResultService by inject(qualifier = named(MODULE_DOH)) {
            parametersOf(
                settings
            )
        }

        val result = resultService.run()
        printer.answer(settings, result.queryResultTimed.queryResults).forEach { echo(it) }
        echo(printer.summary(result))
    }
}
