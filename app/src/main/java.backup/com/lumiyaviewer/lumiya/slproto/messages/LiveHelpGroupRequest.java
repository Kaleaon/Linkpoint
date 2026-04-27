package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class LiveHelpGroupRequest extends SLMessage {
    public RequestData RequestData_Field = new RequestData();

    public static class RequestData {
        public UUID AgentID;
        public UUID RequestID;
    }

    public LiveHelpGroupRequest() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 36;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLiveHelpGroupRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 123);
        packUUID(byteBuffer, this.RequestData_Field.RequestID);
        packUUID(byteBuffer, this.RequestData_Field.AgentID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.RequestData_Field.RequestID = unpackUUID(byteBuffer);
        this.RequestData_Field.AgentID = unpackUUID(byteBuffer);
    }
}
