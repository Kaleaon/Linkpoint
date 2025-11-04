// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class DirGroupsReply extends SLMessage
{
    public AgentData AgentData_Field;
    public QueryData QueryData_Field;
    public ArrayList<QueryReplies> QueryReplies_Fields;
    
    public DirGroupsReply() {
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
            n += iterator.next().GroupName.length + 17 + 4 + 4;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleDirGroupsReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)38);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.QueryData_Field.QueryID);
        byteBuffer.put((byte)this.QueryReplies_Fields.size());
        for (final QueryReplies queryReplies : this.QueryReplies_Fields) {
            this.packUUID(byteBuffer, queryReplies.GroupID);
            this.packVariable(byteBuffer, queryReplies.GroupName, 1);
            this.packInt(byteBuffer, queryReplies.Members);
            this.packFloat(byteBuffer, queryReplies.SearchOrder);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.QueryData_Field.QueryID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final QueryReplies e = new QueryReplies();
            e.GroupID = this.unpackUUID(byteBuffer);
            e.GroupName = this.unpackVariable(byteBuffer, 1);
            e.Members = this.unpackInt(byteBuffer);
            e.SearchOrder = this.unpackFloat(byteBuffer);
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
        public UUID GroupID;
        public byte[] GroupName;
        public int Members;
        public float SearchOrder;
    }
}
