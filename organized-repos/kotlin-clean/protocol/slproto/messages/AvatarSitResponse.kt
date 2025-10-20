package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class AvatarSitResponse : SLMessage() {
    public SitObject SitObject_Field = SitObject()
    public SitTransform SitTransform_Field = SitTransform()

    @JvmStatic
    class SitObject {
        public UUID ID
    }

    @JvmStatic
    class SitTransform {
        public Boolean AutoPilot
        public LLVector3 CameraAtOffset
        public LLVector3 CameraEyeOffset
        public Boolean ForceMouselook
        public LLVector3 SitPosition
        public LLQuaternion SitRotation
    }

    public AvatarSitResponse() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return 67
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarSitResponse(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.NAK)
        packUUID(byteBuffer, this.SitObject_Field.ID)
        packBoolean(byteBuffer, this.SitTransform_Field.AutoPilot)
        packLLVector3(byteBuffer, this.SitTransform_Field.SitPosition)
        packLLQuaternion(byteBuffer, this.SitTransform_Field.SitRotation)
        packLLVector3(byteBuffer, this.SitTransform_Field.CameraEyeOffset)
        packLLVector3(byteBuffer, this.SitTransform_Field.CameraAtOffset)
        packBoolean(byteBuffer, this.SitTransform_Field.ForceMouselook)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.SitObject_Field.ID = unpackUUID(byteBuffer)
        this.SitTransform_Field.AutoPilot = unpackBoolean(byteBuffer)
        this.SitTransform_Field.SitPosition = unpackLLVector3(byteBuffer)
        this.SitTransform_Field.SitRotation = unpackLLQuaternion(byteBuffer)
        this.SitTransform_Field.CameraEyeOffset = unpackLLVector3(byteBuffer)
        this.SitTransform_Field.CameraAtOffset = unpackLLVector3(byteBuffer)
        this.SitTransform_Field.ForceMouselook = unpackBoolean(byteBuffer)
    }
}
