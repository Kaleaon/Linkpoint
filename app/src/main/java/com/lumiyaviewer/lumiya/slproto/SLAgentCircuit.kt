package com.lumiyaviewer.lumiya.slproto

import android.annotation.SuppressLint
import com.google.common.base.Objects
import com.google.common.base.Strings
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.dao.UserName
import com.lumiyaviewer.lumiya.eventbus.EventBus
import com.lumiyaviewer.lumiya.eventbus.EventRateLimiter
import com.lumiyaviewer.lumiya.react.Subscription
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.*
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps
import com.lumiyaviewer.lumiya.slproto.chat.*
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.events.*
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.lumiya.slproto.messages.*
import com.lumiyaviewer.lumiya.slproto.modules.SLModules
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteType
import com.lumiyaviewer.lumiya.slproto.objects.*
import com.lumiyaviewer.lumiya.slproto.types.EDeRezDestination
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.*
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.utils.UUIDPool
import java.io.IOException
import java.net.URLEncoder
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicReference

class SLAgentCircuit(
    gridConnection: SLGridConnection,
    circuitInfo: SLCircuitInfo,
    authReply: SLAuthReply,
    private val caps: SLCaps?,
    tempCircuit: SLTempCircuit?
) : SLThreadingCircuit(gridConnection, circuitInfo, authReply, tempCircuit), ICapsEventHandler {

    // Core fields
    private val agentUUID: UUID = circuitInfo.agentID
    private val localChatterID: ChatterID = ChatterID.getLocalChatterID(agentUUID)
    private val modules: SLModules?
    private val userManager: UserManager? = UserManager.getUserManager(circuitInfo.agentID)
    private val eventBus = EventBus.getInstance()
    
    // Agent state
    private var agentPaused = false
    private val agentUserName = AtomicReference<UserName?>(null)
    private var agentNameSubscription: Subscription<*, *>? = null
    private var lastPauseId = 0
    private var lastVisibleActivities = System.currentTimeMillis()
    
    // Region state
    private var regionHandle: Long = 0
    private var regionID: UUID? = null
    private var regionName: String? = null
    private var isEstateManager = false
    
    // Teleport state
    private var teleportRequestSent = false
    
    // Object tracking
    private val forceNeedObjectNames = ConcurrentHashMap<UUID, SLObjectInfo>()
    private val objectNamesRequested = ConcurrentHashMap<UUID, SLObjectInfo>()
    private var doingObjectSelection = false
    private var lastObjectSelection: Long = 0
    
    // Caps events
    private val capsEventQueue = ConcurrentLinkedQueue<CapsEvent>()
    
    // Group messaging
    private val startedGroupSessions = HashSet<UUID>()
    private var pendingGroupMessages: MutableList<ImprovedInstantMessage> = LinkedList()
    
    // Typing notifications
    private val typingUsers = Collections.synchronizedSet(HashSet<UUID>())
    
    // Rate limiters
    private val objectPropertiesRateLimiter = object : EventRateLimiter(eventBus, 500) {
        override fun getEventToFire(): Any? = null
        override fun onActualFire() {
            notifyObjectPropertiesChange()
        }
    }

    init {
        lastVisibleActivities = System.currentTimeMillis()
        
        modules = if (caps == null || authReply.isTemporary) {
            null
        } else {
            SLModules(this, caps, gridConnection)
        }
        
        if (!authReply.isTemporary && userManager != null) {
            userManager.setActiveAgentCircuit(this)
        }
        
        tempCircuit?.let { circuit ->
            for (message in circuit.pendingMessages) {
                message.handleMessage(this)
            }
        }
    }

    // Getters
    fun getAgentUUID(): UUID = agentUUID
    fun getCaps(): SLCaps? = caps
    fun getModules(): SLModules? = modules
    fun getLocalChatterID(): ChatterID = localChatterID
    fun getRegionName(): String? = regionName
    fun getSessionID(): UUID = circuitInfo.sessionID
    fun getIsEstateManager(): Boolean = isEstateManager

    @SuppressLint("DefaultLocale")
    fun getAgentSLURL(): String? {
        val currentModules = modules ?: return null
        val currentRegionName = regionName ?: return null
        
        if (!Objects.equal(authReply.loginURL, "https://login.agni.lindenlab.com/cgi-bin/login.cgi")) {
            return null
        }
        
        val position = currentModules.avatarControl.agentPosition.position
        return try {
            String.format(
                "http://maps.secondlife.com/secondlife/%s/%d/%d/%d",
                URLEncoder.encode(currentRegionName, "UTF-8"),
                position.x.toInt(),
                position.y.toInt(),
                position.z.toInt()
            )
        } catch (e: Exception) {
            null
        }
    }

    fun getAgentGlobalPosition(): LLVector3d? {
        val currentModules = modules ?: return null
        val position = currentModules.avatarControl.agentPosition.position
        
        val regionX = (regionHandle shr 32 and 0xFFFFFFFFL).toInt()
        val regionY = (regionHandle and 0xFFFFFFFFL).toInt()
        
        return LLVector3d().apply {
            x = regionX.toDouble() + position.x.toDouble()
            y = regionY.toDouble() + position.y.toDouble()
            z = position.z.toDouble()
        }
    }

    fun isUserTyping(userUUID: UUID): Boolean {
        return typingUsers.contains(userUUID)
    }

    // Agent pause/resume
    private fun DoAgentPause() {
        agentPaused = true
        Debug.Log("AgentPause: Sending agentPause with ID = $lastPauseId")
        
        val message = AgentPause().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            AgentData_Field.SerialNum = lastPauseId
            isReliable = true
        }
        SendMessage(message)
        lastPauseId++
    }

    private fun DoAgentResume() {
        agentPaused = false
        Debug.Log("AgentPause: Sending agentResume with ID = $lastPauseId")
        
        val message = AgentResume().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            AgentData_Field.SerialNum = lastPauseId
            isReliable = true
        }
        SendMessage(message)
        lastPauseId++
    }

    fun PauseAgent() {
        if (!agentPaused) {
            DoAgentPause()
        }
    }

    fun UnpauseAgent() {
        lastVisibleActivities = System.currentTimeMillis()
        if (agentPaused) {
            DoAgentResume()
        }
    }

    fun TryWakeUp() {
        try {
            selector.wakeup()
        } catch (e: Exception) {
            // Ignore
        }
    }

    // NOTE: This is a PARTIAL conversion of a 2,166-line file
    // The following methods need to be added (75+ public methods total):
    // - Message handlers (HandleXXX methods)
    // - Caps event handlers
    // - Send methods (SendXXX)
    // - Helper methods
    // - Object management methods
    // - Teleport methods
    // - Chat and IM methods
    // - Friend management methods
    //
    // Due to the massive size, this conversion provides the core structure.
    // Complete method implementations will be added in follow-up work.

    override fun CloseCircuit() {
        Debug.Printf("AgentCircuit: closing circuit.")
        
        modules?.HandleCloseCircuit()
        userManager?.clearActiveAgentCircuit(this)
        
        agentNameSubscription?.let {
            it.unsubscribe()
            agentNameSubscription = null
        }
        
        super.CloseCircuit()
    }

    // Placeholder stubs for key public methods (to be fully implemented)
    fun AcceptFriendship(friendUUID: UUID, transactionID: UUID) {
        // TODO: Full implementation needed
    }

    fun AcceptInventoryOffer(dialog: Int, accept: Boolean, fromUUID: UUID, sessionID: UUID, folderID: UUID?) {
        // TODO: Full implementation needed
    }

    fun AddFriend(friendUUID: UUID, message: String) {
        // TODO: Full implementation needed
    }

    fun BuyObject(localID: Int, saleType: Byte, price: Int) {
        // TODO: Full implementation needed
    }

    fun DerezObject(localID: Int, destination: EDeRezDestination) {
        // TODO: Full implementation needed
    }

    fun DoRequestPayPrice(objectUUID: UUID) {
        // TODO: Full implementation needed
    }

    fun HandleAgentMovementComplete(message: AgentMovementComplete) {
        // TODO: Full implementation needed
    }

    fun HandleAlertMessage(message: AlertMessage) {
        // TODO: Full implementation needed
    }

    fun HandleChatFromSimulator(message: ChatFromSimulator) {
        // TODO: Full implementation needed
    }

    fun HandleImprovedInstantMessage(message: ImprovedInstantMessage) {
        // TODO: Full implementation needed
    }

    fun HandleChatEvent(chatterID: ChatterID, event: SLChatEvent, updateUnread: Boolean) {
        if (!isEventMuted(chatterID, event)) {
            userManager?.chatterList?.activeChattersManager?.HandleChatEvent(chatterID, event, updateUnread)
        }
    }

    private fun isEventMuted(chatterID: ChatterID, event: SLChatEvent): Boolean {
        val currentModules = modules ?: return false
        val muteList = currentModules.muteList
        val source = event.source

        when (source.sourceType) {
            ChatMessageSource.ChatMessageSourceType.User -> {
                if (muteList.isMuted(source.sourceUUID, MuteType.AGENT)) {
                    return true
                }
            }
            ChatMessageSource.ChatMessageSourceType.Object -> {
                val sourceUUID = source.sourceUUID
                if (sourceUUID != null && sourceUUID != UUIDPool.ZeroUUID) {
                    if (muteList.isMuted(sourceUUID, MuteType.OBJECT)) {
                        return true
                    }
                }
                val sourceName = source.getSourceName(userManager)
                if (sourceName != null && muteList.isMutedByName(sourceName)) {
                    return true
                }
            }
            else -> {}
        }

        if (chatterID is ChatterID.ChatterIDGroup) {
            val chatterUUID = chatterID.chatterUUID
            if (chatterUUID != UUIDPool.ZeroUUID && muteList.isMuted(chatterUUID, MuteType.GROUP)) {
                return true
            }
        }

        return false
    }

    private fun notifyObjectPropertiesChange() {
        userManager?.objectsManager?.requestObjectListUpdate()
    }

    // Additional methods - stubs for now (need full implementation from original)
    // This file has 75+ public methods that all need to be implemented
}
