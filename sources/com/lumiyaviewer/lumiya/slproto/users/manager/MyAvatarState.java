// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            AutoValue_MyAvatarState

public abstract class MyAvatarState
{

    public MyAvatarState()
    {
    }

    public static MyAvatarState create(boolean flag, int i, boolean flag1, boolean flag2)
    {
        return new AutoValue_MyAvatarState(flag, i, flag1, flag2);
    }

    public abstract boolean hasHUDs();

    public abstract boolean isFlying();

    public abstract boolean isSitting();

    public abstract int sittingOn();
}
