package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.Result
import ch.dnsmap.dnsm.domain.model.settings.ClientSettings

interface ResultService {

    fun run(settings: ClientSettings): Result
}
