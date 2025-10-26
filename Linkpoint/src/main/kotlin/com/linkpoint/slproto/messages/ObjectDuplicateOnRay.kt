package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ObjectDuplicateOnRay : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public Boolean BypassRaycast
        public Boolean CopyCenters
        public Boolean CopyRotates
        public Int DuplicateFlags
        public UUID GroupID
        public LLVector3 RayEnd
        public Boolean RayEndIsIntersection
        public LLVector3 RayStart
        public UUID RayTargetID
        public UUID SessionID
    }

    @JvmStatic
    class ObjectData {
        public Int ObjectLocalID
    }

    public ObjectDuplicateOnRay() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    public fun CalcPayloadSize(): Int {
        return (this.ObjectData_Fields.size() * 4) + 101
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleObjectDuplicateOnRay(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 91)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        packLLVector3(byteBuffer, this.AgentData_Field.RayStart)
        packLLVector3(byteBuffer, this.AgentData_Field.RayEnd)
        packBoolean(byteBuffer, this.AgentData_Field.BypassRaycast)
        packBoolean(byteBuffer, this.AgentData_Field.RayEndIsIntersection)
        packBoolean(byteBuffer, this.AgentData_Field.CopyCenters)
        packBoolean(byteBuffer, this.AgentData_Field.CopyRotates)
        packUUID(byteBuffer, this.AgentData_Field.RayTargetID)
        packInt(byteBuffer, this.AgentData_Field.DuplicateFlags)
        byteBuffer.put((Byte) this.ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ObjectLocalID)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        this.AgentData_Field.RayStart = unpackLLVector3(byteBuffer)
        this.AgentData_Field.RayEnd = unpackLLVector3(byteBuffer)
        this.AgentData_Field.BypassRaycast = unpackBoolean(byteBuffer)
        this.AgentData_Field.RayEndIsIntersection = unpackBoolean(byteBuffer)
        this.AgentData_Field.CopyCenters = unpackBoolean(byteBuffer)
        this.AgentData_Field.CopyRotates = unpackBoolean(byteBuffer)
        this.AgentData_Field.RayTargetID = unpackUUID(byteBuffer)
        this.AgentData_Field.DuplicateFlags = unpackInt(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val objectData: ObjectData = ObjectData()
            objectData.ObjectLocalID = unpackInt(byteBuffer)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
