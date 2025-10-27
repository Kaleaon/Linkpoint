package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ScriptDialogReply : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public Data Data_Field = Data()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class Data {
        public Int ButtonIndex
        public ByteArray ButtonLabel
        public Int ChatChannel
        public UUID ObjectID
    }

    public ScriptDialogReply() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.Data_Field.ButtonLabel.length + 25 + 36
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptDialogReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
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

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.ObjectID = unpackUUID(byteBuffer)
        this.Data_Field.ChatChannel = unpackInt(byteBuffer)
        this.Data_Field.ButtonIndex = unpackInt(byteBuffer)
        this.Data_Field.ButtonLabel = unpackVariable(byteBuffer, 1)
    }
}
