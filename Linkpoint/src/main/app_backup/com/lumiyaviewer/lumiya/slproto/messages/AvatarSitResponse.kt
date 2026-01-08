package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class AvatarSitResponse : SLMessage {
    var SitObject_Field: SitObject = SitObject()
    var SitTransform_Field: SitTransform = SitTransform()

    class SitObject {
        var ID: UUID? = null
    }

    class SitTransform {
        var AutoPilot: Boolean = false
        var CameraAtOffset: LLVector3 = LLVector3()
        var CameraEyeOffset: LLVector3 = LLVector3()
        var ForceMouselook: Boolean = false
        var SitPosition: LLVector3 = LLVector3()
        var SitRotation: LLQuaternion = LLQuaternion()
    }

    constructor() {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return 67
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAvatarSitResponse(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.put(21.toByte()) // Ascii.NAK
        packUUID(byteBuffer, SitObject_Field.ID!!)
        packBoolean(byteBuffer, SitTransform_Field.AutoPilot)
        packLLVector3(byteBuffer, SitTransform_Field.SitPosition)
        packLLQuaternion(byteBuffer, SitTransform_Field.SitRotation)
        packLLVector3(byteBuffer, SitTransform_Field.CameraEyeOffset)
        packLLVector3(byteBuffer, SitTransform_Field.CameraAtOffset)
        packBoolean(byteBuffer, SitTransform_Field.ForceMouselook)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        SitObject_Field.ID = unpackUUID(byteBuffer)
        SitTransform_Field.AutoPilot = unpackBoolean(byteBuffer)
        SitTransform_Field.SitPosition = unpackLLVector3(byteBuffer)
        SitTransform_Field.SitRotation = unpackLLQuaternion(byteBuffer)
        SitTransform_Field.CameraEyeOffset = unpackLLVector3(byteBuffer)
        SitTransform_Field.CameraAtOffset = unpackLLVector3(byteBuffer)
        SitTransform_Field.ForceMouselook = unpackBoolean(byteBuffer)
    }
}
