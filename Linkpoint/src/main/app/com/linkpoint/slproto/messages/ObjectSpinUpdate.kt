package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLQuaternion
import java.nio.ByteBuffer
import java.util.UUID

class ObjectSpinUpdate : SLMessage {
    AgentData AgentData_Field = AgentData()
    ObjectData ObjectData_Field = ObjectData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class ObjectData {
        UUID ObjectID
        LLQuaternion Rotation
    }

    ObjectSpinUpdate() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 64
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleObjectSpinUpdate(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 121)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.ObjectData_Field.ObjectID)
        packLLQuaternion(byteBuffer, this.ObjectData_Field.Rotation)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ObjectData_Field.ObjectID = unpackUUID(byteBuffer)
        this.ObjectData_Field.Rotation = unpackLLQuaternion(byteBuffer)
    }
}
