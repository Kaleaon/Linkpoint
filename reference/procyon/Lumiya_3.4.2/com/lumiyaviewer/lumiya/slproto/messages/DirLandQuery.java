// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class DirLandQuery extends SLMessage
{
    public AgentData AgentData_Field;
    public QueryData QueryData_Field;
    
    public DirLandQuery() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.QueryData_Field = new QueryData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 72;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleDirLandQuery(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)48);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.QueryData_Field.QueryID);
        this.packInt(byteBuffer, this.QueryData_Field.QueryFlags);
        this.packInt(byteBuffer, this.QueryData_Field.SearchType);
        this.packInt(byteBuffer, this.QueryData_Field.Price);
        this.packInt(byteBuffer, this.QueryData_Field.Area);
        this.packInt(byteBuffer, this.QueryData_Field.QueryStart);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.QueryData_Field.QueryID = this.unpackUUID(byteBuffer);
        this.QueryData_Field.QueryFlags = this.unpackInt(byteBuffer);
        this.QueryData_Field.SearchType = this.unpackInt(byteBuffer);
        this.QueryData_Field.Price = this.unpackInt(byteBuffer);
        this.QueryData_Field.Area = this.unpackInt(byteBuffer);
        this.QueryData_Field.QueryStart = this.unpackInt(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class QueryData
    {
        public int Area;
        public int Price;
        public int QueryFlags;
        public UUID QueryID;
        public int QueryStart;
        public int SearchType;
    }
}
