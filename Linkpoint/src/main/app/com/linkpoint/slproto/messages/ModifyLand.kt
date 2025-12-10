package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ModifyLand : SLMessage {
    AgentData AgentData_Field
    ArrayList<ModifyBlockExtended> ModifyBlockExtended_Fields = ArrayList<>()
    ModifyBlock ModifyBlock_Field
    ArrayList<ParcelData> ParcelData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class ModifyBlock {
        Int Action
        Int BrushSize
        Float Height
        Float Seconds
    }

    class ModifyBlockExtended {
        Float BrushSize
    }

    class ParcelData {
        Float East
        Int LocalID
        Float North
        Float South
        Float West
    }

    ModifyLand() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.ModifyBlock_Field = ModifyBlock()
    }

    fun CalcPayloadSize(): Int {
        return (this.ParcelData_Fields.size() * 20) + 47 + 1 + (this.ModifyBlockExtended_Fields.size() * 4)
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleModifyLand(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 124)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packByte(byteBuffer, (this as Byte).ModifyBlock_Field.Action)
        packByte(byteBuffer, (this as Byte).ModifyBlock_Field.BrushSize)
        packFloat(byteBuffer, this.ModifyBlock_Field.Seconds)
        packFloat(byteBuffer, this.ModifyBlock_Field.Height)
        byteBuffer.put((this as Byte).ParcelData_Fields.size())
        for (ParcelData parcelData : this.ParcelData_Fields) {
            packInt(byteBuffer, parcelData.LocalID)
            packFloat(byteBuffer, parcelData.West)
            packFloat(byteBuffer, parcelData.South)
            packFloat(byteBuffer, parcelData.East)
            packFloat(byteBuffer, parcelData.North)
        }
        byteBuffer.put((this as Byte).ModifyBlockExtended_Fields.size())
        for (ModifyBlockExtended modifyBlockExtended : this.ModifyBlockExtended_Fields) {
            packFloat(byteBuffer, modifyBlockExtended.BrushSize)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ModifyBlock_Field.Action = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ModifyBlock_Field.BrushSize = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ModifyBlock_Field.Seconds = unpackFloat(byteBuffer)
        this.ModifyBlock_Field.Height = unpackFloat(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            ParcelData parcelData = ParcelData()
            parcelData.LocalID = unpackInt(byteBuffer)
            parcelData.West = unpackFloat(byteBuffer)
            parcelData.South = unpackFloat(byteBuffer)
            parcelData.East = unpackFloat(byteBuffer)
            parcelData.North = unpackFloat(byteBuffer)
            this.ParcelData_Fields.add(parcelData)
        }
        Byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i2 in 0 until b2) {
            ModifyBlockExtended modifyBlockExtended = ModifyBlockExtended()
            modifyBlockExtended.BrushSize = unpackFloat(byteBuffer)
            this.ModifyBlockExtended_Fields.add(modifyBlockExtended)
        }
    }
}
