package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ObjectGrab : SLMessage {
    AgentData AgentData_Field
    ObjectData ObjectData_Field
    ArrayList<SurfaceInfo> SurfaceInfo_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class ObjectData {
        LLVector3 GrabOffset
        Int LocalID
    }

    class SurfaceInfo {
        LLVector3 Binormal
        Int FaceIndex
        LLVector3 Normal
        LLVector3 Position
        LLVector3 STCoord
        LLVector3 UVCoord
    }

    ObjectGrab() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.ObjectData_Field = ObjectData()
    }

    Int CalcPayloadSize() {
        return (this.SurfaceInfo_Fields.size() * 64) + 53
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectGrab(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 117)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.ObjectData_Field.LocalID)
        packLLVector3(byteBuffer, this.ObjectData_Field.GrabOffset)
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

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ObjectData_Field.LocalID = unpackInt(byteBuffer)
        this.ObjectData_Field.GrabOffset = unpackLLVector3(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            SurfaceInfo surfaceInfo = SurfaceInfo()
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
