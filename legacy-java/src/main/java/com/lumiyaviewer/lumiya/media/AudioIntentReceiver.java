package com.lumiyaviewer.lumiya.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.lumiyaviewer.lumiya.StreamingMediaService;

/* loaded from: classes.dex */
public class AudioIntentReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.media.AUDIO_BECOMING_NOISY")) {
            Intent intent2 = new Intent(context, (Class<?>) StreamingMediaService.class);
            intent2.setAction("com.lumiyaviewer.lumiya.ACTION_STOP_MEDIA");
            context.startService(intent2);
        }
    }
}
