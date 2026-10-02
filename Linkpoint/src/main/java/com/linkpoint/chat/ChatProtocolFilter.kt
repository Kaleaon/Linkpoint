package com.linkpoint.chat

import com.linkpoint.protocol.messages.ChatData
import com.linkpoint.protocol.messages.ChatSourceType
import com.linkpoint.protocol.messages.ChatType
import java.util.UUID

/**
 * ChatProtocolFilter middleware for spatial logs and chat streams.
 *
 * Sanitizes incoming spatial chat packets before dispatch to UI:
 * 1. Strips script channel prefixes (e.g., `/0`, `/11`, `[0]`, `/2147483647:`).
 * 2. Formats embedded UUID strings into human-readable references.
 * 3. Converts raw system event codes into friendly localized text strings.
 */
object ChatProtocolFilter {

    // Regex matching channel prefixes at start of string, e.g.:
    // "/0 ", "/11 ", "/0: ", "/11: ", "[-12345]: ", "[0] ", "/2147483647 "
    private val CHANNEL_PREFIX_REGEX = Regex("""^(?:/(?:-[0-9]+|[0-9]+)[: ]?|\[(?:-[0-9]+|[0-9]+)\][: ]?)\s*""")

    // Regex matching standard 36-character canonical UUID strings (8-4-4-4-12 hex)
    private val UUID_REGEX = Regex("""\b[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\b""")

    // Already formatted UUID reference pattern e.g. [UUID: ...]
    private val ALREADY_FORMATTED_UUID_REGEX = Regex("""\[UUID:\s*[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\]""")

    // Known system event codes map to friendly localized strings
    private val SYSTEM_EVENT_MAP = mapOf(
        "SYS_CONNECTED" to "Connected to region",
        "SYS_DISCONNECTED" to "Disconnected from server",
        "SYS_CONNECTING" to "Connecting to region...",
        "SYS_TELEPORT_START" to "Teleport in progress...",
        "SYS_TELEPORT_PROGRESS" to "Teleporting...",
        "SYS_TELEPORT_FAILED" to "Teleport failed",
        "SYS_TELEPORT_FINISHED" to "Teleport completed",
        "SYS_REGION_CROSS" to "Crossing region boundary",
        "SYS_AGENT_KILLED" to "Disconnected by server",
        "SYS_INVENTORY_OFFERED" to "Inventory item offered",
        "SYS_GROUP_NOTICE" to "Group notice received",
        "SYS_FRIEND_ONLINE" to "Friend came online",
        "SYS_FRIEND_OFFLINE" to "Friend went offline",
        "SYS_MUTE" to "Mute list updated",
        "SYS_BALANCE" to "Currency balance updated",
        "SYS_ALERT" to "System alert",
        "100" to "System Information",
        "101" to "System Warning",
        "102" to "System Error",
        "200" to "Connection Established",
        "201" to "Connection Terminated",
        "400" to "Bad Request",
        "404" to "Resource Not Found",
        "500" to "Server Error",
        "1001" to "Region Notification",
        "1002" to "Grid Broadcast"
    )

    /**
     * Filter a spatial chat message string.
     *
     * @param message Raw input message
     * @param sourceType Origin source type (AGENT, OBJECT, SYSTEM)
     * @param chatType Chat type (WHISPER, NORMAL, SHOUT, DEBUG, etc.)
     * @return Filtered, human-readable chat string
     */
    fun filter(
        message: String,
        sourceType: ChatSourceType = ChatSourceType.AGENT,
        chatType: ChatType = ChatType.NORMAL
    ): String {
        if (message.isEmpty()) return message

        // 1. Transform system event codes if message represents a system event
        var filtered = if (sourceType == ChatSourceType.SYSTEM || message.startsWith("SYS_")) {
            transformSystemEvent(message)
        } else {
            message
        }

        // 2. Strip script channel prefixes (especially for OBJECT, DEBUG, or channel-prefixed spatial chat)
        if (sourceType == ChatSourceType.OBJECT || chatType == ChatType.DEBUG || isChannelPrefixed(filtered)) {
            filtered = stripChannelPrefix(filtered)
        }

        // 3. Format embedded raw UUID strings into human-readable references
        filtered = formatUuidReferences(filtered)

        return filtered
    }

