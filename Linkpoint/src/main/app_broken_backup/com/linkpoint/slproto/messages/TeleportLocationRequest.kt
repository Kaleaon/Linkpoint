package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class TeleportLocationRequest : SLMessage {
    AgentData AgentData_Field = AgentData()
    Info Info_Field = Info()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class Info {
        LLVector3 LookAt
        LLVector3 Position
        Long RegionHandle
    }

    TeleportLocationRequest() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 68
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleTeleportLocationRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 63)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packLong(byteBuffer, this.Info_Field.RegionHandle)
        packLLVector3(byteBuffer, this.Info_Field.Position)
        packLLVector3(byteBuffer, this.Info_Field.LookAt)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Info_Field.RegionHandle = unpackLong(byteBuffer)
        this.Info_Field.Position = unpackLLVector3(byteBuffer)
        this.Info_Field.LookAt = unpackLLVector3(byteBuffer)
    }
}
