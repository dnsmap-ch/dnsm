package ch.dnsmap.dnsm.domain.service

import java.net.InetAddress

class DummyStubResolverServiceImpl : StubResolverService {

    override fun resolve(hostname: String): InetAddress {
        return InetAddress.getLocalHost()
    }
}
