package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class ParcelPropertiesUpdate : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public ParcelData ParcelData_Field = ParcelData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class ParcelData {
        public UUID AuthBuyerID
        public Int Category
        public ByteArray Desc
        public Int Flags
        public UUID GroupID
        public Int LandingType
        public Int LocalID
        public Int MediaAutoScale
        public UUID MediaID
        public ByteArray MediaURL
        public ByteArray MusicURL
        public ByteArray Name
        public Int ParcelFlags
        public Float PassHours
        public Int PassPrice
        public Int SalePrice
        public UUID SnapshotID
        public LLVector3 UserLocation
        public LLVector3 UserLookAt
    }

    public ParcelPropertiesUpdate() {
        this.zeroCoded = true
    }

    public fun CalcPayloadSize(): Int {
        return this.ParcelData_Field.Name.length + 17 + 1 + this.ParcelData_Field.Desc.length + 1 + this.ParcelData_Field.MusicURL.length + 1 + this.ParcelData_Field.MediaURL.length + 16 + 1 + 16 + 4 + 4 + 1 + 16 + 16 + 12 + 12 + 1 + 36
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleParcelPropertiesUpdate(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -58)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.ParcelData_Field.LocalID)
        packInt(byteBuffer, this.ParcelData_Field.Flags)
        packInt(byteBuffer, this.ParcelData_Field.ParcelFlags)
        packInt(byteBuffer, this.ParcelData_Field.SalePrice)
        packVariable(byteBuffer, this.ParcelData_Field.Name, 1)
        packVariable(byteBuffer, this.ParcelData_Field.Desc, 1)
        packVariable(byteBuffer, this.ParcelData_Field.MusicURL, 1)
        packVariable(byteBuffer, this.ParcelData_Field.MediaURL, 1)
        packUUID(byteBuffer, this.ParcelData_Field.MediaID)
        packByte(byteBuffer, (Byte) this.ParcelData_Field.MediaAutoScale)
        packUUID(byteBuffer, this.ParcelData_Field.GroupID)
        packInt(byteBuffer, this.ParcelData_Field.PassPrice)
        packFloat(byteBuffer, this.ParcelData_Field.PassHours)
        packByte(byteBuffer, (Byte) this.ParcelData_Field.Category)
        packUUID(byteBuffer, this.ParcelData_Field.AuthBuyerID)
        packUUID(byteBuffer, this.ParcelData_Field.SnapshotID)
        packLLVector3(byteBuffer, this.ParcelData_Field.UserLocation)
        packLLVector3(byteBuffer, this.ParcelData_Field.UserLookAt)
        packByte(byteBuffer, (Byte) this.ParcelData_Field.LandingType)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ParcelData_Field.LocalID = unpackInt(byteBuffer)
        this.ParcelData_Field.Flags = unpackInt(byteBuffer)
        this.ParcelData_Field.ParcelFlags = unpackInt(byteBuffer)
        this.ParcelData_Field.SalePrice = unpackInt(byteBuffer)
        this.ParcelData_Field.Name = unpackVariable(byteBuffer, 1)
        this.ParcelData_Field.Desc = unpackVariable(byteBuffer, 1)
        this.ParcelData_Field.MusicURL = unpackVariable(byteBuffer, 1)
        this.ParcelData_Field.MediaURL = unpackVariable(byteBuffer, 1)
        this.ParcelData_Field.MediaID = unpackUUID(byteBuffer)
        this.ParcelData_Field.MediaAutoScale = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ParcelData_Field.GroupID = unpackUUID(byteBuffer)
        this.ParcelData_Field.PassPrice = unpackInt(byteBuffer)
        this.ParcelData_Field.PassHours = unpackFloat(byteBuffer)
        this.ParcelData_Field.Category = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ParcelData_Field.AuthBuyerID = unpackUUID(byteBuffer)
        this.ParcelData_Field.SnapshotID = unpackUUID(byteBuffer)
        this.ParcelData_Field.UserLocation = unpackLLVector3(byteBuffer)
        this.ParcelData_Field.UserLookAt = unpackLLVector3(byteBuffer)
        this.ParcelData_Field.LandingType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
    }
}
