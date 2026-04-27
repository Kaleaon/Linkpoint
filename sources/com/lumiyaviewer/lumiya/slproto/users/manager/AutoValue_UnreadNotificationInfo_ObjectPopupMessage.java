// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;


final class AutoValue_UnreadNotificationInfo_ObjectPopupMessage extends UnreadNotificationInfo.ObjectPopupMessage
{

    private final String message;
    private final String objectName;

    AutoValue_UnreadNotificationInfo_ObjectPopupMessage(String s, String s1)
    {
        if (s == null)
        {
            throw new NullPointerException("Null objectName");
        }
        objectName = s;
        if (s1 == null)
        {
            throw new NullPointerException("Null message");
        } else
        {
            message = s1;
            return;
        }
    }

    public boolean equals(Object obj)
    {
        boolean flag = false;
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof UnreadNotificationInfo.ObjectPopupMessage)
        {
            obj = (UnreadNotificationInfo.ObjectPopupMessage)obj;
            if (objectName.equals(((UnreadNotificationInfo.ObjectPopupMessage) (obj)).objectName()))
            {
                flag = message.equals(((UnreadNotificationInfo.ObjectPopupMessage) (obj)).message());
            }
            return flag;
        } else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return (objectName.hashCode() ^ 0xf4243) * 0xf4243 ^ message.hashCode();
    }

    public String message()
    {
        return message;
    }

    public String objectName()
    {
        return objectName;
    }

    public String toString()
    {
        return (new StringBuilder()).append("ObjectPopupMessage{objectName=").append(objectName).append(", ").append("message=").append(message).append("}").toString();
    }
}
