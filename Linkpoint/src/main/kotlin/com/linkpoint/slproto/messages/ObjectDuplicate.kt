package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ObjectDuplicate : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()
    public SharedData SharedData_Field

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID GroupID
        public UUID SessionID
    }

    @JvmStatic
    class ObjectData {
        public Int ObjectLocalID
    }

    @JvmStatic
    class SharedData {
        public Int DuplicateFlags
        public LLVector3 Offset
    }

    public ObjectDuplicate() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.SharedData_Field = SharedData()
    }

    public Int CalcPayloadSize() {
        return (this.ObjectData_Fields.size() * 4) + 69
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectDuplicate(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 90)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        packLLVector3(byteBuffer, this.SharedData_Field.Offset)
        packInt(byteBuffer, this.SharedData_Field.DuplicateFlags)
        byteBuffer.put((Byte) this.ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ObjectLocalID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        this.SharedData_Field.Offset = unpackLLVector3(byteBuffer)
        this.SharedData_Field.DuplicateFlags = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            ObjectData objectData = ObjectData()
            objectData.ObjectLocalID = unpackInt(byteBuffer)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
