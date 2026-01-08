package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ObjectDuplicateOnRay : SLMessage {
    AgentData AgentData_Field
    ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        Boolean BypassRaycast
        Boolean CopyCenters
        Boolean CopyRotates
        Int DuplicateFlags
        UUID GroupID
        LLVector3 RayEnd
        Boolean RayEndIsIntersection
        LLVector3 RayStart
        UUID RayTargetID
        UUID SessionID
    }

    class ObjectData {
        Int ObjectLocalID
    }

    ObjectDuplicateOnRay() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        return (this.ObjectData_Fields.size() * 4) + 101
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleObjectDuplicateOnRay(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
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
        byteBuffer.put((this as Byte).ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ObjectLocalID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
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
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            ObjectData objectData = ObjectData()
            objectData.ObjectLocalID = unpackInt(byteBuffer)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
