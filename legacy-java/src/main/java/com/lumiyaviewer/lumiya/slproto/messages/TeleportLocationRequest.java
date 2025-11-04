package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class TeleportLocationRequest extends SLMessage {
    public AgentData AgentData_Field;
    public Info Info_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class Info {
        public LLVector3 LookAt;
        public LLVector3 Position;
        public long RegionHandle;
    }

    public TeleportLocationRequest() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.Info_Field = new Info();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 68;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTeleportLocationRequest(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 63);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packLong(byteBuffer, this.Info_Field.RegionHandle);
        packLLVector3(byteBuffer, this.Info_Field.Position);
        packLLVector3(byteBuffer, this.Info_Field.LookAt);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.Info_Field.RegionHandle = unpackLong(byteBuffer);
        this.Info_Field.Position = unpackLLVector3(byteBuffer);
        this.Info_Field.LookAt = unpackLLVector3(byteBuffer);
    }
}
