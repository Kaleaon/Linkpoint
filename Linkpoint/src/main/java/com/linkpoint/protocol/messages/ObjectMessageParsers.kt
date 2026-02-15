package com.linkpoint.protocol.messages

import android.util.Log
import com.linkpoint.protocol.types.LLColor4
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import java.nio.ByteBuffer
import java.nio.ByteOrder

internal object ObjectMessageParsers {
    private const val TAG = "ObjectMessageParsers"

    fun parseObjectUpdate(data: ByteArray): List<ObjectUpdateData> {
        val results = mutableListOf<ObjectUpdateData>()
        val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
        try {
            val regionHandle = buffer.long
            buffer.short
            val numBlocks = buffer.get().toInt() and 0xFF
            repeat(numBlocks) { parseObjectBlock(buffer, regionHandle)?.let(results::add) }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse ObjectUpdate", e)
        }
        return results
    }

    fun parseObjectUpdateCompressed(data: ByteArray): List<ObjectUpdateData> {
        val results = mutableListOf<ObjectUpdateData>()
        val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
        try {
            val regionHandle = buffer.long
            buffer.short
            val numBlocks = buffer.get().toInt() and 0xFF
            repeat(numBlocks) { parseCompressedBlock(buffer, regionHandle)?.let(results::add) }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse ObjectUpdateCompressed", e)
        }
        return results
    }

