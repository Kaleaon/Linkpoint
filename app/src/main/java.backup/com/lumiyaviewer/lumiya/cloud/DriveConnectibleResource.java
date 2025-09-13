/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.content.SharedPreferences
 *  android.content.SharedPreferences$Editor
 *  android.preference.PreferenceManager
 */
package com.lumiyaviewer.lumiya.cloud;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.cloud.Debug;
import com.lumiyaviewer.lumiya.cloud.DriveSynchronizer;
import com.lumiyaviewer.lumiya.cloud.ErrorResolutionTracker;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

abstract class DriveConnectibleResource {
    @Nonnull
    private final Context context;
    @Nullable
    private DriveResource driveResource;
    @Nullable
    private String errorMessage;
    @Nonnull
    final GoogleApiClient googleApiClient;
    private final Set<OnResourceReadyListener> listeners = new HashSet<OnResourceReadyListener>();
    private boolean needInvalidate = false;
    private boolean needRecreate = false;
    private final ResultCallback<? super DriveResource.MetadataResult> onFolderMetadata;
    private final OnResourceReadyListener onParentFolderReady;
    private final OnResourceReadyListener onParentFolderReadyForSearch;
    private final ResultCallback<? super DriveApi.MetadataBufferResult> onQueryResults;
    private final DriveSynchronizer.OnSyncCompletedListener onSyncCompleted;
    @Nullable
    private final DriveConnectibleResource parentFolder;
    @Nullable
    private final String preferencesKey;
    private boolean requestedParentInvalidate = false;
    @Nonnull
    final String resourceName;
    @Nonnull
    private State state = State.Idle;
    @Nonnull
    private final DriveSynchronizer synchronizer;

