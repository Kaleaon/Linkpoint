package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.base.Ascii
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AtomicPassObject : SLMessage {
    TaskData TaskData_Field = TaskData()

    class TaskData {
        Boolean AttachmentNeedsSave
        UUID TaskID
    }

    AtomicPassObject() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 18
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAtomicPassObject(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.FS)
        packUUID(byteBuffer, this.TaskData_Field.TaskID)
        packBoolean(byteBuffer, this.TaskData_Field.AttachmentNeedsSave)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.TaskData_Field.TaskID = unpackUUID(byteBuffer)
        this.TaskData_Field.AttachmentNeedsSave = unpackBoolean(byteBuffer)
    }
}
