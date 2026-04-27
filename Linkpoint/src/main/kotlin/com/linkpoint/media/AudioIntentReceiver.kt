package com.linkpoint.media

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.linkpoint.StreamingMediaService

class AudioIntentReceiver : BroadcastReceiver() {
    fun onReceive(context: Context, intent: Intent) {
        if (intent.getAction().equals("android.media.AUDIO_BECOMING_NOISY")) {
            val intent2: Intent = Intent(context, StreamingMediaService.class)
            intent2.setAction("com.linkpoint.ACTION_STOP_MEDIA")
            context.startService(intent2)
        }
    }
}
