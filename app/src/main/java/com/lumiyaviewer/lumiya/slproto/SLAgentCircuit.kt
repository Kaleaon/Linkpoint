package com.lumiyaviewer.lumiya.slproto

import android.annotation.SuppressLint
import com.google.common.base.Objects
import com.google.common.base.Strings
import com.google.common.logging.nano.Vr.VREvent.VrCore.ErrorCode
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
import com.lumiyaviewer.lumiya.slproto.events.SLObjectPayInfoEvent
import com.lumiyaviewer.lumiya.slproto.events.SLRegionInfoChangedEvent
import com.lumiyaviewer.lumiya.slproto.events.SLTeleportResultEvent
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.lumiya.slproto.messages.*
import com.lumiyaviewer.lumiya.slproto.messages.AcceptFriendship.FolderData
import com.lumiyaviewer.lumiya.slproto.messages.DeRezObject.AgentBlock
import com.lumiyaviewer.lumiya.slproto.messages.EstateOwnerMessage.ParamList
import com.lumiyaviewer.lumiya.slproto.messages.ObjectBuy.AgentData
import com.lumiyaviewer.lumiya.slproto.messages.ObjectGrab.SurfaceInfo
import com.lumiyaviewer.lumiya.slproto.messages.ObjectSelect.ObjectData
import com.lumiyaviewer.lumiya.slproto.messages.PayPriceReply.ButtonData
import com.lumiyaviewer.lumiya.slproto.messages.ScriptDialog.Buttons
import com.lumiyaviewer.lumiya.slproto.messages.StartLure.TargetData
import com.lumiyaviewer.lumiya.slproto.modules.SLModules
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteType
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.SLMuteList
import com.lumiyaviewer.lumiya.slproto.objects.PayInfo
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData
import com.lumiyaviewer.lumiya.slproto.objects.UnsupportedObjectTypeException
import com.lumiyaviewer.lumiya.slproto.types.EDeRezDestination
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.ChatterID.*
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUser
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.utils.UUIDPool
import java.io.IOException
import java.io.UnsupportedEncodingException
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
    private val caps: SLCaps,
    tempCircuit: SLTempCircuit?
) : SLThreadingCircuit(gridConnection, circuitInfo, authReply, tempCircuit), ICapsEventHandler {

    private var agentNameSubscription: Subscription? = null
    private var agentPaused = false
    private val agentUUID: UUID = circuitInfo.agentID
    private val agentUserName = AtomicReference<UserName?>(null)
    private val capsEventQueue = ConcurrentLinkedQueue<CapsEvent>()
    private var doingObjectSelection = false
    private val eventBus: EventBus = EventBus.getInstance()
    private val forceNeedObjectNames = ConcurrentHashMap<UUID, SLObjectInfo>()
    private var isEstateManager = false
    private var lastObjectSelection: Long = 0
    private var lastPauseId = 0
    private var lastVisibleActivities: Long = 0
    private val localChatterID: ChatterID
    private val modules: SLModules?
    private val objectNamesRequested = ConcurrentHashMap<UUID, SLObjectInfo>()
    
    private val objectPropertiesRateLimiter = object : EventRateLimiter(eventBus, 500) {
        override fun getEventToFire(): Any? = null
        
        override fun onActualFire() {
            notifyObjectPropertiesChange()
        }
    }
    
    private var pendingGroupMessages = LinkedList<ImprovedInstantMessage>()
    private var regionHandle: Long = 0
    private var regionID: UUID? = null
    private var regionName: String? = null
    private val startedGroupSessions = HashSet<UUID>()
    private var teleportRequestSent = false
    private val typingUsers = Collections.synchronizedSet(HashSet<UUID>())
    private val userManager: UserManager

    init {
        this.localChatterID = ChatterID.getLocalChatterID(agentUUID)
        this.lastVisibleActivities = System.currentTimeMillis()
        this.userManager = UserManager.getUserManager(circuitInfo.agentID)
        
        this.modules = if (caps == null || authReply.isTemporary) {
            null
        } else {
            SLModules(this, caps, gridConnection)
        }
        
        if (!authReply.isTemporary && userManager != null) {
            userManager.setActiveAgentCircuit(this)
        }
        
        tempCircuit?.pendingMessages?.forEach { message ->
            message.handleMessage(this)
        }
    }

    private fun doAgentPause() {
        agentPaused = true
        Debug.Log("AgentPause: Sending agentPause with ID = $lastPauseId")
        
        val agentPause = AgentPause().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            AgentData_Field.SerialNum = lastPauseId
            isReliable = true
        }
        
        sendMessage(agentPause)
        lastPauseId++
    }

    private fun doAgentResume() {
        agentPaused = false
        Debug.Log("AgentPause: Sending agentResume with ID = $lastPauseId")
        
        val agentResume = AgentResume().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            AgentData_Field.SerialNum = lastPauseId
            isReliable = true
        }
        
        sendMessage(agentResume)
        lastPauseId++
    }

    private fun handleCapsEvent(capsEvent: CapsEvent) {
        when (capsEvent.eventType) {
            CapsEventType.ChatterBoxInvitation -> handleChatterBoxInvitation(capsEvent.eventBody)
            CapsEventType.ChatterBoxSessionStartReply -> handleChatterBoxSessionStartReply(capsEvent.eventBody)
            CapsEventType.EstablishAgentCommunication -> handleEstablishAgentCommunication(capsEvent.eventBody)
            CapsEventType.TeleportFailed -> handleTeleportFailed(capsEvent.eventBody)
            CapsEventType.TeleportFinish -> handleTeleportFinish(capsEvent.eventBody)
            else -> defaultEventQueueHandler(capsEvent.eventType, capsEvent.eventBody)
        }
    }

    private fun handleChatterBoxInvitation(node: LLSDNode) {
        try {
            Debug.Log("ChatterBoxInvitation: event = ${node.serializeToXML()}")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        
        try {
            val sessionId = UUID.fromString(node.byKey("session_id").asString())
            val avatarGroupList = userManager.chatterList.groupManager.avatarGroupList
            var avatarGroupEntry = avatarGroupList?.Groups?.get(sessionId)
            
            val messageParams = node.byKey("instantmessage").byKey("message_params")
            val fromId = if (messageParams.keyExists("from_id")) {
                messageParams.byKey("from_id").asUUID()
            } else null
            
            val toId = messageParams.byKey("to_id").asUUID()
            val message = messageParams.byKey("message").asString()
            
            if (avatarGroupEntry == null) {
                avatarGroupEntry = avatarGroupList?.Groups?.get(toId)
            }
            
            if (avatarGroupEntry == null || fromId == null) {
                Debug.Log("ChatterBoxInvitation: chat from unknown group ($sessionId), to_id = $toId")
            } else {
                handleChatEvent(
                    ChatterID.getGroupChatterID(agentUUID, avatarGroupEntry.GroupID),
                    SLChatTextEvent(ChatMessageSourceUser(fromId), agentUUID, message),
                    true
                )
            }
        } catch (e: LLSDException) {
            Debug.Log("ChatterBoxInvitation: LLSDException ${e.message}")
            e.printStackTrace()
        }
    }

    private fun handleChatterBoxSessionStartReply(node: LLSDNode) {
        try {
            Debug.Log("ChatterBoxSessionStartReply: event = ${node.serializeToXML()}")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        
        try {
            val sessionId = node.byKey("session_id").asUUID()
            modules?.voice?.onGroupSessionReady(sessionId)
            
            synchronized(startedGroupSessions) {
                startedGroupSessions.add(sessionId)
                val iterator = pendingGroupMessages.iterator()
                while (iterator.hasNext()) {
                    val message = iterator.next()
                    if (message.MessageBlock_Field.ID == sessionId) {
                        iterator.remove()
                        sendMessage(message)
                    }
                }
            }
        } catch (e: LLSDException) {
            Debug.Log("ChatterBoxSessionStartReply: LLSDException ${e.message}")
            e.printStackTrace()
        }
    }

    private fun handleChatterOnlineStatus(chatterID: ChatterID, isOnline: Boolean) {
        if (userManager.isChatterActive(chatterID) && chatterID is ChatterIDUser) {
            handleChatEvent(
                chatterID,
                SLChatOnlineOfflineEvent(
                    ChatMessageSourceUser(chatterID.chatterUUID),
                    agentUUID,
                    isOnline
                ),
                false
            )
        }
    }

    private fun handleEstablishAgentCommunication(node: LLSDNode) {
        if (!teleportRequestSent) return
        
        try {
            Debug.Log("EstablishAgentCommunication: event = ${node.serializeToXML()}")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        
        try {
            val simIpAndPort = node.byKey("sim-ip-and-port").asString()
            val seedCapability = node.byKey("seed-capability").asString()
            val agentId = node.byKey("agent-id").asUUID()
            
            val parts = simIpAndPort.split(":")
            gridConn.addTempCircuit(
                SLAuthReply(
                    authReply,
                    true,
                    true,
                    agentId,
                    parts[0],
                    parts[1].toInt(),
                    seedCapability
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleGroupNotice(message: ImprovedInstantMessage, source: ChatMessageSource) {
        val buffer = ByteBuffer.wrap(message.MessageBlock_Field.BinaryBucket)
        
        if (buffer.limit() < 18) return
        
        buffer.order(ByteOrder.BIG_ENDIAN)
        val hasItem = buffer.get()
        val assetType = buffer.get()
        val groupUUID = UUID(buffer.long, buffer.long)
        
        var itemName = ""
        if (hasItem.toInt() != 0) {
            val nameBytes = ByteArray(buffer.remaining())
            buffer.get(nameBytes)
            itemName = SLMessage.stringFromVariableOEM(nameBytes)
        }
        
        Debug.Log("HandleGroupNotice: group UUID = $groupUUID")
        
        val groupChatterID = ChatterID.getGroupChatterID(agentUUID, groupUUID)
        val isFromMe = Objects.equal(source.sourceUUID, circuitInfo.agentID)
        
        var messageText = SLMessage.stringFromVariableUTF(message.MessageBlock_Field.Message)
        val pipeIndex = messageText.indexOf(ErrorCode.CONTROLLER_GATT_NOTIFY_FAILED)
        
        if (pipeIndex >= 0) {
            val before = messageText.substring(0, pipeIndex)
            messageText = "$before\n${messageText.substring(pipeIndex + 1)}"
        }
        
        if (isFromMe && hasItem.toInt() != 0) {
            messageText = "$messageText\n(This notice contains attached item '$itemName')"
        }
        
        handleChatEvent(
            groupChatterID,
            SLChatTextEvent(source, agentUUID, message, messageText),
            true
        )
        
        if (hasItem.toInt() != 0 && !isFromMe) {
            handleChatEvent(
                groupChatterID,
                SLChatInventoryItemOfferedByGroupNoticeEvent(
                    source,
                    agentUUID,
                    message,
                    itemName,
                    SLAssetType.getByType(assetType)
                ),
                false
            )
        }
    }

    private fun handleIM(message: ImprovedInstantMessage, source: ChatMessageSource) {
        // Check if RLV controller should handle this IM
        modules?.rlvController?.let {
            if (it.onIncomingIM(message)) return
        }
        
        when (message.MessageBlock_Field.Dialog.toInt()) {
            0, 20 -> handlePersonalMessage(message, source)
            1, 2 -> handleSystemMessage(message)
            3 -> handleGroupInvitation(message, source)
            4 -> handleInventoryOffered(message, source)
            9 -> handleInventoryOfferedFromObject(message)
            17 -> handleSessionIM(message, source)
            19, 31 -> handleObjectMessage(message, source)
            22 -> handleTeleportLure(message, source)
            26 -> handleLureRequest(message, source)
            32, 37 -> handleGroupNotice(message, source)
            38 -> handleFriendshipOffered(message, source)
            39, 40 -> handleFriendshipResult(message, source)
            41 -> handleTypingNotification(source, true)
            42 -> handleTypingNotification(source, false)
            else -> {
                Debug.Log(
                    "HandleIM: unknown type = ${message.MessageBlock_Field.Dialog}, " +
                    "sessionId = ${message.AgentData_Field.SessionID}, " +
                    "toAgentID = ${message.MessageBlock_Field.ToAgentID}, " +
                    "fromGroup = ${message.MessageBlock_Field.FromGroup}, " +
                    "message = '${SLMessage.stringFromVariableUTF(message.MessageBlock_Field.Message)}'"
                )
            }
        }
    }

    private fun handlePersonalMessage(message: ImprovedInstantMessage, source: ChatMessageSource) {
        val chatEvent = SLChatTextEvent(source, agentUUID, message, null)
        val defaultChatter = source.getDefaultChatter(agentUUID)
        val isChatterActive = userManager.isChatterActive(defaultChatter)
        
        handleChatEvent(defaultChatter, chatEvent, true)
        
        // Auto-response logic
        if (!userManager.isChatterMuted(defaultChatter) &&
            message.MessageBlock_Field.Dialog.toInt() != 20 &&
            message.MessageBlock_Field.Offline.toInt() == 0 &&
            message.MessageBlock_Field.Message.isNotEmpty() &&
            !isChatterActive &&
            defaultChatter is ChatterIDUser
        ) {
            val autoResponse = SLGridConnection.getAutoresponse()
            if (!Strings.isNullOrEmpty(autoResponse)) {
                sendInstantMessage(defaultChatter.chatterUUID, autoResponse, 20)
            }
        }
    }

    private fun handleSystemMessage(message: ImprovedInstantMessage) {
        val systemEvent = SLChatSystemMessageEvent(
            ChatMessageSourceUnknown.getInstance(),
            agentUUID,
            SLMessage.stringFromVariableUTF(message.MessageBlock_Field.Message)
        )
        handleChatEvent(localChatterID, systemEvent, true)
    }

    private fun handleGroupInvitation(message: ImprovedInstantMessage, source: ChatMessageSource) {
        val invitationEvent = SLChatGroupInvitationEvent(source, agentUUID, message)
        handleChatEvent(source.getDefaultChatter(agentUUID), invitationEvent, true)
    }

    private fun handleInventoryOffered(message: ImprovedInstantMessage, source: ChatMessageSource) {
        val inventoryEvent = SLChatInventoryItemOfferedEvent(source, agentUUID, message)
        handleChatEvent(source.getDefaultChatter(agentUUID), inventoryEvent, true)
    }

    private fun handleInventoryOfferedFromObject(message: ImprovedInstantMessage) {
        val objectSource = ChatMessageSourceObject(
            message.AgentData_Field.AgentID,
            SLMessage.stringFromVariableOEM(message.MessageBlock_Field.FromAgentName)
        )
        
        val inventoryEvent = SLChatInventoryItemOfferedEvent(objectSource, agentUUID, message)
        handleChatEvent(localChatterID, inventoryEvent, true)
    }

    private fun handleObjectMessage(message: ImprovedInstantMessage, source: ChatMessageSource) {
        val chatEvent = SLChatTextEvent(source, agentUUID, message, null)
        handleChatEvent(source.getDefaultChatter(agentUUID), chatEvent, true)
    }

    private fun handleTeleportLure(message: ImprovedInstantMessage, source: ChatMessageSource) {
        if (source.sourceType == ChatMessageSourceType.User) {
            val sourceUUID = source.sourceUUID
            modules?.let { mod ->
                if (mod.rlvController.autoAcceptTeleport(sourceUUID)) {
                    teleportToLure(message.MessageBlock_Field.ID)
                    return
                } else if (!mod.rlvController.canTeleportToLure(sourceUUID)) {
                    return
                }
            }
        }
        
        val lureEvent = SLChatLureEvent(source, agentUUID, message)
        handleChatEvent(source.getDefaultChatter(agentUUID), lureEvent, true)
    }

    private fun handleLureRequest(message: ImprovedInstantMessage, source: ChatMessageSource) {
        if (source.sourceType == ChatMessageSourceType.User) {
            modules?.let { mod ->
                if (!mod.rlvController.canTeleportToLure(source.sourceUUID)) {
                    return
                }
            }
        }
        
        val lureRequestEvent = SLChatLureRequestEvent(source, agentUUID, message)
        handleChatEvent(source.getDefaultChatter(agentUUID), lureRequestEvent, true)
    }

    private fun handleFriendshipOffered(message: ImprovedInstantMessage, source: ChatMessageSource) {
        val friendshipEvent = SLChatFriendshipOfferedEvent(source, agentUUID, message)
        handleChatEvent(source.getDefaultChatter(agentUUID), friendshipEvent, true)
    }

    private fun handleFriendshipResult(message: ImprovedInstantMessage, source: ChatMessageSource) {
        val friendshipResultEvent = SLChatFriendshipResultEvent(source, agentUUID, message)
        handleChatEvent(source.getDefaultChatter(agentUUID), friendshipResultEvent, true)
        
        // If friendship accepted, add to friend list and request online notification
        if (message.MessageBlock_Field.Dialog.toInt() == 39) {
            if (source.sourceType == ChatMessageSourceType.User) {
                val sourceUUID = source.sourceUUID
                if (sourceUUID != null) {
                    userManager.chatterList.friendManager.addFriend(sourceUUID)
                    sendGenericMessage("requestonlinenotification", arrayOf(sourceUUID.toString()))
                }
            }
        }
    }

    private fun handleSessionIM(message: ImprovedInstantMessage, source: ChatMessageSource) {
        handleChatEvent(
            ChatterID.getGroupChatterID(agentUUID, message.MessageBlock_Field.ID),
            SLChatTextEvent(source, agentUUID, message, null),
            true
        )
    }

    private fun handleTeleportFailed(node: LLSDNode) {
        try {
            Debug.Log("TeleportFailed: event = ${node.serializeToXML()}")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        
        if (teleportRequestSent) {
            teleportRequestSent = false
            eventBus.publish(SLTeleportResultEvent(false, "Teleport has failed."))
        }
    }

    private fun handleTeleportFinish(node: LLSDNode) {
        try {
            Debug.Log("TeleportFinish: event = ${node.serializeToXML()}")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        
        if (!teleportRequestSent) {
            Debug.Log("TeleportFinish: stale teleport finish?")
            return
        }
        
        teleportRequestSent = false
        
        try {
            val info = node.byKey("Info").byIndex(0)
            val seedCapability = info.byKey("SeedCapability").asString()
            val simIP = info.byKey("SimIP").asBinary()
            
            val newAuthReply = SLAuthReply(
                authReply,
                true,
                false,
                authReply.agentID,
                "${simIP[0].toInt() and 0xFF}.${simIP[1].toInt() and 0xFF}.${simIP[2].toInt() and 0xFF}.${simIP[3].toInt() and 0xFF}",
                info.byKey("SimPort").asInt(),
                seedCapability
            )
            
            Debug.Printf("new sim address: %s", newAuthReply.simAddress)
            
            modules?.avatarControl?.setEnableAgentUpdates(false)
            gridConn.handleTeleportFinish(newAuthReply)
        } catch (e: LLSDException) {
            Debug.Log("TeleportFinish: LLSDException, teleport apparently failed")
            e.printStackTrace()
        }
    }

    private fun handleTypingNotification(source: ChatMessageSource, isTyping: Boolean) {
        if (source !is ChatMessageSourceUser) return
        
        val sourceUUID = source.sourceUUID ?: return
        
        if (isTyping) {
            if (typingUsers.add(sourceUUID)) {
                userManager.chatterList.updateUserTypingStatus(sourceUUID)
            }
        } else {
            if (typingUsers.remove(sourceUUID)) {
                userManager.chatterList.updateUserTypingStatus(sourceUUID)
            }
        }
    }

    private fun processObjectSelection() {
        if (!needObjectNames || doingObjectSelection) return
        
        var message: ObjectSelect? = null
        
        for (objectInfo in forceNeedObjectNames.values) {
            if (message == null) {
                message = ObjectSelect().apply {
                    AgentData_Field.AgentID = circuitInfo.agentID
                    AgentData_Field.SessionID = circuitInfo.sessionID
                }
            }
            
            if (message.ObjectData_Fields.size > 16) break
            
            val objectData = ObjectData().apply {
                ObjectLocalID = objectInfo.localID
            }
            message.ObjectData_Fields.add(objectData)
            
            objectInfo.nameRequested = true
            objectInfo.nameRequestedAt = System.currentTimeMillis()
            objectNamesRequested[objectInfo.id] = objectInfo
        }
        
        synchronized(gridConn.parcelInfo.objectNamesQueue) {
            for (objectInfo in gridConn.parcelInfo.objectNamesQueue.values) {
                if (message == null) {
                    message = ObjectSelect().apply {
                        AgentData_Field.AgentID = circuitInfo.agentID
                        AgentData_Field.SessionID = circuitInfo.sessionID
                    }
                }
                
                if (message.ObjectData_Fields.size > 16) break
                
                val objectData = ObjectData().apply {
                    ObjectLocalID = objectInfo.localID
                }
                message.ObjectData_Fields.add(objectData)
                
                objectInfo.nameRequested = true
                objectInfo.nameRequestedAt = System.currentTimeMillis()
                objectNamesRequested[objectInfo.id] = objectInfo
            }
        }
        
        message?.let { msg ->
            Debug.Log(
                "ObjectSelect: Sending ObjectSelect for ${msg.ObjectData_Fields.size} objects, " +
                "${gridConn.parcelInfo.objectNamesQueue.size} remains."
            )
            msg.isReliable = true
            sendMessage(msg)
            lastObjectSelection = System.currentTimeMillis()
            doingObjectSelection = true
        }
    }

    private fun processObjectSelectionTimeout() {
        for (objectInfo in objectNamesRequested.values) {
            val removed = gridConn.parcelInfo.objectNamesQueue.remove(objectInfo.id)
            removed?.let {
                gridConn.parcelInfo.objectNamesQueue[it.id] = it
            }
            forceNeedObjectNames.remove(objectInfo.id)
        }
        objectNamesRequested.clear()
    }

    private fun sendAgentFOV() {
        val agentFOV = AgentFOV().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            AgentData_Field.CircuitCode = circuitInfo.circuitCode
            FOVBlock_Field.GenCounter = 0
            FOVBlock_Field.VerticalAngle = 3.0543263f
            isReliable = true
        }
        sendMessage(agentFOV)
    }

    private fun sendCompleteAgentMovement() {
        val completeAgentMovement = CompleteAgentMovement().apply {
            AgentData_Field.CircuitCode = circuitInfo.circuitCode
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            isReliable = true
        }
        sendMessage(completeAgentMovement)
    }

    private fun sendEstateOwnerMessage(method: String, params: Array<String>) {
        val estateOwnerMessage = EstateOwnerMessage().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            AgentData_Field.TransactionID = UUID(0, 0)
            MethodData_Field.Method = SLMessage.stringToVariableOEM(method)
            MethodData_Field.Invoice = UUID(0, 0)
            
            params.forEach { param ->
                ParamList_Fields.add(ParamList().apply {
                    Parameter = SLMessage.stringToVariableOEM(param)
                })
            }
            
            isReliable = true
        }
        sendMessage(estateOwnerMessage)
    }

    private fun sendGroupSessionStart(groupUUID: UUID) {
        val message = ImprovedInstantMessage().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            MessageBlock_Field.FromGroup = false
            MessageBlock_Field.ToAgentID = groupUUID
            MessageBlock_Field.ParentEstateID = 0
            MessageBlock_Field.RegionID = UUID(0, 0)
            MessageBlock_Field.Position = modules!!.avatarControl.agentPosition.position
            MessageBlock_Field.Offline = 0
            MessageBlock_Field.Dialog = 15
            MessageBlock_Field.ID = groupUUID
            MessageBlock_Field.Timestamp = 0
            MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo")
            MessageBlock_Field.Message = SLMessage.stringToVariableUTF("")
            MessageBlock_Field.BinaryBucket = ByteArray(1)
            isReliable = true
        }
        sendMessage(message)
    }

    private fun sendInstantMessage(targetUUID: UUID, message: String, dialogType: Int): Boolean {
        val rlv = modules?.rlvController
        if (rlv != null && !rlv.canSendIM(targetUUID)) {
            return false
        }
            return false
        }
        
        val imMessage = ImprovedInstantMessage().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            MessageBlock_Field.FromGroup = false
            MessageBlock_Field.ToAgentID = targetUUID
            MessageBlock_Field.ParentEstateID = 0
            MessageBlock_Field.RegionID = UUID(0, 0)
            MessageBlock_Field.Position = LLVector3()
            MessageBlock_Field.Offline = 0
            MessageBlock_Field.Dialog = dialogType.toByte()
            MessageBlock_Field.ID = UUID(
                targetUUID.mostSignificantBits xor circuitInfo.agentID.mostSignificantBits,
                targetUUID.leastSignificantBits xor circuitInfo.agentID.leastSignificantBits
            )
            MessageBlock_Field.Timestamp = 0
            MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo")
            MessageBlock_Field.Message = SLMessage.stringToVariableUTF(message)
            MessageBlock_Field.BinaryBucket = ByteArray(0)
            isReliable = true
        }
        
        sendMessage(imMessage)
        
        when (dialogType) {
            20, 41, 42 -> {} // No chat event for these types
            26 -> handleChatEvent(
                ChatterID.getUserChatterID(agentUUID, targetUUID),
                SLChatLureRequestedEvent(message, agentUUID),
                false
            )
            else -> handleChatEvent(
                ChatterID.getUserChatterID(agentUUID, targetUUID),
                SLChatTextEvent(ChatMessageSourceUser(circuitInfo.agentID), agentUUID, message),
                false
            )
        }
        
        return true
    }

    private fun sendRetrieveInstantMessages() {
        val retrieveInstantMessages = RetrieveInstantMessages().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            isReliable = true
        }
        sendMessage(retrieveInstantMessages)
    }

    private fun getActiveGroupID(): UUID? {
        return modules?.groupManager?.activeGroupID
    }

    private val needObjectNames: Boolean
        get() {
            if (forceNeedObjectNames.isNotEmpty()) return true
            return modules?.drawDistance?.isObjectSelectEnabled() ?: false
        }

    private fun isEventMuted(chatterID: ChatterID, event: SLChatEvent): Boolean {
        modules?.let { mod ->
            val muteList = mod.muteList
            val source = event.source
            
            when (source.sourceType) {
                ChatMessageSourceType.User -> {
                    if (muteList.isMuted(source.sourceUUID, MuteType.AGENT)) {
                        return true
                    }
                }
                ChatMessageSourceType.Object -> {
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
            
            if (chatterID is ChatterIDGroup) {
                val chatterUUID = chatterID.chatterUUID
                if (chatterUUID != UUIDPool.ZeroUUID && muteList.isMuted(chatterUUID, MuteType.GROUP)) {
                    return true
                }
            }
        }
        
        return false
    }

    private fun notifyObjectPropertiesChange() {
        userManager.objectsManager.requestObjectListUpdate()
    }

    private fun processMyAvatarUpdate(avatarInfo: SLObjectAvatarInfo) {
        modules?.avatarControl?.setAgentPosition(
            avatarInfo.absolutePosition,
            avatarInfo.objectCoords[2]
        )
    }

    fun acceptFriendship(friendUUID: UUID, transactionID: UUID) {
        userManager.chatterList.friendManager.addFriend(friendUUID)
        
        val callingCardsFolderUUID = modules?.inventory?.callingCardsFolderUUID ?: UUIDPool.ZeroUUID
        
        val message = AcceptFriendship().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            FolderData_Fields.add(FolderData().apply {
                FolderID = callingCardsFolderUUID
            })
            TransactionBlock_Field.TransactionID = transactionID
            isReliable = true
        }
        
        sendMessage(message)
    }

    fun acceptInventoryOffer(
        dialogType: Int,
        accept: Boolean,
        fromAgentUUID: UUID,
        sessionID: UUID,
        folderUUID: UUID?
    ) {
        val message = ImprovedInstantMessage().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            MessageBlock_Field.FromGroup = false
            MessageBlock_Field.ToAgentID = fromAgentUUID
            MessageBlock_Field.ParentEstateID = 0
            MessageBlock_Field.RegionID = UUID(0, 0)
            MessageBlock_Field.Position = LLVector3()
            MessageBlock_Field.Offline = 0
            MessageBlock_Field.Dialog = (if (accept) dialogType + 1 else dialogType + 2).toByte()
            MessageBlock_Field.ID = sessionID
            MessageBlock_Field.Timestamp = 0
            MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo")
            MessageBlock_Field.Message = SLMessage.stringToVariableUTF("")
            
            MessageBlock_Field.BinaryBucket = if (folderUUID != null) {
                ByteBuffer.wrap(ByteArray(16)).apply {
                    order(ByteOrder.BIG_ENDIAN)
                    putLong(folderUUID.mostSignificantBits)
                    putLong(folderUUID.leastSignificantBits)
                    position(0)
                }.array()
            } else {
                ByteArray(0)
            }
            
            isReliable = true
        }
        
        sendMessage(message)
    }

    fun addFriend(targetUUID: UUID, message: String) {
        val imMessage = ImprovedInstantMessage().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            MessageBlock_Field.FromGroup = false
            MessageBlock_Field.ToAgentID = targetUUID
            MessageBlock_Field.ParentEstateID = 0
            MessageBlock_Field.RegionID = UUID(0, 0)
            MessageBlock_Field.Position = LLVector3()
            MessageBlock_Field.Offline = 0
            MessageBlock_Field.Dialog = 38
            MessageBlock_Field.ID = UUID(
                targetUUID.mostSignificantBits xor circuitInfo.agentID.mostSignificantBits,
                targetUUID.leastSignificantBits xor circuitInfo.agentID.leastSignificantBits
            )
            MessageBlock_Field.Timestamp = 0
            MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo")
            MessageBlock_Field.Message = SLMessage.stringToVariableUTF(message)
            MessageBlock_Field.BinaryBucket = ByteArray(0)
            isReliable = true
        }
        
        sendMessage(imMessage)
    }

    fun buyObject(localID: Int, saleType: Byte, salePrice: Int) {
        val activeGroupID = getActiveGroupID() ?: UUIDPool.ZeroUUID
        
        val message = ObjectBuy().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            AgentData_Field.GroupID = activeGroupID
            AgentData_Field.CategoryID = modules!!.inventory.rootFolder.uuid
            
            ObjectData_Fields.add(ObjectBuy.ObjectData().apply {
                ObjectLocalID = localID
                SaleType = saleType
                SalePrice = salePrice
            })
            
            isReliable = true
        }
        
        sendMessage(message)
    }

    override fun closeCircuit() {
        Debug.Printf("AgentCircuit: closing circuit.")
        
        modules?.handleCloseCircuit()
        userManager.clearActiveAgentCircuit(this)
        
        agentNameSubscription?.unsubscribe()
        agentNameSubscription = null
        
        super.closeCircuit()
    }

    fun derezObject(localID: Int, destination: EDeRezDestination) {
        val activeGroupID = getActiveGroupID() ?: UUID(0, 0)
        
        val message = DeRezObject().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            AgentBlock_Field.GroupID = activeGroupID
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
        
        sendMessage(message)
    }

    fun doRequestPayPrice(objectUUID: UUID) {
        val objectInfo = gridConn.parcelInfo.allObjectsNearby[objectUUID] ?: return
        
        if (objectInfo.payInfo != null) {
            eventBus.publish(SLObjectPayInfoEvent(objectInfo))
            return
        }
        
        val message = RequestPayPrice().apply {
            ObjectData_Field.ObjectID = objectUUID
            isReliable = true
        }
        
        sendMessage(message)
    }

    fun generateChatMoneyEvent(sourceUUID: UUID?, amount: Int, balance: Int) {
        val chatterID = if (sourceUUID != null) {
            ChatterID.getUserChatterID(agentUUID, sourceUUID)
        } else {
            localChatterID
        }
        
        val source = if (sourceUUID != null) {
            ChatMessageSourceUser(sourceUUID)
        } else {
            ChatMessageSourceUnknown.getInstance()
        }
        
        handleChatEvent(
            chatterID,
            SLChatBalanceChangedEvent(source, agentUUID, true, amount, balance),
            true
        )
        
        modules?.financialInfo?.recordChatEvent(sourceUUID, amount, balance)
    }

    fun handleAgentMovementComplete(message: AgentMovementComplete) {
        regionHandle = message.Data_Field.RegionHandle
        modules!!.avatarControl.setAgentPosition(message.Data_Field.Position, null)
        
        Debug.Printf("Got agentPosition: %s", modules.avatarControl.agentPosition.immutablePosition)
        
        sendAgentFOV()
        modules.avatarAppearance.sendAgentWearablesRequest()
        sendRetrieveInstantMessages()
        modules.avatarControl.setEnableAgentUpdates(true)
    }

    fun handleAlertMessage(message: AlertMessage) {
        handleChatEvent(
            localChatterID,
            SLChatSystemMessageEvent(
                ChatMessageSourceUnknown.getInstance(),
                agentUUID,
                SLMessage.stringFromVariableOEM(message.AlertData_Field.Message)
            ),
            true
        )
    }

    fun handleAvatarAnimation(message: AvatarAnimation) {
        val parcelInfo = gridConn.parcelInfo ?: return
        if (modules != null) {
            parcelInfo.applyAvatarAnimation(message, modules.avatarControl)
        }
    }

    fun handleAvatarAppearance(message: AvatarAppearance) {
        Debug.Log(
            "Got AvatarAppearance, ID = ${message.Sender_Field.ID}, " +
            "isTrial = ${message.Sender_Field.IsTrial}, " +
            "our ID = ${circuitInfo.agentID}"
        )
        
        if (message.Sender_Field.ID == circuitInfo.agentID && modules != null) {
            modules.avatarAppearance.handleAvatarAppearance(message)
        }
        
        gridConn.parcelInfo?.applyAvatarAppearance(message)
    }

    fun handleAvatarInterestsReply(message: AvatarInterestsReply) {
        Debug.Log(
            "got AvatarInterestsReply: wantToText = " +
            SLMessage.stringFromVariableOEM(message.PropertiesData_Field.WantToText)
        )
        Debug.Log(
            "got AvatarInterestsReply: skillText = " +
            SLMessage.stringFromVariableOEM(message.PropertiesData_Field.SkillsText)
        )
    }

    fun handleChatEvent(chatterID: ChatterID, event: SLChatEvent, notify: Boolean) {
        if (!isEventMuted(chatterID, event)) {
            userManager.chatterList.activeChattersManager.handleChatEvent(chatterID, event, notify)
        }
    }

    fun handleChatFromSimulator(message: ChatFromSimulator) {
        val modules = modules
        if (modules?.rlvController?.onIncomingChat(message) == true) return
        
        val sourceID = message.ChatData_Field.SourceID
        val fromName = SLMessage.stringFromVariableOEM(message.ChatData_Field.FromName)
        val messageText = SLMessage.stringFromVariableUTF(message.ChatData_Field.Message)
        
        // Check for Firestorm LSL Bridge
        if (message.ChatData_Field.ChatType.toInt() == 8 &&
            message.ChatData_Field.SourceType.toInt() == 2 &&
            fromName.startsWith("#Firestorm LSL Bridge") &&
            messageText.startsWith("<bridgeURL>")
        ) {
            return
        }
        
        // Check RLV permissions and audibility
        if (message.ChatData_Field.SourceType.toInt() == 1 &&
            modules?.rlvController?.canRecvChat(messageText, sourceID) == false
        ) {
            return
        }
        
        if (message.ChatData_Field.Audible.toInt() != 1) return
        
        val chatType = message.ChatData_Field.ChatType.toInt()
        if (chatType == 6 || chatType == 4 || chatType == 5) return
        
        when (message.ChatData_Field.SourceType.toInt()) {
            1 -> handleChatEvent(
                localChatterID,
                SLChatTextEvent(ChatMessageSourceUser(sourceID), agentUUID, messageText),
                true
            )
            2 -> handleChatEvent(
                localChatterID,
                SLChatTextEvent(ChatMessageSourceObject(sourceID, fromName), agentUUID, messageText),
                true
            )
            else -> handleChatEvent(
                localChatterID,
                SLChatTextEvent(ChatMessageSourceUnknown.getInstance(), agentUUID, messageText),
                true
            )
        }
    }

    fun handleImprovedInstantMessage(message: ImprovedInstantMessage) {
        val dialogType = message.MessageBlock_Field.Dialog.toInt()
        
        val source: ChatMessageSource = when {
            dialogType == 19 || dialogType == 31 -> ChatMessageSourceObject(
                message.AgentData_Field.AgentID,
                SLMessage.stringFromVariableOEM(message.MessageBlock_Field.FromAgentName)
            )
            dialogType == 3 -> ChatMessageSourceUnknown.getInstance()
            UUIDPool.ZeroUUID == message.AgentData_Field.AgentID -> ChatMessageSourceUnknown.getInstance()
            else -> {
                val userSource = ChatMessageSourceUser(message.AgentData_Field.AgentID)
                if (!modules.rlvController.canRecvIM(userSource.sourceUUID)) {
                    return
                }
                userSource
            }
        }
        
        handleIM(message, source)
    }

    fun handleImprovedTerseObjectUpdate(message: ImprovedTerseObjectUpdate) {
        val parcelInfo = gridConn.parcelInfo
        var requestMultiple: RequestMultipleObjects? = null
        
        for (objectData in message.ObjectData_Fields) {
            val localID = SLObjectInfo.getLocalID(objectData)
            val uuid = parcelInfo.uuidsNearby[localID]
            
            var objectInfo = if (uuid != null) {
                parcelInfo.allObjectsNearby[uuid]?.also {
                    it.applyTerseObjectUpdate(objectData)
                    
                    when {
                        it is SLObjectAvatarInfo && it.isMyAvatar() -> processMyAvatarUpdate(it)
                        it.isMyAttachment() -> processMyAttachmentUpdate(it)
                    }
                }
            } else null
            
            if (objectInfo == null) {
                if (requestMultiple == null) {
                    requestMultiple = RequestMultipleObjects().apply {
                        AgentData_Field.AgentID = circuitInfo.agentID
                        AgentData_Field.SessionID = circuitInfo.sessionID
                    }
                }
                
                requestMultiple.ObjectData_Fields.add(RequestMultipleObjects.ObjectData().apply {
                    CacheMissType = 0
                    ID = localID
                })
            }
        }
        
        requestMultiple?.let {
            Debug.Log("Handing cache miss for terse update: ${it.ObjectData_Fields.size} objects.")
            it.isReliable = true
            sendMessage(it)
        }
    }

    fun handleKillObject(message: KillObject) {
        val parcelInfo = gridConn.parcelInfo
        var needUpdate = false
        
        for (objectData in message.ObjectData_Fields) {
            if (parcelInfo.killObject(this, objectData.ID)) {
                needUpdate = true
            }
        }
        
        if (needUpdate) {
            objectPropertiesRateLimiter.fire()
        }
    }

    fun handleLayerData(message: LayerData) {
        if (message.LayerID_Field.Type.toInt() != 76) return
        
        gridConn.parcelInfo?.terrainData?.processLayerData(message.LayerDataData_Field.Data)
    }

    fun handleLoadURL(message: LoadURL) {
        handleChatEvent(
            localChatterID,
            SLChatTextEvent(
                ChatMessageSourceObject(
                    message.Data_Field.ObjectID,
                    SLMessage.stringFromVariableOEM(message.Data_Field.ObjectName)
                ),
                agentUUID,
                message
            ),
            true
        )
    }

    fun handleObjectProperties(message: ObjectProperties) {
        Debug.Log(
            "ObjectProperties: ${message.ObjectData_Fields.size} ObjectSelect replies. " +
            "Reqd ${objectNamesRequested.size} obj, " +
            "remains ${gridConn.parcelInfo.objectNamesQueue.size} objects."
        )
        
        for (objectData in message.ObjectData_Fields) {
            var objectInfo = gridConn.parcelInfo.objectNamesQueue.remove(objectData.ObjectID)
            if (objectInfo != null) {
                objectInfo.applyObjectProperties(objectData)
                userManager.objectsManager.requestObjectProfileUpdate(objectInfo.localID)
            }
            
            objectInfo = forceNeedObjectNames.remove(objectData.ObjectID)
            if (objectInfo != null) {
                objectInfo.applyObjectProperties(objectData)
                userManager.objectsManager.requestObjectProfileUpdate(objectInfo.localID)
                
                val parent = objectInfo.parentObject
                if (parent != null) {
                    val id = parent.id
                    if (id != null) {
                        userManager.objectsManager.requestTouchableChildrenUpdate(id)
                    }
                }
            }
            
            objectNamesRequested.remove(objectData.ObjectID)
        }
        
        if (objectNamesRequested.isEmpty()) {
            doingObjectSelection = false
            processObjectSelection()
        }
        
        objectPropertiesRateLimiter.fire()
    }

    fun handleObjectUpdate(message: ObjectUpdate) {
        val parcelInfo = gridConn.parcelInfo
        var needAvatarUpdate = false
        var needObjectUpdate = false
        
        for (objectData in message.ObjectData_Fields) {
            if (objectData.PCode.toInt() != 47 && objectData.PCode.toInt() != 9) continue
            
            var objectInfo = parcelInfo.allObjectsNearby[objectData.FullID]
            
            if (objectInfo != null) {
                val oldParentID = objectInfo.parentID
                objectInfo.applyObjectUpdate(objectData)
                parcelInfo.updateObjectParent(oldParentID, objectInfo)
                
                if (objectInfo.parentID != oldParentID &&
                    objectInfo is SLObjectAvatarInfo &&
                    objectInfo.isMyAvatar()
                ) {
                    needAvatarUpdate = true
                }
                
                needObjectUpdate = true
            } else {
                objectInfo = SLObjectInfo.create(agentUUID, objectData, circuitInfo.agentID)
                if (parcelInfo.addObject(objectInfo)) {
                    needObjectUpdate = true
                }
                
                if (objectInfo is SLObjectAvatarInfo && objectInfo.isMyAvatar()) {
                    Debug.Log("ObjectUpdate: got my avatar (normal)")
                    parcelInfo.setAgentAvatar(objectInfo)
                    modules!!.avatarAppearance.onMyAvatarCreated(objectInfo)
                    needAvatarUpdate = true
                }
            }
            
            when {
                objectInfo is SLObjectAvatarInfo && objectInfo.isMyAvatar() -> 
                    processMyAvatarUpdate(objectInfo)
                objectInfo.isMyAttachment() -> 
                    processMyAttachmentUpdate(objectInfo)
            }
        }
        
        if (needAvatarUpdate) {
            userManager.objectsManager.myAvatarState().requestUpdate(SubscriptionSingleKey.Value)
        }
        
        if (needObjectUpdate) {
            processObjectSelection()
            objectPropertiesRateLimiter.fire()
        }
    }

    fun handleObjectUpdateCached(message: ObjectUpdateCached) {
        val requestMultiple = RequestMultipleObjects().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            
            for (objectData in message.ObjectData_Fields) {
                ObjectData_Fields.add(RequestMultipleObjects.ObjectData().apply {
                    CacheMissType = 0
                    ID = objectData.ID
                })
            }
            
            isReliable = true
        }
        
        sendMessage(requestMultiple)
    }

    fun handleObjectUpdateCompressed(message: ObjectUpdateCompressed) {
        val parcelInfo = gridConn.parcelInfo
        var needAvatarUpdate = false
        var needObjectUpdate = false
        
        for (objectData in message.ObjectData_Fields) {
            try {
                val localID = SLObjectInfo.getLocalID(objectData)
                val uuid = parcelInfo.uuidsNearby[localID]
                var objectInfo = uuid?.let { parcelInfo.allObjectsNearby[it] }
                
                var parentChanged = false
                
                if (objectInfo != null) {
                    val oldParentID = objectInfo.parentID
                    objectInfo.applyObjectUpdate(objectData)
                    parcelInfo.updateObjectParent(oldParentID, objectInfo)
                    parentChanged = objectInfo.parentID != oldParentID
                    needObjectUpdate = true
                } else {
                    objectInfo = SLObjectInfo.create(objectData)
                    if (parcelInfo.addObject(objectInfo)) {
                        needObjectUpdate = true
                    }
                }
                
                if (objectInfo is SLObjectAvatarInfo && objectInfo.isMyAvatar()) {
                    if (parentChanged) {
                        needAvatarUpdate = true
                    }
                    processMyAvatarUpdate(objectInfo)
                } else if (objectInfo.isMyAttachment()) {
                    processMyAttachmentUpdate(objectInfo)
                }
            } catch (e: UnsupportedObjectTypeException) {
                // Ignore
            } catch (e: Throwable) {
                Debug.Warning(e)
            }
        }
        
        if (needObjectUpdate) {
            processObjectSelection()
            objectPropertiesRateLimiter.fire()
        }
        
        if (needAvatarUpdate) {
            userManager.objectsManager.myAvatarState().requestUpdate(SubscriptionSingleKey.Value)
        }
    }

    fun handleOfflineNotification(message: OfflineNotification) {
        val agentIDs = message.AgentBlock_Fields.map { it.AgentID }
        userManager.chatterList.friendManager.setUsersOnline(agentIDs, false)
    }

    fun handleOnlineNotification(message: OnlineNotification) {
        val agentIDs = message.AgentBlock_Fields.map { it.AgentID }
        userManager.chatterList.friendManager.setUsersOnline(agentIDs, true)
    }

    fun handlePayPriceReply(message: PayPriceReply) {
        val objectInfo = gridConn.parcelInfo.allObjectsNearby[message.ObjectData_Field.ObjectID] ?: return
        
        val defaultPrice = message.ObjectData_Field.DefaultPayPrice
        val buttonPrices = IntArray(message.ButtonData_Fields.size) { i ->
            message.ButtonData_Fields[i].PayButton
        }
        
        objectInfo.setPayInfo(PayInfo.create(defaultPrice, buttonPrices))
        
        userManager.objectsManager.requestObjectProfileUpdate(objectInfo.localID)
        eventBus.publish(SLObjectPayInfoEvent(objectInfo))
    }

    fun handleRegionHandshake(message: RegionHandshake) {
        if (authReply.isTemporary) return
        
        val regionHandshakeReply = RegionHandshakeReply().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            RegionInfo_Field.Flags = 0
        }
        
        gridConn.parcelInfo?.terrainData?.applyRegionInfo(message.RegionInfo_Field)
        
        sendMessage(regionHandshakeReply)
        
        regionName = SLMessage.stringFromVariableOEM(message.RegionInfo_Field.SimName)
        
        message.RegionInfo2_Field?.RegionID?.let {
            regionID = it
        }
        
        isEstateManager = message.RegionInfo_Field.IsEstateManager
        
        agentNameSubscription = userManager.userNames.subscribe(circuitInfo.agentID) { userName ->
            agentUserName.set(userName)
        }
        
        eventBus.publish(SLRegionInfoChangedEvent())
    }

    fun handleScriptDialog(message: ScriptDialog) {
        val buttons = if (message.Buttons_Fields.isNotEmpty()) {
            Array(message.Buttons_Fields.size) { i ->
                SLMessage.stringFromVariableUTF(message.Buttons_Fields[i].ButtonLabel)
            }
        } else null
        
        var isTextBox = false
        var textBoxIndex = 0
        
        buttons?.forEachIndexed { index, label ->
            if (label == "!!llTextBox!!") {
                isTextBox = true
                textBoxIndex = index
                return@forEachIndexed
            }
        }
        
        if (isTextBox) {
            handleChatEvent(
                localChatterID,
                SLChatTextBoxDialog(message, agentUUID, textBoxIndex),
                true
            )
        } else {
            handleChatEvent(
                localChatterID,
                SLChatScriptDialog(message, agentUUID, buttons),
                true
            )
        }
    }

    fun handleSimulatorViewerTimeMessage(message: SimulatorViewerTimeMessage) {
        if (authReply.isTemporary) return
        
        val parcelInfo = gridConn.parcelInfo ?: return
        
        val sunPhase = (message.TimeInfo_Field.SunPhase / 6.2831855f) + 0.25f
        val sunHour = sunPhase - floor(sunPhase.toDouble()).toFloat()
        
        parcelInfo.setSunHour(sunHour)
    }

    fun handleTeleportFailed(message: TeleportFailed) {
        Debug.Log("TeleportFailed: reason = ${SLMessage.stringFromVariableOEM(message.Info_Field.Reason)}")
        teleportRequestSent = false
        eventBus.publish(
            SLTeleportResultEvent(false, SLMessage.stringFromVariableOEM(message.Info_Field.Reason))
        )
    }

    fun handleTeleportLocal(message: TeleportLocal) {
        teleportRequestSent = false
        eventBus.publish(SLTeleportResultEvent(true, null))
    }

    fun handleTeleportProgress(message: TeleportProgress) {
        Debug.Log(
            "Teleport progress: flags = ${message.Info_Field.TeleportFlags}, " +
            "progress = ${SLMessage.stringFromVariableOEM(message.Info_Field.Message)}"
        )
    }

    fun handleTeleportStart(message: TeleportStart) {
        Debug.Log("TeleportStart: flags = ${message.Info_Field.TeleportFlags}")
    }

    fun offerInventoryItem(targetUUID: UUID, inventoryEntry: SLInventoryEntry) {
        userManager.inventoryManager.executor.execute {
            handleInventoryItemOffer(inventoryEntry, targetUUID)
        }
    }

    private fun handleInventoryItemOffer(inventoryEntry: SLInventoryEntry, targetUUID: UUID) {
        val items = mutableListOf<SLInventoryEntry>()
        items.add(inventoryEntry)
        
        if (inventoryEntry.isFolder) {
            items.addAll(modules!!.inventory.collectGiveableItems(inventoryEntry))
        }
        
        val message = ImprovedInstantMessage().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            MessageBlock_Field.FromGroup = false
            MessageBlock_Field.ToAgentID = targetUUID
            MessageBlock_Field.ParentEstateID = 0
            MessageBlock_Field.RegionID = UUID(0, 0)
            MessageBlock_Field.Position = LLVector3()
            MessageBlock_Field.Offline = 0
            MessageBlock_Field.Dialog = 4
            MessageBlock_Field.ID = UUID.randomUUID()
            MessageBlock_Field.Timestamp = 0
            MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo")
            MessageBlock_Field.Message = SLMessage.stringToVariableUTF(inventoryEntry.name)
            
            val buffer = ByteBuffer.wrap(ByteArray(items.size * 17))
            buffer.order(ByteOrder.BIG_ENDIAN)
            
            for (item in items) {
                buffer.put(
                    if (item.isFolder) {
                        SLAssetType.AT_CATEGORY.typeCode
                    } else {
                        item.assetType
                    }
                )
                buffer.putLong(item.uuid.mostSignificantBits)
                buffer.putLong(item.uuid.leastSignificantBits)
            }
            
            buffer.position(0)
            MessageBlock_Field.BinaryBucket = buffer.array()
            
            isReliable = true
        }
        
        sendMessage(message)
        
        handleChatEvent(
            ChatterID.getUserChatterID(agentUUID, targetUUID),
            SLChatInventoryItemOfferedByYouEvent(agentUUID, inventoryEntry.name),
            false
        )
    }

    fun offerTeleport(targetUUID: UUID, message: String) {
        val startLure = StartLure().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            Info_Field.Message = SLMessage.stringToVariableUTF(message)
            
            TargetData_Fields.add(TargetData().apply {
                TargetID = targetUUID
            })
            
            isReliable = true
        }
        
        sendMessage(startLure)
    }

    override fun onCapsEvent(capsEvent: CapsEvent) {
        try {
            capsEventQueue.add(capsEvent)
            selector.wakeup()
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun processIdle() {
        if (doingObjectSelection && System.currentTimeMillis() > lastObjectSelection + 15000) {
            doingObjectSelection = false
            processObjectSelectionTimeout()
        }
        
        if (!teleportRequestSent &&
            needObjectNames &&
            !doingObjectSelection &&
            System.currentTimeMillis() >= lastObjectSelection + 500
        ) {
            processObjectSelection()
        }
        
        if (!agentPaused) {
            val currentTime = System.currentTimeMillis()
            if (GridConnectionService.hasVisibleActivities()) {
                lastVisibleActivities = currentTime
            } else if (currentTime >= lastVisibleActivities + 10000) {
                doAgentPause()
            }
        }
        
        objectPropertiesRateLimiter.firePending()
    }

    override fun processNetworkError() {
        super.processNetworkError()
        Debug.Printf("Network: Network error.")
        
        modules?.avatarControl?.setEnableAgentUpdates(false)
        
        if (!authReply.isTemporary) {
            gridConn.processDisconnect(false, "Network connection lost.")
        }
    }

    override fun processTimeout() {
        super.processTimeout()
        
        modules?.avatarControl?.setEnableAgentUpdates(false)
        
        if (!authReply.isTemporary) {
            gridConn.processDisconnect(false, "Connection has timed out.")
        }
    }

    override fun processWakeup() {
        super.processWakeup()
        
        try {
            while (true) {
                val capsEvent = capsEventQueue.poll() ?: break
                handleCapsEvent(capsEvent)
            }
        } catch (e: Exception) {
            // Ignore
        }
        
        processIdle()
    }

    fun removeFriend(friendUUID: UUID) {
        val message = TerminateFriendship().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            ExBlock_Field.OtherID = friendUUID
            isReliable = true
        }
        
        sendMessage(message)
        userManager.chatterList.friendManager.removeFriend(friendUUID)
    }

    fun requestObjectName(objectInfo: SLObjectInfo) {
        val id = objectInfo.id ?: return
        
        if (!objectNamesRequested.containsKey(id) && !forceNeedObjectNames.containsKey(id)) {
            forceNeedObjectNames[id] = objectInfo
        }
        
        tryWakeUp()
    }

    fun requestTeleport(targetUUID: UUID, message: String) {
        sendInstantMessage(targetUUID, message, 26)
    }

    fun restartRegion(delaySeconds: Int): Boolean {
        if (!isEstateManager) return false
        
        sendEstateOwnerMessage("restart", arrayOf(delaySeconds.toString()))
        return true
    }

    fun rezObject(inventoryEntry: SLInventoryEntry) {
        var groupID = UUIDPool.ZeroUUID
        var parcelOwnerID: UUID? = null
        
        // Get parcel information
        userManager.currentLocationInfoSnapshot?.let { locationInfo ->
            locationInfo.parcelData()?.let { parcelData ->
                if (parcelData.isGroupOwned) {
                    parcelOwnerID = parcelData.ownerID
                }
            }
        }
        
        // Determine the group to use
        if (parcelOwnerID != null && parcelOwnerID != UUIDPool.ZeroUUID) {
            groupID = parcelOwnerID!!
        } else {
            userManager.chatterList.groupManager.avatarGroupList?.let { avatarGroupList ->
                if (avatarGroupList.Groups.containsKey(groupID)) {
                    // Use the group ID
                } else {
                    groupID = getActiveGroupID() ?: UUIDPool.ZeroUUID
                }
            }
        }
        
        val agentPosition = modules!!.avatarControl.agentPosition.position
        val agentHeading = modules.avatarControl.agentHeading
        
        val message = RezObject().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            AgentData_Field.GroupID = groupID
            
            RezData_Field.FromTaskID = UUIDPool.ZeroUUID
            RezData_Field.BypassRaycast = true
            RezData_Field.RayStart = agentPosition
            RezData_Field.RayEnd = agentPosition.getRotatedOffset(1.5f, agentHeading)
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
            
            // Set up event listener if item is not copy-ok
            if ((inventoryEntry.ownerMask and 0x8000) == 0) {
                val folderID = inventoryEntry.parentUUID
                setEventListener(object : SLMessageEventListener {
                    override fun onMessageAcknowledged(message: SLMessage) {
                        userManager.inventoryManager.requestFolderContents(folderID)
                    }
                    
                    override fun onMessageTimeout(message: SLMessage) {
                        Debug.Log("RezObject message timed out")
                    }
                })
            }
        }
        
        sendMessage(message)
    }

    fun sendChatMessage(chatterID: ChatterID, message: String) {
        when (chatterID.chatterType) {
            ChatterType.Group -> sendGroupInstantMessage(chatterID.optionalChatterUUID, message)
            ChatterType.Local -> sendLocalChatMessage(message)
            ChatterType.User -> sendInstantMessage(chatterID.optionalChatterUUID, message)
        }
    }

    fun sendGenericMessage(method: String, params: Array<String>) {
        val message = GenericMessage().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            AgentData_Field.TransactionID = UUID(0, 0)
            MethodData_Field.Method = SLMessage.stringToVariableOEM(method)
            MethodData_Field.Invoice = UUID(0, 0)
            
            params.forEach { param ->
                ParamList_Fields.add(GenericMessage.ParamList().apply {
                    Parameter = SLMessage.stringToVariableOEM(param)
                })
            }
            
            isReliable = true
        }
        
        sendMessage(message)
    }

    fun sendGroupInstantMessage(groupUUID: UUID, messageText: String) {
        val message = ImprovedInstantMessage().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            MessageBlock_Field.FromGroup = false
            MessageBlock_Field.ToAgentID = groupUUID
            MessageBlock_Field.ParentEstateID = 0
            MessageBlock_Field.RegionID = UUID(0, 0)
            MessageBlock_Field.Position = modules!!.avatarControl.agentPosition.position
            MessageBlock_Field.Offline = 0
            MessageBlock_Field.Dialog = 17
            MessageBlock_Field.ID = groupUUID
            MessageBlock_Field.Timestamp = 0
            MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo")
            MessageBlock_Field.Message = SLMessage.stringToVariableUTF(messageText)
            MessageBlock_Field.BinaryBucket = ByteArray(1)
            isReliable = true
        }
        
        synchronized(startedGroupSessions) {
            if (startedGroupSessions.contains(groupUUID)) {
                sendMessage(message)
            } else {
                sendGroupSessionStart(groupUUID)
                pendingGroupMessages.add(message)
            }
        }
    }

    fun sendInstantMessage(targetUUID: UUID, message: String): Boolean {
        return sendInstantMessage(targetUUID, message, 0)
    }

    fun sendLocalChatMessage(messageText: String) {
        var channel = 0
        var text = messageText
        
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
                    text = text.substring(digitCount + 1).trim()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        
        if (!modules.rlvController.onSendLocalChat(channel, text)) return
        
        val message = ChatFromViewer().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            ChatData_Field.Channel = channel
            ChatData_Field.Type = 1
            ChatData_Field.Message = SLMessage.stringToVariableUTF(text)
            isReliable = true
        }
        
        sendMessage(message)
    }

    fun sendLogoutRequest() {
        Debug.Log("Logout: Sending logout request.")
        modules!!.avatarControl.setEnableAgentUpdates(false)
        
        val logoutRequest = LogoutRequest().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            isReliable = true
            
            setEventListener(object : SLMessageEventListener {
                override fun onMessageAcknowledged(message: SLMessage) {
                    Debug.Log("Logout: Logout request acknowledged.")
                    gridConn.processDisconnect(true, "Logged out.")
                }
                
                override fun onMessageTimeout(message: SLMessage) {
                    Debug.Log("Logout: LogoutRequest timed out!")
                    gridConn.processDisconnect(false, "Logout request has timed out.")
                }
            })
        }
        
        sendMessage(logoutRequest)
    }

    fun sendScriptDialogReply(objectUUID: UUID, channel: Int, buttonIndex: Int, buttonLabel: String) {
        val message = ScriptDialogReply().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            Data_Field.ObjectID = objectUUID
            Data_Field.ChatChannel = channel
            Data_Field.ButtonIndex = buttonIndex
            Data_Field.ButtonLabel = SLMessage.stringToVariableUTF(buttonLabel)
            isReliable = true
        }
        
        sendMessage(message)
    }

    fun sendUseCode() {
        Debug.Printf("Using circuitCode: %d", circuitInfo.circuitCode)
        
        val useCircuitCode = UseCircuitCode().apply {
            CircuitCode_Field.Code = circuitInfo.circuitCode
            CircuitCode_Field.SessionID = circuitInfo.sessionID
            CircuitCode_Field.ID = circuitInfo.agentID
            isReliable = true
            
            setEventListener(object : SLMessageEventListener {
                override fun onMessageAcknowledged(message: SLMessage) {
                    Debug.Log("SLAgentCircuit: UseCircuitCode acknowledged.")
                    
                    if (!authReply.isTemporary) {
                        if (authReply.fromTeleport) {
                            Debug.Log("SLAgentCircuit: Ack from teleport, sending Teleport success.")
                            eventBus.publish(SLTeleportResultEvent(true, null))
                        } else {
                            gridConn.notifyLoginSuccess()
                        }
                        
                        sendCompleteAgentMovement()
                        
                        modules?.handleCircuitReady()
                    }
                }
                
                override fun onMessageTimeout(message: SLMessage) {
                    if (authReply.fromTeleport) {
                        eventBus.publish(
                            SLTeleportResultEvent(false, "Timed out while connecting to the simulator.")
                        )
                    } else {
                        gridConn.notifyLoginError("Timed out while connecting to the simulator.")
                    }
                }
            })
        }
        
        sendMessage(useCircuitCode)
    }

    fun startGroupSessionForVoice(groupUUID: UUID) {
        var needStart = false
        
        synchronized(startedGroupSessions) {
            if (!startedGroupSessions.contains(groupUUID)) {
                sendGroupSessionStart(groupUUID)
                needStart = true
            }
        }
        
        if (!needStart) {
            modules!!.voice.onGroupSessionReady(groupUUID)
        }
    }

    fun teleportToGlobalPosition(globalPosition: LLVector3) {
        val x = floor(globalPosition.x.toDouble()).toInt()
        val y = floor(globalPosition.y.toDouble()).toInt()
        val xAligned = x - (x % 256)
        val yAligned = y - (y % 256)
        
        val regionHandle = yAligned.toLong() or (xAligned.toLong() shl 32)
        
        val localPosition = LLVector3(
            globalPosition.x % 256.0f,
            globalPosition.y % 256.0f,
            globalPosition.z
        )
        
        val lookAt = LLVector3(localPosition).apply {
            x += 1.0f
        }
        
        Debug.Printf("regionHandle = %s, globalPos = %s", regionHandle.toString(16), globalPosition)
        
        teleportRequestSent = true
        
        val message = TeleportLocationRequest().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            Info_Field.RegionHandle = regionHandle
            Info_Field.Position = localPosition
            Info_Field.LookAt = lookAt
            isReliable = true
            
            setEventListener(object : SLMessageEventListener {
                override fun onMessageAcknowledged(message: SLMessage) {}
                
                override fun onMessageTimeout(message: SLMessage) {
                    eventBus.publish(SLTeleportResultEvent(false, "Teleport request has timed out."))
                }
            })
        }
        
        sendMessage(message)
    }

    fun teleportToLandmarkAsset(landmarkUUID: UUID) {
        if (!modules.rlvController.canTeleportToLandmark()) return
        
        teleportRequestSent = true
        
        val message = TeleportLandmarkRequest().apply {
            Info_Field.AgentID = circuitInfo.agentID
            Info_Field.SessionID = circuitInfo.sessionID
            Info_Field.LandmarkID = landmarkUUID
            isReliable = true
            
            setEventListener(object : SLMessageEventListener {
                override fun onMessageAcknowledged(message: SLMessage) {}
                
                override fun onMessageTimeout(message: SLMessage) {
                    eventBus.publish(SLTeleportResultEvent(false, "Teleport request has timed out."))
                }
            })
        }
        
        sendMessage(message)
    }

    fun teleportToLocalPosition(localPosition: LLVector3): Boolean {
        if (regionID == null) return false
        
        Debug.Printf(
            "Teleport: localPos = %s, regionHandle = %d",
            localPosition.toString(),
            regionHandle
        )
        
        teleportRequestSent = true
        
        val lookAt = LLVector3(localPosition).apply {
            x += 10.0f
        }
        
        val message = TeleportLocationRequest().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            Info_Field.RegionHandle = regionHandle
            Info_Field.Position = localPosition
            Info_Field.LookAt = lookAt
            isReliable = true
            
            setEventListener(object : SLMessageEventListener {
                override fun onMessageAcknowledged(message: SLMessage) {}
                
                override fun onMessageTimeout(message: SLMessage) {
                    eventBus.publish(SLTeleportResultEvent(false, "Teleport request has timed out."))
                }
            })
        }
        
        sendMessage(message)
        return true
    }

    fun teleportToLure(lureID: UUID) {
        teleportRequestSent = true
        
        val message = TeleportLureRequest().apply {
            Info_Field.AgentID = circuitInfo.agentID
            Info_Field.SessionID = circuitInfo.sessionID
            Info_Field.LureID = lureID
            isReliable = true
            
            setEventListener(object : SLMessageEventListener {
                override fun onMessageAcknowledged(message: SLMessage) {}
                
                override fun onMessageTimeout(message: SLMessage) {
                    eventBus.publish(SLTeleportResultEvent(false, "Teleport request has timed out."))
                }
            })
        }
        
        sendMessage(message)
    }

    fun teleportToRegion(regionHandle: Long, x: Int, y: Int, z: Int) {
        if (!modules.rlvController.canTeleportToLocation()) return
        
        Debug.Log(
            "TeleportToRegion: regionHandle = ${regionHandle.toString(16)}, " +
            "pos = ($x, $y, $z)"
        )
        
        teleportRequestSent = true
        
        val message = TeleportLocationRequest().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            Info_Field.RegionHandle = regionHandle
            Info_Field.Position = LLVector3(x.toFloat(), y.toFloat(), z.toFloat())
            Info_Field.LookAt = LLVector3(0.0f, 1.0f, 0.0f)
            isReliable = true
            
            setEventListener(object : SLMessageEventListener {
                override fun onMessageAcknowledged(message: SLMessage) {}
                
                override fun onMessageTimeout(message: SLMessage) {
                    eventBus.publish(SLTeleportResultEvent(false, "Teleport request has timed out."))
                }
            })
        }
        
        sendMessage(message)
    }

    fun touchObject(localID: Int) {
        val grabMessage = ObjectGrab().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            ObjectData_Field.LocalID = localID
            ObjectData_Field.GrabOffset = LLVector3()
            isReliable = true
        }
        sendMessage(grabMessage)
        
        val degrabMessage = ObjectDeGrab().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            ObjectData_Field.LocalID = localID
            isReliable = true
        }
        sendMessage(degrabMessage)
    }

    fun touchObjectFace(
        objectInfo: SLObjectInfo,
        faceIndex: Int,
        posX: Float, posY: Float, posZ: Float,
        uvU: Float, uvV: Float,
        stS: Float, stT: Float
    ) {
        Debug.Printf(
            "Touch: Object %d, face %d, pos (%f, %f, %f), uv (%f, %f)",
            objectInfo.localID, faceIndex, posX, posY, posZ, uvU, uvV
        )
        
        val grabMessage = ObjectGrab().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            ObjectData_Field.LocalID = objectInfo.localID
            ObjectData_Field.GrabOffset = LLVector3()
            
            SurfaceInfo_Fields.add(SurfaceInfo().apply {
                FaceIndex = faceIndex
                Position = LLVector3(posX, posY, posZ)
                UVCoord = LLVector3(uvU, uvV, 0.0f)
                STCoord = LLVector3(stS, stT, 0.0f)
                Normal = LLVector3(1.0f, 0.0f, 0.0f)
                Binormal = LLVector3(0.0f, 0.0f, 1.0f)
            })
            
            isReliable = true
        }
        sendMessage(grabMessage)
        
        val degrabMessage = ObjectDeGrab().apply {
            AgentData_Field.AgentID = circuitInfo.agentID
            AgentData_Field.SessionID = circuitInfo.sessionID
            ObjectData_Field.LocalID = objectInfo.localID
            isReliable = true
        }
        sendMessage(degrabMessage)
    }

    fun tryWakeUp() {
        try {
            selector.wakeup()
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun unpauseAgent() {
        lastVisibleActivities = System.currentTimeMillis()
        if (agentPaused) {
            doAgentResume()
        }
    }

    val agentGlobalPosition: LLVector3d?
        get() {
            if (modules == null) return null
            
            val position = modules.avatarControl.agentPosition.position
            val regionX = ((regionHandle shr 32) and 0xFFFFFFFF).toInt()
            val regionY = (regionHandle and 0xFFFFFFFF).toInt()
            
            return LLVector3d().apply {
                x = regionX.toDouble() + position.x.toDouble()
                y = regionY.toDouble() + position.y.toDouble()
                z = position.z.toDouble()
            }
        }

    @SuppressLint("DefaultLocale")
    val agentSLURL: String?
        get() {
            if (modules == null ||
                authReply.loginURL != "https://login.agni.lindenlab.com/cgi-bin/login.cgi" ||
                regionName == null
            ) {
                return null
            }
            
            val position = modules.avatarControl.agentPosition.position
            
            return try {
                String.format(
                    "http://maps.secondlife.com/secondlife/%s/%d/%d/%d",
                    URLEncoder.encode(regionName, "UTF-8"),
                    position.x.toInt(),
                    position.y.toInt(),
                    position.z.toInt()
                )
            } catch (e: UnsupportedEncodingException) {
                null
            }
        }

    fun getObjectProfile(localID: Int): SLObjectProfileData? {
        val objectInfo = gridConn.parcelInfo.getObjectInfo(localID) ?: return null
        
        val profile = SLObjectProfileData.create(objectInfo)
        
        if (!profile.name().isPresent && !objectInfo.isDead) {
            requestObjectName(objectInfo)
        }
        
        return profile
    }

    fun isUserTyping(userUUID: UUID): Boolean {
        return typingUsers.contains(userUUID)
    }

    fun sendTypingNotify(targetUUID: UUID, isTyping: Boolean) {
        sendInstantMessage(targetUUID, "", if (isTyping) 41 else 42)
    }

    fun processMyAttachmentUpdate(objectInfo: SLObjectInfo) {
        if (!objectInfo.nameKnown && !objectInfo.isDead) {
            requestObjectName(objectInfo)
        }
        modules.avatarAppearance.updateMyAttachments()
    }

    // Getter properties
    fun getAgentUUID(): UUID = agentUUID
    fun getCaps(): SLCaps = caps
    fun getIsEstateManager(): Boolean = isEstateManager
    fun getLocalChatterID(): ChatterID = localChatterID
    fun getModules(): SLModules? = modules
    fun getRegionName(): String? = regionName
    fun getSessionID(): UUID = circuitInfo.sessionID
}
