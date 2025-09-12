// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;


// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            RequestSource, ResultHandler

public interface RequestQueue
    extends RequestSource
{

    public abstract Object getNextRequest();

    public abstract ResultHandler getResultHandler();

    public abstract void returnRequest(Object obj);

    public abstract Object waitForRequest()
        throws InterruptedException;
}
