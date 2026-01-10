package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3d
import java.nio.ByteBuffer
import java.util.UUID

class SendPostcard : SLMessage {
    AgentData AgentData_Field = AgentData()

    class AgentData {
        UUID AgentID
        Boolean AllowPublish
        UUID AssetID
        ByteArray From
        Boolean MaturePublish
        ByteArray Msg
        ByteArray Name
        LLVector3d PosGlobal
        UUID SessionID
        ByteArray Subject
        ByteArray To
    }

    SendPostcard() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.AgentData_Field.To.size + 73 + 1 + this.AgentData_Field.From.size + 1 + this.AgentData_Field.Name.size + 1 + this.AgentData_Field.Subject.size + 2 + this.AgentData_Field.Msg.size + 1 + 1 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleSendPostcard(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -100)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.AssetID)
        packLLVector3d(byteBuffer, this.AgentData_Field.PosGlobal)
        packVariable(byteBuffer, this.AgentData_Field.To, 1)
        packVariable(byteBuffer, this.AgentData_Field.From, 1)
        packVariable(byteBuffer, this.AgentData_Field.Name, 1)
        packVariable(byteBuffer, this.AgentData_Field.Subject, 1)
        packVariable(byteBuffer, this.AgentData_Field.Msg, 2)
        packBoolean(byteBuffer, this.AgentData_Field.AllowPublish)
        packBoolean(byteBuffer, this.AgentData_Field.MaturePublish)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.AssetID = unpackUUID(byteBuffer)
        this.AgentData_Field.PosGlobal = unpackLLVector3d(byteBuffer)
        this.AgentData_Field.To = unpackVariable(byteBuffer, 1)
        this.AgentData_Field.From = unpackVariable(byteBuffer, 1)
        this.AgentData_Field.Name = unpackVariable(byteBuffer, 1)
        this.AgentData_Field.Subject = unpackVariable(byteBuffer, 1)
        this.AgentData_Field.Msg = unpackVariable(byteBuffer, 2)
        this.AgentData_Field.AllowPublish = unpackBoolean(byteBuffer)
        this.AgentData_Field.MaturePublish = unpackBoolean(byteBuffer)
    }
}
