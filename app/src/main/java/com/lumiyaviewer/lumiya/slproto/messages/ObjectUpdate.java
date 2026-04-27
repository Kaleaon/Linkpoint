package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class ObjectUpdate extends SLMessage {
    public ArrayList<ObjectData> ObjectData_Fields = new ArrayList<>();
    public RegionData RegionData_Field;

    public static class ObjectData {
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

    public static class RegionData {
        public long RegionHandle;
        public int TimeDilation;
    }

    public ObjectUpdate() {
        this.zeroCoded = true;
        this.RegionData_Field = new RegionData();
    }

    public int CalcPayloadSize() {
        int i = 12;
        Iterator<T> it = this.ObjectData_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            ObjectData objectData = (ObjectData) it.next();
            i = objectData.ExtraParams.length + objectData.ObjectData.length + 41 + 4 + 4 + 1 + 1 + 2 + 2 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 2 + 2 + 2 + 2 + objectData.TextureEntry.length + 1 + objectData.TextureAnim.length + 2 + objectData.NameValue.length + 2 + objectData.Data.length + 1 + objectData.Text.length + 4 + 1 + objectData.MediaURL.length + 1 + objectData.PSBlock.length + 1 + 16 + 16 + 4 + 1 + 4 + 1 + 12 + 12 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectUpdate(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.FF);
        packLong(byteBuffer, this.RegionData_Field.RegionHandle);
        packShort(byteBuffer, (short) this.RegionData_Field.TimeDilation);
        byteBuffer.put((byte) this.ObjectData_Fields.size());
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ID);
            packByte(byteBuffer, (byte) objectData.State);
            packUUID(byteBuffer, objectData.FullID);
            packInt(byteBuffer, objectData.CRC);
            packByte(byteBuffer, (byte) objectData.PCode);
            packByte(byteBuffer, (byte) objectData.Material);
            packByte(byteBuffer, (byte) objectData.ClickAction);
            packLLVector3(byteBuffer, objectData.Scale);
            packVariable(byteBuffer, objectData.ObjectData, 1);
            packInt(byteBuffer, objectData.ParentID);
            packInt(byteBuffer, objectData.UpdateFlags);
            packByte(byteBuffer, (byte) objectData.PathCurve);
            packByte(byteBuffer, (byte) objectData.ProfileCurve);
            packShort(byteBuffer, (short) objectData.PathBegin);
            packShort(byteBuffer, (short) objectData.PathEnd);
            packByte(byteBuffer, (byte) objectData.PathScaleX);
            packByte(byteBuffer, (byte) objectData.PathScaleY);
            packByte(byteBuffer, (byte) objectData.PathShearX);
            packByte(byteBuffer, (byte) objectData.PathShearY);
            packByte(byteBuffer, (byte) objectData.PathTwist);
            packByte(byteBuffer, (byte) objectData.PathTwistBegin);
            packByte(byteBuffer, (byte) objectData.PathRadiusOffset);
            packByte(byteBuffer, (byte) objectData.PathTaperX);
            packByte(byteBuffer, (byte) objectData.PathTaperY);
            packByte(byteBuffer, (byte) objectData.PathRevolutions);
            packByte(byteBuffer, (byte) objectData.PathSkew);
            packShort(byteBuffer, (short) objectData.ProfileBegin);
            packShort(byteBuffer, (short) objectData.ProfileEnd);
            packShort(byteBuffer, (short) objectData.ProfileHollow);
            packVariable(byteBuffer, objectData.TextureEntry, 2);
            packVariable(byteBuffer, objectData.TextureAnim, 1);
            packVariable(byteBuffer, objectData.NameValue, 2);
            packVariable(byteBuffer, objectData.Data, 2);
            packVariable(byteBuffer, objectData.Text, 1);
            packFixed(byteBuffer, objectData.TextColor, 4);
            packVariable(byteBuffer, objectData.MediaURL, 1);
            packVariable(byteBuffer, objectData.PSBlock, 1);
            packVariable(byteBuffer, objectData.ExtraParams, 1);
            packUUID(byteBuffer, objectData.Sound);
            packUUID(byteBuffer, objectData.OwnerID);
            packFloat(byteBuffer, objectData.Gain);
            packByte(byteBuffer, (byte) objectData.Flags);
            packFloat(byteBuffer, objectData.Radius);
            packByte(byteBuffer, (byte) objectData.JointType);
            packLLVector3(byteBuffer, objectData.JointPivot);
            packLLVector3(byteBuffer, objectData.JointAxisOrAnchor);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.RegionData_Field.RegionHandle = unpackLong(byteBuffer);
        this.RegionData_Field.TimeDilation = unpackShort(byteBuffer) & 65535;
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            ObjectData objectData = new ObjectData();
            objectData.ID = unpackInt(byteBuffer);
            objectData.State = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.FullID = unpackUUID(byteBuffer);
            objectData.CRC = unpackInt(byteBuffer);
            objectData.PCode = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.Material = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.ClickAction = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.Scale = unpackLLVector3(byteBuffer);
            objectData.ObjectData = unpackVariable(byteBuffer, 1);
            objectData.ParentID = unpackInt(byteBuffer);
            objectData.UpdateFlags = unpackInt(byteBuffer);
            objectData.PathCurve = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.ProfileCurve = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.PathBegin = unpackShort(byteBuffer) & 65535;
            objectData.PathEnd = unpackShort(byteBuffer) & 65535;
            objectData.PathScaleX = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.PathScaleY = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.PathShearX = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.PathShearY = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.PathTwist = unpackByte(byteBuffer);
            objectData.PathTwistBegin = unpackByte(byteBuffer);
            objectData.PathRadiusOffset = unpackByte(byteBuffer);
            objectData.PathTaperX = unpackByte(byteBuffer);
            objectData.PathTaperY = unpackByte(byteBuffer);
            objectData.PathRevolutions = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.PathSkew = unpackByte(byteBuffer);
            objectData.ProfileBegin = unpackShort(byteBuffer) & 65535;
            objectData.ProfileEnd = unpackShort(byteBuffer) & 65535;
            objectData.ProfileHollow = unpackShort(byteBuffer) & 65535;
            objectData.TextureEntry = unpackVariable(byteBuffer, 2);
            objectData.TextureAnim = unpackVariable(byteBuffer, 1);
            objectData.NameValue = unpackVariable(byteBuffer, 2);
            objectData.Data = unpackVariable(byteBuffer, 2);
            objectData.Text = unpackVariable(byteBuffer, 1);
            objectData.TextColor = unpackFixed(byteBuffer, 4);
            objectData.MediaURL = unpackVariable(byteBuffer, 1);
            objectData.PSBlock = unpackVariable(byteBuffer, 1);
            objectData.ExtraParams = unpackVariable(byteBuffer, 1);
            objectData.Sound = unpackUUID(byteBuffer);
            objectData.OwnerID = unpackUUID(byteBuffer);
            objectData.Gain = unpackFloat(byteBuffer);
            objectData.Flags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.Radius = unpackFloat(byteBuffer);
            objectData.JointType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.JointPivot = unpackLLVector3(byteBuffer);
            objectData.JointAxisOrAnchor = unpackLLVector3(byteBuffer);
            this.ObjectData_Fields.add(objectData);
        }
    }
}
