package ch.dnsmap.dnsm.domain.service.parser

import ch.dnsmap.dnsm.Message
import ch.dnsmap.dnsm.wire.DnsInput
import ch.dnsmap.dnsm.wire.DnsOutput
import ch.dnsmap.dnsm.wire.ParserOptions

class DnsMessageParserImpl(private val parserOptions: ParserOptions) : DnsMessageParser {

    override
    fun parseBytesToMessage(rawDnsMessage: ByteArray): Pair<Message, Long> {
        val dnsInput = DnsInput.fromWire(parserOptions, rawDnsMessage)
        val message = dnsInput.message
        val parsedBytes = dnsInput.bytesParsed()
        return Pair(message, parsedBytes)
    }

    override fun parseMessageToBytes(message: Message): ByteArray {
        return DnsOutput.toWire(
            parserOptions,
            message.header,
            message.question,
            safelyParseAnswer(message),
            safelyParseAuthority(message),
            safelyParseAdditional(message)
        ).message
    }

    private fun safelyParseAnswer(message: Message) =
        if (message.header.count.anCount == 0 || message.answer == null) {
            emptyList()
        } else {
            message.answer
        }

    private fun safelyParseAuthority(message: Message) =
        if (message.header.count.nsCount == 0 || message.authority == null) {
            emptyList()
        } else {
            message.authority
        }

    private fun safelyParseAdditional(message: Message) =
        if (message.header.count.arCount == 0 || message.additional == null) {
            emptyList()
        } else {
            message.additional
        }
}
