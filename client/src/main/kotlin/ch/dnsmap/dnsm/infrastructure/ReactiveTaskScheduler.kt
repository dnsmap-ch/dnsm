package ch.dnsmap.dnsm.infrastructure

import ch.dnsmap.dnsm.domain.model.QueryResponse
import ch.dnsmap.dnsm.domain.model.QueryTask
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.service.SimpleService
import ch.dnsmap.dnsm.domain.service.UdpService
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.net.InetAddress

class ReactiveTaskScheduler(private val service: SimpleService) {

    private val tasks: MutableList<QueryTask> = mutableListOf()

    fun addTask(task: QueryTask) {
        tasks.add(task)
    }

    fun start(): List<QueryResponse> {
        return Flowable.fromIterable(tasks)
                .parallel()
                .runOn(Schedulers.io())
                .map { eachTask -> service.query(eachTask.name, eachTask.type) }
                .sequential()
                .toList()
                .blockingGet()
    }
}