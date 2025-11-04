// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class DetachAttachmentIntoInv extends SLMessage
{
    public ObjectData ObjectData_Field;
    
    public DetachAttachmentIntoInv() {
        this.zeroCoded = false;
        this.ObjectData_Field = new ObjectData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleDetachAttachmentIntoInv(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-115));
        this.packUUID(byteBuffer, this.ObjectData_Field.AgentID);
        this.packUUID(byteBuffer, this.ObjectData_Field.ItemID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.ObjectData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.ObjectData_Field.ItemID = this.unpackUUID(byteBuffer);
    }
    
    public static class ObjectData
    {
        public UUID AgentID;
        public UUID ItemID;
    }
}
