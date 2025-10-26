package com.linkpoint.slproto

import android.annotation.SuppressLint
import com.google.common.base.Objects
import com.google.common.logging.nano.Vr.VREvent.VrCore.ErrorCode
import com.linkpoint.Debug
import com.linkpoint.GridConnectionService
import com.linkpoint.dao.UserName
import com.linkpoint.eventbus.EventBus
import com.linkpoint.eventbus.EventRateLimiter
import com.linkpoint.react.Subscription
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.slproto.auth.SLAuthReply
import com.linkpoint.slproto.caps.SLCapEventQueue.CapsEvent
import com.linkpoint.slproto.caps.SLCapEventQueue.CapsEventType
import com.linkpoint.slproto.caps.SLCapEventQueue.ICapsEventHandler
import com.linkpoint.slproto.caps.SLCaps
import com.linkpoint.slproto.chat.SLChatBalanceChangedEvent
import com.linkpoint.slproto.chat.SLChatInventoryItemOfferedByGroupNoticeEvent
import com.linkpoint.slproto.chat.SLChatInventoryItemOfferedByYouEvent
import com.linkpoint.slproto.chat.SLChatLureRequestedEvent
import com.linkpoint.slproto.chat.SLChatOnlineOfflineEvent
import com.linkpoint.slproto.chat.SLChatScriptDialog
import com.linkpoint.slproto.chat.SLChatSystemMessageEvent
import com.linkpoint.slproto.chat.SLChatTextBoxDialog
import com.linkpoint.slproto.chat.SLChatTextEvent
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.events.SLObjectPayInfoEvent
import com.linkpoint.slproto.events.SLRegionInfoChangedEvent
import com.linkpoint.slproto.events.SLTeleportResultEvent
import com.linkpoint.slproto.inventory.SLAssetType
import com.linkpoint.slproto.inventory.SLInventoryEntry
import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.messages.AcceptFriendship
import com.linkpoint.slproto.messages.AcceptFriendship.FolderData
import com.linkpoint.slproto.messages.AgentFOV
import com.linkpoint.slproto.messages.AgentMovementComplete
import com.linkpoint.slproto.messages.AgentPause
import com.linkpoint.slproto.messages.AgentResume
import com.linkpoint.slproto.messages.AlertMessage
import com.linkpoint.slproto.messages.AvatarAnimation
import com.linkpoint.slproto.messages.AvatarAppearance
import com.linkpoint.slproto.messages.AvatarInterestsReply
import com.linkpoint.slproto.messages.ChatFromSimulator
import com.linkpoint.slproto.messages.ChatFromViewer
import com.linkpoint.slproto.messages.CompleteAgentMovement
import com.linkpoint.slproto.messages.DeRezObject
import com.linkpoint.slproto.messages.DeRezObject.AgentBlock
import com.linkpoint.slproto.messages.EstateOwnerMessage
import com.linkpoint.slproto.messages.EstateOwnerMessage.ParamList
import com.linkpoint.slproto.messages.GenericMessage
import com.linkpoint.slproto.messages.ImprovedInstantMessage
import com.linkpoint.slproto.messages.ImprovedTerseObjectUpdate
import com.linkpoint.slproto.messages.KillObject
import com.linkpoint.slproto.messages.LayerData
import com.linkpoint.slproto.messages.LoadURL
import com.linkpoint.slproto.messages.LogoutRequest
import com.linkpoint.slproto.messages.ObjectBuy
import com.linkpoint.slproto.messages.ObjectBuy.AgentData
import com.linkpoint.slproto.messages.ObjectDeGrab
import com.linkpoint.slproto.messages.ObjectGrab
import com.linkpoint.slproto.messages.ObjectGrab.SurfaceInfo
import com.linkpoint.slproto.messages.ObjectProperties
import com.linkpoint.slproto.messages.ObjectSelect
import com.linkpoint.slproto.messages.ObjectSelect.ObjectData
import com.linkpoint.slproto.messages.ObjectUpdate
import com.linkpoint.slproto.messages.ObjectUpdateCached
import com.linkpoint.slproto.messages.ObjectUpdateCompressed
import com.linkpoint.slproto.messages.OfflineNotification
import com.linkpoint.slproto.messages.OnlineNotification
import com.linkpoint.slproto.messages.PayPriceReply
import com.linkpoint.slproto.messages.PayPriceReply.ButtonData
import com.linkpoint.slproto.messages.RegionHandshake
import com.linkpoint.slproto.messages.RegionHandshakeReply
import com.linkpoint.slproto.messages.RequestMultipleObjects
import com.linkpoint.slproto.messages.RequestPayPrice
import com.linkpoint.slproto.messages.RetrieveInstantMessages
import com.linkpoint.slproto.messages.ScriptDialog
import com.linkpoint.slproto.messages.ScriptDialog.Buttons
import com.linkpoint.slproto.messages.ScriptDialogReply
import com.linkpoint.slproto.messages.SimulatorViewerTimeMessage
import com.linkpoint.slproto.messages.StartLure
import com.linkpoint.slproto.messages.StartLure.TargetData
import com.linkpoint.slproto.messages.TeleportFailed
import com.linkpoint.slproto.messages.TeleportLandmarkRequest
import com.linkpoint.slproto.messages.TeleportLocal
import com.linkpoint.slproto.messages.TeleportLocationRequest
import com.linkpoint.slproto.messages.TeleportLureRequest
import com.linkpoint.slproto.messages.TeleportProgress
import com.linkpoint.slproto.messages.TeleportStart
import com.linkpoint.slproto.messages.TerminateFriendship
import com.linkpoint.slproto.messages.UseCircuitCode
import com.linkpoint.slproto.modules.SLModules
import com.linkpoint.slproto.modules.groups.AvatarGroupList
import com.linkpoint.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry
import com.linkpoint.slproto.modules.mutelist.MuteType
import com.linkpoint.slproto.modules.mutelist.SLMuteList
import com.linkpoint.slproto.objects.PayInfo
import com.linkpoint.slproto.objects.SLObjectAvatarInfo
import com.linkpoint.slproto.objects.SLObjectInfo
import com.linkpoint.slproto.objects.SLObjectProfileData
import com.linkpoint.slproto.objects.UnsupportedObjectTypeException
import com.linkpoint.slproto.types.EDeRezDestination
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.types.LLVector3d
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.ChatterID.ChatterIDGroup
import com.linkpoint.slproto.users.ChatterID.ChatterIDUser
import com.linkpoint.slproto.users.ChatterID.ChatterType
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType
import com.linkpoint.slproto.users.chatsrc.ChatMessageSourceObject
import com.linkpoint.slproto.users.chatsrc.ChatMessageSourceUnknown
import com.linkpoint.slproto.users.chatsrc.ChatMessageSourceUser
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.utils.UUIDPool
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.ArrayList
import java.util.Collections
import java.util.HashSet
import java.util.Iterator
import java.util.LinkedList
import java.util.List
import java.util.Map
import java.util.Set
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.Nonnull
import javax.annotation.Nullable

class SLAgentCircuit : SLThreadingCircuit(), ICapsEventHandler {
    /* renamed from: -com-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues */
    private const val /* synthetic */ IntArray syntheticField = null
    /* renamed from: -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues */
    private const val /* synthetic */ IntArray syntheticField = null
    private Subscription agentNameSubscription
    private Boolean agentPaused = false
    private val UUID agentUUID
    private val AtomicReference<UserName> agentUserName = AtomicReference(null)
    private val SLCaps caps
    private val ConcurrentLinkedQueue<CapsEvent> capsEventQueue = ConcurrentLinkedQueue()
    private Boolean doingObjectSelection = false
    private val EventBus eventBus = EventBus.getInstance()
    private val Map<UUID, SLObjectInfo> forceNeedObjectNames = ConcurrentHashMap()
    private Boolean isEstateManager = false
    private Long lastObjectSelection = 0
    private Int lastPauseId = 0
    private Long lastVisibleActivities = 0
    private val ChatterID localChatterID
    private val SLModules modules
    private val Map<UUID, SLObjectInfo> objectNamesRequested = ConcurrentHashMap()
    private val EventRateLimiter objectPropertiesRateLimiter = EventRateLimiter(this.eventBus, 500) {
         protected fun getEventToFire(): Object {
            return null
        }

         protected fun onActualFire() {
            SLAgentCircuit.this.notifyObjectPropertiesChange()
        }
    }
    private List<ImprovedInstantMessage> pendingGroupMessages = LinkedList()
    private Long regionHandle = 0
    private UUID regionID = null
    private String regionName = null
    private val Set<UUID> startedGroupSessions = HashSet()
    private Boolean teleportRequestSent = false
    private val Set<UUID> typingUsers = Collections.synchronizedSet(HashSet())
    private val UserManager userManager

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues */
    @JvmStatic
private /* synthetic */ IntArray m38-getcom-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues() {
        if (syntheticField != null) {
            return syntheticField
        }
        val iArr: IntArray = Int[CapsEventType.values().length]
        try {
            iArr[CapsEventType.AgentGroupDataUpdate.ordinal()] = 9
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[CapsEventType.AvatarGroupsReply.ordinal()] = 10
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[CapsEventType.BulkUpdateInventory.ordinal()] = 11
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[CapsEventType.ChatterBoxInvitation.ordinal()] = 1
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[CapsEventType.ChatterBoxSessionStartReply.ordinal()] = 2
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[CapsEventType.EstablishAgentCommunication.ordinal()] = 3
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[CapsEventType.ParcelProperties.ordinal()] = 12
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[CapsEventType.TeleportFailed.ordinal()] = 4
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[CapsEventType.TeleportFinish.ordinal()] = 5
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[CapsEventType.UnknownCapsEvent.ordinal()] = 13
        } catch (NoSuchFieldError e10) {
        }
        syntheticField = iArr
        return iArr
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues */
    @JvmStatic
private /* synthetic */ IntArray m39-getcom-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues() {
        if (syntheticField != null) {
            return syntheticField
        }
        val iArr: IntArray = Int[ChatterType.values().length]
        try {
            iArr[ChatterType.Group.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ChatterType.Local.ordinal()] = 2
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ChatterType.User.ordinal()] = 3
        } catch (NoSuchFieldError e3) {
        }
        syntheticField = iArr
        return iArr
    }

    public SLAgentCircuit(SLGridConnection sLGridConnection, SLCircuitInfo sLCircuitInfo, SLAuthReply sLAuthReply, SLCaps sLCaps, SLTempCircuit sLTempCircuit) throws IOException {
        super(sLGridConnection, sLCircuitInfo, sLAuthReply, sLTempCircuit)
        this.caps = sLCaps
        this.agentUUID = sLCircuitInfo.agentID
        this.localChatterID = ChatterID.getLocalChatterID(this.agentUUID)
        this.lastVisibleActivities = System.currentTimeMillis()
        this.userManager = UserManager.getUserManager(sLCircuitInfo.agentID)
        if (sLCaps == null || (sLAuthReply.isTemporary ^ 1) == 0) {
            this.modules = null
        } else {
            this.modules = SLModules(this, sLCaps, sLGridConnection)
        }
        if (!(sLAuthReply.isTemporary || this.userManager == null)) {
            this.userManager.setActiveAgentCircuit(this)
        }
        if (sLTempCircuit != null) {
            for (SLMessage Handle : sLTempCircuit.getPendingMessages()) {
                Handle.handleMessage(this)
            }
        }
    }

    private fun DoAgentPause() {
        this.agentPaused = true
        Debug.Log("AgentPause: Sending agentPause with ID = " + this.lastPauseId)
        val agentPause: SLMessage = AgentPause()
        agentPause.AgentData_Field.AgentID = this.circuitInfo.agentID
        agentPause.AgentData_Field.SessionID = this.circuitInfo.sessionID
        agentPause.AgentData_Field.SerialNum = this.lastPauseId
        agentPause.isReliable = true
        SendMessage(agentPause)
        this.lastPauseId++
    }

    private fun DoAgentResume() {
        this.agentPaused = false
        Debug.Log("AgentPause: Sending agentResume with ID = " + this.lastPauseId)
        val agentResume: SLMessage = AgentResume()
        agentResume.AgentData_Field.AgentID = this.circuitInfo.agentID
        agentResume.AgentData_Field.SessionID = this.circuitInfo.sessionID
        agentResume.AgentData_Field.SerialNum = this.lastPauseId
        agentResume.isReliable = true
        SendMessage(agentResume)
        this.lastPauseId++
    }

    private fun HandleCapsEvent(capsEvent: CapsEvent) {
        switch (m38-getcom-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues()[capsEvent.eventType.ordinal()]) {
            case 1:
                HandleChatterBoxInvitation(capsEvent.eventBody)
                return
            case 2:
                HandleChatterBoxSessionStartReply(capsEvent.eventBody)
                return
            case 3:
                HandleEstablishAgentCommunication(capsEvent.eventBody)
                return
            case 4:
                HandleTeleportFailed(capsEvent.eventBody)
                return
            case 5:
                HandleTeleportFinish(capsEvent.eventBody)
                return
            default:
                DefaultEventQueueHandler(capsEvent.eventType, capsEvent.eventBody)
                return
        }
    }