    fun parseTerseObjectUpdate(data: ByteArray): List<TerseUpdateData> {
        val results = mutableListOf<TerseUpdateData>()
        val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
        try {
            buffer.long
            buffer.short
            val numBlocks = buffer.get().toInt() and 0xFF
            repeat(numBlocks) {
                val dataLen = buffer.get().toInt() and 0xFF
                if (dataLen > 0) {
                    val blockData = ByteArray(dataLen)
                    buffer.get(blockData)
                    parseTerseBlock(blockData)?.let(results::add)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse TerseObjectUpdate", e)
        }
        return results
    }

    private fun parseObjectBlock(buffer: ByteBuffer, regionHandle: Long): ObjectUpdateData? {
        return try {
            val localId = buffer.int
            buffer.get()
            val fullId = buffer.readUuid()
            buffer.int
            val pcode = buffer.get().toInt() and 0xFF
            val material = buffer.get().toInt() and 0xFF
            val clickAction = buffer.get().toInt() and 0xFF

            val scaleBytes = ByteArray(12)
            buffer.get(scaleBytes)
            val scale = LLVector3.fromBytes(scaleBytes)

            val dataLen = buffer.get().toInt() and 0xFF
            val objectData = ByteArray(dataLen)
            buffer.get(objectData)

            var position = LLVector3.zero()
            var rotation = LLQuaternion.identity()
            var velocity = LLVector3.zero()
            if (dataLen >= 60) {
                position = LLVector3.fromBytes(objectData, 0)
                velocity = LLVector3.fromBytes(objectData, 12)
                rotation = LLQuaternion.fromBytes(objectData, 36)
            } else if (dataLen >= 32) {
                position = LLVector3.fromTerse(objectData, 0, 256f)
                velocity = LLVector3.fromTerse(objectData, 6, 256f)
                rotation = LLQuaternion.fromTerse(objectData, 24)
            }

            val parentId = buffer.int
            val updateFlags = buffer.int
            buffer.get(); buffer.get(); buffer.short; buffer.short
            repeat(10) { buffer.get() }
            buffer.short; buffer.short; buffer.short

            val textureEntry = ByteArray(buffer.short.toInt() and 0xFFFF).also(buffer::get)
            val textureAnim = ByteArray(buffer.get().toInt() and 0xFF).also(buffer::get)
            val nameValueBytes = ByteArray(buffer.short.toInt() and 0xFFFF).also(buffer::get)
            val extraData = ByteArray(buffer.short.toInt() and 0xFFFF).also(buffer::get)

            val text = (buffer.get().toInt() and 0xFF).let { len ->
                if (len > 0) ByteArray(len).also(buffer::get).toString(Charsets.UTF_8) else ""
            }

            val textColor = ByteArray(4).also(buffer::get).let(LLColor4::fromBytes)
            val mediaUrl = (buffer.get().toInt() and 0xFF).let { len ->
                if (len > 0) ByteArray(len).also(buffer::get).toString(Charsets.UTF_8) else ""
            }
            ByteArray(buffer.get().toInt() and 0xFF).also(buffer::get) // ps block
            val extraParams = ByteArray(buffer.get().toInt() and 0xFF).also(buffer::get)

            val soundId = buffer.readUuid()
            val ownerId = buffer.readUuid()
            val gain = buffer.float
            val soundFlags = buffer.get().toInt() and 0xFF
            val soundRadius = buffer.float
            val jointType = buffer.get().toInt() and 0xFF
            val jointPivot = ByteArray(12).also(buffer::get).let(LLVector3::fromBytes)
            val jointAxisOrAnchor = ByteArray(12).also(buffer::get).let(LLVector3::fromBytes)

            ObjectUpdateData(
                localId, fullId, parentId, position, rotation, velocity, scale, pcode, material,
                clickAction, updateFlags, textureEntry, text, textColor, mediaUrl, soundId, ownerId,
                gain, soundFlags, soundRadius, jointType, jointPivot, jointAxisOrAnchor,
                String(nameValueBytes, Charsets.UTF_8), regionHandle, extraParams
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse object block", e)
            null
        }
    }

    private fun parseCompressedBlock(buffer: ByteBuffer, regionHandle: Long): ObjectUpdateData? {
        return try {
            val updateFlags = buffer.int
            val compressedData = ByteArray(buffer.short.toInt() and 0xFFFF).also(buffer::get)
            val cb = ByteBuffer.wrap(compressedData).order(MESSAGE_BYTE_ORDER)

            val fullId = cb.readUuid()
            val localId = cb.int
            val pcode = cb.get().toInt() and 0xFF
            cb.get(); cb.int
            val material = cb.get().toInt() and 0xFF
            val clickAction = cb.get().toInt() and 0xFF
            val scale = ByteArray(12).also(cb::get).let(LLVector3::fromBytes)
            val position = ByteArray(12).also(cb::get).let(LLVector3::fromBytes)
            val rotation = ByteArray(12).also(cb::get).let(LLQuaternion::fromBytes)
            val compFlags = cb.int
            val ownerId = if ((compFlags and 0x01) != 0) cb.readUuid() else null

            ObjectUpdateData(
                localId = localId,
                fullId = fullId,
                parentId = 0,
                position = position,
                rotation = rotation,
                velocity = LLVector3.zero(),
                scale = scale,
                pcode = pcode,
                material = material,
                clickAction = clickAction,
                updateFlags = updateFlags,
                textureEntry = ByteArray(0),
                hoverText = "",
                hoverTextColor = LLColor4(0f, 0f, 0f, 0f),
                mediaUrl = "",
                ownerId = ownerId,
                nameValue = "",
                regionHandle = regionHandle
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse compressed block", e)
            null
        }
    }

    private fun parseTerseBlock(data: ByteArray): TerseUpdateData? {
        if (data.size < 30) return null
        val bb = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
        val localId = bb.int
        val state = bb.get().toInt() and 0xFF
        val isAvatar = (state and 0x01) != 0
        if (isAvatar && data.size >= 46) repeat(4) { bb.float }
        val position = LLVector3.fromTerse(data, bb.position(), 256f); bb.position(bb.position() + 6)
        val velocity = LLVector3.fromTerse(data, bb.position(), 256f); bb.position(bb.position() + 6)
        val acceleration = LLVector3.fromTerse(data, bb.position(), 256f); bb.position(bb.position() + 6)
        val rotation = LLQuaternion.fromTerse(data, bb.position()); bb.position(bb.position() + 8)
        val angularVelocity = if (bb.remaining() >= 6) LLVector3.fromTerse(data, bb.position(), 256f) else LLVector3.zero()

        return TerseUpdateData(localId, isAvatar, position, velocity, acceleration, rotation, angularVelocity)
    }

    fun parseObjectUpdateCached(data: ByteArray): ObjectUpdateCachedData? = try {
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        val regionHandle = buffer.long
        val timeDilation = buffer.short.toInt() and 0xFFFF
        val count = buffer.get().toInt() and 0xFF
        val objects = (0 until count).map { CachedObjectData(buffer.int, buffer.int, buffer.int) }
        ObjectUpdateCachedData(regionHandle, timeDilation, objects)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to parse ObjectUpdateCached", e)
        null
    }

}
