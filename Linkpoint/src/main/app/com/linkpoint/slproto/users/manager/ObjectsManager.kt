package com.linkpoint.slproto.users.manager

import androidx.annotation.Nullable
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSet
import com.linkpoint.Debug
import com.linkpoint.react.RequestSource
import com.linkpoint.react.SimpleRequestHandler
import com.linkpoint.react.Subscribable
import com.linkpoint.react.SubscriptionPool
import com.linkpoint.react.SubscriptionSingleDataPool
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLGridConnection
import com.linkpoint.slproto.SLParcelInfo
import com.linkpoint.slproto.inventory.SLTaskInventory
import com.linkpoint.slproto.objects.SLObjectDisplayInfo
import com.linkpoint.slproto.objects.SLObjectFilterInfo
import com.linkpoint.slproto.objects.SLObjectInfo
import com.linkpoint.slproto.objects.SLObjectProfileData
import com.linkpoint.slproto.users.MultipleChatterNameRetriever
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference

class ObjectsManager(private val userManager: UserManager) {
    private var filterInfo: SLObjectFilterInfo = SLObjectFilterInfo.create()
    private val filterLock = Any()
    
    private val myAvatarStatePool = SubscriptionPool<SubscriptionSingleKey, MyAvatarState>()
    
    private val onChatterNameUpdated = MultipleChatterNameRetriever.OnChatterNameUpdated {
        requestObjectListUpdate()
    }
    
    // TODO: Check if executor needs to be provided
    private val nameRetriever = MultipleChatterNameRetriever(userManager.getUserID(), onChatterNameUpdated, null)
    
    private val objectDisplayListPool = SubscriptionPool<SubscriptionSingleKey, ObjectDisplayList>()
    private val objectProfilePool = SubscriptionPool<Int, SLObjectProfileData>()
    
    private val objectProfileRequestHandler = object : SimpleRequestHandler<Int>() {
        override fun onRequest(localId: Int) {
            val activeAgentCircuit = userManager.getActiveAgentCircuit()
            if (activeAgentCircuit != null) {
                activeAgentCircuit.execute {
                    val objectProfile = activeAgentCircuit.getObjectProfile(localId)
                    if (objectProfile != null) {
                        objectProfilePool.onResultData(localId, objectProfile)
                    } else {
                        objectProfilePool.onResultError(localId, ObjectDoesNotExistException(localId))
                    }
                }
            } else {
                objectProfilePool.onResultError(localId, SLGridConnection.NotConnectedException())
            }
        }
    }

    private val parcelInfo = AtomicReference<SLParcelInfo?>(null)
    private val runningAnimationsPool = SubscriptionSingleDataPool<ImmutableSet<UUID>>()
    private val taskInventoryPool = SubscriptionPool<Int, SLTaskInventory>()
    private val touchableObjectsPool = SubscriptionPool<UUID, ImmutableList<SLObjectInfo>>()
    
    private val touchableObjectsRequestHandler = object : SimpleRequestHandler<UUID>() {
        override fun onRequest(uuid: UUID) {
            val activeAgentCircuit = userManager.getActiveAgentCircuit()
            if (activeAgentCircuit != null) {
                activeAgentCircuit.execute {
                    // Assuming getGridConnection().parcelInfo is accessible and correct
                    val parcelInfo = activeAgentCircuit.getGridConnection().parcelInfo
                    // getUserTouchableObjects might throw or return null, handled here
                    val userTouchableObjects = parcelInfo.getUserTouchableObjects(activeAgentCircuit, uuid)
                    if (userTouchableObjects != null) {
                        touchableObjectsPool.onResultData(uuid, userTouchableObjects)
                    } else {
                        touchableObjectsPool.onResultError(uuid, ObjectDoesNotExistException(uuid))
                    }
                }
            } else {
                touchableObjectsPool.onResultError(uuid, SLGridConnection.NotConnectedException())
            }
        }
    }

