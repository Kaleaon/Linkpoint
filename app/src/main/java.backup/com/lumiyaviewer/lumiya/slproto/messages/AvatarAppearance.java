package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class AvatarAppearance extends SLMessage {
    public ArrayList<AppearanceData> AppearanceData_Fields = new ArrayList<>();
    public ObjectData ObjectData_Field;
    public Sender Sender_Field;
    public ArrayList<VisualParam> VisualParam_Fields = new ArrayList<>();

    public static class AppearanceData {
        public int AppearanceVersion;
        public int CofVersion;
        public int Flags;
    }

    public static class ObjectData {
        public byte[] TextureEntry;
    }

    public static class Sender {
        public UUID ID;
        public boolean IsTrial;
    }

    public static class VisualParam {
        public int ParamValue;
    }

    public AvatarAppearance() {
        this.zeroCoded = true;
        this.Sender_Field = new Sender();
        this.ObjectData_Field = new ObjectData();
    }

    public int CalcPayloadSize() {
        return this.ObjectData_Field.TextureEntry.length + 2 + 21 + 1 + (this.VisualParam_Fields.size() * 1) + 1 + (this.AppearanceData_Fields.size() * 9);
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarAppearance(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -98);
        packUUID(byteBuffer, this.Sender_Field.ID);
        packBoolean(byteBuffer, this.Sender_Field.IsTrial);
        packVariable(byteBuffer, this.ObjectData_Field.TextureEntry, 2);
        byteBuffer.put((byte) this.VisualParam_Fields.size());
        for (VisualParam visualParam : this.VisualParam_Fields) {
            packByte(byteBuffer, (byte) visualParam.ParamValue);
        }
        byteBuffer.put((byte) this.AppearanceData_Fields.size());
        for (AppearanceData appearanceData : this.AppearanceData_Fields) {
            packByte(byteBuffer, (byte) appearanceData.AppearanceVersion);
            packInt(byteBuffer, appearanceData.CofVersion);
            packInt(byteBuffer, appearanceData.Flags);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Sender_Field.ID = unpackUUID(byteBuffer);
        this.Sender_Field.IsTrial = unpackBoolean(byteBuffer);
        this.ObjectData_Field.TextureEntry = unpackVariable(byteBuffer, 2);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            VisualParam visualParam = new VisualParam();
            visualParam.ParamValue = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            this.VisualParam_Fields.add(visualParam);
        }
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < b2; i2++) {
            AppearanceData appearanceData = new AppearanceData();
            appearanceData.AppearanceVersion = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            appearanceData.CofVersion = unpackInt(byteBuffer);
            appearanceData.Flags = unpackInt(byteBuffer);
            this.AppearanceData_Fields.add(appearanceData);
        }
    }
}
