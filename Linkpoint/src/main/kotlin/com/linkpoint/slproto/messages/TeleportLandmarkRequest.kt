package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class TeleportLandmarkRequest : SLMessage() {
    public Info Info_Field = Info()

    @JvmStatic
    class Info {
        public UUID AgentID
        public UUID LandmarkID
        public UUID SessionID
    }

    public TeleportLandmarkRequest() {
        this.zeroCoded = true
    }

    public fun CalcPayloadSize(): Int {
        return 52
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleTeleportLandmarkRequest(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 65)
        packUUID(byteBuffer, this.Info_Field.AgentID)
        packUUID(byteBuffer, this.Info_Field.SessionID)
        packUUID(byteBuffer, this.Info_Field.LandmarkID)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.Info_Field.AgentID = unpackUUID(byteBuffer)
        this.Info_Field.SessionID = unpackUUID(byteBuffer)
        this.Info_Field.LandmarkID = unpackUUID(byteBuffer)
    }
}
