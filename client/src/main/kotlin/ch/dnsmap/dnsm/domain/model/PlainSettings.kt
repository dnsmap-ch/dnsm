package ch.dnsmap.dnsm.domain.model

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.service.QueryType
import java.net.InetAddress

class PlainSettings(
    val resolverHost: InetAddress,
    val resolverPort: Port,
    val name: Domain,
    val types: List<QueryType>
)
