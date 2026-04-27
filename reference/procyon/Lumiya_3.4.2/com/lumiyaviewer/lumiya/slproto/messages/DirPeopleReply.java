// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class DirPeopleReply extends SLMessage
{
    public AgentData AgentData_Field;
    public QueryData QueryData_Field;
    public ArrayList<QueryReplies> QueryReplies_Fields;
    
    public DirPeopleReply() {
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
            final QueryReplies queryReplies = iterator.next();
            n += queryReplies.Group.length + (queryReplies.FirstName.length + 17 + 1 + queryReplies.LastName.length + 1) + 1 + 4;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleDirPeopleReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)36);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.QueryData_Field.QueryID);
        byteBuffer.put((byte)this.QueryReplies_Fields.size());
        for (final QueryReplies queryReplies : this.QueryReplies_Fields) {
            this.packUUID(byteBuffer, queryReplies.AgentID);
            this.packVariable(byteBuffer, queryReplies.FirstName, 1);
            this.packVariable(byteBuffer, queryReplies.LastName, 1);
            this.packVariable(byteBuffer, queryReplies.Group, 1);
            this.packBoolean(byteBuffer, queryReplies.Online);
            this.packInt(byteBuffer, queryReplies.Reputation);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.QueryData_Field.QueryID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final QueryReplies e = new QueryReplies();
            e.AgentID = this.unpackUUID(byteBuffer);
            e.FirstName = this.unpackVariable(byteBuffer, 1);
            e.LastName = this.unpackVariable(byteBuffer, 1);
            e.Group = this.unpackVariable(byteBuffer, 1);
            e.Online = this.unpackBoolean(byteBuffer);
            e.Reputation = this.unpackInt(byteBuffer);
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
        public UUID AgentID;
        public byte[] FirstName;
        public byte[] Group;
        public byte[] LastName;
        public boolean Online;
        public int Reputation;
    }
}
