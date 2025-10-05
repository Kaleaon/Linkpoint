package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class LogParcelChanges : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<ParcelData> ParcelData_Fields = ArrayList<>()
    public RegionData RegionData_Field

    @JvmStatic
    class AgentData {
        public UUID AgentID
    }

    @JvmStatic
    class ParcelData {
        public Int Action
        public Int ActualArea
        public Boolean IsOwnerGroup
        public UUID OwnerID
        public UUID ParcelID
        public UUID TransactionID
    }

    @JvmStatic
    class RegionData {
        public Long RegionHandle
    }

    public LogParcelChanges() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.RegionData_Field = RegionData()
    }

    public Int CalcPayloadSize() {
        return (this.ParcelData_Fields.size() * 54) + 29
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLogParcelChanges(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -32)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packLong(byteBuffer, this.RegionData_Field.RegionHandle)
        byteBuffer.put((Byte) this.ParcelData_Fields.size())
        for (ParcelData parcelData : this.ParcelData_Fields) {
            packUUID(byteBuffer, parcelData.ParcelID)
            packUUID(byteBuffer, parcelData.OwnerID)
            packBoolean(byteBuffer, parcelData.IsOwnerGroup)
            packInt(byteBuffer, parcelData.ActualArea)
            packByte(byteBuffer, (Byte) parcelData.Action)
            packUUID(byteBuffer, parcelData.TransactionID)
        }
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.RegionData_Field.RegionHandle = unpackLong(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            ParcelData parcelData = ParcelData()
            parcelData.ParcelID = unpackUUID(byteBuffer)
            parcelData.OwnerID = unpackUUID(byteBuffer)
            parcelData.IsOwnerGroup = unpackBoolean(byteBuffer)
            parcelData.ActualArea = unpackInt(byteBuffer)
            parcelData.Action = unpackByte(byteBuffer)
            parcelData.TransactionID = unpackUUID(byteBuffer)
            this.ParcelData_Fields.add(parcelData)
        }
    }
}
