// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            MyAvatarState

final class AutoValue_MyAvatarState extends MyAvatarState
{

    private final boolean hasHUDs;
    private final boolean isFlying;
    private final boolean isSitting;
    private final int sittingOn;

    AutoValue_MyAvatarState(boolean flag, int i, boolean flag1, boolean flag2)
    {
        isSitting = flag;
        sittingOn = i;
        isFlying = flag1;
        hasHUDs = flag2;
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof MyAvatarState)
        {
            obj = (MyAvatarState)obj;
            if (isSitting == ((MyAvatarState) (obj)).isSitting() && sittingOn == ((MyAvatarState) (obj)).sittingOn() && isFlying == ((MyAvatarState) (obj)).isFlying())
            {
                return hasHUDs == ((MyAvatarState) (obj)).hasHUDs();
            } else
            {
                return false;
            }
        } else
        {
            return false;
        }
    }

    public boolean hasHUDs()
    {
        return hasHUDs;
    }

    public int hashCode()
    {
        char c2 = '\u04CF';
        char c;
        char c1;
        int i;
        if (isSitting)
        {
            c = '\u04CF';
        } else
        {
            c = '\u04D5';
        }
        i = sittingOn;
        if (isFlying)
        {
            c1 = '\u04CF';
        } else
        {
            c1 = '\u04D5';
        }
        if (!hasHUDs)
        {
            c2 = '\u04D5';
        }
        return (c1 ^ ((c ^ 0xf4243) * 0xf4243 ^ i) * 0xf4243) * 0xf4243 ^ c2;
    }

    public boolean isFlying()
    {
        return isFlying;
    }

    public boolean isSitting()
    {
        return isSitting;
    }

    public int sittingOn()
    {
        return sittingOn;
    }

    public String toString()
    {
        return (new StringBuilder()).append("MyAvatarState{isSitting=").append(isSitting).append(", ").append("sittingOn=").append(sittingOn).append(", ").append("isFlying=").append(isFlying).append(", ").append("hasHUDs=").append(hasHUDs).append("}").toString();
    }
}
