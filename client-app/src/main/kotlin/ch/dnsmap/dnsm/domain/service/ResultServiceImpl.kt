package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.ErrorCode
import ch.dnsmap.dnsm.domain.model.Result
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.settings.ClientSettings
import com.github.ajalt.clikt.core.ProgramResult
import org.koin.core.component.KoinComponent
import java.io.IOException
import kotlin.time.ExperimentalTime
import kotlin.time.TimedValue
import kotlin.time.measureTimedValue

class ResultServiceImpl(
    private val settings: ClientSettings,
    private val queryService: QueryService,
) :
    ResultService,
    KoinComponent {

    @OptIn(ExperimentalTime::class)
    override
    fun run(): Result {
        try {
            val tasks = queryTasks(settings)
            val result = execute(tasks)
            return Result(result.duration, result.value, tasks)
        } catch (e: IOException) {
            println(
                "error: " +
                    (
                        "While connecting to ${settings.resolverHost().hostName}:" +
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
    private fun execute(tasks: List<QueryTask>): TimedValue<List<QueryResult>> {
        return measureTimedValue {
            queryService.connect(settings.resolverHost(), settings.resolverPort())
            queryService.query(tasks)
        }
    }
}
