package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class DataHomeLocationReply : SLMessage {
    Info Info_Field = Info()

    class Info {
        UUID AgentID
        LLVector3 LookAt
        LLVector3 Position
        Long RegionHandle
    }

    DataHomeLocationReply() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 52
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDataHomeLocationReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 68)
        packUUID(byteBuffer, this.Info_Field.AgentID)
        packLong(byteBuffer, this.Info_Field.RegionHandle)
        packLLVector3(byteBuffer, this.Info_Field.Position)
        packLLVector3(byteBuffer, this.Info_Field.LookAt)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.Info_Field.AgentID = unpackUUID(byteBuffer)
        this.Info_Field.RegionHandle = unpackLong(byteBuffer)
        this.Info_Field.Position = unpackLLVector3(byteBuffer)
        this.Info_Field.LookAt = unpackLLVector3(byteBuffer)
    }
}
