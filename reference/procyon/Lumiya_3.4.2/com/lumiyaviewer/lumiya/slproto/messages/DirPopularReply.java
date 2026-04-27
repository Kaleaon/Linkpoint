// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class DirPopularReply extends SLMessage
{
    public AgentData AgentData_Field;
    public QueryData QueryData_Field;
    public ArrayList<QueryReplies> QueryReplies_Fields;
    
    public DirPopularReply() {
        this.QueryReplies_Fields = new ArrayList<QueryReplies>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.QueryData_Field = new QueryData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.QueryReplies_Fields.iterator();
        int n = 37;
        while (iterator.hasNext()) {
            n += iterator.next().Name.length + 17 + 4;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleDirPopularReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)53);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.QueryData_Field.QueryID);
        byteBuffer.put((byte)this.QueryReplies_Fields.size());
        for (final QueryReplies queryReplies : this.QueryReplies_Fields) {
            this.packUUID(byteBuffer, queryReplies.ParcelID);
            this.packVariable(byteBuffer, queryReplies.Name, 1);
            this.packFloat(byteBuffer, queryReplies.Dwell);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.QueryData_Field.QueryID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final QueryReplies e = new QueryReplies();
            e.ParcelID = this.unpackUUID(byteBuffer);
            e.Name = this.unpackVariable(byteBuffer, 1);
            e.Dwell = this.unpackFloat(byteBuffer);
            this.QueryReplies_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
    }
    
    public static class QueryData
    {
        public UUID QueryID;
    }
    
    public static class QueryReplies
    {
        public float Dwell;
        public byte[] Name;
        public UUID ParcelID;
    }
}
