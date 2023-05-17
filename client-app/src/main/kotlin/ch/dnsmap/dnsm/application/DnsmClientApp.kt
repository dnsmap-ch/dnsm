package ch.dnsmap.dnsm.application

import ch.dnsmap.dnsm.infrastructure.DohCommand
import ch.dnsmap.dnsm.infrastructure.DotCommand
import ch.dnsmap.dnsm.infrastructure.PlainCommand
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.versionOption

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
    DnsmClientApp()
        .versionOption(version = "0.4.0-SNAPSHOT", names = setOf("-V", "--version"))
        .subcommands(
            DohCommand(),
            DotCommand(),
            PlainCommand(),
        ).main(args)
}
