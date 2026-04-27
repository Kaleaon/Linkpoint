package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class SetStartLocation : SLMessage() {
    public StartLocationData StartLocationData_Field = StartLocationData()

    @JvmStatic
    class StartLocationData {
        public UUID AgentID
        public Int LocationID
        public LLVector3 LocationLookAt
        public LLVector3 LocationPos
        public Long RegionHandle
        public UUID RegionID
    }

    public SetStartLocation() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return 72
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSetStartLocation(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 69)
        packUUID(byteBuffer, this.StartLocationData_Field.AgentID)
        packUUID(byteBuffer, this.StartLocationData_Field.RegionID)
        packInt(byteBuffer, this.StartLocationData_Field.LocationID)
        packLong(byteBuffer, this.StartLocationData_Field.RegionHandle)
        packLLVector3(byteBuffer, this.StartLocationData_Field.LocationPos)
        packLLVector3(byteBuffer, this.StartLocationData_Field.LocationLookAt)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.StartLocationData_Field.AgentID = unpackUUID(byteBuffer)
        this.StartLocationData_Field.RegionID = unpackUUID(byteBuffer)
        this.StartLocationData_Field.LocationID = unpackInt(byteBuffer)
        this.StartLocationData_Field.RegionHandle = unpackLong(byteBuffer)
        this.StartLocationData_Field.LocationPos = unpackLLVector3(byteBuffer)
        this.StartLocationData_Field.LocationLookAt = unpackLLVector3(byteBuffer)
    }
}
