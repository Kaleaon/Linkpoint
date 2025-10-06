package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GrantGodlikePowers : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public GrantData GrantData_Field = GrantData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class GrantData {
        public Int GodLevel
        public UUID Token
    }

    public GrantGodlikePowers() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 53
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGrantGodlikePowers(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 2)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packByte(byteBuffer, (Byte) this.GrantData_Field.GodLevel)
        packUUID(byteBuffer, this.GrantData_Field.Token)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.GrantData_Field.GodLevel = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.GrantData_Field.Token = unpackUUID(byteBuffer)
    }
}
