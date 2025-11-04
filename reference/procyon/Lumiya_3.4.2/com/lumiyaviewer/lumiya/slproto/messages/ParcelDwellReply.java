// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ParcelDwellReply extends SLMessage
{
    public AgentData AgentData_Field;
    public Data Data_Field;
    
    public ParcelDwellReply() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.Data_Field = new Data();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 44;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleParcelDwellReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-37));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packInt(byteBuffer, this.Data_Field.LocalID);
        this.packUUID(byteBuffer, this.Data_Field.ParcelID);
        this.packFloat(byteBuffer, this.Data_Field.Dwell);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.Data_Field.LocalID = this.unpackInt(byteBuffer);
        this.Data_Field.ParcelID = this.unpackUUID(byteBuffer);
        this.Data_Field.Dwell = this.unpackFloat(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
    }
    
    public static class Data
    {
        public float Dwell;
        public int LocalID;
        public UUID ParcelID;
    }
}
