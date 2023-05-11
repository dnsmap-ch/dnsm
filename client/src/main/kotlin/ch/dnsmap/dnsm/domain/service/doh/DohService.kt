package ch.dnsmap.dnsm.domain.service.doh

import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.query.ConnectionResult
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.query.queryResponse
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsDoh
import ch.dnsmap.dnsm.domain.service.QueryService
import ch.dnsmap.dnsm.wire.ParserOptions
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.net.InetAddress
import java.util.concurrent.CountDownLatch

class DohService(private val settings: ClientSettingsDoh, private val dohExecutor: DohQueryExecutor) : QueryService {

    private val httpClientBuilder = OkHttpClient().newBuilder()
    private var httpClient: OkHttpClient? = null

    override fun connect(resolverHost: InetAddress, resolverPort: Port): ConnectionResult {
        val staticResolver = StaticDns(resolverHost)
        httpClientBuilder.dns(staticResolver)
        val timeout = settings.timeout()
        httpClientBuilder.connectTimeout(timeout.first, timeout.second)
        httpClient = httpClientBuilder.build()
        return ConnectionResult(resolverHost, resolverPort)
    }

    override fun query(queries: List<QueryTask>): List<QueryResult> {
        val resultList = mutableListOf<QueryResult>()

        val latch = CountDownLatch(queries.size)
        val requests = dohExecutor.execute(queries)

        requests.stream()
            .map {
                it
                    .header("accept", "application/dns-message")
                    .build()
            }
            .forEach {
                httpClient!!.newCall(it).enqueue(object : Callback {

                    override fun onFailure(call: Call, e: IOException) {
                        latch.countDown()
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")

                            for ((name, value) in response.headers) {
                                println("$name: $value")
                            }

                            resultList.add(parseResponseBytes(response.body!!.bytes()))
                            latch.countDown()
                        }
                    }
                })
            }

        latch.await()
        httpClient!!.dispatcher.executorService.shutdown()
        return resultList
    }

    fun parseResponseBytes(rawMessage: ByteArray): QueryResult {
        val parserOptionsIn = ParserOptions.Builder.builder().setDomainLabelTolerant().build()
        return queryResponse(parserOptionsIn, rawMessage)
    }
}
