package ch.dnsmap.dnsm.domain.service.plain

import ch.dnsmap.dnsm.domain.model.networking.Protocol.UDP
import ch.dnsmap.dnsm.domain.model.query.ConnectionResult
import ch.dnsmap.dnsm.domain.model.query.QueryResult
import ch.dnsmap.dnsm.domain.model.query.QueryTask
import ch.dnsmap.dnsm.domain.model.settings.ClientSettings
import ch.dnsmap.dnsm.domain.model.settings.ClientSettingsPlain
import ch.dnsmap.dnsm.domain.service.QueryService

class PlainQueryService(
    private val plainTcpService: PlainTcpService,
    private val plainUdpService: PlainUdpService,
) : QueryService {

    private var settings: ClientSettingsPlain? = null

    override fun connect(settings: ClientSettings): ConnectionResult {
        this.settings = settings as ClientSettingsPlain

        return if (settings.resolverPort().protocol == UDP) {
            plainUdpService.connect(settings)
        } else {
            plainTcpService.connect(settings)
        }
    }

    override fun query(queries: List<QueryTask>): List<QueryResult> {
        return if (settings!!.resolverPort().protocol == UDP) {
            plainUdpService.query(queries)
        } else {
            plainTcpService.query(queries)
        }
    }
}
