// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.https;

import java.util.concurrent.ThreadFactory;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.https:
//            GenericHTTPExecutor

static final class A
    implements ThreadFactory
{

    public Thread newThread(Runnable runnable)
    {
        return new Thread(runnable, "HTTPAccess");
    }

    A()
    {
    }
}
