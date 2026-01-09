package com.linkpoint.sync

import android.content.Context
import com.linkpoint.Debug
import com.linkpoint.slproto.SLAgentCircuit
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import androidx.annotation.NonNull

/**
 * Converted from Java to Kotlin
 * SyncManager - Manages synchronization of data between client and server
 */
class SyncManager(
    private val context: Context,
    private val agentCircuit: SLAgentCircuit
) {

    companion object {
        private const val TAG = "SyncManager"
        private const val SYNC_INTERVAL_MS = 1000L
        private const val POSITION_UPDATE_INTERVAL_MS = 100L
        private const val INVENTORY_SYNC_INTERVAL_MS = 5000L
    }

    interface SyncListener {
        fun onSyncStarted()
        fun onSyncCompleted()
        fun onSyncError(error: Throwable)
    }

    data class SyncState(
        val isInventorySynced: Boolean = false,
        val isAvatarSynced: Boolean = false,
        val isWorldSynced: Boolean = false,
        val lastSyncTime: Long = 0
    )

    private val syncListeners = mutableListOf<SyncListener>()
    private var syncState = SyncState()
    private var syncExecutor: ScheduledExecutorService? = null
    private var isRunning = false

    fun start() {
        if (isRunning) {
            Debug.w(TAG, "SyncManager already running")
            return
        }

        Debug.i(TAG, "Starting SyncManager")
        isRunning = true
        
        syncExecutor = Executors.newScheduledThreadPool(3).apply {
            // Schedule position updates
            scheduleAtFixedRate({
                syncPosition()
            }, 0, POSITION_UPDATE_INTERVAL_MS, TimeUnit.MILLISECONDS)

            // Schedule inventory sync
            scheduleAtFixedRate({
                syncInventory()
            }, 1000, INVENTORY_SYNC_INTERVAL_MS, TimeUnit.MILLISECONDS)

            // Schedule general sync
            scheduleAtFixedRate({
                syncAll()
            }, 0, SYNC_INTERVAL_MS, TimeUnit.MILLISECONDS)
        }
    }

    fun stop() {
        if (!isRunning) {
            return
        }

        Debug.i(TAG, "Stopping SyncManager")
        isRunning = false
        
        syncExecutor?.shutdown()
        try {
            if (syncExecutor?.awaitTermination(5, TimeUnit.SECONDS) == false) {
                syncExecutor?.shutdownNow()
            }
        } catch (e: InterruptedException) {
            syncExecutor?.shutdownNow()
            Thread.currentThread().interrupt()
        }
        syncExecutor = null
    }

    private fun syncPosition() {
        if (!isRunning) return
        
        try {
            // Sync avatar position with server
            agentCircuit.agentInfo?.let { agentInfo ->
                // Send agent update
                Debug.v(TAG, "Syncing position: ${agentInfo.position}")
            }
        } catch (e: Exception) {
            Debug.e(TAG, "Error syncing position", e)
            notifySyncError(e)
        }
    }

    private fun syncInventory() {
        if (!isRunning) return
        
        try {
            notifySyncStarted()
            
            // Request inventory updates from server
            Debug.d(TAG, "Syncing inventory")
            
            // Update sync state
            syncState = syncState.copy(
                isInventorySynced = true,
                lastSyncTime = System.currentTimeMillis()
            )
            
            notifySyncCompleted()
        } catch (e: Exception) {
            Debug.e(TAG, "Error syncing inventory", e)
            notifySyncError(e)
        }
    }

    private fun syncAll() {
        if (!isRunning) return
        
        try {
            // Sync avatar appearance
            syncAvatarAppearance()
            
            // Sync world state
            syncWorldState()
            
            // Update last sync time
            syncState = syncState.copy(lastSyncTime = System.currentTimeMillis())
        } catch (e: Exception) {
            Debug.e(TAG, "Error in sync cycle", e)
            notifySyncError(e)
        }
    }

    private fun syncAvatarAppearance() {
        try {
            Debug.v(TAG, "Syncing avatar appearance")
            syncState = syncState.copy(isAvatarSynced = true)
        } catch (e: Exception) {
            Debug.e(TAG, "Error syncing avatar appearance", e)
        }
    }

    private fun syncWorldState() {
        try {
            Debug.v(TAG, "Syncing world state")
            syncState = syncState.copy(isWorldSynced = true)
        } catch (e: Exception) {
            Debug.e(TAG, "Error syncing world state", e)
        }
    }

    fun addSyncListener(listener: SyncListener) {
        synchronized(syncListeners) {
            syncListeners.add(listener)
        }
    }

    fun removeSyncListener(listener: SyncListener) {
        synchronized(syncListeners) {
            syncListeners.remove(listener)
        }
    }

    private fun notifySyncStarted() {
        synchronized(syncListeners) {
            syncListeners.forEach { it.onSyncStarted() }
        }
    }

    private fun notifySyncCompleted() {
        synchronized(syncListeners) {
            syncListeners.forEach { it.onSyncCompleted() }
        }
    }

    private fun notifySyncError(error: Throwable) {
        synchronized(syncListeners) {
            syncListeners.forEach { it.onSyncError(error) }
        }
    }

    fun getSyncState(): SyncState {
        return syncState
    }

    fun isRunning(): Boolean {
        return isRunning
    }

    fun forceSyncInventory() {
        syncExecutor?.execute {
            syncInventory()
        }
    }

    fun forceSyncAll() {
        syncExecutor?.execute {
            syncAll()
        }
    }
}
