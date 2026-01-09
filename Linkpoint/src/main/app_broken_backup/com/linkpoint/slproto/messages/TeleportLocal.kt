package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class TeleportLocal : SLMessage {
    Info Info_Field = Info()

    class Info {
        UUID AgentID
        Int LocationID
        LLVector3 LookAt
        LLVector3 Position
        Int TeleportFlags
    }

    TeleportLocal() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 52
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleTeleportLocal(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 64)
        packUUID(byteBuffer, this.Info_Field.AgentID)
        packInt(byteBuffer, this.Info_Field.LocationID)
        packLLVector3(byteBuffer, this.Info_Field.Position)
        packLLVector3(byteBuffer, this.Info_Field.LookAt)
        packInt(byteBuffer, this.Info_Field.TeleportFlags)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.Info_Field.AgentID = unpackUUID(byteBuffer)
        this.Info_Field.LocationID = unpackInt(byteBuffer)
        this.Info_Field.Position = unpackLLVector3(byteBuffer)
        this.Info_Field.LookAt = unpackLLVector3(byteBuffer)
        this.Info_Field.TeleportFlags = unpackInt(byteBuffer)
    }
}
