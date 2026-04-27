// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Strings;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UnreadNotificationInfo, AutoValue_UnreadNotificationInfo_ObjectPopupMessage

public static abstract class upMessage
{

    public static upMessage create(String s, String s1)
    {
        return new AutoValue_UnreadNotificationInfo_ObjectPopupMessage(Strings.nullToEmpty(s), Strings.nullToEmpty(s1));
    }

    public abstract String message();

    public abstract String objectName();

    public upMessage()
    {
    }
}
