package ch.dnsmap.dnsm.infrastructure

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

class Dnsm : CliktCommand(
    invokeWithoutSubcommand = true,
    help = """
        DNS client utility tool to resolve domain names into ip addresses. 
        """.trimIndent()
) {
    private val isVersion by option(
        "-V", "--version",
        help = "Show version number and exit"
    ).flag()

    override fun run() {
        val subcommand = currentContext.invokedSubcommand
        if (subcommand == null) {
            if (isVersion) {
                echo("dnsm 0.1.0-SNAPSHOT")
            } else {
                echo("dnsm: try 'dnsm --help' for more information")
            }
        }
    }
}

fun main(args: Array<String>) = Dnsm().subcommands(
    PlainCommand(),
).main(args)