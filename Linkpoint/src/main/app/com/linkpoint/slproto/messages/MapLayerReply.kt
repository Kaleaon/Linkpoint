package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class MapLayerReply : SLMessage {
    AgentData AgentData_Field
    ArrayList<LayerData> LayerData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        Int Flags
    }

    class LayerData {
        Int Bottom
        UUID ImageID
        Int Left
        Int Right
        Int Top
    }

    MapLayerReply() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    Int CalcPayloadSize() {
        return (this.LayerData_Fields.size() * 32) + 25
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMapLayerReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
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

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.Flags = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            LayerData layerData = LayerData()
            layerData.Left = unpackInt(byteBuffer)
            layerData.Right = unpackInt(byteBuffer)
            layerData.Top = unpackInt(byteBuffer)
            layerData.Bottom = unpackInt(byteBuffer)
            layerData.ImageID = unpackUUID(byteBuffer)
            this.LayerData_Fields.add(layerData)
        }
    }
}
