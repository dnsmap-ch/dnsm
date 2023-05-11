package ch.dnsmap.dnsm.infrastructure.modules

import ch.dnsmap.dnsm.domain.service.Printer
import ch.dnsmap.dnsm.domain.service.StubResolverService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val commonModule = module {
    singleOf(::Printer)
    singleOf(::StubResolverService)
}
