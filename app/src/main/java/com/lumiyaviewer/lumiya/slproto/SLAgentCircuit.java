package com.lumiyaviewer.lumiya.slproto;

import android.annotation.SuppressLint;
import com.google.common.base.Objects;
import com.google.common.logging.nano.Vr.VREvent.VrCore.ErrorCode;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GridConnectionService;
import com.lumiyaviewer.lumiya.dao.UserName;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.eventbus.EventRateLimiter;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEvent;
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType;
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.ICapsEventHandler;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatBalanceChangedEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatInventoryItemOfferedByGroupNoticeEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatInventoryItemOfferedByYouEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatLureRequestedEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatOnlineOfflineEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatScriptDialog;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatSystemMessageEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextBoxDialog;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLObjectPayInfoEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLRegionInfoChangedEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLTeleportResultEvent;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.messages.AcceptFriendship;
import com.lumiyaviewer.lumiya.slproto.messages.AcceptFriendship.FolderData;
import com.lumiyaviewer.lumiya.slproto.messages.AgentFOV;
import com.lumiyaviewer.lumiya.slproto.messages.AgentMovementComplete;
import com.lumiyaviewer.lumiya.slproto.messages.AgentPause;
import com.lumiyaviewer.lumiya.slproto.messages.AgentResume;
import com.lumiyaviewer.lumiya.slproto.messages.AlertMessage;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAnimation;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarInterestsReply;
import com.lumiyaviewer.lumiya.slproto.messages.ChatFromSimulator;
import com.lumiyaviewer.lumiya.slproto.messages.ChatFromViewer;
import com.lumiyaviewer.lumiya.slproto.messages.CompleteAgentMovement;
import com.lumiyaviewer.lumiya.slproto.messages.DeRezObject;
import com.lumiyaviewer.lumiya.slproto.messages.DeRezObject.AgentBlock;
import com.lumiyaviewer.lumiya.slproto.messages.EstateOwnerMessage;
import com.lumiyaviewer.lumiya.slproto.messages.EstateOwnerMessage.ParamList;
import com.lumiyaviewer.lumiya.slproto.messages.GenericMessage;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedTerseObjectUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.KillObject;
import com.lumiyaviewer.lumiya.slproto.messages.LayerData;
import com.lumiyaviewer.lumiya.slproto.messages.LoadURL;
import com.lumiyaviewer.lumiya.slproto.messages.LogoutRequest;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectBuy;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectBuy.AgentData;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectDeGrab;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectGrab;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectGrab.SurfaceInfo;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectProperties;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectSelect;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectSelect.ObjectData;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCached;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCompressed;
import com.lumiyaviewer.lumiya.slproto.messages.OfflineNotification;
import com.lumiyaviewer.lumiya.slproto.messages.OnlineNotification;
import com.lumiyaviewer.lumiya.slproto.messages.PayPriceReply;
import com.lumiyaviewer.lumiya.slproto.messages.PayPriceReply.ButtonData;
import com.lumiyaviewer.lumiya.slproto.messages.RegionHandshake;
import com.lumiyaviewer.lumiya.slproto.messages.RegionHandshakeReply;
import com.lumiyaviewer.lumiya.slproto.messages.RequestMultipleObjects;
import com.lumiyaviewer.lumiya.slproto.messages.RequestPayPrice;
import com.lumiyaviewer.lumiya.slproto.messages.RetrieveInstantMessages;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptDialog;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptDialog.Buttons;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptDialogReply;
import com.lumiyaviewer.lumiya.slproto.messages.SimulatorViewerTimeMessage;
import com.lumiyaviewer.lumiya.slproto.messages.StartLure;
import com.lumiyaviewer.lumiya.slproto.messages.StartLure.TargetData;
import com.lumiyaviewer.lumiya.slproto.messages.TeleportFailed;
import com.lumiyaviewer.lumiya.slproto.messages.TeleportLandmarkRequest;
import com.lumiyaviewer.lumiya.slproto.messages.TeleportLocal;
import com.lumiyaviewer.lumiya.slproto.messages.TeleportLocationRequest;
import com.lumiyaviewer.lumiya.slproto.messages.TeleportLureRequest;
import com.lumiyaviewer.lumiya.slproto.messages.TeleportProgress;
import com.lumiyaviewer.lumiya.slproto.messages.TeleportStart;
import com.lumiyaviewer.lumiya.slproto.messages.TerminateFriendship;
import com.lumiyaviewer.lumiya.slproto.messages.UseCircuitCode;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteType;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.SLMuteList;
import com.lumiyaviewer.lumiya.slproto.objects.PayInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import com.lumiyaviewer.lumiya.slproto.objects.UnsupportedObjectTypeException;
import com.lumiyaviewer.lumiya.slproto.types.EDeRezDestination;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUser;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SLAgentCircuit extends SLThreadingCircuit implements ICapsEventHandler {
    /* renamed from: -com-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues */
    private static final /* synthetic */ int[] f450-com-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues = null;
    /* renamed from: -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues */
    private static final /* synthetic */ int[] f451-com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues = null;
    private Subscription agentNameSubscription;
    private boolean agentPaused = false;
    @Nonnull
    private final UUID agentUUID;
    private final AtomicReference<UserName> agentUserName = new AtomicReference(null);
    private final SLCaps caps;
    private final ConcurrentLinkedQueue<CapsEvent> capsEventQueue = new ConcurrentLinkedQueue();
    private boolean doingObjectSelection = false;
    private final EventBus eventBus = EventBus.getInstance();
    private final Map<UUID, SLObjectInfo> forceNeedObjectNames = new ConcurrentHashMap();
    private boolean isEstateManager = false;
    private long lastObjectSelection = 0;
    private int lastPauseId = 0;
    private long lastVisibleActivities = 0;
    private final ChatterID localChatterID;
    private final SLModules modules;
    private final Map<UUID, SLObjectInfo> objectNamesRequested = new ConcurrentHashMap();
    private final EventRateLimiter objectPropertiesRateLimiter = new EventRateLimiter(this.eventBus, 500) {
        protected Object getEventToFire() {
            return null;
        }

        protected void onActualFire() {
            SLAgentCircuit.this.notifyObjectPropertiesChange();
        }
    };
    private List<ImprovedInstantMessage> pendingGroupMessages = new LinkedList();
    private long regionHandle = 0;
    private UUID regionID = null;
    private String regionName = null;
    private final Set<UUID> startedGroupSessions = new HashSet();
    private boolean teleportRequestSent = false;
    private final Set<UUID> typingUsers = Collections.synchronizedSet(new HashSet());
    private final UserManager userManager;

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues */
    private static /* synthetic */ int[] m38-getcom-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues() {
        if (f450-com-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues != null) {
            return f450-com-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues;
        }
        int[] iArr = new int[CapsEventType.values().length];
        try {
            iArr[CapsEventType.AgentGroupDataUpdate.ordinal()] = 9;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[CapsEventType.AvatarGroupsReply.ordinal()] = 10;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[CapsEventType.BulkUpdateInventory.ordinal()] = 11;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[CapsEventType.ChatterBoxInvitation.ordinal()] = 1;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[CapsEventType.ChatterBoxSessionStartReply.ordinal()] = 2;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[CapsEventType.EstablishAgentCommunication.ordinal()] = 3;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[CapsEventType.ParcelProperties.ordinal()] = 12;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[CapsEventType.TeleportFailed.ordinal()] = 4;
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[CapsEventType.TeleportFinish.ordinal()] = 5;
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[CapsEventType.UnknownCapsEvent.ordinal()] = 13;
        } catch (NoSuchFieldError e10) {
        }
        f450-com-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues = iArr;
        return iArr;
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues */
    private static /* synthetic */ int[] m39-getcom-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues() {
        if (f451-com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues != null) {
            return f451-com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues;
        }
        int[] iArr = new int[ChatterType.values().length];
        try {
            iArr[ChatterType.Group.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ChatterType.Local.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ChatterType.User.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        f451-com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues = iArr;
        return iArr;
    }

    public SLAgentCircuit(SLGridConnection sLGridConnection, SLCircuitInfo sLCircuitInfo, SLAuthReply sLAuthReply, SLCaps sLCaps, SLTempCircuit sLTempCircuit) throws IOException {
        super(sLGridConnection, sLCircuitInfo, sLAuthReply, sLTempCircuit);
        this.caps = sLCaps;
        this.agentUUID = sLCircuitInfo.agentID;
        this.localChatterID = ChatterID.getLocalChatterID(this.agentUUID);
        this.lastVisibleActivities = System.currentTimeMillis();
        this.userManager = UserManager.getUserManager(sLCircuitInfo.agentID);
        if (sLCaps == null || (sLAuthReply.isTemporary ^ 1) == 0) {
            this.modules = null;
        } else {
            this.modules = new SLModules(this, sLCaps, sLGridConnection);
        }
        if (!(sLAuthReply.isTemporary || this.userManager == null)) {
            this.userManager.setActiveAgentCircuit(this);
        }
        if (sLTempCircuit != null) {
            for (SLMessage Handle : sLTempCircuit.getPendingMessages()) {
                Handle.handleMessage(this);
            }
        }
    }

    private void DoAgentPause() {
        this.agentPaused = true;
        Debug.Log("AgentPause: Sending agentPause with ID = " + this.lastPauseId);
        SLMessage agentPause = new AgentPause();
        agentPause.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentPause.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        agentPause.AgentData_Field.SerialNum = this.lastPauseId;
        agentPause.isReliable = true;
        SendMessage(agentPause);
        this.lastPauseId++;
    }

    private void DoAgentResume() {
        this.agentPaused = false;
        Debug.Log("AgentPause: Sending agentResume with ID = " + this.lastPauseId);
        SLMessage agentResume = new AgentResume();
        agentResume.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentResume.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        agentResume.AgentData_Field.SerialNum = this.lastPauseId;
        agentResume.isReliable = true;
        SendMessage(agentResume);
        this.lastPauseId++;
    }

    private void HandleCapsEvent(CapsEvent capsEvent) {
        switch (m38-getcom-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues()[capsEvent.eventType.ordinal()]) {
            case 1:
                HandleChatterBoxInvitation(capsEvent.eventBody);
                return;
            case 2:
                HandleChatterBoxSessionStartReply(capsEvent.eventBody);
                return;
            case 3:
                HandleEstablishAgentCommunication(capsEvent.eventBody);
                return;
            case 4:
                HandleTeleportFailed(capsEvent.eventBody);
                return;
            case 5:
                HandleTeleportFinish(capsEvent.eventBody);
                return;
            default:
                DefaultEventQueueHandler(capsEvent.eventType, capsEvent.eventBody);
                return;
        }
    }

    private void HandleChatterBoxInvitation(LLSDNode lLSDNode) {
        try {
            Debug.Log("ChatterBoxInvitation: event = " + lLSDNode.serializeToXML());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            UUID fromString = UUID.fromString(lLSDNode.byKey("session_id").asString());
            AvatarGroupList avatarGroupList = this.userManager.getChatterList().getGroupManager().getAvatarGroupList();
            AvatarGroupEntry avatarGroupEntry = avatarGroupList != null ? (AvatarGroupEntry) avatarGroupList.Groups.get(fromString) : null;
            LLSDNode byKey = lLSDNode.byKey("instantmessage").byKey("message_params");
            UUID asUUID = byKey.keyExists("from_id") ? byKey.byKey("from_id").asUUID() : null;
            UUID asUUID2 = byKey.byKey("to_id").asUUID();
            String asString = byKey.byKey("message").asString();
            if (avatarGroupEntry == null) {
                avatarGroupEntry = avatarGroupList != null ? (AvatarGroupEntry) avatarGroupList.Groups.get(asUUID2) : null;
            }
            if (avatarGroupEntry == null || asUUID == null) {
                Debug.Log("ChatterBoxInvitation: chat from unknown group (" + fromString + "), to_id = " + asUUID2);
            } else {
                HandleChatEvent(ChatterID.getGroupChatterID(this.agentUUID, avatarGroupEntry.GroupID), new SLChatTextEvent(new ChatMessageSourceUser(asUUID), this.agentUUID, asString), true);
            }
        } catch (LLSDException e2) {
            Debug.Log("ChatterBoxInvitation: LLSDException " + e2.getMessage());
            e2.printStackTrace();
        }
    }

    private void HandleChatterBoxSessionStartReply(LLSDNode lLSDNode) {
        try {
            Debug.Log("ChatterBoxSessionStartReply: event = " + lLSDNode.serializeToXML());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            UUID asUUID = lLSDNode.byKey("session_id").asUUID();
            this.modules.voice.onGroupSessionReady(asUUID);
            synchronized (this.startedGroupSessions) {
                this.startedGroupSessions.add(asUUID);
                Iterator it = this.pendingGroupMessages.iterator();
                while (it.hasNext()) {
                    ImprovedInstantMessage improvedInstantMessage = (ImprovedInstantMessage) it.next();
                    if (improvedInstantMessage.MessageBlock_Field.ID.equals(asUUID)) {
                        it.remove();
                        SendMessage(improvedInstantMessage);
                    }
                }
            }
        } catch (LLSDException e2) {
            Debug.Log("ChatterBoxSessionStartReply: LLSDException " + e2.getMessage());
            e2.printStackTrace();
        }
    }

    private void HandleChatterOnlineStatus(ChatterID chatterID, boolean z) {
        if (this.userManager.isChatterActive(chatterID) && (chatterID instanceof ChatterIDUser)) {
            HandleChatEvent(chatterID, new SLChatOnlineOfflineEvent(new ChatMessageSourceUser(((ChatterIDUser) chatterID).getChatterUUID()), this.agentUUID, z), false);
        }
    }

    private void HandleEstablishAgentCommunication(LLSDNode lLSDNode) {
        if (this.teleportRequestSent) {
            try {
                Debug.Log("EstablishAgentCommunication: event = " + lLSDNode.serializeToXML());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                String asString = lLSDNode.byKey("sim-ip-and-port").asString();
                String asString2 = lLSDNode.byKey("seed-capability").asString();
                UUID asUUID = lLSDNode.byKey("agent-id").asUUID();
                String[] split = asString.split(":");
                this.gridConn.addTempCircuit(new SLAuthReply(this.authReply, true, true, asUUID, split[0], Integer.parseInt(split[1]), asString2));
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private void HandleGroupNotice(ImprovedInstantMessage improvedInstantMessage, ChatMessageSource chatMessageSource) {
        ByteBuffer wrap = ByteBuffer.wrap(improvedInstantMessage.MessageBlock_Field.BinaryBucket);
        if (wrap.limit() >= 18) {
            wrap.order(ByteOrder.BIG_ENDIAN);
            byte b = wrap.get();
            byte b2 = wrap.get();
            UUID uuid = new UUID(wrap.getLong(), wrap.getLong());
            String str = "";
            if (b != (byte) 0) {
                byte[] bArr = new byte[wrap.remaining()];
                wrap.get(bArr);
                str = SLMessage.stringFromVariableOEM(bArr);
            }
            Debug.Log("HandleGroupNotice: group UUID = " + uuid.toString());
            ChatterID groupChatterID = ChatterID.getGroupChatterID(this.agentUUID, uuid);
            boolean equal = Objects.equal(chatMessageSource.getSourceUUID(), this.circuitInfo.agentID);
            String stringFromVariableUTF = SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message);
            int indexOf = stringFromVariableUTF.indexOf(ErrorCode.CONTROLLER_GATT_NOTIFY_FAILED);
            if (indexOf >= 0) {
                String substring = stringFromVariableUTF.substring(0, indexOf);
                stringFromVariableUTF = substring + "\n" + stringFromVariableUTF.substring(indexOf + 1);
            }
            if (equal && b != (byte) 0) {
                stringFromVariableUTF = stringFromVariableUTF + "\n" + "(This notice contains attached item '" + str + "')";
            }
            HandleChatEvent(groupChatterID, new SLChatTextEvent(chatMessageSource, this.agentUUID, improvedInstantMessage, stringFromVariableUTF), true);
            if (!(b == (byte) 0 || (equal ^ 1) == 0)) {
                HandleChatEvent(groupChatterID, new SLChatInventoryItemOfferedByGroupNoticeEvent(chatMessageSource, this.agentUUID, improvedInstantMessage, str, SLAssetType.getByType(b2)), false);
            }
        }
    }

    /* DevToolsApp WARNING: Missing block: B:24:0x0113, code:
            if (r0.rlvController.canTeleportToLure(r1) != false) goto L_0x0115;
     */
    private void HandleIM(com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage r8, com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource r9) {
    private void HandleIM(com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage improvedInstantMessage, com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource chatMessageSource) {
        // Check if RLV controller should handle this IM
        SLModules modules = getModules();
        if (modules != null && modules.rlvController.onIncomingIM(improvedInstantMessage)) {
            return; // RLV handled it
        }
        
        byte dialogType = improvedInstantMessage.MessageBlock_Field.Dialog;
        
        switch (dialogType) {
            case 0: // MessageFromAgent 
            case 20: // MessageFromAgent
                handlePersonalMessage(improvedInstantMessage, chatMessageSource);
                break;
                
            case 1: // MessageBox
            case 2: // GroupNotice  
                handleSystemMessage(improvedInstantMessage);
                break;
                
            case 3: // GroupInvitation
                handleGroupInvitation(improvedInstantMessage, chatMessageSource);
                break;
                
            case 4: // InventoryOffered
                handleInventoryOffered(improvedInstantMessage, chatMessageSource);
                break;
                
            case 9: // InventoryAccepted
                handleInventoryOfferedFromObject(improvedInstantMessage);
                break;
                
            case 17: // Session message (group chat)
                HandleSessionIM(improvedInstantMessage, chatMessageSource);
                break;
                
            case 19: // MessageFromObject
            case 31: // MessageFromObject  
                handleObjectMessage(improvedInstantMessage, chatMessageSource);
                break;
                
            case 22: // RequestTeleport
                handleTeleportLure(improvedInstantMessage, chatMessageSource);
                break;
                
            case 26: // RequestLure
                handleLureRequest(improvedInstantMessage, chatMessageSource);
                break;
                
            case 32: // GroupNotice
            case 37: // GroupNoticeRequested
                HandleGroupNotice(improvedInstantMessage, chatMessageSource);
                break;
                
            case 38: // FriendshipOffered
                handleFriendshipOffered(improvedInstantMessage, chatMessageSource);
                break;
                
            case 39: // FriendshipAccepted
            case 40: // FriendshipDeclined
                handleFriendshipResult(improvedInstantMessage, chatMessageSource);
                break;
                
            case 41: // TypingStart
                HandleTypingNotification(chatMessageSource, true);
                break;
                
            case 42: // TypingStop
                HandleTypingNotification(chatMessageSource, false);
                break;
                
            default:
                Debug.Log("HandleIM: unknown type = " + dialogType +
                         ", sessionId = " + improvedInstantMessage.AgentData_Field.SessionID.toString() +
                         ", toAgentID = " + improvedInstantMessage.MessageBlock_Field.ToAgentID.toString() +
                         ", fromGroup = " + improvedInstantMessage.MessageBlock_Field.FromGroup +
                         ", message = '" + SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message) + "'");
                break;
        }
    }
    
    private void handlePersonalMessage(ImprovedInstantMessage improvedInstantMessage, ChatMessageSource chatMessageSource) {
        SLChatTextEvent chatEvent = new SLChatTextEvent(chatMessageSource, this.agentUUID, improvedInstantMessage, null);
        ChatterID defaultChatter = chatMessageSource.getDefaultChatter(this.agentUUID);
        boolean isChatterActive = this.userManager.isChatterActive(defaultChatter);
        
        HandleChatEvent(defaultChatter, chatEvent, true);
        
        // Auto-response logic
        if (!this.userManager.isChatterMuted(defaultChatter) &&
            improvedInstantMessage.MessageBlock_Field.Dialog != 20 && // Not MessageFromAgent
            improvedInstantMessage.MessageBlock_Field.Offline == 0 &&
            improvedInstantMessage.MessageBlock_Field.Message.length > 0 &&
            !isChatterActive &&
            defaultChatter instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser) {
            
            String autoResponse = SLGridConnection.getAutoresponse();
            if (!com.google.common.base.Strings.isNullOrEmpty(autoResponse)) {
                com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser userChatter = 
                    (com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser) defaultChatter;
                SendInstantMessage(userChatter.getChatterUUID(), autoResponse, (byte) 20);
            }
        }
    }
    
    private void handleSystemMessage(ImprovedInstantMessage improvedInstantMessage) {
        SLChatSystemMessageEvent systemEvent = new SLChatSystemMessageEvent(
            com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown.getInstance(),
            this.agentUUID,
            SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message)
        );
        HandleChatEvent(this.localChatterID, systemEvent, true);
    }
    
    private void handleGroupInvitation(ImprovedInstantMessage improvedInstantMessage, ChatMessageSource chatMessageSource) {
        SLChatGroupInvitationEvent invitationEvent = new SLChatGroupInvitationEvent(
            chatMessageSource, this.agentUUID, improvedInstantMessage
        );
        HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), invitationEvent, true);
    }
    
    private void handleInventoryOffered(ImprovedInstantMessage improvedInstantMessage, ChatMessageSource chatMessageSource) {
        SLChatInventoryItemOfferedEvent inventoryEvent = new SLChatInventoryItemOfferedEvent(
            chatMessageSource, this.agentUUID, improvedInstantMessage
        );
        HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), inventoryEvent, true);
    }
    
    private void handleInventoryOfferedFromObject(ImprovedInstantMessage improvedInstantMessage) {
        ChatMessageSource objectSource = new com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject(
            improvedInstantMessage.AgentData_Field.AgentID,
            SLMessage.stringFromVariableOEM(improvedInstantMessage.MessageBlock_Field.FromAgentName)
        );
        
        SLChatInventoryItemOfferedEvent inventoryEvent = new SLChatInventoryItemOfferedEvent(
            objectSource, this.agentUUID, improvedInstantMessage
        );
        HandleChatEvent(this.localChatterID, inventoryEvent, true);
    }
    
    private void handleObjectMessage(ImprovedInstantMessage improvedInstantMessage, ChatMessageSource chatMessageSource) {
        SLChatTextEvent chatEvent = new SLChatTextEvent(
            chatMessageSource, this.agentUUID, improvedInstantMessage, null
        );
        HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), chatEvent, true);
    }
    
    private void handleTeleportLure(ImprovedInstantMessage improvedInstantMessage, ChatMessageSource chatMessageSource) {
        SLModules modules = getModules();
        if (chatMessageSource.getSourceType() == ChatMessageSource.ChatMessageSourceType.User) {
            UUID sourceUUID = chatMessageSource.getSourceUUID();
            if (modules != null) {
                if (modules.rlvController.autoAcceptTeleport(sourceUUID)) {
                    TeleportToLure(improvedInstantMessage.MessageBlock_Field.ID);
                    return;
                } else if (!modules.rlvController.canTeleportToLure(sourceUUID)) {
                    return;
                }
            }
        }
        
        SLChatLureEvent lureEvent = new SLChatLureEvent(
            chatMessageSource, this.agentUUID, improvedInstantMessage
        );
        HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), lureEvent, true);
    }
    
    private void handleLureRequest(ImprovedInstantMessage improvedInstantMessage, ChatMessageSource chatMessageSource) {
        SLModules modules = getModules();
        if (chatMessageSource.getSourceType() == ChatMessageSource.ChatMessageSourceType.User && modules != null) {
            if (!modules.rlvController.canTeleportToLure(chatMessageSource.getSourceUUID())) {
                return;
            }
        }
        
        SLChatLureRequestEvent lureRequestEvent = new SLChatLureRequestEvent(
            chatMessageSource, this.agentUUID, improvedInstantMessage
        );
        HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), lureRequestEvent, true);
    }
    
    private void handleFriendshipOffered(ImprovedInstantMessage improvedInstantMessage, ChatMessageSource chatMessageSource) {
        SLChatFriendshipOfferedEvent friendshipEvent = new SLChatFriendshipOfferedEvent(
            chatMessageSource, this.agentUUID, improvedInstantMessage
        );
        HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), friendshipEvent, true);
    }
    
    private void handleFriendshipResult(ImprovedInstantMessage improvedInstantMessage, ChatMessageSource chatMessageSource) {
        SLChatFriendshipResultEvent friendshipResultEvent = new SLChatFriendshipResultEvent(
            chatMessageSource, this.agentUUID, improvedInstantMessage
        );
        HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), friendshipResultEvent, true);
        
        // If friendship accepted, add to friend list and request online notification
        if (improvedInstantMessage.MessageBlock_Field.Dialog == 39) { // FriendshipAccepted
            if (chatMessageSource.getSourceType() == ChatMessageSource.ChatMessageSourceType.User) {
                UUID sourceUUID = chatMessageSource.getSourceUUID();
                if (sourceUUID != null) {
                    this.userManager.getChatterList().getFriendManager().addFriend(sourceUUID);
                    SendGenericMessage("requestonlinenotification", new String[]{sourceUUID.toString()});
                }
            }
        }
    }
    }

    private void HandleSessionIM(ImprovedInstantMessage improvedInstantMessage, ChatMessageSource chatMessageSource) {
        HandleChatEvent(ChatterID.getGroupChatterID(this.agentUUID, improvedInstantMessage.MessageBlock_Field.ID), new SLChatTextEvent(chatMessageSource, this.agentUUID, improvedInstantMessage, null), true);
    }

    private void HandleTeleportFailed(LLSDNode lLSDNode) {
        try {
            Debug.Log("TeleportFailed: event = " + lLSDNode.serializeToXML());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (this.teleportRequestSent) {
            this.teleportRequestSent = false;
            this.eventBus.publish(new SLTeleportResultEvent(false, "Teleport has failed."));
        }
    }

    private void HandleTeleportFinish(LLSDNode lLSDNode) {
        try {
            Debug.Log("TeleportFinish: event = " + lLSDNode.serializeToXML());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (this.teleportRequestSent) {
            this.teleportRequestSent = false;
            try {
                LLSDNode byIndex = lLSDNode.byKey("Info").byIndex(0);
                String asString = byIndex.byKey("SeedCapability").asString();
                byte[] asBinary = byIndex.byKey("SimIP").asBinary();
                Debug.Printf("new sim address: %s", new SLAuthReply(this.authReply, true, false, this.authReply.agentID, String.format("%d.%d.%d.%d", new Object[]{Integer.valueOf(asBinary[0] & 255), Integer.valueOf(asBinary[1] & 255), Integer.valueOf(asBinary[2] & 255), Integer.valueOf(asBinary[3] & 255)}), byIndex.byKey("SimPort").asInt(), asString).simAddress);
                this.modules.avatarControl.setEnableAgentUpdates(false);
                this.gridConn.HandleTeleportFinish(r0);
                return;
            } catch (LLSDException e2) {
                Debug.Log("TeleportFinish: LLSDException, teleport apparently failed");
                e2.printStackTrace();
                return;
            }
        }
        Debug.Log("TeleportFinish: stale teleport finish?");
    }

    private void HandleTypingNotification(ChatMessageSource chatMessageSource, boolean z) {
        if (chatMessageSource instanceof ChatMessageSourceUser) {
            UUID sourceUUID = chatMessageSource.getSourceUUID();
            if (sourceUUID == null) {
                return;
            }
            if (z) {
                if (this.typingUsers.add(sourceUUID)) {
                    this.userManager.getChatterList().updateUserTypingStatus(sourceUUID);
                }
            } else if (this.typingUsers.remove(sourceUUID)) {
                this.userManager.getChatterList().updateUserTypingStatus(sourceUUID);
            }
        }
    }

    private void ProcessObjectSelection() {
        if (getNeedObjectNames() && (this.doingObjectSelection ^ 1) != 0) {
            SLMessage sLMessage;
            SLMessage sLMessage2 = null;
            for (SLObjectInfo sLObjectInfo : this.forceNeedObjectNames.values()) {
                if (sLMessage2 == null) {
                    sLMessage2 = new ObjectSelect();
                    sLMessage2.AgentData_Field.AgentID = this.circuitInfo.agentID;
                    sLMessage2.AgentData_Field.SessionID = this.circuitInfo.sessionID;
                }
                if (sLMessage2.ObjectData_Fields.size() > 16) {
                    break;
                }
                ObjectData objectData = new ObjectData();
                objectData.ObjectLocalID = sLObjectInfo.localID;
                sLMessage2.ObjectData_Fields.add(objectData);
                sLObjectInfo.nameRequested = true;
                sLObjectInfo.nameRequestedAt = System.currentTimeMillis();
                this.objectNamesRequested.put(sLObjectInfo.getId(), sLObjectInfo);
            }
            synchronized (this.gridConn.parcelInfo.objectNamesQueue) {
                for (SLObjectInfo sLObjectInfo2 : this.gridConn.parcelInfo.objectNamesQueue.values()) {
                    if (sLMessage2 == null) {
                        sLMessage2 = new ObjectSelect();
                        sLMessage2.AgentData_Field.AgentID = this.circuitInfo.agentID;
                        sLMessage2.AgentData_Field.SessionID = this.circuitInfo.sessionID;
                    }
                    if (sLMessage2.ObjectData_Fields.size() > 16) {
                        sLMessage = sLMessage2;
                        break;
                    }
                    ObjectData objectData2 = new ObjectData();
                    objectData2.ObjectLocalID = sLObjectInfo2.localID;
                    sLMessage2.ObjectData_Fields.add(objectData2);
                    sLObjectInfo2.nameRequested = true;
                    sLObjectInfo2.nameRequestedAt = System.currentTimeMillis();
                    this.objectNamesRequested.put(sLObjectInfo2.getId(), sLObjectInfo2);
                }
                sLMessage = sLMessage2;
            }
            if (sLMessage != null) {
                Debug.Log("ObjectSelect: Sending ObjectSelect for " + sLMessage.ObjectData_Fields.size() + " objects, " + this.gridConn.parcelInfo.objectNamesQueue.size() + " remains.");
                sLMessage.isReliable = true;
                SendMessage(sLMessage);
                this.lastObjectSelection = System.currentTimeMillis();
                this.doingObjectSelection = true;
            }
        }
    }

    private void ProcessObjectSelectionTimeout() {
        for (SLObjectInfo sLObjectInfo : this.objectNamesRequested.values()) {
            SLObjectInfo sLObjectInfo2 = (SLObjectInfo) this.gridConn.parcelInfo.objectNamesQueue.remove(sLObjectInfo.getId());
            if (sLObjectInfo2 != null) {
                this.gridConn.parcelInfo.objectNamesQueue.put(sLObjectInfo2.getId(), sLObjectInfo2);
            }
            this.forceNeedObjectNames.remove(sLObjectInfo.getId());
        }
        this.objectNamesRequested.clear();
    }

    private void SendAgentFOV() {
        SLMessage agentFOV = new AgentFOV();
        agentFOV.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentFOV.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        agentFOV.AgentData_Field.CircuitCode = this.circuitInfo.circuitCode;
        agentFOV.FOVBlock_Field.GenCounter = 0;
        agentFOV.FOVBlock_Field.VerticalAngle = 3.0543263f;
        agentFOV.isReliable = true;
        SendMessage(agentFOV);
    }

    private void SendCompleteAgentMovement() {
        SLMessage completeAgentMovement = new CompleteAgentMovement();
        completeAgentMovement.AgentData_Field.CircuitCode = this.circuitInfo.circuitCode;
        completeAgentMovement.AgentData_Field.AgentID = this.circuitInfo.agentID;
        completeAgentMovement.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        completeAgentMovement.isReliable = true;
        SendMessage(completeAgentMovement);
    }

    private void SendEstateOwnerMessage(String str, String[] strArr) {
        SLMessage estateOwnerMessage = new EstateOwnerMessage();
        estateOwnerMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        estateOwnerMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        estateOwnerMessage.AgentData_Field.TransactionID = new UUID(0, 0);
        estateOwnerMessage.MethodData_Field.Method = SLMessage.stringToVariableOEM(str);
        estateOwnerMessage.MethodData_Field.Invoice = new UUID(0, 0);
        for (String str2 : strArr) {
            ParamList paramList = new ParamList();
            paramList.Parameter = SLMessage.stringToVariableOEM(str2);
            estateOwnerMessage.ParamList_Fields.add(paramList);
        }
        estateOwnerMessage.isReliable = true;
        SendMessage(estateOwnerMessage);
    }

    private void SendGroupSessionStart(UUID uuid) {
        SLMessage improvedInstantMessage = new ImprovedInstantMessage();
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        improvedInstantMessage.MessageBlock_Field.FromGroup = false;
        improvedInstantMessage.MessageBlock_Field.ToAgentID = uuid;
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0;
        improvedInstantMessage.MessageBlock_Field.RegionID = new UUID(0, 0);
        improvedInstantMessage.MessageBlock_Field.Position = this.modules.avatarControl.getAgentPosition().getPosition();
        improvedInstantMessage.MessageBlock_Field.Offline = 0;
        improvedInstantMessage.MessageBlock_Field.Dialog = 15;
        improvedInstantMessage.MessageBlock_Field.ID = uuid;
        improvedInstantMessage.MessageBlock_Field.Timestamp = 0;
        improvedInstantMessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo");
        improvedInstantMessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF("");
        improvedInstantMessage.MessageBlock_Field.BinaryBucket = new byte[1];
        improvedInstantMessage.isReliable = true;
        SendMessage(improvedInstantMessage);
    }

    private boolean SendInstantMessage(UUID uuid, String str, int i) {
        if (!getModules().rlvController.canSendIM(uuid)) {
            return false;
        }
        SLMessage improvedInstantMessage = new ImprovedInstantMessage();
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        improvedInstantMessage.MessageBlock_Field.FromGroup = false;
        improvedInstantMessage.MessageBlock_Field.ToAgentID = uuid;
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0;
        improvedInstantMessage.MessageBlock_Field.RegionID = new UUID(0, 0);
        improvedInstantMessage.MessageBlock_Field.Position = new LLVector3();
        improvedInstantMessage.MessageBlock_Field.Offline = 0;
        improvedInstantMessage.MessageBlock_Field.Dialog = i;
        improvedInstantMessage.MessageBlock_Field.ID = new UUID(uuid.getMostSignificantBits() ^ this.circuitInfo.agentID.getMostSignificantBits(), uuid.getLeastSignificantBits() ^ this.circuitInfo.agentID.getLeastSignificantBits());
        improvedInstantMessage.MessageBlock_Field.Timestamp = 0;
        improvedInstantMessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo");
        improvedInstantMessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF(str);
        improvedInstantMessage.MessageBlock_Field.BinaryBucket = new byte[0];
        improvedInstantMessage.isReliable = true;
        SendMessage(improvedInstantMessage);
        if (!(i == 20 || i == 41 || i == 42)) {
            if (i == 26) {
                HandleChatEvent(ChatterID.getUserChatterID(this.agentUUID, uuid), new SLChatLureRequestedEvent(str, this.agentUUID), false);
            } else {
                HandleChatEvent(ChatterID.getUserChatterID(this.agentUUID, uuid), new SLChatTextEvent(new ChatMessageSourceUser(this.circuitInfo.agentID), this.agentUUID, str), false);
            }
        }
        return true;
    }

    private void SendRetrieveInstantMessages() {
        SLMessage retrieveInstantMessages = new RetrieveInstantMessages();
        retrieveInstantMessages.AgentData_Field.AgentID = this.circuitInfo.agentID;
        retrieveInstantMessages.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        retrieveInstantMessages.isReliable = true;
        SendMessage(retrieveInstantMessages);
    }

    private UUID getActiveGroupID() {
        return this.modules != null ? this.modules.groupManager.getActiveGroupID() : null;
    }

    private boolean getNeedObjectNames() {
        if (this.forceNeedObjectNames != null && !this.forceNeedObjectNames.isEmpty()) {
            return true;
        }
        return this.modules != null ? this.modules.drawDistance.isObjectSelectEnabled() : false;
    }

    private boolean isEventMuted(ChatterID chatterID, SLChatEvent sLChatEvent) {
        if (this.modules != null) {
            SLMuteList sLMuteList = this.modules.muteList;
            ChatMessageSource source = sLChatEvent.getSource();
            if (source.getSourceType() == ChatMessageSourceType.User) {
                if (sLMuteList.isMuted(source.getSourceUUID(), MuteType.AGENT)) {
                    return true;
                }
            } else if (source.getSourceType() == ChatMessageSourceType.Object) {
                UUID sourceUUID = source.getSourceUUID();
                if (sourceUUID != null && !sourceUUID.equals(UUIDPool.ZeroUUID) && sLMuteList.isMuted(sourceUUID, MuteType.OBJECT)) {
                    return true;
                }
                String sourceName = source.getSourceName(this.userManager);
                if (sourceName != null && sLMuteList.isMutedByName(sourceName)) {
                    return true;
                }
            }
            if (chatterID instanceof ChatterIDGroup) {
                UUID chatterUUID = ((ChatterIDGroup) chatterID).getChatterUUID();
                if (!chatterUUID.equals(UUIDPool.ZeroUUID) && sLMuteList.isMuted(chatterUUID, MuteType.GROUP)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void notifyObjectPropertiesChange() {
        if (this.userManager != null) {
            this.userManager.getObjectsManager().requestObjectListUpdate();
        }
    }

    private void processMyAvatarUpdate(SLObjectAvatarInfo sLObjectAvatarInfo) {
        if (this.modules != null) {
            this.modules.avatarControl.setAgentPosition(sLObjectAvatarInfo.getAbsolutePosition(), sLObjectAvatarInfo.getObjectCoords().get(2));
        }
    }

    public void AcceptFriendship(UUID uuid, UUID uuid2) {
        UUID uuid3 = null;
        this.userManager.getChatterList().getFriendManager().addFriend(uuid);
        SLMessage acceptFriendship = new AcceptFriendship();
        acceptFriendship.AgentData_Field.AgentID = this.circuitInfo.agentID;
        acceptFriendship.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        if (this.modules != null) {
            uuid3 = this.modules.inventory.getCallingCardsFolderUUID();
        }
        FolderData folderData = new FolderData();
        if (uuid3 == null) {
            uuid3 = UUIDPool.ZeroUUID;
        }
        folderData.FolderID = uuid3;
        acceptFriendship.FolderData_Fields.add(folderData);
        acceptFriendship.TransactionBlock_Field.TransactionID = uuid2;
        acceptFriendship.isReliable = true;
        SendMessage(acceptFriendship);
    }

    public void AcceptInventoryOffer(int i, boolean z, UUID uuid, UUID uuid2, UUID uuid3) {
        SLMessage improvedInstantMessage = new ImprovedInstantMessage();
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        improvedInstantMessage.MessageBlock_Field.FromGroup = false;
        improvedInstantMessage.MessageBlock_Field.ToAgentID = uuid;
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0;
        improvedInstantMessage.MessageBlock_Field.RegionID = new UUID(0, 0);
        improvedInstantMessage.MessageBlock_Field.Position = new LLVector3();
        improvedInstantMessage.MessageBlock_Field.Offline = 0;
        if (z) {
            improvedInstantMessage.MessageBlock_Field.Dialog = i + 1;
        } else {
            improvedInstantMessage.MessageBlock_Field.Dialog = i + 2;
        }
        improvedInstantMessage.MessageBlock_Field.ID = uuid2;
        improvedInstantMessage.MessageBlock_Field.Timestamp = 0;
        improvedInstantMessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo");
        improvedInstantMessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF("");
        if (uuid3 != null) {
            ByteBuffer wrap = ByteBuffer.wrap(new byte[16]);
            wrap.order(ByteOrder.BIG_ENDIAN);
            wrap.putLong(uuid3.getMostSignificantBits());
            wrap.putLong(uuid3.getLeastSignificantBits());
            wrap.position(0);
            improvedInstantMessage.MessageBlock_Field.BinaryBucket = wrap.array();
        } else {
            improvedInstantMessage.MessageBlock_Field.BinaryBucket = new byte[0];
        }
        improvedInstantMessage.isReliable = true;
        SendMessage(improvedInstantMessage);
    }

    public void AddFriend(UUID uuid, String str) {
        SLMessage improvedInstantMessage = new ImprovedInstantMessage();
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        improvedInstantMessage.MessageBlock_Field.FromGroup = false;
        improvedInstantMessage.MessageBlock_Field.ToAgentID = uuid;
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0;
        improvedInstantMessage.MessageBlock_Field.RegionID = new UUID(0, 0);
        improvedInstantMessage.MessageBlock_Field.Position = new LLVector3();
        improvedInstantMessage.MessageBlock_Field.Offline = 0;
        improvedInstantMessage.MessageBlock_Field.Dialog = 38;
        improvedInstantMessage.MessageBlock_Field.ID = new UUID(uuid.getMostSignificantBits() ^ this.circuitInfo.agentID.getMostSignificantBits(), uuid.getLeastSignificantBits() ^ this.circuitInfo.agentID.getLeastSignificantBits());
        improvedInstantMessage.MessageBlock_Field.Timestamp = 0;
        improvedInstantMessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo");
        improvedInstantMessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF(str);
        improvedInstantMessage.MessageBlock_Field.BinaryBucket = new byte[0];
        improvedInstantMessage.isReliable = true;
        SendMessage(improvedInstantMessage);
    }

    public void BuyObject(int i, byte b, int i2) {
        UUID activeGroupID = getActiveGroupID();
        SLMessage objectBuy = new ObjectBuy();
        objectBuy.AgentData_Field.AgentID = this.circuitInfo.agentID;
        objectBuy.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        AgentData agentData = objectBuy.AgentData_Field;
        if (activeGroupID == null) {
            activeGroupID = UUIDPool.ZeroUUID;
        }
        agentData.GroupID = activeGroupID;
        objectBuy.AgentData_Field.CategoryID = getModules().inventory.rootFolder.uuid;
        ObjectBuy.ObjectData objectData = new ObjectBuy.ObjectData();
        objectData.ObjectLocalID = i;
        objectData.SaleType = b;
        objectData.SalePrice = i2;
        objectBuy.ObjectData_Fields.add(objectData);
        objectBuy.isReliable = true;
        SendMessage(objectBuy);
    }

    public void CloseCircuit() {
        Debug.Printf("AgentCircuit: closing circuit.", new Object[0]);
        if (this.modules != null) {
            this.modules.HandleCloseCircuit();
        }
        if (this.userManager != null) {
            this.userManager.clearActiveAgentCircuit(this);
        }
        if (this.agentNameSubscription != null) {
            this.agentNameSubscription.unsubscribe();
            this.agentNameSubscription = null;
        }
        super.CloseCircuit();
    }

    public void DerezObject(int i, EDeRezDestination eDeRezDestination) {
        UUID activeGroupID = getActiveGroupID();
        SLMessage deRezObject = new DeRezObject();
        deRezObject.AgentData_Field.AgentID = this.circuitInfo.agentID;
        deRezObject.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        AgentBlock agentBlock = deRezObject.AgentBlock_Field;
        if (activeGroupID == null) {
            activeGroupID = new UUID(0, 0);
        }
        agentBlock.GroupID = activeGroupID;
        deRezObject.AgentBlock_Field.Destination = eDeRezDestination.getCode();
        deRezObject.AgentBlock_Field.DestinationID = new UUID(0, 0);
        deRezObject.AgentBlock_Field.PacketCount = 1;
        deRezObject.AgentBlock_Field.PacketNumber = 0;
        deRezObject.AgentBlock_Field.TransactionID = UUID.randomUUID();
        DeRezObject.ObjectData objectData = new DeRezObject.ObjectData();
        objectData.ObjectLocalID = i;
        deRezObject.ObjectData_Fields.add(objectData);
        deRezObject.isReliable = true;
        SendMessage(deRezObject);
    }

    public void DoRequestPayPrice(UUID uuid) {
        SLObjectInfo sLObjectInfo = (SLObjectInfo) this.gridConn.parcelInfo.allObjectsNearby.get(uuid);
        if (sLObjectInfo == null) {
            return;
        }
        if (sLObjectInfo.getPayInfo() != null) {
            this.eventBus.publish(new SLObjectPayInfoEvent(sLObjectInfo));
            return;
        }
        SLMessage requestPayPrice = new RequestPayPrice();
        requestPayPrice.ObjectData_Field.ObjectID = uuid;
        requestPayPrice.isReliable = true;
        SendMessage(requestPayPrice);
    }

    public void GenerateChatMoneyEvent(UUID uuid, int i, int i2) {
        HandleChatEvent(uuid != null ? ChatterID.getUserChatterID(this.agentUUID, uuid) : this.localChatterID, new SLChatBalanceChangedEvent(uuid != null ? new ChatMessageSourceUser(uuid) : ChatMessageSourceUnknown.getInstance(), this.agentUUID, true, i, i2), true);
        if (this.modules != null) {
            this.modules.financialInfo.RecordChatEvent(uuid, i, i2);
        }
    }

    public void HandleAgentMovementComplete(AgentMovementComplete agentMovementComplete) {
        this.regionHandle = agentMovementComplete.Data_Field.RegionHandle;
        this.modules.avatarControl.setAgentPosition(agentMovementComplete.Data_Field.Position, null);
        Debug.Printf("Got agentPosition: %s", this.modules.avatarControl.getAgentPosition().getImmutablePosition());
        SendAgentFOV();
        this.modules.avatarAppearance.SendAgentWearablesRequest();
        SendRetrieveInstantMessages();
        this.modules.avatarControl.setEnableAgentUpdates(true);
    }

    public void HandleAlertMessage(AlertMessage alertMessage) {
        HandleChatEvent(this.localChatterID, new SLChatSystemMessageEvent(ChatMessageSourceUnknown.getInstance(), this.agentUUID, SLMessage.stringFromVariableOEM(alertMessage.AlertData_Field.Message)), true);
    }

    public void HandleAvatarAnimation(AvatarAnimation avatarAnimation) {
        SLParcelInfo sLParcelInfo = this.gridConn.parcelInfo;
        if (sLParcelInfo != null && this.modules != null) {
            sLParcelInfo.ApplyAvatarAnimation(avatarAnimation, this.modules.avatarControl);
        }
    }

    public void HandleAvatarAppearance(AvatarAppearance avatarAppearance) {
        Debug.Log("Got AvatarAppearance, ID = " + avatarAppearance.Sender_Field.ID.toString() + " isTrial = " + avatarAppearance.Sender_Field.IsTrial + ", our ID = " + this.circuitInfo.agentID.toString());
        if (avatarAppearance.Sender_Field.ID.equals(this.circuitInfo.agentID) && this.modules != null) {
            this.modules.avatarAppearance.HandleAvatarAppearance(avatarAppearance);
        }
        SLParcelInfo sLParcelInfo = this.gridConn.parcelInfo;
        if (sLParcelInfo != null) {
            sLParcelInfo.ApplyAvatarAppearance(avatarAppearance);
        }
    }

    public void HandleAvatarInterestsReply(AvatarInterestsReply avatarInterestsReply) {
        Debug.Log("got AvatarInterestsReply: wantToText = " + SLMessage.stringFromVariableOEM(avatarInterestsReply.PropertiesData_Field.WantToText));
        Debug.Log("got AvatarInterestsReply: skillText = " + SLMessage.stringFromVariableOEM(avatarInterestsReply.PropertiesData_Field.SkillsText));
    }

    public void HandleChatEvent(ChatterID chatterID, SLChatEvent sLChatEvent, boolean z) {
        if (!isEventMuted(chatterID, sLChatEvent)) {
            this.userManager.getChatterList().getActiveChattersManager().HandleChatEvent(chatterID, sLChatEvent, z);
        }
    }

    public void HandleChatFromSimulator(ChatFromSimulator chatFromSimulator) {
        SLModules modules = getModules();
        if (modules == null || !modules.rlvController.onIncomingChat(chatFromSimulator)) {
            UUID uuid = chatFromSimulator.ChatData_Field.SourceID;
            String stringFromVariableOEM = SLMessage.stringFromVariableOEM(chatFromSimulator.ChatData_Field.FromName);
            String stringFromVariableUTF = SLMessage.stringFromVariableUTF(chatFromSimulator.ChatData_Field.Message);
            if (chatFromSimulator.ChatData_Field.ChatType != 8 || chatFromSimulator.ChatData_Field.SourceType != 2 || !stringFromVariableOEM.startsWith("#Firestorm LSL Bridge") || !stringFromVariableUTF.startsWith("<bridgeURL>")) {
                if ((chatFromSimulator.ChatData_Field.SourceType != 1 || modules == null || modules.rlvController.canRecvChat(stringFromVariableUTF, uuid)) && chatFromSimulator.ChatData_Field.Audible == 1) {
                    int i = chatFromSimulator.ChatData_Field.ChatType;
                    if (i != 6 && i != 4 && i != 5) {
                        switch (chatFromSimulator.ChatData_Field.SourceType) {
                            case 1:
                                HandleChatEvent(this.localChatterID, new SLChatTextEvent(new ChatMessageSourceUser(uuid), this.agentUUID, stringFromVariableUTF), true);
                                break;
                            case 2:
                                HandleChatEvent(this.localChatterID, new SLChatTextEvent(new ChatMessageSourceObject(uuid, stringFromVariableOEM), this.agentUUID, stringFromVariableUTF), true);
                                break;
                            default:
                                HandleChatEvent(this.localChatterID, new SLChatTextEvent(ChatMessageSourceUnknown.getInstance(), this.agentUUID, stringFromVariableUTF), true);
                                break;
                        }
                    }
                }
            }
        }
    }

    public void HandleImprovedInstantMessage(ImprovedInstantMessage improvedInstantMessage) {
        ChatMessageSource chatMessageSourceObject;
        int i = improvedInstantMessage.MessageBlock_Field.Dialog;
        if (i == 19 || i == 31) {
            chatMessageSourceObject = new ChatMessageSourceObject(improvedInstantMessage.AgentData_Field.AgentID, SLMessage.stringFromVariableOEM(improvedInstantMessage.MessageBlock_Field.FromAgentName));
        } else if (i == 3) {
            chatMessageSourceObject = ChatMessageSourceUnknown.getInstance();
        } else if (UUIDPool.ZeroUUID.equals(improvedInstantMessage.AgentData_Field.AgentID)) {
            chatMessageSourceObject = ChatMessageSourceUnknown.getInstance();
        } else {
            chatMessageSourceObject = new ChatMessageSourceUser(improvedInstantMessage.AgentData_Field.AgentID);
            if (!getModules().rlvController.canRecvIM(chatMessageSourceObject.getSourceUUID())) {
                return;
            }
        }
        HandleIM(improvedInstantMessage, chatMessageSourceObject);
    }

    public void HandleImprovedTerseObjectUpdate(ImprovedTerseObjectUpdate improvedTerseObjectUpdate) {
        SLParcelInfo sLParcelInfo = this.gridConn.parcelInfo;
        RequestMultipleObjects requestMultipleObjects = null;
        for (ImprovedTerseObjectUpdate.ObjectData objectData : improvedTerseObjectUpdate.ObjectData_Fields) {
            SLObjectInfo sLObjectInfo;
            int localID = SLObjectInfo.getLocalID(objectData);
            UUID uuid = (UUID) sLParcelInfo.uuidsNearby.get(Integer.valueOf(localID));
            if (uuid != null) {
                sLObjectInfo = (SLObjectInfo) sLParcelInfo.allObjectsNearby.get(uuid);
                if (sLObjectInfo != null) {
                    sLObjectInfo.ApplyTerseObjectUpdate(objectData);
                    if (sLObjectInfo instanceof SLObjectAvatarInfo ? ((SLObjectAvatarInfo) sLObjectInfo).isMyAvatar() : false) {
                        processMyAvatarUpdate((SLObjectAvatarInfo) sLObjectInfo);
                    } else if (sLObjectInfo.isMyAttachment()) {
                        processMyAttachmentUpdate(sLObjectInfo);
                    }
                }
            } else {
                sLObjectInfo = null;
            }
            if (sLObjectInfo == null) {
                if (requestMultipleObjects == null) {
                    requestMultipleObjects = new RequestMultipleObjects();
                    requestMultipleObjects.AgentData_Field.AgentID = this.circuitInfo.agentID;
                    requestMultipleObjects.AgentData_Field.SessionID = this.circuitInfo.sessionID;
                }
                RequestMultipleObjects.ObjectData objectData2 = new RequestMultipleObjects.ObjectData();
                objectData2.CacheMissType = 0;
                objectData2.ID = localID;
                requestMultipleObjects.ObjectData_Fields.add(objectData2);
            }
            requestMultipleObjects = requestMultipleObjects;
        }
        if (requestMultipleObjects != null) {
            Debug.Log("Handing cache miss for terse update: " + requestMultipleObjects.ObjectData_Fields.size() + " objects.");
            requestMultipleObjects.isReliable = true;
            SendMessage(requestMultipleObjects);
        }
    }

    public void HandleKillObject(KillObject killObject) {
        Object obj;
        SLParcelInfo sLParcelInfo = this.gridConn.parcelInfo;
        Object obj2 = null;
        Iterator it = killObject.ObjectData_Fields.iterator();
        while (true) {
            obj = obj2;
            if (!it.hasNext()) {
                break;
            }
            obj2 = sLParcelInfo.killObject(this, ((KillObject.ObjectData) it.next()).ID) ? 1 : obj;
        }
        if (obj != null) {
            this.objectPropertiesRateLimiter.fire();
        }
    }

    public void HandleLayerData(LayerData layerData) {
        if (layerData.LayerID_Field.Type == 76) {
            SLParcelInfo sLParcelInfo = this.gridConn.parcelInfo;
            if (sLParcelInfo != null) {
                sLParcelInfo.terrainData.ProcessLayerData(layerData.LayerDataData_Field.Data);
            }
        }
    }

    public void HandleLoadURL(LoadURL loadURL) {
        HandleChatEvent(this.localChatterID, new SLChatTextEvent(new ChatMessageSourceObject(loadURL.Data_Field.ObjectID, SLMessage.stringFromVariableOEM(loadURL.Data_Field.ObjectName)), this.agentUUID, loadURL), true);
    }

    public void HandleObjectProperties(ObjectProperties objectProperties) {
        Debug.Log("ObjectProperties: " + objectProperties.ObjectData_Fields.size() + " ObjectSelect replies. Reqd " + this.objectNamesRequested.size() + " obj, remains " + this.gridConn.parcelInfo.objectNamesQueue.size() + " objects.");
        for (ObjectProperties.ObjectData objectData : objectProperties.ObjectData_Fields) {
            SLObjectInfo sLObjectInfo = (SLObjectInfo) this.gridConn.parcelInfo.objectNamesQueue.remove(objectData.ObjectID);
            if (sLObjectInfo != null) {
                sLObjectInfo.ApplyObjectProperties(objectData);
                this.userManager.getObjectsManager().requestObjectProfileUpdate(sLObjectInfo.localID);
            }
            sLObjectInfo = (SLObjectInfo) this.forceNeedObjectNames.remove(objectData.ObjectID);
            if (sLObjectInfo != null) {
                sLObjectInfo.ApplyObjectProperties(objectData);
                this.userManager.getObjectsManager().requestObjectProfileUpdate(sLObjectInfo.localID);
                sLObjectInfo = sLObjectInfo.getParentObject();
                if (sLObjectInfo != null) {
                    UUID id = sLObjectInfo.getId();
                    if (id != null) {
                        this.userManager.getObjectsManager().requestTouchableChildrenUpdate(id);
                    }
                }
            }
            this.objectNamesRequested.remove(objectData.ObjectID);
        }
        if (this.objectNamesRequested.isEmpty()) {
            this.doingObjectSelection = false;
            ProcessObjectSelection();
        }
        this.objectPropertiesRateLimiter.fire();
    }

    public void HandleObjectUpdate(ObjectUpdate objectUpdate) {
        SLParcelInfo sLParcelInfo = this.gridConn.parcelInfo;
        Object obj = null;
        Object obj2 = null;
        for (ObjectUpdate.ObjectData objectData : objectUpdate.ObjectData_Fields) {
            if (objectData.PCode == 47 || objectData.PCode == 9) {
                SLObjectInfo sLObjectInfo = (SLObjectInfo) sLParcelInfo.allObjectsNearby.get(objectData.FullID);
                if (sLObjectInfo != null) {
                    int i = sLObjectInfo.parentID;
                    sLObjectInfo.ApplyObjectUpdate(objectData);
                    sLParcelInfo.updateObjectParent(i, sLObjectInfo);
                    if (sLObjectInfo.parentID != i && (sLObjectInfo instanceof SLObjectAvatarInfo) && ((SLObjectAvatarInfo) sLObjectInfo).isMyAvatar()) {
                        obj = 1;
                    }
                    obj2 = 1;
                } else {
                    sLObjectInfo = SLObjectInfo.create(this.agentUUID, objectData, this.circuitInfo.agentID);
                    if (sLParcelInfo.addObject(sLObjectInfo)) {
                        obj2 = 1;
                    }
                    if ((sLObjectInfo instanceof SLObjectAvatarInfo) && ((SLObjectAvatarInfo) sLObjectInfo).isMyAvatar()) {
                        Debug.Log("ObjectUpdate: got my avatar (normal)");
                        sLParcelInfo.setAgentAvatar((SLObjectAvatarInfo) sLObjectInfo);
                        this.modules.avatarAppearance.OnMyAvatarCreated((SLObjectAvatarInfo) sLObjectInfo);
                        int i2 = 1;
                    }
                }
                if (sLObjectInfo instanceof SLObjectAvatarInfo ? ((SLObjectAvatarInfo) sLObjectInfo).isMyAvatar() : false) {
                    processMyAvatarUpdate((SLObjectAvatarInfo) sLObjectInfo);
                } else if (sLObjectInfo.isMyAttachment()) {
                    processMyAttachmentUpdate(sLObjectInfo);
                }
            }
            obj = obj;
            obj2 = obj2;
        }
        if (obj != null) {
            this.userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value);
        }
        if (obj2 != null) {
            ProcessObjectSelection();
            this.objectPropertiesRateLimiter.fire();
        }
    }

    public void HandleObjectUpdateCached(ObjectUpdateCached objectUpdateCached) {
        SLMessage requestMultipleObjects = new RequestMultipleObjects();
        requestMultipleObjects.AgentData_Field.AgentID = this.circuitInfo.agentID;
        requestMultipleObjects.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        for (ObjectUpdateCached.ObjectData objectData : objectUpdateCached.ObjectData_Fields) {
            RequestMultipleObjects.ObjectData objectData2 = new RequestMultipleObjects.ObjectData();
            objectData2.CacheMissType = 0;
            objectData2.ID = objectData.ID;
            requestMultipleObjects.ObjectData_Fields.add(objectData2);
        }
        requestMultipleObjects.isReliable = true;
        SendMessage(requestMultipleObjects);
    }

    public void HandleObjectUpdateCompressed(ObjectUpdateCompressed objectUpdateCompressed) {
        SLParcelInfo sLParcelInfo = this.gridConn.parcelInfo;
        Object obj = null;
        Object obj2 = null;
        for (ObjectUpdateCompressed.ObjectData objectData : objectUpdateCompressed.ObjectData_Fields) {
            try {
                Object obj3;
                UUID uuid = (UUID) sLParcelInfo.uuidsNearby.get(Integer.valueOf(SLObjectInfo.getLocalID(objectData)));
                SLObjectInfo sLObjectInfo = uuid != null ? (SLObjectInfo) sLParcelInfo.allObjectsNearby.get(uuid) : null;
                if (sLObjectInfo != null) {
                    int i = sLObjectInfo.parentID;
                    sLObjectInfo.ApplyObjectUpdate(objectData);
                    sLParcelInfo.updateObjectParent(i, sLObjectInfo);
                    obj3 = sLObjectInfo.parentID != i ? 1 : null;
                    obj2 = 1;
                } else {
                    sLObjectInfo = SLObjectInfo.create(objectData);
                    if (sLParcelInfo.addObject(sLObjectInfo)) {
                        obj2 = 1;
                    }
                    obj3 = null;
                }
                if (sLObjectInfo instanceof SLObjectAvatarInfo ? ((SLObjectAvatarInfo) sLObjectInfo).isMyAvatar() : false) {
                    if (obj3 != null) {
                        obj = 1;
                    }
                    processMyAvatarUpdate((SLObjectAvatarInfo) sLObjectInfo);
                } else if (sLObjectInfo.isMyAttachment()) {
                    processMyAttachmentUpdate(sLObjectInfo);
                }
            } catch (UnsupportedObjectTypeException e) {
            } catch (Throwable e2) {
                Debug.Warning(e2);
            }
            obj = obj;
            obj2 = obj2;
        }
        if (obj2 != null) {
            ProcessObjectSelection();
            this.objectPropertiesRateLimiter.fire();
        }
        if (obj != null) {
            this.userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value);
        }
    }

    public void HandleOfflineNotification(OfflineNotification offlineNotification) {
        List arrayList = new ArrayList(offlineNotification.AgentBlock_Fields.size());
        for (OfflineNotification.AgentBlock agentBlock : offlineNotification.AgentBlock_Fields) {
            arrayList.add(agentBlock.AgentID);
        }
        this.userManager.getChatterList().getFriendManager().setUsersOnline(arrayList, false);
    }

    public void HandleOnlineNotification(OnlineNotification onlineNotification) {
        List arrayList = new ArrayList(onlineNotification.AgentBlock_Fields.size());
        for (OnlineNotification.AgentBlock agentBlock : onlineNotification.AgentBlock_Fields) {
            arrayList.add(agentBlock.AgentID);
        }
        this.userManager.getChatterList().getFriendManager().setUsersOnline(arrayList, true);
    }

    public void HandlePayPriceReply(PayPriceReply payPriceReply) {
        SLObjectInfo sLObjectInfo = (SLObjectInfo) this.gridConn.parcelInfo.allObjectsNearby.get(payPriceReply.ObjectData_Field.ObjectID);
        if (sLObjectInfo != null) {
            int i = payPriceReply.ObjectData_Field.DefaultPayPrice;
            int[] iArr = new int[payPriceReply.ButtonData_Fields.size()];
            int i2 = 0;
            while (true) {
                int i3 = i2;
                if (i3 >= payPriceReply.ButtonData_Fields.size()) {
                    break;
                }
                iArr[i3] = ((ButtonData) payPriceReply.ButtonData_Fields.get(i3)).PayButton;
                i2 = i3 + 1;
            }
            sLObjectInfo.setPayInfo(PayInfo.create(i, iArr));
            if (this.userManager != null) {
                this.userManager.getObjectsManager().requestObjectProfileUpdate(sLObjectInfo.localID);
            }
            this.eventBus.publish(new SLObjectPayInfoEvent(sLObjectInfo));
        }
    }

    public void HandleRegionHandshake(RegionHandshake regionHandshake) {
        if (!this.authReply.isTemporary) {
            SLMessage regionHandshakeReply = new RegionHandshakeReply();
            regionHandshakeReply.AgentData_Field.AgentID = this.circuitInfo.agentID;
            regionHandshakeReply.AgentData_Field.SessionID = this.circuitInfo.sessionID;
            regionHandshakeReply.RegionInfo_Field.Flags = 0;
            if (!(this.gridConn == null || this.gridConn.parcelInfo == null)) {
                this.gridConn.parcelInfo.terrainData.ApplyRegionInfo(regionHandshake.RegionInfo_Field);
            }
            SendMessage(regionHandshakeReply);
            this.regionName = SLMessage.stringFromVariableOEM(regionHandshake.RegionInfo_Field.SimName);
            if (!(regionHandshake.RegionInfo2_Field == null || regionHandshake.RegionInfo2_Field.RegionID == null)) {
                this.regionID = regionHandshake.RegionInfo2_Field.RegionID;
            }
            this.isEstateManager = regionHandshake.RegionInfo_Field.IsEstateManager;
            this.agentNameSubscription = this.userManager.getUserNames().subscribe(this.circuitInfo.agentID, new -$Lambda$K1xWCpEh0d4XNuVVYxGUJwEFRxU(this));
            if (this.eventBus != null) {
                this.eventBus.publish(new SLRegionInfoChangedEvent());
            }
        }
    }

    public void HandleScriptDialog(ScriptDialog scriptDialog) {
        boolean z;
        String[] strArr;
        int i = 0;
        if (scriptDialog.Buttons_Fields.size() > 0) {
            String[] strArr2 = new String[scriptDialog.Buttons_Fields.size()];
            int i2 = 0;
            for (Buttons buttons : scriptDialog.Buttons_Fields) {
                strArr2[i2] = SLMessage.stringFromVariableUTF(buttons.ButtonLabel);
                if (strArr2[i2].equals("!!llTextBox!!")) {
                    i = i2;
                    z = true;
                    strArr = strArr2;
                    break;
                }
                i2++;
            }
            z = false;
            strArr = strArr2;
        } else {
            strArr = null;
            z = false;
        }
        if (z) {
            HandleChatEvent(this.localChatterID, new SLChatTextBoxDialog(scriptDialog, this.agentUUID, i), true);
        } else {
            HandleChatEvent(this.localChatterID, new SLChatScriptDialog(scriptDialog, this.agentUUID, strArr), true);
        }
    }

    public void HandleSimulatorViewerTimeMessage(SimulatorViewerTimeMessage simulatorViewerTimeMessage) {
        if (!this.authReply.isTemporary && this.gridConn != null && this.gridConn.parcelInfo != null) {
            float f = (simulatorViewerTimeMessage.TimeInfo_Field.SunPhase / 6.2831855f) + 0.25f;
            this.gridConn.parcelInfo.setSunHour((float) (((double) f) - Math.floor((double) f)));
        }
    }

    public void HandleTeleportFailed(TeleportFailed teleportFailed) {
        Debug.Log("TeleportFailed: reason = " + SLMessage.stringFromVariableOEM(teleportFailed.Info_Field.Reason));
        this.teleportRequestSent = false;
        this.eventBus.publish(new SLTeleportResultEvent(false, SLMessage.stringFromVariableOEM(teleportFailed.Info_Field.Reason)));
    }

    public void HandleTeleportLocal(TeleportLocal teleportLocal) {
        this.teleportRequestSent = false;
        this.eventBus.publish(new SLTeleportResultEvent(true, null));
    }

    public void HandleTeleportProgress(TeleportProgress teleportProgress) {
        Debug.Log("Teleport progress: flags = " + teleportProgress.Info_Field.TeleportFlags + ", progress = " + SLMessage.stringFromVariableOEM(teleportProgress.Info_Field.Message));
    }

    public void HandleTeleportStart(TeleportStart teleportStart) {
        Debug.Log("TeleportStart: flags = " + teleportStart.Info_Field.TeleportFlags);
    }

    public void OfferInventoryItem(UUID uuid, SLInventoryEntry sLInventoryEntry) {
        this.userManager.getInventoryManager().getExecutor().execute(new com.lumiyaviewer.lumiya.slproto.-$Lambda$K1xWCpEh0d4XNuVVYxGUJwEFRxU.AnonymousClass1(this, sLInventoryEntry, uuid));
    }

    public void OfferTeleport(UUID uuid, String str) {
        SLMessage startLure = new StartLure();
        startLure.AgentData_Field.AgentID = this.circuitInfo.agentID;
        startLure.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        startLure.Info_Field.Message = SLMessage.stringToVariableUTF(str);
        TargetData targetData = new TargetData();
        targetData.TargetID = uuid;
        startLure.TargetData_Fields.add(targetData);
        startLure.isReliable = true;
        SendMessage(startLure);
    }

    public void OnCapsEvent(CapsEvent capsEvent) {
        try {
            this.capsEventQueue.add(capsEvent);
            this.selector.wakeup();
        } catch (Exception e) {
        }
    }

    public void ProcessIdle() {
        if (this.doingObjectSelection && System.currentTimeMillis() > this.lastObjectSelection + 15000) {
            this.doingObjectSelection = false;
            ProcessObjectSelectionTimeout();
        }
        if (!this.teleportRequestSent && getNeedObjectNames() && (this.doingObjectSelection ^ 1) != 0 && System.currentTimeMillis() >= this.lastObjectSelection + 500) {
            ProcessObjectSelection();
        }
        if (!this.agentPaused) {
            long currentTimeMillis = System.currentTimeMillis();
            if (GridConnectionService.hasVisibleActivities()) {
                this.lastVisibleActivities = currentTimeMillis;
            } else if (currentTimeMillis >= this.lastVisibleActivities + 10000) {
                DoAgentPause();
            }
        }
        if (this.objectPropertiesRateLimiter != null) {
            this.objectPropertiesRateLimiter.firePending();
        }
    }

    public void ProcessNetworkError() {
        super.ProcessNetworkError();
        Debug.Printf("Network: Network error.", new Object[0]);
        if (this.modules != null) {
            this.modules.avatarControl.setEnableAgentUpdates(false);
        }
        if (!this.authReply.isTemporary) {
            this.gridConn.processDisconnect(false, "Network connection lost.");
        }
    }

    public void ProcessTimeout() {
        super.ProcessTimeout();
        if (this.modules != null) {
            this.modules.avatarControl.setEnableAgentUpdates(false);
        }
        if (!this.authReply.isTemporary) {
            this.gridConn.processDisconnect(false, "Connection has timed out.");
        }
    }

    public void ProcessWakeup() {
        super.ProcessWakeup();
        while (true) {
            try {
                CapsEvent capsEvent = (CapsEvent) this.capsEventQueue.poll();
                if (capsEvent == null) {
                    break;
                }
                HandleCapsEvent(capsEvent);
            } catch (Exception e) {
            }
        }
        ProcessIdle();
    }

    public void RemoveFriend(UUID uuid) {
        SLMessage terminateFriendship = new TerminateFriendship();
        terminateFriendship.AgentData_Field.AgentID = this.circuitInfo.agentID;
        terminateFriendship.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        terminateFriendship.ExBlock_Field.OtherID = uuid;
        terminateFriendship.isReliable = true;
        SendMessage(terminateFriendship);
        this.userManager.getChatterList().getFriendManager().removeFriend(uuid);
    }

    void RequestObjectName(SLObjectInfo sLObjectInfo) {
        if (!(sLObjectInfo.getId() == null || this.objectNamesRequested.containsKey(sLObjectInfo.getId()) || (this.forceNeedObjectNames.containsKey(sLObjectInfo.getId()) ^ 1) == 0)) {
            this.forceNeedObjectNames.put(sLObjectInfo.getId(), sLObjectInfo);
        }
        TryWakeUp();
    }

    public void RequestTeleport(UUID uuid, String str) {
        SendInstantMessage(uuid, str, 26);
    }

    public boolean RestartRegion(int i) {
        if (!this.isEstateManager) {
            return false;
        }
        SendEstateOwnerMessage("restart", new String[]{Integer.toString(i)});
        return true;
    }

    /* DevToolsApp WARNING: Removed duplicated region for block: B:24:0x015a  */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:10:0x0023  */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:25:0x015d  */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:13:0x002d  */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:18:0x0047  */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:21:0x014c  */
    public void RezObject(com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry r8) {
    public void RezObject(com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry inventoryEntry) {
        UUID groupID = com.lumiyaviewer.lumiya.utils.UUIDPool.ZeroUUID;
        UUID parcelOwnerID = null;
        
        // Get parcel information to determine group ownership
        if (this.userManager != null) {
            var locationInfo = this.userManager.getCurrentLocationInfoSnapshot();
            if (locationInfo != null) {
                var parcelData = locationInfo.parcelData();
                if (parcelData != null && parcelData.isGroupOwned()) {
                    parcelOwnerID = parcelData.getOwnerID();
                }
            }
        }
        
        // Determine the group to use for rezzing
        if (parcelOwnerID != null && !com.lumiyaviewer.lumiya.utils.UUIDPool.ZeroUUID.equals(parcelOwnerID)) {
            groupID = parcelOwnerID;
        } else {
            // Check if we're in a group that can rez objects
            if (this.userManager != null) {
                var groupManager = this.userManager.getChatterList().getGroupManager();
                var avatarGroupList = groupManager.getAvatarGroupList();
                if (avatarGroupList != null && avatarGroupList.Groups.containsKey(groupID)) {
                    // Use the group ID
                } else {
                    groupID = getActiveGroupID();
                }
            } else {
                groupID = getActiveGroupID();
            }
        }
        
        if (groupID == null) {
            groupID = com.lumiyaviewer.lumiya.utils.UUIDPool.ZeroUUID;
        }
        
        // Create the RezObject message
        com.lumiyaviewer.lumiya.slproto.messages.RezObject rezMessage = 
            new com.lumiyaviewer.lumiya.slproto.messages.RezObject();
        
        // Set agent data
        rezMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        rezMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        rezMessage.AgentData_Field.GroupID = groupID;
        
        // Set rez data
        rezMessage.RezData_Field.FromTaskID = com.lumiyaviewer.lumiya.utils.UUIDPool.ZeroUUID;
        rezMessage.RezData_Field.BypassRaycast = true;
        
        // Set position and rotation based on avatar location
        var agentPosition = this.modules.avatarControl.getAgentPosition().getPosition();
        rezMessage.RezData_Field.RayStart = agentPosition;
        
        // Calculate end position (1.5 meters in front of avatar)
        var agentHeading = this.getModules().avatarControl.getAgentHeading();
        rezMessage.RezData_Field.RayEnd = agentPosition.getRotatedOffset(1.5f, agentHeading);
        
        rezMessage.RezData_Field.RayEndIsIntersection = true;
        rezMessage.RezData_Field.RayTargetID = com.lumiyaviewer.lumiya.utils.UUIDPool.ZeroUUID;
        rezMessage.RezData_Field.RezSelected = false;
        rezMessage.RezData_Field.RemoveItem = false;
        rezMessage.RezData_Field.ItemFlags = 0;
        rezMessage.RezData_Field.GroupMask = inventoryEntry.groupMask;
        rezMessage.RezData_Field.EveryoneMask = inventoryEntry.everyoneMask;
        rezMessage.RezData_Field.NextOwnerMask = inventoryEntry.nextOwnerMask;
        
        // Set inventory data
        rezMessage.InventoryData_Field.ItemID = inventoryEntry.uuid;
        rezMessage.InventoryData_Field.FolderID = inventoryEntry.parentUUID;
        rezMessage.InventoryData_Field.CreatorID = inventoryEntry.creatorUUID;
        rezMessage.InventoryData_Field.OwnerID = inventoryEntry.ownerUUID;
        rezMessage.InventoryData_Field.GroupID = inventoryEntry.groupUUID;
        rezMessage.InventoryData_Field.BaseMask = inventoryEntry.baseMask;
        rezMessage.InventoryData_Field.OwnerMask = inventoryEntry.ownerMask;
        rezMessage.InventoryData_Field.GroupMask = inventoryEntry.groupMask;
        rezMessage.InventoryData_Field.EveryoneMask = inventoryEntry.everyoneMask;
        rezMessage.InventoryData_Field.NextOwnerMask = inventoryEntry.nextOwnerMask;
        rezMessage.InventoryData_Field.GroupOwned = inventoryEntry.isGroupOwned;
        rezMessage.InventoryData_Field.TransactionID = java.util.UUID.randomUUID();
        rezMessage.InventoryData_Field.Type = inventoryEntry.assetType;
        rezMessage.InventoryData_Field.InvType = inventoryEntry.invType;
        rezMessage.InventoryData_Field.Flags = inventoryEntry.flags;
        rezMessage.InventoryData_Field.SaleType = inventoryEntry.saleType;
        rezMessage.InventoryData_Field.SalePrice = inventoryEntry.salePrice;
        rezMessage.InventoryData_Field.Name = SLMessage.stringToVariableOEM(inventoryEntry.name);
        rezMessage.InventoryData_Field.Description = SLMessage.stringToVariableOEM(inventoryEntry.description);
        rezMessage.InventoryData_Field.CreationDate = inventoryEntry.creationDate;
        rezMessage.InventoryData_Field.CRC = 0;
        
        rezMessage.isReliable = true;
        
        // Set up event listener if the item is not copy-ok (will be removed from inventory)
        if ((inventoryEntry.ownerMask & 0x8000) == 0) { // PERM_COPY bit
            UUID folderID = inventoryEntry.parentUUID;
            rezMessage.setEventListener(new com.lumiyaviewer.lumiya.slproto.SLAgentCircuit.RezObjectEventListener(folderID));
        }
        
        SendMessage(rezMessage);
    }
    
    // Inner class for handling rez object events
    private class RezObjectEventListener implements com.lumiyaviewer.lumiya.slproto.messages.SLMessageEventListener {
        private final UUID folderID;
        
        public RezObjectEventListener(UUID folderID) {
            this.folderID = folderID;
        }
        
        @Override
        public void onMessageAcknowledged() {
            // Object was successfully rezzed, refresh inventory if needed
            if (userManager != null) {
                userManager.getInventoryManager().requestFolderContents(folderID);
            }
        }
        
        @Override
        public void onMessageTimeout() {
            // Object rezzing failed
            Debug.Log("RezObject message timed out");
        }
    }
    }

    public void SendChatMessage(@Nonnull ChatterID chatterID, String str) {
        switch (m39-getcom-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues()[chatterID.getChatterType().ordinal()]) {
            case 1:
                SendGroupInstantMessage(chatterID.getOptionalChatterUUID(), str);
                return;
            case 2:
                SendLocalChatMessage(str);
                return;
            case 3:
                SendInstantMessage(chatterID.getOptionalChatterUUID(), str);
                return;
            default:
                return;
        }
    }

    public void SendGenericMessage(String str, String[] strArr) {
        SLMessage genericMessage = new GenericMessage();
        genericMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        genericMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        genericMessage.AgentData_Field.TransactionID = new UUID(0, 0);
        genericMessage.MethodData_Field.Method = SLMessage.stringToVariableOEM(str);
        genericMessage.MethodData_Field.Invoice = new UUID(0, 0);
        for (String str2 : strArr) {
            GenericMessage.ParamList paramList = new GenericMessage.ParamList();
            paramList.Parameter = SLMessage.stringToVariableOEM(str2);
            genericMessage.ParamList_Fields.add(paramList);
        }
        genericMessage.isReliable = true;
        SendMessage(genericMessage);
    }

    public void SendGroupInstantMessage(UUID uuid, String str) {
        SLMessage improvedInstantMessage = new ImprovedInstantMessage();
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        improvedInstantMessage.MessageBlock_Field.FromGroup = false;
        improvedInstantMessage.MessageBlock_Field.ToAgentID = uuid;
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0;
        improvedInstantMessage.MessageBlock_Field.RegionID = new UUID(0, 0);
        improvedInstantMessage.MessageBlock_Field.Position = this.modules.avatarControl.getAgentPosition().getPosition();
        improvedInstantMessage.MessageBlock_Field.Offline = 0;
        improvedInstantMessage.MessageBlock_Field.Dialog = 17;
        improvedInstantMessage.MessageBlock_Field.ID = uuid;
        improvedInstantMessage.MessageBlock_Field.Timestamp = 0;
        improvedInstantMessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo");
        improvedInstantMessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF(str);
        improvedInstantMessage.MessageBlock_Field.BinaryBucket = new byte[1];
        improvedInstantMessage.isReliable = true;
        synchronized (this.startedGroupSessions) {
            if (this.startedGroupSessions.contains(uuid)) {
                SendMessage(improvedInstantMessage);
            } else {
                SendGroupSessionStart(uuid);
                this.pendingGroupMessages.add(improvedInstantMessage);
            }
        }
    }

    public boolean SendInstantMessage(UUID uuid, String str) {
        return SendInstantMessage(uuid, str, 0);
    }

    public void SendLocalChatMessage(String str) {
        int i = 0;
        if (str.startsWith("/")) {
            int i2 = 1;
            int i3 = 0;
            while (i2 < str.length() && Character.isDigit(str.charAt(i2))) {
                i3++;
                i2++;
            }
            if (i3 >= 0) {
                try {
                    i = Integer.parseInt(str.substring(1, i3 + 1));
                    str = str.substring(i3 + 1).trim();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (getModules().rlvController.onSendLocalChat(i, str)) {
            SLMessage chatFromViewer = new ChatFromViewer();
            chatFromViewer.AgentData_Field.AgentID = this.circuitInfo.agentID;
            chatFromViewer.AgentData_Field.SessionID = this.circuitInfo.sessionID;
            chatFromViewer.ChatData_Field.Channel = i;
            chatFromViewer.ChatData_Field.Type = 1;
            chatFromViewer.ChatData_Field.Message = SLMessage.stringToVariableUTF(str);
            chatFromViewer.isReliable = true;
            SendMessage(chatFromViewer);
        }
    }

    void SendLogoutRequest() {
        Debug.Log("Logout: Sending logout request.");
        this.modules.avatarControl.setEnableAgentUpdates(false);
        SLMessage logoutRequest = new LogoutRequest();
        logoutRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        logoutRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        logoutRequest.isReliable = true;
        logoutRequest.setEventListener(new SLMessageEventListener() {
            public void onMessageAcknowledged(SLMessage sLMessage) {
                Debug.Log("Logout: Logout request acknowledged.");
                SLAgentCircuit.this.gridConn.processDisconnect(true, "Logged out.");
            }

            public void onMessageTimeout(SLMessage sLMessage) {
                Debug.Log("Logout: LogoutRequest timed out!");
                SLAgentCircuit.this.gridConn.processDisconnect(false, "Logout request has timed out.");
            }
        });
        SendMessage(logoutRequest);
    }

    public void SendScriptDialogReply(UUID uuid, int i, int i2, String str) {
        SLMessage scriptDialogReply = new ScriptDialogReply();
        scriptDialogReply.AgentData_Field.AgentID = this.circuitInfo.agentID;
        scriptDialogReply.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        scriptDialogReply.isReliable = true;
        scriptDialogReply.Data_Field.ObjectID = uuid;
        scriptDialogReply.Data_Field.ChatChannel = i;
        scriptDialogReply.Data_Field.ButtonIndex = i2;
        scriptDialogReply.Data_Field.ButtonLabel = SLMessage.stringToVariableUTF(str);
        SendMessage(scriptDialogReply);
    }

    void SendUseCode() {
        Debug.Printf("Using circuitCode: %d", Integer.valueOf(this.circuitInfo.circuitCode));
        SLMessage useCircuitCode = new UseCircuitCode();
        useCircuitCode.CircuitCode_Field.Code = this.circuitInfo.circuitCode;
        useCircuitCode.CircuitCode_Field.SessionID = this.circuitInfo.sessionID;
        useCircuitCode.CircuitCode_Field.ID = this.circuitInfo.agentID;
        useCircuitCode.isReliable = true;
        useCircuitCode.setEventListener(new SLMessageEventListener() {
            public void onMessageAcknowledged(SLMessage sLMessage) {
                Debug.Log("SLAgentCircuit: UseCircuitCode acknowledged.");
                if (!SLAgentCircuit.this.authReply.isTemporary) {
                    if (SLAgentCircuit.this.authReply.fromTeleport) {
                        Debug.Log("SLAgentCircuit: Ack from teleport, sending Teleport success.");
                        SLAgentCircuit.this.eventBus.publish(new SLTeleportResultEvent(true, null));
                    } else {
                        SLAgentCircuit.this.gridConn.notifyLoginSuccess();
                    }
                    SLAgentCircuit.this.SendCompleteAgentMovement();
                    if (SLAgentCircuit.this.modules != null) {
                        SLAgentCircuit.this.modules.HandleCircuitReady();
                    }
                }
            }

            public void onMessageTimeout(SLMessage sLMessage) {
                if (SLAgentCircuit.this.authReply.fromTeleport) {
                    SLAgentCircuit.this.eventBus.publish(new SLTeleportResultEvent(false, "Timed out while connecting to the simulator."));
                } else {
                    SLAgentCircuit.this.gridConn.notifyLoginError("Timed out while connecting to the simulator.");
                }
            }
        });
        SendMessage(useCircuitCode);
    }

    public void StartGroupSessionForVoice(UUID uuid) {
        Object obj = null;
        synchronized (this.startedGroupSessions) {
            if (!this.startedGroupSessions.contains(uuid)) {
                SendGroupSessionStart(uuid);
                obj = 1;
            }
        }
        if (obj == null) {
            this.modules.voice.onGroupSessionReady(uuid);
        }
    }

    public void TeleportToGlobalPosition(LLVector3 lLVector3) {
        int floor = (int) Math.floor((double) lLVector3.x);
        int floor2 = (int) Math.floor((double) lLVector3.y);
        floor -= floor % 256;
        long j = ((long) (floor2 - (floor2 % 256))) | (((long) floor) << 32);
        LLVector3 lLVector32 = new LLVector3(lLVector3.x % 256.0f, lLVector3.y % 256.0f, lLVector3.z);
        LLVector3 lLVector33 = new LLVector3(lLVector32);
        lLVector33.x += 1.0f;
        Debug.Printf("regionHandle = %s, globalPos = %s", Long.toHexString(j), lLVector3);
        this.teleportRequestSent = true;
        SLMessage teleportLocationRequest = new TeleportLocationRequest();
        teleportLocationRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        teleportLocationRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        teleportLocationRequest.Info_Field.RegionHandle = j;
        teleportLocationRequest.Info_Field.Position = lLVector32;
        teleportLocationRequest.Info_Field.LookAt = lLVector33;
        teleportLocationRequest.isReliable = true;
        teleportLocationRequest.setEventListener(new SLMessageEventListener() {
            public void onMessageAcknowledged(SLMessage sLMessage) {
            }

            public void onMessageTimeout(SLMessage sLMessage) {
                SLAgentCircuit.this.eventBus.publish(new SLTeleportResultEvent(false, "Teleport request has timed out."));
            }
        });
        SendMessage(teleportLocationRequest);
    }

    public void TeleportToLandmarkAsset(UUID uuid) {
        if (getModules().rlvController.canTeleportToLandmark()) {
            this.teleportRequestSent = true;
            SLMessage teleportLandmarkRequest = new TeleportLandmarkRequest();
            teleportLandmarkRequest.Info_Field.AgentID = this.circuitInfo.agentID;
            teleportLandmarkRequest.Info_Field.SessionID = this.circuitInfo.sessionID;
            teleportLandmarkRequest.Info_Field.LandmarkID = uuid;
            teleportLandmarkRequest.isReliable = true;
            teleportLandmarkRequest.setEventListener(new SLMessageEventListener() {
                public void onMessageAcknowledged(SLMessage sLMessage) {
                }

                public void onMessageTimeout(SLMessage sLMessage) {
                    SLAgentCircuit.this.eventBus.publish(new SLTeleportResultEvent(false, "Teleport request has timed out."));
                }
            });
            SendMessage(teleportLandmarkRequest);
        }
    }

    public boolean TeleportToLocalPosition(LLVector3 lLVector3) {
        if (this.regionID == null) {
            return false;
        }
        Debug.Printf("Teleport: localPos = %s, regionHandle = %d", lLVector3.toString(), Long.valueOf(this.regionHandle));
        this.teleportRequestSent = true;
        SLMessage teleportLocationRequest = new TeleportLocationRequest();
        teleportLocationRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        teleportLocationRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        teleportLocationRequest.Info_Field.RegionHandle = this.regionHandle;
        teleportLocationRequest.Info_Field.Position = lLVector3;
        teleportLocationRequest.Info_Field.LookAt = new LLVector3(lLVector3);
        LLVector3 lLVector32 = teleportLocationRequest.Info_Field.LookAt;
        lLVector32.x += 10.0f;
        teleportLocationRequest.isReliable = true;
        teleportLocationRequest.setEventListener(new SLMessageEventListener() {
            public void onMessageAcknowledged(SLMessage sLMessage) {
            }

            public void onMessageTimeout(SLMessage sLMessage) {
                SLAgentCircuit.this.eventBus.publish(new SLTeleportResultEvent(false, "Teleport request has timed out."));
            }
        });
        SendMessage(teleportLocationRequest);
        return true;
    }

    public void TeleportToLure(UUID uuid) {
        this.teleportRequestSent = true;
        SLMessage teleportLureRequest = new TeleportLureRequest();
        teleportLureRequest.Info_Field.AgentID = this.circuitInfo.agentID;
        teleportLureRequest.Info_Field.SessionID = this.circuitInfo.sessionID;
        teleportLureRequest.Info_Field.LureID = uuid;
        teleportLureRequest.isReliable = true;
        teleportLureRequest.setEventListener(new SLMessageEventListener() {
            public void onMessageAcknowledged(SLMessage sLMessage) {
            }

            public void onMessageTimeout(SLMessage sLMessage) {
                SLAgentCircuit.this.eventBus.publish(new SLTeleportResultEvent(false, "Teleport request has timed out."));
            }
        });
        SendMessage(teleportLureRequest);
    }

    public void TeleportToRegion(long j, int i, int i2, int i3) {
        if (getModules().rlvController.canTeleportToLocation()) {
            Debug.Log("TeleportToRegion: regionHandle = " + Long.toHexString(j) + ", pos = (" + i + ", " + i2 + ", " + i3 + ")");
            this.teleportRequestSent = true;
            SLMessage teleportLocationRequest = new TeleportLocationRequest();
            teleportLocationRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
            teleportLocationRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
            teleportLocationRequest.Info_Field.RegionHandle = j;
            teleportLocationRequest.Info_Field.Position = new LLVector3((float) i, (float) i2, (float) i3);
            teleportLocationRequest.Info_Field.LookAt = new LLVector3(0.0f, 1.0f, 0.0f);
            teleportLocationRequest.isReliable = true;
            teleportLocationRequest.setEventListener(new SLMessageEventListener() {
                public void onMessageAcknowledged(SLMessage sLMessage) {
                }

                public void onMessageTimeout(SLMessage sLMessage) {
                    SLAgentCircuit.this.eventBus.publish(new SLTeleportResultEvent(false, "Teleport request has timed out."));
                }
            });
            SendMessage(teleportLocationRequest);
        }
    }

    public void TouchObject(int i) {
        SLMessage objectGrab = new ObjectGrab();
        objectGrab.AgentData_Field.AgentID = this.circuitInfo.agentID;
        objectGrab.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        objectGrab.ObjectData_Field.LocalID = i;
        objectGrab.ObjectData_Field.GrabOffset = new LLVector3();
        objectGrab.isReliable = true;
        SendMessage(objectGrab);
        objectGrab = new ObjectDeGrab();
        objectGrab.AgentData_Field.AgentID = this.circuitInfo.agentID;
        objectGrab.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        objectGrab.ObjectData_Field.LocalID = i;
        objectGrab.isReliable = true;
        SendMessage(objectGrab);
    }

    public void TouchObjectFace(SLObjectInfo sLObjectInfo, int i, float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        Debug.Printf("Touch: Object %d, face %d, pos (%f, %f, %f), uv (%f, %f)", Integer.valueOf(sLObjectInfo.localID), Integer.valueOf(i), Float.valueOf(f), Float.valueOf(f2), Float.valueOf(f3), Float.valueOf(f4), Float.valueOf(f5));
        SLMessage objectGrab = new ObjectGrab();
        objectGrab.AgentData_Field.AgentID = this.circuitInfo.agentID;
        objectGrab.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        objectGrab.ObjectData_Field.LocalID = sLObjectInfo.localID;
        objectGrab.ObjectData_Field.GrabOffset = new LLVector3();
        SurfaceInfo surfaceInfo = new SurfaceInfo();
        surfaceInfo.FaceIndex = i;
        surfaceInfo.Position = new LLVector3(f, f2, f3);
        surfaceInfo.UVCoord = new LLVector3(f4, f5, 0.0f);
        surfaceInfo.STCoord = new LLVector3(f6, f7, 0.0f);
        surfaceInfo.Normal = new LLVector3(1.0f, 0.0f, 0.0f);
        surfaceInfo.Binormal = new LLVector3(0.0f, 0.0f, 1.0f);
        objectGrab.SurfaceInfo_Fields.add(surfaceInfo);
        objectGrab.isReliable = true;
        SendMessage(objectGrab);
        objectGrab = new ObjectDeGrab();
        objectGrab.AgentData_Field.AgentID = this.circuitInfo.agentID;
        objectGrab.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        objectGrab.ObjectData_Field.LocalID = sLObjectInfo.localID;
        objectGrab.isReliable = true;
        SendMessage(objectGrab);
    }

    public void TryWakeUp() {
        try {
            this.selector.wakeup();
        } catch (Exception e) {
        }
    }

    public void UnpauseAgent() {
        this.lastVisibleActivities = System.currentTimeMillis();
        if (this.agentPaused) {
            DoAgentResume();
        }
    }

    @Nullable
    public LLVector3d getAgentGlobalPosition() {
        if (this.modules == null) {
            return null;
        }
        LLVector3 position = this.modules.avatarControl.getAgentPosition().getPosition();
        int i = (int) ((this.regionHandle >> 32) & 4294967295L);
        int i2 = (int) (this.regionHandle & 4294967295L);
        LLVector3d lLVector3d = new LLVector3d();
        lLVector3d.x = ((double) i) + ((double) position.x);
        lLVector3d.y = ((double) i2) + ((double) position.y);
        lLVector3d.z = (double) position.z;
        return lLVector3d;
    }

    @SuppressLint({"DefaultLocale"})
    @Nullable
    public String getAgentSLURL() {
        if (this.modules == null || !Objects.equal(this.authReply.loginURL, "https://login.agni.lindenlab.com/cgi-bin/login.cgi") || this.regionName == null) {
            return null;
        }
        LLVector3 position = this.modules.avatarControl.getAgentPosition().getPosition();
        try {
            return String.format("http://maps.secondlife.com/secondlife/%s/%d/%d/%d", new Object[]{URLEncoder.encode(this.regionName, "UTF-8"), Integer.valueOf((int) position.x), Integer.valueOf((int) position.y), Integer.valueOf((int) position.z)});
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Nonnull
    public UUID getAgentUUID() {
        return this.agentUUID;
    }

    public SLCaps getCaps() {
        return this.caps;
    }

    public boolean getIsEstateManager() {
        return this.isEstateManager;
    }

    public ChatterID getLocalChatterID() {
        return this.localChatterID;
    }

    public SLModules getModules() {
        return this.modules;
    }

    public SLObjectProfileData getObjectProfile(int i) {
        SLObjectInfo objectInfo = this.gridConn.parcelInfo.getObjectInfo(i);
        if (objectInfo == null) {
            return null;
        }
        SLObjectProfileData create = SLObjectProfileData.create(objectInfo);
        if (!(create.name().isPresent() || (objectInfo.isDead ^ 1) == 0)) {
            RequestObjectName(objectInfo);
        }
        return create;
    }

    public String getRegionName() {
        return this.regionName;
    }

    public UUID getSessionID() {
        return this.circuitInfo.sessionID;
    }

    public Boolean isUserTyping(UUID uuid) {
        return Boolean.valueOf(this.typingUsers.contains(uuid));
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_SLAgentCircuit_14593 */
    /* synthetic */ void m40lambda$-com_lumiyaviewer_lumiya_slproto_SLAgentCircuit_14593(UserName userName) {
        this.agentUserName.set(userName);
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_SLAgentCircuit_77024 */
    /* synthetic */ void m41lambda$-com_lumiyaviewer_lumiya_slproto_SLAgentCircuit_77024(SLInventoryEntry sLInventoryEntry, UUID uuid) {
        Iterable<SLInventoryEntry> arrayList = new ArrayList();
        arrayList.add(sLInventoryEntry);
        if (sLInventoryEntry.isFolder) {
            arrayList.addAll(this.modules.inventory.CollectGiveableItems(sLInventoryEntry));
        }
        SLMessage improvedInstantMessage = new ImprovedInstantMessage();
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        improvedInstantMessage.MessageBlock_Field.FromGroup = false;
        improvedInstantMessage.MessageBlock_Field.ToAgentID = uuid;
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0;
        improvedInstantMessage.MessageBlock_Field.RegionID = new UUID(0, 0);
        improvedInstantMessage.MessageBlock_Field.Position = new LLVector3();
        improvedInstantMessage.MessageBlock_Field.Offline = 0;
        improvedInstantMessage.MessageBlock_Field.Dialog = 4;
        improvedInstantMessage.MessageBlock_Field.ID = UUID.randomUUID();
        improvedInstantMessage.MessageBlock_Field.Timestamp = 0;
        improvedInstantMessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo");
        improvedInstantMessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF(sLInventoryEntry.name);
        ByteBuffer wrap = ByteBuffer.wrap(new byte[(arrayList.size() * 17)]);
        wrap.order(ByteOrder.BIG_ENDIAN);
        for (SLInventoryEntry sLInventoryEntry2 : arrayList) {
            wrap.put((byte) (sLInventoryEntry2.isFolder ? SLAssetType.AT_CATEGORY.getTypeCode() : sLInventoryEntry2.assetType));
            wrap.putLong(sLInventoryEntry2.uuid.getMostSignificantBits());
            wrap.putLong(sLInventoryEntry2.uuid.getLeastSignificantBits());
        }
        wrap.position(0);
        improvedInstantMessage.MessageBlock_Field.BinaryBucket = wrap.array();
        improvedInstantMessage.isReliable = true;
        SendMessage(improvedInstantMessage);
        HandleChatEvent(ChatterID.getUserChatterID(this.agentUUID, uuid), new SLChatInventoryItemOfferedByYouEvent(this.agentUUID, sLInventoryEntry.name), false);
    }

    void processMyAttachmentUpdate(SLObjectInfo sLObjectInfo) {
        if (!(sLObjectInfo == null || sLObjectInfo.nameKnown || (sLObjectInfo.isDead ^ 1) == 0)) {
            RequestObjectName(sLObjectInfo);
        }
        getModules().avatarAppearance.UpdateMyAttachments();
    }

    public void sendTypingNotify(UUID uuid, boolean z) {
        SendInstantMessage(uuid, "", z ? 41 : 42);
    }
}
