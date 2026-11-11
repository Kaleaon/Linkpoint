package com.linkpoint.protocol.messages

import android.util.Log
import com.linkpoint.protocol.types.LLVector3
import java.nio.ByteBuffer

internal object ChatMessageParsers {
    private const val TAG = "ChatMessageParsers"

    fun parseChatFromSimulator(data: ByteArray): ChatData? {
        val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
        return try {
            val fromName = String(ByteArray(buffer.get().toInt() and 0xFF).also(buffer::get), Charsets.UTF_8).trimEnd('\u0000')
            val sourceId = buffer.readUuid()
            val ownerId = buffer.readUuid()
            val sourceType = buffer.get().toInt() and 0xFF
            val chatType = buffer.get().toInt() and 0xFF
            val audible = buffer.get().toInt() and 0xFF
            val position = LLVector3.fromBytes(ByteArray(12).also(buffer::get))
            val message = String(ByteArray(buffer.short.toInt() and 0xFFFF).also(buffer::get), Charsets.UTF_8).trimEnd('\u0000')
            ChatData(fromName, sourceId, ownerId, ChatSourceType.fromValue(sourceType), ChatType.fromValue(chatType), audible, position, message)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse ChatFromSimulator", e)
            null
        }
    }
}
