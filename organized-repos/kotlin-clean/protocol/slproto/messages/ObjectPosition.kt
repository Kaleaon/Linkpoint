package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ObjectPosition : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class ObjectData {
        public Int ObjectLocalID
        public LLVector3 Position
    }

    public ObjectPosition() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    public Int CalcPayloadSize() {
        return (this.ObjectData_Fields.size() * 16) + 35
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectPosition(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((Byte) -1)
        byteBuffer.put((Byte) 4)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        byteBuffer.put((Byte) this.ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ObjectLocalID)
            packLLVector3(byteBuffer, objectData.Position)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            ObjectData objectData = ObjectData()
            objectData.ObjectLocalID = unpackInt(byteBuffer)
            objectData.Position = unpackLLVector3(byteBuffer)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
