package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class InternalScriptMail : SLMessage() {
    val DataBlock_Field = DataBlock()

    class DataBlock {
        lateinit var From: ByteArray
        var To: UUID? = null
        lateinit var Subject: ByteArray
        lateinit var Body: ByteArray
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return DataBlock_Field.From.size + 1 + 16 + 1 + DataBlock_Field.Subject.size + 2 + DataBlock_Field.Body.size + 2
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleInternalScriptMail(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.put((-1).toByte())
        buffer.put(16.toByte())
        packVariable(buffer, DataBlock_Field.From, 1)
        packUUID(buffer, DataBlock_Field.To)
        packVariable(buffer, DataBlock_Field.Subject, 1)
        packVariable(buffer, DataBlock_Field.Body, 2)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        DataBlock_Field.From = unpackVariable(buffer, 1)
        DataBlock_Field.To = unpackUUID(buffer)
        DataBlock_Field.Subject = unpackVariable(buffer, 1)
        DataBlock_Field.Body = unpackVariable(buffer, 2)
    }
}