package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3d
import java.nio.ByteBuffer
import java.util.UUID

class ClassifiedInfoUpdate : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public Data Data_Field = Data()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class Data {
        public Int Category
        public Int ClassifiedFlags
        public UUID ClassifiedID
        public Byte[] Desc
        public Byte[] Name
        public UUID ParcelID
        public Int ParentEstate
        public LLVector3d PosGlobal
        public Int PriceForListing
        public UUID SnapshotID
    }

    public ClassifiedInfoUpdate() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.Data_Field.Name.length + 21 + 2 + this.Data_Field.Desc.length + 16 + 4 + 16 + 24 + 1 + 4 + 36
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleClassifiedInfoUpdate(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 45)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.Data_Field.ClassifiedID)
        packInt(byteBuffer, this.Data_Field.Category)
        packVariable(byteBuffer, this.Data_Field.Name, 1)
        packVariable(byteBuffer, this.Data_Field.Desc, 2)
        packUUID(byteBuffer, this.Data_Field.ParcelID)
        packInt(byteBuffer, this.Data_Field.ParentEstate)
        packUUID(byteBuffer, this.Data_Field.SnapshotID)
        packLLVector3d(byteBuffer, this.Data_Field.PosGlobal)
        packByte(byteBuffer, (Byte) this.Data_Field.ClassifiedFlags)
        packInt(byteBuffer, this.Data_Field.PriceForListing)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.ClassifiedID = unpackUUID(byteBuffer)
        this.Data_Field.Category = unpackInt(byteBuffer)
        this.Data_Field.Name = unpackVariable(byteBuffer, 1)
        this.Data_Field.Desc = unpackVariable(byteBuffer, 2)
        this.Data_Field.ParcelID = unpackUUID(byteBuffer)
        this.Data_Field.ParentEstate = unpackInt(byteBuffer)
        this.Data_Field.SnapshotID = unpackUUID(byteBuffer)
        this.Data_Field.PosGlobal = unpackLLVector3d(byteBuffer)
        this.Data_Field.ClassifiedFlags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.Data_Field.PriceForListing = unpackInt(byteBuffer)
    }
}
