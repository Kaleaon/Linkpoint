package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class ScriptSensorRequest : SLMessage {
    Requester Requester_Field = Requester()

    class Requester {
        Float Arc
        Float Range
        Long RegionHandle
        UUID RequestID
        LLQuaternion SearchDir
        UUID SearchID
        ByteArray SearchName
        LLVector3 SearchPos
        Int SearchRegions
        UUID SourceID
        Int Type
    }

    ScriptSensorRequest() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.Requester_Field.SearchName.size + 73 + 4 + 4 + 4 + 8 + 1 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleScriptSensorRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -9)
        packUUID(byteBuffer, this.Requester_Field.SourceID)
        packUUID(byteBuffer, this.Requester_Field.RequestID)
        packUUID(byteBuffer, this.Requester_Field.SearchID)
        packLLVector3(byteBuffer, this.Requester_Field.SearchPos)
        packLLQuaternion(byteBuffer, this.Requester_Field.SearchDir)
        packVariable(byteBuffer, this.Requester_Field.SearchName, 1)
        packInt(byteBuffer, this.Requester_Field.Type)
        packFloat(byteBuffer, this.Requester_Field.Range)
        packFloat(byteBuffer, this.Requester_Field.Arc)
        packLong(byteBuffer, this.Requester_Field.RegionHandle)
        packByte(byteBuffer, (this as Byte).Requester_Field.SearchRegions)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.Requester_Field.SourceID = unpackUUID(byteBuffer)
        this.Requester_Field.RequestID = unpackUUID(byteBuffer)
        this.Requester_Field.SearchID = unpackUUID(byteBuffer)
        this.Requester_Field.SearchPos = unpackLLVector3(byteBuffer)
        this.Requester_Field.SearchDir = unpackLLQuaternion(byteBuffer)
        this.Requester_Field.SearchName = unpackVariable(byteBuffer, 1)
        this.Requester_Field.Type = unpackInt(byteBuffer)
        this.Requester_Field.Range = unpackFloat(byteBuffer)
        this.Requester_Field.Arc = unpackFloat(byteBuffer)
        this.Requester_Field.RegionHandle = unpackLong(byteBuffer)
        this.Requester_Field.SearchRegions = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
    }
}
