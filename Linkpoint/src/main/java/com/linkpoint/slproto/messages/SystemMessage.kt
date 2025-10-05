package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SystemMessage : SLMessage() {
    val MethodData_Field = MethodData()
    val ParamList_Fields = ArrayList<ParamList>()

    class MethodData {
        lateinit var Method: ByteArray
        var Invoice: UUID? = null
        lateinit var Digest: ByteArray
    }

    class ParamList {
        lateinit var Parameter: ByteArray
    }

    init {
        zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        var length = MethodData_Field.Method.size + 1 + 16 + 32 + 4 + 1
        for (param in ParamList_Fields) {
            length += param.Parameter.size + 1
        }
        return length
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleSystemMessage(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put((-108).toByte())
        packVariable(buffer, MethodData_Field.Method, 1)
        packUUID(buffer, MethodData_Field.Invoice)
        packFixed(buffer, MethodData_Field.Digest, 32)
        buffer.put(ParamList_Fields.size.toByte())
        for (paramList in ParamList_Fields) {
            packVariable(buffer, paramList.Parameter, 1)
        }
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        MethodData_Field.Method = unpackVariable(buffer, 1)
        MethodData_Field.Invoice = unpackUUID(buffer)
        MethodData_Field.Digest = unpackFixed(buffer, 32)
        val count = buffer.get().toInt() and UnsignedBytes.MAX_VALUE.toInt()
        for (i in 0 until count) {
            val paramList = ParamList()
            paramList.Parameter = unpackVariable(buffer, 1)
            ParamList_Fields.add(paramList)
        }
    }
}