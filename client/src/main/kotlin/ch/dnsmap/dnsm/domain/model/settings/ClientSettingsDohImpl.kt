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

class ClientSettingsDohImpl private constructor(
    private val resolverHost: String?,
    private val resolverIp: InetAddress?,
    private val resolverPort: Port?,
    private val name: Domain?,
    private val types: List<QueryType>?,
    private val timeout: Pair<Long, TimeUnit>?,
    private val url: URI?,
    private val method: HttpMethod?,
) : ClientSettingsDoh {

    override
    fun resolverHost(): String {
        return resolverHost!!
    }

    override
    fun resolverIp(): InetAddress {
        return resolverIp!!
    }

    override
    fun resolverPort(): Port {
        return resolverPort!!
    }

    override
    fun name(): Domain {
        return name!!
    }

    override
    fun types(): List<QueryType> {
        return types!!
    }

    override
    fun timeout(): Pair<Long, TimeUnit> {
        return timeout!!
    }

    override
    fun url(): URI {
        return url!!.resolve("/")
    }

    override
    fun method(): HttpMethod {
        return method!!
    }

    data class ClientSettingsDohImplBuilder(
        var resolverHost: String? = null,
        var resolverIp: InetAddress? = null,
        var resolverPort: Port = Port(DOH_DEFAULT_PORT, TCP),
        var name: Domain? = null,
        var types: List<QueryType>? = null,
        var timeout: Pair<Long, TimeUnit>? = null,
        var url: URI? = null,
        var method: HttpMethod = POST,
    ) {

        fun resolverHost(resolverHost: String) = apply { this.resolverHost = resolverHost }
        fun resolverIp(resolverIp: InetAddress) = apply { this.resolverIp = resolverIp }
        fun resolverPort(resolverPort: Port) = apply { this.resolverPort = resolverPort }
        fun name(name: Domain) = apply { this.name = name }
        fun types(types: List<QueryType>) = apply { this.types = types }
        fun timeout(timeout: Pair<Long, TimeUnit>) = apply { this.timeout = timeout }
        fun url(url: URI) = apply { this.url = url }
        fun method(method: HttpMethod) = apply { this.method = method }

        fun build() = ClientSettingsDohImpl(
            resolverHost,
            resolverIp,
            resolverPort,
            name,
            types,
            timeout,
            url,
            method
        )
    }
}
