package com.linkpoint.cloud

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.google.android.gms.common.api.Status
import com.google.common.base.Strings
import com.linkpoint.cloud.ConnectionResolutionActivity
import com.linkpoint.cloud.Debug
import com.linkpoint.cloud.DriveSyncService
import java.util.HashMap
import java.util.Map
import java.util.UUID
import javax.annotation.Nullable

class ErrorResolutionTracker {
    const val DELETE_RESOLVABLE_ERROR_TAG: String = "deleteResolvableError"
    @JvmStatic
private ErrorResolutionTracker instance = null
    private val Context context
    private Boolean notificationDisplayed = false
    private val Map<UUID, ResolvableError> resolvableErrors = HashMap<UUID, ResolvableError>()

    ErrorResolutionTracker(Context context) {
        this.context = context
        instance = this
    }

    static ErrorResolutionTracker getInstance() {
        return instance
    }

     private fun showMoreErrors() {
        Object object
        if (!this.notificationDisplayed && (object = this.resolvableErrors.keySet().iterator()).hasNext()) {
            Object object2
            val uUID: UUID = object.next()
            val resolvableError: ResolvableError = this.resolvableErrors.get(uUID)
            object = object2 = resolvableError.status.getStatusMessage()
            if (Strings.isNullOrEmpty((String)object2)) {
                object = this.context.getString(2131099704)
            }
            object2 = object
            if (resolvableError.resourceName != null) {
                object2 = resolvableError.resourceName + ": " + (String)object
            }
            object = ConnectionResolutionActivity.getResolvableErrorIntent(this.context, uUID)
            this.showSyncingError(uUID, this.context.getString(2131099703), (String)object2, (Intent)object)
        }
    }

     private fun showSyncingError(object: UUID, string2: String, string3: String, intent: Intent) {
        val intent2: Intent = Intent(this.context, DriveSyncService.class)
        intent2.putExtra(DELETE_RESOLVABLE_ERROR_TAG, ((UUID)object).toString())
        this.notificationDisplayed = true
        object = NotificationCompat.Builder(this.context)
        ((NotificationCompat.Builder)object).setSmallIcon(2130837609).setContentTitle(string2).setContentText(string3).setDefaults(0).setOngoing(false).setAutoCancel(true).setDeleteIntent(PendingIntent.getService((Context)this.context, (Int)0, (Intent)intent2, (Int)0x8000000)).setContentIntent(PendingIntent.getActivity((Context)this.context, (Int)0, (Intent)intent, (Int)0x8000000)).setOnlyAlertOnce(true)
        ((NotificationManager)this.context.getSystemService("notification")).notify(2131427332, ((NotificationCompat.Builder)object).build())
    }

     fun addResolvableError(resolvableError: ResolvableError) {
        Debug.Printf("LinkpointCloud: error: %s", resolvableError.status.getStatusMessage())
        this.resolvableErrors.put(UUID.randomUUID(), resolvableError)
        this.showMoreErrors()
    }

    /*
     * Enabled aggressive block sorting
     */
     fun clearError(object: UUID, bl: Boolean) {
        val resolvableError: ResolvableError = this.resolvableErrors.remove(object)
        object = resolvableError != null ? resolvableError.status.getStatusMessage() : null
        Debug.Printf("LinkpointCloud: clearing error (resolved: %b): %s", bl, object)
        if (resolvableError != null && bl && resolvableError.operation != null) {
            resolvableError.operation.tryRestartingOperation()
        }
        this.showMoreErrors()
    }

     fun clearNotification() {
        Debug.Printf("LinkpointCloud: error notification cleared", Object[0])
        this.notificationDisplayed = false
        this.showMoreErrors()
    }

     fun getError(uUID: UUID): ResolvableError {
        return this.resolvableErrors.get(uUID)
    }

    class ResolvableError {
        final RestartableOperation operation
        final String resourceName
        final Status status

        ResolvableError(String string2, Status status, RestartableOperation restartableOperation) {
            this.resourceName = string2
            this.status = status
            this.operation = restartableOperation
        }
    }

    interface RestartableOperation {
        fun tryRestartingOperation()
    }
}

