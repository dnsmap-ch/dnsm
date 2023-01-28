package ch.dnsmap.dnsm.domain.model

import ch.dnsmap.dnsm.domain.model.networking.Port
import java.net.InetAddress

class PlainSettings(
    val resolverHost: InetAddress,
    val resolverPort: Port,
    val name: String,
    val types: List<String>
) {
}