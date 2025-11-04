// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ParcelAccessListRequest extends SLMessage
{
    public AgentData AgentData_Field;
    public Data Data_Field;
    
    public ParcelAccessListRequest() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.Data_Field = new Data();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 48;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleParcelAccessListRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-41));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.Data_Field.SequenceID);
        this.packInt(byteBuffer, this.Data_Field.Flags);
        this.packInt(byteBuffer, this.Data_Field.LocalID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.Data_Field.SequenceID = this.unpackInt(byteBuffer);
        this.Data_Field.Flags = this.unpackInt(byteBuffer);
        this.Data_Field.LocalID = this.unpackInt(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class Data
    {
        public int Flags;
        public int LocalID;
        public int SequenceID;
    }
}
