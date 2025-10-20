package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class RequestMultipleObjects : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class ObjectData {
        public Int CacheMissType
        public Int ID
    }

    public RequestMultipleObjects() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    public Int CalcPayloadSize() {
        return (this.ObjectData_Fields.size() * 5) + 35
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRequestMultipleObjects(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((Byte) -1)
        byteBuffer.put((Byte) 3)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        byteBuffer.put((Byte) this.ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packByte(byteBuffer, (Byte) objectData.CacheMissType)
            packInt(byteBuffer, objectData.ID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            ObjectData objectData = ObjectData()
            objectData.CacheMissType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.ID = unpackInt(byteBuffer)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
