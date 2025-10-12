package com.lumiyaviewer.lumiya.cloud.common

import android.os.Bundle
import java.util.UUID

/**
 * Modern Kotlin LogSyncStart data class
 * Represents the start of a log synchronization session
 */
data class LogSyncStart(
    val appVersionCode: Int,
    val agentUUID: UUID
) : Bundleable {
    
    /**
     * Creates a LogSyncStart from a Bundle
     */
    constructor(bundle: Bundle) : this(
        appVersionCode = bundle.getInt("appVersionCode"),
        agentUUID = UUID.fromString(bundle.getString("agentUUID") ?: "")
    )
    
    /**
     * Converts this message to a Bundle
     */
    override fun toBundle(): Bundle = Bundle().apply {
        putInt("appVersionCode", appVersionCode)
        putString("agentUUID", agentUUID.toString())
    }
}