package ch.dnsmap.dnsm.infrastructure.modules

import ch.dnsmap.dnsm.domain.service.QueryService
import ch.dnsmap.dnsm.domain.service.ResultService
import ch.dnsmap.dnsm.domain.service.ResultServiceImpl
import ch.dnsmap.dnsm.domain.service.doh.DohQueryService
import ch.dnsmap.dnsm.domain.service.mainloop.DohMainLoop
import ch.dnsmap.dnsm.domain.service.mainloop.MainLoop
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dohModule = module {
    singleOf(::DohQueryService) {
        bind<QueryService>()
    }
    singleOf(::ResultServiceImpl) {
        bind<ResultService>()
    }
    singleOf(::DohMainLoop) {
        bind<MainLoop>()
    }
}
