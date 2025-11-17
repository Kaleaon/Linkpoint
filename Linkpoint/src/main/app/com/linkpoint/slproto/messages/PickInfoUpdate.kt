package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3d
import java.nio.ByteBuffer
import java.util.UUID

class PickInfoUpdate : SLMessage {
    AgentData AgentData_Field = AgentData()
    Data Data_Field = Data()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class Data {
        UUID CreatorID
        ByteArray Desc
        Boolean Enabled
        ByteArray Name
        UUID ParcelID
        UUID PickID
        LLVector3d PosGlobal
        UUID SnapshotID
        Int SortOrder
        Boolean TopPick
    }

    PickInfoUpdate() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.Data_Field.Name.length + 50 + 2 + this.Data_Field.Desc.length + 16 + 24 + 4 + 1 + 36
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandlePickInfoUpdate(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -71)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.Data_Field.PickID)
        packUUID(byteBuffer, this.Data_Field.CreatorID)
        packBoolean(byteBuffer, this.Data_Field.TopPick)
        packUUID(byteBuffer, this.Data_Field.ParcelID)
        packVariable(byteBuffer, this.Data_Field.Name, 1)
        packVariable(byteBuffer, this.Data_Field.Desc, 2)
        packUUID(byteBuffer, this.Data_Field.SnapshotID)
        packLLVector3d(byteBuffer, this.Data_Field.PosGlobal)
        packInt(byteBuffer, this.Data_Field.SortOrder)
        packBoolean(byteBuffer, this.Data_Field.Enabled)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.PickID = unpackUUID(byteBuffer)
        this.Data_Field.CreatorID = unpackUUID(byteBuffer)
        this.Data_Field.TopPick = unpackBoolean(byteBuffer)
        this.Data_Field.ParcelID = unpackUUID(byteBuffer)
        this.Data_Field.Name = unpackVariable(byteBuffer, 1)
        this.Data_Field.Desc = unpackVariable(byteBuffer, 2)
        this.Data_Field.SnapshotID = unpackUUID(byteBuffer)
        this.Data_Field.PosGlobal = unpackLLVector3d(byteBuffer)
        this.Data_Field.SortOrder = unpackInt(byteBuffer)
        this.Data_Field.Enabled = unpackBoolean(byteBuffer)
    }
}
