package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class Error : SLMessage {
    AgentData AgentData_Field = AgentData()
    Data Data_Field = Data()

    class AgentData {
        UUID AgentID
    }

    class Data {
        Int Code
        ByteArray Data
        UUID ID
        ByteArray Message
        ByteArray System
        ByteArray Token
    }

    Error() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.Data_Field.Token.size + 5 + 16 + 1 + this.Data_Field.System.size + 2 + this.Data_Field.Message.size + 2 + this.Data_Field.Data.size + 20
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleError(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) -89)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packInt(byteBuffer, this.Data_Field.Code)
        packVariable(byteBuffer, this.Data_Field.Token, 1)
        packUUID(byteBuffer, this.Data_Field.ID)
        packVariable(byteBuffer, this.Data_Field.System, 1)
        packVariable(byteBuffer, this.Data_Field.Message, 2)
        packVariable(byteBuffer, this.Data_Field.Data, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.Data_Field.Code = unpackInt(byteBuffer)
        this.Data_Field.Token = unpackVariable(byteBuffer, 1)
        this.Data_Field.ID = unpackUUID(byteBuffer)
        this.Data_Field.System = unpackVariable(byteBuffer, 1)
        this.Data_Field.Message = unpackVariable(byteBuffer, 2)
        this.Data_Field.Data = unpackVariable(byteBuffer, 2)
    }
}
