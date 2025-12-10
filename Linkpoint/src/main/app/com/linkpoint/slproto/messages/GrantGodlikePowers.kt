package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GrantGodlikePowers : SLMessage {
    AgentData AgentData_Field = AgentData()
    GrantData GrantData_Field = GrantData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class GrantData {
        Int GodLevel
        UUID Token
    }

    GrantGodlikePowers() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 53
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleGrantGodlikePowers(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 2)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packByte(byteBuffer, (this as byte).GrantData_Field.GodLevel)
        packUUID(byteBuffer, this.GrantData_Field.Token)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.GrantData_Field.GodLevel = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.GrantData_Field.Token = unpackUUID(byteBuffer)
    }
}
