// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class DirPopularQueryBackend extends SLMessage
{
    public AgentData AgentData_Field;
    public QueryData QueryData_Field;
    
    public DirPopularQueryBackend() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.QueryData_Field = new QueryData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 45;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleDirPopularQueryBackend(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)52);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.QueryData_Field.QueryID);
        this.packInt(byteBuffer, this.QueryData_Field.QueryFlags);
        this.packInt(byteBuffer, this.QueryData_Field.EstateID);
        this.packBoolean(byteBuffer, this.QueryData_Field.Godlike);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.QueryData_Field.QueryID = this.unpackUUID(byteBuffer);
        this.QueryData_Field.QueryFlags = this.unpackInt(byteBuffer);
        this.QueryData_Field.EstateID = this.unpackInt(byteBuffer);
        this.QueryData_Field.Godlike = this.unpackBoolean(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
    }
    
    public static class QueryData
    {
        public int EstateID;
        public boolean Godlike;
        public int QueryFlags;
        public UUID QueryID;
    }
}
