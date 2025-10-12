package com.lumiyaviewer.lumiya.cloud.common

import android.os.Bundle

/**
 * Modern Kotlin LogSyncStatus data class
 * Represents the status of a log synchronization session
 */
data class LogSyncStatus(
    val pluginVersionCode: Int,
    val status: Status,
    val errorMessage: String?
) : Bundleable {
    
    /**
     * Synchronization status enum
     */
    enum class Status {
        Ready,
        AppVersionRejected,
        GoogleDriveError
    }
    
    /**
     * Creates a LogSyncStatus from a Bundle
     */
    constructor(bundle: Bundle) : this(
        pluginVersionCode = bundle.getInt("pluginVersionCode"),
        status = Status.valueOf(bundle.getString("status") ?: "Ready"),
        errorMessage = bundle.getString("errorMessage")
    )
    
    /**
     * Converts this status to a Bundle
     */
    override fun toBundle(): Bundle = Bundle().apply {
        putInt("pluginVersionCode", pluginVersionCode)
        putString("status", status.toString())
        putString("errorMessage", errorMessage)
    }
}