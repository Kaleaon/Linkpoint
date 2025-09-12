// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.lumiyaviewer.lumiya.StreamingMediaService;

public class AudioIntentReceiver extends BroadcastReceiver
{

    public AudioIntentReceiver()
    {
    }

    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals("android.media.AUDIO_BECOMING_NOISY"))
        {
            intent = new Intent(context, com/lumiyaviewer/lumiya/StreamingMediaService);
            intent.setAction("com.lumiyaviewer.lumiya.ACTION_STOP_MEDIA");
            context.startService(intent);
        }
    }
}
