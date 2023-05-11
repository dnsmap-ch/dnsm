package ch.dnsmap.dnsm.domain.model.settings

import ch.dnsmap.dnsm.domain.model.HttpMethod
import java.net.URI

interface ClientSettingsDoh : ClientSettings {

    fun url(): URI
    fun method(): HttpMethod
}
