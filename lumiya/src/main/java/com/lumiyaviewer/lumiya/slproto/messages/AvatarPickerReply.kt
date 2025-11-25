package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class AvatarPickerReply : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var Data_Fields: ArrayList<Data> = ArrayList()

    class AgentData {
        var AgentID: UUID? = null
        var QueryID: UUID? = null
    }

    class Data {
        var AvatarID: UUID? = null
        var FirstName: ByteArray = ByteArray(0)
        var LastName: ByteArray = ByteArray(0)
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        var length = 37
        for (data in Data_Fields) {
            length += data.LastName.size + data.FirstName.size + 17 + 1
        }
        return length
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAvatarPickerReply(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(28.toByte()) // Ascii.FS
        packUUID(byteBuffer, AgentData_Field.AgentID!!)
        packUUID(byteBuffer, AgentData_Field.QueryID!!)
        byteBuffer.put(Data_Fields.size.toByte())
        for (data in Data_Fields) {
            packUUID(byteBuffer, data.AvatarID!!)
            packVariable(byteBuffer, data.FirstName, 1)
            packVariable(byteBuffer, data.LastName, 1)
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(byteBuffer)
        AgentData_Field.QueryID = unpackUUID(byteBuffer)
        val b = (byteBuffer.get().toInt() and 0xFF)
        for (i in 0 until b) {
            val data = Data()
            data.AvatarID = unpackUUID(byteBuffer)
            data.FirstName = unpackVariable(byteBuffer, 1)
            data.LastName = unpackVariable(byteBuffer, 1)
            Data_Fields.add(data)
        }
    }
}
