package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class ObjectExtraParams : SLMessage {
    AgentData AgentData_Field
    ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class ObjectData {
        Int ObjectLocalID
        Byte[] ParamData
        Boolean ParamInUse
        Int ParamSize
        Int ParamType
    }

    ObjectExtraParams() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    Int CalcPayloadSize() {
        Int i = 37
        Iterator<T> it = this.ObjectData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((ObjectData) it.next()).ParamData.length + 12 + i2
        }
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectExtraParams(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 99)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        byteBuffer.put((Byte) this.ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ObjectLocalID)
            packShort(byteBuffer, (Short) objectData.ParamType)
            packBoolean(byteBuffer, objectData.ParamInUse)
            packInt(byteBuffer, objectData.ParamSize)
            packVariable(byteBuffer, objectData.ParamData, 1)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            ObjectData objectData = ObjectData()
            objectData.ObjectLocalID = unpackInt(byteBuffer)
            objectData.ParamType = unpackShort(byteBuffer) & 65535
            objectData.ParamInUse = unpackBoolean(byteBuffer)
            objectData.ParamSize = unpackInt(byteBuffer)
            objectData.ParamData = unpackVariable(byteBuffer, 1)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
