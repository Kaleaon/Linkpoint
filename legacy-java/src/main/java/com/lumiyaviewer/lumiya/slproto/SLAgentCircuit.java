package com.lumiyaviewer.lumiya.slproto;

import android.annotation.SuppressLint;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.logging.nano.Vr;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GridConnectionService;
import com.lumiyaviewer.lumiya.dao.UserName;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.eventbus.EventRateLimiter;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatBalanceChangedEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatFriendshipOfferedEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatFriendshipResultEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatGroupInvitationEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatInventoryItemOfferedByGroupNoticeEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatInventoryItemOfferedByYouEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatInventoryItemOfferedEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatLureEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatLureRequestEvent;
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
import com.lumiyaviewer.lumiya.slproto.messages.EstateOwnerMessage;
import com.lumiyaviewer.lumiya.slproto.messages.GenericMessage;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedTerseObjectUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.KillObject;
import com.lumiyaviewer.lumiya.slproto.messages.LayerData;
import com.lumiyaviewer.lumiya.slproto.messages.LoadURL;
import com.lumiyaviewer.lumiya.slproto.messages.LogoutRequest;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectBuy;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectDeGrab;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectGrab;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectProperties;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectSelect;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCached;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCompressed;
import com.lumiyaviewer.lumiya.slproto.messages.OfflineNotification;
import com.lumiyaviewer.lumiya.slproto.messages.OnlineNotification;
import com.lumiyaviewer.lumiya.slproto.messages.PayPriceReply;
import com.lumiyaviewer.lumiya.slproto.messages.RegionHandshake;
import com.lumiyaviewer.lumiya.slproto.messages.RegionHandshakeReply;
import com.lumiyaviewer.lumiya.slproto.messages.RequestMultipleObjects;
import com.lumiyaviewer.lumiya.slproto.messages.RequestPayPrice;
import com.lumiyaviewer.lumiya.slproto.messages.RetrieveInstantMessages;
import com.lumiyaviewer.lumiya.slproto.messages.RezObject;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptDialog;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptDialogReply;
import com.lumiyaviewer.lumiya.slproto.messages.SimulatorViewerTimeMessage;
import com.lumiyaviewer.lumiya.slproto.messages.StartLure;
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
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUser;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
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

/* loaded from: classes.dex */
public class SLAgentCircuit extends SLThreadingCircuit implements SLCapEventQueue.ICapsEventHandler {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f60x94bee140 = null;

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f61xb1d54699 = null;
    private Subscription agentNameSubscription;
    private boolean agentPaused;

