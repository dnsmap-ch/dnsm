package ch.dnsmap.dnsm.domain.service.doh

import okhttp3.Dns
import java.net.InetAddress

class StaticDns(val address: InetAddress) : Dns {

    override fun lookup(hostname: String): List<InetAddress> {
        return listOf(address)
    }
}
