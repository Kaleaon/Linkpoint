package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class LiveHelpGroupRequest extends SLMessage {
    public RequestData RequestData_Field;

    /* loaded from: classes.dex */
    public static class RequestData {
        public UUID AgentID;
        public UUID RequestID;
    }

    public LiveHelpGroupRequest() {
        this.zeroCoded = false;
        this.RequestData_Field = new RequestData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 36;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLiveHelpGroupRequest(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 123);
        packUUID(byteBuffer, this.RequestData_Field.RequestID);
        packUUID(byteBuffer, this.RequestData_Field.AgentID);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.RequestData_Field.RequestID = unpackUUID(byteBuffer);
        this.RequestData_Field.AgentID = unpackUUID(byteBuffer);
    }
}
