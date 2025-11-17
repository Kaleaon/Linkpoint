package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class RequestImage : SLMessage {
    AgentData AgentData_Field
    ArrayList<RequestImageData> RequestImageData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class RequestImageData {
        Int DiscardLevel
        Float DownloadPriority
        UUID Image
        Int Packet
        Int Type
    }

    RequestImage() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    Int CalcPayloadSize() {
        return (this.RequestImageData_Fields.size() * 26) + 34
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRequestImage(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((Byte) 8)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        byteBuffer.put((Byte) this.RequestImageData_Fields.size())
        for (RequestImageData requestImageData : this.RequestImageData_Fields) {
            packUUID(byteBuffer, requestImageData.Image)
            packByte(byteBuffer, (Byte) requestImageData.DiscardLevel)
            packFloat(byteBuffer, requestImageData.DownloadPriority)
            packInt(byteBuffer, requestImageData.Packet)
            packByte(byteBuffer, (Byte) requestImageData.Type)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            RequestImageData requestImageData = RequestImageData()
            requestImageData.Image = unpackUUID(byteBuffer)
            requestImageData.DiscardLevel = unpackByte(byteBuffer)
            requestImageData.DownloadPriority = unpackFloat(byteBuffer)
            requestImageData.Packet = unpackInt(byteBuffer)
            requestImageData.Type = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.RequestImageData_Fields.add(requestImageData)
        }
    }
}
