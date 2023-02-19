package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.ClientSettings
import ch.dnsmap.dnsm.domain.model.QueryResponse
import ch.dnsmap.dnsm.domain.model.QueryTask
import ch.dnsmap.dnsm.domain.model.Result
import ch.dnsmap.dnsm.infrastructure.ErrorCode
import com.github.ajalt.clikt.core.ProgramResult
import org.koin.core.component.KoinComponent
import java.io.IOException
import kotlin.time.ExperimentalTime
import kotlin.time.TimedValue
import kotlin.time.measureTimedValue

class ResultServiceImpl(
    private val settings: ClientSettings,
    private val queryService: QueryService,
    private val taskService: TaskService
) :
    ResultService,
    KoinComponent {

    @OptIn(ExperimentalTime::class)
    override
    fun run(): Result {
        try {
            val tasks = taskService.queryTasks(settings)
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

    @OptIn(ExperimentalTime::class)
    private fun execute(tasks: List<QueryTask>): TimedValue<List<QueryResponse>> {
        return measureTimedValue {
            queryService.query(
                settings.resolverHost(),
                settings.resolverPort(),
                tasks
            )
        }
    }
}
