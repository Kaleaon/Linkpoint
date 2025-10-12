package com.lumiyaviewer.lumiya.cloud.common

import android.os.Bundle
import com.google.common.collect.ImmutableList
import java.util.UUID

/**
 * Modern Kotlin LogMessagesFlushed data class
 * Represents flushed messages for cloud logging
 */
data class LogMessagesFlushed(
    val agentUUID: UUID,
    val messageIDs: ImmutableList<Long>
) : Bundleable {
    
    /**
     * Creates a LogMessagesFlushed from a Bundle
     */
    constructor(bundle: Bundle) : this(
        agentUUID = UUID.fromString(bundle.getString("agentUUID") ?: ""),
        messageIDs = ImmutableList.copyOf(
            (bundle.getLongArray("messageIDs") ?: longArrayOf()).toList()
        )
    )
    
    /**
     * Convenience constructor that accepts a Collection
     */
    constructor(agentUUID: UUID, messageIDs: Collection<Long>) : this(
        agentUUID = agentUUID,
        messageIDs = ImmutableList.copyOf(messageIDs)
    )
    
    /**
     * Converts this message to a Bundle
     */
    override fun toBundle(): Bundle = Bundle().apply {
        putString("agentUUID", agentUUID.toString())
        putLongArray("messageIDs", messageIDs.toLongArray())
    }
}