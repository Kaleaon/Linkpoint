package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AtomicPassObject : SLMessage() {
    public TaskData TaskData_Field = TaskData()

    @JvmStatic
    class TaskData {
        public Boolean AttachmentNeedsSave
        public UUID TaskID
    }

    public AtomicPassObject() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 18
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAtomicPassObject(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.FS)
        packUUID(byteBuffer, this.TaskData_Field.TaskID)
        packBoolean(byteBuffer, this.TaskData_Field.AttachmentNeedsSave)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.TaskData_Field.TaskID = unpackUUID(byteBuffer)
        this.TaskData_Field.AttachmentNeedsSave = unpackBoolean(byteBuffer)
    }
}
