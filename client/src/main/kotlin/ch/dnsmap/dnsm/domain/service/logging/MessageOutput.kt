package ch.dnsmap.dnsm.domain.service.logging

import ch.dnsmap.dnsm.Message

open class MessageOutput(printerFunction: (msg: String) -> Unit) : MessageSizeOutput(printerFunction) {

    override fun printMessage(msg: String) {
        print(msg)
    }

    override fun printMessage(msg: Message) {
        val header = msg.header
        val question = msg.question
        val headerString = """
            HEADER
              id:         ${header.id.id}
              flags:      ${header.flags.flags.joinToString(", ")}
              query:      ${header.count.qdCount}
              answer:     ${header.count.anCount}
              authority:  ${header.count.nsCount}
              additional: ${header.count.arCount}
            QUESTION
              ${question.questionName.canonical} ${question.questionClass.asText()} ${question.questionType.asText()}
        """.trimIndent()
        print(headerString)
    }
}
