package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SetScriptRunning : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public Script Script_Field = Script()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class Script {
        public UUID ItemID
        public UUID ObjectID
        public Boolean Running
    }

    public SetScriptRunning() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 69
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSetScriptRunning(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -11)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.Script_Field.ObjectID)
        packUUID(byteBuffer, this.Script_Field.ItemID)
        packBoolean(byteBuffer, this.Script_Field.Running)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Script_Field.ObjectID = unpackUUID(byteBuffer)
        this.Script_Field.ItemID = unpackUUID(byteBuffer)
        this.Script_Field.Running = unpackBoolean(byteBuffer)
    }
}
