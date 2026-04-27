package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ObjectPermissions : SLMessage() {
    public AgentData AgentData_Field
    public HeaderData HeaderData_Field
    public ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class HeaderData {
        public Boolean Override
    }

    @JvmStatic
    class ObjectData {
        public Int Field
        public Int Mask
        public Int ObjectLocalID
        public Int Set
    }

    public ObjectPermissions() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.HeaderData_Field = HeaderData()
    }

    public fun CalcPayloadSize(): Int {
        return (this.ObjectData_Fields.size() * 10) + 38
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleObjectPermissions(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
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

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.HeaderData_Field.Override = unpackBoolean(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val objectData: ObjectData = ObjectData()
            objectData.ObjectLocalID = unpackInt(byteBuffer)
            objectData.Field = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.Set = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.Mask = unpackInt(byteBuffer)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
