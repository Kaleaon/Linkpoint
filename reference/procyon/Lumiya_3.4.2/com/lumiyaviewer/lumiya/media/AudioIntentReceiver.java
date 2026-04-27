// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.media;

import com.lumiyaviewer.lumiya.StreamingMediaService;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;

public class AudioIntentReceiver extends BroadcastReceiver
{
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals("android.media.AUDIO_BECOMING_NOISY")) {
            intent = new Intent(context, (Class)StreamingMediaService.class);
            intent.setAction("com.lumiyaviewer.lumiya.ACTION_STOP_MEDIA");
            context.startService(intent);
        }
    }
}
