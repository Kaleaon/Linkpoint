package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ActivateGestures : SLMessage {
    val AgentData_Field = AgentData()
    val Data_Fields = ArrayList<Data>()

    class AgentData {
        var AgentID: UUID? = null
        var Flags: Int = 0
        var SessionID: UUID? = null
    }

    class Data {
        var AssetID: UUID? = null
        var GestureFlags: Int = 0
        var ItemID: UUID? = null
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return (this.Data_Fields.size * 36) + 41
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleActivateGestures(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put(60.toByte())
        packUUID(buffer, this.AgentData_Field.AgentID)
        packUUID(buffer, this.AgentData_Field.SessionID)
        packInt(buffer, this.AgentData_Field.Flags)
        buffer.put(this.Data_Fields.size.toByte())
        for (data in this.Data_Fields) {
            packUUID(buffer, data.ItemID)
            packUUID(buffer, data.AssetID)
            packInt(buffer, data.GestureFlags)
        }
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(buffer)
        this.AgentData_Field.SessionID = unpackUUID(buffer)
        this.AgentData_Field.Flags = unpackInt(buffer)
        val count = buffer.get().toInt() and 0xFF
        for (i in 0 until count) {
            val data = Data()
            data.ItemID = unpackUUID(buffer)
            data.AssetID = unpackUUID(buffer)
            data.GestureFlags = unpackInt(buffer)
            this.Data_Fields.add(data)
        }
    }
}
