package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class InitiateDownload : SLMessage() {
    val AgentData_Field = AgentData()
    val FileData_Field = FileData()

    class AgentData {
        var AgentID: UUID? = null
    }

    class FileData {
        lateinit var SimFilename: ByteArray
        lateinit var ViewerFilename: ByteArray
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return FileData_Field.SimFilename.size + 1 + 1 + FileData_Field.ViewerFilename.size + 20
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleInitiateDownload(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put((-109).toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packVariable(buffer, FileData_Field.SimFilename, 1)
        packVariable(buffer, FileData_Field.ViewerFilename, 1)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        FileData_Field.SimFilename = unpackVariable(buffer, 1)
        FileData_Field.ViewerFilename = unpackVariable(buffer, 1)
    }
}