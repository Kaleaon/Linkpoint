package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ScriptQuestion : SLMessage() {
    val Data_Field = Data()

    class Data {
        var TaskID: UUID? = null
        var ItemID: UUID? = null
        lateinit var ObjectName: ByteArray
        lateinit var ObjectOwner: ByteArray
        var Questions: Int = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return Data_Field.ObjectName.size + 33 + 1 + Data_Field.ObjectOwner.size + 4 + 4
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleScriptQuestion(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-68).toByte())
        packUUID(buffer, Data_Field.TaskID)
        packUUID(buffer, Data_Field.ItemID)
        packVariable(buffer, Data_Field.ObjectName, 1)
        packVariable(buffer, Data_Field.ObjectOwner, 1)
        packInt(buffer, Data_Field.Questions)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        Data_Field.TaskID = unpackUUID(buffer)
        Data_Field.ItemID = unpackUUID(buffer)
        Data_Field.ObjectName = unpackVariable(buffer, 1)
        Data_Field.ObjectOwner = unpackVariable(buffer, 1)
        Data_Field.Questions = unpackInt(buffer)
    }
}