    private fun HandleChatterBoxInvitation(lLSDNode: LLSDNode) {
        try {
            Debug.Log("ChatterBoxInvitation: event = " + lLSDNode.serializeToXML())
        } catch (IOException e) {
            e.printStackTrace()
        }
        try {
            val fromString: UUID = UUID.fromString(lLSDNode.byKey("session_id").asString())
            val avatarGroupList: AvatarGroupList = this.userManager.getChatterList().getGroupManager().getAvatarGroupList()
            val avatarGroupEntry: AvatarGroupEntry = avatarGroupList != null ? (AvatarGroupEntry) avatarGroupList.Groups.get(fromString) : null
            val byKey: LLSDNode = lLSDNode.byKey("instantmessage").byKey("message_params")
            val asUUID: UUID = byKey.keyExists("from_id") ? byKey.byKey("from_id").asUUID() : null
            val asUUID2: UUID = byKey.byKey("to_id").asUUID()
            val asString: String = byKey.byKey("message").asString()
            if (avatarGroupEntry == null) {
                avatarGroupEntry = avatarGroupList != null ? (AvatarGroupEntry) avatarGroupList.Groups.get(asUUID2) : null
            }
            if (avatarGroupEntry == null || asUUID == null) {
                Debug.Log("ChatterBoxInvitation: chat from unknown group (" + fromString + "), to_id = " + asUUID2)
            } else {
                HandleChatEvent(ChatterID.getGroupChatterID(this.agentUUID, avatarGroupEntry.GroupID), SLChatTextEvent(ChatMessageSourceUser(asUUID), this.agentUUID, asString), true)
            }
        } catch (LLSDException e2) {
            Debug.Log("ChatterBoxInvitation: LLSDException " + e2.getMessage())
            e2.printStackTrace()
        }
    }

    private fun HandleChatterBoxSessionStartReply(lLSDNode: LLSDNode) {
        try {
            Debug.Log("ChatterBoxSessionStartReply: event = " + lLSDNode.serializeToXML())
        } catch (IOException e) {
            e.printStackTrace()
        }
        try {
            val asUUID: UUID = lLSDNode.byKey("session_id").asUUID()
            this.modules.voice.onGroupSessionReady(asUUID)
            synchronized (this.startedGroupSessions) {
                this.startedGroupSessions.add(asUUID)
                val it: Iterator = this.pendingGroupMessages.iterator()
                while (it.hasNext()) {
                    val improvedInstantMessage: ImprovedInstantMessage = (ImprovedInstantMessage) it.next()
                    if (improvedInstantMessage.MessageBlock_Field.ID.equals(asUUID)) {
                        it.remove()
                        SendMessage(improvedInstantMessage)
                    }
                }
            }
        } catch (LLSDException e2) {
            Debug.Log("ChatterBoxSessionStartReply: LLSDException " + e2.getMessage())
            e2.printStackTrace()
        }
    }

    private fun HandleChatterOnlineStatus(chatterID: ChatterID, z: Boolean) {
        if (this.userManager.isChatterActive(chatterID) && (chatterID instanceof ChatterIDUser)) {
            HandleChatEvent(chatterID, SLChatOnlineOfflineEvent(ChatMessageSourceUser(((ChatterIDUser) chatterID).getChatterUUID()), this.agentUUID, z), false)
        }
    }

    private fun HandleEstablishAgentCommunication(lLSDNode: LLSDNode) {
        if (this.teleportRequestSent) {
            try {
                Debug.Log("EstablishAgentCommunication: event = " + lLSDNode.serializeToXML())
            } catch (IOException e) {
                e.printStackTrace()
            }
            try {
                val asString: String = lLSDNode.byKey("sim-ip-and-port").asString()
                val asString2: String = lLSDNode.byKey("seed-capability").asString()
                val asUUID: UUID = lLSDNode.byKey("agent-id").asUUID()
                val split: Array<String> = asString.split(":")
                this.gridConn.addTempCircuit(SLAuthReply(this.authReply, true, true, asUUID, split[0], Integer.parseInt(split[1]), asString2))
            } catch (Exception e2) {
                e2.printStackTrace()
            }
        }
    }

