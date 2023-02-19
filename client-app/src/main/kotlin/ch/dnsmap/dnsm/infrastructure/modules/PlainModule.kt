package ch.dnsmap.dnsm.infrastructure.modules

import ch.dnsmap.dnsm.domain.model.ClientSettingsPlain
import ch.dnsmap.dnsm.domain.model.networking.Protocol
import ch.dnsmap.dnsm.domain.service.Printer
import ch.dnsmap.dnsm.domain.service.ResultService
import ch.dnsmap.dnsm.domain.service.ResultServiceImpl
import ch.dnsmap.dnsm.domain.service.TaskService
import ch.dnsmap.dnsm.domain.service.plain.TcpService
import ch.dnsmap.dnsm.domain.service.plain.UdpService
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val MODULE_PLAIN = "plain"

val plainModule = module {
    single { Printer() }
    single<ResultService>(named(MODULE_PLAIN)) { provideService(it, get()) }
    single { TaskService() }
}

private fun provideService(
    params: ParametersHolder,
    taskService: TaskService
): ResultService {
    val settings: ClientSettingsPlain = params.get()
    val service = if (settings.resolverPort.protocol == Protocol.UDP) {
        UdpService(settings)
    } else {
        TcpService(settings)
    }
    return ResultServiceImpl(settings, service, taskService)
}
