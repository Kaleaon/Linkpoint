package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
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

    fun CalcPayloadSize(): Int {
        return 69
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleSetScriptRunning(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -11)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.Script_Field.ObjectID)
        packUUID(byteBuffer, this.Script_Field.ItemID)
        packBoolean(byteBuffer, this.Script_Field.Running)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Script_Field.ObjectID = unpackUUID(byteBuffer)
        this.Script_Field.ItemID = unpackUUID(byteBuffer)
        this.Script_Field.Running = unpackBoolean(byteBuffer)
    }
}
