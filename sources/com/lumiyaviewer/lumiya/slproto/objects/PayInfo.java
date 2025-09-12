// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.objects;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.objects:
//            AutoValue_PayInfo

public abstract class PayInfo
{

    public static final int MAX_PAY_PRICES = 4;

    public PayInfo()
    {
    }

    public static PayInfo create(int i, int ai[])
    {
        ImmutableList immutablelist = null;
        if (ai != null)
        {
            immutablelist = ImmutableList.copyOf(Ints.asList(ai));
        }
        return new AutoValue_PayInfo(i, immutablelist);
    }

    public abstract int defaultPayPrice();

    public abstract ImmutableList payPrices();
}
