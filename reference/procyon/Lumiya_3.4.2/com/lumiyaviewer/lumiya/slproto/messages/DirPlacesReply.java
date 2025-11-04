// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class DirPlacesReply extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<QueryData> QueryData_Fields;
    public ArrayList<QueryReplies> QueryReplies_Fields;
    public ArrayList<StatusData> StatusData_Fields;
    
    public DirPlacesReply() {
        this.QueryData_Fields = new ArrayList<QueryData>();
        this.QueryReplies_Fields = new ArrayList<QueryReplies>();
        this.StatusData_Fields = new ArrayList<StatusData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final int size = this.QueryData_Fields.size();
        final Iterator<Object> iterator = this.QueryReplies_Fields.iterator();
        int n = size * 16 + 21 + 1;
        while (iterator.hasNext()) {
            n += iterator.next().Name.length + 17 + 1 + 1 + 4;
        }
        return n + 1 + this.StatusData_Fields.size() * 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleDirPlacesReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)35);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        byteBuffer.put((byte)this.QueryData_Fields.size());
        final Iterator<Object> iterator = this.QueryData_Fields.iterator();
        while (iterator.hasNext()) {
            this.packUUID(byteBuffer, iterator.next().QueryID);
        }
        byteBuffer.put((byte)this.QueryReplies_Fields.size());
        for (final QueryReplies queryReplies : this.QueryReplies_Fields) {
            this.packUUID(byteBuffer, queryReplies.ParcelID);
            this.packVariable(byteBuffer, queryReplies.Name, 1);
            this.packBoolean(byteBuffer, queryReplies.ForSale);
            this.packBoolean(byteBuffer, queryReplies.Auction);
            this.packFloat(byteBuffer, queryReplies.Dwell);
        }
        byteBuffer.put((byte)this.StatusData_Fields.size());
        final Iterator<Object> iterator3 = this.StatusData_Fields.iterator();
        while (iterator3.hasNext()) {
            this.packInt(byteBuffer, iterator3.next().Status);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final int n = 0;
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final QueryData e = new QueryData();
            e.QueryID = this.unpackUUID(byteBuffer);
            this.QueryData_Fields.add(e);
        }
        final byte value2 = byteBuffer.get();
        for (int j = 0; j < (value2 & 0xFF); ++j) {
            final QueryReplies e2 = new QueryReplies();
            e2.ParcelID = this.unpackUUID(byteBuffer);
            e2.Name = this.unpackVariable(byteBuffer, 1);
            e2.ForSale = this.unpackBoolean(byteBuffer);
            e2.Auction = this.unpackBoolean(byteBuffer);
            e2.Dwell = this.unpackFloat(byteBuffer);
            this.QueryReplies_Fields.add(e2);
        }
        final byte value3 = byteBuffer.get();
        for (int k = n; k < (value3 & 0xFF); ++k) {
            final StatusData e3 = new StatusData();
            e3.Status = this.unpackInt(byteBuffer);
            this.StatusData_Fields.add(e3);
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
        public boolean Auction;
        public float Dwell;
        public boolean ForSale;
        public byte[] Name;
        public UUID ParcelID;
    }
    
    public static class StatusData
    {
        public int Status;
    }
}
