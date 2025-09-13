package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

public class TeleportRequest extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public Info Info_Field = new Info();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class Info {
        public LLVector3 LookAt;
        public LLVector3 Position;
        public UUID RegionID;
    }

    public TeleportRequest() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 76;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTeleportRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 62);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.Info_Field.RegionID);
        packLLVector3(byteBuffer, this.Info_Field.Position);
        packLLVector3(byteBuffer, this.Info_Field.LookAt);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.Info_Field.RegionID = unpackUUID(byteBuffer);
        this.Info_Field.Position = unpackLLVector3(byteBuffer);
        this.Info_Field.LookAt = unpackLLVector3(byteBuffer);
    }
}
