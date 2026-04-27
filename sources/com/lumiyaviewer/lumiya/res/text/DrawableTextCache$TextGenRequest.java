// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.text;

import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;

// Referenced classes of package com.lumiyaviewer.lumiya.res.text:
//            DrawableTextCache, DrawableTextBitmap, DrawableTextParams

private static class fontSize extends ResourceRequest
    implements Runnable
{

    private final int fontSize;

    public void execute()
    {
        PrimComputeExecutor.getInstance().execute(this);
    }

    public void run()
    {
        completeRequest(new DrawableTextBitmap((DrawableTextParams)getParams(), fontSize));
    }

    (DrawableTextParams drawabletextparams, int i, ResourceManager resourcemanager)
    {
        super(drawabletextparams, resourcemanager);
        fontSize = i;
    }
}
