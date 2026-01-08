package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class AvatarPickerRequestBackend : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var Data_Field: Data = Data()

    class AgentData {
        var AgentID: UUID? = null
        var GodLevel: Int = 0
        var QueryID: UUID? = null
        var SessionID: UUID? = null
    }

    class Data {
        var Name: ByteArray = ByteArray(0)
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return Data_Field.Name.size + 1 + 53
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAvatarPickerRequestBackend(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(27.toByte()) // Ascii.ESC
        packUUID(byteBuffer, AgentData_Field.AgentID!!)
        packUUID(byteBuffer, AgentData_Field.SessionID!!)
        packUUID(byteBuffer, AgentData_Field.QueryID!!)
        packByte(byteBuffer, AgentData_Field.GodLevel.toByte())
        packVariable(byteBuffer, Data_Field.Name, 1)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(byteBuffer)
        AgentData_Field.SessionID = unpackUUID(byteBuffer)
        AgentData_Field.QueryID = unpackUUID(byteBuffer)
        AgentData_Field.GodLevel = (unpackByte(byteBuffer).toInt() and 0xFF)
        Data_Field.Name = unpackVariable(byteBuffer, 1)
    }
}
