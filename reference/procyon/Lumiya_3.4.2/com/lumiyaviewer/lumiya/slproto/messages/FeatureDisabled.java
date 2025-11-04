// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class FeatureDisabled extends SLMessage
{
    public FailureInfo FailureInfo_Field;
    
    public FeatureDisabled() {
        this.zeroCoded = false;
        this.FailureInfo_Field = new FailureInfo();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.FailureInfo_Field.ErrorMessage.length + 1 + 16 + 16 + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleFeatureDisabled(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)19);
        this.packVariable(byteBuffer, this.FailureInfo_Field.ErrorMessage, 1);
        this.packUUID(byteBuffer, this.FailureInfo_Field.AgentID);
        this.packUUID(byteBuffer, this.FailureInfo_Field.TransactionID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.FailureInfo_Field.ErrorMessage = this.unpackVariable(byteBuffer, 1);
        this.FailureInfo_Field.AgentID = this.unpackUUID(byteBuffer);
        this.FailureInfo_Field.TransactionID = this.unpackUUID(byteBuffer);
    }
    
    public static class FailureInfo
    {
        public UUID AgentID;
        public byte[] ErrorMessage;
        public UUID TransactionID;
    }
}
