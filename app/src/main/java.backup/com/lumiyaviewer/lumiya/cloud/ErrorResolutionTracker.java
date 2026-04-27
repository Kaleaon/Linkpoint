/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.app.NotificationManager
 *  android.app.PendingIntent
 *  android.content.Context
 *  android.content.Intent
 */
package com.lumiyaviewer.lumiya.cloud;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.google.android.gms.common.api.Status;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.cloud.ConnectionResolutionActivity;
import com.lumiyaviewer.lumiya.cloud.Debug;
import com.lumiyaviewer.lumiya.cloud.DriveSyncService;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

class ErrorResolutionTracker {
    static final String DELETE_RESOLVABLE_ERROR_TAG = "deleteResolvableError";
    private static ErrorResolutionTracker instance = null;
    private final Context context;
    private boolean notificationDisplayed = false;
    private final Map<UUID, ResolvableError> resolvableErrors = new HashMap<UUID, ResolvableError>();

    ErrorResolutionTracker(Context context) {
        this.context = context;
        instance = this;
    }

    static ErrorResolutionTracker getInstance() {
        return instance;
    }

    private void showMoreErrors() {
        Object object;
        if (!this.notificationDisplayed && (object = this.resolvableErrors.keySet().iterator()).hasNext()) {
            Object object2;
            UUID uUID = object.next();
            ResolvableError resolvableError = this.resolvableErrors.get(uUID);
            object = object2 = resolvableError.status.getStatusMessage();
            if (Strings.isNullOrEmpty((String)object2)) {
                object = this.context.getString(2131099704);
            }
            object2 = object;
            if (resolvableError.resourceName != null) {
                object2 = resolvableError.resourceName + ": " + (String)object;
            }
            object = ConnectionResolutionActivity.getResolvableErrorIntent(this.context, uUID);
            this.showSyncingError(uUID, this.context.getString(2131099703), (String)object2, (Intent)object);
        }
    }

    private void showSyncingError(UUID object, String string2, String string3, Intent intent) {
        Intent intent2 = new Intent(this.context, DriveSyncService.class);
        intent2.putExtra(DELETE_RESOLVABLE_ERROR_TAG, ((UUID)object).toString());
        this.notificationDisplayed = true;
        object = new NotificationCompat.Builder(this.context);
        ((NotificationCompat.Builder)object).setSmallIcon(2130837609).setContentTitle(string2).setContentText(string3).setDefaults(0).setOngoing(false).setAutoCancel(true).setDeleteIntent(PendingIntent.getService((Context)this.context, (int)0, (Intent)intent2, (int)0x8000000)).setContentIntent(PendingIntent.getActivity((Context)this.context, (int)0, (Intent)intent, (int)0x8000000)).setOnlyAlertOnce(true);
        ((NotificationManager)this.context.getSystemService("notification")).notify(2131427332, ((NotificationCompat.Builder)object).build());
    }

    void addResolvableError(ResolvableError resolvableError) {
        Debug.Printf("LumiyaCloud: new error: %s", resolvableError.status.getStatusMessage());
        this.resolvableErrors.put(UUID.randomUUID(), resolvableError);
        this.showMoreErrors();
    }

    /*
     * Enabled aggressive block sorting
     */
    void clearError(UUID object, boolean bl) {
        ResolvableError resolvableError = this.resolvableErrors.remove(object);
        object = resolvableError != null ? resolvableError.status.getStatusMessage() : null;
        Debug.Printf("LumiyaCloud: clearing error (resolved: %b): %s", bl, object);
        if (resolvableError != null && bl && resolvableError.operation != null) {
            resolvableError.operation.tryRestartingOperation();
        }
        this.showMoreErrors();
    }

    void clearNotification() {
        Debug.Printf("LumiyaCloud: error notification cleared", new Object[0]);
        this.notificationDisplayed = false;
        this.showMoreErrors();
    }

    ResolvableError getError(UUID uUID) {
        return this.resolvableErrors.get(uUID);
    }

    static class ResolvableError {
        final RestartableOperation operation;
        @Nullable
        final String resourceName;
        final Status status;

        ResolvableError(@Nullable String string2, Status status, RestartableOperation restartableOperation) {
            this.resourceName = string2;
            this.status = status;
            this.operation = restartableOperation;
        }
    }

    static interface RestartableOperation {
        public void tryRestartingOperation();
    }
}

