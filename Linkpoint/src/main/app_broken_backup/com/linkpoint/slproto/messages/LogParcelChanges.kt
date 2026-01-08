package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class LogParcelChanges : SLMessage {
    AgentData AgentData_Field
    ArrayList<ParcelData> ParcelData_Fields = ArrayList<>()
    RegionData RegionData_Field

    class AgentData {
        UUID AgentID
    }

    class ParcelData {
        Int Action
        Int ActualArea
        Boolean IsOwnerGroup
        UUID OwnerID
        UUID ParcelID
        UUID TransactionID
    }

    class RegionData {
        Long RegionHandle
    }

    constructor() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.RegionData_Field = RegionData()
    }

    fun CalcPayloadSize(): Int {
        return (this.ParcelData_Fields.size() * 54) + 29
    }

    fun Handle(sLMessageHandler: SLMessageHandler): Unit {
        sLMessageHandler.HandleLogParcelChanges(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -32)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packLong(byteBuffer, this.RegionData_Field.RegionHandle)
        byteBuffer.put((this as Byte).ParcelData_Fields.size())
        for (ParcelData parcelData : this.ParcelData_Fields) {
            packUUID(byteBuffer, parcelData.ParcelID)
            packUUID(byteBuffer, parcelData.OwnerID)
            packBoolean(byteBuffer, parcelData.IsOwnerGroup)
            packInt(byteBuffer, parcelData.ActualArea)
            packByte(byteBuffer, (parcelData as Byte).Action)
            packUUID(byteBuffer, parcelData.TransactionID)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.RegionData_Field.RegionHandle = unpackLong(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
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
