package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ObjectPermissions : SLMessage {
    AgentData AgentData_Field
    HeaderData HeaderData_Field
    ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class HeaderData {
        Boolean Override
    }

    class ObjectData {
        Int Field
        Int Mask
        Int ObjectLocalID
        Int Set
    }

    ObjectPermissions() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.HeaderData_Field = HeaderData()
    }

    Int CalcPayloadSize() {
        return (this.ObjectData_Fields.size() * 10) + 38
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectPermissions(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 105)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packBoolean(byteBuffer, this.HeaderData_Field.Override)
        byteBuffer.put((Byte) this.ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ObjectLocalID)
            packByte(byteBuffer, (Byte) objectData.Field)
            packByte(byteBuffer, (Byte) objectData.Set)
            packInt(byteBuffer, objectData.Mask)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.HeaderData_Field.Override = unpackBoolean(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            ObjectData objectData = ObjectData()
            objectData.ObjectLocalID = unpackInt(byteBuffer)
            objectData.Field = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.Set = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.Mask = unpackInt(byteBuffer)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
