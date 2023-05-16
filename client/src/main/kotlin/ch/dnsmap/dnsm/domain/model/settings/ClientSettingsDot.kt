package ch.dnsmap.dnsm.domain.model.settings

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol.TCP
import ch.dnsmap.dnsm.domain.model.query.QueryType
import java.net.InetAddress
import java.util.concurrent.TimeUnit

private const val DOT_DEFAULT_PORT = 853

data class ClientSettingsDot(
    val resolverHost: String,
    val resolverIp: InetAddress,
    val resolverPort: Port = Port(DOT_DEFAULT_PORT, TCP),
    val name: Domain,
    val types: List<QueryType>,
    val timeout: Pair<Long, TimeUnit>
) : ClientSettings {

    override
    fun resolverHost(): String {
        return resolverHost
    }

    override
    fun resolverIp(): InetAddress {
        return resolverIp
    }

    override
    fun resolverPort(): Port {
        return resolverPort
    }

    override
    fun name(): Domain {
        return name
    }

    override
    fun types(): List<QueryType> {
        return types
    }

    override
    fun timeout(): Pair<Long, TimeUnit> {
        return timeout
    }
}
