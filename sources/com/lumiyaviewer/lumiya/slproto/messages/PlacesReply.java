package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class PlacesReply extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<QueryData> QueryData_Fields = new ArrayList<>();
    public TransactionData TransactionData_Field;

    public static class AgentData {
        public UUID AgentID;
        public UUID QueryID;
    }

    public static class QueryData {
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

    public static class TransactionData {
        public UUID TransactionID;
    }

    public PlacesReply() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.TransactionData_Field = new TransactionData();
    }

    public int CalcPayloadSize() {
        int i = 53;
        Iterator<T> it = this.QueryData_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            QueryData queryData = (QueryData) it.next();
            i = queryData.SimName.length + queryData.Name.length + 17 + 1 + queryData.Desc.length + 4 + 4 + 1 + 4 + 4 + 4 + 1 + 16 + 4 + 4 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandlePlacesReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put(Ascii.RS);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.QueryID);
        packUUID(byteBuffer, this.TransactionData_Field.TransactionID);
        byteBuffer.put((byte) this.QueryData_Fields.size());
        for (QueryData queryData : this.QueryData_Fields) {
            packUUID(byteBuffer, queryData.OwnerID);
            packVariable(byteBuffer, queryData.Name, 1);
            packVariable(byteBuffer, queryData.Desc, 1);
            packInt(byteBuffer, queryData.ActualArea);
            packInt(byteBuffer, queryData.BillableArea);
            packByte(byteBuffer, (byte) queryData.Flags);
            packFloat(byteBuffer, queryData.GlobalX);
            packFloat(byteBuffer, queryData.GlobalY);
            packFloat(byteBuffer, queryData.GlobalZ);
            packVariable(byteBuffer, queryData.SimName, 1);
            packUUID(byteBuffer, queryData.SnapshotID);
            packFloat(byteBuffer, queryData.Dwell);
            packInt(byteBuffer, queryData.Price);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.QueryID = unpackUUID(byteBuffer);
        this.TransactionData_Field.TransactionID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            QueryData queryData = new QueryData();
            queryData.OwnerID = unpackUUID(byteBuffer);
            queryData.Name = unpackVariable(byteBuffer, 1);
            queryData.Desc = unpackVariable(byteBuffer, 1);
            queryData.ActualArea = unpackInt(byteBuffer);
            queryData.BillableArea = unpackInt(byteBuffer);
            queryData.Flags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            queryData.GlobalX = unpackFloat(byteBuffer);
            queryData.GlobalY = unpackFloat(byteBuffer);
            queryData.GlobalZ = unpackFloat(byteBuffer);
            queryData.SimName = unpackVariable(byteBuffer, 1);
            queryData.SnapshotID = unpackUUID(byteBuffer);
            queryData.Dwell = unpackFloat(byteBuffer);
            queryData.Price = unpackInt(byteBuffer);
            this.QueryData_Fields.add(queryData);
        }
    }
}
