package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
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

    fun CalcPayloadSize(): Int {
        return 18
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleAtomicPassObject(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.put(Ascii.FS)
        packUUID(byteBuffer, this.TaskData_Field.TaskID)
        packBoolean(byteBuffer, this.TaskData_Field.AttachmentNeedsSave)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.TaskData_Field.TaskID = unpackUUID(byteBuffer)
        this.TaskData_Field.AttachmentNeedsSave = unpackBoolean(byteBuffer)
    }
}
