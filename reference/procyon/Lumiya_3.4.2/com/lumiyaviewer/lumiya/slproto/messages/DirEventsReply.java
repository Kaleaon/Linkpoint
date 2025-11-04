// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class DirEventsReply extends SLMessage
{
    public AgentData AgentData_Field;
    public QueryData QueryData_Field;
    public ArrayList<QueryReplies> QueryReplies_Fields;
    public ArrayList<StatusData> StatusData_Fields;
    
    public DirEventsReply() {
        this.QueryReplies_Fields = new ArrayList<QueryReplies>();
        this.StatusData_Fields = new ArrayList<StatusData>();
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
            n += queryReplies.Date.length + (queryReplies.Name.length + 17 + 4 + 1) + 4 + 4;
        }
        return n + 1 + this.StatusData_Fields.size() * 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleDirEventsReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)37);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.QueryData_Field.QueryID);
        byteBuffer.put((byte)this.QueryReplies_Fields.size());
        for (final QueryReplies queryReplies : this.QueryReplies_Fields) {
            this.packUUID(byteBuffer, queryReplies.OwnerID);
            this.packVariable(byteBuffer, queryReplies.Name, 1);
            this.packInt(byteBuffer, queryReplies.EventID);
            this.packVariable(byteBuffer, queryReplies.Date, 1);
            this.packInt(byteBuffer, queryReplies.UnixTime);
            this.packInt(byteBuffer, queryReplies.EventFlags);
        }
        byteBuffer.put((byte)this.StatusData_Fields.size());
        final Iterator<Object> iterator2 = this.StatusData_Fields.iterator();
        while (iterator2.hasNext()) {
            this.packInt(byteBuffer, iterator2.next().Status);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final int n = 0;
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.QueryData_Field.QueryID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final QueryReplies e = new QueryReplies();
            e.OwnerID = this.unpackUUID(byteBuffer);
            e.Name = this.unpackVariable(byteBuffer, 1);
            e.EventID = this.unpackInt(byteBuffer);
            e.Date = this.unpackVariable(byteBuffer, 1);
            e.UnixTime = this.unpackInt(byteBuffer);
            e.EventFlags = this.unpackInt(byteBuffer);
            this.QueryReplies_Fields.add(e);
        }
        final byte value2 = byteBuffer.get();
        for (int j = n; j < (value2 & 0xFF); ++j) {
            final StatusData e2 = new StatusData();
            e2.Status = this.unpackInt(byteBuffer);
            this.StatusData_Fields.add(e2);
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
        public byte[] Date;
        public int EventFlags;
        public int EventID;
        public byte[] Name;
        public UUID OwnerID;
        public int UnixTime;
    }
    
    public static class StatusData
    {
        public int Status;
    }
}
