package com.linkpoint.cloud

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.drive.Drive
import com.google.android.gms.drive.DriveApi
import com.google.android.gms.drive.DriveFolder
import com.google.android.gms.drive.DriveId
import com.google.android.gms.drive.DriveResource
import com.google.android.gms.drive.Metadata
import com.google.android.gms.drive.MetadataBuffer
import com.google.android.gms.drive.query.Filters
import com.google.android.gms.drive.query.Query
import com.google.android.gms.drive.query.SearchableField
import com.google.common.collect.ImmutableList
import com.linkpoint.cloud.Debug
import com.linkpoint.cloud.DriveSynchronizer
import com.linkpoint.cloud.ErrorResolutionTracker
import java.util.HashSet
import java.util.Iterator
import java.util.Set
import javax.annotation.Nonnull
import javax.annotation.Nullable

abstract class DriveConnectibleResource {
    private val Context context
    private DriveResource driveResource
    private String errorMessage
    final GoogleApiClient googleApiClient
    private val Set<OnResourceReadyListener> listeners = HashSet<OnResourceReadyListener>()
    private Boolean needInvalidate = false
    private Boolean needRecreate = false
    private val ResultCallback<? super DriveResource.MetadataResult> onFolderMetadata
    private val OnResourceReadyListener onParentFolderReady
    private val OnResourceReadyListener onParentFolderReadyForSearch
    private val ResultCallback<? super DriveApi.MetadataBufferResult> onQueryResults
    private val DriveSynchronizer.OnSyncCompletedListener onSyncCompleted
    private val DriveConnectibleResource parentFolder
    private val String preferencesKey
    private Boolean requestedParentInvalidate = false
    final String resourceName
    private State state = State.Idle
    private val DriveSynchronizer synchronizer

