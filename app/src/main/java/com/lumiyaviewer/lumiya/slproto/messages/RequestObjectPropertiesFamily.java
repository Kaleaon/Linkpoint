package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class RequestObjectPropertiesFamily extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public ObjectData ObjectData_Field = new ObjectData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class ObjectData {
        public UUID ObjectID;
        public int RequestFlags;
    }

    public RequestObjectPropertiesFamily() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 54;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRequestObjectPropertiesFamily(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) -1);
        byteBuffer.put((byte) 5);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.ObjectData_Field.RequestFlags);
        packUUID(byteBuffer, this.ObjectData_Field.ObjectID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.ObjectData_Field.RequestFlags = unpackInt(byteBuffer);
        this.ObjectData_Field.ObjectID = unpackUUID(byteBuffer);
    }
}
