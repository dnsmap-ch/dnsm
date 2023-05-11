package ch.dnsmap.dnsm.domain.service.doh

import ch.dnsmap.dnsm.domain.model.query.QueryTask
import okhttp3.Request

interface DohQueryExecutor {

    fun execute(queries: List<QueryTask>): List<Request.Builder>
}
