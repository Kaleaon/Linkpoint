package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ScriptQuestion : SLMessage {
    Data Data_Field = Data()

    class Data {
        UUID ItemID
        ByteArray ObjectName
        ByteArray ObjectOwner
        Int Questions
        UUID TaskID
    }

    ScriptQuestion() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.Data_Field.ObjectName.size + 33 + 1 + this.Data_Field.ObjectOwner.size + 4 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleScriptQuestion(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -68)
        packUUID(byteBuffer, this.Data_Field.TaskID)
        packUUID(byteBuffer, this.Data_Field.ItemID)
        packVariable(byteBuffer, this.Data_Field.ObjectName, 1)
        packVariable(byteBuffer, this.Data_Field.ObjectOwner, 1)
        packInt(byteBuffer, this.Data_Field.Questions)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.Data_Field.TaskID = unpackUUID(byteBuffer)
        this.Data_Field.ItemID = unpackUUID(byteBuffer)
        this.Data_Field.ObjectName = unpackVariable(byteBuffer, 1)
        this.Data_Field.ObjectOwner = unpackVariable(byteBuffer, 1)
        this.Data_Field.Questions = unpackInt(byteBuffer)
    }
}
