package com.linkpoint.chat

import java.util.UUID

/**
 * Represents a chat payload rendered inside the modern Linkpoint UI.
 */
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val author: String,
    val body: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isLocal: Boolean
)

enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED
}
