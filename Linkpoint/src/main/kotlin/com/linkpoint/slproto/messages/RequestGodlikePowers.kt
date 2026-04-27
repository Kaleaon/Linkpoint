package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RequestGodlikePowers : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public RequestBlock RequestBlock_Field = RequestBlock()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class RequestBlock {
        public Boolean Godlike
        public UUID Token
    }

    public RequestGodlikePowers() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return 53
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleRequestGodlikePowers(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 1)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packBoolean(byteBuffer, this.RequestBlock_Field.Godlike)
        packUUID(byteBuffer, this.RequestBlock_Field.Token)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.RequestBlock_Field.Godlike = unpackBoolean(byteBuffer)
        this.RequestBlock_Field.Token = unpackUUID(byteBuffer)
    }
}
