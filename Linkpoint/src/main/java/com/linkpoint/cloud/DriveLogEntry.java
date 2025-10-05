/*
 * Decompiled with CFR 0.152.
 */
package com.linkpoint.cloud;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.linkpoint.cloud.MessageSyncBatch;

public class DriveLogEntry {
    public final long messageID;
    @Nullable
    public final MessageSyncBatch syncBatch;
    @NonNull
    public final String text;

    public DriveLogEntry(@NonNull String string2, @Nullable MessageSyncBatch messageSyncBatch, long l) {
        this.text = string2;
        this.syncBatch = messageSyncBatch;
        this.messageID = l;
    }
}

