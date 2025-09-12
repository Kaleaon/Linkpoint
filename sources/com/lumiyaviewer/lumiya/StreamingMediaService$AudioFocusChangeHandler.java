// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya;

import android.os.Handler;
import android.os.Message;
import java.lang.ref.WeakReference;

// Referenced classes of package com.lumiyaviewer.lumiya:
//            StreamingMediaService

private static class <init> extends Handler
{

    private final WeakReference streamingMediaService;

    public void handleMessage(Message message)
    {
        if (message.what == 100)
        {
            StreamingMediaService streamingmediaservice = (StreamingMediaService)streamingMediaService.get();
            if (streamingmediaservice != null)
            {
                StreamingMediaService._2D_wrap0(streamingmediaservice, message.arg1);
            }
        }
    }

    private (StreamingMediaService streamingmediaservice)
    {
        streamingMediaService = new WeakReference(streamingmediaservice);
    }

    streamingMediaService(StreamingMediaService streamingmediaservice, streamingMediaService streamingmediaservice1)
    {
        this(streamingmediaservice);
    }
}
