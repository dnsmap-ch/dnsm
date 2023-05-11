package ch.dnsmap.dnsm.infrastructure.modules

import ch.dnsmap.dnsm.domain.model.HttpMethod.GET
import ch.dnsmap.dnsm.domain.model.HttpMethod.POST
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsDoh
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsDohImpl
import ch.dnsmap.dnsm.domain.service.QueryService
import ch.dnsmap.dnsm.domain.service.ResultService
import ch.dnsmap.dnsm.domain.service.ResultServiceImpl
import ch.dnsmap.dnsm.domain.service.doh.DohGetQueryExecutor
import ch.dnsmap.dnsm.domain.service.doh.DohPostQueryExecutor
import ch.dnsmap.dnsm.domain.service.doh.DohQueryExecutor
import ch.dnsmap.dnsm.domain.service.doh.DohService
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.singleOf
import org.koin.core.parameter.ParametersHolder
import org.koin.dsl.module

const val MODULE_DOH = "doh"

val dohModule = module {
    single<ClientSettingsDohImpl> { provideSettings(it) }
    singleOf(::DohService) { bind<QueryService>() }
    singleOf(::ResultServiceImpl) {
        bind<ResultService>()
        named(MODULE_DOH)
    }
    single { provideDohMethodExecutor(get()) }
}

private fun provideSettings(params: ParametersHolder): ClientSettingsDohImpl {
    return params.get<ClientSettingsDohImpl>()
}

private fun provideDohMethodExecutor(settings: ClientSettingsDoh): DohQueryExecutor {
    return when (settings.method()) {
        POST -> DohPostQueryExecutor(settings)
        GET -> DohGetQueryExecutor(settings)
    }
}
