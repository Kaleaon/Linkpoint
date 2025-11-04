// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RemoveAttachment extends SLMessage
{
    public AgentData AgentData_Field;
    public AttachmentBlock AttachmentBlock_Field;
    
    public RemoveAttachment() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.AttachmentBlock_Field = new AttachmentBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 53;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRemoveAttachment(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)76);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packByte(byteBuffer, (byte)this.AttachmentBlock_Field.AttachmentPoint);
        this.packUUID(byteBuffer, this.AttachmentBlock_Field.ItemID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AttachmentBlock_Field.AttachmentPoint = (this.unpackByte(byteBuffer) & 0xFF);
        this.AttachmentBlock_Field.ItemID = this.unpackUUID(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class AttachmentBlock
    {
        public int AttachmentPoint;
        public UUID ItemID;
    }
}
