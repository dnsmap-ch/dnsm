package ch.dnsmap.dnsm.domain.service.mainloop

import ch.dnsmap.dnsm.domain.model.OptionFlags
import ch.dnsmap.dnsm.domain.model.Result
import ch.dnsmap.dnsm.domain.model.Summary
import ch.dnsmap.dnsm.domain.model.settings.ClientSettings
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsDot
import ch.dnsmap.dnsm.domain.service.Formatter
import ch.dnsmap.dnsm.domain.service.ResultService
import ch.dnsmap.dnsm.domain.service.StubResolverService
import ch.dnsmap.dnsm.domain.service.logging.Output
import java.util.concurrent.TimeUnit.SECONDS

class DotMainLoop(
    private val stubResolverService: StubResolverService,
    private val resultService: ResultService,
    formatter: Formatter,
    out: Output
) : MainLoop(formatter, out) {

    override fun prepare(flags: OptionFlags): ClientSettings {
        val resolverIp = stubResolverService.resolve(flags.resolverHost!!)

        return ClientSettingsDot.ClientSettingsDotBuilder()
            .name(flags.name!!)
            .resolverHost(flags.resolverHost)
            .resolverIp(resolverIp)
            .resolverPort(flags.resolverPort!!)
            .timeout(Pair(flags.timeout!!, SECONDS))
            .types(flags.types!!)
            .build()
    }

    override fun act(settings: ClientSettings): Result {
        return resultService.run(settings)
    }

    override fun complete(result: Result): Summary {
        val queryTotal = result.tasks.size
        val answerTotal = result.queryResultTimed.queryResults.size
        val connectionResultTimed = result.connectionResultTimed
        val queryResultTimed = result.queryResultTimed
        return Summary(queryTotal, answerTotal, connectionResultTimed, queryResultTimed)
    }
}
