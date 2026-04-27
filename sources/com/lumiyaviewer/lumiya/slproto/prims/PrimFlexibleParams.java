// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;

public class PrimFlexibleParams
{

    public float AirFriction;
    public float Gravity;
    public int NumFlexiSections;
    public float Tension;
    public LLVector3 UserForce;
    public float WindSensitivity;

    public PrimFlexibleParams(ByteBuffer bytebuffer, int i)
    {
        byte byte0 = 0;
        super();
        int j = bytebuffer.get();
        byte byte1 = bytebuffer.get();
        Tension = (float)(j & 0x7f) / 10F;
        AirFriction = (float)(byte1 & 0x7f) / 10F;
        if ((j & 0x80) != 0)
        {
            byte0 = 2;
        }
        j = byte0;
        if ((byte1 & 0x80) != 0)
        {
            j = byte0 | 1;
        }
        NumFlexiSections = (1 << j) + 1;
        Gravity = (float)(bytebuffer.get() & 0xff) / 10F - 10F;
        WindSensitivity = (float)(bytebuffer.get() & 0xff) / 10F;
        if (bytebuffer.position() < i)
        {
            UserForce = LLVector3.parseFloatVec(bytebuffer);
            return;
        } else
        {
            UserForce = LLVector3.Zero;
            return;
        }
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof PrimFlexibleParams))
        {
            return false;
        }
        for (obj = (PrimFlexibleParams)obj; Tension != ((PrimFlexibleParams) (obj)).Tension || AirFriction != ((PrimFlexibleParams) (obj)).AirFriction || Gravity != ((PrimFlexibleParams) (obj)).Gravity || WindSensitivity != ((PrimFlexibleParams) (obj)).WindSensitivity || NumFlexiSections != ((PrimFlexibleParams) (obj)).NumFlexiSections || UserForce.equals(((PrimFlexibleParams) (obj)).UserForce) ^ true;)
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return Float.floatToRawIntBits(Tension) + 0 + Float.floatToRawIntBits(AirFriction) + Float.floatToRawIntBits(Gravity) + Float.floatToRawIntBits(WindSensitivity) + NumFlexiSections + UserForce.hashCode();
    }
}
