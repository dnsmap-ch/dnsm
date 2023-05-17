package ch.dnsmap.dnsm.domain.model.logging

enum class VerbosityLevel {
    /**
     * Output of all relevant information for a regular use case and user.
     */
    NORMAL,

    /**
     * Output of message size and direction of travel.
     * Includes NORMAL
     */
    MESSAGE_SIZE,

    /**
     * Output of message content.
     * Includes NORMAL, MESSAGE_SIZE
     */
    MESSAGE,

    /**
     * For debugging and troubleshooting purposes. Operates on byte level.
     * Includes NORMAL, MESSAGE_SIZE, MESSAGE
     */
    DEBUG
}
