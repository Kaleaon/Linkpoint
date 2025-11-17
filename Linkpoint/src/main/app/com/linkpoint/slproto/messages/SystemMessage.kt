package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class SystemMessage : SLMessage {
    MethodData MethodData_Field
    ArrayList<ParamList> ParamList_Fields = ArrayList<>()

    class MethodData {
        ByteArray Digest
        UUID Invoice
        ByteArray Method
    }

    class ParamList {
        ByteArray Parameter
    }

    SystemMessage() {
        this.zeroCoded = true
        this.MethodData_Field = MethodData()
    }

    Int CalcPayloadSize() {
        Int length = this.MethodData_Field.Method.length + 1 + 16 + 32 + 4 + 1
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
        sLMessageHandler.HandleSystemMessage(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -108)
        packVariable(byteBuffer, this.MethodData_Field.Method, 1)
        packUUID(byteBuffer, this.MethodData_Field.Invoice)
        packFixed(byteBuffer, this.MethodData_Field.Digest, 32)
        byteBuffer.put((Byte) this.ParamList_Fields.size())
        for (ParamList paramList : this.ParamList_Fields) {
            packVariable(byteBuffer, paramList.Parameter, 1)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.MethodData_Field.Method = unpackVariable(byteBuffer, 1)
        this.MethodData_Field.Invoice = unpackUUID(byteBuffer)
        this.MethodData_Field.Digest = unpackFixed(byteBuffer, 32)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            ParamList paramList = ParamList()
            paramList.Parameter = unpackVariable(byteBuffer, 1)
            this.ParamList_Fields.add(paramList)
        }
    }
}
