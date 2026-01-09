package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RemoveMuteListEntry : SLMessage {
    AgentData AgentData_Field = AgentData()
    MuteData MuteData_Field = MuteData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class MuteData {
        UUID MuteID
        ByteArray MuteName
    }

    RemoveMuteListEntry() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.MuteData_Field.MuteName.size + 17 + 36
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleRemoveMuteListEntry(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 8)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.MuteData_Field.MuteID)
        packVariable(byteBuffer, this.MuteData_Field.MuteName, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.MuteData_Field.MuteID = unpackUUID(byteBuffer)
        this.MuteData_Field.MuteName = unpackVariable(byteBuffer, 1)
    }
}
