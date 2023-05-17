package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.ErrorCode
import ch.dnsmap.dnsm.domain.model.Result
import ch.dnsmap.dnsm.domain.model.query.ConnectionResultTimed
import ch.dnsmap.dnsm.domain.model.query.QueryResultTimed
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.settings.ClientSettings
import com.github.ajalt.clikt.core.ProgramResult
import java.io.IOException
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class ResultServiceImpl(private val queryService: QueryService) : ResultService {

    override
    fun run(settings: ClientSettings): Result {
        try {
            val tasks = queryTasks(settings)
            val connectionResult = connectToServer(settings)
            val queryResult = runQueries(tasks)
            return Result(tasks, connectionResult, queryResult)
        } catch (e: IOException) {
            println(
                "error: " +
                    (
                        "While connecting to ${settings.resolverIp().hostAddress} on " +
                            "${settings.resolverPort().asString()}: ${e.message}"
                        )
            )
            throw ProgramResult(ErrorCode.NETWORK_CONNECTION_ERROR.ordinal)
        }
    }

    private fun queryTasks(settings: ClientSettings): List<QueryTask> {
        return settings.types()
            .map { type -> QueryTask(settings.name(), type) }
            .toList()
    }

    @OptIn(ExperimentalTime::class)
    private fun connectToServer(settings: ClientSettings): ConnectionResultTimed {
        val timedValue = measureTimedValue {
            queryService.connect(settings)
        }
        return ConnectionResultTimed(timedValue.value, timedValue.duration)
    }

    @OptIn(ExperimentalTime::class)
    private fun runQueries(tasks: List<QueryTask>): QueryResultTimed {
        val timedValue = measureTimedValue { queryService.query(tasks) }
        return QueryResultTimed(timedValue.value, timedValue.duration)
    }
}
