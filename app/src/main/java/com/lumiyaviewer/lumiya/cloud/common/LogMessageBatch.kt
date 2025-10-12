package com.lumiyaviewer.lumiya.cloud.common

import android.os.Bundle
import com.google.common.collect.ImmutableList
import java.util.UUID

/**
 * Modern Kotlin LogMessageBatch data class
 * Represents a batch of chat messages for cloud logging
 */
data class LogMessageBatch(
    val agentUUID: UUID,
    val agentName: String,
    val messages: ImmutableList<LogChatMessage>,
    val lastMessageID: Long
) : Bundleable {
    
    /**
     * Creates a LogMessageBatch from a Bundle
     */
    constructor(bundle: Bundle) : this(
        agentUUID = UUID.fromString(bundle.getString("agentUUID") ?: ""),
        agentName = bundle.getString("agentName") ?: "",
        messages = run {
            val builder = ImmutableList.builder<LogChatMessage>()
            bundle.getParcelableArray("messages")?.forEach { parcelable ->
                if (parcelable is Bundle) {
                    builder.add(LogChatMessage(parcelable))
                }
            }
            builder.build()
        },
        lastMessageID = bundle.getLong("lastMessageID")
    )
    
    /**
     * Convenience constructor that accepts a List
     */
    constructor(
        agentUUID: UUID,
        agentName: String,
        messages: List<LogChatMessage>,
        lastMessageID: Long
    ) : this(
        agentUUID = agentUUID,
        agentName = agentName,
        messages = ImmutableList.copyOf(messages),
        lastMessageID = lastMessageID
    )
    
    /**
     * Converts this batch to a Bundle
     */
    override fun toBundle(): Bundle = Bundle().apply {
        putString("agentUUID", agentUUID.toString())
        putString("agentName", agentName)
        putLong("lastMessageID", lastMessageID)
        
        val messageBundles = Array(messages.size) { index ->
            messages[index].toBundle()
        }
        putParcelableArray("messages", messageBundles)
    }
}