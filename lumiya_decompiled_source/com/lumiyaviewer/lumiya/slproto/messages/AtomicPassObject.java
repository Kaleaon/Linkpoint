package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class AtomicPassObject extends SLMessage {
    public TaskData TaskData_Field;

    /* loaded from: classes.dex */
    public static class TaskData {
        public boolean AttachmentNeedsSave;
        public UUID TaskID;
    }

    public AtomicPassObject() {
        this.zeroCoded = false;
        this.TaskData_Field = new TaskData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 18;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAtomicPassObject(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.FS);
        packUUID(byteBuffer, this.TaskData_Field.TaskID);
        packBoolean(byteBuffer, this.TaskData_Field.AttachmentNeedsSave);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.TaskData_Field.TaskID = unpackUUID(byteBuffer);
        this.TaskData_Field.AttachmentNeedsSave = unpackBoolean(byteBuffer);
    }
}
