package ch.dnsmap.dnsm.domain.model.settings

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.query.QueryType
import java.net.InetAddress
import java.util.concurrent.TimeUnit

interface ClientSettings {

    fun resolverHost(): InetAddress
    fun resolverPort(): Port
    fun name(): Domain
    fun types(): List<QueryType>
    fun timeout(): Pair<Long, TimeUnit>
}
