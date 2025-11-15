package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class EstateOwnerMessage : SLMessage {
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
        byte[] Method
    }

    class ParamList {
        byte[] Parameter
    }

    EstateOwnerMessage() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.MethodData_Field = MethodData()
    }

    Int CalcPayloadSize() {
        Int length = this.MethodData_Field.Method.length + 1 + 16 + 52 + 1
        Iterator<T> it = this.ParamList_Fields.iterator()
        while (true) {
            Int i = length
            if (!it.hasNext()) {
                return i
            }
            length = ((ParamList) it.next()).Parameter.length + 1 + i
        }
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEstateOwnerMessage(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 4)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.TransactionID)
        packVariable(byteBuffer, this.MethodData_Field.Method, 1)
        packUUID(byteBuffer, this.MethodData_Field.Invoice)
        byteBuffer.put((byte) this.ParamList_Fields.size())
        for (ParamList paramList : this.ParamList_Fields) {
            packVariable(byteBuffer, paramList.Parameter, 1)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.TransactionID = unpackUUID(byteBuffer)
        this.MethodData_Field.Method = unpackVariable(byteBuffer, 1)
        this.MethodData_Field.Invoice = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            ParamList paramList = ParamList()
            paramList.Parameter = unpackVariable(byteBuffer, 1)
            this.ParamList_Fields.add(paramList)
        }
    }
}
