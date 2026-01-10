package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3d
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
        ByteArray Desc
        Boolean Enabled
        ByteArray Name
        ByteArray OriginalName
        UUID ParcelID
        UUID PickID
        LLVector3d PosGlobal
        ByteArray SimName
        UUID SnapshotID
        Int SortOrder
        Boolean TopPick
        ByteArray User
    }

    PickInfoReply() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.Data_Field.Name.size + 50 + 2 + this.Data_Field.Desc.size + 16 + 1 + this.Data_Field.User.size + 1 + this.Data_Field.OriginalName.size + 1 + this.Data_Field.SimName.size + 24 + 4 + 1 + 20
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandlePickInfoReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
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

    fun UnpackPayload(ByteBuffer byteBuffer)  {
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
