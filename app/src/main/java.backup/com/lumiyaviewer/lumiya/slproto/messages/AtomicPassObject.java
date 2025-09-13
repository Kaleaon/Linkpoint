package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class AtomicPassObject extends SLMessage {
    public TaskData TaskData_Field = new TaskData();

    public static class TaskData {
        public boolean AttachmentNeedsSave;
        public UUID TaskID;
    }

    public AtomicPassObject() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 18;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAtomicPassObject(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.FS);
        packUUID(byteBuffer, this.TaskData_Field.TaskID);
        packBoolean(byteBuffer, this.TaskData_Field.AttachmentNeedsSave);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.TaskData_Field.TaskID = unpackUUID(byteBuffer);
        this.TaskData_Field.AttachmentNeedsSave = unpackBoolean(byteBuffer);
    }
}
