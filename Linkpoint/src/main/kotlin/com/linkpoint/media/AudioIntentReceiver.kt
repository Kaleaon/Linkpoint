package com.linkpoint.media

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.linkpoint.StreamingMediaService

class AudioIntentReceiver : BroadcastReceiver() {
    public Unit onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.media.AUDIO_BECOMING_NOISY")) {
            Intent intent2 = Intent(context, StreamingMediaService.class)
            intent2.setAction("com.linkpoint.ACTION_STOP_MEDIA")
            context.startService(intent2)
        }
    }
}
