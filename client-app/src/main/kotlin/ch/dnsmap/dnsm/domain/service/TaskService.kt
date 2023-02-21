package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.settings.ClientSettings

class TaskService {

    fun queryTasks(settings: ClientSettings): List<QueryTask> {
        return settings.types()
            .map { type -> QueryTask(settings.name(), type) }
            .toList()
    }
}
