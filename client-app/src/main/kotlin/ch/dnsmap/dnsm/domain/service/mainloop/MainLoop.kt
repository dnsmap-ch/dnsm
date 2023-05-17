package ch.dnsmap.dnsm.domain.service.mainloop

import ch.dnsmap.dnsm.domain.model.OptionFlags
import ch.dnsmap.dnsm.domain.model.Result
import ch.dnsmap.dnsm.domain.model.Summary
import ch.dnsmap.dnsm.domain.model.settings.ClientSettings
import ch.dnsmap.dnsm.domain.service.Formatter
import ch.dnsmap.dnsm.domain.service.logging.Output

abstract class MainLoop(private val formatter: Formatter, private val out: Output) {

    abstract fun prepare(flags: OptionFlags): ClientSettings
    abstract fun act(settings: ClientSettings): Result
    abstract fun complete(result: Result): Summary

    fun run(flags: OptionFlags) {
        val settings = prepare(flags)
        out.printNormal(formatter.header(settings))

        val result = act(settings)
        out.printNormal(formatter.result(result))

        val summary = complete(result)
        out.printNormal(formatter.summary(summary))
    }
}
