// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ObjectUpdate extends SLMessage
{
    public ArrayList<ObjectData> ObjectData_Fields;
    public RegionData RegionData_Field;
    
    public ObjectUpdate() {
        this.ObjectData_Fields = new ArrayList<ObjectData>();
        this.zeroCoded = true;
        this.RegionData_Field = new RegionData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.ObjectData_Fields.iterator();
        int n = 12;
        while (iterator.hasNext()) {
            final ObjectData objectData = iterator.next();
            n += objectData.ExtraParams.length + (objectData.ObjectData.length + 41 + 4 + 4 + 1 + 1 + 2 + 2 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 2 + 2 + 2 + 2 + objectData.TextureEntry.length + 1 + objectData.TextureAnim.length + 2 + objectData.NameValue.length + 2 + objectData.Data.length + 1 + objectData.Text.length + 4 + 1 + objectData.MediaURL.length + 1 + objectData.PSBlock.length + 1) + 16 + 16 + 4 + 1 + 4 + 1 + 12 + 12;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleObjectUpdate(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)12);
        this.packLong(byteBuffer, this.RegionData_Field.RegionHandle);
        this.packShort(byteBuffer, (short)this.RegionData_Field.TimeDilation);
        byteBuffer.put((byte)this.ObjectData_Fields.size());
        for (final ObjectData objectData : this.ObjectData_Fields) {
            this.packInt(byteBuffer, objectData.ID);
            this.packByte(byteBuffer, (byte)objectData.State);
            this.packUUID(byteBuffer, objectData.FullID);
            this.packInt(byteBuffer, objectData.CRC);
            this.packByte(byteBuffer, (byte)objectData.PCode);
            this.packByte(byteBuffer, (byte)objectData.Material);
            this.packByte(byteBuffer, (byte)objectData.ClickAction);
            this.packLLVector3(byteBuffer, objectData.Scale);
            this.packVariable(byteBuffer, objectData.ObjectData, 1);
            this.packInt(byteBuffer, objectData.ParentID);
            this.packInt(byteBuffer, objectData.UpdateFlags);
            this.packByte(byteBuffer, (byte)objectData.PathCurve);
            this.packByte(byteBuffer, (byte)objectData.ProfileCurve);
            this.packShort(byteBuffer, (short)objectData.PathBegin);
            this.packShort(byteBuffer, (short)objectData.PathEnd);
            this.packByte(byteBuffer, (byte)objectData.PathScaleX);
            this.packByte(byteBuffer, (byte)objectData.PathScaleY);
            this.packByte(byteBuffer, (byte)objectData.PathShearX);
            this.packByte(byteBuffer, (byte)objectData.PathShearY);
            this.packByte(byteBuffer, (byte)objectData.PathTwist);
            this.packByte(byteBuffer, (byte)objectData.PathTwistBegin);
            this.packByte(byteBuffer, (byte)objectData.PathRadiusOffset);
            this.packByte(byteBuffer, (byte)objectData.PathTaperX);
            this.packByte(byteBuffer, (byte)objectData.PathTaperY);
            this.packByte(byteBuffer, (byte)objectData.PathRevolutions);
            this.packByte(byteBuffer, (byte)objectData.PathSkew);
            this.packShort(byteBuffer, (short)objectData.ProfileBegin);
            this.packShort(byteBuffer, (short)objectData.ProfileEnd);
            this.packShort(byteBuffer, (short)objectData.ProfileHollow);
            this.packVariable(byteBuffer, objectData.TextureEntry, 2);
            this.packVariable(byteBuffer, objectData.TextureAnim, 1);
            this.packVariable(byteBuffer, objectData.NameValue, 2);
            this.packVariable(byteBuffer, objectData.Data, 2);
            this.packVariable(byteBuffer, objectData.Text, 1);
            this.packFixed(byteBuffer, objectData.TextColor, 4);
            this.packVariable(byteBuffer, objectData.MediaURL, 1);
            this.packVariable(byteBuffer, objectData.PSBlock, 1);
            this.packVariable(byteBuffer, objectData.ExtraParams, 1);
            this.packUUID(byteBuffer, objectData.Sound);
            this.packUUID(byteBuffer, objectData.OwnerID);
            this.packFloat(byteBuffer, objectData.Gain);
            this.packByte(byteBuffer, (byte)objectData.Flags);
            this.packFloat(byteBuffer, objectData.Radius);
            this.packByte(byteBuffer, (byte)objectData.JointType);
            this.packLLVector3(byteBuffer, objectData.JointPivot);
            this.packLLVector3(byteBuffer, objectData.JointAxisOrAnchor);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.RegionData_Field.RegionHandle = this.unpackLong(byteBuffer);
        this.RegionData_Field.TimeDilation = (this.unpackShort(byteBuffer) & 0xFFFF);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ObjectData e = new ObjectData();
            e.ID = this.unpackInt(byteBuffer);
            e.State = (this.unpackByte(byteBuffer) & 0xFF);
            e.FullID = this.unpackUUID(byteBuffer);
            e.CRC = this.unpackInt(byteBuffer);
            e.PCode = (this.unpackByte(byteBuffer) & 0xFF);
            e.Material = (this.unpackByte(byteBuffer) & 0xFF);
            e.ClickAction = (this.unpackByte(byteBuffer) & 0xFF);
            e.Scale = this.unpackLLVector3(byteBuffer);
            e.ObjectData = this.unpackVariable(byteBuffer, 1);
            e.ParentID = this.unpackInt(byteBuffer);
            e.UpdateFlags = this.unpackInt(byteBuffer);
            e.PathCurve = (this.unpackByte(byteBuffer) & 0xFF);
            e.ProfileCurve = (this.unpackByte(byteBuffer) & 0xFF);
            e.PathBegin = (this.unpackShort(byteBuffer) & 0xFFFF);
            e.PathEnd = (this.unpackShort(byteBuffer) & 0xFFFF);
            e.PathScaleX = (this.unpackByte(byteBuffer) & 0xFF);
            e.PathScaleY = (this.unpackByte(byteBuffer) & 0xFF);
            e.PathShearX = (this.unpackByte(byteBuffer) & 0xFF);
            e.PathShearY = (this.unpackByte(byteBuffer) & 0xFF);
            e.PathTwist = this.unpackByte(byteBuffer);
            e.PathTwistBegin = this.unpackByte(byteBuffer);
            e.PathRadiusOffset = this.unpackByte(byteBuffer);
            e.PathTaperX = this.unpackByte(byteBuffer);
            e.PathTaperY = this.unpackByte(byteBuffer);
            e.PathRevolutions = (this.unpackByte(byteBuffer) & 0xFF);
            e.PathSkew = this.unpackByte(byteBuffer);
            e.ProfileBegin = (this.unpackShort(byteBuffer) & 0xFFFF);
            e.ProfileEnd = (this.unpackShort(byteBuffer) & 0xFFFF);
            e.ProfileHollow = (this.unpackShort(byteBuffer) & 0xFFFF);
            e.TextureEntry = this.unpackVariable(byteBuffer, 2);
            e.TextureAnim = this.unpackVariable(byteBuffer, 1);
            e.NameValue = this.unpackVariable(byteBuffer, 2);
            e.Data = this.unpackVariable(byteBuffer, 2);
            e.Text = this.unpackVariable(byteBuffer, 1);
            e.TextColor = this.unpackFixed(byteBuffer, 4);
            e.MediaURL = this.unpackVariable(byteBuffer, 1);
            e.PSBlock = this.unpackVariable(byteBuffer, 1);
            e.ExtraParams = this.unpackVariable(byteBuffer, 1);
            e.Sound = this.unpackUUID(byteBuffer);
            e.OwnerID = this.unpackUUID(byteBuffer);
            e.Gain = this.unpackFloat(byteBuffer);
            e.Flags = (this.unpackByte(byteBuffer) & 0xFF);
            e.Radius = this.unpackFloat(byteBuffer);
            e.JointType = (this.unpackByte(byteBuffer) & 0xFF);
            e.JointPivot = this.unpackLLVector3(byteBuffer);
            e.JointAxisOrAnchor = this.unpackLLVector3(byteBuffer);
            this.ObjectData_Fields.add(e);
        }
    }
    
    public static class ObjectData
    {
        public int CRC;
        public int ClickAction;
        public byte[] Data;
        public byte[] ExtraParams;
        public int Flags;
        public UUID FullID;
        public float Gain;
        public int ID;
        public LLVector3 JointAxisOrAnchor;
        public LLVector3 JointPivot;
        public int JointType;
        public int Material;
        public byte[] MediaURL;
        public byte[] NameValue;
        public byte[] ObjectData;
        public UUID OwnerID;
        public int PCode;
        public byte[] PSBlock;
        public int ParentID;
        public int PathBegin;
        public int PathCurve;
        public int PathEnd;
        public int PathRadiusOffset;
        public int PathRevolutions;
        public int PathScaleX;
        public int PathScaleY;
        public int PathShearX;
        public int PathShearY;
        public int PathSkew;
        public int PathTaperX;
        public int PathTaperY;
        public int PathTwist;
        public int PathTwistBegin;
        public int ProfileBegin;
        public int ProfileCurve;
        public int ProfileEnd;
        public int ProfileHollow;
        public float Radius;
        public LLVector3 Scale;
        public UUID Sound;
        public int State;
        public byte[] Text;
        public byte[] TextColor;
        public byte[] TextureAnim;
        public byte[] TextureEntry;
        public int UpdateFlags;
    }
    
    public static class RegionData
    {
        public long RegionHandle;
        public int TimeDilation;
    }
}
