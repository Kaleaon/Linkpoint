package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class DeRezObject : SLMessage {
    AgentBlock AgentBlock_Field
    AgentData AgentData_Field
    ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()

    class AgentBlock {
        Int Destination
        UUID DestinationID
        UUID GroupID
        Int PacketCount
        Int PacketNumber
        UUID TransactionID
    }

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class ObjectData {
        Int ObjectLocalID
    }

    DeRezObject() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.AgentBlock_Field = AgentBlock()
    }

    fun CalcPayloadSize(): Int {
        return (this.ObjectData_Fields.size() * 4) + 88
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleDeRezObject(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 35)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentBlock_Field.GroupID)
        packByte(byteBuffer, (this as byte).AgentBlock_Field.Destination)
        packUUID(byteBuffer, this.AgentBlock_Field.DestinationID)
        packUUID(byteBuffer, this.AgentBlock_Field.TransactionID)
        packByte(byteBuffer, (this as byte).AgentBlock_Field.PacketCount)
        packByte(byteBuffer, (this as byte).AgentBlock_Field.PacketNumber)
        byteBuffer.put((this as byte).ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ObjectLocalID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentBlock_Field.GroupID = unpackUUID(byteBuffer)
        this.AgentBlock_Field.Destination = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.AgentBlock_Field.DestinationID = unpackUUID(byteBuffer)
        this.AgentBlock_Field.TransactionID = unpackUUID(byteBuffer)
        this.AgentBlock_Field.PacketCount = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.AgentBlock_Field.PacketNumber = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            ObjectData objectData = ObjectData()
            objectData.ObjectLocalID = unpackInt(byteBuffer)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
