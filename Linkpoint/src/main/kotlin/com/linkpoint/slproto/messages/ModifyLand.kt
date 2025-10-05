package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ModifyLand : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<ModifyBlockExtended> ModifyBlockExtended_Fields = ArrayList<>()
    public ModifyBlock ModifyBlock_Field
    public ArrayList<ParcelData> ParcelData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class ModifyBlock {
        public Int Action
        public Int BrushSize
        public Float Height
        public Float Seconds
    }

    @JvmStatic
    class ModifyBlockExtended {
        public Float BrushSize
    }

    @JvmStatic
    class ParcelData {
        public Float East
        public Int LocalID
        public Float North
        public Float South
        public Float West
    }

    public ModifyLand() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.ModifyBlock_Field = ModifyBlock()
    }

    public Int CalcPayloadSize() {
        return (this.ParcelData_Fields.size() * 20) + 47 + 1 + (this.ModifyBlockExtended_Fields.size() * 4)
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleModifyLand(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 124)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packByte(byteBuffer, (Byte) this.ModifyBlock_Field.Action)
        packByte(byteBuffer, (Byte) this.ModifyBlock_Field.BrushSize)
        packFloat(byteBuffer, this.ModifyBlock_Field.Seconds)
        packFloat(byteBuffer, this.ModifyBlock_Field.Height)
        byteBuffer.put((Byte) this.ParcelData_Fields.size())
        for (ParcelData parcelData : this.ParcelData_Fields) {
            packInt(byteBuffer, parcelData.LocalID)
            packFloat(byteBuffer, parcelData.West)
            packFloat(byteBuffer, parcelData.South)
            packFloat(byteBuffer, parcelData.East)
            packFloat(byteBuffer, parcelData.North)
        }
        byteBuffer.put((Byte) this.ModifyBlockExtended_Fields.size())
        for (ModifyBlockExtended modifyBlockExtended : this.ModifyBlockExtended_Fields) {
            packFloat(byteBuffer, modifyBlockExtended.BrushSize)
        }
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ModifyBlock_Field.Action = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ModifyBlock_Field.BrushSize = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ModifyBlock_Field.Seconds = unpackFloat(byteBuffer)
        this.ModifyBlock_Field.Height = unpackFloat(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            ParcelData parcelData = ParcelData()
            parcelData.LocalID = unpackInt(byteBuffer)
            parcelData.West = unpackFloat(byteBuffer)
            parcelData.South = unpackFloat(byteBuffer)
            parcelData.East = unpackFloat(byteBuffer)
            parcelData.North = unpackFloat(byteBuffer)
            this.ParcelData_Fields.add(parcelData)
        }
        Byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b2; i2++) {
            ModifyBlockExtended modifyBlockExtended = ModifyBlockExtended()
            modifyBlockExtended.BrushSize = unpackFloat(byteBuffer)
            this.ModifyBlockExtended_Fields.add(modifyBlockExtended)
        }
    }
}
