// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Optional;

final class AutoValue_UnreadNotificationInfo_ObjectPopupNotification extends UnreadNotificationInfo.ObjectPopupNotification
{

    private final int freshObjectPopupsCount;
    private final Optional lastObjectPopup;
    private final int objectPopupsCount;

    AutoValue_UnreadNotificationInfo_ObjectPopupNotification(int i, int j, Optional optional)
    {
        freshObjectPopupsCount = i;
        objectPopupsCount = j;
        if (optional == null)
        {
            throw new NullPointerException("Null lastObjectPopup");
        } else
        {
            lastObjectPopup = optional;
            return;
        }
    }

    public boolean equals(Object obj)
    {
        boolean flag1 = false;
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof UnreadNotificationInfo.ObjectPopupNotification)
        {
            obj = (UnreadNotificationInfo.ObjectPopupNotification)obj;
            boolean flag = flag1;
            if (freshObjectPopupsCount == ((UnreadNotificationInfo.ObjectPopupNotification) (obj)).freshObjectPopupsCount())
            {
                flag = flag1;
                if (objectPopupsCount == ((UnreadNotificationInfo.ObjectPopupNotification) (obj)).objectPopupsCount())
                {
                    flag = lastObjectPopup.equals(((UnreadNotificationInfo.ObjectPopupNotification) (obj)).lastObjectPopup());
                }
            }
            return flag;
        } else
        {
            return false;
        }
    }

    public int freshObjectPopupsCount()
    {
        return freshObjectPopupsCount;
    }

    public int hashCode()
    {
        return ((freshObjectPopupsCount ^ 0xf4243) * 0xf4243 ^ objectPopupsCount) * 0xf4243 ^ lastObjectPopup.hashCode();
    }

    public Optional lastObjectPopup()
    {
        return lastObjectPopup;
    }

    public int objectPopupsCount()
    {
        return objectPopupsCount;
    }

    public String toString()
    {
        return (new StringBuilder()).append("ObjectPopupNotification{freshObjectPopupsCount=").append(freshObjectPopupsCount).append(", ").append("objectPopupsCount=").append(objectPopupsCount).append(", ").append("lastObjectPopup=").append(lastObjectPopup).append("}").toString();
    }
}
