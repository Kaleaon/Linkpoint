package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector4
import java.nio.ByteBuffer

class CameraConstraint : SLMessage() {
    val CameraCollidePlane_Field = CameraCollidePlane()

    class CameraCollidePlane {
        var Plane: LLVector4? = null
    }

    init {
        zeroCoded = true
    }

    override fun CalcPayloadSize(): Int = 17

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleCameraConstraint(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.put(Ascii.SYN)
        packLLVector4(buffer, CameraCollidePlane_Field.Plane)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        CameraCollidePlane_Field.Plane = unpackLLVector4(buffer)
    }
}