package ch.dnsmap.dnsm.domain.service.mainloop

import ch.dnsmap.dnsm.domain.model.OptionFlags
import ch.dnsmap.dnsm.domain.model.Result
import ch.dnsmap.dnsm.domain.model.Summary
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol.TCP
import ch.dnsmap.dnsm.domain.model.settings.ClientSettings
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsDohImpl
import ch.dnsmap.dnsm.domain.service.Formatter
import ch.dnsmap.dnsm.domain.service.ResultService
import ch.dnsmap.dnsm.domain.service.StubResolverService
import ch.dnsmap.dnsm.domain.service.logging.Output
import java.net.URI
import java.util.concurrent.TimeUnit.SECONDS

private const val DEFAULT_PORT_NUMBER = 443

class DohMainLoop(
    private val stubResolverService: StubResolverService,
    private val resultService: ResultService,
    formatter: Formatter,
    out: Output
) : MainLoop(formatter, out) {

    override fun prepare(flags: OptionFlags): ClientSettings {
        val url = URI.create(flags.resolverUrl!!)
        val hostname = url.host
        val resolverIp = stubResolverService.resolve(hostname)
        val parsePort = if (url.port == -1) {
            DEFAULT_PORT_NUMBER
        } else {
            url.port
        }
        val resPort = Port(parsePort, TCP)

        return ClientSettingsDohImpl.ClientSettingsDohImplBuilder()
            .method(flags.method!!)
            .name(flags.name!!)
            .resolverHost(hostname)
            .resolverIp(resolverIp)
            .resolverPort(resPort)
            .timeout(Pair(flags.timeout!!, SECONDS))
            .types(flags.types!!)
            .url(url)
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
