package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class MapBlockRequest extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public PositionData PositionData_Field = new PositionData();

    public static class AgentData {
        public UUID AgentID;
        public int EstateID;
        public int Flags;
        public boolean Godlike;
        public UUID SessionID;
    }

    public static class PositionData {
        public int MaxX;
        public int MaxY;
        public int MinX;
        public int MinY;
    }

    public MapBlockRequest() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 53;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMapBlockRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -105);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.AgentData_Field.Flags);
        packInt(byteBuffer, this.AgentData_Field.EstateID);
        packBoolean(byteBuffer, this.AgentData_Field.Godlike);
        packShort(byteBuffer, (short) this.PositionData_Field.MinX);
        packShort(byteBuffer, (short) this.PositionData_Field.MaxX);
        packShort(byteBuffer, (short) this.PositionData_Field.MinY);
        packShort(byteBuffer, (short) this.PositionData_Field.MaxY);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.Flags = unpackInt(byteBuffer);
        this.AgentData_Field.EstateID = unpackInt(byteBuffer);
        this.AgentData_Field.Godlike = unpackBoolean(byteBuffer);
        this.PositionData_Field.MinX = unpackShort(byteBuffer) & 65535;
        this.PositionData_Field.MaxX = unpackShort(byteBuffer) & 65535;
        this.PositionData_Field.MinY = unpackShort(byteBuffer) & 65535;
        this.PositionData_Field.MaxY = unpackShort(byteBuffer) & 65535;
    }
}
