package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class SystemKickUser : SLMessage {
    ArrayList<AgentInfo> AgentInfo_Fields = ArrayList<>()

    class AgentInfo {
        UUID AgentID
    }

    SystemKickUser() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return (this.AgentInfo_Fields.size() * 16) + 5
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleSystemKickUser(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -90)
        byteBuffer.put((this as Byte).AgentInfo_Fields.size())
        for (AgentInfo agentInfo : this.AgentInfo_Fields) {
            packUUID(byteBuffer, agentInfo.AgentID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            AgentInfo agentInfo = AgentInfo()
            agentInfo.AgentID = unpackUUID(byteBuffer)
            this.AgentInfo_Fields.add(agentInfo)
        }
    }
}
