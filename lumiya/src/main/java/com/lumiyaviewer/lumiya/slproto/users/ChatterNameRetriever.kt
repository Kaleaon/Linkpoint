package com.lumiyaviewer.lumiya.slproto.users

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.GlobalOptions
import com.lumiyaviewer.lumiya.dao.UserName
import com.lumiyaviewer.lumiya.react.Subscription
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo
import java.lang.ref.WeakReference
import java.util.concurrent.Executor

class ChatterNameRetriever(
    val chatterID: ChatterID,
    onChatterNameUpdated: OnChatterNameUpdated,
    private val executor: Executor?,
    autoSubscribe: Boolean = true
) {
    private val listener = WeakReference(onChatterNameUpdated)
    @Volatile var resolvedName: String? = null
        private set
    @Volatile var resolvedSecondaryName: String? = null
        private set
    private var subscription: Subscription? = null

    interface OnChatterNameUpdated {
        fun onChatterNameUpdated(chatterNameRetriever: ChatterNameRetriever)
    }

    init {
        if (autoSubscribe) {
            subscribe()
        }
    }
    
    constructor(chatterID: ChatterID, onChatterNameUpdated: OnChatterNameUpdated, executor: Executor?) : this(chatterID, onChatterNameUpdated, executor, true)

    private fun onCurrentLocation(currentLocationInfo: CurrentLocationInfo) {
        // Stub implementation
        val name = "Current Location" // Placeholder
        if (resolvedName != name) {
            resolvedName = name
            listener.get()?.onChatterNameUpdated(this)
        }
    }
    
    private fun onGroupProfile(groupProfileReply: GroupProfileReply) {
        val name = groupProfileReply.GroupData_Field?.Name?.let { String(it) }
        resolvedName = name
        resolvedSecondaryName = name
        listener.get()?.onChatterNameUpdated(this)
    }
    
    private fun onUserName(userName: UserName) {
        Debug.Log("Resolved name for ${userName.uuid}")
        if (GlobalOptions.getInstance().isLegacyUserNames) {
            resolvedName = userName.userName
            resolvedSecondaryName = userName.displayName
        } else {
            resolvedName = userName.displayName
            resolvedSecondaryName = userName.userName
        }
        listener.get()?.onChatterNameUpdated(this)
    }

    fun dispose() {
        subscription?.unsubscribe()
    }

    fun subscribe() {
        // Stub implementation
        // Real implementation requires dependency chains not fully available/decompiled
    }
    
    fun getResolvedName(): String? = resolvedName
    
    fun getResolvedSecondaryName(): String? = resolvedSecondaryName
}
