package ch.dnsmap.dnsm.domain.model

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.query.QueryType

class OptionFlags private constructor(
    val method: HttpMethod?,
    val name: Domain?,
    val resolverHost: String?,
    val resolverPort: Port?,
    val resolverUrl: String?,
    val timeout: Long?,
    val types: List<QueryType>?,
) {

    data class Builder(
        var resolverHost: String? = null,
        var resolverPort: Port? = null,
        var method: HttpMethod? = null,
        var name: Domain? = null,
        var resolverUrl: String? = null,
        var timeout: Long? = null,
        var types: List<QueryType>? = null,
    ) {

        fun method(method: HttpMethod) = apply { this.method = method }
        fun name(name: Domain) = apply { this.name = name }
        fun resolverHost(resolverHost: String) = apply { this.resolverHost = resolverHost }
        fun resolverPort(resolverPort: Port) = apply { this.resolverPort = resolverPort }
        fun resolverUrl(resolverUrl: String) = apply { this.resolverUrl = resolverUrl }
        fun timeout(timeout: Long) = apply { this.timeout = timeout }
        fun types(types: List<QueryType>) = apply { this.types = types }

        fun build() = OptionFlags(
            method = method,
            name = name,
            resolverHost = resolverHost,
            resolverPort = resolverPort,
            resolverUrl = resolverUrl,
            timeout = timeout,
            types = types
        )
    }
}
