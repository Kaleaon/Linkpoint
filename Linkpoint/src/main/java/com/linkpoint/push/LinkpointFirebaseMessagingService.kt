package com.linkpoint.push

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.linkpoint.service.BackgroundRuntimeConfig
import com.linkpoint.service.BackgroundResumeScheduler

class LinkpointFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "LPFirebaseMessagingSvc"
    }

    override fun onMessageReceived(message: RemoteMessage) {
        if (!BackgroundRuntimeConfig.isPushWakeupsEnabled(applicationContext)) {
            Log.d(TAG, "Push wakeups disabled; dropping incoming push")
            return
        }

        val event = PushEventParser.parse(message)
        PushEventBus.publish(event)

        // Ensure process wake + constrained retry orchestration even if app process was dead.
        BackgroundResumeScheduler.schedule(applicationContext, immediate = true)
        Log.i(TAG, "Push event processed: type=${event.type} dedupe=${event.dedupeId}")
    }

    override fun onNewToken(token: String) {
        Log.i(TAG, "FCM token refreshed (${token.length} chars)")
        // Future: upload token to grid service / self-host relay endpoint.
    }
}
