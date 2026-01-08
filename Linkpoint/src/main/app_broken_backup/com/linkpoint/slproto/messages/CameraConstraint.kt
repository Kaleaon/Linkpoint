package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector4
import java.nio.ByteBuffer

class CameraConstraint : SLMessage {
    CameraCollidePlane CameraCollidePlane_Field = CameraCollidePlane()

    class CameraCollidePlane {
        LLVector4 Plane
    }

    CameraConstraint() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 17
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleCameraConstraint(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.put(Ascii.SYN)
        packLLVector4(byteBuffer, this.CameraCollidePlane_Field.Plane)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.CameraCollidePlane_Field.Plane = unpackLLVector4(byteBuffer)
    }
}
