package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SetScriptRunning : SLMessage {
    AgentData AgentData_Field = AgentData()
    Script Script_Field = Script()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class Script {
        UUID ItemID
        UUID ObjectID
        Boolean Running
    }

    SetScriptRunning() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 69
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSetScriptRunning(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -11)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.Script_Field.ObjectID)
        packUUID(byteBuffer, this.Script_Field.ItemID)
        packBoolean(byteBuffer, this.Script_Field.Running)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Script_Field.ObjectID = unpackUUID(byteBuffer)
        this.Script_Field.ItemID = unpackUUID(byteBuffer)
        this.Script_Field.Running = unpackBoolean(byteBuffer)
    }
}