    @Nonnull
    private final UUID agentUUID;
    private final AtomicReference<UserName> agentUserName;
    private final SLCaps caps;
    private final ConcurrentLinkedQueue<SLCapEventQueue.CapsEvent> capsEventQueue;
    private boolean doingObjectSelection;
    private final EventBus eventBus;
    private final Map<UUID, SLObjectInfo> forceNeedObjectNames;
    private boolean isEstateManager;
    private long lastObjectSelection;
    private int lastPauseId;
    private long lastVisibleActivities;
    private final ChatterID localChatterID;
    private final SLModules modules;
    private final Map<UUID, SLObjectInfo> objectNamesRequested;
    private final EventRateLimiter objectPropertiesRateLimiter;
    private List<ImprovedInstantMessage> pendingGroupMessages;
    private long regionHandle;
    private UUID regionID;
    private String regionName;
    private final Set<UUID> startedGroupSessions;
    private boolean teleportRequestSent;
    private final Set<UUID> typingUsers;
    private final UserManager userManager;

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m133x4d1ccd1c() {
        if (f60x94bee140 != null) {
            return f60x94bee140;
        }
        int[] iArr = new int[SLCapEventQueue.CapsEventType.valuesCustom().length];
        try {
            iArr[SLCapEventQueue.CapsEventType.AgentGroupDataUpdate.ordinal()] = 9;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[SLCapEventQueue.CapsEventType.AvatarGroupsReply.ordinal()] = 10;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[SLCapEventQueue.CapsEventType.BulkUpdateInventory.ordinal()] = 11;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[SLCapEventQueue.CapsEventType.ChatterBoxInvitation.ordinal()] = 1;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[SLCapEventQueue.CapsEventType.ChatterBoxSessionStartReply.ordinal()] = 2;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[SLCapEventQueue.CapsEventType.EstablishAgentCommunication.ordinal()] = 3;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[SLCapEventQueue.CapsEventType.ParcelProperties.ordinal()] = 12;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[SLCapEventQueue.CapsEventType.TeleportFailed.ordinal()] = 4;
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[SLCapEventQueue.CapsEventType.TeleportFinish.ordinal()] = 5;
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[SLCapEventQueue.CapsEventType.UnknownCapsEvent.ordinal()] = 13;
        } catch (NoSuchFieldError e10) {
        }
        f60x94bee140 = iArr;
        return iArr;
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m134x2680ba3d() {
        if (f61xb1d54699 != null) {
            return f61xb1d54699;
        }
        int[] iArr = new int[ChatterID.ChatterType.valuesCustom().length];
        try {
            iArr[ChatterID.ChatterType.Group.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ChatterID.ChatterType.Local.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ChatterID.ChatterType.User.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        f61xb1d54699 = iArr;
        return iArr;
    }

    public SLAgentCircuit(SLGridConnection sLGridConnection, SLCircuitInfo sLCircuitInfo, SLAuthReply sLAuthReply, SLCaps sLCaps, SLTempCircuit sLTempCircuit) throws IOException {
        super(sLGridConnection, sLCircuitInfo, sLAuthReply, sLTempCircuit);
        this.eventBus = EventBus.getInstance();
        this.capsEventQueue = new ConcurrentLinkedQueue<>();
        this.startedGroupSessions = new HashSet();
        this.pendingGroupMessages = new LinkedList();
        this.teleportRequestSent = false;
        this.regionID = null;
        this.regionName = null;
        this.regionHandle = 0L;
        this.isEstateManager = false;
        this.lastObjectSelection = 0L;
        this.doingObjectSelection = false;
        this.objectPropertiesRateLimiter = new EventRateLimiter(this.eventBus, 500L) { // from class: com.lumiyaviewer.lumiya.slproto.SLAgentCircuit.1
            @Override // com.lumiyaviewer.lumiya.eventbus.EventRateLimiter
            protected Object getEventToFire() {
                return null;
            }

            @Override // com.lumiyaviewer.lumiya.eventbus.EventRateLimiter
            protected void onActualFire() {
                SLAgentCircuit.this.notifyObjectPropertiesChange();
            }
        };
        this.objectNamesRequested = new ConcurrentHashMap();
        this.forceNeedObjectNames = new ConcurrentHashMap();
        this.agentPaused = false;
        this.lastVisibleActivities = 0L;
        this.lastPauseId = 0;
        this.agentUserName = new AtomicReference<>(null);
        this.typingUsers = Collections.synchronizedSet(new HashSet());
        this.caps = sLCaps;
        this.agentUUID = sLCircuitInfo.agentID;
        this.localChatterID = ChatterID.getLocalChatterID(this.agentUUID);
        this.lastVisibleActivities = System.currentTimeMillis();
        this.userManager = UserManager.getUserManager(sLCircuitInfo.agentID);
        if (sLCaps == null || !(!sLAuthReply.isTemporary)) {
            this.modules = null;
        } else {
            this.modules = new SLModules(this, sLCaps, sLGridConnection);
        }
        if (!sLAuthReply.isTemporary && this.userManager != null) {
            this.userManager.setActiveAgentCircuit(this);
        }
        if (sLTempCircuit != null) {
            Iterator<T> it = sLTempCircuit.getPendingMessages().iterator();
            while (it.hasNext()) {
                ((SLMessage) it.next()).Handle(this);
            }
        }
    }

    private void DoAgentPause() {
        this.agentPaused = true;
        Debug.Log("AgentPause: Sending agentPause with ID = " + this.lastPauseId);
        AgentPause agentPause = new AgentPause();
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
        AgentResume agentResume = new AgentResume();
        agentResume.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentResume.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        agentResume.AgentData_Field.SerialNum = this.lastPauseId;
        agentResume.isReliable = true;
        SendMessage(agentResume);
        this.lastPauseId++;
    }

    private void HandleCapsEvent(SLCapEventQueue.CapsEvent capsEvent) {
        switch (m133x4d1ccd1c()[capsEvent.eventType.ordinal()]) {
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
            AvatarGroupList.AvatarGroupEntry avatarGroupEntry = avatarGroupList != null ? avatarGroupList.Groups.get(fromString) : null;
            LLSDNode byKey = lLSDNode.byKey("instantmessage").byKey("message_params");
            UUID asUUID = byKey.keyExists("from_id") ? byKey.byKey("from_id").asUUID() : null;
            UUID asUUID2 = byKey.byKey("to_id").asUUID();
            String asString = byKey.byKey("message").asString();
            if (avatarGroupEntry == null) {
                avatarGroupEntry = avatarGroupList != null ? avatarGroupList.Groups.get(asUUID2) : null;
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
                Iterator<ImprovedInstantMessage> it = this.pendingGroupMessages.iterator();
                while (it.hasNext()) {
                    ImprovedInstantMessage next = it.next();
                    if (next.MessageBlock_Field.ID.equals(asUUID)) {
                        it.remove();
                        SendMessage(next);
                    }
                }
            }
        } catch (LLSDException e2) {
            Debug.Log("ChatterBoxSessionStartReply: LLSDException " + e2.getMessage());
            e2.printStackTrace();
        }
    }

    private void HandleChatterOnlineStatus(ChatterID chatterID, boolean z) {
        if (this.userManager.isChatterActive(chatterID) && (chatterID instanceof ChatterID.ChatterIDUser)) {
            HandleChatEvent(chatterID, new SLChatOnlineOfflineEvent(new ChatMessageSourceUser(((ChatterID.ChatterIDUser) chatterID).getChatterUUID()), this.agentUUID, z), false);
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
        if (wrap.limit() < 18) {
            return;
        }
        wrap.order(ByteOrder.BIG_ENDIAN);
        byte b = wrap.get();
        byte b2 = wrap.get();
        UUID uuid = new UUID(wrap.getLong(), wrap.getLong());
        String str = "";
        if (b != 0) {
            byte[] bArr = new byte[wrap.remaining()];
            wrap.get(bArr);
            str = SLMessage.stringFromVariableOEM(bArr);
        }
        Debug.Log("HandleGroupNotice: group UUID = " + uuid.toString());
        ChatterID groupChatterID = ChatterID.getGroupChatterID(this.agentUUID, uuid);
        boolean equal = Objects.equal(chatMessageSource.getSourceUUID(), this.circuitInfo.agentID);
        String stringFromVariableUTF = SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message);
        int indexOf = stringFromVariableUTF.indexOf(Vr.VREvent.VrCore.ErrorCode.CONTROLLER_GATT_NOTIFY_FAILED);
        if (indexOf >= 0) {
            stringFromVariableUTF = stringFromVariableUTF.substring(0, indexOf) + "\n" + stringFromVariableUTF.substring(indexOf + 1);
        }
        if (equal && b != 0) {
            stringFromVariableUTF = stringFromVariableUTF + "\n(This notice contains attached item '" + str + "')";
        }
        HandleChatEvent(groupChatterID, new SLChatTextEvent(chatMessageSource, this.agentUUID, improvedInstantMessage, stringFromVariableUTF), true);
        if (b == 0 || !(!equal)) {
            return;
        }
        HandleChatEvent(groupChatterID, new SLChatInventoryItemOfferedByGroupNoticeEvent(chatMessageSource, this.agentUUID, improvedInstantMessage, str, SLAssetType.getByType(b2)), false);
    }

    private void HandleIM(ImprovedInstantMessage improvedInstantMessage, ChatMessageSource chatMessageSource) {
        UUID sourceUUID;
        SLModules modules = getModules();
        if (modules == null || !modules.rlvController.onIncomingIM(improvedInstantMessage)) {
            int i = improvedInstantMessage.MessageBlock_Field.Dialog;
            switch (i) {
                case 0:
                case 20:
                    SLChatTextEvent sLChatTextEvent = new SLChatTextEvent(chatMessageSource, this.agentUUID, improvedInstantMessage, null);
                    ChatterID defaultChatter = chatMessageSource.getDefaultChatter(this.agentUUID);
                    boolean isChatterActive = this.userManager.isChatterActive(defaultChatter);
                    HandleChatEvent(defaultChatter, sLChatTextEvent, true);
                    if (this.userManager.isChatterMuted(defaultChatter) || i == 20 || improvedInstantMessage.MessageBlock_Field.Offline != 0 || improvedInstantMessage.MessageBlock_Field.Message.length == 0 || isChatterActive || !(defaultChatter instanceof ChatterID.ChatterIDUser)) {
                        return;
                    }
                    String autoresponse = SLGridConnection.getAutoresponse();
                    if (Strings.isNullOrEmpty(autoresponse)) {
                        return;
                    }
                    SendInstantMessage(((ChatterID.ChatterIDUser) defaultChatter).getChatterUUID(), autoresponse, 20);
                    return;
                case 1:
                case 2:
                    HandleChatEvent(this.localChatterID, new SLChatSystemMessageEvent(ChatMessageSourceUnknown.getInstance(), this.agentUUID, SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message)), true);
                    return;
                case 3:
                    HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), new SLChatGroupInvitationEvent(chatMessageSource, this.agentUUID, improvedInstantMessage), true);
                    return;
                case 4:
                    HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), new SLChatInventoryItemOfferedEvent(chatMessageSource, this.agentUUID, improvedInstantMessage), true);
                    return;
                case 5:
                case 6:
                case 7:
                case 8:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 18:
                case 21:
                case 23:
                case 24:
                case 25:
                case 27:
                case 28:
                case 29:
                case 30:
                case 33:
                case 34:
                case 35:
                case 36:
                default:
                    Debug.Log("HandleIM: unknown type = " + i + ", sessionId = " + improvedInstantMessage.AgentData_Field.SessionID.toString() + ", toAgentID = " + improvedInstantMessage.MessageBlock_Field.ToAgentID.toString() + ", fromGroup = " + improvedInstantMessage.MessageBlock_Field.FromGroup + ", message = '" + SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message) + "'");
                    return;
                case 9:
                    HandleChatEvent(this.localChatterID, new SLChatInventoryItemOfferedEvent(new ChatMessageSourceObject(improvedInstantMessage.AgentData_Field.AgentID, SLMessage.stringFromVariableOEM(improvedInstantMessage.MessageBlock_Field.FromAgentName)), this.agentUUID, improvedInstantMessage), true);
                    return;
                case 17:
                    HandleSessionIM(improvedInstantMessage, chatMessageSource);
                    return;
                case 19:
                case 31:
                    HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), new SLChatTextEvent(chatMessageSource, this.agentUUID, improvedInstantMessage, null), true);
                    return;
                case 22:
                    if (chatMessageSource.getSourceType() == ChatMessageSource.ChatMessageSourceType.User) {
                        UUID sourceUUID2 = chatMessageSource.getSourceUUID();
                        if (modules != null) {
                            if (modules.rlvController.autoAcceptTeleport(sourceUUID2)) {
                                TeleportToLure(improvedInstantMessage.MessageBlock_Field.ID);
                                return;
                            } else if (!modules.rlvController.canTeleportToLure(sourceUUID2)) {
                                return;
                            }
                        }
                    }
                    HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), new SLChatLureEvent(chatMessageSource, this.agentUUID, improvedInstantMessage), true);
                    return;
                case 26:
                    if (chatMessageSource.getSourceType() != ChatMessageSource.ChatMessageSourceType.User || modules == null || modules.rlvController.canTeleportToLure(chatMessageSource.getSourceUUID())) {
                        HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), new SLChatLureRequestEvent(chatMessageSource, this.agentUUID, improvedInstantMessage), true);
                        return;
                    }
                    return;
                case 32:
                case 37:
                    HandleGroupNotice(improvedInstantMessage, chatMessageSource);
                    return;
                case 38:
                    HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), new SLChatFriendshipOfferedEvent(chatMessageSource, this.agentUUID, improvedInstantMessage), true);
                    return;
                case 39:
                case 40:
                    HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), new SLChatFriendshipResultEvent(chatMessageSource, this.agentUUID, improvedInstantMessage), true);
                    if (i == 39 && chatMessageSource.getSourceType() == ChatMessageSource.ChatMessageSourceType.User && (sourceUUID = chatMessageSource.getSourceUUID()) != null) {
                        this.userManager.getChatterList().getFriendManager().addFriend(sourceUUID);
                        SendGenericMessage("requestonlinenotification", new String[]{sourceUUID.toString()});
                        return;
                    }
                    return;
                case 41:
                    HandleTypingNotification(chatMessageSource, true);
                    return;
                case 42:
                    HandleTypingNotification(chatMessageSource, false);
                    return;
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
        if (!this.teleportRequestSent) {
            Debug.Log("TeleportFinish: stale teleport finish?");
            return;
        }
        this.teleportRequestSent = false;
        try {
            LLSDNode byIndex = lLSDNode.byKey("Info").byIndex(0);
            String asString = byIndex.byKey("SeedCapability").asString();
            byte[] asBinary = byIndex.byKey("SimIP").asBinary();
            SLAuthReply sLAuthReply = new SLAuthReply(this.authReply, true, false, this.authReply.agentID, String.format("%d.%d.%d.%d", Integer.valueOf(asBinary[0] & UnsignedBytes.MAX_VALUE), Integer.valueOf(asBinary[1] & UnsignedBytes.MAX_VALUE), Integer.valueOf(asBinary[2] & UnsignedBytes.MAX_VALUE), Integer.valueOf(asBinary[3] & UnsignedBytes.MAX_VALUE)), byIndex.byKey("SimPort").asInt(), asString);
            Debug.Printf("new sim address: %s", sLAuthReply.simAddress);
            this.modules.avatarControl.setEnableAgentUpdates(false);
            this.gridConn.HandleTeleportFinish(sLAuthReply);
        } catch (LLSDException e2) {
            Debug.Log("TeleportFinish: LLSDException, teleport apparently failed");
            e2.printStackTrace();
        }
    }

    private void HandleTypingNotification(ChatMessageSource chatMessageSource, boolean z) {
        UUID sourceUUID;
        if (!(chatMessageSource instanceof ChatMessageSourceUser) || (sourceUUID = chatMessageSource.getSourceUUID()) == null) {
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

    private void ProcessObjectSelection() {
        ObjectSelect objectSelect;
        if (getNeedObjectNames() && (!this.doingObjectSelection)) {
            ObjectSelect objectSelect2 = null;
            for (SLObjectInfo sLObjectInfo : this.forceNeedObjectNames.values()) {
                if (objectSelect2 == null) {
                    objectSelect2 = new ObjectSelect();
                    objectSelect2.AgentData_Field.AgentID = this.circuitInfo.agentID;
                    objectSelect2.AgentData_Field.SessionID = this.circuitInfo.sessionID;
                }
                if (objectSelect2.ObjectData_Fields.size() > 16) {
                    break;
                }
                ObjectSelect.ObjectData objectData = new ObjectSelect.ObjectData();
                objectData.ObjectLocalID = sLObjectInfo.localID;
                objectSelect2.ObjectData_Fields.add(objectData);
                sLObjectInfo.nameRequested = true;
                sLObjectInfo.nameRequestedAt = System.currentTimeMillis();
                this.objectNamesRequested.put(sLObjectInfo.getId(), sLObjectInfo);
            }
            synchronized (this.gridConn.parcelInfo.objectNamesQueue) {
                Iterator<T> it = this.gridConn.parcelInfo.objectNamesQueue.values().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        objectSelect = objectSelect2;
                        break;
                    }
                    SLObjectInfo sLObjectInfo2 = (SLObjectInfo) it.next();
                    if (objectSelect2 == null) {
                        objectSelect2 = new ObjectSelect();
                        objectSelect2.AgentData_Field.AgentID = this.circuitInfo.agentID;
                        objectSelect2.AgentData_Field.SessionID = this.circuitInfo.sessionID;
                    }
                    if (objectSelect2.ObjectData_Fields.size() > 16) {
                        objectSelect = objectSelect2;
                        break;
                    }
                    ObjectSelect.ObjectData objectData2 = new ObjectSelect.ObjectData();
                    objectData2.ObjectLocalID = sLObjectInfo2.localID;
                    objectSelect2.ObjectData_Fields.add(objectData2);
                    sLObjectInfo2.nameRequested = true;
                    sLObjectInfo2.nameRequestedAt = System.currentTimeMillis();
                    this.objectNamesRequested.put(sLObjectInfo2.getId(), sLObjectInfo2);
                }
            }
            if (objectSelect != null) {
                Debug.Log("ObjectSelect: Sending ObjectSelect for " + objectSelect.ObjectData_Fields.size() + " objects, " + this.gridConn.parcelInfo.objectNamesQueue.size() + " remains.");
                objectSelect.isReliable = true;
                SendMessage(objectSelect);
                this.lastObjectSelection = System.currentTimeMillis();
                this.doingObjectSelection = true;
            }
        }
    }

    private void ProcessObjectSelectionTimeout() {
        for (SLObjectInfo sLObjectInfo : this.objectNamesRequested.values()) {
            SLObjectInfo remove = this.gridConn.parcelInfo.objectNamesQueue.remove(sLObjectInfo.getId());
            if (remove != null) {
                this.gridConn.parcelInfo.objectNamesQueue.put(remove.getId(), remove);
            }
            this.forceNeedObjectNames.remove(sLObjectInfo.getId());
        }
        this.objectNamesRequested.clear();
    }

    private void SendAgentFOV() {
        AgentFOV agentFOV = new AgentFOV();
        agentFOV.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentFOV.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        agentFOV.AgentData_Field.CircuitCode = this.circuitInfo.circuitCode;
        agentFOV.FOVBlock_Field.GenCounter = 0;
        agentFOV.FOVBlock_Field.VerticalAngle = 3.0543263f;
        agentFOV.isReliable = true;
        SendMessage(agentFOV);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SendCompleteAgentMovement() {
        CompleteAgentMovement completeAgentMovement = new CompleteAgentMovement();
        completeAgentMovement.AgentData_Field.CircuitCode = this.circuitInfo.circuitCode;
        completeAgentMovement.AgentData_Field.AgentID = this.circuitInfo.agentID;
        completeAgentMovement.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        completeAgentMovement.isReliable = true;
        SendMessage(completeAgentMovement);
    }

    private void SendEstateOwnerMessage(String str, String[] strArr) {
        EstateOwnerMessage estateOwnerMessage = new EstateOwnerMessage();
        estateOwnerMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        estateOwnerMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        estateOwnerMessage.AgentData_Field.TransactionID = new UUID(0L, 0L);
        estateOwnerMessage.MethodData_Field.Method = SLMessage.stringToVariableOEM(str);
        estateOwnerMessage.MethodData_Field.Invoice = new UUID(0L, 0L);
        for (String str2 : strArr) {
            EstateOwnerMessage.ParamList paramList = new EstateOwnerMessage.ParamList();
            paramList.Parameter = SLMessage.stringToVariableOEM(str2);
            estateOwnerMessage.ParamList_Fields.add(paramList);
        }
        estateOwnerMessage.isReliable = true;
        SendMessage(estateOwnerMessage);
    }

    private void SendGroupSessionStart(UUID uuid) {
        ImprovedInstantMessage improvedInstantMessage = new ImprovedInstantMessage();
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        improvedInstantMessage.MessageBlock_Field.FromGroup = false;
        improvedInstantMessage.MessageBlock_Field.ToAgentID = uuid;
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0;
        improvedInstantMessage.MessageBlock_Field.RegionID = new UUID(0L, 0L);
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
        ImprovedInstantMessage improvedInstantMessage = new ImprovedInstantMessage();
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        improvedInstantMessage.MessageBlock_Field.FromGroup = false;
        improvedInstantMessage.MessageBlock_Field.ToAgentID = uuid;
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0;
        improvedInstantMessage.MessageBlock_Field.RegionID = new UUID(0L, 0L);
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
        if (i != 20 && i != 41 && i != 42) {
            if (i == 26) {
                HandleChatEvent(ChatterID.getUserChatterID(this.agentUUID, uuid), new SLChatLureRequestedEvent(str, this.agentUUID), false);
            } else {
                HandleChatEvent(ChatterID.getUserChatterID(this.agentUUID, uuid), new SLChatTextEvent(new ChatMessageSourceUser(this.circuitInfo.agentID), this.agentUUID, str), false);
            }
        }
        return true;
    }

    private void SendRetrieveInstantMessages() {
        RetrieveInstantMessages retrieveInstantMessages = new RetrieveInstantMessages();
        retrieveInstantMessages.AgentData_Field.AgentID = this.circuitInfo.agentID;
        retrieveInstantMessages.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        retrieveInstantMessages.isReliable = true;
        SendMessage(retrieveInstantMessages);
    }

    private UUID getActiveGroupID() {
        if (this.modules != null) {
            return this.modules.groupManager.getActiveGroupID();
        }
        return null;
    }

    private boolean getNeedObjectNames() {
        if (this.forceNeedObjectNames != null && !this.forceNeedObjectNames.isEmpty()) {
            return true;
        }
        if (this.modules != null) {
            return this.modules.drawDistance.isObjectSelectEnabled();
        }
        return false;
    }

    private boolean isEventMuted(ChatterID chatterID, SLChatEvent sLChatEvent) {
        if (this.modules == null) {
            return false;
        }
        SLMuteList sLMuteList = this.modules.muteList;
        ChatMessageSource source = sLChatEvent.getSource();
        if (source.getSourceType() == ChatMessageSource.ChatMessageSourceType.User) {
            if (sLMuteList.isMuted(source.getSourceUUID(), MuteType.AGENT)) {
                return true;
            }
        } else if (source.getSourceType() == ChatMessageSource.ChatMessageSourceType.Object) {
            UUID sourceUUID = source.getSourceUUID();
            if (sourceUUID != null && !sourceUUID.equals(UUIDPool.ZeroUUID) && sLMuteList.isMuted(sourceUUID, MuteType.OBJECT)) {
                return true;
            }
            String sourceName = source.getSourceName(this.userManager);
            if (sourceName != null && sLMuteList.isMutedByName(sourceName)) {
                return true;
            }
        }
        if (!(chatterID instanceof ChatterID.ChatterIDGroup)) {
            return false;
        }
        UUID chatterUUID = ((ChatterID.ChatterIDGroup) chatterID).getChatterUUID();
        return !chatterUUID.equals(UUIDPool.ZeroUUID) && sLMuteList.isMuted(chatterUUID, MuteType.GROUP);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyObjectPropertiesChange() {
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
        this.userManager.getChatterList().getFriendManager().addFriend(uuid);
        AcceptFriendship acceptFriendship = new AcceptFriendship();
        acceptFriendship.AgentData_Field.AgentID = this.circuitInfo.agentID;
        acceptFriendship.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        UUID callingCardsFolderUUID = this.modules != null ? this.modules.inventory.getCallingCardsFolderUUID() : null;
        AcceptFriendship.FolderData folderData = new AcceptFriendship.FolderData();
        if (callingCardsFolderUUID == null) {
            callingCardsFolderUUID = UUIDPool.ZeroUUID;
        }
        folderData.FolderID = callingCardsFolderUUID;
        acceptFriendship.FolderData_Fields.add(folderData);
        acceptFriendship.TransactionBlock_Field.TransactionID = uuid2;
        acceptFriendship.isReliable = true;
        SendMessage(acceptFriendship);
    }

    public void AcceptInventoryOffer(int i, boolean z, UUID uuid, UUID uuid2, UUID uuid3) {
        ImprovedInstantMessage improvedInstantMessage = new ImprovedInstantMessage();
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        improvedInstantMessage.MessageBlock_Field.FromGroup = false;
        improvedInstantMessage.MessageBlock_Field.ToAgentID = uuid;
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0;
        improvedInstantMessage.MessageBlock_Field.RegionID = new UUID(0L, 0L);
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
        ImprovedInstantMessage improvedInstantMessage = new ImprovedInstantMessage();
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        improvedInstantMessage.MessageBlock_Field.FromGroup = false;
        improvedInstantMessage.MessageBlock_Field.ToAgentID = uuid;
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0;
        improvedInstantMessage.MessageBlock_Field.RegionID = new UUID(0L, 0L);
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
        ObjectBuy objectBuy = new ObjectBuy();
        objectBuy.AgentData_Field.AgentID = this.circuitInfo.agentID;
        objectBuy.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        ObjectBuy.AgentData agentData = objectBuy.AgentData_Field;
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

    @Override // com.lumiyaviewer.lumiya.slproto.SLCircuit
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
        DeRezObject deRezObject = new DeRezObject();
        deRezObject.AgentData_Field.AgentID = this.circuitInfo.agentID;
        deRezObject.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        DeRezObject.AgentBlock agentBlock = deRezObject.AgentBlock_Field;
        if (activeGroupID == null) {
            activeGroupID = new UUID(0L, 0L);
        }
        agentBlock.GroupID = activeGroupID;
        deRezObject.AgentBlock_Field.Destination = eDeRezDestination.getCode();
        deRezObject.AgentBlock_Field.DestinationID = new UUID(0L, 0L);
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
        SLObjectInfo sLObjectInfo = this.gridConn.parcelInfo.allObjectsNearby.get(uuid);
        if (sLObjectInfo != null) {
            if (sLObjectInfo.getPayInfo() != null) {
                this.eventBus.publish(new SLObjectPayInfoEvent(sLObjectInfo));
                return;
            }
            RequestPayPrice requestPayPrice = new RequestPayPrice();
            requestPayPrice.ObjectData_Field.ObjectID = uuid;
            requestPayPrice.isReliable = true;
            SendMessage(requestPayPrice);
        }
    }

    public void GenerateChatMoneyEvent(UUID uuid, int i, int i2) {
        HandleChatEvent(uuid != null ? ChatterID.getUserChatterID(this.agentUUID, uuid) : this.localChatterID, new SLChatBalanceChangedEvent(uuid != null ? new ChatMessageSourceUser(uuid) : ChatMessageSourceUnknown.getInstance(), this.agentUUID, true, i, i2), true);
        if (this.modules != null) {
            this.modules.financialInfo.RecordChatEvent(uuid, i, i2);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleAgentMovementComplete(AgentMovementComplete agentMovementComplete) {
        this.regionHandle = agentMovementComplete.Data_Field.RegionHandle;
        this.modules.avatarControl.setAgentPosition(agentMovementComplete.Data_Field.Position, null);
        Debug.Printf("Got agentPosition: %s", this.modules.avatarControl.getAgentPosition().getImmutablePosition());
        SendAgentFOV();
        this.modules.avatarAppearance.SendAgentWearablesRequest();
        SendRetrieveInstantMessages();
        this.modules.avatarControl.setEnableAgentUpdates(true);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleAlertMessage(AlertMessage alertMessage) {
        HandleChatEvent(this.localChatterID, new SLChatSystemMessageEvent(ChatMessageSourceUnknown.getInstance(), this.agentUUID, SLMessage.stringFromVariableOEM(alertMessage.AlertData_Field.Message)), true);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleAvatarAnimation(AvatarAnimation avatarAnimation) {
        SLParcelInfo sLParcelInfo = this.gridConn.parcelInfo;
        if (sLParcelInfo == null || this.modules == null) {
            return;
        }
        sLParcelInfo.ApplyAvatarAnimation(avatarAnimation, this.modules.avatarControl);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
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

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleAvatarInterestsReply(AvatarInterestsReply avatarInterestsReply) {
        Debug.Log("got AvatarInterestsReply: wantToText = " + SLMessage.stringFromVariableOEM(avatarInterestsReply.PropertiesData_Field.WantToText));
        Debug.Log("got AvatarInterestsReply: skillText = " + SLMessage.stringFromVariableOEM(avatarInterestsReply.PropertiesData_Field.SkillsText));
    }

    public void HandleChatEvent(ChatterID chatterID, SLChatEvent sLChatEvent, boolean z) {
        if (isEventMuted(chatterID, sLChatEvent)) {
            return;
        }
        this.userManager.getChatterList().getActiveChattersManager().HandleChatEvent(chatterID, sLChatEvent, z);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleChatFromSimulator(ChatFromSimulator chatFromSimulator) {
        int i;
        SLModules modules = getModules();
        if (modules == null || !modules.rlvController.onIncomingChat(chatFromSimulator)) {
            UUID uuid = chatFromSimulator.ChatData_Field.SourceID;
            String stringFromVariableOEM = SLMessage.stringFromVariableOEM(chatFromSimulator.ChatData_Field.FromName);
            String stringFromVariableUTF = SLMessage.stringFromVariableUTF(chatFromSimulator.ChatData_Field.Message);
            if (chatFromSimulator.ChatData_Field.ChatType == 8 && chatFromSimulator.ChatData_Field.SourceType == 2 && stringFromVariableOEM.startsWith("#Firestorm LSL Bridge") && stringFromVariableUTF.startsWith("<bridgeURL>")) {
                return;
            }
            if ((chatFromSimulator.ChatData_Field.SourceType == 1 && modules != null && !modules.rlvController.canRecvChat(stringFromVariableUTF, uuid)) || chatFromSimulator.ChatData_Field.Audible != 1 || (i = chatFromSimulator.ChatData_Field.ChatType) == 6 || i == 4 || i == 5) {
                return;
            }
            switch (chatFromSimulator.ChatData_Field.SourceType) {
                case 1:
                    HandleChatEvent(this.localChatterID, new SLChatTextEvent(new ChatMessageSourceUser(uuid), this.agentUUID, stringFromVariableUTF), true);
                    return;
                case 2:
                    HandleChatEvent(this.localChatterID, new SLChatTextEvent(new ChatMessageSourceObject(uuid, stringFromVariableOEM), this.agentUUID, stringFromVariableUTF), true);
                    return;
                default:
                    HandleChatEvent(this.localChatterID, new SLChatTextEvent(ChatMessageSourceUnknown.getInstance(), this.agentUUID, stringFromVariableUTF), true);
                    return;
            }
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
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

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleImprovedTerseObjectUpdate(ImprovedTerseObjectUpdate improvedTerseObjectUpdate) {
        SLObjectInfo sLObjectInfo;
        SLParcelInfo sLParcelInfo = this.gridConn.parcelInfo;
        RequestMultipleObjects requestMultipleObjects = null;
        for (ImprovedTerseObjectUpdate.ObjectData objectData : improvedTerseObjectUpdate.ObjectData_Fields) {
            int localID = SLObjectInfo.getLocalID(objectData);
            UUID uuid = sLParcelInfo.uuidsNearby.get(Integer.valueOf(localID));
            if (uuid != null) {
                sLObjectInfo = sLParcelInfo.allObjectsNearby.get(uuid);
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

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleKillObject(KillObject killObject) {
        boolean z;
        SLParcelInfo sLParcelInfo = this.gridConn.parcelInfo;
        boolean z2 = false;
        Iterator<T> it = killObject.ObjectData_Fields.iterator();
        while (true) {
            z = z2;
            if (!it.hasNext()) {
                break;
            } else {
                z2 = sLParcelInfo.killObject(this, ((KillObject.ObjectData) it.next()).ID) ? true : z;
            }
        }
        if (z) {
            this.objectPropertiesRateLimiter.fire();
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleLayerData(LayerData layerData) {
        SLParcelInfo sLParcelInfo;
        if (layerData.LayerID_Field.Type != 76 || (sLParcelInfo = this.gridConn.parcelInfo) == null) {
            return;
        }
        sLParcelInfo.terrainData.ProcessLayerData(layerData.LayerDataData_Field.Data);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleLoadURL(LoadURL loadURL) {
        HandleChatEvent(this.localChatterID, new SLChatTextEvent(new ChatMessageSourceObject(loadURL.Data_Field.ObjectID, SLMessage.stringFromVariableOEM(loadURL.Data_Field.ObjectName)), this.agentUUID, loadURL), true);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleObjectProperties(ObjectProperties objectProperties) {
        UUID id;
        Debug.Log("ObjectProperties: " + objectProperties.ObjectData_Fields.size() + " ObjectSelect replies. Reqd " + this.objectNamesRequested.size() + " obj, remains " + this.gridConn.parcelInfo.objectNamesQueue.size() + " objects.");
        for (ObjectProperties.ObjectData objectData : objectProperties.ObjectData_Fields) {
            SLObjectInfo remove = this.gridConn.parcelInfo.objectNamesQueue.remove(objectData.ObjectID);
            if (remove != null) {
                remove.ApplyObjectProperties(objectData);
                this.userManager.getObjectsManager().requestObjectProfileUpdate(remove.localID);
            }
            SLObjectInfo remove2 = this.forceNeedObjectNames.remove(objectData.ObjectID);
            if (remove2 != null) {
                remove2.ApplyObjectProperties(objectData);
                this.userManager.getObjectsManager().requestObjectProfileUpdate(remove2.localID);
                SLObjectInfo parentObject = remove2.getParentObject();
                if (parentObject != null && (id = parentObject.getId()) != null) {
                    this.userManager.getObjectsManager().requestTouchableChildrenUpdate(id);
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

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleObjectUpdate(ObjectUpdate objectUpdate) {
        SLParcelInfo sLParcelInfo = this.gridConn.parcelInfo;
        boolean z = false;
        boolean z2 = false;
        for (ObjectUpdate.ObjectData objectData : objectUpdate.ObjectData_Fields) {
            if (objectData.PCode == 47 || objectData.PCode == 9) {
                SLObjectInfo sLObjectInfo = sLParcelInfo.allObjectsNearby.get(objectData.FullID);
                if (sLObjectInfo != null) {
                    int i = sLObjectInfo.parentID;
                    sLObjectInfo.ApplyObjectUpdate(objectData);
                    sLParcelInfo.updateObjectParent(i, sLObjectInfo);
                    if (sLObjectInfo.parentID != i && (sLObjectInfo instanceof SLObjectAvatarInfo) && ((SLObjectAvatarInfo) sLObjectInfo).isMyAvatar()) {
                        z = true;
                    }
                    z2 = true;
                } else {
                    sLObjectInfo = SLObjectInfo.create(this.agentUUID, objectData, this.circuitInfo.agentID);
                    if (sLParcelInfo.addObject(sLObjectInfo)) {
                        z2 = true;
                    }
                    if ((sLObjectInfo instanceof SLObjectAvatarInfo) && ((SLObjectAvatarInfo) sLObjectInfo).isMyAvatar()) {
                        Debug.Log("ObjectUpdate: got my avatar (normal)");
                        sLParcelInfo.setAgentAvatar((SLObjectAvatarInfo) sLObjectInfo);
                        this.modules.avatarAppearance.OnMyAvatarCreated((SLObjectAvatarInfo) sLObjectInfo);
                        z = true;
                    }
                }
                if (sLObjectInfo instanceof SLObjectAvatarInfo ? ((SLObjectAvatarInfo) sLObjectInfo).isMyAvatar() : false) {
                    processMyAvatarUpdate((SLObjectAvatarInfo) sLObjectInfo);
                } else if (sLObjectInfo.isMyAttachment()) {
                    processMyAttachmentUpdate(sLObjectInfo);
                }
            }
            z = z;
            z2 = z2;
        }
        if (z) {
            this.userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value);
        }
        if (z2) {
            ProcessObjectSelection();
            this.objectPropertiesRateLimiter.fire();
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleObjectUpdateCached(ObjectUpdateCached objectUpdateCached) {
        RequestMultipleObjects requestMultipleObjects = new RequestMultipleObjects();
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

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleObjectUpdateCompressed(ObjectUpdateCompressed objectUpdateCompressed) {
        boolean z;
        SLParcelInfo sLParcelInfo = this.gridConn.parcelInfo;
        boolean z2 = false;
        boolean z3 = false;
        for (ObjectUpdateCompressed.ObjectData objectData : objectUpdateCompressed.ObjectData_Fields) {
            try {
                UUID uuid = sLParcelInfo.uuidsNearby.get(Integer.valueOf(SLObjectInfo.getLocalID(objectData)));
                SLObjectInfo sLObjectInfo = uuid != null ? sLParcelInfo.allObjectsNearby.get(uuid) : null;
                if (sLObjectInfo != null) {
                    int i = sLObjectInfo.parentID;
                    sLObjectInfo.ApplyObjectUpdate(objectData);
                    sLParcelInfo.updateObjectParent(i, sLObjectInfo);
                    z = sLObjectInfo.parentID != i;
                    z3 = true;
                } else {
                    sLObjectInfo = SLObjectInfo.create(objectData);
                    if (sLParcelInfo.addObject(sLObjectInfo)) {
                        z3 = true;
                    }
                    z = false;
                }
                if (sLObjectInfo instanceof SLObjectAvatarInfo ? ((SLObjectAvatarInfo) sLObjectInfo).isMyAvatar() : false) {
                    if (z) {
                        z2 = true;
                    }
                    processMyAvatarUpdate((SLObjectAvatarInfo) sLObjectInfo);
                } else if (sLObjectInfo.isMyAttachment()) {
                    processMyAttachmentUpdate(sLObjectInfo);
                }
            } catch (UnsupportedObjectTypeException e) {
            } catch (Exception e2) {
                Debug.Warning(e2);
            }
            z2 = z2;
            z3 = z3;
        }
        if (z3) {
            ProcessObjectSelection();
            this.objectPropertiesRateLimiter.fire();
        }
        if (z2) {
            this.userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleOfflineNotification(OfflineNotification offlineNotification) {
        ArrayList arrayList = new ArrayList(offlineNotification.AgentBlock_Fields.size());
        Iterator<T> it = offlineNotification.AgentBlock_Fields.iterator();
        while (it.hasNext()) {
            arrayList.add(((OfflineNotification.AgentBlock) it.next()).AgentID);
        }
        this.userManager.getChatterList().getFriendManager().setUsersOnline(arrayList, false);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleOnlineNotification(OnlineNotification onlineNotification) {
        ArrayList arrayList = new ArrayList(onlineNotification.AgentBlock_Fields.size());
        Iterator<T> it = onlineNotification.AgentBlock_Fields.iterator();
        while (it.hasNext()) {
            arrayList.add(((OnlineNotification.AgentBlock) it.next()).AgentID);
        }
        this.userManager.getChatterList().getFriendManager().setUsersOnline(arrayList, true);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandlePayPriceReply(PayPriceReply payPriceReply) {
        SLObjectInfo sLObjectInfo = this.gridConn.parcelInfo.allObjectsNearby.get(payPriceReply.ObjectData_Field.ObjectID);
        if (sLObjectInfo != null) {
            int i = payPriceReply.ObjectData_Field.DefaultPayPrice;
            int[] iArr = new int[payPriceReply.ButtonData_Fields.size()];
            int i2 = 0;
            while (true) {
                int i3 = i2;
                if (i3 >= payPriceReply.ButtonData_Fields.size()) {
                    break;
                }
                iArr[i3] = payPriceReply.ButtonData_Fields.get(i3).PayButton;
                i2 = i3 + 1;
            }
            sLObjectInfo.setPayInfo(PayInfo.create(i, iArr));
            if (this.userManager != null) {
                this.userManager.getObjectsManager().requestObjectProfileUpdate(sLObjectInfo.localID);
            }
            this.eventBus.publish(new SLObjectPayInfoEvent(sLObjectInfo));
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleRegionHandshake(RegionHandshake regionHandshake) {
        if (this.authReply.isTemporary) {
            return;
        }
        RegionHandshakeReply regionHandshakeReply = new RegionHandshakeReply();
        regionHandshakeReply.AgentData_Field.AgentID = this.circuitInfo.agentID;
        regionHandshakeReply.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        regionHandshakeReply.RegionInfo_Field.Flags = 0;
        if (this.gridConn != null && this.gridConn.parcelInfo != null) {
            this.gridConn.parcelInfo.terrainData.ApplyRegionInfo(regionHandshake.RegionInfo_Field);
        }
        SendMessage(regionHandshakeReply);
        this.regionName = SLMessage.stringFromVariableOEM(regionHandshake.RegionInfo_Field.SimName);
        if (regionHandshake.RegionInfo2_Field != null && regionHandshake.RegionInfo2_Field.RegionID != null) {
            this.regionID = regionHandshake.RegionInfo2_Field.RegionID;
        }
        this.isEstateManager = regionHandshake.RegionInfo_Field.IsEstateManager;
        this.agentNameSubscription = this.userManager.getUserNames().subscribe(this.circuitInfo.agentID, new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.slproto.-$Lambda$K1xWCpEh0d4XNuVVYxGUJwEFRxU
            private final /* synthetic */ void $m$0(Object obj) {
                ((SLAgentCircuit) this).m137lambda$com_lumiyaviewer_lumiya_slproto_SLAgentCircuit_14593((UserName) obj);
            }

            @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
            public final void onData(Object obj) {
                $m$0(obj);
            }
        });
        if (this.eventBus != null) {
            this.eventBus.publish(new SLRegionInfoChangedEvent());
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleScriptDialog(ScriptDialog scriptDialog) {
        String[] strArr;
        boolean z;
        int i = 0;
        if (scriptDialog.Buttons_Fields.size() > 0) {
            String[] strArr2 = new String[scriptDialog.Buttons_Fields.size()];
            Iterator<T> it = scriptDialog.Buttons_Fields.iterator();
            int i2 = 0;
            while (true) {
                if (!it.hasNext()) {
                    z = false;
                    strArr = strArr2;
                    break;
                }
                strArr2[i2] = SLMessage.stringFromVariableUTF(((ScriptDialog.Buttons) it.next()).ButtonLabel);
                if (strArr2[i2].equals("!!llTextBox!!")) {
                    i = i2;
                    z = true;
                    strArr = strArr2;
                    break;
                }
                i2++;
            }
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

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleSimulatorViewerTimeMessage(SimulatorViewerTimeMessage simulatorViewerTimeMessage) {
        if (this.authReply.isTemporary || this.gridConn == null || this.gridConn.parcelInfo == null) {
            return;
        }
        float f = (simulatorViewerTimeMessage.TimeInfo_Field.SunPhase / 6.2831855f) + 0.25f;
        this.gridConn.parcelInfo.setSunHour((float) (f - Math.floor(f)));
    }

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleTeleportFailed(TeleportFailed teleportFailed) {
        Debug.Log("TeleportFailed: reason = " + SLMessage.stringFromVariableOEM(teleportFailed.Info_Field.Reason));
        this.teleportRequestSent = false;
        this.eventBus.publish(new SLTeleportResultEvent(false, SLMessage.stringFromVariableOEM(teleportFailed.Info_Field.Reason)));
    }

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleTeleportLocal(TeleportLocal teleportLocal) {
        this.teleportRequestSent = false;
        this.eventBus.publish(new SLTeleportResultEvent(true, null));
    }

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleTeleportProgress(TeleportProgress teleportProgress) {
        Debug.Log("Teleport progress: flags = " + teleportProgress.Info_Field.TeleportFlags + ", progress = " + SLMessage.stringFromVariableOEM(teleportProgress.Info_Field.Message));
    }

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleTeleportStart(TeleportStart teleportStart) {
        Debug.Log("TeleportStart: flags = " + teleportStart.Info_Field.TeleportFlags);
    }

    public void OfferInventoryItem(final UUID uuid, final SLInventoryEntry sLInventoryEntry) {
        this.userManager.getInventoryManager().getExecutor().execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.-$Lambda$K1xWCpEh0d4XNuVVYxGUJwEFRxU.1
            private final /* synthetic */ void $m$0() {
                ((SLAgentCircuit) this).m138lambda$com_lumiyaviewer_lumiya_slproto_SLAgentCircuit_77024((SLInventoryEntry) sLInventoryEntry, (UUID) uuid);
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    public void OfferTeleport(UUID uuid, String str) {
        StartLure startLure = new StartLure();
        startLure.AgentData_Field.AgentID = this.circuitInfo.agentID;
        startLure.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        startLure.Info_Field.Message = SLMessage.stringToVariableUTF(str);
        StartLure.TargetData targetData = new StartLure.TargetData();
        targetData.TargetID = uuid;
        startLure.TargetData_Fields.add(targetData);
        startLure.isReliable = true;
        SendMessage(startLure);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.ICapsEventHandler
    public void OnCapsEvent(SLCapEventQueue.CapsEvent capsEvent) {
        try {
            this.capsEventQueue.add(capsEvent);
            this.selector.wakeup();
        } catch (Exception e) {
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLCircuit
    public void ProcessIdle() {
        if (this.doingObjectSelection && System.currentTimeMillis() > this.lastObjectSelection + 15000) {
            this.doingObjectSelection = false;
            ProcessObjectSelectionTimeout();
        }
        if (!this.teleportRequestSent && getNeedObjectNames() && (!this.doingObjectSelection) && System.currentTimeMillis() >= this.lastObjectSelection + 500) {
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

    @Override // com.lumiyaviewer.lumiya.slproto.SLThreadingCircuit, com.lumiyaviewer.lumiya.slproto.SLCircuit
    public void ProcessNetworkError() {
        super.ProcessNetworkError();
        Debug.Printf("Network: Network error.", new Object[0]);
        if (this.modules != null) {
            this.modules.avatarControl.setEnableAgentUpdates(false);
        }
        if (this.authReply.isTemporary) {
            return;
        }
        this.gridConn.processDisconnect(false, "Network connection lost.");
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLThreadingCircuit, com.lumiyaviewer.lumiya.slproto.SLCircuit
    public void ProcessTimeout() {
        super.ProcessTimeout();
        if (this.modules != null) {
            this.modules.avatarControl.setEnableAgentUpdates(false);
        }
        if (this.authReply.isTemporary) {
            return;
        }
        this.gridConn.processDisconnect(false, "Connection has timed out.");
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLCircuit
    public void ProcessWakeup() {
        super.ProcessWakeup();
        while (true) {
            try {
                SLCapEventQueue.CapsEvent poll = this.capsEventQueue.poll();
                if (poll == null) {
                    break;
                } else {
                    HandleCapsEvent(poll);
                }
            } catch (Exception e) {
            }
        }
        ProcessIdle();
    }

    public void RemoveFriend(UUID uuid) {
        TerminateFriendship terminateFriendship = new TerminateFriendship();
        terminateFriendship.AgentData_Field.AgentID = this.circuitInfo.agentID;
        terminateFriendship.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        terminateFriendship.ExBlock_Field.OtherID = uuid;
        terminateFriendship.isReliable = true;
        SendMessage(terminateFriendship);
        this.userManager.getChatterList().getFriendManager().removeFriend(uuid);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void RequestObjectName(SLObjectInfo sLObjectInfo) {
        if (sLObjectInfo.getId() != null && !this.objectNamesRequested.containsKey(sLObjectInfo.getId()) && (!this.forceNeedObjectNames.containsKey(sLObjectInfo.getId()))) {
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

    public void RezObject(SLInventoryEntry sLInventoryEntry) {
        CurrentLocationInfo currentLocationInfoSnapshot;
        ParcelData parcelData;
        UUID uuid = null;
        UUID uuid2 = UUIDPool.ZeroUUID;
        UUID ownerID = (this.userManager == null || (currentLocationInfoSnapshot = this.userManager.getCurrentLocationInfoSnapshot()) == null || (parcelData = currentLocationInfoSnapshot.parcelData()) == null || !parcelData.isGroupOwned()) ? null : parcelData.getOwnerID();
        if (ownerID == null) {
            uuid = ownerID;
        } else if (!UUIDPool.ZeroUUID.equals(ownerID)) {
            uuid = ownerID;
        }
        if (uuid != null) {
            AvatarGroupList avatarGroupList = this.userManager.getChatterList().getGroupManager().getAvatarGroupList();
            if (avatarGroupList == null || !avatarGroupList.Groups.containsKey(uuid)) {
                uuid = uuid2;
            }
        } else {
            uuid = getActiveGroupID();
        }
        if (uuid == null) {
            uuid = UUIDPool.ZeroUUID;
        }
        RezObject rezObject = new RezObject();
        rezObject.AgentData_Field.AgentID = this.circuitInfo.agentID;
        rezObject.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        rezObject.AgentData_Field.GroupID = uuid;
        rezObject.RezData_Field.FromTaskID = UUIDPool.ZeroUUID;
        rezObject.RezData_Field.BypassRaycast = 1;
        rezObject.RezData_Field.RayStart = this.modules.avatarControl.getAgentPosition().getPosition();
        rezObject.RezData_Field.RayEnd = rezObject.RezData_Field.RayStart.getRotatedOffset(1.5f, getModules().avatarControl.getAgentHeading());
        rezObject.RezData_Field.RayEndIsIntersection = true;
        rezObject.RezData_Field.RayTargetID = UUIDPool.ZeroUUID;
        rezObject.RezData_Field.RezSelected = false;
        rezObject.RezData_Field.RemoveItem = false;
        rezObject.RezData_Field.ItemFlags = 0;
        rezObject.RezData_Field.GroupMask = sLInventoryEntry.groupMask;
        rezObject.RezData_Field.EveryoneMask = sLInventoryEntry.everyoneMask;
        rezObject.RezData_Field.NextOwnerMask = sLInventoryEntry.nextOwnerMask;
        rezObject.InventoryData_Field.ItemID = sLInventoryEntry.uuid;
        rezObject.InventoryData_Field.FolderID = sLInventoryEntry.parentUUID;
        rezObject.InventoryData_Field.CreatorID = sLInventoryEntry.creatorUUID;
        rezObject.InventoryData_Field.OwnerID = sLInventoryEntry.ownerUUID;
        rezObject.InventoryData_Field.GroupID = sLInventoryEntry.groupUUID;
        rezObject.InventoryData_Field.BaseMask = sLInventoryEntry.baseMask;
        rezObject.InventoryData_Field.OwnerMask = sLInventoryEntry.ownerMask;
        rezObject.InventoryData_Field.GroupMask = sLInventoryEntry.groupMask;
        rezObject.InventoryData_Field.EveryoneMask = sLInventoryEntry.everyoneMask;
        rezObject.InventoryData_Field.NextOwnerMask = sLInventoryEntry.nextOwnerMask;
        rezObject.InventoryData_Field.GroupOwned = sLInventoryEntry.isGroupOwned;
        rezObject.InventoryData_Field.TransactionID = UUID.randomUUID();
        rezObject.InventoryData_Field.Type = sLInventoryEntry.assetType;
        rezObject.InventoryData_Field.InvType = sLInventoryEntry.invType;
        rezObject.InventoryData_Field.Flags = sLInventoryEntry.flags;
        rezObject.InventoryData_Field.SaleType = sLInventoryEntry.saleType;
        rezObject.InventoryData_Field.SalePrice = sLInventoryEntry.salePrice;
        rezObject.InventoryData_Field.Name = SLMessage.stringToVariableOEM(sLInventoryEntry.name);
        rezObject.InventoryData_Field.Description = SLMessage.stringToVariableOEM(sLInventoryEntry.description);
        rezObject.InventoryData_Field.CreationDate = sLInventoryEntry.creationDate;
        rezObject.InventoryData_Field.CRC = 0;
        rezObject.isReliable = true;
        if ((sLInventoryEntry.ownerMask & 32768) == 0) {
            final UUID uuid3 = sLInventoryEntry.parentUUID;
            rezObject.setEventListener(new SLMessageEventListener() { // from class: com.lumiyaviewer.lumiya.slproto.SLAgentCircuit.9
                @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
                public void onMessageAcknowledged(SLMessage sLMessage) {
                    if (SLAgentCircuit.this.userManager != null) {
                        SLAgentCircuit.this.userManager.getInventoryManager().requestFolderUpdate(uuid3);
                    }
                }

                @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
                public void onMessageTimeout(SLMessage sLMessage) {
                }
            });
        }
        SendMessage(rezObject);
    }

    public void SendChatMessage(@Nonnull ChatterID chatterID, String str) {
        switch (m134x2680ba3d()[chatterID.getChatterType().ordinal()]) {
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
        GenericMessage genericMessage = new GenericMessage();
        genericMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        genericMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        genericMessage.AgentData_Field.TransactionID = new UUID(0L, 0L);
        genericMessage.MethodData_Field.Method = SLMessage.stringToVariableOEM(str);
        genericMessage.MethodData_Field.Invoice = new UUID(0L, 0L);
        for (String str2 : strArr) {
            GenericMessage.ParamList paramList = new GenericMessage.ParamList();
            paramList.Parameter = SLMessage.stringToVariableOEM(str2);
            genericMessage.ParamList_Fields.add(paramList);
        }
        genericMessage.isReliable = true;
        SendMessage(genericMessage);
    }

    public void SendGroupInstantMessage(UUID uuid, String str) {
        ImprovedInstantMessage improvedInstantMessage = new ImprovedInstantMessage();
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        improvedInstantMessage.MessageBlock_Field.FromGroup = false;
        improvedInstantMessage.MessageBlock_Field.ToAgentID = uuid;
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0;
        improvedInstantMessage.MessageBlock_Field.RegionID = new UUID(0L, 0L);
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
            int i2 = 0;
            for (int i3 = 1; i3 < str.length() && Character.isDigit(str.charAt(i3)); i3++) {
                i2++;
            }
            if (i2 >= 0) {
                try {
                    i = Integer.parseInt(str.substring(1, i2 + 1));
                    str = str.substring(i2 + 1).trim();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (getModules().rlvController.onSendLocalChat(i, str)) {
            ChatFromViewer chatFromViewer = new ChatFromViewer();
            chatFromViewer.AgentData_Field.AgentID = this.circuitInfo.agentID;
            chatFromViewer.AgentData_Field.SessionID = this.circuitInfo.sessionID;
            chatFromViewer.ChatData_Field.Channel = i;
            chatFromViewer.ChatData_Field.Type = 1;
            chatFromViewer.ChatData_Field.Message = SLMessage.stringToVariableUTF(str);
            chatFromViewer.isReliable = true;
            SendMessage(chatFromViewer);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void SendLogoutRequest() {
        Debug.Log("Logout: Sending logout request.");
        this.modules.avatarControl.setEnableAgentUpdates(false);
        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        logoutRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        logoutRequest.isReliable = true;
        logoutRequest.setEventListener(new SLMessageEventListener() { // from class: com.lumiyaviewer.lumiya.slproto.SLAgentCircuit.7
            @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
            public void onMessageAcknowledged(SLMessage sLMessage) {
                Debug.Log("Logout: Logout request acknowledged.");
                SLAgentCircuit.this.gridConn.processDisconnect(true, "Logged out.");
            }

            @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
            public void onMessageTimeout(SLMessage sLMessage) {
                Debug.Log("Logout: LogoutRequest timed out!");
                SLAgentCircuit.this.gridConn.processDisconnect(false, "Logout request has timed out.");
            }
        });
        SendMessage(logoutRequest);
    }

    public void SendScriptDialogReply(UUID uuid, int i, int i2, String str) {
        ScriptDialogReply scriptDialogReply = new ScriptDialogReply();
        scriptDialogReply.AgentData_Field.AgentID = this.circuitInfo.agentID;
        scriptDialogReply.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        scriptDialogReply.isReliable = true;
        scriptDialogReply.Data_Field.ObjectID = uuid;
        scriptDialogReply.Data_Field.ChatChannel = i;
        scriptDialogReply.Data_Field.ButtonIndex = i2;
        scriptDialogReply.Data_Field.ButtonLabel = SLMessage.stringToVariableUTF(str);
        SendMessage(scriptDialogReply);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void SendUseCode() {
        Debug.Printf("Using circuitCode: %d", Integer.valueOf(this.circuitInfo.circuitCode));
        UseCircuitCode useCircuitCode = new UseCircuitCode();
        useCircuitCode.CircuitCode_Field.Code = this.circuitInfo.circuitCode;
        useCircuitCode.CircuitCode_Field.SessionID = this.circuitInfo.sessionID;
        useCircuitCode.CircuitCode_Field.ID = this.circuitInfo.agentID;
        useCircuitCode.isReliable = true;
        useCircuitCode.setEventListener(new SLMessageEventListener() { // from class: com.lumiyaviewer.lumiya.slproto.SLAgentCircuit.2
            @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
            public void onMessageAcknowledged(SLMessage sLMessage) {
                Debug.Log("SLAgentCircuit: UseCircuitCode acknowledged.");
                if (SLAgentCircuit.this.authReply.isTemporary) {
                    return;
                }
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

            @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
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
        boolean z = false;
        synchronized (this.startedGroupSessions) {
            if (!this.startedGroupSessions.contains(uuid)) {
                SendGroupSessionStart(uuid);
                z = true;
            }
        }
        if (z) {
            return;
        }
        this.modules.voice.onGroupSessionReady(uuid);
    }

    public void TeleportToGlobalPosition(LLVector3 lLVector3) {
        int floor = (int) Math.floor(lLVector3.x);
        int floor2 = (int) Math.floor(lLVector3.y);
        long j = (floor2 - (floor2 % 256)) | ((floor - (floor % 256)) << 32);
        LLVector3 lLVector32 = new LLVector3(lLVector3.x % 256.0f, lLVector3.y % 256.0f, lLVector3.z);
        LLVector3 lLVector33 = new LLVector3(lLVector32);
        lLVector33.x += 1.0f;
        Debug.Printf("regionHandle = %s, globalPos = %s", Long.toHexString(j), lLVector3);
        this.teleportRequestSent = true;
        TeleportLocationRequest teleportLocationRequest = new TeleportLocationRequest();
        teleportLocationRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        teleportLocationRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        teleportLocationRequest.Info_Field.RegionHandle = j;
        teleportLocationRequest.Info_Field.Position = lLVector32;
        teleportLocationRequest.Info_Field.LookAt = lLVector33;
        teleportLocationRequest.isReliable = true;
        teleportLocationRequest.setEventListener(new SLMessageEventListener() { // from class: com.lumiyaviewer.lumiya.slproto.SLAgentCircuit.5
            @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
            public void onMessageAcknowledged(SLMessage sLMessage) {
            }

            @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
            public void onMessageTimeout(SLMessage sLMessage) {
                SLAgentCircuit.this.eventBus.publish(new SLTeleportResultEvent(false, "Teleport request has timed out."));
            }
        });
        SendMessage(teleportLocationRequest);
    }

    public void TeleportToLandmarkAsset(UUID uuid) {
        if (getModules().rlvController.canTeleportToLandmark()) {
            this.teleportRequestSent = true;
            TeleportLandmarkRequest teleportLandmarkRequest = new TeleportLandmarkRequest();
            teleportLandmarkRequest.Info_Field.AgentID = this.circuitInfo.agentID;
            teleportLandmarkRequest.Info_Field.SessionID = this.circuitInfo.sessionID;
            teleportLandmarkRequest.Info_Field.LandmarkID = uuid;
            teleportLandmarkRequest.isReliable = true;
            teleportLandmarkRequest.setEventListener(new SLMessageEventListener() { // from class: com.lumiyaviewer.lumiya.slproto.SLAgentCircuit.3
                @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
                public void onMessageAcknowledged(SLMessage sLMessage) {
                }

                @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
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
        TeleportLocationRequest teleportLocationRequest = new TeleportLocationRequest();
        teleportLocationRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        teleportLocationRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        teleportLocationRequest.Info_Field.RegionHandle = this.regionHandle;
        teleportLocationRequest.Info_Field.Position = lLVector3;
        teleportLocationRequest.Info_Field.LookAt = new LLVector3(lLVector3);
        teleportLocationRequest.Info_Field.LookAt.x += 10.0f;
        teleportLocationRequest.isReliable = true;
        teleportLocationRequest.setEventListener(new SLMessageEventListener() { // from class: com.lumiyaviewer.lumiya.slproto.SLAgentCircuit.8
            @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
            public void onMessageAcknowledged(SLMessage sLMessage) {
            }

            @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
            public void onMessageTimeout(SLMessage sLMessage) {
                SLAgentCircuit.this.eventBus.publish(new SLTeleportResultEvent(false, "Teleport request has timed out."));
            }
        });
        SendMessage(teleportLocationRequest);
        return true;
    }

    public void TeleportToLure(UUID uuid) {
        this.teleportRequestSent = true;
        TeleportLureRequest teleportLureRequest = new TeleportLureRequest();
        teleportLureRequest.Info_Field.AgentID = this.circuitInfo.agentID;
        teleportLureRequest.Info_Field.SessionID = this.circuitInfo.sessionID;
        teleportLureRequest.Info_Field.LureID = uuid;
        teleportLureRequest.isReliable = true;
        teleportLureRequest.setEventListener(new SLMessageEventListener() { // from class: com.lumiyaviewer.lumiya.slproto.SLAgentCircuit.4
            @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
            public void onMessageAcknowledged(SLMessage sLMessage) {
            }

            @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
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
            TeleportLocationRequest teleportLocationRequest = new TeleportLocationRequest();
            teleportLocationRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
            teleportLocationRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
            teleportLocationRequest.Info_Field.RegionHandle = j;
            teleportLocationRequest.Info_Field.Position = new LLVector3(i, i2, i3);
            teleportLocationRequest.Info_Field.LookAt = new LLVector3(0.0f, 1.0f, 0.0f);
            teleportLocationRequest.isReliable = true;
            teleportLocationRequest.setEventListener(new SLMessageEventListener() { // from class: com.lumiyaviewer.lumiya.slproto.SLAgentCircuit.6
                @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
                public void onMessageAcknowledged(SLMessage sLMessage) {
                }

                @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
                public void onMessageTimeout(SLMessage sLMessage) {
                    SLAgentCircuit.this.eventBus.publish(new SLTeleportResultEvent(false, "Teleport request has timed out."));
                }
            });
            SendMessage(teleportLocationRequest);
        }
    }

    public void TouchObject(int i) {
        ObjectGrab objectGrab = new ObjectGrab();
        objectGrab.AgentData_Field.AgentID = this.circuitInfo.agentID;
        objectGrab.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        objectGrab.ObjectData_Field.LocalID = i;
        objectGrab.ObjectData_Field.GrabOffset = new LLVector3();
        objectGrab.isReliable = true;
        SendMessage(objectGrab);
        ObjectDeGrab objectDeGrab = new ObjectDeGrab();
        objectDeGrab.AgentData_Field.AgentID = this.circuitInfo.agentID;
        objectDeGrab.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        objectDeGrab.ObjectData_Field.LocalID = i;
        objectDeGrab.isReliable = true;
        SendMessage(objectDeGrab);
    }

    public void TouchObjectFace(SLObjectInfo sLObjectInfo, int i, float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        Debug.Printf("Touch: Object %d, face %d, pos (%f, %f, %f), uv (%f, %f)", Integer.valueOf(sLObjectInfo.localID), Integer.valueOf(i), Float.valueOf(f), Float.valueOf(f2), Float.valueOf(f3), Float.valueOf(f4), Float.valueOf(f5));
        ObjectGrab objectGrab = new ObjectGrab();
        objectGrab.AgentData_Field.AgentID = this.circuitInfo.agentID;
        objectGrab.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        objectGrab.ObjectData_Field.LocalID = sLObjectInfo.localID;
        objectGrab.ObjectData_Field.GrabOffset = new LLVector3();
        ObjectGrab.SurfaceInfo surfaceInfo = new ObjectGrab.SurfaceInfo();
        surfaceInfo.FaceIndex = i;
        surfaceInfo.Position = new LLVector3(f, f2, f3);
        surfaceInfo.UVCoord = new LLVector3(f4, f5, 0.0f);
        surfaceInfo.STCoord = new LLVector3(f6, f7, 0.0f);
        surfaceInfo.Normal = new LLVector3(1.0f, 0.0f, 0.0f);
        surfaceInfo.Binormal = new LLVector3(0.0f, 0.0f, 1.0f);
        objectGrab.SurfaceInfo_Fields.add(surfaceInfo);
        objectGrab.isReliable = true;
        SendMessage(objectGrab);
        ObjectDeGrab objectDeGrab = new ObjectDeGrab();
        objectDeGrab.AgentData_Field.AgentID = this.circuitInfo.agentID;
        objectDeGrab.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        objectDeGrab.ObjectData_Field.LocalID = sLObjectInfo.localID;
        objectDeGrab.isReliable = true;
        SendMessage(objectDeGrab);
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
        lLVector3d.x = i + position.x;
        lLVector3d.y = i2 + position.y;
        lLVector3d.z = position.z;
        return lLVector3d;
    }

    @Nullable
    @SuppressLint({"DefaultLocale"})
    public String getAgentSLURL() {
        if (this.modules == null || !Objects.equal(this.authReply.loginURL, "https://login.agni.lindenlab.com/cgi-bin/login.cgi") || this.regionName == null) {
            return null;
        }
        LLVector3 position = this.modules.avatarControl.getAgentPosition().getPosition();
        try {
            return String.format("http://maps.secondlife.com/secondlife/%s/%d/%d/%d", URLEncoder.encode(this.regionName, "UTF-8"), Integer.valueOf((int) position.x), Integer.valueOf((int) position.y), Integer.valueOf((int) position.z));
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
        if (!create.name().isPresent() && (!objectInfo.isDead)) {
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

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_SLAgentCircuit_14593, reason: not valid java name */
    public /* synthetic */ void m137lambda$com_lumiyaviewer_lumiya_slproto_SLAgentCircuit_14593(UserName userName) {
        this.agentUserName.set(userName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_SLAgentCircuit_77024, reason: not valid java name */
    public /* synthetic */ void m138lambda$com_lumiyaviewer_lumiya_slproto_SLAgentCircuit_77024(SLInventoryEntry sLInventoryEntry, UUID uuid) {
        ArrayList<SLInventoryEntry> arrayList = new ArrayList();
        arrayList.add(sLInventoryEntry);
        if (sLInventoryEntry.isFolder) {
            arrayList.addAll(this.modules.inventory.CollectGiveableItems(sLInventoryEntry));
        }
        ImprovedInstantMessage improvedInstantMessage = new ImprovedInstantMessage();
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        improvedInstantMessage.MessageBlock_Field.FromGroup = false;
        improvedInstantMessage.MessageBlock_Field.ToAgentID = uuid;
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0;
        improvedInstantMessage.MessageBlock_Field.RegionID = new UUID(0L, 0L);
        improvedInstantMessage.MessageBlock_Field.Position = new LLVector3();
        improvedInstantMessage.MessageBlock_Field.Offline = 0;
        improvedInstantMessage.MessageBlock_Field.Dialog = 4;
        improvedInstantMessage.MessageBlock_Field.ID = UUID.randomUUID();
        improvedInstantMessage.MessageBlock_Field.Timestamp = 0;
        improvedInstantMessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo");
        improvedInstantMessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF(sLInventoryEntry.name);
        ByteBuffer wrap = ByteBuffer.wrap(new byte[arrayList.size() * 17]);
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

    /* JADX INFO: Access modifiers changed from: package-private */
    public void processMyAttachmentUpdate(SLObjectInfo sLObjectInfo) {
        if (sLObjectInfo != null && !sLObjectInfo.nameKnown && (!sLObjectInfo.isDead)) {
            RequestObjectName(sLObjectInfo);
        }
        getModules().avatarAppearance.UpdateMyAttachments();
    }

    public void sendTypingNotify(UUID uuid, boolean z) {
        SendInstantMessage(uuid, "", z ? 41 : 42);
    }
}
