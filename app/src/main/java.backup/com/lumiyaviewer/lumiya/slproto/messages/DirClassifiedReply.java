package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class DirClassifiedReply extends SLMessage {
    public AgentData AgentData_Field;
    public QueryData QueryData_Field;
    public ArrayList<QueryReplies> QueryReplies_Fields = new ArrayList<>();
    public ArrayList<StatusData> StatusData_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
    }

    public static class QueryData {
        public UUID QueryID;
    }

    public static class QueryReplies {
        public int ClassifiedFlags;
        public UUID ClassifiedID;
        public int CreationDate;
        public int ExpirationDate;
        public byte[] Name;
        public int PriceForListing;
    }

    public static class StatusData {
        public int Status;
    }

    public DirClassifiedReply() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.QueryData_Field = new QueryData();
    }

    public int CalcPayloadSize() {
        int i = 37;
        Iterator<T> it = this.QueryReplies_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2 + 1 + (this.StatusData_Fields.size() * 4);
            }
            i = ((QueryReplies) it.next()).Name.length + 17 + 1 + 4 + 4 + 4 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDirClassifiedReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 41);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.QueryData_Field.QueryID);
        byteBuffer.put((byte) this.QueryReplies_Fields.size());
        for (QueryReplies queryReplies : this.QueryReplies_Fields) {
            packUUID(byteBuffer, queryReplies.ClassifiedID);
            packVariable(byteBuffer, queryReplies.Name, 1);
            packByte(byteBuffer, (byte) queryReplies.ClassifiedFlags);
            packInt(byteBuffer, queryReplies.CreationDate);
            packInt(byteBuffer, queryReplies.ExpirationDate);
            packInt(byteBuffer, queryReplies.PriceForListing);
        }
        byteBuffer.put((byte) this.StatusData_Fields.size());
        for (StatusData statusData : this.StatusData_Fields) {
            packInt(byteBuffer, statusData.Status);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            QueryReplies queryReplies = new QueryReplies();
            queryReplies.ClassifiedID = unpackUUID(byteBuffer);
            queryReplies.Name = unpackVariable(byteBuffer, 1);
            queryReplies.ClassifiedFlags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            queryReplies.CreationDate = unpackInt(byteBuffer);
            queryReplies.ExpirationDate = unpackInt(byteBuffer);
            queryReplies.PriceForListing = unpackInt(byteBuffer);
            this.QueryReplies_Fields.add(queryReplies);
        }
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < b2; i2++) {
            StatusData statusData = new StatusData();
            statusData.Status = unpackInt(byteBuffer);
            this.StatusData_Fields.add(statusData);
        }
    }
}
