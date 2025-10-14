package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class InitiateDownload : SLMessage {
    AgentData AgentData_Field = AgentData()
    FileData FileData_Field = FileData()

    class AgentData {
        UUID AgentID
    }

    class FileData {
        byte[] SimFilename
        byte[] ViewerFilename
    }

    InitiateDownload() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.FileData_Field.SimFilename.length + 1 + 1 + this.FileData_Field.ViewerFilename.length + 20
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleInitiateDownload(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) -109)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packVariable(byteBuffer, this.FileData_Field.SimFilename, 1)
        packVariable(byteBuffer, this.FileData_Field.ViewerFilename, 1)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.FileData_Field.SimFilename = unpackVariable(byteBuffer, 1)
        this.FileData_Field.ViewerFilename = unpackVariable(byteBuffer, 1)
    }
}
