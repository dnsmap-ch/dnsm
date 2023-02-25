package ch.dnsmap.dnsm.domain.model.query

import ch.dnsmap.dnsm.domain.model.networking.Port
import java.net.InetAddress
import kotlin.time.Duration

data class ConnectionResult(val resolverHost: InetAddress, val resolverPort: Port)

data class ConnectionResultTimed(val connectionResult: ConnectionResult, val duration: Duration)
