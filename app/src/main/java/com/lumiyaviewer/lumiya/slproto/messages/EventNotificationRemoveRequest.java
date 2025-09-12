package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class EventNotificationRemoveRequest extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public EventData EventData_Field = new EventData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class EventData {
        public int EventID;
    }

    public EventNotificationRemoveRequest() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 40;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEventNotificationRemoveRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -74);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.EventData_Field.EventID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.EventData_Field.EventID = unpackInt(byteBuffer);
    }
}
