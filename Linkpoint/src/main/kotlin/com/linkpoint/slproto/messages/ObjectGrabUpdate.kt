package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ObjectGrabUpdate : SLMessage() {
    public AgentData AgentData_Field
    public ObjectData ObjectData_Field
    public ArrayList<SurfaceInfo> SurfaceInfo_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class ObjectData {
        public LLVector3 GrabOffsetInitial
        public LLVector3 GrabPosition
        public UUID ObjectID
        public Int TimeSinceLast
    }

    @JvmStatic
    class SurfaceInfo {
        public LLVector3 Binormal
        public Int FaceIndex
        public LLVector3 Normal
        public LLVector3 Position
        public LLVector3 STCoord
        public LLVector3 UVCoord
    }

    public ObjectGrabUpdate() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.ObjectData_Field = ObjectData()
    }

    public fun CalcPayloadSize(): Int {
        return (this.SurfaceInfo_Fields.size() * 64) + 81
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleObjectGrabUpdate(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 118)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.ObjectData_Field.ObjectID)
        packLLVector3(byteBuffer, this.ObjectData_Field.GrabOffsetInitial)
        packLLVector3(byteBuffer, this.ObjectData_Field.GrabPosition)
        packInt(byteBuffer, this.ObjectData_Field.TimeSinceLast)
        byteBuffer.put((Byte) this.SurfaceInfo_Fields.size())
        for (SurfaceInfo surfaceInfo : this.SurfaceInfo_Fields) {
            packLLVector3(byteBuffer, surfaceInfo.UVCoord)
            packLLVector3(byteBuffer, surfaceInfo.STCoord)
            packInt(byteBuffer, surfaceInfo.FaceIndex)
            packLLVector3(byteBuffer, surfaceInfo.Position)
            packLLVector3(byteBuffer, surfaceInfo.Normal)
            packLLVector3(byteBuffer, surfaceInfo.Binormal)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ObjectData_Field.ObjectID = unpackUUID(byteBuffer)
        this.ObjectData_Field.GrabOffsetInitial = unpackLLVector3(byteBuffer)
        this.ObjectData_Field.GrabPosition = unpackLLVector3(byteBuffer)
        this.ObjectData_Field.TimeSinceLast = unpackInt(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val surfaceInfo: SurfaceInfo = SurfaceInfo()
            surfaceInfo.UVCoord = unpackLLVector3(byteBuffer)
            surfaceInfo.STCoord = unpackLLVector3(byteBuffer)
            surfaceInfo.FaceIndex = unpackInt(byteBuffer)
            surfaceInfo.Position = unpackLLVector3(byteBuffer)
            surfaceInfo.Normal = unpackLLVector3(byteBuffer)
            surfaceInfo.Binormal = unpackLLVector3(byteBuffer)
            this.SurfaceInfo_Fields.add(surfaceInfo)
        }
    }
}
