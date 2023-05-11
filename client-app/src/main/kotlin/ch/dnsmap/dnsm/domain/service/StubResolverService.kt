package ch.dnsmap.dnsm.domain.service

import java.net.InetAddress

interface StubResolverService {

    /**
     * Returns an IP address of the provided [hostname] by querying the OS stub resolver,
     * if [hostname] not already is an IP address.
     */
    fun resolve(hostname: String): InetAddress
}
