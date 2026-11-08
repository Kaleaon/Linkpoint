package com.linkpoint.protocol.messages

import android.util.Log
import java.nio.ByteBuffer

internal object AvatarMessageParsers {
    private const val TAG = "AvatarMessageParsers"

    fun parseAvatarAnimation(data: ByteArray): AvatarAnimationData? {
        val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
        return try {
            val agentId = buffer.readUuid()
            val numAnimations = buffer.get().toInt() and 0xFF
            val animations = (0 until numAnimations).map {
                val animId = buffer.readUuid()
                animId to buffer.int
            }
            val numSources = buffer.get().toInt() and 0xFF
            val sources = (0 until numSources).map { buffer.readUuid() }
            AvatarAnimationData(agentId, animations, sources)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse AvatarAnimation", e)
            null
        }
    }
}
