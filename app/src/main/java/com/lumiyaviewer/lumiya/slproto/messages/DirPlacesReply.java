package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class DirPlacesReply extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<QueryData> QueryData_Fields = new ArrayList<>();
    public ArrayList<QueryReplies> QueryReplies_Fields = new ArrayList<>();
    public ArrayList<StatusData> StatusData_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
    }

    public static class QueryData {
        public UUID QueryID;
    }

    public static class QueryReplies {
        public boolean Auction;
        public float Dwell;
        public boolean ForSale;
        public byte[] Name;
        public UUID ParcelID;
    }

    public static class StatusData {
        public int Status;
    }

    public DirPlacesReply() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize() {
        int size = (this.QueryData_Fields.size() * 16) + 21 + 1;
        Iterator<T> it = this.QueryReplies_Fields.iterator();
        while (true) {
            int i = size;
            if (!it.hasNext()) {
                return i + 1 + (this.StatusData_Fields.size() * 4);
            }
            size = ((QueryReplies) it.next()).Name.length + 17 + 1 + 1 + 4 + i;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDirPlacesReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 35);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        byteBuffer.put((byte) this.QueryData_Fields.size());
        for (QueryData queryData : this.QueryData_Fields) {
            packUUID(byteBuffer, queryData.QueryID);
        }
        byteBuffer.put((byte) this.QueryReplies_Fields.size());
        for (QueryReplies queryReplies : this.QueryReplies_Fields) {
            packUUID(byteBuffer, queryReplies.ParcelID);
            packVariable(byteBuffer, queryReplies.Name, 1);
            packBoolean(byteBuffer, queryReplies.ForSale);
            packBoolean(byteBuffer, queryReplies.Auction);
            packFloat(byteBuffer, queryReplies.Dwell);
        }
        byteBuffer.put((byte) this.StatusData_Fields.size());
        for (StatusData statusData : this.StatusData_Fields) {
            packInt(byteBuffer, statusData.Status);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            QueryData queryData = new QueryData();
            queryData.QueryID = unpackUUID(byteBuffer);
            this.QueryData_Fields.add(queryData);
        }
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < b2; i2++) {
            QueryReplies queryReplies = new QueryReplies();
            queryReplies.ParcelID = unpackUUID(byteBuffer);
            queryReplies.Name = unpackVariable(byteBuffer, 1);
            queryReplies.ForSale = unpackBoolean(byteBuffer);
            queryReplies.Auction = unpackBoolean(byteBuffer);
            queryReplies.Dwell = unpackFloat(byteBuffer);
            this.QueryReplies_Fields.add(queryReplies);
        }
        byte b3 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i3 = 0; i3 < b3; i3++) {
            StatusData statusData = new StatusData();
            statusData.Status = unpackInt(byteBuffer);
            this.StatusData_Fields.add(statusData);
        }
    }
}
