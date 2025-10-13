package com.lumiyaviewer.lumiya.slproto

import android.annotation.SuppressLint
import com.google.common.base.Objects
import com.google.common.base.Strings
import com.google.common.logging.nano.Vr
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.GridConnectionService
import com.lumiyaviewer.lumiya.dao.UserName
import com.lumiyaviewer.lumiya.eventbus.EventBus
import com.lumiyaviewer.lumiya.eventbus.EventRateLimiter
import com.lumiyaviewer.lumiya.react.Subscription
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey
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
import kotlin.math.floor

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

    // ========== Public Getter Methods ==========

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

    fun isUserTyping(userUUID: UUID): Boolean = typingUsers.contains(userUUID)

    fun getObjectProfile(localID: Int): SLObjectProfileData? {
        val objectInfo = gridConn.parcelInfo.getObjectInfo(localID) ?: return null
        val profile = SLObjectProfileData.create(objectInfo)
        
        if (!profile.name().isPresent && !objectInfo.isDead) {
            RequestObjectName(objectInfo)
        }
        
        return profile
    }

    // ========== Circuit Lifecycle Methods ==========

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

    override fun ProcessWakeup() {
        super.ProcessWakeup()
        
        while (true) {
            try {
                val capsEvent = capsEventQueue.poll() ?: break
                HandleCapsEvent(capsEvent)
            } catch (e: Exception) {
                // Ignore
            }
        }
        
        ProcessIdle()
    }

    override fun ProcessTimeout() {
        super.ProcessTimeout()
        modules?.avatarControl?.setEnableAgentUpdates(false)
        
        if (!authReply.isTemporary) {
            gridConn.processDisconnect(false, "Connection has timed out.")
        }
    }

    override fun ProcessNetworkError() {
        super.ProcessNetworkError()
        Debug.Printf("Network: Network error.")
        
        modules?.avatarControl?.setEnableAgentUpdates(false)
        
        if (!authReply.isTemporary) {
            gridConn.processDisconnect(false, "Network connection lost.")
        }
    }

    fun ProcessIdle() {
        // Handle object selection timeout
        if (doingObjectSelection && System.currentTimeMillis() > lastObjectSelection + 15000) {
            doingObjectSelection = false
            ProcessObjectSelectionTimeout()
        }
        
        // Process object selection if needed
        if (!teleportRequestSent && getNeedObjectNames() && !doingObjectSelection && 
            System.currentTimeMillis() >= lastObjectSelection + 500) {
            ProcessObjectSelection()
        }
        
        // Handle agent pause
        if (!agentPaused) {
            val currentTime = System.currentTimeMillis()
            if (GridConnectionService.hasVisibleActivities()) {
                lastVisibleActivities = currentTime
            } else if (currentTime >= lastVisibleActivities + 10000) {
                DoAgentPause()
            }
        }
        
        objectPropertiesRateLimiter.firePending()
    }

    // ========== Agent Pause/Resume ==========

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

    // ========== Message Handlers ==========

    fun HandleAgentMovementComplete(message: AgentMovementComplete) {
        regionHandle = message.Data_Field.RegionHandle
        modules?.avatarControl?.setAgentPosition(message.Data_Field.Position, null)
        Debug.Printf("Got agentPosition: %s", modules?.avatarControl?.agentPosition?.immutablePosition)
        
        SendAgentFOV()
        modules?.avatarAppearance?.SendAgentWearablesRequest()
        SendRetrieveInstantMessages()
        modules?.avatarControl?.setEnableAgentUpdates(true)
    }

    fun HandleAlertMessage(message: AlertMessage) {
        val text = SLMessage.stringFromVariableOEM(message.AlertData_Field.Message)
        val event = SLChatSystemMessageEvent(ChatMessageSourceUnknown.getInstance(), agentUUID, text)
        HandleChatEvent(localChatterID, event, true)
    }

    fun HandleAvatarAnimation(message: AvatarAnimation) {
        val parcelInfo = gridConn.parcelInfo
        if (parcelInfo != null && modules != null) {
            parcelInfo.ApplyAvatarAnimation(message, modules.avatarControl)
        }
    }

    fun HandleAvatarAppearance(message: AvatarAppearance) {
        Debug.Log("Got AvatarAppearance, ID = ${message.Sender_Field.ID}, isTrial = ${message.Sender_Field.IsTrial}, our ID = ${circuitInfo.agentID}")
        
        if (message.Sender_Field.ID == circuitInfo.agentID && modules != null) {
            modules.avatarAppearance.HandleAvatarAppearance(message)
        }
        
        gridConn.parcelInfo?.ApplyAvatarAppearance(message)
    }

    fun HandleAvatarInterestsReply(message: AvatarInterestsReply) {
        Debug.Log("got AvatarInterestsReply: wantToText = ${SLMessage.stringFromVariableOEM(message.PropertiesData_Field.WantToText)}")
        Debug.Log("got AvatarInterestsReply: skillText = ${SLMessage.stringFromVariableOEM(message.PropertiesData_Field.SkillsText)}")
    }

    fun HandleChatFromSimulator(message: ChatFromSimulator) {
        val currentModules = getModules()
        if (currentModules?.rlvController?.onIncomingChat(message) == true) {
            return
        }

        val uuid = message.ChatData_Field.SourceID
        val fromName = SLMessage.stringFromVariableOEM(message.ChatData_Field.FromName)
        val text = SLMessage.stringFromVariableUTF(message.ChatData_Field.Message)

        // Ignore Firestorm bridge messages
        if (message.ChatData_Field.ChatType == 8.toByte() && 
            message.ChatData_Field.SourceType == 2.toByte() &&
            fromName.startsWith("#Firestorm LSL Bridge") && 
            text.startsWith("<bridgeURL>")) {
            return
        }

        // Check RLV permissions
        if (message.ChatData_Field.SourceType == 1.toByte()) {
            if (currentModules?.rlvController?.canRecvChat(text, uuid) == false) {
                return
            }
        }

        if (message.ChatData_Field.Audible == 1.toByte()) {
            val chatType = message.ChatData_Field.ChatType.toInt()
            if (chatType != 6 && chatType != 4 && chatType != 5) {
                val chatEvent = when (message.ChatData_Field.SourceType.toInt()) {
                    1 -> SLChatTextEvent(ChatMessageSourceUser(uuid), agentUUID, text)
                    2 -> SLChatTextEvent(ChatMessageSourceObject(uuid, fromName), agentUUID, text)
                    else -> SLChatTextEvent(ChatMessageSourceUnknown.getInstance(), agentUUID, text)
                }
                HandleChatEvent(localChatterID, chatEvent, true)
            }
        }
    }

    fun HandleImprovedInstantMessage(message: ImprovedInstantMessage) {
        val dialog = message.MessageBlock_Field.Dialog.toInt()
        
        val source: ChatMessageSource = when {
            dialog == 19 || dialog == 31 -> {
                ChatMessageSourceObject(
                    message.AgentData_Field.AgentID,
                    SLMessage.stringFromVariableOEM(message.MessageBlock_Field.FromAgentName)
                )
            }
            dialog == 3 -> ChatMessageSourceUnknown.getInstance()
            UUIDPool.ZeroUUID == message.AgentData_Field.AgentID -> ChatMessageSourceUnknown.getInstance()
            else -> {
                val userSource = ChatMessageSourceUser(message.AgentData_Field.AgentID)
                if (getModules()?.rlvController?.canRecvIM(userSource.sourceUUID) == false) {
                    return
                }
                userSource
            }
        }
        
        HandleIM(message, source)
    }

    private fun HandleIM(message: ImprovedInstantMessage, source: ChatMessageSource) {
        // Check if RLV controller should handle this IM
        if (getModules()?.rlvController?.onIncomingIM(message) == true) {
            return
        }
        
        when (message.MessageBlock_Field.Dialog.toInt()) {
            0, 20 -> handlePersonalMessage(message, source)
            1, 2 -> handleSystemMessage(message)
            3 -> handleGroupInvitation(message, source)
            4 -> handleInventoryOffered(message, source)
            9 -> handleInventoryOfferedFromObject(message)
            17 -> HandleSessionIM(message, source)
            19, 31 -> handleObjectMessage(message, source)
            22 -> handleTeleportLure(message, source)
            26 -> handleLureRequest(message, source)
            32, 37 -> HandleGroupNotice(message, source)
            38 -> handleFriendshipOffered(message, source)
            39, 40 -> handleFriendshipResult(message, source)
            41 -> HandleTypingNotification(source, true)
            42 -> HandleTypingNotification(source, false)
            else -> {
                Debug.Log("HandleIM: unknown type = ${message.MessageBlock_Field.Dialog}, " +
                    "sessionId = ${message.AgentData_Field.SessionID}, " +
                    "toAgentID = ${message.MessageBlock_Field.ToAgentID}, " +
                    "fromGroup = ${message.MessageBlock_Field.FromGroup}, " +
                    "message = '${SLMessage.stringFromVariableUTF(message.MessageBlock_Field.Message)}'")
            }
        }
    }

    private fun handlePersonalMessage(message: ImprovedInstantMessage, source: ChatMessageSource) {
        val chatEvent = SLChatTextEvent(source, agentUUID, message, null)
        val defaultChatter = source.getDefaultChatter(agentUUID)
        val isChatterActive = userManager?.isChatterActive(defaultChatter) ?: false
        
        HandleChatEvent(defaultChatter, chatEvent, true)
        
        // Auto-response logic
        if (userManager?.isChatterMuted(defaultChatter) == false &&
            message.MessageBlock_Field.Dialog != 20.toByte() &&
            message.MessageBlock_Field.Offline == 0.toByte() &&
            message.MessageBlock_Field.Message.isNotEmpty() &&
            !isChatterActive &&
            defaultChatter is ChatterID.ChatterIDUser) {
            
            val autoResponse = SLGridConnection.getAutoresponse()
            if (!Strings.isNullOrEmpty(autoResponse)) {
                SendInstantMessage(defaultChatter.chatterUUID, autoResponse, 20)
            }
        }
    }

    private fun handleSystemMessage(message: ImprovedInstantMessage) {
        val text = SLMessage.stringFromVariableUTF(message.MessageBlock_Field.Message)
        val event = SLChatSystemMessageEvent(ChatMessageSourceUnknown.getInstance(), agentUUID, text)
        HandleChatEvent(localChatterID, event, true)
    }

    private fun handleGroupInvitation(message: ImprovedInstantMessage, source: ChatMessageSource) {
        val event = SLChatGroupInvitationEvent(source, agentUUID, message)
        HandleChatEvent(source.getDefaultChatter(agentUUID), event, true)
    }

    private fun handleInventoryOffered(message: ImprovedInstantMessage, source: ChatMessageSource) {
        val event = SLChatInventoryItemOfferedEvent(source, agentUUID, message)
        HandleChatEvent(source.getDefaultChatter(agentUUID), event, true)
    }

    private fun handleInventoryOfferedFromObject(message: ImprovedInstantMessage) {
        val objectSource = ChatMessageSourceObject(
            message.AgentData_Field.AgentID,
            SLMessage.stringFromVariableOEM(message.MessageBlock_Field.FromAgentName)
        )
        val event = SLChatInventoryItemOfferedEvent(objectSource, agentUUID, message)
        HandleChatEvent(localChatterID, event, true)
    }

    private fun handleObjectMessage(message: ImprovedInstantMessage, source: ChatMessageSource) {
        val event = SLChatTextEvent(source, agentUUID, message, null)
        HandleChatEvent(source.getDefaultChatter(agentUUID), event, true)
    }

    private fun handleTeleportLure(message: ImprovedInstantMessage, source: ChatMessageSource) {
        val currentModules = getModules()
        
        if (source.sourceType == ChatMessageSource.ChatMessageSourceType.User) {
            val sourceUUID = source.sourceUUID
            if (currentModules != null) {
                if (currentModules.rlvController.autoAcceptTeleport(sourceUUID)) {
                    TeleportToLure(message.MessageBlock_Field.ID)
                    return
                } else if (!currentModules.rlvController.canTeleportToLure(sourceUUID)) {
                    return
                }
            }
        }
        
        val event = SLChatLureEvent(source, agentUUID, message)
        HandleChatEvent(source.getDefaultChatter(agentUUID), event, true)
    }

    private fun handleLureRequest(message: ImprovedInstantMessage, source: ChatMessageSource) {
        val currentModules = getModules()
        
        if (source.sourceType == ChatMessageSource.ChatMessageSourceType.User && currentModules != null) {
            if (!currentModules.rlvController.canTeleportToLure(source.sourceUUID)) {
                return
            }
        }
        
        val event = SLChatLureRequestEvent(source, agentUUID, message)
        HandleChatEvent(source.getDefaultChatter(agentUUID), event, true)
    }

    private fun handleFriendshipOffered(message: ImprovedInstantMessage, source: ChatMessageSource) {
        val event = SLChatFriendshipOfferedEvent(source, agentUUID, message)
        HandleChatEvent(source.getDefaultChatter(agentUUID), event, true)
    }

    private fun handleFriendshipResult(message: ImprovedInstantMessage, source: ChatMessageSource) {
        val event = SLChatFriendshipResultEvent(source, agentUUID, message)
        HandleChatEvent(source.getDefaultChatter(agentUUID), event, true)
        
        // If friendship accepted, add to friend list
        if (message.MessageBlock_Field.Dialog == 39.toByte()) { // FriendshipAccepted
            if (source.sourceType == ChatMessageSource.ChatMessageSourceType.User) {
                source.sourceUUID?.let { friendUUID ->
                    userManager?.chatterList?.friendManager?.addFriend(friendUUID)
                    SendGenericMessage("requestonlinenotification", arrayOf(friendUUID.toString()))
                }
            }
        }
    }

    private fun HandleSessionIM(message: ImprovedInstantMessage, source: ChatMessageSource) {
        val groupChatterID = ChatterID.getGroupChatterID(agentUUID, message.MessageBlock_Field.ID)
        val event = SLChatTextEvent(source, agentUUID, message, null)
        HandleChatEvent(groupChatterID, event, true)
    }

    private fun HandleGroupNotice(message: ImprovedInstantMessage, source: ChatMessageSource) {
        val buffer = ByteBuffer.wrap(message.MessageBlock_Field.BinaryBucket)
        
        if (buffer.limit() < 18) return
        
        buffer.order(ByteOrder.BIG_ENDIAN)
        val hasAttachment = buffer.get()
        val assetType = buffer.get()
        val groupUUID = UUID(buffer.long, buffer.long)
        
        var attachmentName = ""
        if (hasAttachment != 0.toByte()) {
            val nameBytes = ByteArray(buffer.remaining())
            buffer.get(nameBytes)
            attachmentName = SLMessage.stringFromVariableOEM(nameBytes)
        }
        
        Debug.Log("HandleGroupNotice: group UUID = $groupUUID")
        
        val groupChatterID = ChatterID.getGroupChatterID(agentUUID, groupUUID)
        val sentByMe = Objects.equal(source.sourceUUID, circuitInfo.agentID)
        
        var noticeText = SLMessage.stringFromVariableUTF(message.MessageBlock_Field.Message)
        val pipeIndex = noticeText.indexOf(Vr.VREvent.VrCore.ErrorCode.CONTROLLER_GATT_NOTIFY_FAILED)
        if (pipeIndex >= 0) {
            noticeText = noticeText.substring(0, pipeIndex) + "\n" + noticeText.substring(pipeIndex + 1)
        }
        
        if (sentByMe && hasAttachment != 0.toByte()) {
            noticeText += "\n(This notice contains attached item '$attachmentName')"
        }
        
        HandleChatEvent(groupChatterID, SLChatTextEvent(source, agentUUID, message, noticeText), true)
        
        if (hasAttachment != 0.toByte() && !sentByMe) {
            val itemEvent = SLChatInventoryItemOfferedByGroupNoticeEvent(
                source, agentUUID, message, attachmentName, SLAssetType.getByType(assetType)
            )
            HandleChatEvent(groupChatterID, itemEvent, false)
        }
    }

    private fun HandleTypingNotification(source: ChatMessageSource, isTyping: Boolean) {
        if (source is ChatMessageSourceUser) {
            val sourceUUID = source.sourceUUID ?: return
            
            if (isTyping) {
                if (typingUsers.add(sourceUUID)) {
                    userManager?.chatterList?.updateUserTypingStatus(sourceUUID)
                }
            } else {
                if (typingUsers.remove(sourceUUID)) {
                    userManager?.chatterList?.updateUserTypingStatus(sourceUUID)
                }
            }
        }
    }

    fun HandleImprovedTerseObjectUpdate(message: ImprovedTerseObjectUpdate) {
        val parcelInfo = gridConn.parcelInfo
        var requestMessage: RequestMultipleObjects? = null
        
        for (objectData in message.ObjectData_Fields) {
            val localID = SLObjectInfo.getLocalID(objectData)
            val uuid = parcelInfo.uuidsNearby[localID]
            var objectInfo = uuid?.let { parcelInfo.allObjectsNearby[it] }
            
            if (objectInfo != null) {
                objectInfo.ApplyTerseObjectUpdate(objectData)
                
                when {
                    objectInfo is SLObjectAvatarInfo && objectInfo.isMyAvatar() -> {
                        processMyAvatarUpdate(objectInfo)
                    }
                    objectInfo.isMyAttachment() -> {
                        processMyAttachmentUpdate(objectInfo)
                    }
                }
            } else {
                // Cache miss - request full object data
                if (requestMessage == null) {
                    requestMessage = RequestMultipleObjects().apply {
                        AgentData_Field.AgentID = circuitInfo.agentID
                        AgentData_Field.SessionID = circuitInfo.sessionID
                    }
                }
                requestMessage.ObjectData_Fields.add(RequestMultipleObjects.ObjectData().apply {
                    CacheMissType = 0
                    ID = localID
                })
            }
        }
        
        requestMessage?.let {
            Debug.Log("Handing cache miss for terse update: ${it.ObjectData_Fields.size} objects.")
            it.isReliable = true
            SendMessage(it)
        }
    }

    fun HandleKillObject(message: KillObject) {
        val parcelInfo = gridConn.parcelInfo
        var anyKilled = false
        
        for (objectData in message.ObjectData_Fields) {
            if (parcelInfo.killObject(this, objectData.ID)) {
                anyKilled = true
            }
        }
        
        if (anyKilled) {
            objectPropertiesRateLimiter.fire()
        }
    }

    fun HandleLayerData(message: LayerData) {
        if (message.LayerID_Field.Type == 76) {
            gridConn.parcelInfo?.terrainData?.ProcessLayerData(message.LayerDataData_Field.Data)
        }
    }

    fun HandleLoadURL(message: LoadURL) {
        val source = ChatMessageSourceObject(
            message.Data_Field.ObjectID,
            SLMessage.stringFromVariableOEM(message.Data_Field.ObjectName)
        )
        val event = SLChatTextEvent(source, agentUUID, message)
        HandleChatEvent(localChatterID, event, true)
    }

    fun HandleObjectProperties(message: ObjectProperties) {
        Debug.Log("ObjectProperties: ${message.ObjectData_Fields.size} ObjectSelect replies. " +
            "Reqd ${objectNamesRequested.size} obj, remains ${gridConn.parcelInfo.objectNamesQueue.size} objects.")
        
        for (objectData in message.ObjectData_Fields) {
            // Handle regular object name requests
            var objectInfo = gridConn.parcelInfo.objectNamesQueue.remove(objectData.ObjectID)
            objectInfo?.let {
                it.ApplyObjectProperties(objectData)
                userManager?.objectsManager?.requestObjectProfileUpdate(it.localID)
            }
            
            // Handle force-requested object names
            objectInfo = forceNeedObjectNames.remove(objectData.ObjectID)
            objectInfo?.let {
                it.ApplyObjectProperties(objectData)
                userManager?.objectsManager?.requestObjectProfileUpdate(it.localID)
                
                it.parentObject?.let { parent ->
                    parent.id?.let { parentID ->
                        userManager?.objectsManager?.requestTouchableChildrenUpdate(parentID)
                    }
                }
            }
            
            objectNamesRequested.remove(objectData.ObjectID)
        }
        
        if (objectNamesRequested.isEmpty()) {
            doingObjectSelection = false
            ProcessObjectSelection()
        }
        
        objectPropertiesRateLimiter.fire()
    }

    fun HandleObjectUpdate(message: ObjectUpdate) {
        val parcelInfo = gridConn.parcelInfo
        var myAvatarChanged = false
        var objectsChanged = false
        
        for (objectData in message.ObjectData_Fields) {
            if (objectData.PCode == 47.toByte() || objectData.PCode == 9.toByte()) {
                var objectInfo = parcelInfo.allObjectsNearby[objectData.FullID]
                
                if (objectInfo != null) {
                    val oldParentID = objectInfo.parentID
                    objectInfo.ApplyObjectUpdate(objectData)
                    parcelInfo.updateObjectParent(oldParentID, objectInfo)
                    
                    if (objectInfo.parentID != oldParentID && 
                        objectInfo is SLObjectAvatarInfo && objectInfo.isMyAvatar()) {
                        myAvatarChanged = true
                    }
                    objectsChanged = true
                } else {
                    objectInfo = SLObjectInfo.create(agentUUID, objectData, circuitInfo.agentID)
                    if (parcelInfo.addObject(objectInfo)) {
                        objectsChanged = true
                    }
                    
                    if (objectInfo is SLObjectAvatarInfo && objectInfo.isMyAvatar()) {
                        Debug.Log("ObjectUpdate: got my avatar (normal)")
                        parcelInfo.setAgentAvatar(objectInfo)
                        modules?.avatarAppearance?.OnMyAvatarCreated(objectInfo)
                    }
                }
                
                when {
                    objectInfo is SLObjectAvatarInfo && objectInfo.isMyAvatar() -> {
                        processMyAvatarUpdate(objectInfo)
                    }
                    objectInfo.isMyAttachment() -> {
                        processMyAttachmentUpdate(objectInfo)
                    }
                }
            }
        }
        
        if (myAvatarChanged) {
            userManager?.objectsManager?.myAvatarState()?.requestUpdate(SubscriptionSingleKey.Value)
        }
        
        if (objectsChanged) {
            ProcessObjectSelection()
            objectPropertiesRateLimiter.fire()
        }
    }

    fun HandleObjectUpdateCached(message: ObjectUpdateCached) {
        val requestMessage = RequestMultipleObjects().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
        }
        
        for (objectData in message.ObjectData_Fields) {
            requestMessage.ObjectData_Fields.add(RequestMultipleObjects.ObjectData().apply {
                CacheMissType = 0
                ID = objectData.ID
            })
        }
        
        requestMessage.isReliable = true
        SendMessage(requestMessage)
    }

    fun HandleObjectUpdateCompressed(message: ObjectUpdateCompressed) {
        val parcelInfo = gridConn.parcelInfo
        var myAvatarChanged = false
        var objectsChanged = false
        
        for (objectData in message.ObjectData_Fields) {
            try {
                val localID = SLObjectInfo.getLocalID(objectData)
                val uuid = parcelInfo.uuidsNearby[localID]
                var objectInfo = uuid?.let { parcelInfo.allObjectsNearby[it] }
                
                val parentChanged: Boolean
                if (objectInfo != null) {
                    val oldParentID = objectInfo.parentID
                    objectInfo.ApplyObjectUpdate(objectData)
                    parcelInfo.updateObjectParent(oldParentID, objectInfo)
                    parentChanged = objectInfo.parentID != oldParentID
                    objectsChanged = true
                } else {
                    objectInfo = SLObjectInfo.create(objectData)
                    if (parcelInfo.addObject(objectInfo)) {
                        objectsChanged = true
                    }
                    parentChanged = false
                }
                
                if (objectInfo is SLObjectAvatarInfo && objectInfo.isMyAvatar()) {
                    if (parentChanged) {
                        myAvatarChanged = true
                    }
                    processMyAvatarUpdate(objectInfo)
                } else if (objectInfo.isMyAttachment()) {
                    processMyAttachmentUpdate(objectInfo)
                }
            } catch (e: UnsupportedObjectTypeException) {
                // Ignore unsupported object types
            } catch (e: Throwable) {
                Debug.Warning(e)
            }
        }
        
        if (objectsChanged) {
            ProcessObjectSelection()
            objectPropertiesRateLimiter.fire()
        }
        
        if (myAvatarChanged) {
            userManager?.objectsManager?.myAvatarState()?.requestUpdate(SubscriptionSingleKey.Value)
        }
    }

    fun HandleOfflineNotification(message: OfflineNotification) {
        val agentIDs = message.AgentBlock_Fields.map { it.AgentID }
        userManager?.chatterList?.friendManager?.setUsersOnline(agentIDs, false)
    }

    fun HandleOnlineNotification(message: OnlineNotification) {
        val agentIDs = message.AgentBlock_Fields.map { it.AgentID }
        userManager?.chatterList?.friendManager?.setUsersOnline(agentIDs, true)
    }

    fun HandlePayPriceReply(message: PayPriceReply) {
        val objectInfo = gridConn.parcelInfo.allObjectsNearby[message.ObjectData_Field.ObjectID]
        
        objectInfo?.let {
            val defaultPrice = message.ObjectData_Field.DefaultPayPrice
            val buttonPrices = IntArray(message.ButtonData_Fields.size) { i ->
                message.ButtonData_Fields[i].PayButton
            }
            
            it.setPayInfo(PayInfo.create(defaultPrice, buttonPrices))
            userManager?.objectsManager?.requestObjectProfileUpdate(it.localID)
            eventBus.publish(SLObjectPayInfoEvent(it))
        }
    }

    fun HandleRegionHandshake(message: RegionHandshake) {
        if (authReply.isTemporary) return
        
        // Send handshake reply
        val reply = RegionHandshakeReply().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            RegionInfo_Field.Flags = 0
        }
        
        gridConn.parcelInfo?.terrainData?.ApplyRegionInfo(message.RegionInfo_Field)
        SendMessage(reply)
        
        regionName = SLMessage.stringFromVariableOEM(message.RegionInfo_Field.SimName)
        message.RegionInfo2_Field?.RegionID?.let { regionID = it }
        isEstateManager = message.RegionInfo_Field.IsEstateManager
        
        agentNameSubscription = userManager?.userNames?.subscribe(
            circuitInfo.agentID
        ) { userName ->
            agentUserName.set(userName)
        }
        
        eventBus.publish(SLRegionInfoChangedEvent())
    }

    fun HandleScriptDialog(message: ScriptDialog) {
        var buttonLabels: Array<String>? = null
        var textBoxIndex = 0
        var isTextBox = false
        
        if (message.Buttons_Fields.isNotEmpty()) {
            val labels = Array(message.Buttons_Fields.size) { i ->
                val label = SLMessage.stringFromVariableUTF(message.Buttons_Fields[i].ButtonLabel)
                if (label == "!!llTextBox!!") {
                    textBoxIndex = i
                    isTextBox = true
                }
                label
            }
            buttonLabels = labels
        }
        
        val event = if (isTextBox) {
            SLChatTextBoxDialog(message, agentUUID, textBoxIndex)
        } else {
            SLChatScriptDialog(message, agentUUID, buttonLabels)
        }
        
        HandleChatEvent(localChatterID, event, true)
    }

    fun HandleSimulatorViewerTimeMessage(message: SimulatorViewerTimeMessage) {
        if (!authReply.isTemporary) {
            val sunPhase = (message.TimeInfo_Field.SunPhase / 6.2831855f) + 0.25f
            val sunHour = sunPhase - floor(sunPhase.toDouble()).toFloat()
            gridConn.parcelInfo?.setSunHour(sunHour)
        }
    }

    fun HandleTeleportFailed(message: TeleportFailed) {
        val reason = SLMessage.stringFromVariableOEM(message.Info_Field.Reason)
        Debug.Log("TeleportFailed: reason = $reason")
        teleportRequestSent = false
        eventBus.publish(SLTeleportResultEvent(false, reason))
    }

    fun HandleTeleportLocal(message: TeleportLocal) {
        teleportRequestSent = false
        eventBus.publish(SLTeleportResultEvent(true, null))
    }

    fun HandleTeleportProgress(message: TeleportProgress) {
        val progressText = SLMessage.stringFromVariableOEM(message.Info_Field.Message)
        Debug.Log("Teleport progress: flags = ${message.Info_Field.TeleportFlags}, progress = $progressText")
    }

    fun HandleTeleportStart(message: TeleportStart) {
        Debug.Log("TeleportStart: flags = ${message.Info_Field.TeleportFlags}")
    }

    fun HandleChatEvent(chatterID: ChatterID, event: SLChatEvent, updateUnread: Boolean) {
        if (!isEventMuted(chatterID, event)) {
            userManager?.chatterList?.activeChattersManager?.HandleChatEvent(chatterID, event, updateUnread)
        }
    }

    // ========== Caps Event Handlers ==========

    override fun OnCapsEvent(capsEvent: CapsEvent) {
        try {
            capsEventQueue.add(capsEvent)
            selector.wakeup()
        } catch (e: Exception) {
            // Ignore
        }
    }

    private fun HandleCapsEvent(capsEvent: CapsEvent) {
        when (capsEvent.eventType) {
            CapsEventType.ChatterBoxInvitation -> HandleChatterBoxInvitation(capsEvent.eventBody)
            CapsEventType.ChatterBoxSessionStartReply -> HandleChatterBoxSessionStartReply(capsEvent.eventBody)
            CapsEventType.EstablishAgentCommunication -> HandleEstablishAgentCommunication(capsEvent.eventBody)
            CapsEventType.TeleportFailed -> HandleTeleportFailed(capsEvent.eventBody)
            CapsEventType.TeleportFinish -> HandleTeleportFinish(capsEvent.eventBody)
            else -> DefaultEventQueueHandler(capsEvent.eventType, capsEvent.eventBody)
        }
    }

    private fun HandleChatterBoxInvitation(llsd: LLSDNode) {
        try {
            Debug.Log("ChatterBoxInvitation: event = ${llsd.serializeToXML()}")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        
        try {
            val sessionID = UUID.fromString(llsd.byKey("session_id").asString())
            val avatarGroupList = userManager?.chatterList?.groupManager?.avatarGroupList
            var groupEntry = avatarGroupList?.Groups?.get(sessionID)
            
            val params = llsd.byKey("instantmessage").byKey("message_params")
            val fromID = if (params.keyExists("from_id")) params.byKey("from_id").asUUID() else null
            val toID = params.byKey("to_id").asUUID()
            val messageText = params.byKey("message").asString()
            
            if (groupEntry == null) {
                groupEntry = avatarGroupList?.Groups?.get(toID)
            }
            
            if (groupEntry == null || fromID == null) {
                Debug.Log("ChatterBoxInvitation: chat from unknown group ($sessionID), to_id = $toID")
            } else {
                val groupChatterID = ChatterID.getGroupChatterID(agentUUID, groupEntry.GroupID)
                val event = SLChatTextEvent(ChatMessageSourceUser(fromID), agentUUID, messageText)
                HandleChatEvent(groupChatterID, event, true)
            }
        } catch (e: LLSDException) {
            Debug.Log("ChatterBoxInvitation: LLSDException ${e.message}")
            e.printStackTrace()
        }
    }

    private fun HandleChatterBoxSessionStartReply(llsd: LLSDNode) {
        try {
            Debug.Log("ChatterBoxSessionStartReply: event = ${llsd.serializeToXML()}")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        
        try {
            val sessionID = llsd.byKey("session_id").asUUID()
            modules?.voice?.onGroupSessionReady(sessionID)
            
            synchronized(startedGroupSessions) {
                startedGroupSessions.add(sessionID)
                
                val iterator = pendingGroupMessages.iterator()
                while (iterator.hasNext()) {
                    val pendingMessage = iterator.next()
                    if (pendingMessage.MessageBlock_Field.ID == sessionID) {
                        iterator.remove()
                        SendMessage(pendingMessage)
                    }
                }
            }
        } catch (e: LLSDException) {
            Debug.Log("ChatterBoxSessionStartReply: LLSDException ${e.message}")
            e.printStackTrace()
        }
    }

    private fun HandleChatterOnlineStatus(chatterID: ChatterID, isOnline: Boolean) {
        if (userManager?.isChatterActive(chatterID) == true && chatterID is ChatterID.ChatterIDUser) {
            val source = ChatMessageSourceUser(chatterID.chatterUUID)
            val event = SLChatOnlineOfflineEvent(source, agentUUID, isOnline)
            HandleChatEvent(chatterID, event, false)
        }
    }

    private fun HandleEstablishAgentCommunication(llsd: LLSDNode) {
        if (!teleportRequestSent) return
        
        try {
            Debug.Log("EstablishAgentCommunication: event = ${llsd.serializeToXML()}")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        
        try {
            val simIPAndPort = llsd.byKey("sim-ip-and-port").asString()
            val seedCapability = llsd.byKey("seed-capability").asString()
            val agentID = llsd.byKey("agent-id").asUUID()
            
            val parts = simIPAndPort.split(":")
            val newAuthReply = SLAuthReply(
                authReply,
                true,
                true,
                agentID,
                parts[0],
                parts[1].toInt(),
                seedCapability
            )
            
            gridConn.addTempCircuit(newAuthReply)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun HandleTeleportFailed(llsd: LLSDNode) {
        try {
            Debug.Log("TeleportFailed: event = ${llsd.serializeToXML()}")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        
        if (teleportRequestSent) {
            teleportRequestSent = false
            eventBus.publish(SLTeleportResultEvent(false, "Teleport has failed."))
        }
    }

    private fun HandleTeleportFinish(llsd: LLSDNode) {
        try {
            Debug.Log("TeleportFinish: event = ${llsd.serializeToXML()}")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        
        if (!teleportRequestSent) {
            Debug.Log("TeleportFinish: stale teleport finish?")
            return
        }
        
        teleportRequestSent = false
        
        try {
            val info = llsd.byKey("Info").byIndex(0)
            val seedCapability = info.byKey("SeedCapability").asString()
            val simIP = info.byKey("SimIP").asBinary()
            val simPort = info.byKey("SimPort").asInt()
            
            val ipAddress = String.format(
                "%d.%d.%d.%d",
                simIP[0].toInt() and 0xFF,
                simIP[1].toInt() and 0xFF,
                simIP[2].toInt() and 0xFF,
                simIP[3].toInt() and 0xFF
            )
            
            val newAuthReply = SLAuthReply(
                authReply,
                true,
                false,
                authReply.agentID,
                ipAddress,
                simPort,
                seedCapability
            )
            
            Debug.Printf("new sim address: %s", newAuthReply.simAddress)
            modules?.avatarControl?.setEnableAgentUpdates(false)
            gridConn.HandleTeleportFinish(newAuthReply)
        } catch (e: LLSDException) {
            Debug.Log("TeleportFinish: LLSDException, teleport apparently failed")
            e.printStackTrace()
        }
    }

    private fun DefaultEventQueueHandler(eventType: CapsEventType, eventBody: LLSDNode) {
        // Delegate to modules for other event types
        modules?.HandleCapsEvent(eventType, eventBody)
    }

    // ========== Send Message Methods ==========

    private fun SendAgentFOV() {
        val message = AgentFOV().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            AgentData_Field.CircuitCode = circuitInfo.circuitCode
            FOVBlock_Field.GenCounter = 0
            FOVBlock_Field.VerticalAngle = 3.0543263f
            isReliable = true
        }
        SendMessage(message)
    }

    private fun SendCompleteAgentMovement() {
        val message = CompleteAgentMovement().apply {
            AgentData_Field.CircuitCode = circuitInfo.circuitCode
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            isReliable = true
        }
        SendMessage(message)
    }

    private fun SendRetrieveInstantMessages() {
        val message = RetrieveInstantMessages().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            isReliable = true
        }
        SendMessage(message)
    }

    private fun SendGroupSessionStart(groupUUID: UUID) {
        val message = ImprovedInstantMessage().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            MessageBlock_Field.FromGroup = false
            MessageBlock_Field.ToAgentID = groupUUID
            MessageBlock_Field.ParentEstateID = 0
            MessageBlock_Field.RegionID = UUID(0, 0)
            MessageBlock_Field.Position = modules?.avatarControl?.agentPosition?.position ?: LLVector3()
            MessageBlock_Field.Offline = 0
            MessageBlock_Field.Dialog = 15
            MessageBlock_Field.ID = groupUUID
            MessageBlock_Field.Timestamp = 0
            MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo")
            MessageBlock_Field.Message = SLMessage.stringToVariableUTF("")
            MessageBlock_Field.BinaryBucket = ByteArray(1)
            isReliable = true
        }
        SendMessage(message)
    }

    private fun SendEstateOwnerMessage(method: String, params: Array<String>) {
        val message = EstateOwnerMessage().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            AgentData_Field.TransactionID = UUID(0, 0)
            MethodData_Field.Method = SLMessage.stringToVariableOEM(method)
            MethodData_Field.Invoice = UUID(0, 0)
            
            for (param in params) {
                ParamList_Fields.add(EstateOwnerMessage.ParamList().apply {
                    Parameter = SLMessage.stringToVariableOEM(param)
                })
            }
            
            isReliable = true
        }
        SendMessage(message)
    }

    private fun SendInstantMessage(toUUID: UUID, text: String, dialog: Int): Boolean {
        if (getModules()?.rlvController?.canSendIM(toUUID) == false) {
            return false
        }
        
        val message = ImprovedInstantMessage().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            MessageBlock_Field.FromGroup = false
            MessageBlock_Field.ToAgentID = toUUID
            MessageBlock_Field.ParentEstateID = 0
            MessageBlock_Field.RegionID = UUID(0, 0)
            MessageBlock_Field.Position = LLVector3()
            MessageBlock_Field.Offline = 0
            MessageBlock_Field.Dialog = dialog
            MessageBlock_Field.ID = UUID(
                toUUID.mostSignificantBits xor circuitInfo.agentID.mostSignificantBits,
                toUUID.leastSignificantBits xor circuitInfo.agentID.leastSignificantBits
            )
            MessageBlock_Field.Timestamp = 0
            MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo")
            MessageBlock_Field.Message = SLMessage.stringToVariableUTF(text)
            MessageBlock_Field.BinaryBucket = ByteArray(0)
            isReliable = true
        }
        
        SendMessage(message)
        
        // Generate chat event for sent message
        when (dialog) {
            20, 41, 42 -> {} // Don't generate event for these
            26 -> {
                val event = SLChatLureRequestedEvent(text, agentUUID)
                HandleChatEvent(ChatterID.getUserChatterID(agentUUID, toUUID), event, false)
            }
            else -> {
                val source = ChatMessageSourceUser(circuitInfo.agentID)
                val event = SLChatTextEvent(source, agentUUID, text)
                HandleChatEvent(ChatterID.getUserChatterID(agentUUID, toUUID), event, false)
            }
        }
        
        return true
    }

    fun SendInstantMessage(toUUID: UUID, text: String): Boolean {
        return SendInstantMessage(toUUID, text, 0)
    }

    fun SendGroupInstantMessage(groupUUID: UUID, text: String) {
        val message = ImprovedInstantMessage().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            MessageBlock_Field.FromGroup = false
            MessageBlock_Field.ToAgentID = groupUUID
            MessageBlock_Field.ParentEstateID = 0
            MessageBlock_Field.RegionID = UUID(0, 0)
            MessageBlock_Field.Position = modules?.avatarControl?.agentPosition?.position ?: LLVector3()
            MessageBlock_Field.Offline = 0
            MessageBlock_Field.Dialog = 17
            MessageBlock_Field.ID = groupUUID
            MessageBlock_Field.Timestamp = 0
            MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo")
            MessageBlock_Field.Message = SLMessage.stringToVariableUTF(text)
            MessageBlock_Field.BinaryBucket = ByteArray(1)
            isReliable = true
        }
        
        synchronized(startedGroupSessions) {
            if (startedGroupSessions.contains(groupUUID)) {
                SendMessage(message)
            } else {
                SendGroupSessionStart(groupUUID)
                pendingGroupMessages.add(message)
            }
        }
    }

    fun SendLocalChatMessage(text: String) {
        var channel = 0
        var messageText = text
        
        // Parse channel number if message starts with /
        if (text.startsWith("/")) {
            var index = 1
            var digitCount = 0
            
            while (index < text.length && text[index].isDigit()) {
                digitCount++
                index++
            }
            
            if (digitCount > 0) {
                try {
                    channel = text.substring(1, digitCount + 1).toInt()
                    messageText = text.substring(digitCount + 1).trim()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        
        if (getModules()?.rlvController?.onSendLocalChat(channel, messageText) == true) {
            val message = ChatFromViewer().apply {
                AgentData_Field.AgentID = circuitInfo.agentID
                AgentData_Field.SessionID = circuitInfo.sessionID
                ChatData_Field.Channel = channel
                ChatData_Field.Type = 1
                ChatData_Field.Message = SLMessage.stringToVariableUTF(messageText)
                isReliable = true
            }
            SendMessage(message)
        }
    }

    fun SendChatMessage(chatterID: ChatterID, text: String) {
        when (chatterID.chatterType) {
            ChatterID.ChatterType.Group -> SendGroupInstantMessage(chatterID.optionalChatterUUID!!, text)
            ChatterID.ChatterType.LocalChat -> SendLocalChatMessage(text)
            ChatterID.ChatterType.User -> SendInstantMessage(chatterID.optionalChatterUUID!!, text)
        }
    }

    fun SendGenericMessage(method: String, params: Array<String>) {
        val message = GenericMessage().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            AgentData_Field.TransactionID = UUID(0, 0)
            MethodData_Field.Method = SLMessage.stringToVariableOEM(method)
            MethodData_Field.Invoice = UUID(0, 0)
            
            for (param in params) {
                ParamList_Fields.add(GenericMessage.ParamList().apply {
                    Parameter = SLMessage.stringToVariableOEM(param)
                })
            }
            
            isReliable = true
        }
        SendMessage(message)
    }

    fun SendScriptDialogReply(objectID: UUID, channel: Int, buttonIndex: Int, buttonLabel: String) {
        val message = ScriptDialogReply().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            Data_Field.ObjectID = objectID
            Data_Field.ChatChannel = channel
            Data_Field.ButtonIndex = buttonIndex
            Data_Field.ButtonLabel = SLMessage.stringToVariableUTF(buttonLabel)
            isReliable = true
        }
        SendMessage(message)
    }

    fun sendTypingNotify(toUUID: UUID, isTyping: Boolean) {
        SendInstantMessage(toUUID, "", if (isTyping) 41 else 42)
    }

    fun SendLogoutRequest() {
        Debug.Log("Logout: Sending logout request.")
        modules?.avatarControl?.setEnableAgentUpdates(false)
        
        val message = LogoutRequest().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            isReliable = true
        }
        
        message.setEventListener(object : SLMessageEventListener {
            override fun onMessageAcknowledged(msg: SLMessage) {
                Debug.Log("Logout: Logout request acknowledged.")
                gridConn.processDisconnect(true, "Logged out.")
            }
            
            override fun onMessageTimeout(msg: SLMessage) {
                Debug.Log("Logout: LogoutRequest timed out!")
                gridConn.processDisconnect(false, "Logout request has timed out.")
            }
        })
        
        SendMessage(message)
    }

    fun SendUseCode() {
        Debug.Printf("Using circuitCode: %d", circuitInfo.circuitCode)
        
        val message = UseCircuitCode().apply {
            CircuitCode_Field.Code = circuitInfo.circuitCode
            CircuitCode_Field.SessionID = circuitInfo.sessionID
            CircuitCode_Field.ID = circuitInfo.agentID
            isReliable = true
        }
        
        message.setEventListener(object : SLMessageEventListener {
            override fun onMessageAcknowledged(msg: SLMessage) {
                Debug.Log("SLAgentCircuit: UseCircuitCode acknowledged.")
                
                if (!authReply.isTemporary) {
                    if (authReply.fromTeleport) {
                        Debug.Log("SLAgentCircuit: Ack from teleport, sending Teleport success.")
                        eventBus.publish(SLTeleportResultEvent(true, null))
                    } else {
                        gridConn.notifyLoginSuccess()
                    }
                    
                    SendCompleteAgentMovement()
                    modules?.HandleCircuitReady()
                }
            }
            
            override fun onMessageTimeout(msg: SLMessage) {
                if (authReply.fromTeleport) {
                    eventBus.publish(SLTeleportResultEvent(false, "Timed out while connecting to the simulator."))
                } else {
                    gridConn.notifyLoginError("Timed out while connecting to the simulator.")
                }
            }
        })
        
        SendMessage(message)
    }

    // ========== Public Action Methods ==========

    fun AcceptFriendship(friendUUID: UUID, transactionID: UUID) {
        userManager?.chatterList?.friendManager?.addFriend(friendUUID)
        
        val folderID = modules?.inventory?.callingCardsFolderUUID ?: UUIDPool.ZeroUUID
        
        val message = AcceptFriendship().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            FolderData_Fields.add(AcceptFriendship.FolderData().apply {
                FolderID = folderID
            })
            TransactionBlock_Field.TransactionID = transactionID
            isReliable = true
        }
        SendMessage(message)
    }

    fun AcceptInventoryOffer(dialog: Int, accept: Boolean, fromUUID: UUID, sessionID: UUID, folderID: UUID?) {
        val message = ImprovedInstantMessage().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            MessageBlock_Field.FromGroup = false
            MessageBlock_Field.ToAgentID = fromUUID
            MessageBlock_Field.ParentEstateID = 0
            MessageBlock_Field.RegionID = UUID(0, 0)
            MessageBlock_Field.Position = LLVector3()
            MessageBlock_Field.Offline = 0
            MessageBlock_Field.Dialog = if (accept) dialog + 1 else dialog + 2
            MessageBlock_Field.ID = sessionID
            MessageBlock_Field.Timestamp = 0
            MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo")
            MessageBlock_Field.Message = SLMessage.stringToVariableUTF("")
            
            MessageBlock_Field.BinaryBucket = folderID?.let {
                ByteBuffer.allocate(16).apply {
                    order(ByteOrder.BIG_ENDIAN)
                    putLong(it.mostSignificantBits)
                    putLong(it.leastSignificantBits)
                    position(0)
                }.array()
            } ?: ByteArray(0)
            
            isReliable = true
        }
        SendMessage(message)
    }

    fun AddFriend(friendUUID: UUID, message: String) {
        val imMessage = ImprovedInstantMessage().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            MessageBlock_Field.FromGroup = false
            MessageBlock_Field.ToAgentID = friendUUID
            MessageBlock_Field.ParentEstateID = 0
            MessageBlock_Field.RegionID = UUID(0, 0)
            MessageBlock_Field.Position = LLVector3()
            MessageBlock_Field.Offline = 0
            MessageBlock_Field.Dialog = 38
            MessageBlock_Field.ID = UUID(
                friendUUID.mostSignificantBits xor circuitInfo.agentID.mostSignificantBits,
                friendUUID.leastSignificantBits xor circuitInfo.agentID.leastSignificantBits
            )
            MessageBlock_Field.Timestamp = 0
            MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo")
            MessageBlock_Field.Message = SLMessage.stringToVariableUTF(message)
            MessageBlock_Field.BinaryBucket = ByteArray(0)
            isReliable = true
        }
        SendMessage(imMessage)
    }

    fun RemoveFriend(friendUUID: UUID) {
        val message = TerminateFriendship().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            ExBlock_Field.OtherID = friendUUID
            isReliable = true
        }
        SendMessage(message)
        userManager?.chatterList?.friendManager?.removeFriend(friendUUID)
    }

    fun BuyObject(localID: Int, saleType: Byte, price: Int) {
        val groupID = getActiveGroupID() ?: UUIDPool.ZeroUUID
        
        val message = ObjectBuy().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            AgentData_Field.GroupID = groupID
            AgentData_Field.CategoryID = getModules()?.inventory?.rootFolder?.uuid ?: UUIDPool.ZeroUUID
            
            ObjectData_Fields.add(ObjectBuy.ObjectData().apply {
                ObjectLocalID = localID
                SaleType = saleType
                SalePrice = price
            })
            
            isReliable = true
        }
        SendMessage(message)
    }

    fun DerezObject(localID: Int, destination: EDeRezDestination) {
        val groupID = getActiveGroupID() ?: UUID(0, 0)
        
        val message = DeRezObject().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            AgentBlock_Field.GroupID = groupID
            AgentBlock_Field.Destination = destination.code
            AgentBlock_Field.DestinationID = UUID(0, 0)
            AgentBlock_Field.PacketCount = 1
            AgentBlock_Field.PacketNumber = 0
            AgentBlock_Field.TransactionID = UUID.randomUUID()
            
            ObjectData_Fields.add(DeRezObject.ObjectData().apply {
                ObjectLocalID = localID
            })
            
            isReliable = true
        }
        SendMessage(message)
    }

    fun DoRequestPayPrice(objectUUID: UUID) {
        val objectInfo = gridConn.parcelInfo.allObjectsNearby[objectUUID] ?: return
        
        if (objectInfo.payInfo != null) {
            eventBus.publish(SLObjectPayInfoEvent(objectInfo))
            return
        }
        
        val message = RequestPayPrice().apply {
            ObjectData_Field.ObjectID = objectUUID
            isReliable = true
        }
        SendMessage(message)
    }

    fun TouchObject(localID: Int) {
        // Send grab
        val grab = ObjectGrab().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            ObjectData_Field.LocalID = localID
            ObjectData_Field.GrabOffset = LLVector3()
            isReliable = true
        }
        SendMessage(grab)
        
        // Send degrab
        val degrab = ObjectDeGrab().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            ObjectData_Field.LocalID = localID
            isReliable = true
        }
        SendMessage(degrab)
    }

    fun TouchObjectFace(
        objectInfo: SLObjectInfo,
        faceIndex: Int,
        posX: Float, posY: Float, posZ: Float,
        uvU: Float, uvV: Float,
        stS: Float, stT: Float
    ) {
        Debug.Printf("Touch: Object %d, face %d, pos (%f, %f, %f), uv (%f, %f)",
            objectInfo.localID, faceIndex, posX, posY, posZ, uvU, uvV)
        
        // Send grab with surface info
        val grab = ObjectGrab().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            ObjectData_Field.LocalID = objectInfo.localID
            ObjectData_Field.GrabOffset = LLVector3()
            
            SurfaceInfo_Fields.add(ObjectGrab.SurfaceInfo().apply {
                FaceIndex = faceIndex
                Position = LLVector3(posX, posY, posZ)
                UVCoord = LLVector3(uvU, uvV, 0.0f)
                STCoord = LLVector3(stS, stT, 0.0f)
                Normal = LLVector3(1.0f, 0.0f, 0.0f)
                Binormal = LLVector3(0.0f, 0.0f, 1.0f)
            })
            
            isReliable = true
        }
        SendMessage(grab)
        
        // Send degrab
        val degrab = ObjectDeGrab().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            ObjectData_Field.LocalID = objectInfo.localID
            isReliable = true
        }
        SendMessage(degrab)
    }

    // ========== Teleport Methods ==========

    fun TeleportToLocalPosition(position: LLVector3): Boolean {
        val currentRegionID = regionID ?: return false
        
        Debug.Printf("Teleport: localPos = %s, regionHandle = %d", position.toString(), regionHandle)
        teleportRequestSent = true
        
        val message = TeleportLocationRequest().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            Info_Field.RegionHandle = regionHandle
            Info_Field.Position = position
            Info_Field.LookAt = LLVector3(position).apply { x += 10.0f }
            isReliable = true
        }
        
        message.setEventListener(object : SLMessageEventListener {
            override fun onMessageAcknowledged(msg: SLMessage) {}
            override fun onMessageTimeout(msg: SLMessage) {
                eventBus.publish(SLTeleportResultEvent(false, "Teleport request has timed out."))
            }
        })
        
        SendMessage(message)
        return true
    }

    fun TeleportToGlobalPosition(globalPos: LLVector3) {
        val regionX = floor(globalPos.x.toDouble()).toInt()
        val regionY = floor(globalPos.y.toDouble()).toInt()
        val alignedX = regionX - (regionX % 256)
        val alignedY = regionY - (regionY % 256)
        val regionHandle = (alignedX.toLong() shl 32) or (alignedY.toLong() and 0xFFFFFFFFL)
        
        val localPos = LLVector3(globalPos.x % 256.0f, globalPos.y % 256.0f, globalPos.z)
        val lookAt = LLVector3(localPos).apply { x += 1.0f }
        
        Debug.Printf("regionHandle = %s, globalPos = %s", regionHandle.toString(16), globalPos)
        teleportRequestSent = true
        
        val message = TeleportLocationRequest().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            Info_Field.RegionHandle = regionHandle
            Info_Field.Position = localPos
            Info_Field.LookAt = lookAt
            isReliable = true
        }
        
        message.setEventListener(object : SLMessageEventListener {
            override fun onMessageAcknowledged(msg: SLMessage) {}
            override fun onMessageTimeout(msg: SLMessage) {
                eventBus.publish(SLTeleportResultEvent(false, "Teleport request has timed out."))
            }
        })
        
        SendMessage(message)
    }

    fun TeleportToRegion(regionHandle: Long, x: Int, y: Int, z: Int) {
        if (getModules()?.rlvController?.canTeleportToLocation() == false) {
            return
        }
        
        Debug.Log("TeleportToRegion: regionHandle = ${regionHandle.toString(16)}, pos = ($x, $y, $z)")
        teleportRequestSent = true
        
        val message = TeleportLocationRequest().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            Info_Field.RegionHandle = regionHandle
            Info_Field.Position = LLVector3(x.toFloat(), y.toFloat(), z.toFloat())
            Info_Field.LookAt = LLVector3(0.0f, 1.0f, 0.0f)
            isReliable = true
        }
        
        message.setEventListener(object : SLMessageEventListener {
            override fun onMessageAcknowledged(msg: SLMessage) {}
            override fun onMessageTimeout(msg: SLMessage) {
                eventBus.publish(SLTeleportResultEvent(false, "Teleport request has timed out."))
            }
        })
        
        SendMessage(message)
    }

    fun TeleportToLandmarkAsset(landmarkUUID: UUID) {
        if (getModules()?.rlvController?.canTeleportToLandmark() == false) {
            return
        }
        
        teleportRequestSent = true
        
        val message = TeleportLandmarkRequest().apply {
            Info_Field.AgentID = circuitInfo.agentID
            Info_Field.SessionID = circuitInfo.sessionID
            Info_Field.LandmarkID = landmarkUUID
            isReliable = true
        }
        
        message.setEventListener(object : SLMessageEventListener {
            override fun onMessageAcknowledged(msg: SLMessage) {}
            override fun onMessageTimeout(msg: SLMessage) {
                eventBus.publish(SLTeleportResultEvent(false, "Teleport request has timed out."))
            }
        })
        
        SendMessage(message)
    }

    fun TeleportToLure(lureID: UUID) {
        teleportRequestSent = true
        
        val message = TeleportLureRequest().apply {
            Info_Field.AgentID = circuitInfo.agentID
            Info_Field.SessionID = circuitInfo.sessionID
            Info_Field.LureID = lureID
            isReliable = true
        }
        
        message.setEventListener(object : SLMessageEventListener {
            override fun onMessageAcknowledged(msg: SLMessage) {}
            override fun onMessageTimeout(msg: SLMessage) {
                eventBus.publish(SLTeleportResultEvent(false, "Teleport request has timed out."))
            }
        })
        
        SendMessage(message)
    }

    fun RequestTeleport(toUUID: UUID, message: String) {
        SendInstantMessage(toUUID, message, 26)
    }

    fun OfferTeleport(toUUID: UUID, message: String) {
        val startLure = StartLure().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            Info_Field.Message = SLMessage.stringToVariableUTF(message)
            TargetData_Fields.add(StartLure.TargetData().apply {
                TargetID = toUUID
            })
            isReliable = true
        }
        SendMessage(startLure)
    }

    fun OfferInventoryItem(toUUID: UUID, inventoryEntry: SLInventoryEntry) {
        userManager?.inventoryManager?.executor?.execute {
            handleInventoryItemOffer(inventoryEntry, toUUID)
        }
    }

    private fun handleInventoryItemOffer(inventoryEntry: SLInventoryEntry, toUUID: UUID) {
        val items = mutableListOf<SLInventoryEntry>(inventoryEntry)
        
        if (inventoryEntry.isFolder) {
            modules?.inventory?.CollectGiveableItems(inventoryEntry)?.let { items.addAll(it) }
        }
        
        val message = ImprovedInstantMessage().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            MessageBlock_Field.FromGroup = false
            MessageBlock_Field.ToAgentID = toUUID
            MessageBlock_Field.ParentEstateID = 0
            MessageBlock_Field.RegionID = UUID(0, 0)
            MessageBlock_Field.Position = LLVector3()
            MessageBlock_Field.Offline = 0
            MessageBlock_Field.Dialog = 4
            MessageBlock_Field.ID = UUID.randomUUID()
            MessageBlock_Field.Timestamp = 0
            MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo")
            MessageBlock_Field.Message = SLMessage.stringToVariableUTF(inventoryEntry.name)
            
            MessageBlock_Field.BinaryBucket = ByteBuffer.allocate(items.size * 17).apply {
                order(ByteOrder.BIG_ENDIAN)
                for (item in items) {
                    val assetType = if (item.isFolder) {
                        SLAssetType.AT_CATEGORY.typeCode
                    } else {
                        item.assetType
                    }
                    put(assetType)
                    putLong(item.uuid.mostSignificantBits)
                    putLong(item.uuid.leastSignificantBits)
                }
                position(0)
            }.array()
            
            isReliable = true
        }
        
        SendMessage(message)
        
        val event = SLChatInventoryItemOfferedByYouEvent(agentUUID, inventoryEntry.name)
        HandleChatEvent(ChatterID.getUserChatterID(agentUUID, toUUID), event, false)
    }

    fun RezObject(inventoryEntry: SLInventoryEntry) {
        // Determine group to use for rezzing
        var groupID = UUIDPool.ZeroUUID
        
        userManager?.currentLocationInfoSnapshot?.let { locationInfo ->
            locationInfo.parcelData()?.let { parcelData ->
                if (parcelData.isGroupOwned()) {
                    parcelData.ownerID?.let { groupID = it }
                }
            }
        }
        
        if (groupID == UUIDPool.ZeroUUID) {
            groupID = getActiveGroupID() ?: UUIDPool.ZeroUUID
        }
        
        // Create RezObject message
        val message = com.lumiyaviewer.lumiya.slproto.messages.RezObject().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            AgentData_Field.GroupID = groupID
            
            RezData_Field.FromTaskID = UUIDPool.ZeroUUID
            RezData_Field.BypassRaycast = true
            
            val agentPos = modules?.avatarControl?.agentPosition?.position ?: LLVector3()
            RezData_Field.RayStart = agentPos
            RezData_Field.RayEnd = LLVector3(agentPos).apply {
                // 1.5 meters in front of avatar
                x += 1.5f
            }
            
            RezData_Field.RayEndIsIntersection = true
            RezData_Field.RayTargetID = UUIDPool.ZeroUUID
            RezData_Field.RezSelected = false
            RezData_Field.RemoveItem = false
            RezData_Field.ItemFlags = 0
            RezData_Field.GroupMask = inventoryEntry.groupMask
            RezData_Field.EveryoneMask = inventoryEntry.everyoneMask
            RezData_Field.NextOwnerMask = inventoryEntry.nextOwnerMask
            
            InventoryData_Field.ItemID = inventoryEntry.uuid
            InventoryData_Field.FolderID = inventoryEntry.parentUUID
            InventoryData_Field.CreatorID = inventoryEntry.creatorUUID
            InventoryData_Field.OwnerID = inventoryEntry.ownerUUID
            InventoryData_Field.GroupID = inventoryEntry.groupUUID
            InventoryData_Field.BaseMask = inventoryEntry.baseMask
            InventoryData_Field.OwnerMask = inventoryEntry.ownerMask
            InventoryData_Field.GroupMask = inventoryEntry.groupMask
            InventoryData_Field.EveryoneMask = inventoryEntry.everyoneMask
            InventoryData_Field.NextOwnerMask = inventoryEntry.nextOwnerMask
            InventoryData_Field.GroupOwned = inventoryEntry.isGroupOwned
            InventoryData_Field.TransactionID = UUID.randomUUID()
            InventoryData_Field.Type = inventoryEntry.assetType
            InventoryData_Field.InvType = inventoryEntry.invType
            InventoryData_Field.Flags = inventoryEntry.flags
            InventoryData_Field.SaleType = inventoryEntry.saleType
            InventoryData_Field.SalePrice = inventoryEntry.salePrice
            InventoryData_Field.Name = SLMessage.stringToVariableOEM(inventoryEntry.name)
            InventoryData_Field.Description = SLMessage.stringToVariableOEM(inventoryEntry.description)
            InventoryData_Field.CreationDate = inventoryEntry.creationDate
            InventoryData_Field.CRC = 0
            
            isReliable = true
        }
        
        // If item is not copyable, refresh inventory after rez
        if ((inventoryEntry.ownerMask and 0x8000) == 0) {
            message.setEventListener(object : SLMessageEventListener {
                override fun onMessageAcknowledged(msg: SLMessage) {
                    userManager?.inventoryManager?.requestFolderContents(inventoryEntry.parentUUID)
                }
                override fun onMessageTimeout(msg: SLMessage) {
                    Debug.Log("RezObject message timed out")
                }
            })
        }
        
        SendMessage(message)
    }

    fun RestartRegion(seconds: Int): Boolean {
        if (!isEstateManager) {
            return false
        }
        
        SendEstateOwnerMessage("restart", arrayOf(seconds.toString()))
        return true
    }

    fun StartGroupSessionForVoice(groupUUID: UUID) {
        var needsStart = false
        
        synchronized(startedGroupSessions) {
            if (!startedGroupSessions.contains(groupUUID)) {
                SendGroupSessionStart(groupUUID)
                needsStart = true
            }
        }
        
        if (!needsStart) {
            modules?.voice?.onGroupSessionReady(groupUUID)
        }
    }

    fun GenerateChatMoneyEvent(sourceUUID: UUID?, balanceChange: Int, newBalance: Int) {
        val chatterID = sourceUUID?.let { ChatterID.getUserChatterID(agentUUID, it) } ?: localChatterID
        val source = sourceUUID?.let { ChatMessageSourceUser(it) } ?: ChatMessageSourceUnknown.getInstance()
        val event = SLChatBalanceChangedEvent(source, agentUUID, true, balanceChange, newBalance)
        
        HandleChatEvent(chatterID, event, true)
        modules?.financialInfo?.RecordChatEvent(sourceUUID, balanceChange, newBalance)
    }

    // ========== Helper Methods ==========

    private fun getActiveGroupID(): UUID? {
        return modules?.groupManager?.activeGroupID
    }

    private fun getNeedObjectNames(): Boolean {
        if (forceNeedObjectNames.isNotEmpty()) {
            return true
        }
        return modules?.drawDistance?.isObjectSelectEnabled() ?: false
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

    private fun processMyAvatarUpdate(avatarInfo: SLObjectAvatarInfo) {
        modules?.avatarControl?.setAgentPosition(
            avatarInfo.absolutePosition,
            avatarInfo.objectCoords.get(2)
        )
    }

    fun processMyAttachmentUpdate(objectInfo: SLObjectInfo) {
        if (objectInfo.nameKnown || objectInfo.isDead) return
        
        RequestObjectName(objectInfo)
        getModules()?.avatarAppearance?.UpdateMyAttachments()
    }

    fun RequestObjectName(objectInfo: SLObjectInfo) {
        val objectID = objectInfo.id ?: return
        
        if (!objectNamesRequested.containsKey(objectID) && !forceNeedObjectNames.containsKey(objectID)) {
            forceNeedObjectNames[objectID] = objectInfo
        }
        
        TryWakeUp()
    }

    private fun ProcessObjectSelection() {
        if (!getNeedObjectNames() || doingObjectSelection) return
        
        var selectMessage: ObjectSelect? = null
        
        // Process force-requested object names
        for (objectInfo in forceNeedObjectNames.values) {
            if (selectMessage == null) {
                selectMessage = ObjectSelect().apply {
                    AgentData_Field.AgentID = circuitInfo.agentID
                    AgentData_Field.SessionID = circuitInfo.sessionID
                }
            }
            
            if (selectMessage.ObjectData_Fields.size > 16) break
            
            selectMessage.ObjectData_Fields.add(ObjectSelect.ObjectData().apply {
                ObjectLocalID = objectInfo.localID
            })
            
            objectInfo.nameRequested = true
            objectInfo.nameRequestedAt = System.currentTimeMillis()
            objectNamesRequested[objectInfo.id!!] = objectInfo
        }
        
        // Process queued object name requests
        synchronized(gridConn.parcelInfo.objectNamesQueue) {
            for (objectInfo in gridConn.parcelInfo.objectNamesQueue.values) {
                if (selectMessage == null) {
                    selectMessage = ObjectSelect().apply {
                        AgentData_Field.AgentID = circuitInfo.agentID
                        AgentData_Field.SessionID = circuitInfo.sessionID
                    }
                }
                
                if (selectMessage.ObjectData_Fields.size > 16) break
                
                selectMessage.ObjectData_Fields.add(ObjectSelect.ObjectData().apply {
                    ObjectLocalID = objectInfo.localID
                })
                
                objectInfo.nameRequested = true
                objectInfo.nameRequestedAt = System.currentTimeMillis()
                objectNamesRequested[objectInfo.id!!] = objectInfo
            }
        }
        
        selectMessage?.let {
            Debug.Log("ObjectSelect: Sending ObjectSelect for ${it.ObjectData_Fields.size} objects, " +
                "${gridConn.parcelInfo.objectNamesQueue.size} remains.")
            it.isReliable = true
            SendMessage(it)
            lastObjectSelection = System.currentTimeMillis()
            doingObjectSelection = true
        }
    }

    private fun ProcessObjectSelectionTimeout() {
        for (objectInfo in objectNamesRequested.values) {
            val queuedInfo = gridConn.parcelInfo.objectNamesQueue.remove(objectInfo.id)
            queuedInfo?.let {
                gridConn.parcelInfo.objectNamesQueue[it.id!!] = it
            }
            forceNeedObjectNames.remove(objectInfo.id)
        }
        objectNamesRequested.clear()
    }
}
