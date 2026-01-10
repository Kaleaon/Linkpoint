package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SetGroupAcceptNotices : SLMessage {
    AgentData AgentData_Field = AgentData()
    Data Data_Field = Data()
    NewData NewData_Field = NewData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class Data {
        Boolean AcceptNotices
        UUID GroupID
    }

    class NewData {
        Boolean ListInProfile
    }

    SetGroupAcceptNotices() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 54
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleSetGroupAcceptNotices(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 114)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.Data_Field.GroupID)
        packBoolean(byteBuffer, this.Data_Field.AcceptNotices)
        packBoolean(byteBuffer, this.NewData_Field.ListInProfile)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.GroupID = unpackUUID(byteBuffer)
        this.Data_Field.AcceptNotices = unpackBoolean(byteBuffer)
        this.NewData_Field.ListInProfile = unpackBoolean(byteBuffer)
    }
}
