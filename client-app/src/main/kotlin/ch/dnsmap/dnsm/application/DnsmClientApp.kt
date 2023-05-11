package ch.dnsmap.dnsm.application

import ch.dnsmap.dnsm.infrastructure.DohCommand
import ch.dnsmap.dnsm.infrastructure.DotCommand
import ch.dnsmap.dnsm.infrastructure.PlainCommand
import ch.dnsmap.dnsm.infrastructure.modules.commonModule
import ch.dnsmap.dnsm.infrastructure.modules.dohModule
import ch.dnsmap.dnsm.infrastructure.modules.dotModule
import ch.dnsmap.dnsm.infrastructure.modules.plainModule
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.versionOption
import org.koin.core.context.startKoin

class DnsmClientApp : CliktCommand(
    invokeWithoutSubcommand = true,
    printHelpOnEmptyArgs = true,
    help = """
        DNS client utility tool to resolve domain names into IP addresses."""
        .trimIndent()
) {
    override fun run() {
        currentContext.invokedSubcommand
    }
}

fun main(args: Array<String>) {
    startKoin {
        modules(commonModule)
        modules(dohModule)
        modules(dotModule)
        modules(plainModule)
    }

    DnsmClientApp()
        .versionOption(version = "0.4.0-SNAPSHOT", names = setOf("-V", "--version"))
        .subcommands(
            DohCommand(),
            DotCommand(),
            PlainCommand(),
        ).main(args)
}
