// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.objects;

import com.google.common.collect.ImmutableList;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.objects:
//            PayInfo

final class AutoValue_PayInfo extends PayInfo
{

    private final int defaultPayPrice;
    private final ImmutableList payPrices;

    AutoValue_PayInfo(int i, ImmutableList immutablelist)
    {
        defaultPayPrice = i;
        payPrices = immutablelist;
    }

    public int defaultPayPrice()
    {
        return defaultPayPrice;
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof PayInfo)
        {
            obj = (PayInfo)obj;
            if (defaultPayPrice == ((PayInfo) (obj)).defaultPayPrice())
            {
                if (payPrices == null)
                {
                    return ((PayInfo) (obj)).payPrices() == null;
                } else
                {
                    return payPrices.equals(((PayInfo) (obj)).payPrices());
                }
            } else
            {
                return false;
            }
        } else
        {
            return false;
        }
    }

    public int hashCode()
    {
        int j = defaultPayPrice;
        int i;
        if (payPrices == null)
        {
            i = 0;
        } else
        {
            i = payPrices.hashCode();
        }
        return i ^ 0xf4243 * (j ^ 0xf4243);
    }

    public ImmutableList payPrices()
    {
        return payPrices;
    }

    public String toString()
    {
        return (new StringBuilder()).append("PayInfo{defaultPayPrice=").append(defaultPayPrice).append(", ").append("payPrices=").append(payPrices).append("}").toString();
    }
}
