package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MapLayerReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var flags: Int = 0
    val layerData: MutableList<LayerDataBlock> = mutableListOf()

    data class LayerDataBlock(
        var left: Int = 0,
        var right: Int = 0,
        var top: Int = 0,
        var bottom: Int = 0,
        var imageId: UUID = UUID(0L, 0L)
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packInt(buffer, flags)
        require(layerData.size <= 0xFF) { "LayerData size exceeds 255 (" + layerData.size + ")" }
        packByte(buffer, layerData.size)
        layerData.forEach { entry ->
            packInt(buffer, entry.left)
            packInt(buffer, entry.right)
            packInt(buffer, entry.top)
            packInt(buffer, entry.bottom)
            packUUID(buffer, entry.imageId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        flags = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            layerData.clear()
            repeat(count) {
                val entry = LayerDataBlock()
                entry.left = unpackInt(buffer)
                entry.right = unpackInt(buffer)
                entry.top = unpackInt(buffer)
                entry.bottom = unpackInt(buffer)
                entry.imageId = unpackUUID(buffer)
                layerData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0196.toInt()

    override fun getMessageName(): String = "MapLayerReply"
}
