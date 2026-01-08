package com.lumiyaviewer.lumiya.cloud

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.android.gms.common.api.Status
import com.google.common.base.Strings
import java.util.UUID

internal class ErrorResolutionTracker(private val context: Context) {
    private var notificationDisplayed: Boolean = false
    private val resolvableErrors: MutableMap<UUID, ResolvableError> = HashMap()

    init {
        instance = this
    }

    private fun showMoreErrors() {
        if (!notificationDisplayed) {
            val iterator = resolvableErrors.keys.iterator()
            if (iterator.hasNext()) {
                val uuid = iterator.next()
                val resolvableError = resolvableErrors[uuid] ?: return

                var message: String = resolvableError.status.statusMessage ?: ""
                if (Strings.isNullOrEmpty(message)) {
                    message = context.getString(2131099704)
                }

                val finalMessage =
                    if (resolvableError.resourceName != null) {
                        "${resolvableError.resourceName}: $message"
                    } else {
                        message
                    }

                val intent = ConnectionResolutionActivity.getResolvableErrorIntent(context, uuid)
                showSyncingError(uuid, context.getString(2131099703), finalMessage, intent)
            }
        }
    }

    private fun showSyncingError(
        uuid: UUID,
        title: String,
        message: String,
        intent: Intent,
    ) {
        val deleteIntent =
            Intent(context, DriveSyncService::class.java).apply {
                putExtra(DELETE_RESOLVABLE_ERROR_TAG, uuid.toString())
            }

        notificationDisplayed = true

        val notification =
            NotificationCompat.Builder(context)
                .setSmallIcon(2130837609)
                .setContentTitle(title)
                .setContentText(message)
                .setDefaults(0)
                .setOngoing(false)
                .setAutoCancel(true)
                .setDeleteIntent(
                    PendingIntent.getService(
                        context,
                        0,
                        deleteIntent,
                        PendingIntent.FLAG_IMMUTABLE,
                    ),
                )
                .setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE,
                    ),
                )
                .setOnlyAlertOnce(true)
                .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(2131427332, notification)
    }

    fun addResolvableError(resolvableError: ResolvableError) {
        Debug.Printf("LumiyaCloud: new error: %s", resolvableError.status.statusMessage)
        resolvableErrors[UUID.randomUUID()] = resolvableError
        showMoreErrors()
    }

    fun clearError(
        uuid: UUID,
        resolved: Boolean,
    ) {
        val resolvableError = resolvableErrors.remove(uuid)
        val message = resolvableError?.status?.statusMessage

        Debug.Printf("LumiyaCloud: clearing error (resolved: %b): %s", resolved, message)

        if (resolvableError != null && resolved && resolvableError.operation != null) {
            resolvableError.operation.tryRestartingOperation()
        }

        showMoreErrors()
    }

    fun clearNotification() {
        Debug.Printf("LumiyaCloud: error notification cleared")
        notificationDisplayed = false
        showMoreErrors()
    }

    fun getError(uuid: UUID): ResolvableError? {
        return resolvableErrors[uuid]
    }

    class ResolvableError(
        val resourceName: String?,
        val status: Status,
        val operation: RestartableOperation?,
    )

    interface RestartableOperation {
        fun tryRestartingOperation()
    }

    companion object {
        const val DELETE_RESOLVABLE_ERROR_TAG = "deleteResolvableError"
        private var instance: ErrorResolutionTracker? = null

        @JvmStatic
        fun getInstance(): ErrorResolutionTracker? {
            return instance
        }
    }
}
