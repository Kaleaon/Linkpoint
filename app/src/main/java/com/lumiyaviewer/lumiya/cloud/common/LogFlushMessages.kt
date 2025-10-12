package com.lumiyaviewer.lumiya.cloud.common

import android.os.Bundle
import java.util.UUID

/**
 * Modern Kotlin LogFlushMessages data class
 * Represents a request to flush messages for cloud logging
 */
data class LogFlushMessages(
    val agentUUID: UUID,
    val agentName: String?,
    val chatterName: String?
) : Bundleable {
    
    /**
     * Creates a LogFlushMessages from a Bundle
     */
    constructor(bundle: Bundle) : this(
        agentUUID = UUID.fromString(bundle.getString("agentUUID") ?: ""),
        agentName = bundle.getString("agentName"),
        chatterName = bundle.getString("chatterName")
    )
    
    /**
     * Converts this message to a Bundle
     */
    override fun toBundle(): Bundle = Bundle().apply {
        putString("agentUUID", agentUUID.toString())
        putString("agentName", agentName ?: "")
        putString("chatterName", chatterName)
    }
}