    /**
     * Strips channel prefixes like "/0 ", "/11 ", "/0: ", "[0] ", "/2147483647: " from the message start.
     * Preserves command prefixes like "/me", "/shout", "/whisper".
     */
    fun stripChannelPrefix(message: String): String {
        if (message.isEmpty()) return message

        // Exclude slash commands that are user interactions, not script channel numbers
        val lower = message.trimStart().lowercase()
        if (lower.startsWith("/me ") || lower.startsWith("/shout ") || lower.startsWith("/whisper ")) {
            return message
        }

        val match = CHANNEL_PREFIX_REGEX.find(message)
        return if (match != null) {
            val prefix = match.value
            val remainder = message.substring(prefix.length)
            // Ensure remainder is not left with only leading whitespace
            if (remainder.isNotEmpty()) remainder else message
        } else {
            message
        }
    }

    /**
     * Checks whether a message starts with a channel prefix pattern.
     */
    private fun isChannelPrefixed(message: String): Boolean {
        return CHANNEL_PREFIX_REGEX.containsMatchIn(message)
    }

    /**
     * Replaces raw 36-char UUID strings in the message with formatted references: "[UUID: <uuid>]".
     * Gracefully handles malformed UUID strings without crashing.
     */
    fun formatUuidReferences(message: String): String {
        if (message.isEmpty()) return message

        return try {
            // Find UUIDs that are not already enclosed in [UUID: ...]
            UUID_REGEX.replace(message) { matchResult ->
                val uuidStr = matchResult.value
                val startIdx = matchResult.range.first
                val endIdx = matchResult.range.last + 1

                // Check if already inside [UUID: ...]
                val prefixContext = if (startIdx >= 7) message.substring(startIdx - 7, startIdx) else ""
                if (prefixContext.equals("[UUID: ", ignoreCase = true) || prefixContext.endsWith("UUID: ")) {
                    uuidStr
                } else {
                    try {
                        // Validate UUID parsing to catch malformed UUID edge cases
                        UUID.fromString(uuidStr)
                        "[UUID: $uuidStr]"
                    } catch (e: Exception) {
                        // Malformed UUID: preserve original match gracefully
                        uuidStr
                    }
                }
            }
        } catch (e: Exception) {
            // Fallback gracefully on unexpected errors
            message
        }
    }

    /**
     * Converts a raw protocol system event code/string to a friendly localized text string.
     */
    fun transformSystemEvent(rawMessageOrCode: String): String {
        val trimmed = rawMessageOrCode.trim()
        if (trimmed.isEmpty()) return rawMessageOrCode

        // Direct lookup
        SYSTEM_EVENT_MAP[trimmed]?.let { return it }

        // Case-insensitive lookup for SYS_ codes
        val upperCode = trimmed.uppercase()
        SYSTEM_EVENT_MAP[upperCode]?.let { return it }

        // Handle pattern "SYS_<CODE>: extra detail"
        if (upperCode.startsWith("SYS_")) {
            val parts = trimmed.split(":", limit = 2)
            val codeKey = parts[0].trim().uppercase()
            val mappedKey = SYSTEM_EVENT_MAP[codeKey]
            if (mappedKey != null) {
                return if (parts.size > 1 && parts[1].isNotBlank()) {
                    "$mappedKey:${parts[1]}"
                } else {
                    mappedKey
                }
            }
            return "System Event (${trimmed.removePrefix("SYS_").removePrefix("sys_")})"
        }

        // Handle numeric event codes like "1001" or "Event 1001"
        if (trimmed.all { it.isDigit() }) {
            return SYSTEM_EVENT_MAP[trimmed] ?: "System Event ($trimmed)"
        }

        return rawMessageOrCode
    }

    /**
     * Filter helper for ChatData packet model.
     */
    fun filterChatData(chatData: ChatData): ChatData {
        val filtered = filter(
            message = chatData.message,
            sourceType = chatData.sourceType,
            chatType = chatData.chatType
        )
        return chatData.copy(message = filtered)
    }

    /**
     * Filter helper for ChatMessage UI data model.
     */
    fun filterChatMessage(chatMessage: ChatMessage): ChatMessage {
        val filtered = filter(
            message = chatMessage.message,
            sourceType = chatMessage.sourceType,
            chatType = chatMessage.chatType
        )
        return chatMessage.copy(message = filtered)
    }
}
