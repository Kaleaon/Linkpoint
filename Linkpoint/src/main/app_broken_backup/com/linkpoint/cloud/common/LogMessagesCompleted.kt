package com.linkpoint.cloud.common

import android.os.Bundle
import java.util.UUID

/**
 * Modern Kotlin LogMessagesCompleted data class
 * Represents completion of message logging to cloud
 */
data class LogMessagesCompleted(
    val agentUUID: UUID,
    val lastWrittenMessageID: Long,
) : Bundleable {
    /**
     * Creates a LogMessagesCompleted from a Bundle
     */
    constructor(bundle: Bundle) : this(
        agentUUID = UUID.fromString(bundle.getString("agentUUID") ?: ""),
        lastWrittenMessageID = bundle.getLong("lastWrittenMessageID"),
    )

    /**
     * Converts this message to a Bundle
     */
    override fun toBundle(): Bundle =
        Bundle().apply {
            putString("agentUUID", agentUUID.toString())
            putLong("lastWrittenMessageID", lastWrittenMessageID)
        }
}
