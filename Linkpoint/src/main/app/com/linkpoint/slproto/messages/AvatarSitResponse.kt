package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class AvatarSitResponse : SLMessage {
    SitObject SitObject_Field = SitObject()
    SitTransform SitTransform_Field = SitTransform()

    class SitObject {
        UUID ID
    }

    class SitTransform {
        Boolean AutoPilot
        LLVector3 CameraAtOffset
        LLVector3 CameraEyeOffset
        Boolean ForceMouselook
        LLVector3 SitPosition
        LLQuaternion SitRotation
    }

    AvatarSitResponse() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return 67
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarSitResponse(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.NAK)
        packUUID(byteBuffer, this.SitObject_Field.ID)
        packBoolean(byteBuffer, this.SitTransform_Field.AutoPilot)
        packLLVector3(byteBuffer, this.SitTransform_Field.SitPosition)
        packLLQuaternion(byteBuffer, this.SitTransform_Field.SitRotation)
        packLLVector3(byteBuffer, this.SitTransform_Field.CameraEyeOffset)
        packLLVector3(byteBuffer, this.SitTransform_Field.CameraAtOffset)
        packBoolean(byteBuffer, this.SitTransform_Field.ForceMouselook)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.SitObject_Field.ID = unpackUUID(byteBuffer)
        this.SitTransform_Field.AutoPilot = unpackBoolean(byteBuffer)
        this.SitTransform_Field.SitPosition = unpackLLVector3(byteBuffer)
        this.SitTransform_Field.SitRotation = unpackLLQuaternion(byteBuffer)
        this.SitTransform_Field.CameraEyeOffset = unpackLLVector3(byteBuffer)
        this.SitTransform_Field.CameraAtOffset = unpackLLVector3(byteBuffer)
        this.SitTransform_Field.ForceMouselook = unpackBoolean(byteBuffer)
    }
}