    private fun HandleGroupNotice(improvedInstantMessage: ImprovedInstantMessage, chatMessageSource: ChatMessageSource) {
        val wrap: ByteBuffer = ByteBuffer.wrap(improvedInstantMessage.MessageBlock_Field.BinaryBucket)
        if (wrap.limit() >= 18) {
            wrap.order(ByteOrder.BIG_ENDIAN)
            val b: Byte = wrap.get()
            val b2: Byte = wrap.get()
            val uuid: UUID = UUID(wrap.getLong(), wrap.getLong())
            val str: String = ""
            if (b != (Byte) 0) {
                val bArr: ByteArray = Byte[wrap.remaining()]
                wrap.get(bArr)
                str = SLMessage.stringFromVariableOEM(bArr)
            }
            Debug.Log("HandleGroupNotice: group UUID = " + uuid.toString())
            val groupChatterID: ChatterID = ChatterID.getGroupChatterID(this.agentUUID, uuid)
            val equal: Boolean = Objects.equal(chatMessageSource.getSourceUUID(), this.circuitInfo.agentID)
            val stringFromVariableUTF: String = SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message)
            val indexOf: Int = stringFromVariableUTF.indexOf(ErrorCode.CONTROLLER_GATT_NOTIFY_FAILED)
            if (indexOf >= 0) {
                val substring: String = stringFromVariableUTF.substring(0, indexOf)
                stringFromVariableUTF = substring + "\n" + stringFromVariableUTF.substring(indexOf + 1)
            }
            if (equal && b != (Byte) 0) {
                stringFromVariableUTF = stringFromVariableUTF + "\n" + "(This notice contains attached item '" + str + "')"
            }
            HandleChatEvent(groupChatterID, SLChatTextEvent(chatMessageSource, this.agentUUID, improvedInstantMessage, stringFromVariableUTF), true)
            if (!(b == (Byte) 0 || (equal ^ 1) == 0)) {
                HandleChatEvent(groupChatterID, SLChatInventoryItemOfferedByGroupNoticeEvent(chatMessageSource, this.agentUUID, improvedInstantMessage, str, SLAssetType.getByType(b2)), false)
            }
        }
    }

    /* DevToolsApp WARNING: Missing block: B:24:0x0113, code:
            if (r0.rlvController.canTeleportToLure(r1) != false) goto L_0x0115
     */
    private fun HandleIM(com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage r8, com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource r9) {
    private fun HandleIM(com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage improvedInstantMessage, com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource chatMessageSource) {
        // Check if RLV controller should handle this IM
        val modules: SLModules = getModules()
        if (modules != null && modules.rlvController.onIncomingIM(improvedInstantMessage)) {
            return; // RLV handled it
        }
        
        val dialogType: Byte = improvedInstantMessage.MessageBlock_Field.Dialog
        
        switch (dialogType) {
            case 0: // MessageFromAgent 
            case 20: // MessageFromAgent
                handlePersonalMessage(improvedInstantMessage, chatMessageSource)
                break
                
            case 1: // MessageBox
            case 2: // GroupNotice  
                handleSystemMessage(improvedInstantMessage)
                break
                
            case 3: // GroupInvitation
                handleGroupInvitation(improvedInstantMessage, chatMessageSource)
                break
                
            case 4: // InventoryOffered
                handleInventoryOffered(improvedInstantMessage, chatMessageSource)
                break
                
            case 9: // InventoryAccepted
                handleInventoryOfferedFromObject(improvedInstantMessage)
                break
                
            case 17: // Session message (group chat)
                HandleSessionIM(improvedInstantMessage, chatMessageSource)
                break
                
            case 19: // MessageFromObject
            case 31: // MessageFromObject  
                handleObjectMessage(improvedInstantMessage, chatMessageSource)
                break
                
            case 22: // RequestTeleport
                handleTeleportLure(improvedInstantMessage, chatMessageSource)
                break
                
            case 26: // RequestLure
                handleLureRequest(improvedInstantMessage, chatMessageSource)
                break
                
            case 32: // GroupNotice
            case 37: // GroupNoticeRequested
                HandleGroupNotice(improvedInstantMessage, chatMessageSource)
                break
                
            case 38: // FriendshipOffered
                handleFriendshipOffered(improvedInstantMessage, chatMessageSource)
                break
                
            case 39: // FriendshipAccepted
            case 40: // FriendshipDeclined
                handleFriendshipResult(improvedInstantMessage, chatMessageSource)
                break
                
            case 41: // TypingStart
                HandleTypingNotification(chatMessageSource, true)
                break
                
            case 42: // TypingStop
                HandleTypingNotification(chatMessageSource, false)
                break
                
            default:
                Debug.Log("HandleIM: unknown type = " + dialogType +
                         ", sessionId = " + improvedInstantMessage.AgentData_Field.SessionID.toString() +
                         ", toAgentID = " + improvedInstantMessage.MessageBlock_Field.ToAgentID.toString() +
                         ", fromGroup = " + improvedInstantMessage.MessageBlock_Field.FromGroup +
                         ", message = '" + SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message) + "'")
                break
        }
    }
    
     private fun handlePersonalMessage(improvedInstantMessage: ImprovedInstantMessage, chatMessageSource: ChatMessageSource) {
        val chatEvent: SLChatTextEvent = SLChatTextEvent(chatMessageSource, this.agentUUID, improvedInstantMessage, null)
        val defaultChatter: ChatterID = chatMessageSource.getDefaultChatter(this.agentUUID)
        val isChatterActive: Boolean = this.userManager.isChatterActive(defaultChatter)
        
        HandleChatEvent(defaultChatter, chatEvent, true)
        
        // Auto-response logic
        if (!this.userManager.isChatterMuted(defaultChatter) &&
            improvedInstantMessage.MessageBlock_Field.Dialog != 20 && // Not MessageFromAgent
            improvedInstantMessage.MessageBlock_Field.Offline == 0 &&
            improvedInstantMessage.MessageBlock_Field.Message.length > 0 &&
            !isChatterActive &&
            defaultChatter instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser) {
            
            val autoResponse: String = SLGridConnection.getAutoresponse()
            if (!com.google.common.base.Strings.isNullOrEmpty(autoResponse)) {
                com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser userChatter = 
                    (com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser) defaultChatter
                SendInstantMessage(userChatter.getChatterUUID(), autoResponse, (Byte) 20)
            }
        }
    }
    
     private fun handleSystemMessage(improvedInstantMessage: ImprovedInstantMessage) {
        val systemEvent: SLChatSystemMessageEvent = SLChatSystemMessageEvent(
            com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown.getInstance(),
            this.agentUUID,
            SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message)
        )
        HandleChatEvent(this.localChatterID, systemEvent, true)
    }
    
     private fun handleGroupInvitation(improvedInstantMessage: ImprovedInstantMessage, chatMessageSource: ChatMessageSource) {
        val invitationEvent: SLChatGroupInvitationEvent = SLChatGroupInvitationEvent(
            chatMessageSource, this.agentUUID, improvedInstantMessage
        )
        HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), invitationEvent, true)
    }
    
     private fun handleInventoryOffered(improvedInstantMessage: ImprovedInstantMessage, chatMessageSource: ChatMessageSource) {
        val inventoryEvent: SLChatInventoryItemOfferedEvent = SLChatInventoryItemOfferedEvent(
            chatMessageSource, this.agentUUID, improvedInstantMessage
        )
        HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), inventoryEvent, true)
    }
    
     private fun handleInventoryOfferedFromObject(improvedInstantMessage: ImprovedInstantMessage) {
        val objectSource: ChatMessageSource = com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject(
            improvedInstantMessage.AgentData_Field.AgentID,
            SLMessage.stringFromVariableOEM(improvedInstantMessage.MessageBlock_Field.FromAgentName)
        )
        
        val inventoryEvent: SLChatInventoryItemOfferedEvent = SLChatInventoryItemOfferedEvent(
            objectSource, this.agentUUID, improvedInstantMessage
        )
        HandleChatEvent(this.localChatterID, inventoryEvent, true)
    }
    
     private fun handleObjectMessage(improvedInstantMessage: ImprovedInstantMessage, chatMessageSource: ChatMessageSource) {
        val chatEvent: SLChatTextEvent = SLChatTextEvent(
            chatMessageSource, this.agentUUID, improvedInstantMessage, null
        )
        HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), chatEvent, true)
    }
    
     private fun handleTeleportLure(improvedInstantMessage: ImprovedInstantMessage, chatMessageSource: ChatMessageSource) {
        val modules: SLModules = getModules()
        if (chatMessageSource.getSourceType() == ChatMessageSource.ChatMessageSourceType.User) {
            val sourceUUID: UUID = chatMessageSource.getSourceUUID()
            if (modules != null) {
                if (modules.rlvController.autoAcceptTeleport(sourceUUID)) {
                    TeleportToLure(improvedInstantMessage.MessageBlock_Field.ID)
                    return
                } else if (!modules.rlvController.canTeleportToLure(sourceUUID)) {
                    return
                }
            }
        }
        
        val lureEvent: SLChatLureEvent = SLChatLureEvent(
            chatMessageSource, this.agentUUID, improvedInstantMessage
        )
        HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), lureEvent, true)
    }
    
     private fun handleLureRequest(improvedInstantMessage: ImprovedInstantMessage, chatMessageSource: ChatMessageSource) {
        val modules: SLModules = getModules()
        if (chatMessageSource.getSourceType() == ChatMessageSource.ChatMessageSourceType.User && modules != null) {
            if (!modules.rlvController.canTeleportToLure(chatMessageSource.getSourceUUID())) {
                return
            }
        }
        
        val lureRequestEvent: SLChatLureRequestEvent = SLChatLureRequestEvent(
            chatMessageSource, this.agentUUID, improvedInstantMessage
        )
        HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), lureRequestEvent, true)
    }
    
     private fun handleFriendshipOffered(improvedInstantMessage: ImprovedInstantMessage, chatMessageSource: ChatMessageSource) {
        val friendshipEvent: SLChatFriendshipOfferedEvent = SLChatFriendshipOfferedEvent(
            chatMessageSource, this.agentUUID, improvedInstantMessage
        )
        HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), friendshipEvent, true)
    }
    
     private fun handleFriendshipResult(improvedInstantMessage: ImprovedInstantMessage, chatMessageSource: ChatMessageSource) {
        val friendshipResultEvent: SLChatFriendshipResultEvent = SLChatFriendshipResultEvent(
            chatMessageSource, this.agentUUID, improvedInstantMessage
        )
        HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), friendshipResultEvent, true)
        
        // If friendship accepted, add to friend list and request online notification
        if (improvedInstantMessage.MessageBlock_Field.Dialog == 39) { // FriendshipAccepted
            if (chatMessageSource.getSourceType() == ChatMessageSource.ChatMessageSourceType.User) {
                val sourceUUID: UUID = chatMessageSource.getSourceUUID()
                if (sourceUUID != null) {
                    this.userManager.getChatterList().getFriendManager().addFriend(sourceUUID)
                    SendGenericMessage("requestonlinenotification", Array<String>{sourceUUID.toString()})
                }
            }
        }
    }
    }

    private fun HandleSessionIM(improvedInstantMessage: ImprovedInstantMessage, chatMessageSource: ChatMessageSource) {
        HandleChatEvent(ChatterID.getGroupChatterID(this.agentUUID, improvedInstantMessage.MessageBlock_Field.ID), SLChatTextEvent(chatMessageSource, this.agentUUID, improvedInstantMessage, null), true)
    }

    private fun HandleTeleportFailed(lLSDNode: LLSDNode) {
        try {
            Debug.Log("TeleportFailed: event = " + lLSDNode.serializeToXML())
        } catch (IOException e) {
            e.printStackTrace()
        }
        if (this.teleportRequestSent) {
            this.teleportRequestSent = false
            this.eventBus.publish(SLTeleportResultEvent(false, "Teleport has failed."))
        }
    }

    private fun HandleTeleportFinish(lLSDNode: LLSDNode) {
        try {
            Debug.Log("TeleportFinish: event = " + lLSDNode.serializeToXML())
        } catch (IOException e) {
            e.printStackTrace()
        }
        if (this.teleportRequestSent) {
            this.teleportRequestSent = false
            try {
                val byIndex: LLSDNode = lLSDNode.byKey("Info").byIndex(0)
                val asString: String = byIndex.byKey("SeedCapability").asString()
                val asBinary: ByteArray = byIndex.byKey("SimIP").asBinary()
                Debug.Printf("sim address: %s", SLAuthReply(this.authReply, true, false, this.authReply.agentID, String.format("%d.%d.%d.%d", Array<Any>{Integer.valueOf(asBinary[0] & 255), Integer.valueOf(asBinary[1] & 255), Integer.valueOf(asBinary[2] & 255), Integer.valueOf(asBinary[3] & 255)}), byIndex.byKey("SimPort").asInt(), asString).simAddress)
                this.modules.avatarControl.setEnableAgentUpdates(false)
                this.gridConn.HandleTeleportFinish(r0)
                return
            } catch (LLSDException e2) {
                Debug.Log("TeleportFinish: LLSDException, teleport apparently failed")
                e2.printStackTrace()
                return
            }
        }
        Debug.Log("TeleportFinish: stale teleport finish?")
    }

    private fun HandleTypingNotification(chatMessageSource: ChatMessageSource, z: Boolean) {
        if (chatMessageSource instanceof ChatMessageSourceUser) {
            val sourceUUID: UUID = chatMessageSource.getSourceUUID()
            if (sourceUUID == null) {
                return
            }
            if (z) {
                if (this.typingUsers.add(sourceUUID)) {
                    this.userManager.getChatterList().updateUserTypingStatus(sourceUUID)
                }
            } else if (this.typingUsers.remove(sourceUUID)) {
                this.userManager.getChatterList().updateUserTypingStatus(sourceUUID)
            }
        }
    }

    private fun ProcessObjectSelection() {
        if (getNeedObjectNames() && (this.doingObjectSelection ^ 1) != 0) {
            SLMessage sLMessage
            val sLMessage2: SLMessage = null
            for (SLObjectInfo sLObjectInfo : this.forceNeedObjectNames.values()) {
                if (sLMessage2 == null) {
                    sLMessage2 = ObjectSelect()
                    sLMessage2.AgentData_Field.AgentID = this.circuitInfo.agentID
                    sLMessage2.AgentData_Field.SessionID = this.circuitInfo.sessionID
                }
                if (sLMessage2.ObjectData_Fields.size() > 16) {
                    break
                }
                val objectData: ObjectData = ObjectData()
                objectData.ObjectLocalID = sLObjectInfo.localID
                sLMessage2.ObjectData_Fields.add(objectData)
                sLObjectInfo.nameRequested = true
                sLObjectInfo.nameRequestedAt = System.currentTimeMillis()
                this.objectNamesRequested.put(sLObjectInfo.getId(), sLObjectInfo)
            }
            synchronized (this.gridConn.parcelInfo.objectNamesQueue) {
                for (SLObjectInfo sLObjectInfo2 : this.gridConn.parcelInfo.objectNamesQueue.values()) {
                    if (sLMessage2 == null) {
                        sLMessage2 = ObjectSelect()
                        sLMessage2.AgentData_Field.AgentID = this.circuitInfo.agentID
                        sLMessage2.AgentData_Field.SessionID = this.circuitInfo.sessionID
                    }
                    if (sLMessage2.ObjectData_Fields.size() > 16) {
                        sLMessage = sLMessage2
                        break
                    }
                    val objectData2: ObjectData = ObjectData()
                    objectData2.ObjectLocalID = sLObjectInfo2.localID
                    sLMessage2.ObjectData_Fields.add(objectData2)
                    sLObjectInfo2.nameRequested = true
                    sLObjectInfo2.nameRequestedAt = System.currentTimeMillis()
                    this.objectNamesRequested.put(sLObjectInfo2.getId(), sLObjectInfo2)
                }
                sLMessage = sLMessage2
            }
            if (sLMessage != null) {
                Debug.Log("ObjectSelect: Sending ObjectSelect for " + sLMessage.ObjectData_Fields.size() + " objects, " + this.gridConn.parcelInfo.objectNamesQueue.size() + " remains.")
                sLMessage.isReliable = true
                SendMessage(sLMessage)
                this.lastObjectSelection = System.currentTimeMillis()
                this.doingObjectSelection = true
            }
        }
    }

    private fun ProcessObjectSelectionTimeout() {
        for (SLObjectInfo sLObjectInfo : this.objectNamesRequested.values()) {
            val sLObjectInfo2: SLObjectInfo = (SLObjectInfo) this.gridConn.parcelInfo.objectNamesQueue.remove(sLObjectInfo.getId())
            if (sLObjectInfo2 != null) {
                this.gridConn.parcelInfo.objectNamesQueue.put(sLObjectInfo2.getId(), sLObjectInfo2)
            }
            this.forceNeedObjectNames.remove(sLObjectInfo.getId())
        }
        this.objectNamesRequested.clear()
    }

    private fun SendAgentFOV() {
        val agentFOV: SLMessage = AgentFOV()
        agentFOV.AgentData_Field.AgentID = this.circuitInfo.agentID
        agentFOV.AgentData_Field.SessionID = this.circuitInfo.sessionID
        agentFOV.AgentData_Field.CircuitCode = this.circuitInfo.circuitCode
        agentFOV.FOVBlock_Field.GenCounter = 0
        agentFOV.FOVBlock_Field.VerticalAngle = 3.0543263f
        agentFOV.isReliable = true
        SendMessage(agentFOV)
    }

    private fun SendCompleteAgentMovement() {
        val completeAgentMovement: SLMessage = CompleteAgentMovement()
        completeAgentMovement.AgentData_Field.CircuitCode = this.circuitInfo.circuitCode
        completeAgentMovement.AgentData_Field.AgentID = this.circuitInfo.agentID
        completeAgentMovement.AgentData_Field.SessionID = this.circuitInfo.sessionID
        completeAgentMovement.isReliable = true
        SendMessage(completeAgentMovement)
    }

    private fun SendEstateOwnerMessage(str: String, strArr: Array<String>) {
        val estateOwnerMessage: SLMessage = EstateOwnerMessage()
        estateOwnerMessage.AgentData_Field.AgentID = this.circuitInfo.agentID
        estateOwnerMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID
        estateOwnerMessage.AgentData_Field.TransactionID = UUID(0, 0)
        estateOwnerMessage.MethodData_Field.Method = SLMessage.stringToVariableOEM(str)
        estateOwnerMessage.MethodData_Field.Invoice = UUID(0, 0)
        for (String str2 : strArr) {
            val paramList: ParamList = ParamList()
            paramList.Parameter = SLMessage.stringToVariableOEM(str2)
            estateOwnerMessage.ParamList_Fields.add(paramList)
        }
        estateOwnerMessage.isReliable = true
        SendMessage(estateOwnerMessage)
    }

    private fun SendGroupSessionStart(uuid: UUID) {
        val improvedInstantMessage: SLMessage = ImprovedInstantMessage()
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID
        improvedInstantMessage.MessageBlock_Field.FromGroup = false
        improvedInstantMessage.MessageBlock_Field.ToAgentID = uuid
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0
        improvedInstantMessage.MessageBlock_Field.RegionID = UUID(0, 0)
        improvedInstantMessage.MessageBlock_Field.Position = this.modules.avatarControl.getAgentPosition().getPosition()
        improvedInstantMessage.MessageBlock_Field.Offline = 0
        improvedInstantMessage.MessageBlock_Field.Dialog = 15
        improvedInstantMessage.MessageBlock_Field.ID = uuid
        improvedInstantMessage.MessageBlock_Field.Timestamp = 0
        improvedInstantMessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo")
        improvedInstantMessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF("")
        improvedInstantMessage.MessageBlock_Field.BinaryBucket = Byte[1]
        improvedInstantMessage.isReliable = true
        SendMessage(improvedInstantMessage)
    }

    private fun SendInstantMessage(uuid: UUID, str: String, i: Int): Boolean {
        if (!getModules().rlvController.canSendIM(uuid)) {
            return false
        }
        val improvedInstantMessage: SLMessage = ImprovedInstantMessage()
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID
        improvedInstantMessage.MessageBlock_Field.FromGroup = false
        improvedInstantMessage.MessageBlock_Field.ToAgentID = uuid
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0
        improvedInstantMessage.MessageBlock_Field.RegionID = UUID(0, 0)
        improvedInstantMessage.MessageBlock_Field.Position = LLVector3()
        improvedInstantMessage.MessageBlock_Field.Offline = 0
        improvedInstantMessage.MessageBlock_Field.Dialog = i
        improvedInstantMessage.MessageBlock_Field.ID = UUID(uuid.getMostSignificantBits() ^ this.circuitInfo.agentID.getMostSignificantBits(), uuid.getLeastSignificantBits() ^ this.circuitInfo.agentID.getLeastSignificantBits())
        improvedInstantMessage.MessageBlock_Field.Timestamp = 0
        improvedInstantMessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo")
        improvedInstantMessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF(str)
        improvedInstantMessage.MessageBlock_Field.BinaryBucket = Byte[0]
        improvedInstantMessage.isReliable = true
        SendMessage(improvedInstantMessage)
        if (!(i == 20 || i == 41 || i == 42)) {
            if (i == 26) {
                HandleChatEvent(ChatterID.getUserChatterID(this.agentUUID, uuid), SLChatLureRequestedEvent(str, this.agentUUID), false)
            } else {
                HandleChatEvent(ChatterID.getUserChatterID(this.agentUUID, uuid), SLChatTextEvent(ChatMessageSourceUser(this.circuitInfo.agentID), this.agentUUID, str), false)
            }
        }
        return true
    }

    private fun SendRetrieveInstantMessages() {
        val retrieveInstantMessages: SLMessage = RetrieveInstantMessages()
        retrieveInstantMessages.AgentData_Field.AgentID = this.circuitInfo.agentID
        retrieveInstantMessages.AgentData_Field.SessionID = this.circuitInfo.sessionID
        retrieveInstantMessages.isReliable = true
        SendMessage(retrieveInstantMessages)
    }

     private fun getActiveGroupID(): UUID {
        return this.modules != null ? this.modules.groupManager.getActiveGroupID() : null
    }

     private fun getNeedObjectNames(): Boolean {
        if (this.forceNeedObjectNames != null && !this.forceNeedObjectNames.isEmpty()) {
            return true
        }
        return this.modules != null ? this.modules.drawDistance.isObjectSelectEnabled() : false
    }

     private fun isEventMuted(chatterID: ChatterID, sLChatEvent: SLChatEvent): Boolean {
        if (this.modules != null) {
            val sLMuteList: SLMuteList = this.modules.muteList
            val source: ChatMessageSource = sLChatEvent.getSource()
            if (source.getSourceType() == ChatMessageSourceType.User) {
                if (sLMuteList.isMuted(source.getSourceUUID(), MuteType.AGENT)) {
                    return true
                }
            } else if (source.getSourceType() == ChatMessageSourceType.Object) {
                val sourceUUID: UUID = source.getSourceUUID()
                if (sourceUUID != null && !sourceUUID.equals(UUIDPool.ZeroUUID) && sLMuteList.isMuted(sourceUUID, MuteType.OBJECT)) {
                    return true
                }
                val sourceName: String = source.getSourceName(this.userManager)
                if (sourceName != null && sLMuteList.isMutedByName(sourceName)) {
                    return true
                }
            }
            if (chatterID instanceof ChatterIDGroup) {
                val chatterUUID: UUID = ((ChatterIDGroup) chatterID).getChatterUUID()
                if (!chatterUUID.equals(UUIDPool.ZeroUUID) && sLMuteList.isMuted(chatterUUID, MuteType.GROUP)) {
                    return true
                }
            }
        }
        return false
    }

     private fun notifyObjectPropertiesChange() {
        if (this.userManager != null) {
            this.userManager.getObjectsManager().requestObjectListUpdate()
        }
    }

     private fun processMyAvatarUpdate(sLObjectAvatarInfo: SLObjectAvatarInfo) {
        if (this.modules != null) {
            this.modules.avatarControl.setAgentPosition(sLObjectAvatarInfo.getAbsolutePosition(), sLObjectAvatarInfo.getObjectCoords().get(2))
        }
    }

    fun AcceptFriendship(uuid: UUID, uuid2: UUID) {
        val uuid3: UUID = null
        this.userManager.getChatterList().getFriendManager().addFriend(uuid)
        val acceptFriendship: SLMessage = AcceptFriendship()
        acceptFriendship.AgentData_Field.AgentID = this.circuitInfo.agentID
        acceptFriendship.AgentData_Field.SessionID = this.circuitInfo.sessionID
        if (this.modules != null) {
            uuid3 = this.modules.inventory.getCallingCardsFolderUUID()
        }
        val folderData: FolderData = FolderData()
        if (uuid3 == null) {
            uuid3 = UUIDPool.ZeroUUID
        }
        folderData.FolderID = uuid3
        acceptFriendship.FolderData_Fields.add(folderData)
        acceptFriendship.TransactionBlock_Field.TransactionID = uuid2
        acceptFriendship.isReliable = true
        SendMessage(acceptFriendship)
    }

    fun AcceptInventoryOffer(i: Int, z: Boolean, uuid: UUID, uuid2: UUID, uuid3: UUID) {
        val improvedInstantMessage: SLMessage = ImprovedInstantMessage()
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID
        improvedInstantMessage.MessageBlock_Field.FromGroup = false
        improvedInstantMessage.MessageBlock_Field.ToAgentID = uuid
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0
        improvedInstantMessage.MessageBlock_Field.RegionID = UUID(0, 0)
        improvedInstantMessage.MessageBlock_Field.Position = LLVector3()
        improvedInstantMessage.MessageBlock_Field.Offline = 0
        if (z) {
            improvedInstantMessage.MessageBlock_Field.Dialog = i + 1
        } else {
            improvedInstantMessage.MessageBlock_Field.Dialog = i + 2
        }
        improvedInstantMessage.MessageBlock_Field.ID = uuid2
        improvedInstantMessage.MessageBlock_Field.Timestamp = 0
        improvedInstantMessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo")
        improvedInstantMessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF("")
        if (uuid3 != null) {
            val wrap: ByteBuffer = ByteBuffer.wrap(Byte[16])
            wrap.order(ByteOrder.BIG_ENDIAN)
            wrap.putLong(uuid3.getMostSignificantBits())
            wrap.putLong(uuid3.getLeastSignificantBits())
            wrap.position(0)
            improvedInstantMessage.MessageBlock_Field.BinaryBucket = wrap.array()
        } else {
            improvedInstantMessage.MessageBlock_Field.BinaryBucket = Byte[0]
        }
        improvedInstantMessage.isReliable = true
        SendMessage(improvedInstantMessage)
    }

    fun AddFriend(uuid: UUID, str: String) {
        val improvedInstantMessage: SLMessage = ImprovedInstantMessage()
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID
        improvedInstantMessage.MessageBlock_Field.FromGroup = false
        improvedInstantMessage.MessageBlock_Field.ToAgentID = uuid
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0
        improvedInstantMessage.MessageBlock_Field.RegionID = UUID(0, 0)
        improvedInstantMessage.MessageBlock_Field.Position = LLVector3()
        improvedInstantMessage.MessageBlock_Field.Offline = 0
        improvedInstantMessage.MessageBlock_Field.Dialog = 38
        improvedInstantMessage.MessageBlock_Field.ID = UUID(uuid.getMostSignificantBits() ^ this.circuitInfo.agentID.getMostSignificantBits(), uuid.getLeastSignificantBits() ^ this.circuitInfo.agentID.getLeastSignificantBits())
        improvedInstantMessage.MessageBlock_Field.Timestamp = 0
        improvedInstantMessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo")
        improvedInstantMessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF(str)
        improvedInstantMessage.MessageBlock_Field.BinaryBucket = Byte[0]
        improvedInstantMessage.isReliable = true
        SendMessage(improvedInstantMessage)
    }

    fun BuyObject(i: Int, b: Byte, i2: Int) {
        val activeGroupID: UUID = getActiveGroupID()
        val objectBuy: SLMessage = ObjectBuy()
        objectBuy.AgentData_Field.AgentID = this.circuitInfo.agentID
        objectBuy.AgentData_Field.SessionID = this.circuitInfo.sessionID
        val agentData: AgentData = objectBuy.AgentData_Field
        if (activeGroupID == null) {
            activeGroupID = UUIDPool.ZeroUUID
        }
        agentData.GroupID = activeGroupID
        objectBuy.AgentData_Field.CategoryID = getModules().inventory.rootFolder.uuid
        ObjectBuy.ObjectData objectData = ObjectBuy.ObjectData()
        objectData.ObjectLocalID = i
        objectData.SaleType = b
        objectData.SalePrice = i2
        objectBuy.ObjectData_Fields.add(objectData)
        objectBuy.isReliable = true
        SendMessage(objectBuy)
    }

    fun CloseCircuit() {
        Debug.Printf("AgentCircuit: closing circuit.", Object[0])
        if (this.modules != null) {
            this.modules.HandleCloseCircuit()
        }
        if (this.userManager != null) {
            this.userManager.clearActiveAgentCircuit(this)
        }
        if (this.agentNameSubscription != null) {
            this.agentNameSubscription.unsubscribe()
            this.agentNameSubscription = null
        }
        super.CloseCircuit()
    }

    fun DerezObject(i: Int, eDeRezDestination: EDeRezDestination) {
        val activeGroupID: UUID = getActiveGroupID()
        val deRezObject: SLMessage = DeRezObject()
        deRezObject.AgentData_Field.AgentID = this.circuitInfo.agentID
        deRezObject.AgentData_Field.SessionID = this.circuitInfo.sessionID
        val agentBlock: AgentBlock = deRezObject.AgentBlock_Field
        if (activeGroupID == null) {
            activeGroupID = UUID(0, 0)
        }
        agentBlock.GroupID = activeGroupID
        deRezObject.AgentBlock_Field.Destination = eDeRezDestination.getCode()
        deRezObject.AgentBlock_Field.DestinationID = UUID(0, 0)
        deRezObject.AgentBlock_Field.PacketCount = 1
        deRezObject.AgentBlock_Field.PacketNumber = 0
        deRezObject.AgentBlock_Field.TransactionID = UUID.randomUUID()
        DeRezObject.ObjectData objectData = DeRezObject.ObjectData()
        objectData.ObjectLocalID = i
        deRezObject.ObjectData_Fields.add(objectData)
        deRezObject.isReliable = true
        SendMessage(deRezObject)
    }

    fun DoRequestPayPrice(uuid: UUID) {
        val sLObjectInfo: SLObjectInfo = (SLObjectInfo) this.gridConn.parcelInfo.allObjectsNearby.get(uuid)
        if (sLObjectInfo == null) {
            return
        }
        if (sLObjectInfo.getPayInfo() != null) {
            this.eventBus.publish(SLObjectPayInfoEvent(sLObjectInfo))
            return
        }
        val requestPayPrice: SLMessage = RequestPayPrice()
        requestPayPrice.ObjectData_Field.ObjectID = uuid
        requestPayPrice.isReliable = true
        SendMessage(requestPayPrice)
    }

    fun GenerateChatMoneyEvent(uuid: UUID, i: Int, i2: Int) {
        HandleChatEvent(uuid != null ? ChatterID.getUserChatterID(this.agentUUID, uuid) : this.localChatterID, SLChatBalanceChangedEvent(uuid != null ? ChatMessageSourceUser(uuid) : ChatMessageSourceUnknown.getInstance(), this.agentUUID, true, i, i2), true)
        if (this.modules != null) {
            this.modules.financialInfo.RecordChatEvent(uuid, i, i2)
        }
    }

    fun HandleAgentMovementComplete(agentMovementComplete: AgentMovementComplete) {
        this.regionHandle = agentMovementComplete.Data_Field.RegionHandle
        this.modules.avatarControl.setAgentPosition(agentMovementComplete.Data_Field.Position, null)
        Debug.Printf("Got agentPosition: %s", this.modules.avatarControl.getAgentPosition().getImmutablePosition())
        SendAgentFOV()
        this.modules.avatarAppearance.SendAgentWearablesRequest()
        SendRetrieveInstantMessages()
        this.modules.avatarControl.setEnableAgentUpdates(true)
    }

    fun HandleAlertMessage(alertMessage: AlertMessage) {
        HandleChatEvent(this.localChatterID, SLChatSystemMessageEvent(ChatMessageSourceUnknown.getInstance(), this.agentUUID, SLMessage.stringFromVariableOEM(alertMessage.AlertData_Field.Message)), true)
    }

    fun HandleAvatarAnimation(avatarAnimation: AvatarAnimation) {
        val sLParcelInfo: SLParcelInfo = this.gridConn.parcelInfo
        if (sLParcelInfo != null && this.modules != null) {
            sLParcelInfo.ApplyAvatarAnimation(avatarAnimation, this.modules.avatarControl)
        }
    }

    fun HandleAvatarAppearance(avatarAppearance: AvatarAppearance) {
        Debug.Log("Got AvatarAppearance, ID = " + avatarAppearance.Sender_Field.ID.toString() + " isTrial = " + avatarAppearance.Sender_Field.IsTrial + ", our ID = " + this.circuitInfo.agentID.toString())
        if (avatarAppearance.Sender_Field.ID.equals(this.circuitInfo.agentID) && this.modules != null) {
            this.modules.avatarAppearance.HandleAvatarAppearance(avatarAppearance)
        }
        val sLParcelInfo: SLParcelInfo = this.gridConn.parcelInfo
        if (sLParcelInfo != null) {
            sLParcelInfo.ApplyAvatarAppearance(avatarAppearance)
        }
    }

    fun HandleAvatarInterestsReply(avatarInterestsReply: AvatarInterestsReply) {
        Debug.Log("got AvatarInterestsReply: wantToText = " + SLMessage.stringFromVariableOEM(avatarInterestsReply.PropertiesData_Field.WantToText))
        Debug.Log("got AvatarInterestsReply: skillText = " + SLMessage.stringFromVariableOEM(avatarInterestsReply.PropertiesData_Field.SkillsText))
    }

    fun HandleChatEvent(chatterID: ChatterID, sLChatEvent: SLChatEvent, z: Boolean) {
        if (!isEventMuted(chatterID, sLChatEvent)) {
            this.userManager.getChatterList().getActiveChattersManager().HandleChatEvent(chatterID, sLChatEvent, z)
        }
    }

    fun HandleChatFromSimulator(chatFromSimulator: ChatFromSimulator) {
        val modules: SLModules = getModules()
        if (modules == null || !modules.rlvController.onIncomingChat(chatFromSimulator)) {
            val uuid: UUID = chatFromSimulator.ChatData_Field.SourceID
            val stringFromVariableOEM: String = SLMessage.stringFromVariableOEM(chatFromSimulator.ChatData_Field.FromName)
            val stringFromVariableUTF: String = SLMessage.stringFromVariableUTF(chatFromSimulator.ChatData_Field.Message)
            if (chatFromSimulator.ChatData_Field.ChatType != 8 || chatFromSimulator.ChatData_Field.SourceType != 2 || !stringFromVariableOEM.startsWith("#Firestorm LSL Bridge") || !stringFromVariableUTF.startsWith("<bridgeURL>")) {
                if ((chatFromSimulator.ChatData_Field.SourceType != 1 || modules == null || modules.rlvController.canRecvChat(stringFromVariableUTF, uuid)) && chatFromSimulator.ChatData_Field.Audible == 1) {
                    val i: Int = chatFromSimulator.ChatData_Field.ChatType
                    if (i != 6 && i != 4 && i != 5) {
                        switch (chatFromSimulator.ChatData_Field.SourceType) {
                            case 1:
                                HandleChatEvent(this.localChatterID, SLChatTextEvent(ChatMessageSourceUser(uuid), this.agentUUID, stringFromVariableUTF), true)
                                break
                            case 2:
                                HandleChatEvent(this.localChatterID, SLChatTextEvent(ChatMessageSourceObject(uuid, stringFromVariableOEM), this.agentUUID, stringFromVariableUTF), true)
                                break
                            default:
                                HandleChatEvent(this.localChatterID, SLChatTextEvent(ChatMessageSourceUnknown.getInstance(), this.agentUUID, stringFromVariableUTF), true)
                                break
                        }
                    }
                }
            }
        }
    }

    fun HandleImprovedInstantMessage(improvedInstantMessage: ImprovedInstantMessage) {
        ChatMessageSource chatMessageSourceObject
        val i: Int = improvedInstantMessage.MessageBlock_Field.Dialog
        if (i == 19 || i == 31) {
            chatMessageSourceObject = ChatMessageSourceObject(improvedInstantMessage.AgentData_Field.AgentID, SLMessage.stringFromVariableOEM(improvedInstantMessage.MessageBlock_Field.FromAgentName))
        } else if (i == 3) {
            chatMessageSourceObject = ChatMessageSourceUnknown.getInstance()
        } else if (UUIDPool.ZeroUUID.equals(improvedInstantMessage.AgentData_Field.AgentID)) {
            chatMessageSourceObject = ChatMessageSourceUnknown.getInstance()
        } else {
            chatMessageSourceObject = ChatMessageSourceUser(improvedInstantMessage.AgentData_Field.AgentID)
            if (!getModules().rlvController.canRecvIM(chatMessageSourceObject.getSourceUUID())) {
                return
            }
        }
        HandleIM(improvedInstantMessage, chatMessageSourceObject)
    }

    fun HandleImprovedTerseObjectUpdate(improvedTerseObjectUpdate: ImprovedTerseObjectUpdate) {
        val sLParcelInfo: SLParcelInfo = this.gridConn.parcelInfo
        val requestMultipleObjects: RequestMultipleObjects = null
        for (ImprovedTerseObjectUpdate.ObjectData objectData : improvedTerseObjectUpdate.ObjectData_Fields) {
            SLObjectInfo sLObjectInfo
            val localID: Int = SLObjectInfo.getLocalID(objectData)
            val uuid: UUID = (UUID) sLParcelInfo.uuidsNearby.get(Integer.valueOf(localID))
            if (uuid != null) {
                sLObjectInfo = (SLObjectInfo) sLParcelInfo.allObjectsNearby.get(uuid)
                if (sLObjectInfo != null) {
                    sLObjectInfo.ApplyTerseObjectUpdate(objectData)
                    if (sLObjectInfo instanceof SLObjectAvatarInfo ? ((SLObjectAvatarInfo) sLObjectInfo).isMyAvatar() : false) {
                        processMyAvatarUpdate((SLObjectAvatarInfo) sLObjectInfo)
                    } else if (sLObjectInfo.isMyAttachment()) {
                        processMyAttachmentUpdate(sLObjectInfo)
                    }
                }
            } else {
                sLObjectInfo = null
            }
            if (sLObjectInfo == null) {
                if (requestMultipleObjects == null) {
                    requestMultipleObjects = RequestMultipleObjects()
                    requestMultipleObjects.AgentData_Field.AgentID = this.circuitInfo.agentID
                    requestMultipleObjects.AgentData_Field.SessionID = this.circuitInfo.sessionID
                }
                RequestMultipleObjects.ObjectData objectData2 = RequestMultipleObjects.ObjectData()
                objectData2.CacheMissType = 0
                objectData2.ID = localID
                requestMultipleObjects.ObjectData_Fields.add(objectData2)
            }
            requestMultipleObjects = requestMultipleObjects
        }
        if (requestMultipleObjects != null) {
            Debug.Log("Handing cache miss for terse update: " + requestMultipleObjects.ObjectData_Fields.size() + " objects.")
            requestMultipleObjects.isReliable = true
            SendMessage(requestMultipleObjects)
        }
    }

    fun HandleKillObject(killObject: KillObject) {
        val sLParcelInfo: SLParcelInfo = this.gridConn.parcelInfo
        val obj2: Object = null
        val it: Iterator = killObject.ObjectData_Fields.iterator()
        while (true) {
            obj = obj2
            if (!it.hasNext()) {
                break
            }
            obj2 = sLParcelInfo.killObject(this, ((KillObject.ObjectData) it.next()).ID) ? 1 : obj
        }
        if (obj != null) {
            this.objectPropertiesRateLimiter.fire()
        }
    }

    fun HandleLayerData(layerData: LayerData) {
        if (layerData.LayerID_Field.Type == 76) {
            val sLParcelInfo: SLParcelInfo = this.gridConn.parcelInfo
            if (sLParcelInfo != null) {
                sLParcelInfo.terrainData.ProcessLayerData(layerData.LayerDataData_Field.Data)
            }
        }
    }

    fun HandleLoadURL(loadURL: LoadURL) {
        HandleChatEvent(this.localChatterID, SLChatTextEvent(ChatMessageSourceObject(loadURL.Data_Field.ObjectID, SLMessage.stringFromVariableOEM(loadURL.Data_Field.ObjectName)), this.agentUUID, loadURL), true)
    }

    fun HandleObjectProperties(objectProperties: ObjectProperties) {
        Debug.Log("ObjectProperties: " + objectProperties.ObjectData_Fields.size() + " ObjectSelect replies. Reqd " + this.objectNamesRequested.size() + " obj, remains " + this.gridConn.parcelInfo.objectNamesQueue.size() + " objects.")
        for (ObjectProperties.ObjectData objectData : objectProperties.ObjectData_Fields) {
            val sLObjectInfo: SLObjectInfo = (SLObjectInfo) this.gridConn.parcelInfo.objectNamesQueue.remove(objectData.ObjectID)
            if (sLObjectInfo != null) {
                sLObjectInfo.ApplyObjectProperties(objectData)
                this.userManager.getObjectsManager().requestObjectProfileUpdate(sLObjectInfo.localID)
            }
            sLObjectInfo = (SLObjectInfo) this.forceNeedObjectNames.remove(objectData.ObjectID)
            if (sLObjectInfo != null) {
                sLObjectInfo.ApplyObjectProperties(objectData)
                this.userManager.getObjectsManager().requestObjectProfileUpdate(sLObjectInfo.localID)
                sLObjectInfo = sLObjectInfo.getParentObject()
                if (sLObjectInfo != null) {
                    val id: UUID = sLObjectInfo.getId()
                    if (id != null) {
                        this.userManager.getObjectsManager().requestTouchableChildrenUpdate(id)
                    }
                }
            }
            this.objectNamesRequested.remove(objectData.ObjectID)
        }
        if (this.objectNamesRequested.isEmpty()) {
            this.doingObjectSelection = false
            ProcessObjectSelection()
        }
        this.objectPropertiesRateLimiter.fire()
    }

    fun HandleObjectUpdate(objectUpdate: ObjectUpdate) {
        val sLParcelInfo: SLParcelInfo = this.gridConn.parcelInfo
        val obj: Object = null
        val obj2: Object = null
        for (ObjectUpdate.ObjectData objectData : objectUpdate.ObjectData_Fields) {
            if (objectData.PCode == 47 || objectData.PCode == 9) {
                val sLObjectInfo: SLObjectInfo = (SLObjectInfo) sLParcelInfo.allObjectsNearby.get(objectData.FullID)
                if (sLObjectInfo != null) {
                    val i: Int = sLObjectInfo.parentID
                    sLObjectInfo.ApplyObjectUpdate(objectData)
                    sLParcelInfo.updateObjectParent(i, sLObjectInfo)
                    if (sLObjectInfo.parentID != i && (sLObjectInfo instanceof SLObjectAvatarInfo) && ((SLObjectAvatarInfo) sLObjectInfo).isMyAvatar()) {
                        obj = 1
                    }
                    obj2 = 1
                } else {
                    sLObjectInfo = SLObjectInfo.create(this.agentUUID, objectData, this.circuitInfo.agentID)
                    if (sLParcelInfo.addObject(sLObjectInfo)) {
                        obj2 = 1
                    }
                    if ((sLObjectInfo instanceof SLObjectAvatarInfo) && ((SLObjectAvatarInfo) sLObjectInfo).isMyAvatar()) {
                        Debug.Log("ObjectUpdate: got my avatar (normal)")
                        sLParcelInfo.setAgentAvatar((SLObjectAvatarInfo) sLObjectInfo)
                        this.modules.avatarAppearance.OnMyAvatarCreated((SLObjectAvatarInfo) sLObjectInfo)
                        val i2: Int = 1
                    }
                }
                if (sLObjectInfo instanceof SLObjectAvatarInfo ? ((SLObjectAvatarInfo) sLObjectInfo).isMyAvatar() : false) {
                    processMyAvatarUpdate((SLObjectAvatarInfo) sLObjectInfo)
                } else if (sLObjectInfo.isMyAttachment()) {
                    processMyAttachmentUpdate(sLObjectInfo)
                }
            }
            obj = obj
            obj2 = obj2
        }
        if (obj != null) {
            this.userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value)
        }
        if (obj2 != null) {
            ProcessObjectSelection()
            this.objectPropertiesRateLimiter.fire()
        }
    }

    fun HandleObjectUpdateCached(objectUpdateCached: ObjectUpdateCached) {
        val requestMultipleObjects: SLMessage = RequestMultipleObjects()
        requestMultipleObjects.AgentData_Field.AgentID = this.circuitInfo.agentID
        requestMultipleObjects.AgentData_Field.SessionID = this.circuitInfo.sessionID
        for (ObjectUpdateCached.ObjectData objectData : objectUpdateCached.ObjectData_Fields) {
            RequestMultipleObjects.ObjectData objectData2 = RequestMultipleObjects.ObjectData()
            objectData2.CacheMissType = 0
            objectData2.ID = objectData.ID
            requestMultipleObjects.ObjectData_Fields.add(objectData2)
        }
        requestMultipleObjects.isReliable = true
        SendMessage(requestMultipleObjects)
    }

    fun HandleObjectUpdateCompressed(objectUpdateCompressed: ObjectUpdateCompressed) {
        val sLParcelInfo: SLParcelInfo = this.gridConn.parcelInfo
        val obj: Object = null
        val obj2: Object = null
        for (ObjectUpdateCompressed.ObjectData objectData : objectUpdateCompressed.ObjectData_Fields) {
            try {
                val uuid: UUID = (UUID) sLParcelInfo.uuidsNearby.get(Integer.valueOf(SLObjectInfo.getLocalID(objectData)))
                val sLObjectInfo: SLObjectInfo = uuid != null ? (SLObjectInfo) sLParcelInfo.allObjectsNearby.get(uuid) : null
                if (sLObjectInfo != null) {
                    val i: Int = sLObjectInfo.parentID
                    sLObjectInfo.ApplyObjectUpdate(objectData)
                    sLParcelInfo.updateObjectParent(i, sLObjectInfo)
                    obj3 = sLObjectInfo.parentID != i ? 1 : null
                    obj2 = 1
                } else {
                    sLObjectInfo = SLObjectInfo.create(objectData)
                    if (sLParcelInfo.addObject(sLObjectInfo)) {
                        obj2 = 1
                    }
                    obj3 = null
                }
                if (sLObjectInfo instanceof SLObjectAvatarInfo ? ((SLObjectAvatarInfo) sLObjectInfo).isMyAvatar() : false) {
                    if (obj3 != null) {
                        obj = 1
                    }
                    processMyAvatarUpdate((SLObjectAvatarInfo) sLObjectInfo)
                } else if (sLObjectInfo.isMyAttachment()) {
                    processMyAttachmentUpdate(sLObjectInfo)
                }
            } catch (UnsupportedObjectTypeException e) {
            } catch (Throwable e2) {
                Debug.Warning(e2)
            }
            obj = obj
            obj2 = obj2
        }
        if (obj2 != null) {
            ProcessObjectSelection()
            this.objectPropertiesRateLimiter.fire()
        }
        if (obj != null) {
            this.userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value)
        }
    }

    fun HandleOfflineNotification(offlineNotification: OfflineNotification) {
        val arrayList: List = ArrayList(offlineNotification.AgentBlock_Fields.size())
        for (OfflineNotification.AgentBlock agentBlock : offlineNotification.AgentBlock_Fields) {
            arrayList.add(agentBlock.AgentID)
        }
        this.userManager.getChatterList().getFriendManager().setUsersOnline(arrayList, false)
    }

    fun HandleOnlineNotification(onlineNotification: OnlineNotification) {
        val arrayList: List = ArrayList(onlineNotification.AgentBlock_Fields.size())
        for (OnlineNotification.AgentBlock agentBlock : onlineNotification.AgentBlock_Fields) {
            arrayList.add(agentBlock.AgentID)
        }
        this.userManager.getChatterList().getFriendManager().setUsersOnline(arrayList, true)
    }

    fun HandlePayPriceReply(payPriceReply: PayPriceReply) {
        val sLObjectInfo: SLObjectInfo = (SLObjectInfo) this.gridConn.parcelInfo.allObjectsNearby.get(payPriceReply.ObjectData_Field.ObjectID)
        if (sLObjectInfo != null) {
            val i: Int = payPriceReply.ObjectData_Field.DefaultPayPrice
            val iArr: IntArray = Int[payPriceReply.ButtonData_Fields.size()]
            val i2: Int = 0
            while (true) {
                val i3: Int = i2
                if (i3 >= payPriceReply.ButtonData_Fields.size()) {
                    break
                }
                iArr[i3] = ((ButtonData) payPriceReply.ButtonData_Fields.get(i3)).PayButton
                i2 = i3 + 1
            }
            sLObjectInfo.setPayInfo(PayInfo.create(i, iArr))
            if (this.userManager != null) {
                this.userManager.getObjectsManager().requestObjectProfileUpdate(sLObjectInfo.localID)
            }
            this.eventBus.publish(SLObjectPayInfoEvent(sLObjectInfo))
        }
    }

    fun HandleRegionHandshake(regionHandshake: RegionHandshake) {
        if (!this.authReply.isTemporary) {
            val regionHandshakeReply: SLMessage = RegionHandshakeReply()
            regionHandshakeReply.AgentData_Field.AgentID = this.circuitInfo.agentID
            regionHandshakeReply.AgentData_Field.SessionID = this.circuitInfo.sessionID
            regionHandshakeReply.RegionInfo_Field.Flags = 0
            if (!(this.gridConn == null || this.gridConn.parcelInfo == null)) {
                this.gridConn.parcelInfo.terrainData.ApplyRegionInfo(regionHandshake.RegionInfo_Field)
            }
            SendMessage(regionHandshakeReply)
            this.regionName = SLMessage.stringFromVariableOEM(regionHandshake.RegionInfo_Field.SimName)
            if (!(regionHandshake.RegionInfo2_Field == null || regionHandshake.RegionInfo2_Field.RegionID == null)) {
                this.regionID = regionHandshake.RegionInfo2_Field.RegionID
            }
            this.isEstateManager = regionHandshake.RegionInfo_Field.IsEstateManager
            this.agentNameSubscription = this.userManager.getUserNames().subscribe(this.circuitInfo.agentID, () -> { // Lambda implementation })
            if (this.eventBus != null) {
                this.eventBus.publish(SLRegionInfoChangedEvent())
            }
        }
    }

    fun HandleScriptDialog(scriptDialog: ScriptDialog) {
        Array<String> strArr
        val i: Int = 0
        if (scriptDialog.Buttons_Fields.size() > 0) {
            val strArr2: Array<String> = String[scriptDialog.Buttons_Fields.size()]
            val i2: Int = 0
            for (Buttons buttons : scriptDialog.Buttons_Fields) {
                strArr2[i2] = SLMessage.stringFromVariableUTF(buttons.ButtonLabel)
                if (strArr2[i2].equals("!!llTextBox!!")) {
                    i = i2
                    z = true
                    strArr = strArr2
                    break
                }
                i2++
            }
            z = false
            strArr = strArr2
        } else {
            strArr = null
            z = false
        }
        if (z) {
            HandleChatEvent(this.localChatterID, SLChatTextBoxDialog(scriptDialog, this.agentUUID, i), true)
        } else {
            HandleChatEvent(this.localChatterID, SLChatScriptDialog(scriptDialog, this.agentUUID, strArr), true)
        }
    }

    fun HandleSimulatorViewerTimeMessage(simulatorViewerTimeMessage: SimulatorViewerTimeMessage) {
        if (!this.authReply.isTemporary && this.gridConn != null && this.gridConn.parcelInfo != null) {
            val f: Float = (simulatorViewerTimeMessage.TimeInfo_Field.SunPhase / 6.2831855f) + 0.25f
            this.gridConn.parcelInfo.setSunHour((Float) (((Double) f) - Math.floor((Double) f)))
        }
    }

    fun HandleTeleportFailed(teleportFailed: TeleportFailed) {
        Debug.Log("TeleportFailed: reason = " + SLMessage.stringFromVariableOEM(teleportFailed.Info_Field.Reason))
        this.teleportRequestSent = false
        this.eventBus.publish(SLTeleportResultEvent(false, SLMessage.stringFromVariableOEM(teleportFailed.Info_Field.Reason)))
    }

    fun HandleTeleportLocal(teleportLocal: TeleportLocal) {
        this.teleportRequestSent = false
        this.eventBus.publish(SLTeleportResultEvent(true, null))
    }

    fun HandleTeleportProgress(teleportProgress: TeleportProgress) {
        Debug.Log("Teleport progress: flags = " + teleportProgress.Info_Field.TeleportFlags + ", progress = " + SLMessage.stringFromVariableOEM(teleportProgress.Info_Field.Message))
    }

    fun HandleTeleportStart(teleportStart: TeleportStart) {
        Debug.Log("TeleportStart: flags = " + teleportStart.Info_Field.TeleportFlags)
    }

    fun OfferInventoryItem(uuid: UUID, sLInventoryEntry: SLInventoryEntry) {
        this.userManager.getInventoryManager().getExecutor().execute(com.lumiyaviewer.lumiya.slproto.-$Lambda$K1xWCpEh0d4XNuVVYxGUJwEFRxU.AnonymousClass1(this, sLInventoryEntry, uuid))
    }

    fun OfferTeleport(uuid: UUID, str: String) {
        val startLure: SLMessage = StartLure()
        startLure.AgentData_Field.AgentID = this.circuitInfo.agentID
        startLure.AgentData_Field.SessionID = this.circuitInfo.sessionID
        startLure.Info_Field.Message = SLMessage.stringToVariableUTF(str)
        val targetData: TargetData = TargetData()
        targetData.TargetID = uuid
        startLure.TargetData_Fields.add(targetData)
        startLure.isReliable = true
        SendMessage(startLure)
    }

    fun OnCapsEvent(capsEvent: CapsEvent) {
        try {
            this.capsEventQueue.add(capsEvent)
            this.selector.wakeup()
        } catch (Exception e) {
        }
    }

    fun ProcessIdle() {
        if (this.doingObjectSelection && System.currentTimeMillis() > this.lastObjectSelection + 15000) {
            this.doingObjectSelection = false
            ProcessObjectSelectionTimeout()
        }
        if (!this.teleportRequestSent && getNeedObjectNames() && (this.doingObjectSelection ^ 1) != 0 && System.currentTimeMillis() >= this.lastObjectSelection + 500) {
            ProcessObjectSelection()
        }
        if (!this.agentPaused) {
            val currentTimeMillis: Long = System.currentTimeMillis()
            if (GridConnectionService.hasVisibleActivities()) {
                this.lastVisibleActivities = currentTimeMillis
            } else if (currentTimeMillis >= this.lastVisibleActivities + 10000) {
                DoAgentPause()
            }
        }
        if (this.objectPropertiesRateLimiter != null) {
            this.objectPropertiesRateLimiter.firePending()
        }
    }

    fun ProcessNetworkError() {
        super.ProcessNetworkError()
        Debug.Printf("Network: Network error.", Object[0])
        if (this.modules != null) {
            this.modules.avatarControl.setEnableAgentUpdates(false)
        }
        if (!this.authReply.isTemporary) {
            this.gridConn.processDisconnect(false, "Network connection lost.")
        }
    }

    fun ProcessTimeout() {
        super.ProcessTimeout()
        if (this.modules != null) {
            this.modules.avatarControl.setEnableAgentUpdates(false)
        }
        if (!this.authReply.isTemporary) {
            this.gridConn.processDisconnect(false, "Connection has timed out.")
        }
    }

    fun ProcessWakeup() {
        super.ProcessWakeup()
        while (true) {
            try {
                val capsEvent: CapsEvent = (CapsEvent) this.capsEventQueue.poll()
                if (capsEvent == null) {
                    break
                }
                HandleCapsEvent(capsEvent)
            } catch (Exception e) {
            }
        }
        ProcessIdle()
    }

    fun RemoveFriend(uuid: UUID) {
        val terminateFriendship: SLMessage = TerminateFriendship()
        terminateFriendship.AgentData_Field.AgentID = this.circuitInfo.agentID
        terminateFriendship.AgentData_Field.SessionID = this.circuitInfo.sessionID
        terminateFriendship.ExBlock_Field.OtherID = uuid
        terminateFriendship.isReliable = true
        SendMessage(terminateFriendship)
        this.userManager.getChatterList().getFriendManager().removeFriend(uuid)
    }

    fun RequestObjectName(sLObjectInfo: SLObjectInfo) {
        if (!(sLObjectInfo.getId() == null || this.objectNamesRequested.containsKey(sLObjectInfo.getId()) || (this.forceNeedObjectNames.containsKey(sLObjectInfo.getId()) ^ 1) == 0)) {
            this.forceNeedObjectNames.put(sLObjectInfo.getId(), sLObjectInfo)
        }
        TryWakeUp()
    }

    fun RequestTeleport(uuid: UUID, str: String) {
        SendInstantMessage(uuid, str, 26)
    }

    public fun RestartRegion(i: Int): Boolean {
        if (!this.isEstateManager) {
            return false
        }
        SendEstateOwnerMessage("restart", Array<String>{Integer.toString(i)})
        return true
    }

    /* DevToolsApp WARNING: Removed duplicated region for block: B:24:0x015a  */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:10:0x0023  */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:25:0x015d  */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:13:0x002d  */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:18:0x0047  */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:21:0x014c  */
    fun RezObject(com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry r8) {
    fun RezObject(com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry inventoryEntry) {
        val groupID: UUID = com.lumiyaviewer.lumiya.utils.UUIDPool.ZeroUUID
        val parcelOwnerID: UUID = null
        
        // Get parcel information to determine group ownership
        if (this.userManager != null) {
            var locationInfo = this.userManager.getCurrentLocationInfoSnapshot()
            if (locationInfo != null) {
                var parcelData = locationInfo.parcelData()
                if (parcelData != null && parcelData.isGroupOwned()) {
                    parcelOwnerID = parcelData.getOwnerID()
                }
            }
        }
        
        // Determine the group to use for rezzing
        if (parcelOwnerID != null && !com.lumiyaviewer.lumiya.utils.UUIDPool.ZeroUUID.equals(parcelOwnerID)) {
            groupID = parcelOwnerID
        } else {
            // Check if we're in a group that can rez objects
            if (this.userManager != null) {
                var groupManager = this.userManager.getChatterList().getGroupManager()
                var avatarGroupList = groupManager.getAvatarGroupList()
                if (avatarGroupList != null && avatarGroupList.Groups.containsKey(groupID)) {
                    // Use the group ID
                } else {
                    groupID = getActiveGroupID()
                }
            } else {
                groupID = getActiveGroupID()
            }
        }
        
        if (groupID == null) {
            groupID = com.lumiyaviewer.lumiya.utils.UUIDPool.ZeroUUID
        }
        
        // Create the RezObject message
        com.lumiyaviewer.lumiya.slproto.messages.RezObject rezMessage = 
            com.lumiyaviewer.lumiya.slproto.messages.RezObject()
        
        // Set agent data
        rezMessage.AgentData_Field.AgentID = this.circuitInfo.agentID
        rezMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID
        rezMessage.AgentData_Field.GroupID = groupID
        
        // Set rez data
        rezMessage.RezData_Field.FromTaskID = com.lumiyaviewer.lumiya.utils.UUIDPool.ZeroUUID
        rezMessage.RezData_Field.BypassRaycast = true
        
        // Set position and rotation based on avatar location
        var agentPosition = this.modules.avatarControl.getAgentPosition().getPosition()
        rezMessage.RezData_Field.RayStart = agentPosition
        
        // Calculate end position (1.5 meters in front of avatar)
        var agentHeading = this.getModules().avatarControl.getAgentHeading()
        rezMessage.RezData_Field.RayEnd = agentPosition.getRotatedOffset(1.5f, agentHeading)
        
        rezMessage.RezData_Field.RayEndIsIntersection = true
        rezMessage.RezData_Field.RayTargetID = com.lumiyaviewer.lumiya.utils.UUIDPool.ZeroUUID
        rezMessage.RezData_Field.RezSelected = false
        rezMessage.RezData_Field.RemoveItem = false
        rezMessage.RezData_Field.ItemFlags = 0
        rezMessage.RezData_Field.GroupMask = inventoryEntry.groupMask
        rezMessage.RezData_Field.EveryoneMask = inventoryEntry.everyoneMask
        rezMessage.RezData_Field.NextOwnerMask = inventoryEntry.nextOwnerMask
        
        // Set inventory data
        rezMessage.InventoryData_Field.ItemID = inventoryEntry.uuid
        rezMessage.InventoryData_Field.FolderID = inventoryEntry.parentUUID
        rezMessage.InventoryData_Field.CreatorID = inventoryEntry.creatorUUID
        rezMessage.InventoryData_Field.OwnerID = inventoryEntry.ownerUUID
        rezMessage.InventoryData_Field.GroupID = inventoryEntry.groupUUID
        rezMessage.InventoryData_Field.BaseMask = inventoryEntry.baseMask
        rezMessage.InventoryData_Field.OwnerMask = inventoryEntry.ownerMask
        rezMessage.InventoryData_Field.GroupMask = inventoryEntry.groupMask
        rezMessage.InventoryData_Field.EveryoneMask = inventoryEntry.everyoneMask
        rezMessage.InventoryData_Field.NextOwnerMask = inventoryEntry.nextOwnerMask
        rezMessage.InventoryData_Field.GroupOwned = inventoryEntry.isGroupOwned
        rezMessage.InventoryData_Field.TransactionID = java.util.UUID.randomUUID()
        rezMessage.InventoryData_Field.Type = inventoryEntry.assetType
        rezMessage.InventoryData_Field.InvType = inventoryEntry.invType
        rezMessage.InventoryData_Field.Flags = inventoryEntry.flags
        rezMessage.InventoryData_Field.SaleType = inventoryEntry.saleType
        rezMessage.InventoryData_Field.SalePrice = inventoryEntry.salePrice
        rezMessage.InventoryData_Field.Name = SLMessage.stringToVariableOEM(inventoryEntry.name)
        rezMessage.InventoryData_Field.Description = SLMessage.stringToVariableOEM(inventoryEntry.description)
        rezMessage.InventoryData_Field.CreationDate = inventoryEntry.creationDate
        rezMessage.InventoryData_Field.CRC = 0
        
        rezMessage.isReliable = true
        
        // Set up event listener if the item is not copy-ok (will be removed from inventory)
        if ((inventoryEntry.ownerMask & 0x8000) == 0) { // PERM_COPY bit
            val folderID: UUID = inventoryEntry.parentUUID
            rezMessage.setEventListener(com.lumiyaviewer.lumiya.slproto.SLAgentCircuit.RezObjectEventListener(folderID))
        }
        
        SendMessage(rezMessage)
    }
    
    // Inner class for handling rez object events
    private class RezObjectEventListener : com.lumiyaviewer.lumiya.slproto.messages.SLMessageEventListener {
        private val UUID folderID
        
        public RezObjectEventListener(UUID folderID) {
            this.folderID = folderID
        }
        
        override Unit onMessageAcknowledged() {
            // Object was successfully rezzed, refresh inventory if needed
            if (userManager != null) {
                userManager.getInventoryManager().requestFolderContents(folderID)
            }
        }
        
        override Unit onMessageTimeout() {
            // Object rezzing failed
            Debug.Log("RezObject message timed out")
        }
    }
    }

    fun SendChatMessage(chatterID: ChatterID, str: String) {
        switch (m39-getcom-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues()[chatterID.getChatterType().ordinal()]) {
            case 1:
                SendGroupInstantMessage(chatterID.getOptionalChatterUUID(), str)
                return
            case 2:
                SendLocalChatMessage(str)
                return
            case 3:
                SendInstantMessage(chatterID.getOptionalChatterUUID(), str)
                return
            default:
                return
        }
    }

    fun SendGenericMessage(str: String, strArr: Array<String>) {
        val genericMessage: SLMessage = GenericMessage()
        genericMessage.AgentData_Field.AgentID = this.circuitInfo.agentID
        genericMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID
        genericMessage.AgentData_Field.TransactionID = UUID(0, 0)
        genericMessage.MethodData_Field.Method = SLMessage.stringToVariableOEM(str)
        genericMessage.MethodData_Field.Invoice = UUID(0, 0)
        for (String str2 : strArr) {
            GenericMessage.ParamList paramList = GenericMessage.ParamList()
            paramList.Parameter = SLMessage.stringToVariableOEM(str2)
            genericMessage.ParamList_Fields.add(paramList)
        }
        genericMessage.isReliable = true
        SendMessage(genericMessage)
    }

    fun SendGroupInstantMessage(uuid: UUID, str: String) {
        val improvedInstantMessage: SLMessage = ImprovedInstantMessage()
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID
        improvedInstantMessage.MessageBlock_Field.FromGroup = false
        improvedInstantMessage.MessageBlock_Field.ToAgentID = uuid
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0
        improvedInstantMessage.MessageBlock_Field.RegionID = UUID(0, 0)
        improvedInstantMessage.MessageBlock_Field.Position = this.modules.avatarControl.getAgentPosition().getPosition()
        improvedInstantMessage.MessageBlock_Field.Offline = 0
        improvedInstantMessage.MessageBlock_Field.Dialog = 17
        improvedInstantMessage.MessageBlock_Field.ID = uuid
        improvedInstantMessage.MessageBlock_Field.Timestamp = 0
        improvedInstantMessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo")
        improvedInstantMessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF(str)
        improvedInstantMessage.MessageBlock_Field.BinaryBucket = Byte[1]
        improvedInstantMessage.isReliable = true
        synchronized (this.startedGroupSessions) {
            if (this.startedGroupSessions.contains(uuid)) {
                SendMessage(improvedInstantMessage)
            } else {
                SendGroupSessionStart(uuid)
                this.pendingGroupMessages.add(improvedInstantMessage)
            }
        }
    }

    public fun SendInstantMessage(uuid: UUID, str: String): Boolean {
        return SendInstantMessage(uuid, str, 0)
    }

    fun SendLocalChatMessage(str: String) {
        val i: Int = 0
        if (str.startsWith("/")) {
            val i2: Int = 1
            val i3: Int = 0
            while (i2 < str.length() && Character.isDigit(str.charAt(i2))) {
                i3++
                i2++
            }
            if (i3 >= 0) {
                try {
                    i = Integer.parseInt(str.substring(1, i3 + 1))
                    str = str.substring(i3 + 1).trim()
                } catch (Exception e) {
                    e.printStackTrace()
                }
            }
        }
        if (getModules().rlvController.onSendLocalChat(i, str)) {
            val chatFromViewer: SLMessage = ChatFromViewer()
            chatFromViewer.AgentData_Field.AgentID = this.circuitInfo.agentID
            chatFromViewer.AgentData_Field.SessionID = this.circuitInfo.sessionID
            chatFromViewer.ChatData_Field.Channel = i
            chatFromViewer.ChatData_Field.Type = 1
            chatFromViewer.ChatData_Field.Message = SLMessage.stringToVariableUTF(str)
            chatFromViewer.isReliable = true
            SendMessage(chatFromViewer)
        }
    }

    fun SendLogoutRequest() {
        Debug.Log("Logout: Sending logout request.")
        this.modules.avatarControl.setEnableAgentUpdates(false)
        val logoutRequest: SLMessage = LogoutRequest()
        logoutRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        logoutRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        logoutRequest.isReliable = true
        logoutRequest.setEventListener(SLMessageEventListener() {
            fun onMessageAcknowledged(sLMessage: SLMessage) {
                Debug.Log("Logout: Logout request acknowledged.")
                SLAgentCircuit.this.gridConn.processDisconnect(true, "Logged out.")
            }

            fun onMessageTimeout(sLMessage: SLMessage) {
                Debug.Log("Logout: LogoutRequest timed out!")
                SLAgentCircuit.this.gridConn.processDisconnect(false, "Logout request has timed out.")
            }
        SendMessage(logoutRequest)
    }

    fun SendScriptDialogReply(uuid: UUID, i: Int, i2: Int, str: String) {
        val scriptDialogReply: SLMessage = ScriptDialogReply()
        scriptDialogReply.AgentData_Field.AgentID = this.circuitInfo.agentID
        scriptDialogReply.AgentData_Field.SessionID = this.circuitInfo.sessionID
        scriptDialogReply.isReliable = true
        scriptDialogReply.Data_Field.ObjectID = uuid
        scriptDialogReply.Data_Field.ChatChannel = i
        scriptDialogReply.Data_Field.ButtonIndex = i2
        scriptDialogReply.Data_Field.ButtonLabel = SLMessage.stringToVariableUTF(str)
        SendMessage(scriptDialogReply)
    }

    fun SendUseCode() {
        Debug.Printf("Using circuitCode: %d", Integer.valueOf(this.circuitInfo.circuitCode))
        val useCircuitCode: SLMessage = UseCircuitCode()
        useCircuitCode.CircuitCode_Field.Code = this.circuitInfo.circuitCode
        useCircuitCode.CircuitCode_Field.SessionID = this.circuitInfo.sessionID
        useCircuitCode.CircuitCode_Field.ID = this.circuitInfo.agentID
        useCircuitCode.isReliable = true
        useCircuitCode.setEventListener(SLMessageEventListener() {
            fun onMessageAcknowledged(sLMessage: SLMessage) {
                Debug.Log("SLAgentCircuit: UseCircuitCode acknowledged.")
                if (!SLAgentCircuit.this.authReply.isTemporary) {
                    if (SLAgentCircuit.this.authReply.fromTeleport) {
                        Debug.Log("SLAgentCircuit: Ack from teleport, sending Teleport success.")
                        SLAgentCircuit.this.eventBus.publish(SLTeleportResultEvent(true, null))
                    } else {
                        SLAgentCircuit.this.gridConn.notifyLoginSuccess()
                    }
                    SLAgentCircuit.this.SendCompleteAgentMovement()
                    if (SLAgentCircuit.this.modules != null) {
                        SLAgentCircuit.this.modules.HandleCircuitReady()
                    }
                }
            }

            fun onMessageTimeout(sLMessage: SLMessage) {
                if (SLAgentCircuit.this.authReply.fromTeleport) {
                    SLAgentCircuit.this.eventBus.publish(SLTeleportResultEvent(false, "Timed out while connecting to the simulator."))
                } else {
                    SLAgentCircuit.this.gridConn.notifyLoginError("Timed out while connecting to the simulator.")
                }
            }
        SendMessage(useCircuitCode)
    }

    fun StartGroupSessionForVoice(uuid: UUID) {
        val obj: Object = null
        synchronized (this.startedGroupSessions) {
            if (!this.startedGroupSessions.contains(uuid)) {
                SendGroupSessionStart(uuid)
                obj = 1
            }
        }
        if (obj == null) {
            this.modules.voice.onGroupSessionReady(uuid)
        }
    }

    fun TeleportToGlobalPosition(lLVector3: LLVector3) {
        val floor: Int = (Int) Math.floor((Double) lLVector3.x)
        val floor2: Int = (Int) Math.floor((Double) lLVector3.y)
        floor -= floor % 256
        val j: Long = ((Long) (floor2 - (floor2 % 256))) | (((Long) floor) << 32)
        val lLVector32: LLVector3 = LLVector3(lLVector3.x % 256.0f, lLVector3.y % 256.0f, lLVector3.z)
        val lLVector33: LLVector3 = LLVector3(lLVector32)
        lLVector33.x += 1.0f
        Debug.Printf("regionHandle = %s, globalPos = %s", Long.toHexString(j), lLVector3)
        this.teleportRequestSent = true
        val teleportLocationRequest: SLMessage = TeleportLocationRequest()
        teleportLocationRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        teleportLocationRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        teleportLocationRequest.Info_Field.RegionHandle = j
        teleportLocationRequest.Info_Field.Position = lLVector32
        teleportLocationRequest.Info_Field.LookAt = lLVector33
        teleportLocationRequest.isReliable = true
        teleportLocationRequest.setEventListener(SLMessageEventListener() {
            fun onMessageAcknowledged(sLMessage: SLMessage) {
            }

            fun onMessageTimeout(sLMessage: SLMessage) {
                SLAgentCircuit.this.eventBus.publish(SLTeleportResultEvent(false, "Teleport request has timed out."))
            }
        SendMessage(teleportLocationRequest)
    }

    fun TeleportToLandmarkAsset(uuid: UUID) {
        if (getModules().rlvController.canTeleportToLandmark()) {
            this.teleportRequestSent = true
            val teleportLandmarkRequest: SLMessage = TeleportLandmarkRequest()
            teleportLandmarkRequest.Info_Field.AgentID = this.circuitInfo.agentID
            teleportLandmarkRequest.Info_Field.SessionID = this.circuitInfo.sessionID
            teleportLandmarkRequest.Info_Field.LandmarkID = uuid
            teleportLandmarkRequest.isReliable = true
            teleportLandmarkRequest.setEventListener(SLMessageEventListener() {
                fun onMessageAcknowledged(sLMessage: SLMessage) {
                }

                fun onMessageTimeout(sLMessage: SLMessage) {
                    SLAgentCircuit.this.eventBus.publish(SLTeleportResultEvent(false, "Teleport request has timed out."))
                }
            SendMessage(teleportLandmarkRequest)
        }
    }

    public fun TeleportToLocalPosition(lLVector3: LLVector3): Boolean {
        if (this.regionID == null) {
            return false
        }
        Debug.Printf("Teleport: localPos = %s, regionHandle = %d", lLVector3.toString(), Long.valueOf(this.regionHandle))
        this.teleportRequestSent = true
        val teleportLocationRequest: SLMessage = TeleportLocationRequest()
        teleportLocationRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        teleportLocationRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        teleportLocationRequest.Info_Field.RegionHandle = this.regionHandle
        teleportLocationRequest.Info_Field.Position = lLVector3
        teleportLocationRequest.Info_Field.LookAt = LLVector3(lLVector3)
        val lLVector32: LLVector3 = teleportLocationRequest.Info_Field.LookAt
        lLVector32.x += 10.0f
        teleportLocationRequest.isReliable = true
        teleportLocationRequest.setEventListener(SLMessageEventListener() {
            fun onMessageAcknowledged(sLMessage: SLMessage) {
            }

            fun onMessageTimeout(sLMessage: SLMessage) {
                SLAgentCircuit.this.eventBus.publish(SLTeleportResultEvent(false, "Teleport request has timed out."))
            }
        SendMessage(teleportLocationRequest)
        return true
    }

    fun TeleportToLure(uuid: UUID) {
        this.teleportRequestSent = true
        val teleportLureRequest: SLMessage = TeleportLureRequest()
        teleportLureRequest.Info_Field.AgentID = this.circuitInfo.agentID
        teleportLureRequest.Info_Field.SessionID = this.circuitInfo.sessionID
        teleportLureRequest.Info_Field.LureID = uuid
        teleportLureRequest.isReliable = true
        teleportLureRequest.setEventListener(SLMessageEventListener() {
            fun onMessageAcknowledged(sLMessage: SLMessage) {
            }

            fun onMessageTimeout(sLMessage: SLMessage) {
                SLAgentCircuit.this.eventBus.publish(SLTeleportResultEvent(false, "Teleport request has timed out."))
            }
        SendMessage(teleportLureRequest)
    }

    fun TeleportToRegion(j: Long, i: Int, i2: Int, i3: Int) {
        if (getModules().rlvController.canTeleportToLocation()) {
            Debug.Log("TeleportToRegion: regionHandle = " + Long.toHexString(j) + ", pos = (" + i + ", " + i2 + ", " + i3 + ")")
            this.teleportRequestSent = true
            val teleportLocationRequest: SLMessage = TeleportLocationRequest()
            teleportLocationRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
            teleportLocationRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
            teleportLocationRequest.Info_Field.RegionHandle = j
            teleportLocationRequest.Info_Field.Position = LLVector3((Float) i, (Float) i2, (Float) i3)
            teleportLocationRequest.Info_Field.LookAt = LLVector3(0.0f, 1.0f, 0.0f)
            teleportLocationRequest.isReliable = true
            teleportLocationRequest.setEventListener(SLMessageEventListener() {
                fun onMessageAcknowledged(sLMessage: SLMessage) {
                }

                fun onMessageTimeout(sLMessage: SLMessage) {
                    SLAgentCircuit.this.eventBus.publish(SLTeleportResultEvent(false, "Teleport request has timed out."))
                }
            SendMessage(teleportLocationRequest)
        }
    }

    fun TouchObject(i: Int) {
        val objectGrab: SLMessage = ObjectGrab()
        objectGrab.AgentData_Field.AgentID = this.circuitInfo.agentID
        objectGrab.AgentData_Field.SessionID = this.circuitInfo.sessionID
        objectGrab.ObjectData_Field.LocalID = i
        objectGrab.ObjectData_Field.GrabOffset = LLVector3()
        objectGrab.isReliable = true
        SendMessage(objectGrab)
        objectGrab = ObjectDeGrab()
        objectGrab.AgentData_Field.AgentID = this.circuitInfo.agentID
        objectGrab.AgentData_Field.SessionID = this.circuitInfo.sessionID
        objectGrab.ObjectData_Field.LocalID = i
        objectGrab.isReliable = true
        SendMessage(objectGrab)
    }

    fun TouchObjectFace(sLObjectInfo: SLObjectInfo, i: Int, f: Float, f2: Float, f3: Float, f4: Float, f5: Float, f6: Float, f7: Float) {
        Debug.Printf("Touch: Object %d, face %d, pos (%f, %f, %f), uv (%f, %f)", Integer.valueOf(sLObjectInfo.localID), Integer.valueOf(i), Float.valueOf(f), Float.valueOf(f2), Float.valueOf(f3), Float.valueOf(f4), Float.valueOf(f5))
        val objectGrab: SLMessage = ObjectGrab()
        objectGrab.AgentData_Field.AgentID = this.circuitInfo.agentID
        objectGrab.AgentData_Field.SessionID = this.circuitInfo.sessionID
        objectGrab.ObjectData_Field.LocalID = sLObjectInfo.localID
        objectGrab.ObjectData_Field.GrabOffset = LLVector3()
        val surfaceInfo: SurfaceInfo = SurfaceInfo()
        surfaceInfo.FaceIndex = i
        surfaceInfo.Position = LLVector3(f, f2, f3)
        surfaceInfo.UVCoord = LLVector3(f4, f5, 0.0f)
        surfaceInfo.STCoord = LLVector3(f6, f7, 0.0f)
        surfaceInfo.Normal = LLVector3(1.0f, 0.0f, 0.0f)
        surfaceInfo.Binormal = LLVector3(0.0f, 0.0f, 1.0f)
        objectGrab.SurfaceInfo_Fields.add(surfaceInfo)
        objectGrab.isReliable = true
        SendMessage(objectGrab)
        objectGrab = ObjectDeGrab()
        objectGrab.AgentData_Field.AgentID = this.circuitInfo.agentID
        objectGrab.AgentData_Field.SessionID = this.circuitInfo.sessionID
        objectGrab.ObjectData_Field.LocalID = sLObjectInfo.localID
        objectGrab.isReliable = true
        SendMessage(objectGrab)
    }

    fun TryWakeUp() {
        try {
            this.selector.wakeup()
        } catch (Exception e) {
        }
    }

    fun UnpauseAgent() {
        this.lastVisibleActivities = System.currentTimeMillis()
        if (this.agentPaused) {
            DoAgentResume()
        }
    }

     public fun getAgentGlobalPosition(): LLVector3d {
        if (this.modules == null) {
            return null
        }
        val position: LLVector3 = this.modules.avatarControl.getAgentPosition().getPosition()
        val i: Int = (Int) ((this.regionHandle >> 32) & 4294967295L)
        val i2: Int = (Int) (this.regionHandle & 4294967295L)
        val lLVector3d: LLVector3d = LLVector3d()
        lLVector3d.x = ((Double) i) + ((Double) position.x)
        lLVector3d.y = ((Double) i2) + ((Double) position.y)
        lLVector3d.z = (Double) position.z
        return lLVector3d
    }

    @SuppressLint({"DefaultLocale"})
     public fun getAgentSLURL(): String {
        if (this.modules == null || !Objects.equal(this.authReply.loginURL, "https://login.agni.lindenlab.com/cgi-bin/login.cgi") || this.regionName == null) {
            return null
        }
        val position: LLVector3 = this.modules.avatarControl.getAgentPosition().getPosition()
        try {
            return String.format("http://maps.secondlife.com/secondlife/%s/%d/%d/%d", Array<Any>{URLEncoder.encode(this.regionName, "UTF-8"), Integer.valueOf((Int) position.x), Integer.valueOf((Int) position.y), Integer.valueOf((Int) position.z)})
        } catch (UnsupportedEncodingException e) {
            return null
        }
    }

     public fun getAgentUUID(): UUID {
        return this.agentUUID
    }

     public fun getCaps(): SLCaps {
        return this.caps
    }

     public fun getIsEstateManager(): Boolean {
        return this.isEstateManager
    }

     public fun getLocalChatterID(): ChatterID {
        return this.localChatterID
    }

     public fun getModules(): SLModules {
        return this.modules
    }

     public fun getObjectProfile(i: Int): SLObjectProfileData {
        val objectInfo: SLObjectInfo = this.gridConn.parcelInfo.getObjectInfo(i)
        if (objectInfo == null) {
            return null
        }
        val create: SLObjectProfileData = SLObjectProfileData.create(objectInfo)
        if (!(create.name().isPresent() || (objectInfo.isDead ^ 1) == 0)) {
            RequestObjectName(objectInfo)
        }
        return create
    }

     public fun getRegionName(): String {
        return this.regionName
    }

     public fun getSessionID(): UUID {
        return this.circuitInfo.sessionID
    }

     public fun isUserTyping(uuid: UUID): Boolean {
        return Boolean.valueOf(this.typingUsers.contains(uuid))
    }

    /* renamed from: handleUserNameUpdate */
    /* synthetic */ Unit handleUserNameUpdate(UserName userName) {
        this.agentUserName.set(userName)
    }

    /* renamed from: handleInventoryItemOffer */
    /* synthetic */ Unit handleInventoryItemOffer(SLInventoryEntry sLInventoryEntry, UUID uuid) {
        val arrayList: Iterable<SLInventoryEntry> = ArrayList()
        arrayList.add(sLInventoryEntry)
        if (sLInventoryEntry.isFolder) {
            arrayList.addAll(this.modules.inventory.CollectGiveableItems(sLInventoryEntry))
        }
        val improvedInstantMessage: SLMessage = ImprovedInstantMessage()
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID
        improvedInstantMessage.MessageBlock_Field.FromGroup = false
        improvedInstantMessage.MessageBlock_Field.ToAgentID = uuid
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0
        improvedInstantMessage.MessageBlock_Field.RegionID = UUID(0, 0)
        improvedInstantMessage.MessageBlock_Field.Position = LLVector3()
        improvedInstantMessage.MessageBlock_Field.Offline = 0
        improvedInstantMessage.MessageBlock_Field.Dialog = 4
        improvedInstantMessage.MessageBlock_Field.ID = UUID.randomUUID()
        improvedInstantMessage.MessageBlock_Field.Timestamp = 0
        improvedInstantMessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo")
        improvedInstantMessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF(sLInventoryEntry.name)
        val wrap: ByteBuffer = ByteBuffer.wrap(Byte[(arrayList.size() * 17)])
        wrap.order(ByteOrder.BIG_ENDIAN)
        for (SLInventoryEntry sLInventoryEntry2 : arrayList) {
            wrap.put((Byte) (sLInventoryEntry2.isFolder ? SLAssetType.AT_CATEGORY.getTypeCode() : sLInventoryEntry2.assetType))
            wrap.putLong(sLInventoryEntry2.uuid.getMostSignificantBits())
            wrap.putLong(sLInventoryEntry2.uuid.getLeastSignificantBits())
        }
        wrap.position(0)
        improvedInstantMessage.MessageBlock_Field.BinaryBucket = wrap.array()
        improvedInstantMessage.isReliable = true
        SendMessage(improvedInstantMessage)
        HandleChatEvent(ChatterID.getUserChatterID(this.agentUUID, uuid), SLChatInventoryItemOfferedByYouEvent(this.agentUUID, sLInventoryEntry.name), false)
    }

     fun processMyAttachmentUpdate(sLObjectInfo: SLObjectInfo) {
        if (!(sLObjectInfo == null || sLObjectInfo.nameKnown || (sLObjectInfo.isDead ^ 1) == 0)) {
            RequestObjectName(sLObjectInfo)
        }
        getModules().avatarAppearance.UpdateMyAttachments()
    }

    fun sendTypingNotify(uuid: UUID, z: Boolean) {
        SendInstantMessage(uuid, "", z ? 41 : 42)
    }
}
