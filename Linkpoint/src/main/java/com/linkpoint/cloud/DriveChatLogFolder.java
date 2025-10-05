/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 */
package com.linkpoint.cloud;

import android.content.Context;
import com.google.android.gms.common.api.GoogleApiClient;
import com.linkpoint.cloud.AgentSyncConnections;
import com.linkpoint.cloud.DriveConnectibleFolder;
import com.linkpoint.cloud.DriveConnectibleResource;
import com.linkpoint.cloud.DriveSynchronizer;
import com.linkpoint.cloud.DriveTextFile;
import com.linkpoint.cloud.LogWriteTracker;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class DriveChatLogFolder {
    @Nonnull
    private final Context context;
    private final Map<String, DriveTextFile> files = new HashMap<String, DriveTextFile>();
    @Nonnull
    private final DriveConnectibleFolder folder;
    @Nonnull
    private final GoogleApiClient googleApiClient;
    @Nonnull
    private final DriveSynchronizer synchronizer;

    DriveChatLogFolder(@Nonnull Context context, @Nonnull DriveSynchronizer driveSynchronizer, @Nullable String string2, DriveConnectibleResource driveConnectibleResource, @Nonnull GoogleApiClient googleApiClient, @Nonnull String string3) {
        this.context = context;
        this.googleApiClient = googleApiClient;
        this.synchronizer = driveSynchronizer;
        this.folder = new DriveConnectibleFolder(context, driveSynchronizer, string2, driveConnectibleResource, googleApiClient, string3);
    }

    @Nullable
    DriveTextFile getChatLogFile(AgentSyncConnections agentSyncConnections, UUID uUID, @Nonnull String string2, @Nonnull LogWriteTracker logWriteTracker, boolean bl) {
        DriveTextFile driveTextFile;
        DriveTextFile driveTextFile2 = driveTextFile = this.files.get(string2);
        if (driveTextFile == null) {
            driveTextFile2 = driveTextFile;
            if (bl) {
                driveTextFile2 = new DriveTextFile(this.context, this.synchronizer, agentSyncConnections, uUID, this.folder, this.googleApiClient, string2, logWriteTracker);
                this.files.put(string2, driveTextFile2);
            }
        }
        return driveTextFile2;
    }

    void resume() {
        Iterator<DriveTextFile> iterator = this.files.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().resume();
        }
    }

    void suspend() {
        Iterator<DriveTextFile> iterator = this.files.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().suspend();
        }
    }
}

