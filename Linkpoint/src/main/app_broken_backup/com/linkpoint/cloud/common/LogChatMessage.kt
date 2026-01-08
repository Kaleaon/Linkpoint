package com.linkpoint.cloud.common

import android.os.Bundle
import java.util.UUID

/**
 * Modern Kotlin LogChatMessage data class
 * Represents a chat message for cloud logging
 */
data class LogChatMessage(
    val chatterType: Int,
    val chatterUUID: UUID?,
    val messageID: Long,
    val chatterName: String,
    val messageText: String,
) : Bundleable {
    /**
     * Creates a LogChatMessage from a Bundle
     */
    constructor(bundle: Bundle) : this(
        chatterType = bundle.getInt("chatterType"),
        chatterUUID = bundle.getString("chatterUUID")?.let { UUID.fromString(it) },
        messageID = bundle.getLong("messageID"),
        chatterName = bundle.getString("chatterName") ?: "",
        messageText = bundle.getString("messageText") ?: "",
    )

    /**
     * Converts this message to a Bundle
     */
    override fun toBundle(): Bundle =
        Bundle().apply {
            putInt("chatterType", chatterType)
            chatterUUID?.let { putString("chatterUUID", it.toString()) }
            putLong("messageID", messageID)
            putString("chatterName", chatterName)
            putString("messageText", messageText)
        }
}
