package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RequestGodlikePowers : SLMessage {
    AgentData AgentData_Field = AgentData()
    RequestBlock RequestBlock_Field = RequestBlock()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class RequestBlock {
        Boolean Godlike
        UUID Token
    }

    RequestGodlikePowers() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 53
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleRequestGodlikePowers(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 1)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packBoolean(byteBuffer, this.RequestBlock_Field.Godlike)
        packUUID(byteBuffer, this.RequestBlock_Field.Token)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.RequestBlock_Field.Godlike = unpackBoolean(byteBuffer)
        this.RequestBlock_Field.Token = unpackUUID(byteBuffer)
    }
}
