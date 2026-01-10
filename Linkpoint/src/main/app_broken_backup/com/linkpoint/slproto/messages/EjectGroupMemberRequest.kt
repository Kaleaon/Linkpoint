package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class EjectGroupMemberRequest : SLMessage {
    AgentData AgentData_Field
    ArrayList<EjectData> EjectData_Fields = ArrayList<>()
    GroupData GroupData_Field

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class EjectData {
        UUID EjecteeID
    }

    class GroupData {
        UUID GroupID
    }

    EjectGroupMemberRequest() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
        this.GroupData_Field = GroupData()
    }

    fun CalcPayloadSize(): Int {
        return (this.EjectData_Fields.size() * 16) + 53
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleEjectGroupMemberRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 89)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.GroupData_Field.GroupID)
        byteBuffer.put((this as byte).EjectData_Fields.size())
        for (EjectData ejectData : this.EjectData_Fields) {
            packUUID(byteBuffer, ejectData.EjecteeID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            EjectData ejectData = EjectData()
            ejectData.EjecteeID = unpackUUID(byteBuffer)
            this.EjectData_Fields.add(ejectData)
        }
    }
}
