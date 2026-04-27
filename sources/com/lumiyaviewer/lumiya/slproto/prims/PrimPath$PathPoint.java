// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.prims:
//            PrimPath

public static class TexT
{

    float TexT;
    LLVector3 pos;
    LLQuaternion rot;
    LLVector2 scale;

    public ()
    {
        pos = new LLVector3();
        scale = new LLVector2();
        rot = new LLQuaternion();
        TexT = 0.0F;
    }
}
