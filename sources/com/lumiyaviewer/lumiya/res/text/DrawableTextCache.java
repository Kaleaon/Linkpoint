// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.text;

import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;

// Referenced classes of package com.lumiyaviewer.lumiya.res.text:
//            DrawableTextParams, DrawableTextBitmap

public class DrawableTextCache extends ResourceMemoryCache
{
    private static class TextGenRequest extends ResourceRequest
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

        TextGenRequest(DrawableTextParams drawabletextparams, int i, ResourceManager resourcemanager)
        {
            super(drawabletextparams, resourcemanager);
            fontSize = i;
        }
    }


    private final int fontSize;

    public DrawableTextCache(int i)
    {
        fontSize = i;
    }

    protected ResourceRequest CreateNewRequest(DrawableTextParams drawabletextparams, ResourceManager resourcemanager)
    {
        return new TextGenRequest(drawabletextparams, fontSize, resourcemanager);
    }

    protected volatile ResourceRequest CreateNewRequest(Object obj, ResourceManager resourcemanager)
    {
        return CreateNewRequest((DrawableTextParams)obj, resourcemanager);
    }
}
