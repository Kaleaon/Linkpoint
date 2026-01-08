package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer

class CreateGroupRequest : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var GroupData_Field: GroupData = GroupData()

    class AgentData {
        var AgentID: UUID = UUIDPool.ZeroUUID
        var SessionID: UUID = UUIDPool.ZeroUUID
    }

    class GroupData {
        var Charter: ByteArray = ByteArray(0)
        var InsigniaID: UUID = UUIDPool.ZeroUUID
        var MembershipFee: Int = 0
        var Name: ByteArray = ByteArray(0)
        var OpenEnrollment: Boolean = false
        var ShowInList: Boolean = false
        var UseInsignia: Boolean = false
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return this.GroupData_Field.Name.size + 32 + 1 + 2 + this.GroupData_Field.Charter.size + 1 + 1 + 1 + 16 + 4 + 1 + 1
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleCreateGroupRequest(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(31.toByte())
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packVariable(byteBuffer, this.GroupData_Field.Name, 1)
        packVariable(byteBuffer, this.GroupData_Field.Charter, 2)
        packBoolean(byteBuffer, this.GroupData_Field.ShowInList)
        packUUID(byteBuffer, this.GroupData_Field.InsigniaID)
        packInt(byteBuffer, this.GroupData_Field.MembershipFee)
        packBoolean(byteBuffer, this.GroupData_Field.OpenEnrollment)
        packBoolean(byteBuffer, this.GroupData_Field.UseInsignia)
        byteBuffer.put(0.toByte()) // MaturePublish
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.GroupData_Field.Name = unpackVariable(byteBuffer, 1)
        this.GroupData_Field.Charter = unpackVariable(byteBuffer, 2)
        this.GroupData_Field.ShowInList = unpackBoolean(byteBuffer)
        this.GroupData_Field.InsigniaID = unpackUUID(byteBuffer)
        this.GroupData_Field.MembershipFee = unpackInt(byteBuffer)
        this.GroupData_Field.OpenEnrollment = unpackBoolean(byteBuffer)
        this.GroupData_Field.UseInsignia = unpackBoolean(byteBuffer)
        byteBuffer.get() // MaturePublish
    }
}
