package com.linkpoint.chat.queue

import com.linkpoint.ui.chat.ChatChannel
import com.linkpoint.ui.chat.ChatMessage as UiChatMessage

/**
 * Immutable UI state snapshot produced by the Frame Manager on frame ticks.
 *
 * Consumed by the Jetpack Compose chat UI to guarantee frame-aligned rendering
 * without direct network packet coupling.
 */
data class ChatViewState(
    val messages: List<UiChatMessage> = emptyList(),
    val filteredMessagesByChannel: Map<ChatChannel, List<UiChatMessage>> = emptyMap(),
    val frameTimestampNanos: Long = 0L,
    val pendingQueueSize: Int = 0,
    val totalProcessedCount: Long = 0L,
    val backpressureEventCount: Long = 0L,
) {
    /**
     * Helper to retrieve immutable pre-filtered messages for a specific channel.
     */
    fun messagesForChannel(channel: ChatChannel): List<UiChatMessage> {
        return filteredMessagesByChannel[channel] ?: messages.filter { it.channel == channel }
    }
}
