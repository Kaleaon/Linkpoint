// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;


// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            RequestHandler, ResultHandler

public interface RequestSource
{

    public abstract ResultHandler attachRequestHandler(RequestHandler requesthandler);

    public abstract void detachRequestHandler(RequestHandler requesthandler);
}
