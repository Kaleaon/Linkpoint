// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AvatarAppearance extends SLMessage
{
    public ArrayList<AppearanceData> AppearanceData_Fields;
    public ObjectData ObjectData_Field;
    public Sender Sender_Field;
    public ArrayList<VisualParam> VisualParam_Fields;
    
    public AvatarAppearance() {
        this.VisualParam_Fields = new ArrayList<VisualParam>();
        this.AppearanceData_Fields = new ArrayList<AppearanceData>();
        this.zeroCoded = true;
        this.Sender_Field = new Sender();
        this.ObjectData_Field = new ObjectData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ObjectData_Field.TextureEntry.length + 2 + 21 + 1 + this.VisualParam_Fields.size() * 1 + 1 + this.AppearanceData_Fields.size() * 9;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAvatarAppearance(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-98));
        this.packUUID(byteBuffer, this.Sender_Field.ID);
        this.packBoolean(byteBuffer, this.Sender_Field.IsTrial);
        this.packVariable(byteBuffer, this.ObjectData_Field.TextureEntry, 2);
        byteBuffer.put((byte)this.VisualParam_Fields.size());
        final Iterator<Object> iterator = this.VisualParam_Fields.iterator();
        while (iterator.hasNext()) {
            this.packByte(byteBuffer, (byte)iterator.next().ParamValue);
        }
        byteBuffer.put((byte)this.AppearanceData_Fields.size());
        for (final AppearanceData appearanceData : this.AppearanceData_Fields) {
            this.packByte(byteBuffer, (byte)appearanceData.AppearanceVersion);
            this.packInt(byteBuffer, appearanceData.CofVersion);
            this.packInt(byteBuffer, appearanceData.Flags);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final int n = 0;
        this.Sender_Field.ID = this.unpackUUID(byteBuffer);
        this.Sender_Field.IsTrial = this.unpackBoolean(byteBuffer);
        this.ObjectData_Field.TextureEntry = this.unpackVariable(byteBuffer, 2);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final VisualParam e = new VisualParam();
            e.ParamValue = (this.unpackByte(byteBuffer) & 0xFF);
            this.VisualParam_Fields.add(e);
        }
        final byte value2 = byteBuffer.get();
        for (int j = n; j < (value2 & 0xFF); ++j) {
            final AppearanceData e2 = new AppearanceData();
            e2.AppearanceVersion = (this.unpackByte(byteBuffer) & 0xFF);
            e2.CofVersion = this.unpackInt(byteBuffer);
            e2.Flags = this.unpackInt(byteBuffer);
            this.AppearanceData_Fields.add(e2);
        }
    }
    
    public static class AppearanceData
    {
        public int AppearanceVersion;
        public int CofVersion;
        public int Flags;
    }
    
    public static class ObjectData
    {
        public byte[] TextureEntry;
    }
    
    public static class Sender
    {
        public UUID ID;
        public boolean IsTrial;
    }
    
    public static class VisualParam
    {
        public int ParamValue;
    }
}
