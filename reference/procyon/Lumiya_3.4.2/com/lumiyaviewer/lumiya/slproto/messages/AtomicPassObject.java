// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AtomicPassObject extends SLMessage
{
    public TaskData TaskData_Field;
    
    public AtomicPassObject() {
        this.zeroCoded = false;
        this.TaskData_Field = new TaskData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 18;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAtomicPassObject(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)28);
        this.packUUID(byteBuffer, this.TaskData_Field.TaskID);
        this.packBoolean(byteBuffer, this.TaskData_Field.AttachmentNeedsSave);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.TaskData_Field.TaskID = this.unpackUUID(byteBuffer);
        this.TaskData_Field.AttachmentNeedsSave = this.unpackBoolean(byteBuffer);
    }
    
    public static class TaskData
    {
        public boolean AttachmentNeedsSave;
        public UUID TaskID;
    }
}
