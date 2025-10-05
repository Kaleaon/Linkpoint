package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class ObjectGrab : SLMessage() {
    val AgentData_Field = AgentData()
    val ObjectData_Field = ObjectData()
    val SurfaceInfo_Fields = ArrayList<SurfaceInfo>()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class ObjectData {
        var LocalID: Int = 0
        var GrabOffset: LLVector3? = null
    }

    class SurfaceInfo {
        var UVCoord: LLVector3? = null
        var STCoord: LLVector3? = null
        var FaceIndex: Int = 0
        var Position: LLVector3? = null
        var Normal: LLVector3? = null
        var Binormal: LLVector3? = null
    }

    init {
        zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return (SurfaceInfo_Fields.size * 64) + 53
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleObjectGrab(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(117.toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
        packInt(buffer, ObjectData_Field.LocalID)
        packLLVector3(buffer, ObjectData_Field.GrabOffset)
        buffer.put(SurfaceInfo_Fields.size.toByte())
        for (surfaceInfo in SurfaceInfo_Fields) {
            packLLVector3(buffer, surfaceInfo.UVCoord)
            packLLVector3(buffer, surfaceInfo.STCoord)
            packInt(buffer, surfaceInfo.FaceIndex)
            packLLVector3(buffer, surfaceInfo.Position)
            packLLVector3(buffer, surfaceInfo.Normal)
            packLLVector3(buffer, surfaceInfo.Binormal)
        }
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        ObjectData_Field.LocalID = unpackInt(buffer)
        ObjectData_Field.GrabOffset = unpackLLVector3(buffer)
        val count = buffer.get().toInt() and UnsignedBytes.MAX_VALUE.toInt()
        for (i in 0 until count) {
            val surfaceInfo = SurfaceInfo()
            surfaceInfo.UVCoord = unpackLLVector3(buffer)
            surfaceInfo.STCoord = unpackLLVector3(buffer)
            surfaceInfo.FaceIndex = unpackInt(buffer)
            surfaceInfo.Position = unpackLLVector3(buffer)
            surfaceInfo.Normal = unpackLLVector3(buffer)
            surfaceInfo.Binormal = unpackLLVector3(buffer)
            SurfaceInfo_Fields.add(surfaceInfo)
        }
    }
}