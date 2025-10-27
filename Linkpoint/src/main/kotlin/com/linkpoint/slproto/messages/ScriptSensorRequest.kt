package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class ScriptSensorRequest : SLMessage() {
    public Requester Requester_Field = Requester()

    @JvmStatic
    class Requester {
        public Float Arc
        public Float Range
        public Long RegionHandle
        public UUID RequestID
        public LLQuaternion SearchDir
        public UUID SearchID
        public ByteArray SearchName
        public LLVector3 SearchPos
        public Int SearchRegions
        public UUID SourceID
        public Int Type
    }

    public ScriptSensorRequest() {
        this.zeroCoded = true
    }

    public fun CalcPayloadSize(): Int {
        return this.Requester_Field.SearchName.length + 73 + 4 + 4 + 4 + 8 + 1 + 4
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleScriptSensorRequest(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
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
        packByte(byteBuffer, (Byte) this.Requester_Field.SearchRegions)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
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
