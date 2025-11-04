package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
public class DirEventsReply extends SLMessage {
    public AgentData AgentData_Field;
    public QueryData QueryData_Field;
    public ArrayList<QueryReplies> QueryReplies_Fields = new ArrayList<>();
    public ArrayList<StatusData> StatusData_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
    }

    /* loaded from: classes.dex */
    public static class QueryData {
        public UUID QueryID;
    }

    /* loaded from: classes.dex */
    public static class QueryReplies {
        public byte[] Date;
        public int EventFlags;
        public int EventID;
        public byte[] Name;
        public UUID OwnerID;
        public int UnixTime;
    }

    /* loaded from: classes.dex */
    public static class StatusData {
        public int Status;
    }

    public DirEventsReply() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.QueryData_Field = new QueryData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        int i = 37;
        Iterator<T> it = this.QueryReplies_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2 + 1 + (this.StatusData_Fields.size() * 4);
            }
            QueryReplies queryReplies = (QueryReplies) it.next();
            i = queryReplies.Date.length + queryReplies.Name.length + 17 + 4 + 1 + 4 + 4 + i2;
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDirEventsReply(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 37);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.QueryData_Field.QueryID);
        byteBuffer.put((byte) this.QueryReplies_Fields.size());
        for (QueryReplies queryReplies : this.QueryReplies_Fields) {
            packUUID(byteBuffer, queryReplies.OwnerID);
            packVariable(byteBuffer, queryReplies.Name, 1);
            packInt(byteBuffer, queryReplies.EventID);
            packVariable(byteBuffer, queryReplies.Date, 1);
            packInt(byteBuffer, queryReplies.UnixTime);
            packInt(byteBuffer, queryReplies.EventFlags);
        }
        byteBuffer.put((byte) this.StatusData_Fields.size());
        Iterator<T> it = this.StatusData_Fields.iterator();
        while (it.hasNext()) {
            packInt(byteBuffer, ((StatusData) it.next()).Status);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            QueryReplies queryReplies = new QueryReplies();
            queryReplies.OwnerID = unpackUUID(byteBuffer);
            queryReplies.Name = unpackVariable(byteBuffer, 1);
            queryReplies.EventID = unpackInt(byteBuffer);
            queryReplies.Date = unpackVariable(byteBuffer, 1);
            queryReplies.UnixTime = unpackInt(byteBuffer);
            queryReplies.EventFlags = unpackInt(byteBuffer);
            this.QueryReplies_Fields.add(queryReplies);
        }
        int i3 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i4 = 0; i4 < i3; i4++) {
            StatusData statusData = new StatusData();
            statusData.Status = unpackInt(byteBuffer);
            this.StatusData_Fields.add(statusData);
        }
    }
}
