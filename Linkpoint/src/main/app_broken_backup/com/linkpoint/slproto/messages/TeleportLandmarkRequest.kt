package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class TeleportLandmarkRequest : SLMessage {
    Info Info_Field = Info()

    class Info {
        UUID AgentID
        UUID LandmarkID
        UUID SessionID
    }

    TeleportLandmarkRequest() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 52
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleTeleportLandmarkRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 65)
        packUUID(byteBuffer, this.Info_Field.AgentID)
        packUUID(byteBuffer, this.Info_Field.SessionID)
        packUUID(byteBuffer, this.Info_Field.LandmarkID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.Info_Field.AgentID = unpackUUID(byteBuffer)
        this.Info_Field.SessionID = unpackUUID(byteBuffer)
        this.Info_Field.LandmarkID = unpackUUID(byteBuffer)
    }
}
