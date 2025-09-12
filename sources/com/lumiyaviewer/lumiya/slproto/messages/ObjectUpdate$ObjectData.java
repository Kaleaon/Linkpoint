// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            ObjectUpdate

public static class 
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

    public ()
    {
    }
}
