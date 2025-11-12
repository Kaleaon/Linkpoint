package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarTextureUpdateMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var texturesChanged: Boolean = false
    val wearableData: MutableList<WearableDataBlock> = mutableListOf()
    val textureData: MutableList<TextureDataBlock> = mutableListOf()

    data class WearableDataBlock(
        var cacheId: UUID = UUID(0L, 0L),
        var textureIndex: Int = 0,
        var hostName: ByteArray = ByteArray(0)
    )
    data class TextureDataBlock(
        var textureId: UUID = UUID(0L, 0L)
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packBoolean(buffer, texturesChanged)
        require(wearableData.size <= 0xFF) { "WearableData size exceeds 255 (" + wearableData.size + ")" }
        packByte(buffer, wearableData.size)
        wearableData.forEach { entry ->
            packUUID(buffer, entry.cacheId)
            packByte(buffer, entry.textureIndex)
            packVariable(buffer, entry.hostName, 1)
        }
        require(textureData.size <= 0xFF) { "TextureData size exceeds 255 (" + textureData.size + ")" }
        packByte(buffer, textureData.size)
        textureData.forEach { entry ->
            packUUID(buffer, entry.textureId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        texturesChanged = unpackBoolean(buffer)
        run {
            val count = unpackByte(buffer)
            wearableData.clear()
            repeat(count) {
                val entry = WearableDataBlock()
                entry.cacheId = unpackUUID(buffer)
                entry.textureIndex = unpackByte(buffer)
                entry.hostName = unpackVariable(buffer, 1)
                wearableData += entry
            }
        }
        run {
            val count = unpackByte(buffer)
            textureData.clear()
            repeat(count) {
                val entry = TextureDataBlock()
                entry.textureId = unpackUUID(buffer)
                textureData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0004

    override fun getMessageName(): String = "AvatarTextureUpdate"
}
