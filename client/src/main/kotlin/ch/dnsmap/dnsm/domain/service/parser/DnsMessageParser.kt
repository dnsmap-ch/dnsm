package ch.dnsmap.dnsm.domain.service.parser

import ch.dnsmap.dnsm.Message

interface DnsMessageParser {

    /**
     * Translates raw bytes into a DNS message.
     * Input expected is without length field (as used for TCP connections).
     */
    fun parseBytesToMessage(rawDnsMessage: ByteArray): Message

    /**
     * Translates a DNS message to its raw bytes.
     */
    fun parseMessageToBytes(message: Message): ByteArray
}
