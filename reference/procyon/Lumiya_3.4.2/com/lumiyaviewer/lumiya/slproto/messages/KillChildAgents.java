// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class KillChildAgents extends SLMessage
{
    public IDBlock IDBlock_Field;
    
    public KillChildAgents() {
        this.zeroCoded = false;
        this.IDBlock_Field = new IDBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 20;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleKillChildAgents(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-14));
        this.packUUID(byteBuffer, this.IDBlock_Field.AgentID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.IDBlock_Field.AgentID = this.unpackUUID(byteBuffer);
    }
    
    public static class IDBlock
    {
        public UUID AgentID;
    }
}
