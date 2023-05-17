package ch.dnsmap.dnsm.infrastructure.modules

import ch.dnsmap.dnsm.domain.service.QueryService
import ch.dnsmap.dnsm.domain.service.ResultService
import ch.dnsmap.dnsm.domain.service.ResultServiceImpl
import ch.dnsmap.dnsm.domain.service.mainloop.MainLoop
import ch.dnsmap.dnsm.domain.service.mainloop.PlainMainLoop
import ch.dnsmap.dnsm.domain.service.plain.PlainQueryService
import ch.dnsmap.dnsm.domain.service.plain.PlainTcpService
import ch.dnsmap.dnsm.domain.service.plain.PlainUdpService
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val plainModule = module {
    singleOf(::PlainMainLoop) { bind<MainLoop>() }
    singleOf(::PlainQueryService) { bind<QueryService>() }
    singleOf(::PlainTcpService)
    singleOf(::PlainUdpService)
    singleOf(::ResultServiceImpl) { bind<ResultService>() }
}
