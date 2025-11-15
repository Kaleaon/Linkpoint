package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
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

    Int CalcPayloadSize() {
        return 53
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGrantGodlikePowers(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 2)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packByte(byteBuffer, (byte) this.GrantData_Field.GodLevel)
        packUUID(byteBuffer, this.GrantData_Field.Token)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.GrantData_Field.GodLevel = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.GrantData_Field.Token = unpackUUID(byteBuffer)
    }
}
