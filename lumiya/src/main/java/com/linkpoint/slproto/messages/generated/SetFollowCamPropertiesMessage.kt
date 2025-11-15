package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SetFollowCamPropertiesMessage : SLMessage() {
    var objectId: UUID = UUID(0L, 0L)
    val cameraProperty: MutableList<CameraPropertyBlock> = mutableListOf()

    data class CameraPropertyBlock(
        var type: Int = 0,
        var value: Float = 0f
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, objectId)
        require(cameraProperty.size <= 0xFF) { "CameraProperty size exceeds 255 (" + cameraProperty.size + ")" }
        packByte(buffer, cameraProperty.size)
        cameraProperty.forEach { entry ->
            packInt(buffer, entry.type)
            packFloat(buffer, entry.value)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        objectId = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            cameraProperty.clear()
            repeat(count) {
                val entry = CameraPropertyBlock()
                entry.type = unpackInt(buffer)
                entry.value = unpackFloat(buffer)
                cameraProperty += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF009F

    override fun getMessageName(): String = "SetFollowCamProperties"
}
