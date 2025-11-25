package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.LLVector4
import java.nio.ByteBuffer

class CameraConstraint : SLMessage {
    var CameraCollidePlane_Field: CameraCollidePlane = CameraCollidePlane()

    class CameraCollidePlane {
        var Plane: LLVector4 = LLVector4()
    }

    constructor() {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return 17
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleCameraConstraint(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.put(22.toByte()) // Ascii.SYN
        packLLVector4(byteBuffer, CameraCollidePlane_Field.Plane)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        CameraCollidePlane_Field.Plane = unpackLLVector4(byteBuffer)
    }
}
