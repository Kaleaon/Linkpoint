package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class AvatarPicksReply : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var Data_Fields: ArrayList<Data> = ArrayList()

    class AgentData {
        var AgentID: UUID? = null
        var TargetID: UUID? = null
    }

    class Data {
        var PickID: UUID? = null
        var PickName: ByteArray = ByteArray(0)
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        var length = 37
        for (data in Data_Fields) {
            length += data.PickName.size + 17
        }
        return length
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAvatarPicksReply(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put((-78).toByte())
        packUUID(byteBuffer, AgentData_Field.AgentID!!)
        packUUID(byteBuffer, AgentData_Field.TargetID!!)
        byteBuffer.put(Data_Fields.size.toByte())
        for (data in Data_Fields) {
            packUUID(byteBuffer, data.PickID!!)
            packVariable(byteBuffer, data.PickName, 1)
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(byteBuffer)
        AgentData_Field.TargetID = unpackUUID(byteBuffer)
        val b = (byteBuffer.get().toInt() and 0xFF)
        for (i in 0 until b) {
            val data = Data()
            data.PickID = unpackUUID(byteBuffer)
            data.PickName = unpackVariable(byteBuffer, 1)
            Data_Fields.add(data)
        }
    }
}
