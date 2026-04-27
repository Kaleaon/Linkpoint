// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ObjectUpdate extends SLMessage
{
    public static class ObjectData
    {

        public int CRC;
        public int ClickAction;
        public byte Data[];
        public byte ExtraParams[];
        public int Flags;
        public UUID FullID;
        public float Gain;
        public int ID;
        public LLVector3 JointAxisOrAnchor;
        public LLVector3 JointPivot;
        public int JointType;
        public int Material;
        public byte MediaURL[];
        public byte NameValue[];
        public byte ObjectData[];
        public UUID OwnerID;
        public int PCode;
        public byte PSBlock[];
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
        public byte Text[];
        public byte TextColor[];
        public byte TextureAnim[];
        public byte TextureEntry[];
        public int UpdateFlags;

        public ObjectData()
        {
        }
    }

    public static class RegionData
    {

        public long RegionHandle;
        public int TimeDilation;

        public RegionData()
        {
        }
    }


    public ArrayList ObjectData_Fields;
    public RegionData RegionData_Field;

    public ObjectUpdate()
    {
        ObjectData_Fields = new ArrayList();
        zeroCoded = true;
        RegionData_Field = new RegionData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = ObjectData_Fields.iterator();
        ObjectData objectdata;
        int i;
        int j;
        int k;
        int l;
        int i1;
        int j1;
        int k1;
        int l1;
        int i2;
        for (i = 12; iterator.hasNext(); i = objectdata.ExtraParams.length + (j + 41 + 4 + 4 + 1 + 1 + 2 + 2 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 2 + 2 + 2 + 2 + k + 1 + l + 2 + i1 + 2 + j1 + 1 + k1 + 4 + 1 + l1 + 1 + i2 + 1) + 16 + 16 + 4 + 1 + 4 + 1 + 12 + 12 + i)
        {
            objectdata = (ObjectData)iterator.next();
            j = objectdata.ObjectData.length;
            k = objectdata.TextureEntry.length;
            l = objectdata.TextureAnim.length;
            i1 = objectdata.NameValue.length;
            j1 = objectdata.Data.length;
            k1 = objectdata.Text.length;
            l1 = objectdata.MediaURL.length;
            i2 = objectdata.PSBlock.length;
        }

        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleObjectUpdate(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)12);
        packLong(bytebuffer, RegionData_Field.RegionHandle);
        packShort(bytebuffer, (short)RegionData_Field.TimeDilation);
        bytebuffer.put((byte)ObjectData_Fields.size());
        ObjectData objectdata;
        for (Iterator iterator = ObjectData_Fields.iterator(); iterator.hasNext(); packLLVector3(bytebuffer, objectdata.JointAxisOrAnchor))
        {
            objectdata = (ObjectData)iterator.next();
            packInt(bytebuffer, objectdata.ID);
            packByte(bytebuffer, (byte)objectdata.State);
            packUUID(bytebuffer, objectdata.FullID);
            packInt(bytebuffer, objectdata.CRC);
            packByte(bytebuffer, (byte)objectdata.PCode);
            packByte(bytebuffer, (byte)objectdata.Material);
            packByte(bytebuffer, (byte)objectdata.ClickAction);
            packLLVector3(bytebuffer, objectdata.Scale);
            packVariable(bytebuffer, objectdata.ObjectData, 1);
            packInt(bytebuffer, objectdata.ParentID);
            packInt(bytebuffer, objectdata.UpdateFlags);
            packByte(bytebuffer, (byte)objectdata.PathCurve);
            packByte(bytebuffer, (byte)objectdata.ProfileCurve);
            packShort(bytebuffer, (short)objectdata.PathBegin);
            packShort(bytebuffer, (short)objectdata.PathEnd);
            packByte(bytebuffer, (byte)objectdata.PathScaleX);
            packByte(bytebuffer, (byte)objectdata.PathScaleY);
            packByte(bytebuffer, (byte)objectdata.PathShearX);
            packByte(bytebuffer, (byte)objectdata.PathShearY);
            packByte(bytebuffer, (byte)objectdata.PathTwist);
            packByte(bytebuffer, (byte)objectdata.PathTwistBegin);
            packByte(bytebuffer, (byte)objectdata.PathRadiusOffset);
            packByte(bytebuffer, (byte)objectdata.PathTaperX);
            packByte(bytebuffer, (byte)objectdata.PathTaperY);
            packByte(bytebuffer, (byte)objectdata.PathRevolutions);
            packByte(bytebuffer, (byte)objectdata.PathSkew);
            packShort(bytebuffer, (short)objectdata.ProfileBegin);
            packShort(bytebuffer, (short)objectdata.ProfileEnd);
            packShort(bytebuffer, (short)objectdata.ProfileHollow);
            packVariable(bytebuffer, objectdata.TextureEntry, 2);
            packVariable(bytebuffer, objectdata.TextureAnim, 1);
            packVariable(bytebuffer, objectdata.NameValue, 2);
            packVariable(bytebuffer, objectdata.Data, 2);
            packVariable(bytebuffer, objectdata.Text, 1);
            packFixed(bytebuffer, objectdata.TextColor, 4);
            packVariable(bytebuffer, objectdata.MediaURL, 1);
            packVariable(bytebuffer, objectdata.PSBlock, 1);
            packVariable(bytebuffer, objectdata.ExtraParams, 1);
            packUUID(bytebuffer, objectdata.Sound);
            packUUID(bytebuffer, objectdata.OwnerID);
            packFloat(bytebuffer, objectdata.Gain);
            packByte(bytebuffer, (byte)objectdata.Flags);
            packFloat(bytebuffer, objectdata.Radius);
            packByte(bytebuffer, (byte)objectdata.JointType);
            packLLVector3(bytebuffer, objectdata.JointPivot);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        RegionData_Field.RegionHandle = unpackLong(bytebuffer);
        RegionData_Field.TimeDilation = unpackShort(bytebuffer) & 0xffff;
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            ObjectData objectdata = new ObjectData();
            objectdata.ID = unpackInt(bytebuffer);
            objectdata.State = unpackByte(bytebuffer) & 0xff;
            objectdata.FullID = unpackUUID(bytebuffer);
            objectdata.CRC = unpackInt(bytebuffer);
            objectdata.PCode = unpackByte(bytebuffer) & 0xff;
            objectdata.Material = unpackByte(bytebuffer) & 0xff;
            objectdata.ClickAction = unpackByte(bytebuffer) & 0xff;
            objectdata.Scale = unpackLLVector3(bytebuffer);
            objectdata.ObjectData = unpackVariable(bytebuffer, 1);
            objectdata.ParentID = unpackInt(bytebuffer);
            objectdata.UpdateFlags = unpackInt(bytebuffer);
            objectdata.PathCurve = unpackByte(bytebuffer) & 0xff;
            objectdata.ProfileCurve = unpackByte(bytebuffer) & 0xff;
            objectdata.PathBegin = unpackShort(bytebuffer) & 0xffff;
            objectdata.PathEnd = unpackShort(bytebuffer) & 0xffff;
            objectdata.PathScaleX = unpackByte(bytebuffer) & 0xff;
            objectdata.PathScaleY = unpackByte(bytebuffer) & 0xff;
            objectdata.PathShearX = unpackByte(bytebuffer) & 0xff;
            objectdata.PathShearY = unpackByte(bytebuffer) & 0xff;
            objectdata.PathTwist = unpackByte(bytebuffer);
            objectdata.PathTwistBegin = unpackByte(bytebuffer);
            objectdata.PathRadiusOffset = unpackByte(bytebuffer);
            objectdata.PathTaperX = unpackByte(bytebuffer);
            objectdata.PathTaperY = unpackByte(bytebuffer);
            objectdata.PathRevolutions = unpackByte(bytebuffer) & 0xff;
            objectdata.PathSkew = unpackByte(bytebuffer);
            objectdata.ProfileBegin = unpackShort(bytebuffer) & 0xffff;
            objectdata.ProfileEnd = unpackShort(bytebuffer) & 0xffff;
            objectdata.ProfileHollow = unpackShort(bytebuffer) & 0xffff;
            objectdata.TextureEntry = unpackVariable(bytebuffer, 2);
            objectdata.TextureAnim = unpackVariable(bytebuffer, 1);
            objectdata.NameValue = unpackVariable(bytebuffer, 2);
            objectdata.Data = unpackVariable(bytebuffer, 2);
            objectdata.Text = unpackVariable(bytebuffer, 1);
            objectdata.TextColor = unpackFixed(bytebuffer, 4);
            objectdata.MediaURL = unpackVariable(bytebuffer, 1);
            objectdata.PSBlock = unpackVariable(bytebuffer, 1);
            objectdata.ExtraParams = unpackVariable(bytebuffer, 1);
            objectdata.Sound = unpackUUID(bytebuffer);
            objectdata.OwnerID = unpackUUID(bytebuffer);
            objectdata.Gain = unpackFloat(bytebuffer);
            objectdata.Flags = unpackByte(bytebuffer) & 0xff;
            objectdata.Radius = unpackFloat(bytebuffer);
            objectdata.JointType = unpackByte(bytebuffer) & 0xff;
            objectdata.JointPivot = unpackLLVector3(bytebuffer);
            objectdata.JointAxisOrAnchor = unpackLLVector3(bytebuffer);
            ObjectData_Fields.add(objectdata);
        }

    }
}
