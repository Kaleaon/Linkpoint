package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class ImprovedTerseObjectUpdateMessage : SLMessage() {
    var regionHandle: Long = 0L
    var timeDilation: Int = 0
    val objectData: MutableList<ObjectDataBlock> = mutableListOf()

    data class ObjectDataBlock(
        var data: ByteArray = ByteArray(0),
        var textureEntry: ByteArray = ByteArray(0)
    )


    override fun packPayload(buffer: ByteBuffer) {
        packLong(buffer, regionHandle)
        packUInt16(buffer, timeDilation)
        require(objectData.size <= 0xFF) { "ObjectData size exceeds 255 (" + objectData.size + ")" }
        packByte(buffer, objectData.size)
        objectData.forEach { entry ->
            packVariable(buffer, entry.data, 1)
            packVariable(buffer, entry.textureEntry, 2)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        regionHandle = unpackLong(buffer)
        timeDilation = unpackUInt16(buffer)
        run {
            val count = unpackByte(buffer)
            objectData.clear()
            repeat(count) {
                val entry = ObjectDataBlock()
                entry.data = unpackVariable(buffer, 1)
                entry.textureEntry = unpackVariable(buffer, 2)
                objectData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0x0000000F

    override fun getMessageName(): String = "ImprovedTerseObjectUpdate"
}
