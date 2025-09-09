package com.lumiyaviewer.lumiya.cloud.common;

import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LogSyncStatus implements Bundleable {
    @Nullable
    public final String errorMessage;
    public final int pluginVersionCode;
    @Nonnull
    public final Status status;

    public enum Status {
        Ready,
        AppVersionRejected,
        GoogleDriveError
    }

    public LogSyncStatus(int i, @Nonnull Status status2, @Nullable String str) {
        this.pluginVersionCode = i;
        this.status = status2;
        this.errorMessage = str;
    }

    public LogSyncStatus(Bundle bundle) {
        this.pluginVersionCode = bundle.getInt("pluginVersionCode");
        this.status = Status.valueOf(bundle.getString(NotificationCompat.CATEGORY_STATUS));
        this.errorMessage = bundle.getString("errorMessage");
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("pluginVersionCode", this.pluginVersionCode);
        bundle.putString(NotificationCompat.CATEGORY_STATUS, this.status.toString());
        bundle.putString("errorMessage", this.errorMessage);
        return bundle;
    }
}
