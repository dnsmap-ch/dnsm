package ch.dnsmap.dnsm.domain.model.settings

import ch.dnsmap.dnsm.Domain
import ch.dnsmap.dnsm.domain.model.networking.Port
import ch.dnsmap.dnsm.domain.model.networking.Protocol.UDP
import ch.dnsmap.dnsm.domain.model.query.QueryType.A
import ch.dnsmap.dnsm.domain.model.query.QueryType.AAAA
import java.net.InetAddress
import java.util.concurrent.TimeUnit.SECONDS

private const val PLAIN_DEFAULT_PORT = 53

fun plainSettings(): ClientSettingsPlain {
    return ClientSettingsPlain.ClientSettingsPlainBuilder()
        .resolverHost("127.0.0.1")
        .resolverIp(InetAddress.getByName("127.0.0.1"))
        .resolverPort(Port(PLAIN_DEFAULT_PORT, UDP))
        .name(Domain.of("example.org"))
        .types(listOf(A, AAAA))
        .timeout(Pair(42, SECONDS))
        .build()
}
