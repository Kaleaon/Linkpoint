// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import android.os.Parcel;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            AvatarPickKey

static final class 
    implements android.os.or
{

    public AvatarPickKey createFromParcel(Parcel parcel)
    {
        return new AvatarPickKey(parcel);
    }

    public volatile Object createFromParcel(Parcel parcel)
    {
        return createFromParcel(parcel);
    }

    public AvatarPickKey[] newArray(int i)
    {
        return new AvatarPickKey[i];
    }

    public volatile Object[] newArray(int i)
    {
        return newArray(i);
    }

    ()
    {
    }
}
