package ch.dnsmap.dnsm.domain.service.plain

import ch.dnsmap.dnsm.domain.model.query.ConnectionResult
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.settings.ClientSettings
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsPlain
import ch.dnsmap.dnsm.domain.service.QueryService
import ch.dnsmap.dnsm.domain.service.logging.Output
import ch.dnsmap.dnsm.domain.service.network.TcpService
import ch.dnsmap.dnsm.domain.service.parser.DnsMessageParserImpl
import ch.dnsmap.dnsm.wire.ParserOptions
import java.net.Socket

class PlainTcpService(private val out: Output) : QueryService {

    private var socket: Socket? = null
    private var settings: ClientSettingsPlain? = null
    private val parserOutput = DnsMessageParserImpl(ParserOptions.Builder.builder().setTcp().build())
    private val parserInput = DnsMessageParserImpl(ParserOptions.Builder.builder().build())

    override
    fun connect(settings: ClientSettings): ConnectionResult {
        this.settings = settings as ClientSettingsPlain
        socket = Socket(settings.resolverHost(), settings.resolverPort().port)
        return ConnectionResult(settings.resolverIp(), settings.resolverPort())
    }

    override
    fun query(queries: List<QueryTask>): List<QueryResult> {
        requireNotNull(socket)
        val tcp = TcpService(settings!!, socket!!, parserOutput, parserInput, out)
        return tcp.query(queries)
    }
}
