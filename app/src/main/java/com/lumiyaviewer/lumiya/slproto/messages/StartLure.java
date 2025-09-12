package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class StartLure extends SLMessage {
    public AgentData AgentData_Field;
    public Info Info_Field;
    public ArrayList<TargetData> TargetData_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class Info {
        public int LureType;
        public byte[] Message;
    }

    public static class TargetData {
        public UUID TargetID;
    }

    public StartLure() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.Info_Field = new Info();
    }

    public int CalcPayloadSize() {
        return this.Info_Field.Message.length + 2 + 36 + 1 + (this.TargetData_Fields.size() * 16);
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleStartLure(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
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

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.Info_Field.LureType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.Info_Field.Message = unpackVariable(byteBuffer, 1);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            TargetData targetData = new TargetData();
            targetData.TargetID = unpackUUID(byteBuffer);
            this.TargetData_Fields.add(targetData);
        }
    }
}
