package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ObjectOwner : SLMessage() {
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
        public UUID GroupID
        public Boolean Override
        public UUID OwnerID
    }

    @JvmStatic
    class ObjectData {
        public Int ObjectLocalID
    }

    public ObjectOwner() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.HeaderData_Field = HeaderData()
    }

    public Int CalcPayloadSize() {
        return (this.ObjectData_Fields.size() * 4) + 70
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectOwner(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 100)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packBoolean(byteBuffer, this.HeaderData_Field.Override)
        packUUID(byteBuffer, this.HeaderData_Field.OwnerID)
        packUUID(byteBuffer, this.HeaderData_Field.GroupID)
        byteBuffer.put((Byte) this.ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ObjectLocalID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.HeaderData_Field.Override = unpackBoolean(byteBuffer)
        this.HeaderData_Field.OwnerID = unpackUUID(byteBuffer)
        this.HeaderData_Field.GroupID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            ObjectData objectData = ObjectData()
            objectData.ObjectLocalID = unpackInt(byteBuffer)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
