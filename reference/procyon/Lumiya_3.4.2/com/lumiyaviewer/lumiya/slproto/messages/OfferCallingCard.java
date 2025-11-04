// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class OfferCallingCard extends SLMessage
{
    public AgentBlock AgentBlock_Field;
    public AgentData AgentData_Field;
    
    public OfferCallingCard() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.AgentBlock_Field = new AgentBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 68;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleOfferCallingCard(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)45);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.AgentBlock_Field.DestID);
        this.packUUID(byteBuffer, this.AgentBlock_Field.TransactionID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentBlock_Field.DestID = this.unpackUUID(byteBuffer);
        this.AgentBlock_Field.TransactionID = this.unpackUUID(byteBuffer);
    }
    
    public static class AgentBlock
    {
        public UUID DestID;
        public UUID TransactionID;
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
}