    DriveConnectibleResource(Context context, DriveSynchronizer driveSynchronizer, String string2, DriveConnectibleResource driveConnectibleResource, GoogleApiClient googleApiClient, String string3) {
        this.onSyncCompleted = DriveSynchronizer.OnSyncCompletedListener(this){
            final DriveConnectibleResource this$0
            {
                this.this$0 = driveConnectibleResource
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            override Unit onSyncCompleted() {
                Debug.Printf("LinkpointCloud: '%s': sync completed", this.this$0.resourceName)
                if (this.this$0.needInvalidate && this.this$0.needRecreate) {
                    Debug.Printf("LinkpointCloud: '%s': re-creating because requested", this.this$0.resourceName)
                    DriveConnectibleResource.access$002(this.this$0, false)
                    DriveConnectibleResource.access$102(this.this$0, false)
                    this.this$0.startCreatingResource()
                    return
                }
                Boolean bl = false
                Debug.Printf("LinkpointCloud: '%s': key %s, needInvalidate %b", this.this$0.resourceName, this.this$0.preferencesKey, this.this$0.needInvalidate)
                Boolean bl2 = bl
                if (this.this$0.preferencesKey != null) {
                    bl2 = bl
                    if (!this.this$0.needInvalidate) {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences((Context)this.this$0.context)
                        bl2 = bl
                        if (sharedPreferences.contains(this.this$0.preferencesKey)) {
                            String string2 = sharedPreferences.getString(this.this$0.preferencesKey, null)
                            bl2 = bl
                            if (string2 != null) {
                                Debug.Printf("Resource '%s': has stored DriveId: %s", this.this$0.resourceName, string2)
                                try {
                                    DriveId.decodeFromString(string2).asDriveFolder().getMetadata(this.this$0.googleApiClient).setResultCallback(this.this$0.onFolderMetadata)
                                    bl2 = true
                                }
                                catch (IllegalArgumentException illegalArgumentException) {
                                    Debug.Warning(illegalArgumentException)
                                    bl2 = bl
                                }
                            }
                        }
                    }
                }
                DriveConnectibleResource.access$002(this.this$0, false)
                if (bl2) return
                if (this.this$0.parentFolder != null) {
                    Debug.Printf("Resource '%s': need parent folder to search in", this.this$0.resourceName)
                    this.this$0.parentFolder.getResource(this.this$0.onParentFolderReadyForSearch)
                    return
                }
                Debug.Printf("LinkpointCloud: '%s': searching root folder", this.this$0.resourceName)
                DriveFolder driveFolder = Drive.DriveApi.getRootFolder(this.this$0.googleApiClient)
                this.this$0.startSearching(driveFolder)
            }
        }
        this.onParentFolderReadyForSearch = OnResourceReadyListener(this){
            final DriveConnectibleResource this$0
            {
                this.this$0 = driveConnectibleResource
            }

            /*
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            override Unit onResourceReady(DriveResource driveResource, String string2) {
                if (driveResource != null && driveResource instanceof DriveFolder) {
                    driveResource = (DriveFolder)driveResource
                    this.this$0.startSearching((DriveFolder)driveResource)
                    return
                }
                this.this$0.invokeListeners(null, string2)
            }
        }
        this.onQueryResults = ResultCallback<DriveApi.MetadataBufferResult>(this){
            final DriveConnectibleResource this$0
            {
                this.this$0 = driveConnectibleResource
            }

            override Unit onResult(DriveApi.MetadataBufferResult object) {
                Boolean bl
                MetadataBuffer metadataBuffer
                block2: {
                    metadataBuffer = object.getMetadataBuffer()
                    Debug.Printf("Resource '%s': got %d results.", this.this$0.resourceName, metadataBuffer.getCount())
                    Boolean bl2 = false
                    Iterator iterator = metadataBuffer.iterator()
                    do {
                        bl = bl2
                        if (!iterator.hasNext()) break block2
                    } while (!this.this$0.isMetadataOk((Metadata)(object = (Metadata)iterator.next())))
                    bl = true
                    Debug.Printf("Resource '%s': found good one.", this.this$0.resourceName)
                    this.this$0.invokeListeners(((Metadata)object).getDriveId().asDriveResource(), null)
                }
                metadataBuffer.release()
                if (!bl) {
                    Debug.Printf("Resource '%s': not found good one.", this.this$0.resourceName)
                    this.this$0.startCreatingResource()
                }
            }
        }
        this.onFolderMetadata = ResultCallback<DriveResource.MetadataResult>(this){
            final DriveConnectibleResource this$0
            {
                this.this$0 = driveConnectibleResource
            }

            /*
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            override Unit onResult(DriveResource.MetadataResult object) {
                Status status = object.getStatus()
                Debug.Printf("Resource '%s': metadata received, success %b", this.this$0.resourceName, status.isSuccess())
                if (status.isSuccess()) {
                    if (!this.this$0.isMetadataOk(object.getMetadata())) {
                        Debug.Printf("Resource '%s': metadata is not ok.", this.this$0.resourceName)
                        this.this$0.startCreatingResource()
                        return
                    }
                    Debug.Printf("Resource '%s': metadata is ok.", this.this$0.resourceName)
                    this.this$0.invokeListeners(object.getMetadata().getDriveId().asDriveResource(), null)
                    return
                }
                if (status.hasResolution()) {
                    object = ErrorResolutionTracker.getInstance()
                    if (object == null) return
                    ((ErrorResolutionTracker)object).addResolvableError(ErrorResolutionTracker.ResolvableError(this.this$0.resourceName, status, ErrorResolutionTracker.RestartableOperation(this){
                        final 4 this$1
                        {
                            this.this$1 = var1_1
                        }

                        override Unit tryRestartingOperation() {
                            this.this$1.this$0.startRequestingResource()
                        }
                    }))
                    return
                }
                Debug.Printf("Resource '%s': no metadata, resource will have to be created.", this.this$0.resourceName)
                this.this$0.startCreatingResource()
            }
        }
        this.onParentFolderReady = OnResourceReadyListener(this){
            final DriveConnectibleResource this$0
            {
                this.this$0 = driveConnectibleResource
            }

            /*
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            override Unit onResourceReady(DriveResource driveResource, String string2) {
                Debug.Printf("Resource '%s': parent folder ready: %s", this.this$0.resourceName, driveResource)
                if (driveResource != null && driveResource instanceof DriveFolder) {
                    driveResource = (DriveFolder)driveResource
                    this.this$0.createResource((DriveFolder)driveResource)
                    return
                }
                this.this$0.invokeListeners(null, string2)
            }
        }
        this.context = context
        this.synchronizer = driveSynchronizer
        this.preferencesKey = string2
        this.parentFolder = driveConnectibleResource
        this.googleApiClient = googleApiClient
        this.resourceName = string3
    }

    static /* synthetic */ Boolean access$002(DriveConnectibleResource driveConnectibleResource, Boolean bl) {
        driveConnectibleResource.needInvalidate = bl
        return bl
    }

    static /* synthetic */ Boolean access$102(DriveConnectibleResource driveConnectibleResource, Boolean bl) {
        driveConnectibleResource.needRecreate = bl
        return bl
    }

    /*
     * Enabled aggressive block sorting
     */
    private Unit invokeListeners(DriveResource driveResource, String string2) {
        Debug.Printf("Resource '%s': calling listeners, resource %s, message %s.", this.resourceName, driveResource, string2)
        if (driveResource != null) {
            this.requestedParentInvalidate = false
        }
        this.driveResource = driveResource
        this.errorMessage = string2
        this.state = this.driveResource != null ? State.Idle : State.Error
        Object object = ImmutableList.copyOf(this.listeners)
        this.listeners.clear()
        object = ((ImmutableList)object).iterator()
        while (object.hasNext()) {
            ((OnResourceReadyListener)object.next()).onResourceReady(driveResource, string2)
        }
        return
    }

    private Unit startRequestingResource() {
        Debug.Printf("Resource '%s': starting work.", this.resourceName)
        this.state = State.Working
        if (this.needInvalidate) {
            this.synchronizer.invalidateSync()
        }
        this.synchronizer.requestSync(this.onSyncCompleted)
    }

    private Unit startSearching(DriveFolder driveFolder) {
        Debug.Printf("Resource '%s': starting to search", this.resourceName)
        Query query = Query.Builder().addFilter(Filters.and(Filters.eq(SearchableField.TITLE, this.resourceName), Filters.eq(SearchableField.TRASHED, false), Filters.eq(SearchableField.MIME_TYPE, this.getMimeType()))).build()
        driveFolder.queryChildren(this.googleApiClient, query).setResultCallback(this.onQueryResults)
    }

    protected abstract Unit createResource(DriveFolder var1)

    protected abstract String getMimeType()

    /*
     * Enabled aggressive block sorting
     */
    Unit getResource(OnResourceReadyListener onResourceReadyListener) {
        Debug.Printf("Asked for resource '%s', state %s", Object[]{this.resourceName, this.state})
        if (this.driveResource != null) {
            if (onResourceReadyListener == null) return
            onResourceReadyListener.onResourceReady(this.driveResource, null)
            return
        }
        if (this.state == State.Error) {
            if (onResourceReadyListener == null) return
            onResourceReadyListener.onResourceReady(null, this.errorMessage)
            return
        }
        if (onResourceReadyListener != null) {
            this.listeners.add(onResourceReadyListener)
        }
        if (this.state != State.Idle) return
        this.startRequestingResource()
    }

    protected abstract Boolean isMetadataOk(Metadata var1)

    Unit onResourceCreated(DriveResource driveResource) {
        Debug.Printf("Resource '%s': created.", this.resourceName)
        if (this.preferencesKey != null) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences((Context)this.context).edit()
            editor.putString(this.preferencesKey, driveResource.getDriveId().encodeToString())
            editor.apply()
        }
        this.invokeListeners(driveResource, null)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    Unit onResourceCreationFailed(String string2) {
        Debug.Printf("Resource '%s': creation failed, requestedParentInvalidate %b.", this.resourceName, this.requestedParentInvalidate)
        if (!this.requestedParentInvalidate) {
            this.requestedParentInvalidate = true
            Debug.Printf("Resource '%s': requesting invalidate.", this.resourceName)
            this.requestInvalidate(false, true)
            this.startRequestingResource()
            return
        }
        Debug.Printf("Resource '%s': creation failed completely.", this.resourceName)
        this.invokeListeners(null, string2)
        this.state = State.Error
        this.errorMessage = string2
    }

    Unit requestInvalidate(Boolean bl, Boolean bl2) {
        Debug.Printf("LinkpointCloud: invalidate requested for '%s', recreate %b, parents %b", this.resourceName, bl, bl2)
        this.needInvalidate = true
        this.needRecreate |= bl
        if (this.state == State.Error) {
            this.state = State.Idle
        }
        if (this.state == State.Idle) {
            this.driveResource = null
        }
        if (bl2 && this.parentFolder != null) {
            this.parentFolder.requestInvalidate(false, true)
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    Unit startCreatingResource() {
        if (this.parentFolder != null) {
            Debug.Printf("Resource '%s': asking for parent folder", this.resourceName)
            this.parentFolder.getResource(this.onParentFolderReady)
            return
        }
        Debug.Printf("Resource '%s': creating resource in root folder.", this.resourceName)
        this.createResource(Drive.DriveApi.getRootFolder(this.googleApiClient))
    }

    static interface OnResourceReadyListener {
        fun onResourceReady(DriveResource var1, String var2)
    }

    @JvmStatic
private enum State {
        Idle,
        Working,
        Error

    }
}

