package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class AgentMovementComplete extends SLMessage {
    public AgentData AgentData_Field;
    public Data Data_Field;
    public SimData SimData_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class Data {
        public LLVector3 LookAt;
        public LLVector3 Position;
        public long RegionHandle;
        public int Timestamp;
    }

    /* loaded from: classes.dex */
    public static class SimData {
        public byte[] ChannelVersion;
    }

    public AgentMovementComplete() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.Data_Field = new Data();
        this.SimData_Field = new SimData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.SimData_Field.ChannelVersion.length + 2 + 72;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentMovementComplete(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -6);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packLLVector3(byteBuffer, this.Data_Field.Position);
        packLLVector3(byteBuffer, this.Data_Field.LookAt);
        packLong(byteBuffer, this.Data_Field.RegionHandle);
        packInt(byteBuffer, this.Data_Field.Timestamp);
        packVariable(byteBuffer, this.SimData_Field.ChannelVersion, 2);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.Data_Field.Position = unpackLLVector3(byteBuffer);
        this.Data_Field.LookAt = unpackLLVector3(byteBuffer);
        this.Data_Field.RegionHandle = unpackLong(byteBuffer);
        this.Data_Field.Timestamp = unpackInt(byteBuffer);
        this.SimData_Field.ChannelVersion = unpackVariable(byteBuffer, 2);
    }
}
