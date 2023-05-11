package ch.dnsmap.dnsm.domain.model.settings

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.HttpMethod
import ch.dnsmap.dnsm.domain.model.HttpMethod.POST
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol.TCP
import ch.dnsmap.dnsm.domain.model.query.QueryType
import java.net.InetAddress
import java.net.URI
import java.util.concurrent.TimeUnit

private const val DOH_DEFAULT_PORT = 443

data class ClientSettingsDohImpl(
    val resolverHost: String,
    val resolverIp: InetAddress,
    val resolverPort: Port = Port(DOH_DEFAULT_PORT, TCP),
    val name: Domain,
    val types: List<QueryType>,
    val timeout: Pair<Long, TimeUnit>,
    val url: URI,
    val path: String = "dns-query",
    val method: HttpMethod = POST
) : ClientSettingsDoh {

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

    override
    fun url(): URI {
        return url.resolve(path)
    }

    override
    fun method(): HttpMethod {
        return method
    }
}
