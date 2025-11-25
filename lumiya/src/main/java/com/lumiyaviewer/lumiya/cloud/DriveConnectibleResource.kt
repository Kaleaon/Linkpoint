package com.lumiyaviewer.lumiya.cloud

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.drive.*
import com.google.android.gms.drive.query.Filters
import com.google.android.gms.drive.query.Query
import com.google.android.gms.drive.query.SearchableField
import java.util.concurrent.CopyOnWriteArrayList

abstract class DriveConnectibleResource(
    protected val context: Context,
    protected val synchronizer: DriveSynchronizer,
    private val preferencesKey: String?,
    private val parentFolder: DriveConnectibleResource?,
    protected val googleApiClient: GoogleApiClient,
    val resourceName: String
) {

    enum class State {
        Idle, Working, Error
    }

    interface OnResourceReadyListener {
        fun onResourceReady(resource: DriveResource?, errorMessage: String?)
    }

    private var driveResource: DriveResource? = null
    private var errorMessage: String? = null
    private var state = State.Idle
    private val listeners = CopyOnWriteArrayList<OnResourceReadyListener>()
    private var needInvalidate = false
    private var needRecreate = false
    private var requestedParentInvalidate = false

    private val onSyncCompleted = object : DriveSynchronizer.OnSyncCompletedListener {
        override fun onSyncCompleted() {
            Debug.Printf("LumiyaCloud: '%s': sync completed", resourceName)
            
            if (needInvalidate && needRecreate) {
                Debug.Printf("LumiyaCloud: '%s': re-creating because requested", resourceName)
                needInvalidate = false
                needRecreate = false
                startCreatingResource()
                return
            }
            
            var foundInPrefs = false
            Debug.Printf("LumiyaCloud: '%s': key %s, needInvalidate %b", resourceName, preferencesKey, needInvalidate)
            
            if (preferencesKey != null && !needInvalidate) {
                val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                if (prefs.contains(preferencesKey)) {
                    val encodedId = prefs.getString(preferencesKey, null)
                    if (encodedId != null) {
                        Debug.Printf("Resource '%s': has stored DriveId: %s", resourceName, encodedId)
                        try {
                            val driveId = DriveId.decodeFromString(encodedId)
                            // Ensure we handle null or invalid state if asDriveFolder fails
                            driveId.asDriveFolder().getMetadata(googleApiClient)
                                .setResultCallback(onFolderMetadata)
                            foundInPrefs = true
                        } catch (e: Exception) {
                            Debug.Warning(e)
                        }
                    }
                }
            }
            
            needInvalidate = false
            
            if (foundInPrefs) return
            
            if (parentFolder != null) {
                Debug.Printf("Resource '%s': need parent folder to search in", resourceName)
                parentFolder.getResource(onParentFolderReadyForSearch)
                return
            }
            
            Debug.Printf("LumiyaCloud: '%s': searching root folder", resourceName)
            val rootFolder = Drive.DriveApi.getRootFolder(googleApiClient)
            if (rootFolder != null) {
                startSearching(rootFolder)
            } else {
                onResourceCreationFailed("Root folder is unavailable")
            }
        }
    }

    private val onParentFolderReadyForSearch = object : OnResourceReadyListener {
        override fun onResourceReady(resource: DriveResource?, error: String?) {
            if (resource is DriveFolder) {
                startSearching(resource)
            } else {
                invokeListeners(null, error)
            }
        }
    }

    private val onQueryResults = ResultCallback<DriveApi.MetadataBufferResult> { result ->
        val metadataBuffer = result.metadataBuffer
        Debug.Printf("Resource '%s': got %d results.", resourceName, metadataBuffer.count)
        
        var foundMetadata: Metadata? = null
        for (metadata in metadataBuffer) {
            if (isMetadataOk(metadata)) {
                foundMetadata = metadata
                break // Found a good one
            }
        }
        
        if (foundMetadata != null) {
            Debug.Printf("Resource '%s': found good one.", resourceName)
            invokeListeners(foundMetadata.driveId.asDriveResource(), null)
        } else {
            Debug.Printf("Resource '%s': not found good one.", resourceName)
            startCreatingResource()
        }
        
        metadataBuffer.release()
    }

    private val onFolderMetadata = ResultCallback<DriveResource.MetadataResult> { result ->
        val status = result.status
        Debug.Printf("Resource '%s': metadata received, success %b", resourceName, status.isSuccess)
        
        if (status.isSuccess) {
            val metadata = result.metadata
            if (metadata != null && !isMetadataOk(metadata)) {
                Debug.Printf("Resource '%s': metadata is not ok.", resourceName)
                startCreatingResource()
                return@ResultCallback
            }
            Debug.Printf("Resource '%s': metadata is ok.", resourceName)
            invokeListeners(metadata?.driveId?.asDriveResource(), null)
            return@ResultCallback
        }
        
        if (status.hasResolution()) {
            ErrorResolutionTracker.getInstance()?.addResolvableError(
                ErrorResolutionTracker.ResolvableError(
                    resourceName,
                    status,
                    object : ErrorResolutionTracker.RestartableOperation {
                        override fun tryRestartingOperation() {
                            startRequestingResource()
                        }
                    }
                )
            )
            return@ResultCallback
        }
        
        Debug.Printf("Resource '%s': no metadata, resource will have to be created.", resourceName)
        startCreatingResource()
    }

    private val onParentFolderReady = object : OnResourceReadyListener {
        override fun onResourceReady(resource: DriveResource?, error: String?) {
            Debug.Printf("Resource '%s': parent folder ready: %s", resourceName, resource)
            if (resource is DriveFolder) {
                createResource(resource)
            } else {
                invokeListeners(null, error)
            }
        }
    }

    protected abstract fun createResource(folder: DriveFolder)
    protected abstract fun getMimeType(): String
    protected abstract fun isMetadataOk(metadata: Metadata): Boolean

    fun getResource(listener: OnResourceReadyListener?) {
        Debug.Printf("Asked for resource '%s', state %s", arrayOf(resourceName, state))
        
        if (driveResource != null) {
            listener?.onResourceReady(driveResource, null)
            return
        }
        
        if (state == State.Error) {
            listener?.onResourceReady(null, errorMessage)
            return
        }
        
        if (listener != null) {
            listeners.add(listener)
        }
        
        if (state == State.Idle) {
            startRequestingResource()
        }
    }

    protected fun onResourceCreated(resource: DriveResource) {
        Debug.Printf("Resource '%s': created.", resourceName)
        if (preferencesKey != null) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            prefs.edit()
                .putString(preferencesKey, resource.driveId.encodeToString())
                .apply()
        }
        invokeListeners(resource, null)
    }

    protected fun onResourceCreationFailed(error: String?) {
        Debug.Printf("Resource '%s': creation failed, requestedParentInvalidate %b.", resourceName, requestedParentInvalidate)
        
        if (!requestedParentInvalidate) {
            requestedParentInvalidate = true
            Debug.Printf("Resource '%s': requesting invalidate.", resourceName)
            requestInvalidate(recreate = false, parents = true)
            startRequestingResource()
            return
        }
        
        Debug.Printf("Resource '%s': creation failed completely.", resourceName)
        invokeListeners(null, error)
        state = State.Error
        errorMessage = error
    }

    fun requestInvalidate(recreate: Boolean, parents: Boolean) {
        Debug.Printf("LumiyaCloud: invalidate requested for '%s', recreate %b, parents %b", resourceName, recreate, parents)
        needInvalidate = true
        if (recreate) needRecreate = true
        
        if (state == State.Error) {
            state = State.Idle
        }
        if (state == State.Idle) {
            driveResource = null
        }
        
        if (parents) {
            parentFolder?.requestInvalidate(recreate = false, parents = true)
        }
    }

    protected fun startRequestingResource() {
        Debug.Printf("Resource '%s': starting work.", resourceName)
        state = State.Working
        if (needInvalidate) {
            synchronizer.invalidateSync()
        }
        synchronizer.requestSync(onSyncCompleted)
    }

    protected fun startCreatingResource() {
        if (parentFolder != null) {
            Debug.Printf("Resource '%s': asking for parent folder", resourceName)
            parentFolder.getResource(onParentFolderReady)
            return
        }
        Debug.Printf("Resource '%s': creating resource in root folder.", resourceName)
        val rootFolder = Drive.DriveApi.getRootFolder(googleApiClient)
        if (rootFolder != null) {
            createResource(rootFolder)
        } else {
            onResourceCreationFailed("Root folder is unavailable")
        }
    }

    private fun startSearching(folder: DriveFolder) {
        Debug.Printf("Resource '%s': starting to search", resourceName)
        val query = Query.Builder()
            .addFilter(
                Filters.and(
                    Filters.eq(SearchableField.TITLE, resourceName),
                    Filters.eq(SearchableField.TRASHED, false),
                    Filters.eq(SearchableField.MIME_TYPE, getMimeType())
                )
            )
            .build()
        
        folder.queryChildren(googleApiClient, query).setResultCallback(onQueryResults)
    }

    private fun invokeListeners(resource: DriveResource?, error: String?) {
        Debug.Printf("Resource '%s': calling listeners, resource %s, message %s.", resourceName, resource, error)
        
        if (resource != null) {
            requestedParentInvalidate = false
        }
        
        driveResource = resource
        errorMessage = error
        state = if (driveResource != null) State.Idle else State.Error
        
        val currentListeners = ArrayList(listeners)
        listeners.clear()
        
        for (listener in currentListeners) {
            listener.onResourceReady(resource, error)
        }
    }
}
