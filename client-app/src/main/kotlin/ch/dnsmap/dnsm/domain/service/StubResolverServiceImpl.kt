package ch.dnsmap.dnsm.domain.service

import java.net.InetAddress

class StubResolverServiceImpl : StubResolverService {

    override
    fun resolve(hostname: String): InetAddress {
        return InetAddress.getByName(hostname)
    }
}
