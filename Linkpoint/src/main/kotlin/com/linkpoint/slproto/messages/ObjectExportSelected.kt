package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ObjectExportSelected : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID RequestID
        public Int VolumeDetail
    }

    @JvmStatic
    class ObjectData {
        public UUID ObjectID
    }

    public ObjectExportSelected() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    public fun CalcPayloadSize(): Int {
        return (this.ObjectData_Fields.size() * 16) + 39
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleObjectExportSelected(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 123)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.RequestID)
        packShort(byteBuffer, (Short) this.AgentData_Field.VolumeDetail)
        byteBuffer.put((Byte) this.ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packUUID(byteBuffer, objectData.ObjectID)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.RequestID = unpackUUID(byteBuffer)
        this.AgentData_Field.VolumeDetail = unpackShort(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val objectData: ObjectData = ObjectData()
            objectData.ObjectID = unpackUUID(byteBuffer)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
