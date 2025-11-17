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
        byte[] Data
        UUID ID
        byte[] Message
        byte[] System
        byte[] Token
    }

    Error() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return this.Data_Field.Token.length + 5 + 16 + 1 + this.Data_Field.System.length + 2 + this.Data_Field.Message.length + 2 + this.Data_Field.Data.length + 20
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleError(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
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

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.Data_Field.Code = unpackInt(byteBuffer)
        this.Data_Field.Token = unpackVariable(byteBuffer, 1)
        this.Data_Field.ID = unpackUUID(byteBuffer)
        this.Data_Field.System = unpackVariable(byteBuffer, 1)
        this.Data_Field.Message = unpackVariable(byteBuffer, 2)
        this.Data_Field.Data = unpackVariable(byteBuffer, 2)
    }
}
