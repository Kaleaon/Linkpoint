package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class RequestImage : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<RequestImageData> RequestImageData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class RequestImageData {
        public Int DiscardLevel
        public Float DownloadPriority
        public UUID Image
        public Int Packet
        public Int Type
    }

    public RequestImage() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    public Int CalcPayloadSize() {
        return (this.RequestImageData_Fields.size() * 26) + 34
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRequestImage(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
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

    fun UnpackPayload(ByteBuffer byteBuffer) {
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
