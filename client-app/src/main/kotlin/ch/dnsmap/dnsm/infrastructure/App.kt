package ch.dnsmap.dnsm.infrastructure

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.versionOption

class Dnsm : CliktCommand(
    invokeWithoutSubcommand = true,
    help = """
        DNS client utility tool to resolve domain names into ip addresses. 
        """.trimIndent()
) {
    override fun run() {
        val subcommand = currentContext.invokedSubcommand
        if (subcommand == null) {
            echo("dnsm: try 'dnsm --help' for more information")
        }
    }
}

fun main(args: Array<String>) = Dnsm()
    .versionOption(version = "0.3.0-SNAPSHOT", names = setOf("-V", "--version"))
    .subcommands(
        PlainCommand(),
    ).main(args)