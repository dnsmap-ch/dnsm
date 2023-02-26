package ch.dnsmap.dnsm.infrastructure.modules

import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsDot
import ch.dnsmap.dnsm.domain.service.Printer
import ch.dnsmap.dnsm.domain.service.ResultService
import ch.dnsmap.dnsm.domain.service.ResultServiceImpl
import ch.dnsmap.dnsm.domain.service.StubResolverService
import ch.dnsmap.dnsm.domain.service.dot.DotService
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val MODULE_DOT = "dot"

val dotModule = module {
    single { Printer() }
    single { StubResolverService() }
    single<ResultService>(named(MODULE_DOT)) { provideService(it) }
}

private fun provideService(params: ParametersHolder): ResultService {
    val settings: ClientSettingsDot = params.get()
    val service = DotService(settings)
    return ResultServiceImpl(settings, service)
}
