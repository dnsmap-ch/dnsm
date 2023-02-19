package ch.dnsmap.dnsm.domain.model

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.networking.Port
import java.net.InetAddress
import java.util.concurrent.TimeUnit

data class ClientSettingsPlain(
    val resolverHost: InetAddress,
    val resolverPort: Port,
    val name: Domain,
    val types: List<QueryType>,
    val timeout: Pair<Long, TimeUnit>
) : ClientSettings {

    override fun resolverHost(): InetAddress {
        return resolverHost
    }

    override fun resolverPort(): Port {
        return resolverPort
    }

    override fun name(): Domain {
        return name
    }

    override fun types(): List<QueryType> {
        return types
    }

    override fun timeout(): Pair<Long, TimeUnit> {
        return timeout
    }
}
