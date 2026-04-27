// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.orm;


// Referenced classes of package com.lumiyaviewer.lumiya.orm:
//            DBObject

public static class  extends Exception
{

    public (Class class1, String s)
    {
        super((new StringBuilder()).append("Failed to bind ").append(class1.getSimpleName()).append(": ").append(s).toString());
    }

    public (String s)
    {
        super(s);
    }
}
