// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class FormFriendship extends SLMessage
{
    public AgentBlock AgentBlock_Field;
    
    public FormFriendship() {
        this.zeroCoded = false;
        this.AgentBlock_Field = new AgentBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleFormFriendship(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)43);
        this.packUUID(byteBuffer, this.AgentBlock_Field.SourceID);
        this.packUUID(byteBuffer, this.AgentBlock_Field.DestID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentBlock_Field.SourceID = this.unpackUUID(byteBuffer);
        this.AgentBlock_Field.DestID = this.unpackUUID(byteBuffer);
    }
    
    public static class AgentBlock
    {
        public UUID DestID;
        public UUID SourceID;
    }
}
