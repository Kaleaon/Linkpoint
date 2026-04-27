package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class DirPlacesQuery extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public QueryData QueryData_Field = new QueryData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class QueryData {
        public int Category;
        public int QueryFlags;
        public UUID QueryID;
        public int QueryStart;
        public byte[] QueryText;
        public byte[] SimName;
    }

    public DirPlacesQuery() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.QueryData_Field.QueryText.length + 17 + 4 + 1 + 1 + this.QueryData_Field.SimName.length + 4 + 36;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDirPlacesQuery(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 33);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.QueryData_Field.QueryID);
        packVariable(byteBuffer, this.QueryData_Field.QueryText, 1);
        packInt(byteBuffer, this.QueryData_Field.QueryFlags);
        packByte(byteBuffer, (byte) this.QueryData_Field.Category);
        packVariable(byteBuffer, this.QueryData_Field.SimName, 1);
        packInt(byteBuffer, this.QueryData_Field.QueryStart);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer);
        this.QueryData_Field.QueryText = unpackVariable(byteBuffer, 1);
        this.QueryData_Field.QueryFlags = unpackInt(byteBuffer);
        this.QueryData_Field.Category = unpackByte(byteBuffer);
        this.QueryData_Field.SimName = unpackVariable(byteBuffer, 1);
        this.QueryData_Field.QueryStart = unpackInt(byteBuffer);
    }
}
