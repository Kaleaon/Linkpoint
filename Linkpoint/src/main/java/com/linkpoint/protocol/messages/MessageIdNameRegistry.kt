package com.linkpoint.protocol.messages

internal object MessageIdNameRegistry {
    fun getMessageName(messageId: Int, messageNameMap: Map<Int, String>): String {
        return messageNameMap[messageId] ?: formatUnknownMessageId(messageId)
    }

    fun formatUnknownMessageId(messageId: Int): String {
        return "Unknown(0x${formatHex(messageId)})"
    }

    /**
     * Format a Second Life message ID as an unsigned hex string suitable for
     * logs and debug reports.
     *
     * SL message IDs span three frequency classes that all share a single
     * Kotlin `Int` slot:
     *   - High   (1 byte):  0x01..0xFE        -> positive 1..254
     *   - Medium (2 bytes): 0xFF01..0xFFFE    -> positive 65281..65534
     *   - Low    (4 bytes): 0xFFFF0001..0xFFFFFFFE
     *
     * Low-frequency IDs are produced by `(short or 0xFFFF0000)`. Since Kotlin's
     * `Int` is signed, ORing in the top 16 bits flips the sign — for example
     * RegionHandshake (0xFFFF0094) lands as -65388. Calling `toString(16)` on
     * that prints `"-ffff0094"`, which is how user-visible logs ended up
     * showing IDs like `0x-FFFD` or `(ID: -65452)`.
     *
     * Masking through `Long and 0xFFFFFFFFL` reinterprets the bits as unsigned
     * before formatting, recovering the protocol-correct value. Width is
     * chosen per band so the output matches the on-wire encoding length.
     */
    fun formatHex(messageId: Int): String {
        return when {
            messageId in 1..254 ->
                messageId.toString(16).uppercase().padStart(2, '0')
            messageId in 65280..65535 -> {
                val lowByte = messageId and 0xFF
                "FF${lowByte.toString(16).uppercase().padStart(2, '0')}"
            }
            messageId < 0 -> {
                val unsignedVal = messageId.toLong() and 0xFFFFFFFFL
                unsignedVal.toString(16).uppercase().padStart(8, '0')
            }
            else -> messageId.toString(16).uppercase()
        }
    }
}
