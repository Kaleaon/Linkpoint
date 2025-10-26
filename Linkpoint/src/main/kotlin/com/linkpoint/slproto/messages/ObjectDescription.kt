package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class ObjectDescription : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class ObjectData {
        public ByteArray Description
        public Int LocalID
    }

    public ObjectDescription() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    public fun CalcPayloadSize(): Int {
        val i: Int = 37
        val it: Iterator<T> = this.ObjectData_Fields.iterator()
        while (true) {
            val i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((ObjectData) it.next()).Description.length + 5 + i2
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleObjectDescription(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 108)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        byteBuffer.put((Byte) this.ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.LocalID)
            packVariable(byteBuffer, objectData.Description, 1)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val objectData: ObjectData = ObjectData()
            objectData.LocalID = unpackInt(byteBuffer)
            objectData.Description = unpackVariable(byteBuffer, 1)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
