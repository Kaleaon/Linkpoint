// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.llsd.types:
//            LLSDMap

public static class value
{

    final String key;
    final LLSDNode value;

    public (String s, LLSDNode llsdnode)
    {
        key = s;
        value = llsdnode;
    }
}
