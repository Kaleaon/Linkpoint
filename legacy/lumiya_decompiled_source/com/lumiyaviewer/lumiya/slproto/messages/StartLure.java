package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;
/* loaded from: classes.dex */
public class StartLure extends SLMessage {
    public AgentData AgentData_Field;
    public Info Info_Field;
    public ArrayList<TargetData> TargetData_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class Info {
        public int LureType;
        public byte[] Message;
    }

    /* loaded from: classes.dex */
    public static class TargetData {
        public UUID TargetID;
    }

    public StartLure() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.Info_Field = new Info();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.Info_Field.Message.length + 2 + 36 + 1 + (this.TargetData_Fields.size() * 16);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleStartLure(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 70);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packByte(byteBuffer, (byte) this.Info_Field.LureType);
        packVariable(byteBuffer, this.Info_Field.Message, 1);
        byteBuffer.put((byte) this.TargetData_Fields.size());
        for (TargetData targetData : this.TargetData_Fields) {
            packUUID(byteBuffer, targetData.TargetID);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.Info_Field.LureType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.Info_Field.Message = unpackVariable(byteBuffer, 1);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            TargetData targetData = new TargetData();
            targetData.TargetID = unpackUUID(byteBuffer);
            this.TargetData_Fields.add(targetData);
        }
    }
}
