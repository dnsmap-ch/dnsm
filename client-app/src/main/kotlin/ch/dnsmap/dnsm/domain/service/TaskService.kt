package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.PlainSettings
import ch.dnsmap.dnsm.domain.model.QueryTask

class TaskService {

    fun queryTasks(settings: PlainSettings): List<QueryTask> {
        return settings.types
            .map { type -> QueryTask(settings.name, type) }
            .toList()
    }
}
