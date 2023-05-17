package ch.dnsmap.dnsm.domain.service.logging

private const val BYTES_PER_HALF_LINE = 8
private const val BYTES_PER_LINE = 16
private const val SPACES_PER_HALF_LINE = 24
private const val SPACES_PER_LINE = 48

class DebugOutput(printerFunction: (msg: String) -> Unit) : MessageOutput(printerFunction) {

    override
    fun printDebug(msg: String) {
        print(msg)
    }

    override
    fun printRawMessage(msg: ByteArray) {
        prettyHex(msg)
    }

    private fun prettyHex(byteArray: ByteArray) {
        byteArray.asSequence()
            .chunked(BYTES_PER_LINE)
            .map { splitBytesIntoTwoParts(it) }
            .map { formatBytesPrintable(it) }
            .withIndex()
            .forEach { logEachByteLine(it.index, it.value) }
    }

    private fun splitBytesIntoTwoParts(upTo16Bytes: List<Byte>): Raw16Bytes {
        val leftHexBytes = upTo16Bytes.take(BYTES_PER_HALF_LINE)
        return if (upTo16Bytes.size > BYTES_PER_HALF_LINE) {
            val rightHexBytes = upTo16Bytes.takeLast(BYTES_PER_HALF_LINE)
            Raw16Bytes(leftHexBytes, rightHexBytes)
        } else {
            Raw16Bytes(leftHexBytes, emptyList())
        }
    }

    private fun formatBytesPrintable(it: Raw16Bytes) =
        if (it.rightBytes.isNotEmpty()) {
            val leftByteString = formatDataAsHexString(it.leftBytes, SPACES_PER_HALF_LINE)
            val rightByteString = formatDataAsHexString(it.rightBytes, SPACES_PER_HALF_LINE)
            val byteString = "$leftByteString  $rightByteString"

            val leftChars = charsOrDot(it.leftBytes).joinToString(separator = "")
            val rightChars = charsOrDot(it.rightBytes).joinToString(separator = "")
            val charString = "|$leftChars $rightChars|"

            FormattedBytes(byteString, charString)
        } else {
            val byteString = formatDataAsHexString(it.leftBytes, SPACES_PER_LINE)
            val charString = charsOrDot(it.leftBytes).joinToString(
                separator = "",
                prefix = "  |",
                postfix = "|"
            )
            FormattedBytes(byteString, charString)
        }

    private fun logEachByteLine(index: Int, formatted: FormattedBytes) {
        val counter = index * BYTES_PER_LINE
        print("%08x  %s  %s".format(counter, formatted.byteString, formatted.charString))
    }

    private fun formatDataAsHexString(eightBytes: List<Byte>, nofSpaceTillEnd: Int): String {
        return eightBytes.joinToString(" ") { "%02x".format(it) }.padEnd(nofSpaceTillEnd)
    }

    private fun charsOrDot(dataBytes: List<Byte>) = dataBytes.map {
        val char = it.toInt().toChar()
        if (char.isLetterOrDigit()) {
            char
        } else {
            '.'
        }
    }
}

class Raw16Bytes(val leftBytes: List<Byte>, val rightBytes: List<Byte>)
class FormattedBytes(val byteString: String, val charString: String)
