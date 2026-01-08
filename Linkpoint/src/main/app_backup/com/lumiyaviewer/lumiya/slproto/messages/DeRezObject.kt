package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer
import java.util.ArrayList

class DeRezObject : SLMessage {
    var AgentBlock_Field: AgentBlock = AgentBlock()
    var AgentData_Field: AgentData = AgentData()
    var ObjectData_Fields: ArrayList<ObjectData> = ArrayList()

    class AgentBlock {
        var Destination: Int = 0
        var DestinationID: UUID = UUIDPool.ZeroUUID
        var GroupID: UUID = UUIDPool.ZeroUUID
        var PacketCount: Int = 0
        var PacketNumber: Int = 0
        var TransactionID: UUID = UUIDPool.ZeroUUID
    }

    class AgentData {
        var AgentID: UUID = UUIDPool.ZeroUUID
        var SessionID: UUID = UUIDPool.ZeroUUID
    }

    class ObjectData {
        var ObjectLocalID: Int = 0
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return (this.ObjectData_Fields.size * 4) + 88
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleDeRezObject(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(1.toByte())
        byteBuffer.put(35.toByte())
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentBlock_Field.GroupID)
        packByte(byteBuffer, this.AgentBlock_Field.Destination.toByte())
        packUUID(byteBuffer, this.AgentBlock_Field.DestinationID)
        packUUID(byteBuffer, this.AgentBlock_Field.TransactionID)
        packByte(byteBuffer, this.AgentBlock_Field.PacketCount.toByte())
        packByte(byteBuffer, this.AgentBlock_Field.PacketNumber.toByte())
        byteBuffer.put(this.ObjectData_Fields.size.toByte())
        for (objectData in this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ObjectLocalID)
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentBlock_Field.GroupID = unpackUUID(byteBuffer)
        this.AgentBlock_Field.Destination = unpackByte(byteBuffer).toInt() and 0xFF
        this.AgentBlock_Field.DestinationID = unpackUUID(byteBuffer)
        this.AgentBlock_Field.TransactionID = unpackUUID(byteBuffer)
        this.AgentBlock_Field.PacketCount = unpackByte(byteBuffer).toInt() and 0xFF
        this.AgentBlock_Field.PacketNumber = unpackByte(byteBuffer).toInt() and 0xFF
        val b = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until b) {
            val objectData = ObjectData()
            objectData.ObjectLocalID = unpackInt(byteBuffer)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
