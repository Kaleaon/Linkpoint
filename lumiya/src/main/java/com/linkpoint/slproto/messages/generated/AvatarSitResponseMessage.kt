package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class AvatarSitResponseMessage : SLMessage() {
    var id: UUID = UUID(0L, 0L)
    var autoPilot: Boolean = false
    var sitPosition: LLVector3 = LLVector3()
    var sitRotation: LLQuaternion = LLQuaternion()
    var cameraEyeOffset: LLVector3 = LLVector3()
    var cameraAtOffset: LLVector3 = LLVector3()
    var forceMouselook: Boolean = false


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, id)
        packBoolean(buffer, autoPilot)
        sitPosition.pack(buffer)
        sitRotation.pack(buffer)
        cameraEyeOffset.pack(buffer)
        cameraAtOffset.pack(buffer)
        packBoolean(buffer, forceMouselook)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        id = unpackUUID(buffer)
        autoPilot = unpackBoolean(buffer)
        sitPosition = LLVector3.unpack(buffer)
        sitRotation = LLQuaternion.unpack(buffer)
        cameraEyeOffset = LLVector3.unpack(buffer)
        cameraAtOffset = LLVector3.unpack(buffer)
        forceMouselook = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = 0x00000015

    override fun getMessageName(): String = "AvatarSitResponse"
}
