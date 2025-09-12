package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class AgentSetAppearance extends SLMessage {
    public AgentData AgentData_Field;
    public ObjectData ObjectData_Field;
    public ArrayList<VisualParam> VisualParam_Fields = new ArrayList<>();
    public ArrayList<WearableData> WearableData_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public int SerialNum;
        public UUID SessionID;
        public LLVector3 Size;
    }

    public static class ObjectData {
        public byte[] TextureEntry;
    }

    public static class VisualParam {
        public int ParamValue;
    }

    public static class WearableData {
        public UUID CacheID;
        public int TextureIndex;
    }

    public AgentSetAppearance() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.ObjectData_Field = new ObjectData();
    }

    public int CalcPayloadSize() {
        return (this.WearableData_Fields.size() * 17) + 53 + this.ObjectData_Field.TextureEntry.length + 2 + 1 + (this.VisualParam_Fields.size() * 1);
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentSetAppearance(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 84);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.AgentData_Field.SerialNum);
        packLLVector3(byteBuffer, this.AgentData_Field.Size);
        byteBuffer.put((byte) this.WearableData_Fields.size());
        for (WearableData wearableData : this.WearableData_Fields) {
            packUUID(byteBuffer, wearableData.CacheID);
            packByte(byteBuffer, (byte) wearableData.TextureIndex);
        }
        packVariable(byteBuffer, this.ObjectData_Field.TextureEntry, 2);
        byteBuffer.put((byte) this.VisualParam_Fields.size());
        for (VisualParam visualParam : this.VisualParam_Fields) {
            packByte(byteBuffer, (byte) visualParam.ParamValue);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.SerialNum = unpackInt(byteBuffer);
        this.AgentData_Field.Size = unpackLLVector3(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            WearableData wearableData = new WearableData();
            wearableData.CacheID = unpackUUID(byteBuffer);
            wearableData.TextureIndex = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            this.WearableData_Fields.add(wearableData);
        }
        this.ObjectData_Field.TextureEntry = unpackVariable(byteBuffer, 2);
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < b2; i2++) {
            VisualParam visualParam = new VisualParam();
            visualParam.ParamValue = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            this.VisualParam_Fields.add(visualParam);
        }
    }
}
