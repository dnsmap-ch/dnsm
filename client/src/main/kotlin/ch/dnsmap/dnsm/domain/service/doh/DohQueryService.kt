package ch.dnsmap.dnsm.domain.service.doh

import ch.dnsmap.dnsm.domain.model.HttpMethod
import ch.dnsmap.dnsm.domain.model.query.ConnectionResult
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.query.queryResponse
import ch.dnsmap.dnsm.domain.model.settings.ClientSettings
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsDoh
import ch.dnsmap.dnsm.domain.service.QueryService
import ch.dnsmap.dnsm.domain.service.logging.Output
import ch.dnsmap.dnsm.domain.service.parser.DnsMessageParserImpl
import ch.dnsmap.dnsm.wire.ParserOptions
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.CountDownLatch

class DohQueryService(private val out: Output) : QueryService {

    private var dohExecutor: DohQueryExecutor? = null
    private val httpClientBuilder = OkHttpClient().newBuilder()
    private var httpClient: OkHttpClient? = null
    private val parser = DnsMessageParserImpl(ParserOptions.Builder.builder().build())

    override fun connect(settings: ClientSettings): ConnectionResult {
        val staticResolver = StaticDns(settings.resolverIp())
        httpClientBuilder.dns(staticResolver)
        val timeout = settings.timeout()
        httpClientBuilder.connectTimeout(timeout.first, timeout.second)
        httpClient = httpClientBuilder.build()

        dohExecutor = when ((settings as ClientSettingsDoh).method()) {
            HttpMethod.POST -> DohPostQueryExecutor(settings, parser, out)
            HttpMethod.GET -> DohGetQueryExecutor(settings, parser, out)
        }

        return ConnectionResult(settings.resolverIp(), settings.resolverPort())
    }

    override fun query(queries: List<QueryTask>): List<QueryResult> {
        val resultList = mutableListOf<QueryResult>()

        val latch = CountDownLatch(queries.size)
        val requests = dohExecutor!!.execute(queries)

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
                                out.printDebug("$name: $value")
                            }

                            val rawBytes = response.body!!.bytes()
                            resultList.add(parseResponseBytes(rawBytes))
                            latch.countDown()
                        }
                    }
                })
            }

        latch.await()
        httpClient!!.dispatcher.executorService.shutdown()
        return resultList
    }

    fun parseResponseBytes(rawDnsMessage: ByteArray): QueryResult {
        val parsedMessage = parser.parseBytesToMessage(rawDnsMessage)

        out.printSizeIn(rawDnsMessage.size.toLong())
        out.printMessage(parsedMessage.first)
        out.printRawMessage(rawDnsMessage)

        return queryResponse(parsedMessage.first)
    }
}
