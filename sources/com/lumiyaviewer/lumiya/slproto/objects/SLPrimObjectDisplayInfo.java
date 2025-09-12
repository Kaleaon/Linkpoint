// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.objects;

import com.google.common.base.Strings;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.objects:
//            SLObjectDisplayInfo, SLObjectInfo

public class SLPrimObjectDisplayInfo extends SLObjectDisplayInfo
{

    public final int localID;
    public final boolean payable;
    public final boolean touchable;

    public SLPrimObjectDisplayInfo(SLObjectInfo slobjectinfo, float f)
    {
        int i = slobjectinfo.localID;
        String s;
        boolean flag;
        if (slobjectinfo.nameKnown)
        {
            s = Strings.nullToEmpty(slobjectinfo.name);
        } else
        {
            s = null;
        }
        super(i, s, f, slobjectinfo.hierLevel);
        localID = slobjectinfo.localID;
        touchable = slobjectinfo.isTouchable();
        if (slobjectinfo.isPayable() || slobjectinfo.saleType != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        payable = flag;
    }
}
