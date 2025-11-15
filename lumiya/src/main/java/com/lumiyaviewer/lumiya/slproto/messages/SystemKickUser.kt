package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
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

    Int CalcPayloadSize() {
        return (this.AgentInfo_Fields.size() * 16) + 5
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSystemKickUser(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -90)
        byteBuffer.put((Byte) this.AgentInfo_Fields.size())
        for (AgentInfo agentInfo : this.AgentInfo_Fields) {
            packUUID(byteBuffer, agentInfo.AgentID)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            AgentInfo agentInfo = AgentInfo()
            agentInfo.AgentID = unpackUUID(byteBuffer)
            this.AgentInfo_Fields.add(agentInfo)
        }
    }
}
