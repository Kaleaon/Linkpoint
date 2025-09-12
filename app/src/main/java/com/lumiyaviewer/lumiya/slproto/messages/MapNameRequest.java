package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class MapNameRequest extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public NameData NameData_Field = new NameData();

    public static class AgentData {
        public UUID AgentID;
        public int EstateID;
        public int Flags;
        public boolean Godlike;
        public UUID SessionID;
    }

    public static class NameData {
        public byte[] Name;
    }

    public MapNameRequest() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.NameData_Field.Name.length + 1 + 45;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMapNameRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -104);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.AgentData_Field.Flags);
        packInt(byteBuffer, this.AgentData_Field.EstateID);
        packBoolean(byteBuffer, this.AgentData_Field.Godlike);
        packVariable(byteBuffer, this.NameData_Field.Name, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.Flags = unpackInt(byteBuffer);
        this.AgentData_Field.EstateID = unpackInt(byteBuffer);
        this.AgentData_Field.Godlike = unpackBoolean(byteBuffer);
        this.NameData_Field.Name = unpackVariable(byteBuffer, 1);
    }
}
