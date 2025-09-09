package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class GrantUserRights extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<Rights> Rights_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class Rights {
        public UUID AgentRelated;
        public int RelatedRights;
    }

    public GrantUserRights() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize() {
        return (this.Rights_Fields.size() * 20) + 37;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGrantUserRights(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 64);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        byteBuffer.put((byte) this.Rights_Fields.size());
        for (Rights rights : this.Rights_Fields) {
            packUUID(byteBuffer, rights.AgentRelated);
            packInt(byteBuffer, rights.RelatedRights);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            Rights rights = new Rights();
            rights.AgentRelated = unpackUUID(byteBuffer);
            rights.RelatedRights = unpackInt(byteBuffer);
            this.Rights_Fields.add(rights);
        }
    }
}
