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

    fun CalcPayloadSize(): Int {
        return (this.RequestImageData_Fields.size() * 26) + 34
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleRequestImage(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.put((Byte) 8)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        byteBuffer.put((this as Byte).RequestImageData_Fields.size())
        for (RequestImageData requestImageData : this.RequestImageData_Fields) {
            packUUID(byteBuffer, requestImageData.Image)
            packByte(byteBuffer, (requestImageData as Byte).DiscardLevel)
            packFloat(byteBuffer, requestImageData.DownloadPriority)
            packInt(byteBuffer, requestImageData.Packet)
            packByte(byteBuffer, (requestImageData as Byte).Type)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
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
