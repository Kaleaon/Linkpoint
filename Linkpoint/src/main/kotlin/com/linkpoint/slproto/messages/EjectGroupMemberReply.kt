package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class EjectGroupMemberReply : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public EjectData EjectData_Field = EjectData()
    public GroupData GroupData_Field = GroupData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
    }

    @JvmStatic
    class EjectData {
        public Boolean Success
    }

    @JvmStatic
    class GroupData {
        public UUID GroupID
    }

    public EjectGroupMemberReply() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 37
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEjectGroupMemberReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 90)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.GroupData_Field.GroupID)
        packBoolean(byteBuffer, this.EjectData_Field.Success)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer)
        this.EjectData_Field.Success = unpackBoolean(byteBuffer)
    }
}
