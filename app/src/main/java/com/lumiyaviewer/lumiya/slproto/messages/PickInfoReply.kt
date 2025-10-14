package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d
import java.nio.ByteBuffer
import java.util.UUID

class PickInfoReply : SLMessage {
    AgentData AgentData_Field = AgentData()
    Data Data_Field = Data()

    class AgentData {
        UUID AgentID
    }

    class Data {
        UUID CreatorID
        Byte[] Desc
        Boolean Enabled
        Byte[] Name
        Byte[] OriginalName
        UUID ParcelID
        UUID PickID
        LLVector3d PosGlobal
        Byte[] SimName
        UUID SnapshotID
        Int SortOrder
        Boolean TopPick
        Byte[] User
    }

    PickInfoReply() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.Data_Field.Name.length + 50 + 2 + this.Data_Field.Desc.length + 16 + 1 + this.Data_Field.User.length + 1 + this.Data_Field.OriginalName.length + 1 + this.Data_Field.SimName.length + 24 + 4 + 1 + 20
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandlePickInfoReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -72)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.Data_Field.PickID)
        packUUID(byteBuffer, this.Data_Field.CreatorID)
        packBoolean(byteBuffer, this.Data_Field.TopPick)
        packUUID(byteBuffer, this.Data_Field.ParcelID)
        packVariable(byteBuffer, this.Data_Field.Name, 1)
        packVariable(byteBuffer, this.Data_Field.Desc, 2)
        packUUID(byteBuffer, this.Data_Field.SnapshotID)
        packVariable(byteBuffer, this.Data_Field.User, 1)
        packVariable(byteBuffer, this.Data_Field.OriginalName, 1)
        packVariable(byteBuffer, this.Data_Field.SimName, 1)
        packLLVector3d(byteBuffer, this.Data_Field.PosGlobal)
        packInt(byteBuffer, this.Data_Field.SortOrder)
        packBoolean(byteBuffer, this.Data_Field.Enabled)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.Data_Field.PickID = unpackUUID(byteBuffer)
        this.Data_Field.CreatorID = unpackUUID(byteBuffer)
        this.Data_Field.TopPick = unpackBoolean(byteBuffer)
        this.Data_Field.ParcelID = unpackUUID(byteBuffer)
        this.Data_Field.Name = unpackVariable(byteBuffer, 1)
        this.Data_Field.Desc = unpackVariable(byteBuffer, 2)
        this.Data_Field.SnapshotID = unpackUUID(byteBuffer)
        this.Data_Field.User = unpackVariable(byteBuffer, 1)
        this.Data_Field.OriginalName = unpackVariable(byteBuffer, 1)
        this.Data_Field.SimName = unpackVariable(byteBuffer, 1)
        this.Data_Field.PosGlobal = unpackLLVector3d(byteBuffer)
        this.Data_Field.SortOrder = unpackInt(byteBuffer)
        this.Data_Field.Enabled = unpackBoolean(byteBuffer)
    }
}
