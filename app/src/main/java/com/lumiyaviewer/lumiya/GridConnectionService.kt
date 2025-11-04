package com.lumiyaviewer.lumiya

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.lumiyaviewer.lumiya.slproto.SLGridConnection
import java.util.concurrent.atomic.AtomicReference

/**
 * Temporary stub of the legacy GridConnectionService. The original service is
 * extremely large; this Kotlin version keeps the public surface required by the
 * rest of the app so compilation succeeds while we rebuild the feature set.
 */
class GridConnectionService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val LOGIN_ACTION = "com.lumiyaviewer.lumiya.action.LOGIN"
        const val LOGOUT_ACTION = "com.lumiyaviewer.lumiya.action.LOGOUT"

        const val ACTION_VOICE_ACCEPT = "com.lumiyaviewer.lumiya.voice.ACCEPT"
        const val ACTION_VOICE_REJECT = "com.lumiyaviewer.lumiya.voice.REJECT"

        private val connectionRef = AtomicReference<SLGridConnection?>(null)

        fun getGridConnection(): SLGridConnection? = connectionRef.get()

        fun setGridConnection(connection: SLGridConnection?) {
            connectionRef.set(connection)
        }

        fun getServiceInstance(): GridConnectionService? = serviceInstance.get()

        fun setServiceInstance(service: GridConnectionService?) {
            serviceInstance.set(service)
        }

        fun hasVisibleActivities(): Boolean = false

        fun setVoiceLoginInfo(@Suppress("UNUSED_PARAMETER") info: Any?) {}

        private val serviceInstance = AtomicReference<GridConnectionService?>(null)

        enum class LEDAction { NONE, PULSE, SOLID }
    }
}
