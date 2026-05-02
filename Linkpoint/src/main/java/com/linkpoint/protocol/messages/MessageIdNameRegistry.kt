package com.linkpoint.protocol.messages

internal object MessageIdNameRegistry {
    fun getMessageName(messageId: Int, messageNameMap: Map<Int, String>): String {
        return messageNameMap[messageId] ?: formatUnknownMessageId(messageId)
    }

    fun formatUnknownMessageId(messageId: Int): String {
        return when {
            messageId in 1..254 -> "Unknown(0x${messageId.toString(16).uppercase().padStart(2, '0')})"
            messageId in 65280..65535 -> {
                val lowByte = messageId and 0xFF
                "Unknown(0xFF${lowByte.toString(16).uppercase().padStart(2, '0')})"
            }
            messageId < 0 -> {
                val unsignedVal = messageId.toLong() and 0xFFFFFFFFL
                "Unknown(0x${unsignedVal.toString(16).uppercase().padStart(8, '0')})"
            }
            else -> "Unknown(0x${messageId.toString(16).uppercase()})"
        }
    }
}
