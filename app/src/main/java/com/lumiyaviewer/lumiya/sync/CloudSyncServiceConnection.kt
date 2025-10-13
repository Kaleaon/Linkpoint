package com.lumiyaviewer.lumiya.sync

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import androidx.core.app.NotificationCompat
import com.google.common.base.Strings
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.cloud.common.Bundleable
import com.lumiyaviewer.lumiya.cloud.common.CloudSyncMessenger
import com.lumiyaviewer.lumiya.cloud.common.LogMessagesCompleted
import com.lumiyaviewer.lumiya.cloud.common.LogMessagesFlushed
import com.lumiyaviewer.lumiya.cloud.common.LogSyncStart
import com.lumiyaviewer.lumiya.cloud.common.LogSyncStatus
import com.lumiyaviewer.lumiya.cloud.common.MessageType
import com.lumiyaviewer.lumiya.licensing.LicenseChecker
import com.lumiyaviewer.lumiya.slproto.avatar.SLMoveEvents
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.concurrent.atomic.AtomicBoolean

class CloudSyncServiceConnection(
    private val context: Context,
    private val userManager: UserManager
) : ServiceConnection {

    companion object {
        private const val REQUIRED_PLUGIN_VERSION = 1
        private const val MESSAGE_WHAT = 100
        private const val KEY_MESSAGE = "message"
        private const val KEY_MESSAGE_TYPE = "messageType"

        fun checkPluginInstalled(context: Context): Boolean {
            val intent = Intent().apply {
                component = ComponentName(
                    "com.lumiyaviewer.lumiya.cloud",
                    "com.lumiyaviewer.lumiya.cloud.DriveSyncService"
                )
            }
            val queryResult = context.packageManager.queryIntentServices(intent, 0)
            return queryResult != null && queryResult.isNotEmpty()
        }
    }

    private val syncingStarted = AtomicBoolean(false)
    private var toPluginMessenger: Messenger? = null
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private val fromPluginHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(message: Message) {
            if (message.what == MESSAGE_WHAT && message.obj is Bundle) {
                val bundle = message.obj as Bundle
                if (bundle.containsKey(KEY_MESSAGE) && bundle.containsKey(KEY_MESSAGE_TYPE)) {
                    try {
                        val messageType = MessageType.valueOf(bundle.getString(KEY_MESSAGE_TYPE)!!)
                        val messageBundle = bundle.getBundle(KEY_MESSAGE)!!
                        
                        when (messageType) {
                            MessageType.LogMessagesCompleted -> {
                                onLogMessagesCompleted(LogMessagesCompleted(messageBundle))
                            }
                            MessageType.LogMessagesFlushed -> {
                                onLogMessagesFlushed(LogMessagesFlushed(messageBundle))
                            }
                            MessageType.LogSyncStatus -> {
                                onLogSyncStatus(LogSyncStatus(messageBundle))
                            }
                            else -> {
                                // Ignore other message types
                            }
                        }
                    } catch (e: Exception) {
                        Debug.Warning(e)
                    }
                }
            }
        }
    }

    private val fromPluginMessenger = Messenger(fromPluginHandler)

    override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
        Debug.Printf("LumiyaCloud: service connected")
        toPluginMessenger = Messenger(binder)
        try {
            val versionCode = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
            sendMessage(MessageType.LogSyncStart, LogSyncStart(versionCode, userManager.userID))
        } catch (e: PackageManager.NameNotFoundException) {
            Debug.Warning(e)
        }
    }

    override fun onServiceDisconnected(componentName: ComponentName) {
        Debug.Printf("LumiyaCloud: service disconnected")
    }

    fun disconnect() {
        mainThreadHandler.post {
            Debug.Printf("LumiyaCloud: disconnecting from sync service")
            context.unbindService(this)
        }
    }

    fun sendMessage(messageType: MessageType, bundleable: Bundleable): Boolean {
        val messenger = toPluginMessenger ?: return false
        return CloudSyncMessenger.sendMessage(messenger, messageType, bundleable, fromPluginMessenger)
    }

    fun stopSyncing() {
        if (!syncingStarted.getAndSet(false)) {
            disconnect()
        } else {
            toPluginMessenger?.let {
                userManager.syncManager.stopSyncing()
            }
        }
    }

    private fun onLogMessagesCompleted(logMessagesCompleted: LogMessagesCompleted) {
        Debug.Printf(
            "LumiyaCloud: written messages until %d for agent %s",
            logMessagesCompleted.lastWrittenMessageID,
            logMessagesCompleted.agentUUID
        )
        if (userManager.userID == logMessagesCompleted.agentUUID) {
            userManager.syncManager.onMessagesWritten(logMessagesCompleted.lastWrittenMessageID)
        }
    }

    private fun onLogMessagesFlushed(logMessagesFlushed: LogMessagesFlushed) {
        Debug.Printf("LumiyaCloud: flushed some messages for agent %s", logMessagesFlushed.agentUUID)
        if (userManager.userID == logMessagesFlushed.agentUUID) {
            userManager.syncManager.onMessagesFlushed(logMessagesFlushed.messageIDs)
        }
    }

    private fun onLogSyncStatus(logSyncStatus: LogSyncStatus) {
        Debug.Printf(
            "LumiyaCloud: got logSyncStatus %s, plugin version %d",
            logSyncStatus.status,
            logSyncStatus.pluginVersionCode
        )
        
        toPluginMessenger ?: return

        when (logSyncStatus.status) {
            LogSyncStatus.Status.AppVersionRejected -> {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(LicenseChecker.APP_STORE_URL)
                }
                showSyncingError(
                    context.getString(R.string.cloud_sync_app_outdated),
                    context.getString(R.string.cloud_sync_app_outdated_long),
                    intent
                )
            }
            LogSyncStatus.Status.GoogleDriveError -> {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://drive.google.com/")
                }
                val errorMessage = if (Strings.isNullOrEmpty(logSyncStatus.errorMessage)) {
                    context.getString(R.string.cloud_sync_drive_error_long)
                } else {
                    context.getString(R.string.cloud_sync_drive_error_message, logSyncStatus.errorMessage)
                }
                showSyncingError(
                    context.getString(R.string.cloud_sync_drive_error),
                    errorMessage,
                    intent
                )
            }
            LogSyncStatus.Status.Ready -> {
                if (logSyncStatus.pluginVersionCode >= REQUIRED_PLUGIN_VERSION) {
                    syncingStarted.set(true)
                    userManager.syncManager.startSyncing(this)
                } else {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(LicenseChecker.CLOUD_PLUGIN_URL)
                    }
                    showSyncingError(
                        context.getString(R.string.cloud_sync_plugin_outdated),
                        context.getString(R.string.cloud_sync_plugin_outdated_long),
                        intent
                    )
                }
            }
        }
    }

    fun showSyncingError(title: String, message: String, intent: Intent) {
        val notification = NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.ic_cloud_sync_notify)
            .setContentTitle(title)
            .setContentText(message)
            .setDefaults(0)
            .setOngoing(false)
            .setAutoCancel(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    SLMoveEvents.AGENT_CONTROL_AWAY
                )
            )
            .setOnlyAlertOnce(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(R.id.google_drive_problem_notify, notification)
    }
}
