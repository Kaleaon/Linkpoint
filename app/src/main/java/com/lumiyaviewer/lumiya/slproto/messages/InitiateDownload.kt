package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class InitiateDownload : SLMessage {
    var AgentData_Field = AgentData()
    var FileData_Field = FileData()

    class AgentData {
        lateinit var AgentID: UUID
    }

    class FileData {
        lateinit var SimFilename: ByteArray
        lateinit var ViewerFilename: ByteArray
    }

    init {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.FileData_Field.SimFilename.size + 1 + 1 + this.FileData_Field.ViewerFilename.size + 20
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleInitiateDownload(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(1.toByte())
        byteBuffer.put((-109).toByte())
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packVariable(byteBuffer, this.FileData_Field.SimFilename, 1)
        packVariable(byteBuffer, this.FileData_Field.ViewerFilename, 1)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.FileData_Field.SimFilename = unpackVariable(byteBuffer, 1)
        this.FileData_Field.ViewerFilename = unpackVariable(byteBuffer, 1)
    }
}
