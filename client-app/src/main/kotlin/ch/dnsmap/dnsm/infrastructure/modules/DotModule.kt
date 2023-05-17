package ch.dnsmap.dnsm.infrastructure.modules

import ch.dnsmap.dnsm.domain.service.QueryService
import ch.dnsmap.dnsm.domain.service.ResultService
import ch.dnsmap.dnsm.domain.service.ResultServiceImpl
import ch.dnsmap.dnsm.domain.service.dot.DotQueryService
import ch.dnsmap.dnsm.domain.service.mainloop.DotMainLoop
import ch.dnsmap.dnsm.domain.service.mainloop.MainLoop
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dotModule = module {
    singleOf(::DotQueryService) {
        bind<QueryService>()
    }
    singleOf(::ResultServiceImpl) {
        bind<ResultService>()
    }
    singleOf(::DotMainLoop) {
        bind<MainLoop>()
    }
}
