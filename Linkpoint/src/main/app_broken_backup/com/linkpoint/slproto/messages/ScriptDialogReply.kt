package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ScriptDialogReply : SLMessage {
    AgentData AgentData_Field = AgentData()
    Data Data_Field = Data()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class Data {
        Int ButtonIndex
        ByteArray ButtonLabel
        Int ChatChannel
        UUID ObjectID
    }

    ScriptDialogReply() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.Data_Field.ButtonLabel.size + 25 + 36
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleScriptDialogReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -65)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.Data_Field.ObjectID)
        packInt(byteBuffer, this.Data_Field.ChatChannel)
        packInt(byteBuffer, this.Data_Field.ButtonIndex)
        packVariable(byteBuffer, this.Data_Field.ButtonLabel, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.ObjectID = unpackUUID(byteBuffer)
        this.Data_Field.ChatChannel = unpackInt(byteBuffer)
        this.Data_Field.ButtonIndex = unpackInt(byteBuffer)
        this.Data_Field.ButtonLabel = unpackVariable(byteBuffer, 1)
    }
}
