package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class TeleportProgress extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public Info Info_Field = new Info();

    public static class AgentData {
        public UUID AgentID;
    }

    public static class Info {
        public byte[] Message;
        public int TeleportFlags;
    }

    public TeleportProgress() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.Info_Field.Message.length + 5 + 20;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTeleportProgress(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 66);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packInt(byteBuffer, this.Info_Field.TeleportFlags);
        packVariable(byteBuffer, this.Info_Field.Message, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.Info_Field.TeleportFlags = unpackInt(byteBuffer);
        this.Info_Field.Message = unpackVariable(byteBuffer, 1);
    }
}
