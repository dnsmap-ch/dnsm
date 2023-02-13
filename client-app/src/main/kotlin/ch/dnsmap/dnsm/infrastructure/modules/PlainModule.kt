package ch.dnsmap.dnsm.infrastructure.modules

import ch.dnsmap.dnsm.domain.model.PlainSettings
import ch.dnsmap.dnsm.domain.model.networking.Protocol
import ch.dnsmap.dnsm.domain.service.PlainResultService
import ch.dnsmap.dnsm.domain.service.Printer
import ch.dnsmap.dnsm.domain.service.ResultService
import ch.dnsmap.dnsm.domain.service.TaskService
import ch.dnsmap.dnsm.domain.service.TcpService
import ch.dnsmap.dnsm.domain.service.UdpService
import org.koin.core.parameter.ParametersHolder
import org.koin.dsl.module

val plainModule = module {
    single { Printer() }
    single { provideService(it, get()) }
    single { TaskService() }
}

private fun provideService(
    params: ParametersHolder,
    taskService: TaskService
): ResultService {
    val settings: PlainSettings = params.get()
    val service = if (settings.resolverPort.protocol == Protocol.UDP) {
        UdpService(settings)
    } else {
        TcpService(settings)
    }
    return PlainResultService(settings, service, taskService)
}
