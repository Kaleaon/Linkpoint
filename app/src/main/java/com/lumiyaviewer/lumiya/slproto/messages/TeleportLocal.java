package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

public class TeleportLocal extends SLMessage {
    public Info Info_Field = new Info();

    public static class Info {
        public UUID AgentID;
        public int LocationID;
        public LLVector3 LookAt;
        public LLVector3 Position;
        public int TeleportFlags;
    }

    public TeleportLocal() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 52;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTeleportLocal(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 64);
        packUUID(byteBuffer, this.Info_Field.AgentID);
        packInt(byteBuffer, this.Info_Field.LocationID);
        packLLVector3(byteBuffer, this.Info_Field.Position);
        packLLVector3(byteBuffer, this.Info_Field.LookAt);
        packInt(byteBuffer, this.Info_Field.TeleportFlags);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Info_Field.AgentID = unpackUUID(byteBuffer);
        this.Info_Field.LocationID = unpackInt(byteBuffer);
        this.Info_Field.Position = unpackLLVector3(byteBuffer);
        this.Info_Field.LookAt = unpackLLVector3(byteBuffer);
        this.Info_Field.TeleportFlags = unpackInt(byteBuffer);
    }
}
