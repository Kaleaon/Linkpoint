package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class DirPopularQueryBackend extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public QueryData QueryData_Field = new QueryData();

    public static class AgentData {
        public UUID AgentID;
    }

    public static class QueryData {
        public int EstateID;
        public boolean Godlike;
        public int QueryFlags;
        public UUID QueryID;
    }

    public DirPopularQueryBackend() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 45;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDirPopularQueryBackend(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 52);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.QueryData_Field.QueryID);
        packInt(byteBuffer, this.QueryData_Field.QueryFlags);
        packInt(byteBuffer, this.QueryData_Field.EstateID);
        packBoolean(byteBuffer, this.QueryData_Field.Godlike);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer);
        this.QueryData_Field.QueryFlags = unpackInt(byteBuffer);
        this.QueryData_Field.EstateID = unpackInt(byteBuffer);
        this.QueryData_Field.Godlike = unpackBoolean(byteBuffer);
    }
}
