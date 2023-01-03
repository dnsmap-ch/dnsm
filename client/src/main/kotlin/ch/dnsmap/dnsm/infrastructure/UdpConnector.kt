package ch.dnsmap.dnsm.infrastructure

import ch.dnsmap.dnsm.domain.model.QueryResponse
import ch.dnsmap.dnsm.domain.model.QueryTask
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.service.UdpService
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.net.InetAddress

class UdpConnector(private val server: InetAddress, private val port: Port) {

    private val tasks: MutableList<QueryTask> = mutableListOf()

    fun addTask(task: QueryTask) {
        tasks.add(task)
    }

    fun start(): List<QueryResponse> {
        val udpService = UdpService(server, port)
        return Flowable.fromIterable(tasks)
                .parallel()
                .runOn(Schedulers.io())
                .map { eachTask -> udpService.query(eachTask.name, eachTask.type) }
                .sequential()
                .toList()
                .blockingGet()
    }
}