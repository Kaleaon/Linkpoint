package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
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
        Byte[] ButtonLabel
        Int ChatChannel
        UUID ObjectID
    }

    ScriptDialogReply() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return this.Data_Field.ButtonLabel.length + 25 + 36
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptDialogReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
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

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.ObjectID = unpackUUID(byteBuffer)
        this.Data_Field.ChatChannel = unpackInt(byteBuffer)
        this.Data_Field.ButtonIndex = unpackInt(byteBuffer)
        this.Data_Field.ButtonLabel = unpackVariable(byteBuffer, 1)
    }
}
