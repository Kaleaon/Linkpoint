package com.linkpoint.protocol.messages.ids

object MessageIdNameRegistry {
    fun getMessageName(messageId: Int, messageNameMap: Map<Int, String>): String =
        messageNameMap[messageId] ?: "Unknown($messageId)"
}
