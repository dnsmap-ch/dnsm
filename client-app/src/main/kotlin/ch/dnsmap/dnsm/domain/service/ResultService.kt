package ch.dnsmap.dnsm.domain.service

import ch.dnsmap.dnsm.domain.model.Result

interface ResultService {

    fun run(): Result
}