    private val updateObjectListRunnable = Runnable {
        val currentFilter: SLObjectFilterInfo
        synchronized(filterLock) {
            currentFilter = filterInfo
        }
        
        val activeAgentCircuit = userManager.getActiveAgentCircuit()
        val agentPosition = activeAgentCircuit?.getModules()?.avatarControl?.getAgentPosition()
        val immutablePosition = agentPosition?.getImmutablePosition()
        val currentParcelInfo = parcelInfo.get()
        
        Debug.Printf("ObjectList: updating object list, parcelInfo %s, agentPosVector %s", currentParcelInfo, immutablePosition)
        
        val result = if (currentParcelInfo == null || immutablePosition == null) {
            ObjectDisplayList(ImmutableList.of(), false)
        } else {
            currentParcelInfo.getDisplayObjects(immutablePosition, currentFilter, nameRetriever)
        }
        
        objectDisplayListPool.onResultData(SubscriptionSingleKey.Value, result)
    }

    private val updateRequestHandler = object : SimpleRequestHandler<SubscriptionSingleKey>() {
        override fun onRequest(key: SubscriptionSingleKey) {
            val activeAgentCircuit = userManager.getActiveAgentCircuit()
            if (activeAgentCircuit != null) {
                activeAgentCircuit.execute(updateObjectListRunnable)
            } else {
                objectDisplayListPool.onResultError(SubscriptionSingleKey.Value, SLGridConnection.NotConnectedException())
            }
        }

        override fun onRequestCancelled(key: SubscriptionSingleKey) {
            nameRetriever.clearChatters()
        }
    }

    class ObjectDisplayList(
        val objects: ImmutableList<SLObjectDisplayInfo>,
        val isLoading: Boolean
    )

    class ObjectDoesNotExistException : Exception {
        val localID: Int
        val uuid: UUID?

        constructor(localID: Int) : super("Object with localID $localID does not exist") {
            this.localID = localID
            this.uuid = null
        }

        constructor(uuid: UUID) : super("Object with UUID $uuid does not exist") {
            this.localID = 0
            this.uuid = uuid
        }
    }

    init {
        objectDisplayListPool.attachRequestHandler(updateRequestHandler)
        objectProfilePool.attachRequestHandler(objectProfileRequestHandler)
        touchableObjectsPool.attachRequestHandler(touchableObjectsRequestHandler)
    }

    fun clearParcelInfo(oldInfo: SLParcelInfo?) {
        parcelInfo.compareAndSet(oldInfo, null)
    }

    fun getObjectDisplayList(): Subscribable<SubscriptionSingleKey, ObjectDisplayList> {
        return objectDisplayListPool
    }

    fun getObjectProfile(): Subscribable<Int, SLObjectProfileData> {
        return objectProfilePool
    }

    fun getObjectTaskInventory(): Subscribable<Int, SLTaskInventory> {
        return taskInventoryPool
    }

    fun getTaskInventoryRequestSource(): RequestSource<Int, SLTaskInventory> {
        return taskInventoryPool
    }

    fun myAvatarState(): SubscriptionPool<SubscriptionSingleKey, MyAvatarState> {
        return myAvatarStatePool
    }

    fun requestObjectListUpdate() {
        objectDisplayListPool.requestUpdate(SubscriptionSingleKey.Value)
    }

    fun requestObjectProfileUpdate(localID: Int) {
        objectProfilePool.requestUpdate(localID)
    }

    fun requestTaskInventoryUpdate(localID: Int) {
        taskInventoryPool.requestUpdate(localID)
    }

    fun requestTouchableChildrenUpdate(uuid: UUID) {
        touchableObjectsPool.requestUpdate(uuid)
    }

    fun runningAnimations(): SubscriptionSingleDataPool<ImmutableSet<UUID>> {
        return runningAnimationsPool
    }

    fun setFilter(newFilter: SLObjectFilterInfo) {
        var changed = false
        synchronized(filterLock) {
            if (filterInfo != newFilter) {
                filterInfo = newFilter
                changed = true
            }
        }
        if (changed) {
            requestObjectListUpdate()
        }
    }

    fun setParcelInfo(newInfo: SLParcelInfo?) {
        parcelInfo.set(newInfo)
        requestObjectListUpdate()
    }

    fun touchableObjects(): Subscribable<UUID, ImmutableList<SLObjectInfo>> {
        return touchableObjectsPool
    }
}
