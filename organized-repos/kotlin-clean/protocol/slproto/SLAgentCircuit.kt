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
        protected Object getEventToFire() {
            return null
        }

        protected Unit onActualFire() {
            SLAgentCircuit.this.notifyObjectPropertiesChange()
        }
    }
    private List<ImprovedInstantMessage> pendingGroupMessages = LinkedList()
    private Long regionHandle = 0
    private val regionID: UUID = null
    private val regionName: String = null
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
        IntArray iArr = Int[CapsEventType.values().length]
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
        IntArray iArr = Int[ChatterType.values().length]
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

    private Unit DoAgentPause() {
        this.agentPaused = true
        Debug.Log("AgentPause: Sending agentPause with ID = " + this.lastPauseId)
        SLMessage agentPause = AgentPause()
        agentPause.AgentData_Field.AgentID = this.circuitInfo.agentID
        agentPause.AgentData_Field.SessionID = this.circuitInfo.sessionID
        agentPause.AgentData_Field.SerialNum = this.lastPauseId
        agentPause.isReliable = true
        SendMessage(agentPause)
        this.lastPauseId++
    }

    private Unit DoAgentResume() {
        this.agentPaused = false
        Debug.Log("AgentPause: Sending agentResume with ID = " + this.lastPauseId)
        SLMessage agentResume = AgentResume()
        agentResume.AgentData_Field.AgentID = this.circuitInfo.agentID
        agentResume.AgentData_Field.SessionID = this.circuitInfo.sessionID
        agentResume.AgentData_Field.SerialNum = this.lastPauseId
        agentResume.isReliable = true
        SendMessage(agentResume)
        this.lastPauseId++
    }

    private Unit HandleCapsEvent(CapsEvent capsEvent) {
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

    private Unit HandleChatterBoxInvitation(LLSDNode lLSDNode) {
        try {
            Debug.Log("ChatterBoxInvitation: event = " + lLSDNode.serializeToXML())
        } catch (IOException e) {
            e.printStackTrace()
        }
        try {
            UUID fromString = UUID.fromString(lLSDNode.byKey("session_id").asString())
            AvatarGroupList avatarGroupList = this.userManager.getChatterList().getGroupManager().getAvatarGroupList()
            AvatarGroupEntry avatarGroupEntry = avatarGroupList != null ? (AvatarGroupEntry) avatarGroupList.Groups.get(fromString) : null
            LLSDNode byKey = lLSDNode.byKey("instantmessage").byKey("message_params")
            UUID asUUID = byKey.keyExists("from_id") ? byKey.byKey("from_id").asUUID() : null
            UUID asUUID2 = byKey.byKey("to_id").asUUID()
            String asString = byKey.byKey("message").asString()
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

    private Unit HandleChatterBoxSessionStartReply(LLSDNode lLSDNode) {
        try {
            Debug.Log("ChatterBoxSessionStartReply: event = " + lLSDNode.serializeToXML())
        } catch (IOException e) {
            e.printStackTrace()
        }
        try {
            UUID asUUID = lLSDNode.byKey("session_id").asUUID()
            this.modules.voice.onGroupSessionReady(asUUID)
            synchronized (this.startedGroupSessions) {
                this.startedGroupSessions.add(asUUID)
                Iterator it = this.pendingGroupMessages.iterator()
                while (it.hasNext()) {
                    ImprovedInstantMessage improvedInstantMessage = (ImprovedInstantMessage) it.next()
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

    private Unit HandleChatterOnlineStatus(ChatterID chatterID, Boolean z) {
        if (this.userManager.isChatterActive(chatterID) && (chatterID instanceof ChatterIDUser)) {
            HandleChatEvent(chatterID, SLChatOnlineOfflineEvent(ChatMessageSourceUser(((ChatterIDUser) chatterID).getChatterUUID()), this.agentUUID, z), false)
        }
    }

    private Unit HandleEstablishAgentCommunication(LLSDNode lLSDNode) {
        if (this.teleportRequestSent) {
            try {
                Debug.Log("EstablishAgentCommunication: event = " + lLSDNode.serializeToXML())
            } catch (IOException e) {
                e.printStackTrace()
            }
            try {
                String asString = lLSDNode.byKey("sim-ip-and-port").asString()
                String asString2 = lLSDNode.byKey("seed-capability").asString()
                UUID asUUID = lLSDNode.byKey("agent-id").asUUID()
                Array<String> split = asString.split(":")
                this.gridConn.addTempCircuit(SLAuthReply(this.authReply, true, true, asUUID, split[0], Integer.parseInt(split[1]), asString2))
            } catch (Exception e2) {
                e2.printStackTrace()
            }
        }
    }

    private Unit HandleGroupNotice(ImprovedInstantMessage improvedInstantMessage, ChatMessageSource chatMessageSource) {
        ByteBuffer wrap = ByteBuffer.wrap(improvedInstantMessage.MessageBlock_Field.BinaryBucket)
        if (wrap.limit() >= 18) {
            wrap.order(ByteOrder.BIG_ENDIAN)
            Byte b = wrap.get()
            Byte b2 = wrap.get()
            UUID uuid = UUID(wrap.getLong(), wrap.getLong())
            String str = ""
            if (b != (Byte) 0) {
                ByteArray bArr = Byte[wrap.remaining()]
                wrap.get(bArr)
                str = SLMessage.stringFromVariableOEM(bArr)
            }
            Debug.Log("HandleGroupNotice: group UUID = " + uuid.toString())
            ChatterID groupChatterID = ChatterID.getGroupChatterID(this.agentUUID, uuid)
            Boolean equal = Objects.equal(chatMessageSource.getSourceUUID(), this.circuitInfo.agentID)
            String stringFromVariableUTF = SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message)
            Int indexOf = stringFromVariableUTF.indexOf(ErrorCode.CONTROLLER_GATT_NOTIFY_FAILED)
            if (indexOf >= 0) {
                String substring = stringFromVariableUTF.substring(0, indexOf)
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
    private Unit HandleIM(com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage r8, com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource r9) {
    private Unit HandleIM(com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage improvedInstantMessage, com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource chatMessageSource) {
        // Check if RLV controller should handle this IM
        SLModules modules = getModules()
        if (modules != null && modules.rlvController.onIncomingIM(improvedInstantMessage)) {
            return; // RLV handled it
        }
        
        Byte dialogType = improvedInstantMessage.MessageBlock_Field.Dialog
        
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
    
    private Unit handlePersonalMessage(ImprovedInstantMessage improvedInstantMessage, ChatMessageSource chatMessageSource) {
        SLChatTextEvent chatEvent = SLChatTextEvent(chatMessageSource, this.agentUUID, improvedInstantMessage, null)
        ChatterID defaultChatter = chatMessageSource.getDefaultChatter(this.agentUUID)
        Boolean isChatterActive = this.userManager.isChatterActive(defaultChatter)
        
        HandleChatEvent(defaultChatter, chatEvent, true)
        
        // Auto-response logic
        if (!this.userManager.isChatterMuted(defaultChatter) &&
            improvedInstantMessage.MessageBlock_Field.Dialog != 20 && // Not MessageFromAgent
            improvedInstantMessage.MessageBlock_Field.Offline == 0 &&
            improvedInstantMessage.MessageBlock_Field.Message.length > 0 &&
            !isChatterActive &&
            defaultChatter instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser) {
            
            String autoResponse = SLGridConnection.getAutoresponse()
            if (!com.google.common.base.Strings.isNullOrEmpty(autoResponse)) {
                com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser userChatter = 
                    (com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser) defaultChatter
                SendInstantMessage(userChatter.getChatterUUID(), autoResponse, (Byte) 20)
            }
        }
    }
    
    private Unit handleSystemMessage(ImprovedInstantMessage improvedInstantMessage) {
        SLChatSystemMessageEvent systemEvent = SLChatSystemMessageEvent(
            com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown.getInstance(),
            this.agentUUID,
            SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message)
        )
        HandleChatEvent(this.localChatterID, systemEvent, true)
    }
    
    private Unit handleGroupInvitation(ImprovedInstantMessage improvedInstantMessage, ChatMessageSource chatMessageSource) {
        SLChatGroupInvitationEvent invitationEvent = SLChatGroupInvitationEvent(
            chatMessageSource, this.agentUUID, improvedInstantMessage
        )
        HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), invitationEvent, true)
    }
    
    private Unit handleInventoryOffered(ImprovedInstantMessage improvedInstantMessage, ChatMessageSource chatMessageSource) {
        SLChatInventoryItemOfferedEvent inventoryEvent = SLChatInventoryItemOfferedEvent(
            chatMessageSource, this.agentUUID, improvedInstantMessage
        )
        HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), inventoryEvent, true)
    }
    
    private Unit handleInventoryOfferedFromObject(ImprovedInstantMessage improvedInstantMessage) {
        ChatMessageSource objectSource = com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject(
            improvedInstantMessage.AgentData_Field.AgentID,
            SLMessage.stringFromVariableOEM(improvedInstantMessage.MessageBlock_Field.FromAgentName)
        )
        
        SLChatInventoryItemOfferedEvent inventoryEvent = SLChatInventoryItemOfferedEvent(
            objectSource, this.agentUUID, improvedInstantMessage
        )
        HandleChatEvent(this.localChatterID, inventoryEvent, true)
    }
    
    private Unit handleObjectMessage(ImprovedInstantMessage improvedInstantMessage, ChatMessageSource chatMessageSource) {
        SLChatTextEvent chatEvent = SLChatTextEvent(
            chatMessageSource, this.agentUUID, improvedInstantMessage, null
        )
        HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), chatEvent, true)
    }
    
    private Unit handleTeleportLure(ImprovedInstantMessage improvedInstantMessage, ChatMessageSource chatMessageSource) {
        SLModules modules = getModules()
        if (chatMessageSource.getSourceType() == ChatMessageSource.ChatMessageSourceType.User) {
            UUID sourceUUID = chatMessageSource.getSourceUUID()
            if (modules != null) {
                if (modules.rlvController.autoAcceptTeleport(sourceUUID)) {
                    TeleportToLure(improvedInstantMessage.MessageBlock_Field.ID)
                    return
                } else if (!modules.rlvController.canTeleportToLure(sourceUUID)) {
                    return
                }
            }
        }
        
        SLChatLureEvent lureEvent = SLChatLureEvent(
            chatMessageSource, this.agentUUID, improvedInstantMessage
        )
        HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), lureEvent, true)
    }
    
    private Unit handleLureRequest(ImprovedInstantMessage improvedInstantMessage, ChatMessageSource chatMessageSource) {
        SLModules modules = getModules()
        if (chatMessageSource.getSourceType() == ChatMessageSource.ChatMessageSourceType.User && modules != null) {
            if (!modules.rlvController.canTeleportToLure(chatMessageSource.getSourceUUID())) {
                return
            }
        }
        
        SLChatLureRequestEvent lureRequestEvent = SLChatLureRequestEvent(
            chatMessageSource, this.agentUUID, improvedInstantMessage
        )
        HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), lureRequestEvent, true)
    }
    
    private Unit handleFriendshipOffered(ImprovedInstantMessage improvedInstantMessage, ChatMessageSource chatMessageSource) {
        SLChatFriendshipOfferedEvent friendshipEvent = SLChatFriendshipOfferedEvent(
            chatMessageSource, this.agentUUID, improvedInstantMessage
        )
        HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), friendshipEvent, true)
    }
    
    private Unit handleFriendshipResult(ImprovedInstantMessage improvedInstantMessage, ChatMessageSource chatMessageSource) {
        SLChatFriendshipResultEvent friendshipResultEvent = SLChatFriendshipResultEvent(
            chatMessageSource, this.agentUUID, improvedInstantMessage
        )
        HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), friendshipResultEvent, true)
        
        // If friendship accepted, add to friend list and request online notification
        if (improvedInstantMessage.MessageBlock_Field.Dialog == 39) { // FriendshipAccepted
            if (chatMessageSource.getSourceType() == ChatMessageSource.ChatMessageSourceType.User) {
                UUID sourceUUID = chatMessageSource.getSourceUUID()
                if (sourceUUID != null) {
                    this.userManager.getChatterList().getFriendManager().addFriend(sourceUUID)
                    SendGenericMessage("requestonlinenotification", Array<String>{sourceUUID.toString()})
                }
            }
        }
    }
    }

    private Unit HandleSessionIM(ImprovedInstantMessage improvedInstantMessage, ChatMessageSource chatMessageSource) {
        HandleChatEvent(ChatterID.getGroupChatterID(this.agentUUID, improvedInstantMessage.MessageBlock_Field.ID), SLChatTextEvent(chatMessageSource, this.agentUUID, improvedInstantMessage, null), true)
    }

    private Unit HandleTeleportFailed(LLSDNode lLSDNode) {
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

    private Unit HandleTeleportFinish(LLSDNode lLSDNode) {
        try {
            Debug.Log("TeleportFinish: event = " + lLSDNode.serializeToXML())
        } catch (IOException e) {
            e.printStackTrace()
        }
        if (this.teleportRequestSent) {
            this.teleportRequestSent = false
            try {
                LLSDNode byIndex = lLSDNode.byKey("Info").byIndex(0)
                String asString = byIndex.byKey("SeedCapability").asString()
                ByteArray asBinary = byIndex.byKey("SimIP").asBinary()
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

    private Unit HandleTypingNotification(ChatMessageSource chatMessageSource, Boolean z) {
        if (chatMessageSource instanceof ChatMessageSourceUser) {
            UUID sourceUUID = chatMessageSource.getSourceUUID()
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

    private Unit ProcessObjectSelection() {
        if (getNeedObjectNames() && (this.doingObjectSelection ^ 1) != 0) {
            SLMessage sLMessage
            SLMessage sLMessage2 = null
            for (SLObjectInfo sLObjectInfo : this.forceNeedObjectNames.values()) {
                if (sLMessage2 == null) {
                    sLMessage2 = ObjectSelect()
                    sLMessage2.AgentData_Field.AgentID = this.circuitInfo.agentID
                    sLMessage2.AgentData_Field.SessionID = this.circuitInfo.sessionID
                }
                if (sLMessage2.ObjectData_Fields.size() > 16) {
                    break
                }
                ObjectData objectData = ObjectData()
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
                    ObjectData objectData2 = ObjectData()
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

    private Unit ProcessObjectSelectionTimeout() {
        for (SLObjectInfo sLObjectInfo : this.objectNamesRequested.values()) {
            SLObjectInfo sLObjectInfo2 = (SLObjectInfo) this.gridConn.parcelInfo.objectNamesQueue.remove(sLObjectInfo.getId())
            if (sLObjectInfo2 != null) {
                this.gridConn.parcelInfo.objectNamesQueue.put(sLObjectInfo2.getId(), sLObjectInfo2)
            }
            this.forceNeedObjectNames.remove(sLObjectInfo.getId())
        }
        this.objectNamesRequested.clear()
    }

    private Unit SendAgentFOV() {
        SLMessage agentFOV = AgentFOV()
        agentFOV.AgentData_Field.AgentID = this.circuitInfo.agentID
        agentFOV.AgentData_Field.SessionID = this.circuitInfo.sessionID
        agentFOV.AgentData_Field.CircuitCode = this.circuitInfo.circuitCode
        agentFOV.FOVBlock_Field.GenCounter = 0
        agentFOV.FOVBlock_Field.VerticalAngle = 3.0543263f
        agentFOV.isReliable = true
        SendMessage(agentFOV)
    }

    private Unit SendCompleteAgentMovement() {
        SLMessage completeAgentMovement = CompleteAgentMovement()
        completeAgentMovement.AgentData_Field.CircuitCode = this.circuitInfo.circuitCode
        completeAgentMovement.AgentData_Field.AgentID = this.circuitInfo.agentID
        completeAgentMovement.AgentData_Field.SessionID = this.circuitInfo.sessionID
        completeAgentMovement.isReliable = true
        SendMessage(completeAgentMovement)
    }

    private Unit SendEstateOwnerMessage(String str, Array<String> strArr) {
        SLMessage estateOwnerMessage = EstateOwnerMessage()
        estateOwnerMessage.AgentData_Field.AgentID = this.circuitInfo.agentID
        estateOwnerMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID
        estateOwnerMessage.AgentData_Field.TransactionID = UUID(0, 0)
        estateOwnerMessage.MethodData_Field.Method = SLMessage.stringToVariableOEM(str)
        estateOwnerMessage.MethodData_Field.Invoice = UUID(0, 0)
        for (String str2 : strArr) {
            ParamList paramList = ParamList()
            paramList.Parameter = SLMessage.stringToVariableOEM(str2)
            estateOwnerMessage.ParamList_Fields.add(paramList)
        }
        estateOwnerMessage.isReliable = true
        SendMessage(estateOwnerMessage)
    }

    private Unit SendGroupSessionStart(UUID uuid) {
        SLMessage improvedInstantMessage = ImprovedInstantMessage()
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
        improvedInstantMessage.MessageBlock_Field.BinaryBucket = ByteArray(1)
        improvedInstantMessage.isReliable = true
        SendMessage(improvedInstantMessage)
    }

    private Boolean SendInstantMessage(UUID uuid, String str, Int i) {
        if (!getModules().rlvController.canSendIM(uuid)) {
            return false
        }
        SLMessage improvedInstantMessage = ImprovedInstantMessage()
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
        improvedInstantMessage.MessageBlock_Field.BinaryBucket = ByteArray(0)
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

    private Unit SendRetrieveInstantMessages() {
        SLMessage retrieveInstantMessages = RetrieveInstantMessages()
        retrieveInstantMessages.AgentData_Field.AgentID = this.circuitInfo.agentID
        retrieveInstantMessages.AgentData_Field.SessionID = this.circuitInfo.sessionID
        retrieveInstantMessages.isReliable = true
        SendMessage(retrieveInstantMessages)
    }

    private UUID getActiveGroupID() {
        return this.modules != null ? this.modules.groupManager.getActiveGroupID() : null
    }

    private Boolean getNeedObjectNames() {
        if (this.forceNeedObjectNames != null && !this.forceNeedObjectNames.isEmpty()) {
            return true
        }
        return this.modules != null ? this.modules.drawDistance.isObjectSelectEnabled() : false
    }

    private Boolean isEventMuted(ChatterID chatterID, SLChatEvent sLChatEvent) {
        if (this.modules != null) {
            SLMuteList sLMuteList = this.modules.muteList
            ChatMessageSource source = sLChatEvent.getSource()
            if (source.getSourceType() == ChatMessageSourceType.User) {
                if (sLMuteList.isMuted(source.getSourceUUID(), MuteType.AGENT)) {
                    return true
                }
            } else if (source.getSourceType() == ChatMessageSourceType.Object) {
                UUID sourceUUID = source.getSourceUUID()
                if (sourceUUID != null && !sourceUUID.equals(UUIDPool.ZeroUUID) && sLMuteList.isMuted(sourceUUID, MuteType.OBJECT)) {
                    return true
                }
                String sourceName = source.getSourceName(this.userManager)
                if (sourceName != null && sLMuteList.isMutedByName(sourceName)) {
                    return true
                }
            }
            if (chatterID instanceof ChatterIDGroup) {
                UUID chatterUUID = ((ChatterIDGroup) chatterID).getChatterUUID()
                if (!chatterUUID.equals(UUIDPool.ZeroUUID) && sLMuteList.isMuted(chatterUUID, MuteType.GROUP)) {
                    return true
                }
            }
        }
        return false
    }

    private Unit notifyObjectPropertiesChange() {
        if (this.userManager != null) {
            this.userManager.getObjectsManager().requestObjectListUpdate()
        }
    }

    private Unit processMyAvatarUpdate(SLObjectAvatarInfo sLObjectAvatarInfo) {
        if (this.modules != null) {
            this.modules.avatarControl.setAgentPosition(sLObjectAvatarInfo.getAbsolutePosition(), sLObjectAvatarInfo.getObjectCoords().get(2))
        }
    }

    fun AcceptFriendship(UUID uuid, UUID uuid2) {
        UUID uuid3 = null
        this.userManager.getChatterList().getFriendManager().addFriend(uuid)
        SLMessage acceptFriendship = AcceptFriendship()
        acceptFriendship.AgentData_Field.AgentID = this.circuitInfo.agentID
        acceptFriendship.AgentData_Field.SessionID = this.circuitInfo.sessionID
        if (this.modules != null) {
            uuid3 = this.modules.inventory.getCallingCardsFolderUUID()
        }
        FolderData folderData = FolderData()
        if (uuid3 == null) {
            uuid3 = UUIDPool.ZeroUUID
        }
        folderData.FolderID = uuid3
        acceptFriendship.FolderData_Fields.add(folderData)
        acceptFriendship.TransactionBlock_Field.TransactionID = uuid2
        acceptFriendship.isReliable = true
        SendMessage(acceptFriendship)
    }

    fun AcceptInventoryOffer(Int i, Boolean z, UUID uuid, UUID uuid2, UUID uuid3) {
        SLMessage improvedInstantMessage = ImprovedInstantMessage()
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
            ByteBuffer wrap = ByteBuffer.wrap(ByteArray(16))
            wrap.order(ByteOrder.BIG_ENDIAN)
            wrap.putLong(uuid3.getMostSignificantBits())
            wrap.putLong(uuid3.getLeastSignificantBits())
            wrap.position(0)
            improvedInstantMessage.MessageBlock_Field.BinaryBucket = wrap.array()
        } else {
            improvedInstantMessage.MessageBlock_Field.BinaryBucket = ByteArray(0)
        }
        improvedInstantMessage.isReliable = true
        SendMessage(improvedInstantMessage)
    }

    fun AddFriend(UUID uuid, String str) {
        SLMessage improvedInstantMessage = ImprovedInstantMessage()
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
        improvedInstantMessage.MessageBlock_Field.BinaryBucket = ByteArray(0)
        improvedInstantMessage.isReliable = true
        SendMessage(improvedInstantMessage)
    }

    fun BuyObject(Int i, Byte b, Int i2) {
        UUID activeGroupID = getActiveGroupID()
        SLMessage objectBuy = ObjectBuy()
        objectBuy.AgentData_Field.AgentID = this.circuitInfo.agentID
        objectBuy.AgentData_Field.SessionID = this.circuitInfo.sessionID
        AgentData agentData = objectBuy.AgentData_Field
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

    fun DerezObject(Int i, EDeRezDestination eDeRezDestination) {
        UUID activeGroupID = getActiveGroupID()
        SLMessage deRezObject = DeRezObject()
        deRezObject.AgentData_Field.AgentID = this.circuitInfo.agentID
        deRezObject.AgentData_Field.SessionID = this.circuitInfo.sessionID
        AgentBlock agentBlock = deRezObject.AgentBlock_Field
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

    fun DoRequestPayPrice(UUID uuid) {
        SLObjectInfo sLObjectInfo = (SLObjectInfo) this.gridConn.parcelInfo.allObjectsNearby.get(uuid)
        if (sLObjectInfo == null) {
            return
        }
        if (sLObjectInfo.getPayInfo() != null) {
            this.eventBus.publish(SLObjectPayInfoEvent(sLObjectInfo))
            return
        }
        SLMessage requestPayPrice = RequestPayPrice()
        requestPayPrice.ObjectData_Field.ObjectID = uuid
        requestPayPrice.isReliable = true
        SendMessage(requestPayPrice)
    }

    fun GenerateChatMoneyEvent(UUID uuid, Int i, Int i2) {
        HandleChatEvent(uuid != null ? ChatterID.getUserChatterID(this.agentUUID, uuid) : this.localChatterID, SLChatBalanceChangedEvent(uuid != null ? ChatMessageSourceUser(uuid) : ChatMessageSourceUnknown.getInstance(), this.agentUUID, true, i, i2), true)
        if (this.modules != null) {
            this.modules.financialInfo.RecordChatEvent(uuid, i, i2)
        }
    }

    fun HandleAgentMovementComplete(AgentMovementComplete agentMovementComplete) {
        this.regionHandle = agentMovementComplete.Data_Field.RegionHandle
        this.modules.avatarControl.setAgentPosition(agentMovementComplete.Data_Field.Position, null)
        Debug.Printf("Got agentPosition: %s", this.modules.avatarControl.getAgentPosition().getImmutablePosition())
        SendAgentFOV()
        this.modules.avatarAppearance.SendAgentWearablesRequest()
        SendRetrieveInstantMessages()
        this.modules.avatarControl.setEnableAgentUpdates(true)
    }

    fun HandleAlertMessage(AlertMessage alertMessage) {
        HandleChatEvent(this.localChatterID, SLChatSystemMessageEvent(ChatMessageSourceUnknown.getInstance(), this.agentUUID, SLMessage.stringFromVariableOEM(alertMessage.AlertData_Field.Message)), true)
    }

    fun HandleAvatarAnimation(AvatarAnimation avatarAnimation) {
        SLParcelInfo sLParcelInfo = this.gridConn.parcelInfo
        if (sLParcelInfo != null && this.modules != null) {
            sLParcelInfo.ApplyAvatarAnimation(avatarAnimation, this.modules.avatarControl)
        }
    }

    fun HandleAvatarAppearance(AvatarAppearance avatarAppearance) {
        Debug.Log("Got AvatarAppearance, ID = " + avatarAppearance.Sender_Field.ID.toString() + " isTrial = " + avatarAppearance.Sender_Field.IsTrial + ", our ID = " + this.circuitInfo.agentID.toString())
        if (avatarAppearance.Sender_Field.ID.equals(this.circuitInfo.agentID) && this.modules != null) {
            this.modules.avatarAppearance.HandleAvatarAppearance(avatarAppearance)
        }
        SLParcelInfo sLParcelInfo = this.gridConn.parcelInfo
        if (sLParcelInfo != null) {
            sLParcelInfo.ApplyAvatarAppearance(avatarAppearance)
        }
    }

    fun HandleAvatarInterestsReply(AvatarInterestsReply avatarInterestsReply) {
        Debug.Log("got AvatarInterestsReply: wantToText = " + SLMessage.stringFromVariableOEM(avatarInterestsReply.PropertiesData_Field.WantToText))
        Debug.Log("got AvatarInterestsReply: skillText = " + SLMessage.stringFromVariableOEM(avatarInterestsReply.PropertiesData_Field.SkillsText))
    }

    fun HandleChatEvent(ChatterID chatterID, SLChatEvent sLChatEvent, Boolean z) {
        if (!isEventMuted(chatterID, sLChatEvent)) {
            this.userManager.getChatterList().getActiveChattersManager().HandleChatEvent(chatterID, sLChatEvent, z)
        }
    }

    fun HandleChatFromSimulator(ChatFromSimulator chatFromSimulator) {
        SLModules modules = getModules()
        if (modules == null || !modules.rlvController.onIncomingChat(chatFromSimulator)) {
            UUID uuid = chatFromSimulator.ChatData_Field.SourceID
            String stringFromVariableOEM = SLMessage.stringFromVariableOEM(chatFromSimulator.ChatData_Field.FromName)
            String stringFromVariableUTF = SLMessage.stringFromVariableUTF(chatFromSimulator.ChatData_Field.Message)
            if (chatFromSimulator.ChatData_Field.ChatType != 8 || chatFromSimulator.ChatData_Field.SourceType != 2 || !stringFromVariableOEM.startsWith("#Firestorm LSL Bridge") || !stringFromVariableUTF.startsWith("<bridgeURL>")) {
                if ((chatFromSimulator.ChatData_Field.SourceType != 1 || modules == null || modules.rlvController.canRecvChat(stringFromVariableUTF, uuid)) && chatFromSimulator.ChatData_Field.Audible == 1) {
                    Int i = chatFromSimulator.ChatData_Field.ChatType
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

    fun HandleImprovedInstantMessage(ImprovedInstantMessage improvedInstantMessage) {
        ChatMessageSource chatMessageSourceObject
        Int i = improvedInstantMessage.MessageBlock_Field.Dialog
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

    fun HandleImprovedTerseObjectUpdate(ImprovedTerseObjectUpdate improvedTerseObjectUpdate) {
        SLParcelInfo sLParcelInfo = this.gridConn.parcelInfo
        RequestMultipleObjects requestMultipleObjects = null
        for (ImprovedTerseObjectUpdate.ObjectData objectData : improvedTerseObjectUpdate.ObjectData_Fields) {
            SLObjectInfo sLObjectInfo
            Int localID = SLObjectInfo.getLocalID(objectData)
            UUID uuid = (UUID) sLParcelInfo.uuidsNearby.get(Integer.valueOf(localID))
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

    fun HandleKillObject(KillObject killObject) {
        SLParcelInfo sLParcelInfo = this.gridConn.parcelInfo
        Object obj2 = null
        Iterator it = killObject.ObjectData_Fields.iterator()
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

    fun HandleLayerData(LayerData layerData) {
        if (layerData.LayerID_Field.Type == 76) {
            SLParcelInfo sLParcelInfo = this.gridConn.parcelInfo
            if (sLParcelInfo != null) {
                sLParcelInfo.terrainData.ProcessLayerData(layerData.LayerDataData_Field.Data)
            }
        }
    }

    fun HandleLoadURL(LoadURL loadURL) {
        HandleChatEvent(this.localChatterID, SLChatTextEvent(ChatMessageSourceObject(loadURL.Data_Field.ObjectID, SLMessage.stringFromVariableOEM(loadURL.Data_Field.ObjectName)), this.agentUUID, loadURL), true)
    }

    fun HandleObjectProperties(ObjectProperties objectProperties) {
        Debug.Log("ObjectProperties: " + objectProperties.ObjectData_Fields.size() + " ObjectSelect replies. Reqd " + this.objectNamesRequested.size() + " obj, remains " + this.gridConn.parcelInfo.objectNamesQueue.size() + " objects.")
        for (ObjectProperties.ObjectData objectData : objectProperties.ObjectData_Fields) {
            SLObjectInfo sLObjectInfo = (SLObjectInfo) this.gridConn.parcelInfo.objectNamesQueue.remove(objectData.ObjectID)
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
                    UUID id = sLObjectInfo.getId()
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

    fun HandleObjectUpdate(ObjectUpdate objectUpdate) {
        SLParcelInfo sLParcelInfo = this.gridConn.parcelInfo
        Object obj = null
        Object obj2 = null
        for (ObjectUpdate.ObjectData objectData : objectUpdate.ObjectData_Fields) {
            if (objectData.PCode == 47 || objectData.PCode == 9) {
                SLObjectInfo sLObjectInfo = (SLObjectInfo) sLParcelInfo.allObjectsNearby.get(objectData.FullID)
                if (sLObjectInfo != null) {
                    Int i = sLObjectInfo.parentID
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
                        Int i2 = 1
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

    fun HandleObjectUpdateCached(ObjectUpdateCached objectUpdateCached) {
        SLMessage requestMultipleObjects = RequestMultipleObjects()
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

    fun HandleObjectUpdateCompressed(ObjectUpdateCompressed objectUpdateCompressed) {
        SLParcelInfo sLParcelInfo = this.gridConn.parcelInfo
        Object obj = null
        Object obj2 = null
        for (ObjectUpdateCompressed.ObjectData objectData : objectUpdateCompressed.ObjectData_Fields) {
            try {
                UUID uuid = (UUID) sLParcelInfo.uuidsNearby.get(Integer.valueOf(SLObjectInfo.getLocalID(objectData)))
                SLObjectInfo sLObjectInfo = uuid != null ? (SLObjectInfo) sLParcelInfo.allObjectsNearby.get(uuid) : null
                if (sLObjectInfo != null) {
                    Int i = sLObjectInfo.parentID
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

    fun HandleOfflineNotification(OfflineNotification offlineNotification) {
        List arrayList = ArrayList(offlineNotification.AgentBlock_Fields.size())
        for (OfflineNotification.AgentBlock agentBlock : offlineNotification.AgentBlock_Fields) {
            arrayList.add(agentBlock.AgentID)
        }
        this.userManager.getChatterList().getFriendManager().setUsersOnline(arrayList, false)
    }

    fun HandleOnlineNotification(OnlineNotification onlineNotification) {
        List arrayList = ArrayList(onlineNotification.AgentBlock_Fields.size())
        for (OnlineNotification.AgentBlock agentBlock : onlineNotification.AgentBlock_Fields) {
            arrayList.add(agentBlock.AgentID)
        }
        this.userManager.getChatterList().getFriendManager().setUsersOnline(arrayList, true)
    }

    fun HandlePayPriceReply(PayPriceReply payPriceReply) {
        SLObjectInfo sLObjectInfo = (SLObjectInfo) this.gridConn.parcelInfo.allObjectsNearby.get(payPriceReply.ObjectData_Field.ObjectID)
        if (sLObjectInfo != null) {
            Int i = payPriceReply.ObjectData_Field.DefaultPayPrice
            IntArray iArr = Int[payPriceReply.ButtonData_Fields.size()]
            Int i2 = 0
            while (true) {
                Int i3 = i2
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

    fun HandleRegionHandshake(RegionHandshake regionHandshake) {
        if (!this.authReply.isTemporary) {
            SLMessage regionHandshakeReply = RegionHandshakeReply()
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

    fun HandleScriptDialog(ScriptDialog scriptDialog) {
        Array<String> strArr
        Int i = 0
        if (scriptDialog.Buttons_Fields.size() > 0) {
            Array<String> strArr2 = String[scriptDialog.Buttons_Fields.size()]
            Int i2 = 0
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

    fun HandleSimulatorViewerTimeMessage(SimulatorViewerTimeMessage simulatorViewerTimeMessage) {
        if (!this.authReply.isTemporary && this.gridConn != null && this.gridConn.parcelInfo != null) {
            Float f = (simulatorViewerTimeMessage.TimeInfo_Field.SunPhase / 6.2831855f) + 0.25f
            this.gridConn.parcelInfo.setSunHour((Float) (((Double) f) - Math.floor((Double) f)))
        }
    }

    fun HandleTeleportFailed(TeleportFailed teleportFailed) {
        Debug.Log("TeleportFailed: reason = " + SLMessage.stringFromVariableOEM(teleportFailed.Info_Field.Reason))
        this.teleportRequestSent = false
        this.eventBus.publish(SLTeleportResultEvent(false, SLMessage.stringFromVariableOEM(teleportFailed.Info_Field.Reason)))
    }

    fun HandleTeleportLocal(TeleportLocal teleportLocal) {
        this.teleportRequestSent = false
        this.eventBus.publish(SLTeleportResultEvent(true, null))
    }

    fun HandleTeleportProgress(TeleportProgress teleportProgress) {
        Debug.Log("Teleport progress: flags = " + teleportProgress.Info_Field.TeleportFlags + ", progress = " + SLMessage.stringFromVariableOEM(teleportProgress.Info_Field.Message))
    }

    fun HandleTeleportStart(TeleportStart teleportStart) {
        Debug.Log("TeleportStart: flags = " + teleportStart.Info_Field.TeleportFlags)
    }

    fun OfferInventoryItem(UUID uuid, SLInventoryEntry sLInventoryEntry) {
        this.userManager.getInventoryManager().getExecutor().execute(com.lumiyaviewer.lumiya.slproto.-$Lambda$K1xWCpEh0d4XNuVVYxGUJwEFRxU.AnonymousClass1(this, sLInventoryEntry, uuid))
    }

    fun OfferTeleport(UUID uuid, String str) {
        SLMessage startLure = StartLure()
        startLure.AgentData_Field.AgentID = this.circuitInfo.agentID
        startLure.AgentData_Field.SessionID = this.circuitInfo.sessionID
        startLure.Info_Field.Message = SLMessage.stringToVariableUTF(str)
        TargetData targetData = TargetData()
        targetData.TargetID = uuid
        startLure.TargetData_Fields.add(targetData)
        startLure.isReliable = true
        SendMessage(startLure)
    }

    fun OnCapsEvent(CapsEvent capsEvent) {
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
            Long currentTimeMillis = System.currentTimeMillis()
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
                CapsEvent capsEvent = (CapsEvent) this.capsEventQueue.poll()
                if (capsEvent == null) {
                    break
                }
                HandleCapsEvent(capsEvent)
            } catch (Exception e) {
            }
        }
        ProcessIdle()
    }

    fun RemoveFriend(UUID uuid) {
        SLMessage terminateFriendship = TerminateFriendship()
        terminateFriendship.AgentData_Field.AgentID = this.circuitInfo.agentID
        terminateFriendship.AgentData_Field.SessionID = this.circuitInfo.sessionID
        terminateFriendship.ExBlock_Field.OtherID = uuid
        terminateFriendship.isReliable = true
        SendMessage(terminateFriendship)
        this.userManager.getChatterList().getFriendManager().removeFriend(uuid)
    }

    Unit RequestObjectName(SLObjectInfo sLObjectInfo) {
        if (!(sLObjectInfo.getId() == null || this.objectNamesRequested.containsKey(sLObjectInfo.getId()) || (this.forceNeedObjectNames.containsKey(sLObjectInfo.getId()) ^ 1) == 0)) {
            this.forceNeedObjectNames.put(sLObjectInfo.getId(), sLObjectInfo)
        }
        TryWakeUp()
    }

    fun RequestTeleport(UUID uuid, String str) {
        SendInstantMessage(uuid, str, 26)
    }

    public Boolean RestartRegion(Int i) {
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
        UUID groupID = com.lumiyaviewer.lumiya.utils.UUIDPool.ZeroUUID
        UUID parcelOwnerID = null
        
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
            UUID folderID = inventoryEntry.parentUUID
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

    fun SendChatMessage(ChatterID chatterID, String str) {
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

    fun SendGenericMessage(String str, Array<String> strArr) {
        SLMessage genericMessage = GenericMessage()
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

    fun SendGroupInstantMessage(UUID uuid, String str) {
        SLMessage improvedInstantMessage = ImprovedInstantMessage()
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
        improvedInstantMessage.MessageBlock_Field.BinaryBucket = ByteArray(1)
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

    public Boolean SendInstantMessage(UUID uuid, String str) {
        return SendInstantMessage(uuid, str, 0)
    }

    fun SendLocalChatMessage(String str) {
        Int i = 0
        if (str.startsWith("/")) {
            Int i2 = 1
            Int i3 = 0
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
            SLMessage chatFromViewer = ChatFromViewer()
            chatFromViewer.AgentData_Field.AgentID = this.circuitInfo.agentID
            chatFromViewer.AgentData_Field.SessionID = this.circuitInfo.sessionID
            chatFromViewer.ChatData_Field.Channel = i
            chatFromViewer.ChatData_Field.Type = 1
            chatFromViewer.ChatData_Field.Message = SLMessage.stringToVariableUTF(str)
            chatFromViewer.isReliable = true
            SendMessage(chatFromViewer)
        }
    }

    Unit SendLogoutRequest() {
        Debug.Log("Logout: Sending logout request.")
        this.modules.avatarControl.setEnableAgentUpdates(false)
        SLMessage logoutRequest = LogoutRequest()
        logoutRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        logoutRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        logoutRequest.isReliable = true
        logoutRequest.setEventListener(SLMessageEventListener() {
            fun onMessageAcknowledged(SLMessage sLMessage) {
                Debug.Log("Logout: Logout request acknowledged.")
                SLAgentCircuit.this.gridConn.processDisconnect(true, "Logged out.")
            }

            fun onMessageTimeout(SLMessage sLMessage) {
                Debug.Log("Logout: LogoutRequest timed out!")
                SLAgentCircuit.this.gridConn.processDisconnect(false, "Logout request has timed out.")
            }
        SendMessage(logoutRequest)
    }

    fun SendScriptDialogReply(UUID uuid, Int i, Int i2, String str) {
        SLMessage scriptDialogReply = ScriptDialogReply()
        scriptDialogReply.AgentData_Field.AgentID = this.circuitInfo.agentID
        scriptDialogReply.AgentData_Field.SessionID = this.circuitInfo.sessionID
        scriptDialogReply.isReliable = true
        scriptDialogReply.Data_Field.ObjectID = uuid
        scriptDialogReply.Data_Field.ChatChannel = i
        scriptDialogReply.Data_Field.ButtonIndex = i2
        scriptDialogReply.Data_Field.ButtonLabel = SLMessage.stringToVariableUTF(str)
        SendMessage(scriptDialogReply)
    }

    Unit SendUseCode() {
        Debug.Printf("Using circuitCode: %d", Integer.valueOf(this.circuitInfo.circuitCode))
        SLMessage useCircuitCode = UseCircuitCode()
        useCircuitCode.CircuitCode_Field.Code = this.circuitInfo.circuitCode
        useCircuitCode.CircuitCode_Field.SessionID = this.circuitInfo.sessionID
        useCircuitCode.CircuitCode_Field.ID = this.circuitInfo.agentID
        useCircuitCode.isReliable = true
        useCircuitCode.setEventListener(SLMessageEventListener() {
            fun onMessageAcknowledged(SLMessage sLMessage) {
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

            fun onMessageTimeout(SLMessage sLMessage) {
                if (SLAgentCircuit.this.authReply.fromTeleport) {
                    SLAgentCircuit.this.eventBus.publish(SLTeleportResultEvent(false, "Timed out while connecting to the simulator."))
                } else {
                    SLAgentCircuit.this.gridConn.notifyLoginError("Timed out while connecting to the simulator.")
                }
            }
        SendMessage(useCircuitCode)
    }

    fun StartGroupSessionForVoice(UUID uuid) {
        Object obj = null
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

    fun TeleportToGlobalPosition(LLVector3 lLVector3) {
        Int floor = (Int) Math.floor((Double) lLVector3.x)
        Int floor2 = (Int) Math.floor((Double) lLVector3.y)
        floor -= floor % 256
        Long j = ((Long) (floor2 - (floor2 % 256))) | (((Long) floor) << 32)
        LLVector3 lLVector32 = LLVector3(lLVector3.x % 256.0f, lLVector3.y % 256.0f, lLVector3.z)
        LLVector3 lLVector33 = LLVector3(lLVector32)
        lLVector33.x += 1.0f
        Debug.Printf("regionHandle = %s, globalPos = %s", Long.toHexString(j), lLVector3)
        this.teleportRequestSent = true
        SLMessage teleportLocationRequest = TeleportLocationRequest()
        teleportLocationRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        teleportLocationRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        teleportLocationRequest.Info_Field.RegionHandle = j
        teleportLocationRequest.Info_Field.Position = lLVector32
        teleportLocationRequest.Info_Field.LookAt = lLVector33
        teleportLocationRequest.isReliable = true
        teleportLocationRequest.setEventListener(SLMessageEventListener() {
            fun onMessageAcknowledged(SLMessage sLMessage) {
            }

            fun onMessageTimeout(SLMessage sLMessage) {
                SLAgentCircuit.this.eventBus.publish(SLTeleportResultEvent(false, "Teleport request has timed out."))
            }
        SendMessage(teleportLocationRequest)
    }

    fun TeleportToLandmarkAsset(UUID uuid) {
        if (getModules().rlvController.canTeleportToLandmark()) {
            this.teleportRequestSent = true
            SLMessage teleportLandmarkRequest = TeleportLandmarkRequest()
            teleportLandmarkRequest.Info_Field.AgentID = this.circuitInfo.agentID
            teleportLandmarkRequest.Info_Field.SessionID = this.circuitInfo.sessionID
            teleportLandmarkRequest.Info_Field.LandmarkID = uuid
            teleportLandmarkRequest.isReliable = true
            teleportLandmarkRequest.setEventListener(SLMessageEventListener() {
                fun onMessageAcknowledged(SLMessage sLMessage) {
                }

                fun onMessageTimeout(SLMessage sLMessage) {
                    SLAgentCircuit.this.eventBus.publish(SLTeleportResultEvent(false, "Teleport request has timed out."))
                }
            SendMessage(teleportLandmarkRequest)
        }
    }

    public Boolean TeleportToLocalPosition(LLVector3 lLVector3) {
        if (this.regionID == null) {
            return false
        }
        Debug.Printf("Teleport: localPos = %s, regionHandle = %d", lLVector3.toString(), Long.valueOf(this.regionHandle))
        this.teleportRequestSent = true
        SLMessage teleportLocationRequest = TeleportLocationRequest()
        teleportLocationRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        teleportLocationRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        teleportLocationRequest.Info_Field.RegionHandle = this.regionHandle
        teleportLocationRequest.Info_Field.Position = lLVector3
        teleportLocationRequest.Info_Field.LookAt = LLVector3(lLVector3)
        LLVector3 lLVector32 = teleportLocationRequest.Info_Field.LookAt
        lLVector32.x += 10.0f
        teleportLocationRequest.isReliable = true
        teleportLocationRequest.setEventListener(SLMessageEventListener() {
            fun onMessageAcknowledged(SLMessage sLMessage) {
            }

            fun onMessageTimeout(SLMessage sLMessage) {
                SLAgentCircuit.this.eventBus.publish(SLTeleportResultEvent(false, "Teleport request has timed out."))
            }
        SendMessage(teleportLocationRequest)
        return true
    }

    fun TeleportToLure(UUID uuid) {
        this.teleportRequestSent = true
        SLMessage teleportLureRequest = TeleportLureRequest()
        teleportLureRequest.Info_Field.AgentID = this.circuitInfo.agentID
        teleportLureRequest.Info_Field.SessionID = this.circuitInfo.sessionID
        teleportLureRequest.Info_Field.LureID = uuid
        teleportLureRequest.isReliable = true
        teleportLureRequest.setEventListener(SLMessageEventListener() {
            fun onMessageAcknowledged(SLMessage sLMessage) {
            }

            fun onMessageTimeout(SLMessage sLMessage) {
                SLAgentCircuit.this.eventBus.publish(SLTeleportResultEvent(false, "Teleport request has timed out."))
            }
        SendMessage(teleportLureRequest)
    }

    fun TeleportToRegion(Long j, Int i, Int i2, Int i3) {
        if (getModules().rlvController.canTeleportToLocation()) {
            Debug.Log("TeleportToRegion: regionHandle = " + Long.toHexString(j) + ", pos = (" + i + ", " + i2 + ", " + i3 + ")")
            this.teleportRequestSent = true
            SLMessage teleportLocationRequest = TeleportLocationRequest()
            teleportLocationRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
            teleportLocationRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
            teleportLocationRequest.Info_Field.RegionHandle = j
            teleportLocationRequest.Info_Field.Position = LLVector3((Float) i, (Float) i2, (Float) i3)
            teleportLocationRequest.Info_Field.LookAt = LLVector3(0.0f, 1.0f, 0.0f)
            teleportLocationRequest.isReliable = true
            teleportLocationRequest.setEventListener(SLMessageEventListener() {
                fun onMessageAcknowledged(SLMessage sLMessage) {
                }

                fun onMessageTimeout(SLMessage sLMessage) {
                    SLAgentCircuit.this.eventBus.publish(SLTeleportResultEvent(false, "Teleport request has timed out."))
                }
            SendMessage(teleportLocationRequest)
        }
    }

    fun TouchObject(Int i) {
        SLMessage objectGrab = ObjectGrab()
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

    fun TouchObjectFace(SLObjectInfo sLObjectInfo, Int i, Float f, Float f2, Float f3, Float f4, Float f5, Float f6, Float f7) {
        Debug.Printf("Touch: Object %d, face %d, pos (%f, %f, %f), uv (%f, %f)", Integer.valueOf(sLObjectInfo.localID), Integer.valueOf(i), Float.valueOf(f), Float.valueOf(f2), Float.valueOf(f3), Float.valueOf(f4), Float.valueOf(f5))
        SLMessage objectGrab = ObjectGrab()
        objectGrab.AgentData_Field.AgentID = this.circuitInfo.agentID
        objectGrab.AgentData_Field.SessionID = this.circuitInfo.sessionID
        objectGrab.ObjectData_Field.LocalID = sLObjectInfo.localID
        objectGrab.ObjectData_Field.GrabOffset = LLVector3()
        SurfaceInfo surfaceInfo = SurfaceInfo()
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

    public LLVector3d getAgentGlobalPosition() {
        if (this.modules == null) {
            return null
        }
        LLVector3 position = this.modules.avatarControl.getAgentPosition().getPosition()
        Int i = (Int) ((this.regionHandle >> 32) & 4294967295L)
        Int i2 = (Int) (this.regionHandle & 4294967295L)
        LLVector3d lLVector3d = LLVector3d()
        lLVector3d.x = ((Double) i) + ((Double) position.x)
        lLVector3d.y = ((Double) i2) + ((Double) position.y)
        lLVector3d.z = (Double) position.z
        return lLVector3d
    }

    @SuppressLint({"DefaultLocale"})
    public String getAgentSLURL() {
        if (this.modules == null || !Objects.equal(this.authReply.loginURL, "https://login.agni.lindenlab.com/cgi-bin/login.cgi") || this.regionName == null) {
            return null
        }
        LLVector3 position = this.modules.avatarControl.getAgentPosition().getPosition()
        try {
            return String.format("http://maps.secondlife.com/secondlife/%s/%d/%d/%d", Array<Any>{URLEncoder.encode(this.regionName, "UTF-8"), Integer.valueOf((Int) position.x), Integer.valueOf((Int) position.y), Integer.valueOf((Int) position.z)})
        } catch (UnsupportedEncodingException e) {
            return null
        }
    }

    public UUID getAgentUUID() {
        return this.agentUUID
    }

    public SLCaps getCaps() {
        return this.caps
    }

    public Boolean getIsEstateManager() {
        return this.isEstateManager
    }

    public ChatterID getLocalChatterID() {
        return this.localChatterID
    }

    public SLModules getModules() {
        return this.modules
    }

    public SLObjectProfileData getObjectProfile(Int i) {
        SLObjectInfo objectInfo = this.gridConn.parcelInfo.getObjectInfo(i)
        if (objectInfo == null) {
            return null
        }
        SLObjectProfileData create = SLObjectProfileData.create(objectInfo)
        if (!(create.name().isPresent() || (objectInfo.isDead ^ 1) == 0)) {
            RequestObjectName(objectInfo)
        }
        return create
    }

    public String getRegionName() {
        return this.regionName
    }

    public UUID getSessionID() {
        return this.circuitInfo.sessionID
    }

    public Boolean isUserTyping(UUID uuid) {
        return Boolean.valueOf(this.typingUsers.contains(uuid))
    }

    /* renamed from: handleUserNameUpdate */
    /* synthetic */ Unit handleUserNameUpdate(UserName userName) {
        this.agentUserName.set(userName)
    }

    /* renamed from: handleInventoryItemOffer */
    /* synthetic */ Unit handleInventoryItemOffer(SLInventoryEntry sLInventoryEntry, UUID uuid) {
        Iterable<SLInventoryEntry> arrayList = ArrayList()
        arrayList.add(sLInventoryEntry)
        if (sLInventoryEntry.isFolder) {
            arrayList.addAll(this.modules.inventory.CollectGiveableItems(sLInventoryEntry))
        }
        SLMessage improvedInstantMessage = ImprovedInstantMessage()
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
        ByteBuffer wrap = ByteBuffer.wrap(Byte[(arrayList.size() * 17)])
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

    Unit processMyAttachmentUpdate(SLObjectInfo sLObjectInfo) {
        if (!(sLObjectInfo == null || sLObjectInfo.nameKnown || (sLObjectInfo.isDead ^ 1) == 0)) {
            RequestObjectName(sLObjectInfo)
        }
        getModules().avatarAppearance.UpdateMyAttachments()
    }

    fun sendTypingNotify(UUID uuid, Boolean z) {
        SendInstantMessage(uuid, "", z ? 41 : 42)
    }
}
