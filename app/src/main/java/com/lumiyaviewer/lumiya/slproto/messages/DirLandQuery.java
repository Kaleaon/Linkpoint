package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class DirLandQuery extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public QueryData QueryData_Field = new QueryData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class QueryData {
        public int Area;
        public int Price;
        public int QueryFlags;
        public UUID QueryID;
        public int QueryStart;
        public int SearchType;
    }

    public DirLandQuery() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 72;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDirLandQuery(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 48);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.QueryData_Field.QueryID);
        packInt(byteBuffer, this.QueryData_Field.QueryFlags);
        packInt(byteBuffer, this.QueryData_Field.SearchType);
        packInt(byteBuffer, this.QueryData_Field.Price);
        packInt(byteBuffer, this.QueryData_Field.Area);
        packInt(byteBuffer, this.QueryData_Field.QueryStart);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer);
        this.QueryData_Field.QueryFlags = unpackInt(byteBuffer);
        this.QueryData_Field.SearchType = unpackInt(byteBuffer);
        this.QueryData_Field.Price = unpackInt(byteBuffer);
        this.QueryData_Field.Area = unpackInt(byteBuffer);
        this.QueryData_Field.QueryStart = unpackInt(byteBuffer);
    }
}
