// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class PlacesReply extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<QueryData> QueryData_Fields;
    public TransactionData TransactionData_Field;
    
    public PlacesReply() {
        this.QueryData_Fields = new ArrayList<QueryData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.TransactionData_Field = new TransactionData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.QueryData_Fields.iterator();
        int n = 53;
        while (iterator.hasNext()) {
            final QueryData queryData = iterator.next();
            n += queryData.SimName.length + (queryData.Name.length + 17 + 1 + queryData.Desc.length + 4 + 4 + 1 + 4 + 4 + 4 + 1) + 16 + 4 + 4;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandlePlacesReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)30);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.QueryID);
        this.packUUID(byteBuffer, this.TransactionData_Field.TransactionID);
        byteBuffer.put((byte)this.QueryData_Fields.size());
        for (final QueryData queryData : this.QueryData_Fields) {
            this.packUUID(byteBuffer, queryData.OwnerID);
            this.packVariable(byteBuffer, queryData.Name, 1);
            this.packVariable(byteBuffer, queryData.Desc, 1);
            this.packInt(byteBuffer, queryData.ActualArea);
            this.packInt(byteBuffer, queryData.BillableArea);
            this.packByte(byteBuffer, (byte)queryData.Flags);
            this.packFloat(byteBuffer, queryData.GlobalX);
            this.packFloat(byteBuffer, queryData.GlobalY);
            this.packFloat(byteBuffer, queryData.GlobalZ);
            this.packVariable(byteBuffer, queryData.SimName, 1);
            this.packUUID(byteBuffer, queryData.SnapshotID);
            this.packFloat(byteBuffer, queryData.Dwell);
            this.packInt(byteBuffer, queryData.Price);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.QueryID = this.unpackUUID(byteBuffer);
        this.TransactionData_Field.TransactionID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final QueryData e = new QueryData();
            e.OwnerID = this.unpackUUID(byteBuffer);
            e.Name = this.unpackVariable(byteBuffer, 1);
            e.Desc = this.unpackVariable(byteBuffer, 1);
            e.ActualArea = this.unpackInt(byteBuffer);
            e.BillableArea = this.unpackInt(byteBuffer);
            e.Flags = (this.unpackByte(byteBuffer) & 0xFF);
            e.GlobalX = this.unpackFloat(byteBuffer);
            e.GlobalY = this.unpackFloat(byteBuffer);
            e.GlobalZ = this.unpackFloat(byteBuffer);
            e.SimName = this.unpackVariable(byteBuffer, 1);
            e.SnapshotID = this.unpackUUID(byteBuffer);
            e.Dwell = this.unpackFloat(byteBuffer);
            e.Price = this.unpackInt(byteBuffer);
            this.QueryData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID QueryID;
    }
    
    public static class QueryData
    {
        public int ActualArea;
        public int BillableArea;
        public byte[] Desc;
        public float Dwell;
        public int Flags;
        public float GlobalX;
        public float GlobalY;
        public float GlobalZ;
        public byte[] Name;
        public UUID OwnerID;
        public int Price;
        public byte[] SimName;
        public UUID SnapshotID;
    }
    
    public static class TransactionData
    {
        public UUID TransactionID;
    }
}
