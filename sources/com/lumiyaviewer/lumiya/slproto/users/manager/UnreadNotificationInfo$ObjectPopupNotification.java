// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Optional;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UnreadNotificationInfo, AutoValue_UnreadNotificationInfo_ObjectPopupNotification

public static abstract class ification
{

    private static empty empty = new AutoValue_UnreadNotificationInfo_ObjectPopupNotification(0, 0, Optional.absent());

    public static ification create(int i, int j, ification ification)
    {
        if (i == 0 && j == 0 && ification == null)
        {
            return empty;
        } else
        {
            return new AutoValue_UnreadNotificationInfo_ObjectPopupNotification(i, j, Optional.fromNullable(ification));
        }
    }

    public abstract int freshObjectPopupsCount();

    public boolean isEmpty()
    {
        return equals(empty);
    }

    public abstract Optional lastObjectPopup();

    public abstract int objectPopupsCount();


    public ification()
    {
    }
}
