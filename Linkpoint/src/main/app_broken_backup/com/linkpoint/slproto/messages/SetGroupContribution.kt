package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SetGroupContribution : SLMessage {
    AgentData AgentData_Field = AgentData()
    Data Data_Field = Data()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class Data {
        Int Contribution
        UUID GroupID
    }

    SetGroupContribution() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 56
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleSetGroupContribution(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 113)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.Data_Field.GroupID)
        packInt(byteBuffer, this.Data_Field.Contribution)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.GroupID = unpackUUID(byteBuffer)
        this.Data_Field.Contribution = unpackInt(byteBuffer)
    }
}
