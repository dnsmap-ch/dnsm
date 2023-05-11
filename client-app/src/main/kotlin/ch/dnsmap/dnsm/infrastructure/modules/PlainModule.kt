package ch.dnsmap.dnsm.infrastructure.modules

import ch.dnsmap.dnsm.domain.model.networking.Protocol
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsPlain
import ch.dnsmap.dnsm.domain.service.ResultService
import ch.dnsmap.dnsm.domain.service.ResultServiceImpl
import ch.dnsmap.dnsm.domain.service.plain.PlainTcpService
import ch.dnsmap.dnsm.domain.service.plain.PlainUdpService
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val MODULE_PLAIN = "plain"

val plainModule = module {
    single<ResultService>(named(MODULE_PLAIN)) { provideService(it) }
}

private fun provideService(params: ParametersHolder): ResultService {
    val settings: ClientSettingsPlain = params.get()
    val service = if (settings.resolverPort.protocol == Protocol.UDP) {
        PlainUdpService(settings)
    } else {
        PlainTcpService(settings)
    }
    return ResultServiceImpl(settings, service)
}
