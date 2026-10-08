package com.linkpoint.push

import com.google.firebase.messaging.RemoteMessage
import java.util.UUID

object PushEventParser {
    fun parse(message: RemoteMessage): PushEvent {
        val data = message.data
        val type = when (data["type"]?.lowercase()) {
            "im" -> PushEventType.IM
            "group_notice", "group" -> PushEventType.GROUP_NOTICE
            else -> PushEventType.UNKNOWN
        }

        val title = data["title"] ?: message.notification?.title ?: "Linkpoint"
        val body = data["message"] ?: message.notification?.body ?: "New activity"
        val dedupeId = data["event_id"]
            ?: data["dedupe_id"]
            ?: message.messageId
            ?: UUID.randomUUID().toString()

        return PushEvent(
            dedupeId = dedupeId,
            type = type,
            sessionId = data["session_id"]?.toUuidOrNull(),
            senderId = data["sender_id"]?.toUuidOrNull(),
            senderName = data["sender_name"],
            title = title,
            message = body,
            timestampMs = data["timestamp_ms"]?.toLongOrNull() ?: System.currentTimeMillis()
        )
    }

    private fun String.toUuidOrNull(): UUID? = try {
        UUID.fromString(this)
    } catch (_: IllegalArgumentException) {
        null
    }
}
