package ch.dnsmap.dnsm.domain.model.settings

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol.TCP
import ch.dnsmap.dnsm.domain.model.query.QueryType
import java.net.InetAddress
import java.util.concurrent.TimeUnit

private const val DOT_DEFAULT_PORT = 853

class ClientSettingsDot private constructor(
    private val resolverHost: String,
    private val resolverIp: InetAddress,
    private val resolverPort: Port,
    private val name: Domain,
    private val types: List<QueryType>,
    private val timeout: Pair<Long, TimeUnit>
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

    data class ClientSettingsDotBuilder(
        var resolverHost: String? = null,
        var resolverIp: InetAddress? = null,
        var resolverPort: Port = Port(DOT_DEFAULT_PORT, TCP),
        var name: Domain? = null,
        var types: List<QueryType>? = null,
        var timeout: Pair<Long, TimeUnit>? = null
    ) {

        fun resolverHost(resolverHost: String) = apply { this.resolverHost = resolverHost }
        fun resolverIp(resolverIp: InetAddress) = apply { this.resolverIp = resolverIp }
        fun resolverPort(resolverPort: Port) = apply { this.resolverPort = resolverPort }
        fun name(name: Domain) = apply { this.name = name }
        fun types(types: List<QueryType>) = apply { this.types = types }
        fun timeout(timeout: Pair<Long, TimeUnit>) = apply { this.timeout = timeout }

        fun build() = ClientSettingsDot(
            resolverHost!!,
            resolverIp!!,
            resolverPort,
            name!!,
            types!!,
            timeout!!
        )
    }
}
