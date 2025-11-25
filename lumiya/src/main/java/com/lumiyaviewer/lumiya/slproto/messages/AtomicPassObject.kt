package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class AtomicPassObject : SLMessage {
    var TaskData_Field: TaskData = TaskData()

    class TaskData {
        var AttachmentNeedsSave: Boolean = false
        var TaskID: UUID? = null
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return 18
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAtomicPassObject(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.put(28.toByte()) // Ascii.FS
        packUUID(byteBuffer, TaskData_Field.TaskID!!)
        packBoolean(byteBuffer, TaskData_Field.AttachmentNeedsSave)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        TaskData_Field.TaskID = unpackUUID(byteBuffer)
        TaskData_Field.AttachmentNeedsSave = unpackBoolean(byteBuffer)
    }
}
