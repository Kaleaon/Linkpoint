package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class GodlikeMessage : SLMessage() {
    public AgentData AgentData_Field
    public MethodData MethodData_Field
    public ArrayList<ParamList> ParamList_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
        public UUID TransactionID
    }

    @JvmStatic
    class MethodData {
        public UUID Invoice
        public ByteArray Method
    }

    @JvmStatic
    class ParamList {
        public ByteArray Parameter
    }

    public GodlikeMessage() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.MethodData_Field = MethodData()
    }

    public fun CalcPayloadSize(): Int {
        val length: Int = this.MethodData_Field.Method.length + 1 + 16 + 52 + 1
        val it: Iterator<T> = this.ParamList_Fields.iterator()
        while (true) {
            val i: Int = length
            if (!it.hasNext()) {
                return i
            }
            length = ((ParamList) it.next()).Parameter.length + 1 + i
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleGodlikeMessage(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 3)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.TransactionID)
        packVariable(byteBuffer, this.MethodData_Field.Method, 1)
        packUUID(byteBuffer, this.MethodData_Field.Invoice)
        byteBuffer.put((Byte) this.ParamList_Fields.size())
        for (ParamList paramList : this.ParamList_Fields) {
            packVariable(byteBuffer, paramList.Parameter, 1)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.TransactionID = unpackUUID(byteBuffer)
        this.MethodData_Field.Method = unpackVariable(byteBuffer, 1)
        this.MethodData_Field.Invoice = unpackUUID(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val paramList: ParamList = ParamList()
            paramList.Parameter = unpackVariable(byteBuffer, 1)
            this.ParamList_Fields.add(paramList)
        }
    }
}
