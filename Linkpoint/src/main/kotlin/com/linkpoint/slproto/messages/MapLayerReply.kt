package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class MapLayerReply : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<LayerData> LayerData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public Int Flags
    }

    @JvmStatic
    class LayerData {
        public Int Bottom
        public UUID ImageID
        public Int Left
        public Int Right
        public Int Top
    }

    public MapLayerReply() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    public fun CalcPayloadSize(): Int {
        return (this.LayerData_Fields.size() * 32) + 25
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleMapLayerReply(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -106)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packInt(byteBuffer, this.AgentData_Field.Flags)
        byteBuffer.put((Byte) this.LayerData_Fields.size())
        for (LayerData layerData : this.LayerData_Fields) {
            packInt(byteBuffer, layerData.Left)
            packInt(byteBuffer, layerData.Right)
            packInt(byteBuffer, layerData.Top)
            packInt(byteBuffer, layerData.Bottom)
            packUUID(byteBuffer, layerData.ImageID)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.Flags = unpackInt(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val layerData: LayerData = LayerData()
            layerData.Left = unpackInt(byteBuffer)
            layerData.Right = unpackInt(byteBuffer)
            layerData.Top = unpackInt(byteBuffer)
            layerData.Bottom = unpackInt(byteBuffer)
            layerData.ImageID = unpackUUID(byteBuffer)
            this.LayerData_Fields.add(layerData)
        }
    }
}
