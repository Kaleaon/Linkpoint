package com.linkpoint.media

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import com.linkpoint.StreamingMediaService

/**
 * Modern Kotlin AudioIntentReceiver
 * Handles audio-related broadcast intents, particularly when audio becomes noisy
 */
class AudioIntentReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context?,
        intent: Intent?,
    ) {
        if (context == null || intent == null) return

        when (intent.action) {
            AudioManager.ACTION_AUDIO_BECOMING_NOISY -> {
                // Stop media playback when headphones are unplugged
                Intent(context, StreamingMediaService::class.java).apply {
                    action = "com.linkpoint.ACTION_STOP_MEDIA"
                    context.startService(this)
                }
            }
        }
    }
}
