package ch.dnsmap.dnsm.domain.model

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.service.QueryType
import java.net.InetAddress
import java.util.concurrent.TimeUnit

class PlainSettings(
    val resolverHost: InetAddress,
    val resolverPort: Port,
    val name: Domain,
    val types: List<QueryType>,
    val timeout: Pair<Long, TimeUnit>
)
