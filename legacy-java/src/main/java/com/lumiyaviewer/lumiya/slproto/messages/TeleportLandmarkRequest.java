package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class TeleportLandmarkRequest extends SLMessage {
    public Info Info_Field;

    /* loaded from: classes.dex */
    public static class Info {
        public UUID AgentID;
        public UUID LandmarkID;
        public UUID SessionID;
    }

    public TeleportLandmarkRequest() {
        this.zeroCoded = true;
        this.Info_Field = new Info();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 52;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTeleportLandmarkRequest(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 65);
        packUUID(byteBuffer, this.Info_Field.AgentID);
        packUUID(byteBuffer, this.Info_Field.SessionID);
        packUUID(byteBuffer, this.Info_Field.LandmarkID);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Info_Field.AgentID = unpackUUID(byteBuffer);
        this.Info_Field.SessionID = unpackUUID(byteBuffer);
        this.Info_Field.LandmarkID = unpackUUID(byteBuffer);
    }
}
