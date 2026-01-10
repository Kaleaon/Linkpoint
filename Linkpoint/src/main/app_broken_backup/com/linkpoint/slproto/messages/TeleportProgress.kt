package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class TeleportProgress : SLMessage {
    AgentData AgentData_Field = AgentData()
    Info Info_Field = Info()

    class AgentData {
        UUID AgentID
    }

    class Info {
        ByteArray Message
        Int TeleportFlags
    }

    TeleportProgress() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.Info_Field.Message.size + 5 + 20
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleTeleportProgress(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 66)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packInt(byteBuffer, this.Info_Field.TeleportFlags)
        packVariable(byteBuffer, this.Info_Field.Message, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.Info_Field.TeleportFlags = unpackInt(byteBuffer)
        this.Info_Field.Message = unpackVariable(byteBuffer, 1)
    }
}
