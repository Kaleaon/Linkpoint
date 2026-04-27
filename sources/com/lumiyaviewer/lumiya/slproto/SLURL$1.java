// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto;

import android.os.Parcel;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto:
//            SLURL

static final class 
    implements android.os.le.Creator
{

    public SLURL createFromParcel(Parcel parcel)
    {
        return new SLURL(parcel, null);
    }

    public volatile Object createFromParcel(Parcel parcel)
    {
        return createFromParcel(parcel);
    }

    public SLURL[] newArray(int i)
    {
        return new SLURL[i];
    }

    public volatile Object[] newArray(int i)
    {
        return newArray(i);
    }

    ()
    {
    }
}
