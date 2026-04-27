package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class Error : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public Data Data_Field = Data()

    @JvmStatic
    class AgentData {
        public UUID AgentID
    }

    @JvmStatic
    class Data {
        public Int Code
        public ByteArray Data
        public UUID ID
        public ByteArray Message
        public ByteArray System
        public ByteArray Token
    }

    public Error() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.Data_Field.Token.length + 5 + 16 + 1 + this.Data_Field.System.length + 2 + this.Data_Field.Message.length + 2 + this.Data_Field.Data.length + 20
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleError(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -89)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packInt(byteBuffer, this.Data_Field.Code)
        packVariable(byteBuffer, this.Data_Field.Token, 1)
        packUUID(byteBuffer, this.Data_Field.ID)
        packVariable(byteBuffer, this.Data_Field.System, 1)
        packVariable(byteBuffer, this.Data_Field.Message, 2)
        packVariable(byteBuffer, this.Data_Field.Data, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.Data_Field.Code = unpackInt(byteBuffer)
        this.Data_Field.Token = unpackVariable(byteBuffer, 1)
        this.Data_Field.ID = unpackUUID(byteBuffer)
        this.Data_Field.System = unpackVariable(byteBuffer, 1)
        this.Data_Field.Message = unpackVariable(byteBuffer, 2)
        this.Data_Field.Data = unpackVariable(byteBuffer, 2)
    }
}
