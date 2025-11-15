package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.base.Ascii
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.types.LLVector4
import java.nio.ByteBuffer

class CameraConstraint : SLMessage {
    CameraCollidePlane CameraCollidePlane_Field = CameraCollidePlane()

    class CameraCollidePlane {
        LLVector4 Plane
    }

    CameraConstraint() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return 17
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCameraConstraint(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.SYN)
        packLLVector4(byteBuffer, this.CameraCollidePlane_Field.Plane)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.CameraCollidePlane_Field.Plane = unpackLLVector4(byteBuffer)
    }
}