    DriveConnectibleResource(@Nonnull Context context, @Nonnull DriveSynchronizer driveSynchronizer, @Nullable String string2, @Nullable DriveConnectibleResource driveConnectibleResource, @Nonnull GoogleApiClient googleApiClient, @Nonnull String string3) {
        this.onSyncCompleted = new DriveSynchronizer.OnSyncCompletedListener(this){
            final DriveConnectibleResource this$0;
            {
                this.this$0 = driveConnectibleResource;
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public void onSyncCompleted() {
                Debug.Printf("LumiyaCloud: '%s': sync completed", this.this$0.resourceName);
                if (this.this$0.needInvalidate && this.this$0.needRecreate) {
                    Debug.Printf("LumiyaCloud: '%s': re-creating because requested", this.this$0.resourceName);
                    DriveConnectibleResource.access$002(this.this$0, false);
                    DriveConnectibleResource.access$102(this.this$0, false);
                    this.this$0.startCreatingResource();
                    return;
                }
                boolean bl = false;
                Debug.Printf("LumiyaCloud: '%s': key %s, needInvalidate %b", this.this$0.resourceName, this.this$0.preferencesKey, this.this$0.needInvalidate);
                boolean bl2 = bl;
                if (this.this$0.preferencesKey != null) {
                    bl2 = bl;
                    if (!this.this$0.needInvalidate) {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences((Context)this.this$0.context);
                        bl2 = bl;
                        if (sharedPreferences.contains(this.this$0.preferencesKey)) {
                            String string2 = sharedPreferences.getString(this.this$0.preferencesKey, null);
                            bl2 = bl;
                            if (string2 != null) {
                                Debug.Printf("Resource '%s': has stored DriveId: %s", this.this$0.resourceName, string2);
                                try {
                                    DriveId.decodeFromString(string2).asDriveFolder().getMetadata(this.this$0.googleApiClient).setResultCallback(this.this$0.onFolderMetadata);
                                    bl2 = true;
                                }
                                catch (IllegalArgumentException illegalArgumentException) {
                                    Debug.Warning(illegalArgumentException);
                                    bl2 = bl;
                                }
                            }
                        }
                    }
                }
                DriveConnectibleResource.access$002(this.this$0, false);
                if (bl2) return;
                if (this.this$0.parentFolder != null) {
                    Debug.Printf("Resource '%s': need parent folder to search in", this.this$0.resourceName);
                    this.this$0.parentFolder.getResource(this.this$0.onParentFolderReadyForSearch);
                    return;
                }
                Debug.Printf("LumiyaCloud: '%s': searching root folder", this.this$0.resourceName);
                DriveFolder driveFolder = Drive.DriveApi.getRootFolder(this.this$0.googleApiClient);
                this.this$0.startSearching(driveFolder);
            }
        };
        this.onParentFolderReadyForSearch = new OnResourceReadyListener(this){
            final DriveConnectibleResource this$0;
            {
                this.this$0 = driveConnectibleResource;
            }

            /*
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            @Override
            public void onResourceReady(@Nullable DriveResource driveResource, @Nullable String string2) {
                if (driveResource != null && driveResource instanceof DriveFolder) {
                    driveResource = (DriveFolder)driveResource;
                    this.this$0.startSearching((DriveFolder)driveResource);
                    return;
                }
                this.this$0.invokeListeners(null, string2);
            }
        };
        this.onQueryResults = new ResultCallback<DriveApi.MetadataBufferResult>(this){
            final DriveConnectibleResource this$0;
            {
                this.this$0 = driveConnectibleResource;
            }

            @Override
            public void onResult(@Nonnull DriveApi.MetadataBufferResult object) {
                boolean bl;
                MetadataBuffer metadataBuffer;
                block2: {
                    metadataBuffer = object.getMetadataBuffer();
                    Debug.Printf("Resource '%s': got %d results.", this.this$0.resourceName, metadataBuffer.getCount());
                    boolean bl2 = false;
                    Iterator iterator = metadataBuffer.iterator();
                    do {
                        bl = bl2;
                        if (!iterator.hasNext()) break block2;
                    } while (!this.this$0.isMetadataOk((Metadata)(object = (Metadata)iterator.next())));
                    bl = true;
                    Debug.Printf("Resource '%s': found good one.", this.this$0.resourceName);
                    this.this$0.invokeListeners(((Metadata)object).getDriveId().asDriveResource(), null);
                }
                metadataBuffer.release();
                if (!bl) {
                    Debug.Printf("Resource '%s': not found good one.", this.this$0.resourceName);
                    this.this$0.startCreatingResource();
                }
            }
        };
        this.onFolderMetadata = new ResultCallback<DriveResource.MetadataResult>(this){
            final DriveConnectibleResource this$0;
            {
                this.this$0 = driveConnectibleResource;
            }

            /*
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            @Override
            public void onResult(@Nonnull DriveResource.MetadataResult object) {
                Status status = object.getStatus();
                Debug.Printf("Resource '%s': metadata received, success %b", this.this$0.resourceName, status.isSuccess());
                if (status.isSuccess()) {
                    if (!this.this$0.isMetadataOk(object.getMetadata())) {
                        Debug.Printf("Resource '%s': metadata is not ok.", this.this$0.resourceName);
                        this.this$0.startCreatingResource();
                        return;
                    }
                    Debug.Printf("Resource '%s': metadata is ok.", this.this$0.resourceName);
                    this.this$0.invokeListeners(object.getMetadata().getDriveId().asDriveResource(), null);
                    return;
                }
                if (status.hasResolution()) {
                    object = ErrorResolutionTracker.getInstance();
                    if (object == null) return;
                    ((ErrorResolutionTracker)object).addResolvableError(new ErrorResolutionTracker.ResolvableError(this.this$0.resourceName, status, new ErrorResolutionTracker.RestartableOperation(this){
                        final 4 this$1;
                        {
                            this.this$1 = var1_1;
                        }

                        @Override
                        public void tryRestartingOperation() {
                            this.this$1.this$0.startRequestingResource();
                        }
                    }));
                    return;
                }
                Debug.Printf("Resource '%s': no metadata, resource will have to be created.", this.this$0.resourceName);
                this.this$0.startCreatingResource();
            }
        };
        this.onParentFolderReady = new OnResourceReadyListener(this){
            final DriveConnectibleResource this$0;
            {
                this.this$0 = driveConnectibleResource;
            }

            /*
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            @Override
            public void onResourceReady(@Nullable DriveResource driveResource, @Nullable String string2) {
                Debug.Printf("Resource '%s': parent folder ready: %s", this.this$0.resourceName, driveResource);
                if (driveResource != null && driveResource instanceof DriveFolder) {
                    driveResource = (DriveFolder)driveResource;
                    this.this$0.createResource((DriveFolder)driveResource);
                    return;
                }
                this.this$0.invokeListeners(null, string2);
            }
        };
        this.context = context;
        this.synchronizer = driveSynchronizer;
        this.preferencesKey = string2;
        this.parentFolder = driveConnectibleResource;
        this.googleApiClient = googleApiClient;
        this.resourceName = string3;
    }

    static /* synthetic */ boolean access$002(DriveConnectibleResource driveConnectibleResource, boolean bl) {
        driveConnectibleResource.needInvalidate = bl;
        return bl;
    }

    static /* synthetic */ boolean access$102(DriveConnectibleResource driveConnectibleResource, boolean bl) {
        driveConnectibleResource.needRecreate = bl;
        return bl;
    }

    /*
     * Enabled aggressive block sorting
     */
    private void invokeListeners(DriveResource driveResource, String string2) {
        Debug.Printf("Resource '%s': calling listeners, resource %s, message %s.", this.resourceName, driveResource, string2);
        if (driveResource != null) {
            this.requestedParentInvalidate = false;
        }
        this.driveResource = driveResource;
        this.errorMessage = string2;
        this.state = this.driveResource != null ? State.Idle : State.Error;
        Object object = ImmutableList.copyOf(this.listeners);
        this.listeners.clear();
        object = ((ImmutableList)object).iterator();
        while (object.hasNext()) {
            ((OnResourceReadyListener)object.next()).onResourceReady(driveResource, string2);
        }
        return;
    }

    private void startRequestingResource() {
        Debug.Printf("Resource '%s': starting work.", this.resourceName);
        this.state = State.Working;
        if (this.needInvalidate) {
            this.synchronizer.invalidateSync();
        }
        this.synchronizer.requestSync(this.onSyncCompleted);
    }

    private void startSearching(DriveFolder driveFolder) {
        Debug.Printf("Resource '%s': starting to search", this.resourceName);
        Query query = new Query.Builder().addFilter(Filters.and(Filters.eq(SearchableField.TITLE, this.resourceName), Filters.eq(SearchableField.TRASHED, false), Filters.eq(SearchableField.MIME_TYPE, this.getMimeType()))).build();
        driveFolder.queryChildren(this.googleApiClient, query).setResultCallback(this.onQueryResults);
    }

    protected abstract void createResource(@Nonnull DriveFolder var1);

    @Nonnull
    protected abstract String getMimeType();

    /*
     * Enabled aggressive block sorting
     */
    void getResource(OnResourceReadyListener onResourceReadyListener) {
        Debug.Printf("Asked for resource '%s', state %s", new Object[]{this.resourceName, this.state});
        if (this.driveResource != null) {
            if (onResourceReadyListener == null) return;
            onResourceReadyListener.onResourceReady(this.driveResource, null);
            return;
        }
        if (this.state == State.Error) {
            if (onResourceReadyListener == null) return;
            onResourceReadyListener.onResourceReady(null, this.errorMessage);
            return;
        }
        if (onResourceReadyListener != null) {
            this.listeners.add(onResourceReadyListener);
        }
        if (this.state != State.Idle) return;
        this.startRequestingResource();
    }

    protected abstract boolean isMetadataOk(@Nonnull Metadata var1);

    void onResourceCreated(DriveResource driveResource) {
        Debug.Printf("Resource '%s': created.", this.resourceName);
        if (this.preferencesKey != null) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences((Context)this.context).edit();
            editor.putString(this.preferencesKey, driveResource.getDriveId().encodeToString());
            editor.apply();
        }
        this.invokeListeners(driveResource, null);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    void onResourceCreationFailed(String string2) {
        Debug.Printf("Resource '%s': creation failed, requestedParentInvalidate %b.", this.resourceName, this.requestedParentInvalidate);
        if (!this.requestedParentInvalidate) {
            this.requestedParentInvalidate = true;
            Debug.Printf("Resource '%s': requesting invalidate.", this.resourceName);
            this.requestInvalidate(false, true);
            this.startRequestingResource();
            return;
        }
        Debug.Printf("Resource '%s': creation failed completely.", this.resourceName);
        this.invokeListeners(null, string2);
        this.state = State.Error;
        this.errorMessage = string2;
    }

    void requestInvalidate(boolean bl, boolean bl2) {
        Debug.Printf("LumiyaCloud: invalidate requested for '%s', recreate %b, parents %b", this.resourceName, bl, bl2);
        this.needInvalidate = true;
        this.needRecreate |= bl;
        if (this.state == State.Error) {
            this.state = State.Idle;
        }
        if (this.state == State.Idle) {
            this.driveResource = null;
        }
        if (bl2 && this.parentFolder != null) {
            this.parentFolder.requestInvalidate(false, true);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    void startCreatingResource() {
        if (this.parentFolder != null) {
            Debug.Printf("Resource '%s': asking for parent folder", this.resourceName);
            this.parentFolder.getResource(this.onParentFolderReady);
            return;
        }
        Debug.Printf("Resource '%s': creating resource in root folder.", this.resourceName);
        this.createResource(Drive.DriveApi.getRootFolder(this.googleApiClient));
    }

    static interface OnResourceReadyListener {
        public void onResourceReady(@Nullable DriveResource var1, @Nullable String var2);
    }

    private static enum State {
        Idle,
        Working,
        Error;

    }
}

