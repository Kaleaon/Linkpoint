package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class DeRezObject : SLMessage() {
    public AgentBlock AgentBlock_Field
    public AgentData AgentData_Field
    public ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()

    @JvmStatic
    class AgentBlock {
        public Int Destination
        public UUID DestinationID
        public UUID GroupID
        public Int PacketCount
        public Int PacketNumber
        public UUID TransactionID
    }

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class ObjectData {
        public Int ObjectLocalID
    }

    public DeRezObject() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.AgentBlock_Field = AgentBlock()
    }

    public fun CalcPayloadSize(): Int {
        return (this.ObjectData_Fields.size() * 4) + 88
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleDeRezObject(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 35)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentBlock_Field.GroupID)
        packByte(byteBuffer, (Byte) this.AgentBlock_Field.Destination)
        packUUID(byteBuffer, this.AgentBlock_Field.DestinationID)
        packUUID(byteBuffer, this.AgentBlock_Field.TransactionID)
        packByte(byteBuffer, (Byte) this.AgentBlock_Field.PacketCount)
        packByte(byteBuffer, (Byte) this.AgentBlock_Field.PacketNumber)
        byteBuffer.put((Byte) this.ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ObjectLocalID)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentBlock_Field.GroupID = unpackUUID(byteBuffer)
        this.AgentBlock_Field.Destination = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.AgentBlock_Field.DestinationID = unpackUUID(byteBuffer)
        this.AgentBlock_Field.TransactionID = unpackUUID(byteBuffer)
        this.AgentBlock_Field.PacketCount = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.AgentBlock_Field.PacketNumber = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val objectData: ObjectData = ObjectData()
            objectData.ObjectLocalID = unpackInt(byteBuffer)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
