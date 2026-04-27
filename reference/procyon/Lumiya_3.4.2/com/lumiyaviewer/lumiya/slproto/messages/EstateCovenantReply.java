// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class EstateCovenantReply extends SLMessage
{
    public Data Data_Field;
    
    public EstateCovenantReply() {
        this.zeroCoded = false;
        this.Data_Field = new Data();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Data_Field.EstateName.length + 21 + 16 + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleEstateCovenantReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-52));
        this.packUUID(byteBuffer, this.Data_Field.CovenantID);
        this.packInt(byteBuffer, this.Data_Field.CovenantTimestamp);
        this.packVariable(byteBuffer, this.Data_Field.EstateName, 1);
        this.packUUID(byteBuffer, this.Data_Field.EstateOwnerID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.Data_Field.CovenantID = this.unpackUUID(byteBuffer);
        this.Data_Field.CovenantTimestamp = this.unpackInt(byteBuffer);
        this.Data_Field.EstateName = this.unpackVariable(byteBuffer, 1);
        this.Data_Field.EstateOwnerID = this.unpackUUID(byteBuffer);
    }
    
    public static class Data
    {
        public UUID CovenantID;
        public int CovenantTimestamp;
        public byte[] EstateName;
        public UUID EstateOwnerID;
    }
}
