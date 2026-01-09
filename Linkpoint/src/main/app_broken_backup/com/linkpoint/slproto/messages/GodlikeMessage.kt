package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class GodlikeMessage : SLMessage {
    AgentData AgentData_Field
    MethodData MethodData_Field
    ArrayList<ParamList> ParamList_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
        UUID TransactionID
    }

    class MethodData {
        UUID Invoice
        ByteArray Method
    }

    class ParamList {
        ByteArray Parameter
    }

    GodlikeMessage() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.MethodData_Field = MethodData()
    }

    fun CalcPayloadSize(): Int {
        var length: Int = this.MethodData_Field.Method.size + 1 + 16 + 52 + 1
        Iterator<T> it = this.ParamList_Fields.iterator()
        while (true) {
            var i: Int = length
            if (!it.hasNext()) {
                return i
            }
            length = ((it as ParamList).next()).Parameter.size + 1 + i
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleGodlikeMessage(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 3)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.TransactionID)
        packVariable(byteBuffer, this.MethodData_Field.Method, 1)
        packUUID(byteBuffer, this.MethodData_Field.Invoice)
        byteBuffer.put((this as byte).ParamList_Fields.size())
        for (ParamList paramList : this.ParamList_Fields) {
            packVariable(byteBuffer, paramList.Parameter, 1)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.TransactionID = unpackUUID(byteBuffer)
        this.MethodData_Field.Method = unpackVariable(byteBuffer, 1)
        this.MethodData_Field.Invoice = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            ParamList paramList = ParamList()
            paramList.Parameter = unpackVariable(byteBuffer, 1)
            this.ParamList_Fields.add(paramList)
        }
    }
}
