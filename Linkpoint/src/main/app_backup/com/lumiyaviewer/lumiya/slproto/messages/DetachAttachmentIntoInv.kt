package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer

class DetachAttachmentIntoInv : SLMessage {
    var ObjectData_Field: ObjectData = ObjectData()

    class ObjectData {
        var AgentID: UUID = UUIDPool.ZeroUUID
        var ItemID: UUID = UUIDPool.ZeroUUID
    }

    init {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return 36
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleDetachAttachmentIntoInv(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(1.toByte())
        byteBuffer.put((-115).toByte())
        packUUID(byteBuffer, this.ObjectData_Field.AgentID)
        packUUID(byteBuffer, this.ObjectData_Field.ItemID)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.ObjectData_Field.AgentID = unpackUUID(byteBuffer)
        this.ObjectData_Field.ItemID = unpackUUID(byteBuffer)
    }
}
