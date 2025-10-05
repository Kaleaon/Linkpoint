package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ObjectShape : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class ObjectData {
        public Int ObjectLocalID
        public Int PathBegin
        public Int PathCurve
        public Int PathEnd
        public Int PathRadiusOffset
        public Int PathRevolutions
        public Int PathScaleX
        public Int PathScaleY
        public Int PathShearX
        public Int PathShearY
        public Int PathSkew
        public Int PathTaperX
        public Int PathTaperY
        public Int PathTwist
        public Int PathTwistBegin
        public Int ProfileBegin
        public Int ProfileCurve
        public Int ProfileEnd
        public Int ProfileHollow
    }

    public ObjectShape() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    public Int CalcPayloadSize() {
        return (this.ObjectData_Fields.size() * 27) + 37
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectShape(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 98)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        byteBuffer.put((Byte) this.ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ObjectLocalID)
            packByte(byteBuffer, (Byte) objectData.PathCurve)
            packByte(byteBuffer, (Byte) objectData.ProfileCurve)
            packShort(byteBuffer, (Short) objectData.PathBegin)
            packShort(byteBuffer, (Short) objectData.PathEnd)
            packByte(byteBuffer, (Byte) objectData.PathScaleX)
            packByte(byteBuffer, (Byte) objectData.PathScaleY)
            packByte(byteBuffer, (Byte) objectData.PathShearX)
            packByte(byteBuffer, (Byte) objectData.PathShearY)
            packByte(byteBuffer, (Byte) objectData.PathTwist)
            packByte(byteBuffer, (Byte) objectData.PathTwistBegin)
            packByte(byteBuffer, (Byte) objectData.PathRadiusOffset)
            packByte(byteBuffer, (Byte) objectData.PathTaperX)
            packByte(byteBuffer, (Byte) objectData.PathTaperY)
            packByte(byteBuffer, (Byte) objectData.PathRevolutions)
            packByte(byteBuffer, (Byte) objectData.PathSkew)
            packShort(byteBuffer, (Short) objectData.ProfileBegin)
            packShort(byteBuffer, (Short) objectData.ProfileEnd)
            packShort(byteBuffer, (Short) objectData.ProfileHollow)
        }
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            ObjectData objectData = ObjectData()
            objectData.ObjectLocalID = unpackInt(byteBuffer)
            objectData.PathCurve = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.ProfileCurve = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.PathBegin = unpackShort(byteBuffer) & 65535
            objectData.PathEnd = unpackShort(byteBuffer) & 65535
            objectData.PathScaleX = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.PathScaleY = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.PathShearX = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.PathShearY = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.PathTwist = unpackByte(byteBuffer)
            objectData.PathTwistBegin = unpackByte(byteBuffer)
            objectData.PathRadiusOffset = unpackByte(byteBuffer)
            objectData.PathTaperX = unpackByte(byteBuffer)
            objectData.PathTaperY = unpackByte(byteBuffer)
            objectData.PathRevolutions = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.PathSkew = unpackByte(byteBuffer)
            objectData.ProfileBegin = unpackShort(byteBuffer) & 65535
            objectData.ProfileEnd = unpackShort(byteBuffer) & 65535
            objectData.ProfileHollow = unpackShort(byteBuffer) & 65535
            this.ObjectData_Fields.add(objectData)
        }
    }
}
