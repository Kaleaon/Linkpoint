package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RequestObjectPropertiesFamily : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public ObjectData ObjectData_Field = ObjectData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class ObjectData {
        public UUID ObjectID
        public Int RequestFlags
    }

    public RequestObjectPropertiesFamily() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return 54
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRequestObjectPropertiesFamily(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((Byte) -1)
        byteBuffer.put((Byte) 5)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.ObjectData_Field.RequestFlags)
        packUUID(byteBuffer, this.ObjectData_Field.ObjectID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ObjectData_Field.RequestFlags = unpackInt(byteBuffer)
        this.ObjectData_Field.ObjectID = unpackUUID(byteBuffer)
    }
}
