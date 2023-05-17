package ch.dnsmap.dnsm.infrastructure

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.HttpMethod
import ch.dnsmap.dnsm.domain.model.HttpMethod.POST
import ch.dnsmap.dnsm.domain.model.OptionFlags
import ch.dnsmap.dnsm.domain.model.query.QueryType
import ch.dnsmap.dnsm.domain.service.logging.DebugOutput
import ch.dnsmap.dnsm.domain.service.logging.MessageOutput
import ch.dnsmap.dnsm.domain.service.logging.MessageSizeOutput
import ch.dnsmap.dnsm.domain.service.logging.NormalOutput
import ch.dnsmap.dnsm.domain.service.logging.Output
import ch.dnsmap.dnsm.domain.service.mainloop.MainLoop
import ch.dnsmap.dnsm.domain.service.parseInputType
import ch.dnsmap.dnsm.infrastructure.modules.commonModule
import ch.dnsmap.dnsm.infrastructure.modules.dohModule
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.counted
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.long
import com.github.ajalt.clikt.parameters.types.restrictTo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module

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
        help = """DNS server to send the messages to""".trimIndent()
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

    private val verbosity by option(
        names = arrayOf("-v"),
        help = """
            Makes dnsm verbose during the operation. Useful for debugging and seeing what's going on
            "under  the  hood". A line starting with '>' means data sent by dnsm, '<' means data 
            received by dnsm that is hidden in normal cases, and a line starting  with '*' means 
            additional info provided by dnsm.
        """.trimIndent()
    )
        .counted()

    private val mainLoop: MainLoop by inject()

    override fun run() {
        startKoin {
            modules(
                commonModule,
                dohModule,
                module {
                    single {
                        when (verbosity) {
                            0 -> NormalOutput(::echo)
                            1 -> MessageSizeOutput(::echo)
                            2 -> MessageOutput(::echo)
                            else -> DebugOutput(::echo)
                        }
                    }.bind(Output::class)
                }
            )
        }

        val options: OptionFlags = OptionFlags.Builder()
            .resolverUrl(resolverUrl)
            .method(method)
            .name(name)
            .types(types)
            .timeout(timeout)
            .build()

        mainLoop.run(options)
    }
}
