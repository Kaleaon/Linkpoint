// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.slproto.chat.SLChatInventoryItemOfferedByYouEvent;
import java.util.Collection;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import android.annotation.SuppressLint;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectDeGrab;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectGrab;
import com.lumiyaviewer.lumiya.slproto.messages.TeleportLureRequest;
import com.lumiyaviewer.lumiya.slproto.messages.TeleportLandmarkRequest;
import com.lumiyaviewer.lumiya.slproto.messages.TeleportLocationRequest;
import com.lumiyaviewer.lumiya.slproto.messages.UseCircuitCode;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptDialogReply;
import com.lumiyaviewer.lumiya.slproto.messages.LogoutRequest;
import com.lumiyaviewer.lumiya.slproto.messages.ChatFromViewer;
import com.lumiyaviewer.lumiya.slproto.messages.GenericMessage;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.messages.RezObject;
import com.lumiyaviewer.lumiya.slproto.messages.TerminateFriendship;
import com.lumiyaviewer.lumiya.GridConnectionService;
import com.lumiyaviewer.lumiya.slproto.messages.StartLure;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.messages.TeleportStart;
import com.lumiyaviewer.lumiya.slproto.messages.TeleportProgress;
import com.lumiyaviewer.lumiya.slproto.messages.TeleportLocal;
import com.lumiyaviewer.lumiya.slproto.messages.TeleportFailed;
import com.lumiyaviewer.lumiya.slproto.messages.SimulatorViewerTimeMessage;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextBoxDialog;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatScriptDialog;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptDialog;
import com.lumiyaviewer.lumiya.slproto.events.SLRegionInfoChangedEvent;
import com.lumiyaviewer.lumiya.slproto.messages.RegionHandshakeReply;
import com.lumiyaviewer.lumiya.slproto.messages.RegionHandshake;
import com.lumiyaviewer.lumiya.slproto.objects.PayInfo;
import com.lumiyaviewer.lumiya.slproto.messages.PayPriceReply;
import com.lumiyaviewer.lumiya.slproto.messages.OnlineNotification;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.messages.OfflineNotification;
import com.lumiyaviewer.lumiya.slproto.objects.UnsupportedObjectTypeException;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCompressed;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCached;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectProperties;
import com.lumiyaviewer.lumiya.slproto.messages.LoadURL;
import com.lumiyaviewer.lumiya.slproto.messages.LayerData;
import com.lumiyaviewer.lumiya.slproto.messages.KillObject;
import com.lumiyaviewer.lumiya.slproto.messages.RequestMultipleObjects;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedTerseObjectUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.ChatFromSimulator;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarInterestsReply;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAnimation;
import com.lumiyaviewer.lumiya.slproto.messages.AlertMessage;
import com.lumiyaviewer.lumiya.slproto.messages.AgentMovementComplete;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatBalanceChangedEvent;
import com.lumiyaviewer.lumiya.slproto.messages.RequestPayPrice;
import com.lumiyaviewer.lumiya.slproto.events.SLObjectPayInfoEvent;
import com.lumiyaviewer.lumiya.slproto.messages.DeRezObject;
import com.lumiyaviewer.lumiya.slproto.types.EDeRezDestination;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectBuy;
import com.lumiyaviewer.lumiya.slproto.messages.AcceptFriendship;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.SLMuteList;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteType;
import com.lumiyaviewer.lumiya.slproto.messages.RetrieveInstantMessages;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatLureRequestedEvent;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.messages.EstateOwnerMessage;
import com.lumiyaviewer.lumiya.slproto.messages.CompleteAgentMovement;
import com.lumiyaviewer.lumiya.slproto.messages.AgentFOV;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectSelect;
import com.lumiyaviewer.lumiya.slproto.events.SLTeleportResultEvent;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatGroupInvitationEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatFriendshipResultEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatFriendshipOfferedEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatLureRequestEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatLureEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatInventoryItemOfferedEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatSystemMessageEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatInventoryItemOfferedByGroupNoticeEvent;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent;
import com.google.common.base.Objects;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatOnlineOfflineEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUser;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.messages.AgentResume;
import com.lumiyaviewer.lumiya.slproto.messages.AgentPause;
import com.lumiyaviewer.lumiya.Debug;
import java.io.IOException;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedList;
import java.util.HashSet;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.Set;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import java.util.List;
import com.lumiyaviewer.lumiya.eventbus.EventRateLimiter;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import java.util.Map;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.dao.UserName;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue;

public class SLAgentCircuit extends SLThreadingCircuit implements ICapsEventHandler
{
    private Subscription agentNameSubscription;
    private boolean agentPaused;
    @Nonnull
    private final UUID agentUUID;
    private final AtomicReference<UserName> agentUserName;
    private final SLCaps caps;
    private final ConcurrentLinkedQueue<CapsEvent> capsEventQueue;
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
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues() {
        if (SLAgentCircuit.-com-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues != null) {
            return SLAgentCircuit.-com-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues = new int[CapsEventType.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues[CapsEventType.AgentGroupDataUpdate.ordinal()] = 9;
                try {
                    -com-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues[CapsEventType.AvatarGroupsReply.ordinal()] = 10;
                    try {
                        -com-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues[CapsEventType.BulkUpdateInventory.ordinal()] = 11;
                        try {
                            -com-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues[CapsEventType.ChatterBoxInvitation.ordinal()] = 1;
                            try {
                                -com-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues[CapsEventType.ChatterBoxSessionStartReply.ordinal()] = 2;
                                try {
                                    -com-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues[CapsEventType.EstablishAgentCommunication.ordinal()] = 3;
                                    try {
                                        -com-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues[CapsEventType.ParcelProperties.ordinal()] = 12;
                                        try {
                                            -com-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues[CapsEventType.TeleportFailed.ordinal()] = 4;
                                            try {
                                                -com-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues[CapsEventType.TeleportFinish.ordinal()] = 5;
                                                try {
                                                    -com-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues[CapsEventType.UnknownCapsEvent.ordinal()] = 13;
                                                    return -com-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues = -com-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues;
                                                }
                                                catch (final NoSuchFieldError noSuchFieldError) {}
                                            }
                                            catch (final NoSuchFieldError noSuchFieldError2) {}
                                        }
                                        catch (final NoSuchFieldError noSuchFieldError3) {}
                                    }
                                    catch (final NoSuchFieldError noSuchFieldError4) {}
                                }
                                catch (final NoSuchFieldError noSuchFieldError5) {}
                            }
                            catch (final NoSuchFieldError noSuchFieldError6) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError7) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError8) {}
                }
                catch (final NoSuchFieldError noSuchFieldError9) {}
            }
            catch (final NoSuchFieldError noSuchFieldError10) {
                continue;
            }
            break;
        }
    }
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues() {
        if (SLAgentCircuit.-com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues != null) {
            return SLAgentCircuit.-com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues = new int[ChatterID.ChatterType.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues[ChatterID.ChatterType.Group.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues[ChatterID.ChatterType.Local.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues[ChatterID.ChatterType.User.ordinal()] = 3;
                        return -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues = -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues;
                    }
                    catch (final NoSuchFieldError noSuchFieldError) {}
                }
                catch (final NoSuchFieldError noSuchFieldError2) {}
            }
            catch (final NoSuchFieldError noSuchFieldError3) {
                continue;
            }
            break;
        }
    }
    
    public SLAgentCircuit(final SLGridConnection slGridConnection, final SLCircuitInfo slCircuitInfo, final SLAuthReply slAuthReply, final SLCaps caps, final SLTempCircuit slTempCircuit) throws IOException {
        super(slGridConnection, slCircuitInfo, slAuthReply, slTempCircuit);
        this.eventBus = EventBus.getInstance();
        this.capsEventQueue = new ConcurrentLinkedQueue<CapsEvent>();
        this.startedGroupSessions = new HashSet<UUID>();
        this.pendingGroupMessages = new LinkedList<ImprovedInstantMessage>();
        this.teleportRequestSent = false;
        this.regionID = null;
        this.regionName = null;
        this.regionHandle = 0L;
        this.isEstateManager = false;
        this.lastObjectSelection = 0L;
        this.doingObjectSelection = false;
        this.objectPropertiesRateLimiter = new EventRateLimiter(this.eventBus, 500L) {
            @Override
            protected Object getEventToFire() {
                return null;
            }
            
            @Override
            protected void onActualFire() {
                SLAgentCircuit.this.notifyObjectPropertiesChange();
            }
        };
        this.objectNamesRequested = new ConcurrentHashMap<UUID, SLObjectInfo>();
        this.forceNeedObjectNames = new ConcurrentHashMap<UUID, SLObjectInfo>();
        this.agentPaused = false;
        this.lastVisibleActivities = 0L;
        this.lastPauseId = 0;
        this.agentUserName = new AtomicReference<UserName>(null);
        this.typingUsers = Collections.synchronizedSet(new HashSet<UUID>());
        this.caps = caps;
        this.agentUUID = slCircuitInfo.agentID;
        this.localChatterID = ChatterID.getLocalChatterID(this.agentUUID);
        this.lastVisibleActivities = System.currentTimeMillis();
        this.userManager = UserManager.getUserManager(slCircuitInfo.agentID);
        if (caps != null && (slAuthReply.isTemporary ^ true)) {
            this.modules = new SLModules(this, caps, slGridConnection);
        }
        else {
            this.modules = null;
        }
        if (!slAuthReply.isTemporary && this.userManager != null) {
            this.userManager.setActiveAgentCircuit(this);
        }
        if (slTempCircuit != null) {
            final Iterator<Object> iterator = slTempCircuit.getPendingMessages().iterator();
            while (iterator.hasNext()) {
                iterator.next().Handle(this);
            }
        }
    }
    
    private void DoAgentPause() {
        this.agentPaused = true;
        Debug.Log("AgentPause: Sending agentPause with ID = " + this.lastPauseId);
        final AgentPause agentPause = new AgentPause();
        agentPause.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentPause.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        agentPause.AgentData_Field.SerialNum = this.lastPauseId;
        agentPause.isReliable = true;
        this.SendMessage(agentPause);
        ++this.lastPauseId;
    }
    
    private void DoAgentResume() {
        this.agentPaused = false;
        Debug.Log("AgentPause: Sending agentResume with ID = " + this.lastPauseId);
        final AgentResume agentResume = new AgentResume();
        agentResume.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentResume.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        agentResume.AgentData_Field.SerialNum = this.lastPauseId;
        agentResume.isReliable = true;
        this.SendMessage(agentResume);
        ++this.lastPauseId;
    }
    
    private void HandleCapsEvent(final CapsEvent capsEvent) {
        switch (-getcom-lumiyaviewer-lumiya-slproto-caps-SLCapEventQueue$CapsEventTypeSwitchesValues()[capsEvent.eventType.ordinal()]) {
            default: {
                this.DefaultEventQueueHandler(capsEvent.eventType, capsEvent.eventBody);
                break;
            }
            case 1: {
                this.HandleChatterBoxInvitation(capsEvent.eventBody);
                break;
            }
            case 2: {
                this.HandleChatterBoxSessionStartReply(capsEvent.eventBody);
                break;
            }
            case 4: {
                this.HandleTeleportFailed(capsEvent.eventBody);
                break;
            }
            case 5: {
                this.HandleTeleportFinish(capsEvent.eventBody);
                break;
            }
            case 3: {
                this.HandleEstablishAgentCommunication(capsEvent.eventBody);
                break;
            }
        }
    }
    
    private void HandleChatterBoxInvitation(final LLSDNode p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: astore_2       
        //     4: aload_2        
        //     5: invokespecial   java/lang/StringBuilder.<init>:()V
        //     8: aload_2        
        //     9: ldc_w           "ChatterBoxInvitation: event = "
        //    12: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    15: aload_1        
        //    16: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.serializeToXML:()Ljava/lang/String;
        //    19: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    22: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    25: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //    28: aload_1        
        //    29: ldc_w           "session_id"
        //    32: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //    35: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asString:()Ljava/lang/String;
        //    38: invokestatic    java/util/UUID.fromString:(Ljava/lang/String;)Ljava/util/UUID;
        //    41: astore_3       
        //    42: aload_0        
        //    43: getfield        com/lumiyaviewer/lumiya/slproto/SLAgentCircuit.userManager:Lcom/lumiyaviewer/lumiya/slproto/users/manager/UserManager;
        //    46: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/manager/UserManager.getChatterList:()Lcom/lumiyaviewer/lumiya/slproto/users/manager/ChatterList;
        //    49: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/manager/ChatterList.getGroupManager:()Lcom/lumiyaviewer/lumiya/slproto/users/manager/GroupManager;
        //    52: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/manager/GroupManager.getAvatarGroupList:()Lcom/lumiyaviewer/lumiya/slproto/modules/groups/AvatarGroupList;
        //    55: astore          4
        //    57: aload           4
        //    59: ifnull          311
        //    62: aload           4
        //    64: getfield        com/lumiyaviewer/lumiya/slproto/modules/groups/AvatarGroupList.Groups:Lcom/google/common/collect/ImmutableMap;
        //    67: aload_3        
        //    68: invokevirtual   com/google/common/collect/ImmutableMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //    71: checkcast       Lcom/lumiyaviewer/lumiya/slproto/modules/groups/AvatarGroupList$AvatarGroupEntry;
        //    74: astore_2       
        //    75: aload_1        
        //    76: ldc_w           "instantmessage"
        //    79: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //    82: ldc_w           "message_params"
        //    85: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //    88: astore          5
        //    90: aload           5
        //    92: ldc_w           "from_id"
        //    95: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.keyExists:(Ljava/lang/String;)Z
        //    98: ifeq            306
        //   101: aload           5
        //   103: ldc_w           "from_id"
        //   106: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //   109: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asUUID:()Ljava/util/UUID;
        //   112: astore_1       
        //   113: aload           5
        //   115: ldc_w           "to_id"
        //   118: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //   121: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asUUID:()Ljava/util/UUID;
        //   124: astore          6
        //   126: aload           5
        //   128: ldc_w           "message"
        //   131: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //   134: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asString:()Ljava/lang/String;
        //   137: astore          5
        //   139: aload_2        
        //   140: ifnull          206
        //   143: aload_2        
        //   144: ifnull          233
        //   147: aload_1        
        //   148: ifnull          233
        //   151: aload_0        
        //   152: getfield        com/lumiyaviewer/lumiya/slproto/SLAgentCircuit.agentUUID:Ljava/util/UUID;
        //   155: aload_2        
        //   156: getfield        com/lumiyaviewer/lumiya/slproto/modules/groups/AvatarGroupList$AvatarGroupEntry.GroupID:Ljava/util/UUID;
        //   159: invokestatic    com/lumiyaviewer/lumiya/slproto/users/ChatterID.getGroupChatterID:(Ljava/util/UUID;Ljava/util/UUID;)Lcom/lumiyaviewer/lumiya/slproto/users/ChatterID;
        //   162: astore_3       
        //   163: new             Lcom/lumiyaviewer/lumiya/slproto/chat/SLChatTextEvent;
        //   166: astore          4
        //   168: new             Lcom/lumiyaviewer/lumiya/slproto/users/chatsrc/ChatMessageSourceUser;
        //   171: astore_2       
        //   172: aload_2        
        //   173: aload_1        
        //   174: invokespecial   com/lumiyaviewer/lumiya/slproto/users/chatsrc/ChatMessageSourceUser.<init>:(Ljava/util/UUID;)V
        //   177: aload           4
        //   179: aload_2        
        //   180: aload_0        
        //   181: getfield        com/lumiyaviewer/lumiya/slproto/SLAgentCircuit.agentUUID:Ljava/util/UUID;
        //   184: aload           5
        //   186: invokespecial   com/lumiyaviewer/lumiya/slproto/chat/SLChatTextEvent.<init>:(Lcom/lumiyaviewer/lumiya/slproto/users/chatsrc/ChatMessageSource;Ljava/util/UUID;Ljava/lang/String;)V
        //   189: aload_0        
        //   190: aload_3        
        //   191: aload           4
        //   193: iconst_1       
        //   194: invokevirtual   com/lumiyaviewer/lumiya/slproto/SLAgentCircuit.HandleChatEvent:(Lcom/lumiyaviewer/lumiya/slproto/users/ChatterID;Lcom/lumiyaviewer/lumiya/slproto/chat/generic/SLChatEvent;Z)V
        //   197: return         
        //   198: astore_2       
        //   199: aload_2        
        //   200: invokevirtual   java/io/IOException.printStackTrace:()V
        //   203: goto            28
        //   206: aload           4
        //   208: ifnull          228
        //   211: aload           4
        //   213: getfield        com/lumiyaviewer/lumiya/slproto/modules/groups/AvatarGroupList.Groups:Lcom/google/common/collect/ImmutableMap;
        //   216: aload           6
        //   218: invokevirtual   com/google/common/collect/ImmutableMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   221: checkcast       Lcom/lumiyaviewer/lumiya/slproto/modules/groups/AvatarGroupList$AvatarGroupEntry;
        //   224: astore_2       
        //   225: goto            143
        //   228: aconst_null    
        //   229: astore_2       
        //   230: goto            143
        //   233: new             Ljava/lang/StringBuilder;
        //   236: astore_1       
        //   237: aload_1        
        //   238: invokespecial   java/lang/StringBuilder.<init>:()V
        //   241: aload_1        
        //   242: ldc_w           "ChatterBoxInvitation: chat from unknown group ("
        //   245: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   248: aload_3        
        //   249: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   252: ldc_w           "), to_id = "
        //   255: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   258: aload           6
        //   260: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   263: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   266: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   269: goto            197
        //   272: astore_1       
        //   273: new             Ljava/lang/StringBuilder;
        //   276: dup            
        //   277: invokespecial   java/lang/StringBuilder.<init>:()V
        //   280: ldc_w           "ChatterBoxInvitation: LLSDException "
        //   283: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   286: aload_1        
        //   287: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDException.getMessage:()Ljava/lang/String;
        //   290: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   293: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   296: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   299: aload_1        
        //   300: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDException.printStackTrace:()V
        //   303: goto            197
        //   306: aconst_null    
        //   307: astore_1       
        //   308: goto            113
        //   311: aconst_null    
        //   312: astore_2       
        //   313: goto            75
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                                
        //  -----  -----  -----  -----  ----------------------------------------------------
        //  0      28     198    206    Ljava/io/IOException;
        //  28     57     272    306    Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDException;
        //  62     75     272    306    Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDException;
        //  75     113    272    306    Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDException;
        //  113    139    272    306    Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDException;
        //  151    197    272    306    Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDException;
        //  211    225    272    306    Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDException;
        //  233    269    272    306    Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0028:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void HandleChatterBoxSessionStartReply(final LLSDNode ex) {
        while (true) {
            while (true) {
                try {
                    Debug.Log("ChatterBoxSessionStartReply: event = " + ((LLSDNode)ex).serializeToXML());
                    try {
                        final UUID uuid = ((LLSDNode)ex).byKey("session_id").asUUID();
                        this.modules.voice.onGroupSessionReady(uuid);
                        synchronized (this.startedGroupSessions) {
                            this.startedGroupSessions.add(uuid);
                            final Iterator<ImprovedInstantMessage> iterator = this.pendingGroupMessages.iterator();
                            while (iterator.hasNext()) {
                                final ImprovedInstantMessage improvedInstantMessage = iterator.next();
                                if (improvedInstantMessage.MessageBlock_Field.ID.equals(uuid)) {
                                    iterator.remove();
                                    this.SendMessage(improvedInstantMessage);
                                }
                            }
                        }
                    }
                    catch (final LLSDException ex) {
                        Debug.Log("ChatterBoxSessionStartReply: LLSDException " + ex.getMessage());
                        ex.printStackTrace();
                    }
                    return;
                }
                catch (final IOException ex2) {
                    ex2.printStackTrace();
                    continue;
                }
                break;
            }
            monitorexit(ex);
        }
    }
    
    private void HandleChatterOnlineStatus(final ChatterID chatterID, final boolean b) {
        if (this.userManager.isChatterActive(chatterID) && chatterID instanceof ChatterID.ChatterIDUser) {
            this.HandleChatEvent(chatterID, new SLChatOnlineOfflineEvent(new ChatMessageSourceUser(((ChatterID.ChatterIDUser)chatterID).getChatterUUID()), this.agentUUID, b), false);
        }
    }
    
    private void HandleEstablishAgentCommunication(final LLSDNode p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        com/lumiyaviewer/lumiya/slproto/SLAgentCircuit.teleportRequestSent:Z
        //     4: ifeq            112
        //     7: new             Ljava/lang/StringBuilder;
        //    10: astore_2       
        //    11: aload_2        
        //    12: invokespecial   java/lang/StringBuilder.<init>:()V
        //    15: aload_2        
        //    16: ldc_w           "EstablishAgentCommunication: event = "
        //    19: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    22: aload_1        
        //    23: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.serializeToXML:()Ljava/lang/String;
        //    26: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    29: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    32: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //    35: aload_1        
        //    36: ldc_w           "sim-ip-and-port"
        //    39: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //    42: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asString:()Ljava/lang/String;
        //    45: astore_3       
        //    46: aload_1        
        //    47: ldc_w           "seed-capability"
        //    50: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //    53: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asString:()Ljava/lang/String;
        //    56: astore_2       
        //    57: aload_1        
        //    58: ldc_w           "agent-id"
        //    61: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //    64: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asUUID:()Ljava/util/UUID;
        //    67: astore_1       
        //    68: aload_3        
        //    69: ldc_w           ":"
        //    72: invokevirtual   java/lang/String.split:(Ljava/lang/String;)[Ljava/lang/String;
        //    75: astore_3       
        //    76: new             Lcom/lumiyaviewer/lumiya/slproto/auth/SLAuthReply;
        //    79: astore          4
        //    81: aload           4
        //    83: aload_0        
        //    84: getfield        com/lumiyaviewer/lumiya/slproto/SLAgentCircuit.authReply:Lcom/lumiyaviewer/lumiya/slproto/auth/SLAuthReply;
        //    87: iconst_1       
        //    88: iconst_1       
        //    89: aload_1        
        //    90: aload_3        
        //    91: iconst_0       
        //    92: aaload         
        //    93: aload_3        
        //    94: iconst_1       
        //    95: aaload         
        //    96: invokestatic    java/lang/Integer.parseInt:(Ljava/lang/String;)I
        //    99: aload_2        
        //   100: invokespecial   com/lumiyaviewer/lumiya/slproto/auth/SLAuthReply.<init>:(Lcom/lumiyaviewer/lumiya/slproto/auth/SLAuthReply;ZZLjava/util/UUID;Ljava/lang/String;ILjava/lang/String;)V
        //   103: aload_0        
        //   104: getfield        com/lumiyaviewer/lumiya/slproto/SLAgentCircuit.gridConn:Lcom/lumiyaviewer/lumiya/slproto/SLGridConnection;
        //   107: aload           4
        //   109: invokevirtual   com/lumiyaviewer/lumiya/slproto/SLGridConnection.addTempCircuit:(Lcom/lumiyaviewer/lumiya/slproto/auth/SLAuthReply;)V
        //   112: return         
        //   113: astore_2       
        //   114: aload_2        
        //   115: invokevirtual   java/io/IOException.printStackTrace:()V
        //   118: goto            35
        //   121: astore_1       
        //   122: aload_1        
        //   123: invokevirtual   java/lang/Exception.printStackTrace:()V
        //   126: goto            112
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  7      35     113    121    Ljava/io/IOException;
        //  35     112    121    129    Ljava/lang/Exception;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0035:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void HandleGroupNotice(final ImprovedInstantMessage improvedInstantMessage, final ChatMessageSource chatMessageSource) {
        final ByteBuffer wrap = ByteBuffer.wrap(improvedInstantMessage.MessageBlock_Field.BinaryBucket);
        if (wrap.limit() < 18) {
            return;
        }
        wrap.order(ByteOrder.BIG_ENDIAN);
        final byte value = wrap.get();
        final byte value2 = wrap.get();
        final UUID uuid = new UUID(wrap.getLong(), wrap.getLong());
        String stringFromVariableOEM = "";
        if (value != 0) {
            final byte[] dst = new byte[wrap.remaining()];
            wrap.get(dst);
            stringFromVariableOEM = SLMessage.stringFromVariableOEM(dst);
        }
        Debug.Log("HandleGroupNotice: group UUID = " + uuid.toString());
        final ChatterID groupChatterID = ChatterID.getGroupChatterID(this.agentUUID, uuid);
        final boolean equal = Objects.equal(chatMessageSource.getSourceUUID(), this.circuitInfo.agentID);
        final String stringFromVariableUTF = SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message);
        final int index = stringFromVariableUTF.indexOf(124);
        String string = stringFromVariableUTF;
        if (index >= 0) {
            string = stringFromVariableUTF.substring(0, index) + "\n" + stringFromVariableUTF.substring(index + 1);
        }
        String string2 = string;
        if (equal) {
            string2 = string;
            if (value != 0) {
                string2 = string + "\n" + "(This notice contains attached item '" + stringFromVariableOEM + "')";
            }
        }
        this.HandleChatEvent(groupChatterID, new SLChatTextEvent(chatMessageSource, this.agentUUID, improvedInstantMessage, string2), true);
        if (value != 0 && (equal ^ true)) {
            this.HandleChatEvent(groupChatterID, new SLChatInventoryItemOfferedByGroupNoticeEvent(chatMessageSource, this.agentUUID, improvedInstantMessage, stringFromVariableOEM, SLAssetType.getByType(value2)), false);
        }
    }
    
    private void HandleIM(final ImprovedInstantMessage improvedInstantMessage, final ChatMessageSource chatMessageSource) {
        final SLModules modules = this.getModules();
        if (modules != null && modules.rlvController.onIncomingIM(improvedInstantMessage)) {
            return;
        }
        final int dialog = improvedInstantMessage.MessageBlock_Field.Dialog;
        switch (dialog) {
            default: {
                Debug.Log("HandleIM: unknown type = " + dialog + ", sessionId = " + improvedInstantMessage.AgentData_Field.SessionID.toString() + ", " + "toAgentID = " + improvedInstantMessage.MessageBlock_Field.ToAgentID.toString() + ", " + "fromGroup = " + improvedInstantMessage.MessageBlock_Field.FromGroup + ", " + "message = '" + SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message) + "'");
                break;
            }
            case 1:
            case 2: {
                this.HandleChatEvent(this.localChatterID, new SLChatSystemMessageEvent(ChatMessageSourceUnknown.getInstance(), this.agentUUID, SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message)), true);
                break;
            }
            case 41: {
                this.HandleTypingNotification(chatMessageSource, true);
                break;
            }
            case 42: {
                this.HandleTypingNotification(chatMessageSource, false);
                break;
            }
            case 32:
            case 37: {
                this.HandleGroupNotice(improvedInstantMessage, chatMessageSource);
                break;
            }
            case 17: {
                this.HandleSessionIM(improvedInstantMessage, chatMessageSource);
                break;
            }
            case 4: {
                this.HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), new SLChatInventoryItemOfferedEvent(chatMessageSource, this.agentUUID, improvedInstantMessage), true);
                break;
            }
            case 9: {
                this.HandleChatEvent(this.localChatterID, new SLChatInventoryItemOfferedEvent(new ChatMessageSourceObject(improvedInstantMessage.AgentData_Field.AgentID, SLMessage.stringFromVariableOEM(improvedInstantMessage.MessageBlock_Field.FromAgentName)), this.agentUUID, improvedInstantMessage), true);
                break;
            }
            case 22: {
                if (chatMessageSource.getSourceType() == ChatMessageSource.ChatMessageSourceType.User) {
                    final UUID sourceUUID = chatMessageSource.getSourceUUID();
                    if (modules != null) {
                        if (modules.rlvController.autoAcceptTeleport(sourceUUID)) {
                            this.TeleportToLure(improvedInstantMessage.MessageBlock_Field.ID);
                            break;
                        }
                        if (!modules.rlvController.canTeleportToLure(sourceUUID)) {
                            break;
                        }
                    }
                }
                this.HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), new SLChatLureEvent(chatMessageSource, this.agentUUID, improvedInstantMessage), true);
                break;
            }
            case 26: {
                if (chatMessageSource.getSourceType() != ChatMessageSource.ChatMessageSourceType.User || modules == null || modules.rlvController.canTeleportToLure(chatMessageSource.getSourceUUID())) {
                    this.HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), new SLChatLureRequestEvent(chatMessageSource, this.agentUUID, improvedInstantMessage), true);
                    break;
                }
                break;
            }
            case 38: {
                this.HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), new SLChatFriendshipOfferedEvent(chatMessageSource, this.agentUUID, improvedInstantMessage), true);
                break;
            }
            case 39:
            case 40: {
                this.HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), new SLChatFriendshipResultEvent(chatMessageSource, this.agentUUID, improvedInstantMessage), true);
                if (dialog != 39 || chatMessageSource.getSourceType() != ChatMessageSource.ChatMessageSourceType.User) {
                    break;
                }
                final UUID sourceUUID2 = chatMessageSource.getSourceUUID();
                if (sourceUUID2 != null) {
                    this.userManager.getChatterList().getFriendManager().addFriend(sourceUUID2);
                    this.SendGenericMessage("requestonlinenotification", new String[] { sourceUUID2.toString() });
                    break;
                }
                break;
            }
            case 3: {
                this.HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), new SLChatGroupInvitationEvent(chatMessageSource, this.agentUUID, improvedInstantMessage), true);
                break;
            }
            case 0:
            case 20: {
                final SLChatTextEvent slChatTextEvent = new SLChatTextEvent(chatMessageSource, this.agentUUID, improvedInstantMessage, null);
                final ChatterID defaultChatter = chatMessageSource.getDefaultChatter(this.agentUUID);
                final boolean chatterActive = this.userManager.isChatterActive(defaultChatter);
                this.HandleChatEvent(defaultChatter, slChatTextEvent, true);
                if (this.userManager.isChatterMuted(defaultChatter) || dialog == 20 || improvedInstantMessage.MessageBlock_Field.Offline != 0 || improvedInstantMessage.MessageBlock_Field.Message.length == 0 || chatterActive || !(defaultChatter instanceof ChatterID.ChatterIDUser)) {
                    break;
                }
                final String autoresponse = SLGridConnection.getAutoresponse();
                if (!Strings.isNullOrEmpty(autoresponse)) {
                    this.SendInstantMessage(((ChatterID.ChatterIDUser)defaultChatter).getChatterUUID(), autoresponse, 20);
                    break;
                }
                break;
            }
            case 19:
            case 31: {
                this.HandleChatEvent(chatMessageSource.getDefaultChatter(this.agentUUID), new SLChatTextEvent(chatMessageSource, this.agentUUID, improvedInstantMessage, null), true);
                break;
            }
        }
    }
    
    private void HandleSessionIM(final ImprovedInstantMessage improvedInstantMessage, final ChatMessageSource chatMessageSource) {
        this.HandleChatEvent(ChatterID.getGroupChatterID(this.agentUUID, improvedInstantMessage.MessageBlock_Field.ID), new SLChatTextEvent(chatMessageSource, this.agentUUID, improvedInstantMessage, null), true);
    }
    
    private void HandleTeleportFailed(final LLSDNode llsdNode) {
        while (true) {
            try {
                Debug.Log("TeleportFailed: event = " + llsdNode.serializeToXML());
                if (this.teleportRequestSent) {
                    this.teleportRequestSent = false;
                    this.eventBus.publish(new SLTeleportResultEvent(false, "Teleport has failed."));
                }
            }
            catch (final IOException ex) {
                ex.printStackTrace();
                continue;
            }
            break;
        }
    }
    
    private void HandleTeleportFinish(final LLSDNode p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: astore_2       
        //     4: aload_2        
        //     5: invokespecial   java/lang/StringBuilder.<init>:()V
        //     8: aload_2        
        //     9: ldc_w           "TeleportFinish: event = "
        //    12: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    15: aload_1        
        //    16: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.serializeToXML:()Ljava/lang/String;
        //    19: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    22: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    25: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //    28: aload_0        
        //    29: getfield        com/lumiyaviewer/lumiya/slproto/SLAgentCircuit.teleportRequestSent:Z
        //    32: ifeq            233
        //    35: aload_0        
        //    36: iconst_0       
        //    37: putfield        com/lumiyaviewer/lumiya/slproto/SLAgentCircuit.teleportRequestSent:Z
        //    40: aload_1        
        //    41: ldc_w           "Info"
        //    44: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //    47: iconst_0       
        //    48: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byIndex:(I)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //    51: astore_2       
        //    52: aload_2        
        //    53: ldc_w           "SeedCapability"
        //    56: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //    59: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asString:()Ljava/lang/String;
        //    62: astore_1       
        //    63: aload_2        
        //    64: ldc_w           "SimIP"
        //    67: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //    70: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asBinary:()[B
        //    73: astore_3       
        //    74: ldc_w           "%d.%d.%d.%d"
        //    77: iconst_4       
        //    78: anewarray       Ljava/lang/Object;
        //    81: dup            
        //    82: iconst_0       
        //    83: aload_3        
        //    84: iconst_0       
        //    85: baload         
        //    86: sipush          255
        //    89: iand           
        //    90: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //    93: aastore        
        //    94: dup            
        //    95: iconst_1       
        //    96: aload_3        
        //    97: iconst_1       
        //    98: baload         
        //    99: sipush          255
        //   102: iand           
        //   103: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   106: aastore        
        //   107: dup            
        //   108: iconst_2       
        //   109: aload_3        
        //   110: iconst_2       
        //   111: baload         
        //   112: sipush          255
        //   115: iand           
        //   116: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   119: aastore        
        //   120: dup            
        //   121: iconst_3       
        //   122: aload_3        
        //   123: iconst_3       
        //   124: baload         
        //   125: sipush          255
        //   128: iand           
        //   129: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   132: aastore        
        //   133: invokestatic    java/lang/String.format:(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   136: astore_3       
        //   137: aload_2        
        //   138: ldc_w           "SimPort"
        //   141: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //   144: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asInt:()I
        //   147: istore          4
        //   149: new             Lcom/lumiyaviewer/lumiya/slproto/auth/SLAuthReply;
        //   152: astore_2       
        //   153: aload_2        
        //   154: aload_0        
        //   155: getfield        com/lumiyaviewer/lumiya/slproto/SLAgentCircuit.authReply:Lcom/lumiyaviewer/lumiya/slproto/auth/SLAuthReply;
        //   158: iconst_1       
        //   159: iconst_0       
        //   160: aload_0        
        //   161: getfield        com/lumiyaviewer/lumiya/slproto/SLAgentCircuit.authReply:Lcom/lumiyaviewer/lumiya/slproto/auth/SLAuthReply;
        //   164: getfield        com/lumiyaviewer/lumiya/slproto/auth/SLAuthReply.agentID:Ljava/util/UUID;
        //   167: aload_3        
        //   168: iload           4
        //   170: aload_1        
        //   171: invokespecial   com/lumiyaviewer/lumiya/slproto/auth/SLAuthReply.<init>:(Lcom/lumiyaviewer/lumiya/slproto/auth/SLAuthReply;ZZLjava/util/UUID;Ljava/lang/String;ILjava/lang/String;)V
        //   174: ldc_w           "new sim address: %s"
        //   177: iconst_1       
        //   178: anewarray       Ljava/lang/Object;
        //   181: dup            
        //   182: iconst_0       
        //   183: aload_2        
        //   184: getfield        com/lumiyaviewer/lumiya/slproto/auth/SLAuthReply.simAddress:Ljava/lang/String;
        //   187: aastore        
        //   188: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   191: aload_0        
        //   192: getfield        com/lumiyaviewer/lumiya/slproto/SLAgentCircuit.modules:Lcom/lumiyaviewer/lumiya/slproto/modules/SLModules;
        //   195: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLModules.avatarControl:Lcom/lumiyaviewer/lumiya/slproto/modules/SLAvatarControl;
        //   198: iconst_0       
        //   199: invokevirtual   com/lumiyaviewer/lumiya/slproto/modules/SLAvatarControl.setEnableAgentUpdates:(Z)V
        //   202: aload_0        
        //   203: getfield        com/lumiyaviewer/lumiya/slproto/SLAgentCircuit.gridConn:Lcom/lumiyaviewer/lumiya/slproto/SLGridConnection;
        //   206: aload_2        
        //   207: invokevirtual   com/lumiyaviewer/lumiya/slproto/SLGridConnection.HandleTeleportFinish:(Lcom/lumiyaviewer/lumiya/slproto/auth/SLAuthReply;)V
        //   210: return         
        //   211: astore_2       
        //   212: aload_2        
        //   213: invokevirtual   java/io/IOException.printStackTrace:()V
        //   216: goto            28
        //   219: astore_1       
        //   220: ldc_w           "TeleportFinish: LLSDException, teleport apparently failed"
        //   223: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   226: aload_1        
        //   227: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDException.printStackTrace:()V
        //   230: goto            210
        //   233: ldc_w           "TeleportFinish: stale teleport finish?"
        //   236: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   239: goto            210
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                                
        //  -----  -----  -----  -----  ----------------------------------------------------
        //  0      28     211    219    Ljava/io/IOException;
        //  40     210    219    233    Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0210:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void HandleTypingNotification(final ChatMessageSource chatMessageSource, final boolean b) {
        if (chatMessageSource instanceof ChatMessageSourceUser) {
            final UUID sourceUUID = chatMessageSource.getSourceUUID();
            if (sourceUUID != null) {
                if (b) {
                    if (this.typingUsers.add(sourceUUID)) {
                        this.userManager.getChatterList().updateUserTypingStatus(sourceUUID);
                    }
                }
                else if (this.typingUsers.remove(sourceUUID)) {
                    this.userManager.getChatterList().updateUserTypingStatus(sourceUUID);
                }
            }
        }
    }
    
    private void ProcessObjectSelection() {
        while (true) {
            while (true) {
                Iterator<Object> iterator = null;
                Object o = null;
                Label_0033: {
                    if (this.getNeedObjectNames() && (this.doingObjectSelection ^ true)) {
                        iterator = this.forceNeedObjectNames.values().iterator();
                        o = null;
                        break Label_0033;
                    }
                    return;
                    while (true) {
                    Label_0226:
                        while (true) {
                            Label_0443: {
                                synchronized (this.gridConn.parcelInfo.objectNamesQueue) {
                                    final Iterator<Object> iterator2 = this.gridConn.parcelInfo.objectNamesQueue.values().iterator();
                                    final Object o2;
                                    o = o2;
                                    while (iterator2.hasNext()) {
                                        final SLObjectInfo slObjectInfo = iterator2.next();
                                        Object o3;
                                        if ((o3 = o) == null) {
                                            o3 = new ObjectSelect();
                                            ((ObjectSelect)o3).AgentData_Field.AgentID = this.circuitInfo.agentID;
                                            ((ObjectSelect)o3).AgentData_Field.SessionID = this.circuitInfo.sessionID;
                                        }
                                        if (((ObjectSelect)o3).ObjectData_Fields.size() > 16) {
                                            break Label_0226;
                                        }
                                        o = new ObjectSelect.ObjectData();
                                        ((ObjectSelect.ObjectData)o).ObjectLocalID = slObjectInfo.localID;
                                        ((ObjectSelect)o3).ObjectData_Fields.add((ObjectSelect.ObjectData)o);
                                        slObjectInfo.nameRequested = true;
                                        slObjectInfo.nameRequestedAt = System.currentTimeMillis();
                                        this.objectNamesRequested.put(slObjectInfo.getId(), slObjectInfo);
                                        o = o3;
                                    }
                                    break Label_0443;
                                    monitorexit(this.gridConn.parcelInfo.objectNamesQueue);
                                    Object o3 = null;
                                    if (o3 != null) {
                                        Debug.Log("ObjectSelect: Sending ObjectSelect for " + ((ObjectSelect)o3).ObjectData_Fields.size() + " objects, " + this.gridConn.parcelInfo.objectNamesQueue.size() + " remains.");
                                        ((ObjectSelect)o3).isReliable = true;
                                        this.SendMessage((SLMessage)o3);
                                        this.lastObjectSelection = System.currentTimeMillis();
                                        this.doingObjectSelection = true;
                                    }
                                    return;
                                    o = new ObjectSelect.ObjectData();
                                    final SLObjectInfo slObjectInfo2;
                                    ((ObjectSelect.ObjectData)o).ObjectLocalID = slObjectInfo2.localID;
                                    ((ObjectSelect)o2).ObjectData_Fields.add((ObjectSelect.ObjectData)o);
                                    slObjectInfo2.nameRequested = true;
                                    slObjectInfo2.nameRequestedAt = System.currentTimeMillis();
                                    this.objectNamesRequested.put(slObjectInfo2.getId(), slObjectInfo2);
                                    o = o2;
                                    break;
                                }
                            }
                            Object o3 = o;
                            continue Label_0226;
                        }
                    }
                }
                Object o2 = o;
                if (!iterator.hasNext()) {
                    continue;
                }
                final SLObjectInfo slObjectInfo2 = iterator.next();
                if ((o2 = o) == null) {
                    o2 = new ObjectSelect();
                    ((ObjectSelect)o2).AgentData_Field.AgentID = this.circuitInfo.agentID;
                    ((ObjectSelect)o2).AgentData_Field.SessionID = this.circuitInfo.sessionID;
                }
                if (((ObjectSelect)o2).ObjectData_Fields.size() > 16) {
                    continue;
                }
                break;
            }
            continue;
        }
    }
    
    private void ProcessObjectSelectionTimeout() {
        for (final SLObjectInfo slObjectInfo : this.objectNamesRequested.values()) {
            final SLObjectInfo slObjectInfo2 = this.gridConn.parcelInfo.objectNamesQueue.remove(slObjectInfo.getId());
            if (slObjectInfo2 != null) {
                this.gridConn.parcelInfo.objectNamesQueue.put(slObjectInfo2.getId(), slObjectInfo2);
            }
            this.forceNeedObjectNames.remove(slObjectInfo.getId());
        }
        this.objectNamesRequested.clear();
    }
    
    private void SendAgentFOV() {
        final AgentFOV agentFOV = new AgentFOV();
        agentFOV.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentFOV.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        agentFOV.AgentData_Field.CircuitCode = this.circuitInfo.circuitCode;
        agentFOV.FOVBlock_Field.GenCounter = 0;
        agentFOV.FOVBlock_Field.VerticalAngle = 3.0543263f;
        agentFOV.isReliable = true;
        this.SendMessage(agentFOV);
    }
    
    private void SendCompleteAgentMovement() {
        final CompleteAgentMovement completeAgentMovement = new CompleteAgentMovement();
        completeAgentMovement.AgentData_Field.CircuitCode = this.circuitInfo.circuitCode;
        completeAgentMovement.AgentData_Field.AgentID = this.circuitInfo.agentID;
        completeAgentMovement.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        completeAgentMovement.isReliable = true;
        this.SendMessage(completeAgentMovement);
    }
    
    private void SendEstateOwnerMessage(String s, final String[] array) {
        final EstateOwnerMessage estateOwnerMessage = new EstateOwnerMessage();
        estateOwnerMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        estateOwnerMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        estateOwnerMessage.AgentData_Field.TransactionID = new UUID(0L, 0L);
        estateOwnerMessage.MethodData_Field.Method = SLMessage.stringToVariableOEM(s);
        estateOwnerMessage.MethodData_Field.Invoice = new UUID(0L, 0L);
        for (int i = 0; i < array.length; ++i) {
            s = array[i];
            final EstateOwnerMessage.ParamList e = new EstateOwnerMessage.ParamList();
            e.Parameter = SLMessage.stringToVariableOEM(s);
            estateOwnerMessage.ParamList_Fields.add(e);
        }
        estateOwnerMessage.isReliable = true;
        this.SendMessage(estateOwnerMessage);
    }
    
    private void SendGroupSessionStart(final UUID uuid) {
        final ImprovedInstantMessage improvedInstantMessage = new ImprovedInstantMessage();
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
        this.SendMessage(improvedInstantMessage);
    }
    
    private boolean SendInstantMessage(final UUID toAgentID, final String s, final int dialog) {
        if (!this.getModules().rlvController.canSendIM(toAgentID)) {
            return false;
        }
        final ImprovedInstantMessage improvedInstantMessage = new ImprovedInstantMessage();
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        improvedInstantMessage.MessageBlock_Field.FromGroup = false;
        improvedInstantMessage.MessageBlock_Field.ToAgentID = toAgentID;
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0;
        improvedInstantMessage.MessageBlock_Field.RegionID = new UUID(0L, 0L);
        improvedInstantMessage.MessageBlock_Field.Position = new LLVector3();
        improvedInstantMessage.MessageBlock_Field.Offline = 0;
        improvedInstantMessage.MessageBlock_Field.Dialog = dialog;
        improvedInstantMessage.MessageBlock_Field.ID = new UUID(toAgentID.getMostSignificantBits() ^ this.circuitInfo.agentID.getMostSignificantBits(), toAgentID.getLeastSignificantBits() ^ this.circuitInfo.agentID.getLeastSignificantBits());
        improvedInstantMessage.MessageBlock_Field.Timestamp = 0;
        improvedInstantMessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo");
        improvedInstantMessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF(s);
        improvedInstantMessage.MessageBlock_Field.BinaryBucket = new byte[0];
        improvedInstantMessage.isReliable = true;
        this.SendMessage(improvedInstantMessage);
        if (dialog != 20 && dialog != 41 && dialog != 42) {
            if (dialog == 26) {
                this.HandleChatEvent(ChatterID.getUserChatterID(this.agentUUID, toAgentID), new SLChatLureRequestedEvent(s, this.agentUUID), false);
            }
            else {
                this.HandleChatEvent(ChatterID.getUserChatterID(this.agentUUID, toAgentID), new SLChatTextEvent(new ChatMessageSourceUser(this.circuitInfo.agentID), this.agentUUID, s), false);
            }
        }
        return true;
    }
    
    private void SendRetrieveInstantMessages() {
        final RetrieveInstantMessages retrieveInstantMessages = new RetrieveInstantMessages();
        retrieveInstantMessages.AgentData_Field.AgentID = this.circuitInfo.agentID;
        retrieveInstantMessages.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        retrieveInstantMessages.isReliable = true;
        this.SendMessage(retrieveInstantMessages);
    }
    
    private UUID getActiveGroupID() {
        if (this.modules != null) {
            return this.modules.groupManager.getActiveGroupID();
        }
        return null;
    }
    
    private boolean getNeedObjectNames() {
        return (this.forceNeedObjectNames != null && !this.forceNeedObjectNames.isEmpty()) || (this.modules != null && this.modules.drawDistance.isObjectSelectEnabled());
    }
    
    private boolean isEventMuted(final ChatterID chatterID, final SLChatEvent slChatEvent) {
        if (this.modules != null) {
            final SLMuteList muteList = this.modules.muteList;
            final ChatMessageSource source = slChatEvent.getSource();
            if (source.getSourceType() == ChatMessageSource.ChatMessageSourceType.User) {
                if (muteList.isMuted(source.getSourceUUID(), MuteType.AGENT)) {
                    return true;
                }
            }
            else if (source.getSourceType() == ChatMessageSource.ChatMessageSourceType.Object) {
                final UUID sourceUUID = source.getSourceUUID();
                if (sourceUUID != null && !sourceUUID.equals(UUIDPool.ZeroUUID) && muteList.isMuted(sourceUUID, MuteType.OBJECT)) {
                    return true;
                }
                final String sourceName = source.getSourceName(this.userManager);
                if (sourceName != null && muteList.isMutedByName(sourceName)) {
                    return true;
                }
            }
            if (chatterID instanceof ChatterID.ChatterIDGroup) {
                final UUID chatterUUID = ((ChatterID.ChatterIDGroup)chatterID).getChatterUUID();
                if (!chatterUUID.equals(UUIDPool.ZeroUUID) && muteList.isMuted(chatterUUID, MuteType.GROUP)) {
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
    
    private void processMyAvatarUpdate(final SLObjectAvatarInfo slObjectAvatarInfo) {
        if (this.modules != null) {
            this.modules.avatarControl.setAgentPosition(slObjectAvatarInfo.getAbsolutePosition(), slObjectAvatarInfo.getObjectCoords().get(2));
        }
    }
    
    public void AcceptFriendship(UUID callingCardsFolderUUID, final UUID transactionID) {
        final UUID uuid = null;
        this.userManager.getChatterList().getFriendManager().addFriend(callingCardsFolderUUID);
        final AcceptFriendship acceptFriendship = new AcceptFriendship();
        acceptFriendship.AgentData_Field.AgentID = this.circuitInfo.agentID;
        acceptFriendship.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        callingCardsFolderUUID = uuid;
        if (this.modules != null) {
            callingCardsFolderUUID = this.modules.inventory.getCallingCardsFolderUUID();
        }
        final AcceptFriendship.FolderData e = new AcceptFriendship.FolderData();
        UUID zeroUUID;
        if ((zeroUUID = callingCardsFolderUUID) == null) {
            zeroUUID = UUIDPool.ZeroUUID;
        }
        e.FolderID = zeroUUID;
        acceptFriendship.FolderData_Fields.add(e);
        acceptFriendship.TransactionBlock_Field.TransactionID = transactionID;
        acceptFriendship.isReliable = true;
        this.SendMessage(acceptFriendship);
    }
    
    public void AcceptInventoryOffer(final int n, final boolean b, final UUID toAgentID, final UUID id, final UUID uuid) {
        final ImprovedInstantMessage improvedInstantMessage = new ImprovedInstantMessage();
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        improvedInstantMessage.MessageBlock_Field.FromGroup = false;
        improvedInstantMessage.MessageBlock_Field.ToAgentID = toAgentID;
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0;
        improvedInstantMessage.MessageBlock_Field.RegionID = new UUID(0L, 0L);
        improvedInstantMessage.MessageBlock_Field.Position = new LLVector3();
        improvedInstantMessage.MessageBlock_Field.Offline = 0;
        if (b) {
            improvedInstantMessage.MessageBlock_Field.Dialog = n + 1;
        }
        else {
            improvedInstantMessage.MessageBlock_Field.Dialog = n + 2;
        }
        improvedInstantMessage.MessageBlock_Field.ID = id;
        improvedInstantMessage.MessageBlock_Field.Timestamp = 0;
        improvedInstantMessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo");
        improvedInstantMessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF("");
        if (uuid != null) {
            final ByteBuffer wrap = ByteBuffer.wrap(new byte[16]);
            wrap.order(ByteOrder.BIG_ENDIAN);
            wrap.putLong(uuid.getMostSignificantBits());
            wrap.putLong(uuid.getLeastSignificantBits());
            wrap.position();
            improvedInstantMessage.MessageBlock_Field.BinaryBucket = wrap.array();
        }
        else {
            improvedInstantMessage.MessageBlock_Field.BinaryBucket = new byte[0];
        }
        improvedInstantMessage.isReliable = true;
        this.SendMessage(improvedInstantMessage);
    }
    
    public void AddFriend(final UUID toAgentID, final String s) {
        final ImprovedInstantMessage improvedInstantMessage = new ImprovedInstantMessage();
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        improvedInstantMessage.MessageBlock_Field.FromGroup = false;
        improvedInstantMessage.MessageBlock_Field.ToAgentID = toAgentID;
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0;
        improvedInstantMessage.MessageBlock_Field.RegionID = new UUID(0L, 0L);
        improvedInstantMessage.MessageBlock_Field.Position = new LLVector3();
        improvedInstantMessage.MessageBlock_Field.Offline = 0;
        improvedInstantMessage.MessageBlock_Field.Dialog = 38;
        improvedInstantMessage.MessageBlock_Field.ID = new UUID(toAgentID.getMostSignificantBits() ^ this.circuitInfo.agentID.getMostSignificantBits(), toAgentID.getLeastSignificantBits() ^ this.circuitInfo.agentID.getLeastSignificantBits());
        improvedInstantMessage.MessageBlock_Field.Timestamp = 0;
        improvedInstantMessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo");
        improvedInstantMessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF(s);
        improvedInstantMessage.MessageBlock_Field.BinaryBucket = new byte[0];
        improvedInstantMessage.isReliable = true;
        this.SendMessage(improvedInstantMessage);
    }
    
    public void BuyObject(final int objectLocalID, final byte saleType, final int salePrice) {
        UUID groupID = this.getActiveGroupID();
        final ObjectBuy objectBuy = new ObjectBuy();
        objectBuy.AgentData_Field.AgentID = this.circuitInfo.agentID;
        objectBuy.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        final ObjectBuy.AgentData agentData_Field = objectBuy.AgentData_Field;
        if (groupID == null) {
            groupID = UUIDPool.ZeroUUID;
        }
        agentData_Field.GroupID = groupID;
        objectBuy.AgentData_Field.CategoryID = this.getModules().inventory.rootFolder.uuid;
        final ObjectBuy.ObjectData e = new ObjectBuy.ObjectData();
        e.ObjectLocalID = objectLocalID;
        e.SaleType = saleType;
        e.SalePrice = salePrice;
        objectBuy.ObjectData_Fields.add(e);
        objectBuy.isReliable = true;
        this.SendMessage(objectBuy);
    }
    
    @Override
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
    
    public void DerezObject(final int objectLocalID, final EDeRezDestination eDeRezDestination) {
        UUID activeGroupID = this.getActiveGroupID();
        final DeRezObject deRezObject = new DeRezObject();
        deRezObject.AgentData_Field.AgentID = this.circuitInfo.agentID;
        deRezObject.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        final DeRezObject.AgentBlock agentBlock_Field = deRezObject.AgentBlock_Field;
        if (activeGroupID == null) {
            activeGroupID = new UUID(0L, 0L);
        }
        agentBlock_Field.GroupID = activeGroupID;
        deRezObject.AgentBlock_Field.Destination = eDeRezDestination.getCode();
        deRezObject.AgentBlock_Field.DestinationID = new UUID(0L, 0L);
        deRezObject.AgentBlock_Field.PacketCount = 1;
        deRezObject.AgentBlock_Field.PacketNumber = 0;
        deRezObject.AgentBlock_Field.TransactionID = UUID.randomUUID();
        final DeRezObject.ObjectData e = new DeRezObject.ObjectData();
        e.ObjectLocalID = objectLocalID;
        deRezObject.ObjectData_Fields.add(e);
        deRezObject.isReliable = true;
        this.SendMessage(deRezObject);
    }
    
    public void DoRequestPayPrice(final UUID objectID) {
        final SLObjectInfo slObjectInfo = this.gridConn.parcelInfo.allObjectsNearby.get(objectID);
        if (slObjectInfo != null) {
            if (slObjectInfo.getPayInfo() != null) {
                this.eventBus.publish(new SLObjectPayInfoEvent(slObjectInfo));
            }
            else {
                final RequestPayPrice requestPayPrice = new RequestPayPrice();
                requestPayPrice.ObjectData_Field.ObjectID = objectID;
                requestPayPrice.isReliable = true;
                this.SendMessage(requestPayPrice);
            }
        }
    }
    
    public void GenerateChatMoneyEvent(final UUID uuid, final int n, final int n2) {
        ChatMessageSource instance;
        if (uuid != null) {
            instance = new ChatMessageSourceUser(uuid);
        }
        else {
            instance = ChatMessageSourceUnknown.getInstance();
        }
        ChatterID chatterID;
        if (uuid != null) {
            chatterID = ChatterID.getUserChatterID(this.agentUUID, uuid);
        }
        else {
            chatterID = this.localChatterID;
        }
        this.HandleChatEvent(chatterID, new SLChatBalanceChangedEvent(instance, this.agentUUID, true, n, n2), true);
        if (this.modules != null) {
            this.modules.financialInfo.RecordChatEvent(uuid, n, n2);
        }
    }
    
    @Override
    public void HandleAgentMovementComplete(final AgentMovementComplete agentMovementComplete) {
        this.regionHandle = agentMovementComplete.Data_Field.RegionHandle;
        this.modules.avatarControl.setAgentPosition(agentMovementComplete.Data_Field.Position, null);
        Debug.Printf("Got agentPosition: %s", this.modules.avatarControl.getAgentPosition().getImmutablePosition());
        this.SendAgentFOV();
        this.modules.avatarAppearance.SendAgentWearablesRequest();
        this.SendRetrieveInstantMessages();
        this.modules.avatarControl.setEnableAgentUpdates(true);
    }
    
    @Override
    public void HandleAlertMessage(final AlertMessage alertMessage) {
        this.HandleChatEvent(this.localChatterID, new SLChatSystemMessageEvent(ChatMessageSourceUnknown.getInstance(), this.agentUUID, SLMessage.stringFromVariableOEM(alertMessage.AlertData_Field.Message)), true);
    }
    
    @Override
    public void HandleAvatarAnimation(final AvatarAnimation avatarAnimation) {
        final SLParcelInfo parcelInfo = this.gridConn.parcelInfo;
        if (parcelInfo != null && this.modules != null) {
            parcelInfo.ApplyAvatarAnimation(avatarAnimation, this.modules.avatarControl);
        }
    }
    
    @Override
    public void HandleAvatarAppearance(final AvatarAppearance avatarAppearance) {
        Debug.Log("Got AvatarAppearance, ID = " + avatarAppearance.Sender_Field.ID.toString() + " isTrial = " + avatarAppearance.Sender_Field.IsTrial + ", our ID = " + this.circuitInfo.agentID.toString());
        if (avatarAppearance.Sender_Field.ID.equals(this.circuitInfo.agentID) && this.modules != null) {
            this.modules.avatarAppearance.HandleAvatarAppearance(avatarAppearance);
        }
        final SLParcelInfo parcelInfo = this.gridConn.parcelInfo;
        if (parcelInfo != null) {
            parcelInfo.ApplyAvatarAppearance(avatarAppearance);
        }
    }
    
    @Override
    public void HandleAvatarInterestsReply(final AvatarInterestsReply avatarInterestsReply) {
        Debug.Log("got AvatarInterestsReply: wantToText = " + SLMessage.stringFromVariableOEM(avatarInterestsReply.PropertiesData_Field.WantToText));
        Debug.Log("got AvatarInterestsReply: skillText = " + SLMessage.stringFromVariableOEM(avatarInterestsReply.PropertiesData_Field.SkillsText));
    }
    
    public void HandleChatEvent(final ChatterID chatterID, final SLChatEvent slChatEvent, final boolean b) {
        if (this.isEventMuted(chatterID, slChatEvent)) {
            return;
        }
        this.userManager.getChatterList().getActiveChattersManager().HandleChatEvent(chatterID, slChatEvent, b);
    }
    
    @Override
    public void HandleChatFromSimulator(final ChatFromSimulator chatFromSimulator) {
        final SLModules modules = this.getModules();
        if (modules != null && modules.rlvController.onIncomingChat(chatFromSimulator)) {
            return;
        }
        final UUID sourceID = chatFromSimulator.ChatData_Field.SourceID;
        final String stringFromVariableOEM = SLMessage.stringFromVariableOEM(chatFromSimulator.ChatData_Field.FromName);
        final String stringFromVariableUTF = SLMessage.stringFromVariableUTF(chatFromSimulator.ChatData_Field.Message);
        if (chatFromSimulator.ChatData_Field.ChatType == 8 && chatFromSimulator.ChatData_Field.SourceType == 2 && stringFromVariableOEM.startsWith("#Firestorm LSL Bridge") && stringFromVariableUTF.startsWith("<bridgeURL>")) {
            return;
        }
        if (chatFromSimulator.ChatData_Field.SourceType == 1 && modules != null && !modules.rlvController.canRecvChat(stringFromVariableUTF, sourceID)) {
            return;
        }
        if (chatFromSimulator.ChatData_Field.Audible != 1) {
            return;
        }
        final int chatType = chatFromSimulator.ChatData_Field.ChatType;
        if (chatType != 6 && chatType != 4 && chatType != 5) {
            switch (chatFromSimulator.ChatData_Field.SourceType) {
                default: {
                    this.HandleChatEvent(this.localChatterID, new SLChatTextEvent(ChatMessageSourceUnknown.getInstance(), this.agentUUID, stringFromVariableUTF), true);
                    break;
                }
                case 1: {
                    this.HandleChatEvent(this.localChatterID, new SLChatTextEvent(new ChatMessageSourceUser(sourceID), this.agentUUID, stringFromVariableUTF), true);
                    break;
                }
                case 2: {
                    this.HandleChatEvent(this.localChatterID, new SLChatTextEvent(new ChatMessageSourceObject(sourceID, stringFromVariableOEM), this.agentUUID, stringFromVariableUTF), true);
                    break;
                }
            }
        }
    }
    
    @Override
    public void HandleImprovedInstantMessage(final ImprovedInstantMessage improvedInstantMessage) {
        final int dialog = improvedInstantMessage.MessageBlock_Field.Dialog;
        ChatMessageSource chatMessageSource;
        if (dialog == 19 || dialog == 31) {
            chatMessageSource = new ChatMessageSourceObject(improvedInstantMessage.AgentData_Field.AgentID, SLMessage.stringFromVariableOEM(improvedInstantMessage.MessageBlock_Field.FromAgentName));
        }
        else if (dialog == 3) {
            chatMessageSource = ChatMessageSourceUnknown.getInstance();
        }
        else if (UUIDPool.ZeroUUID.equals(improvedInstantMessage.AgentData_Field.AgentID)) {
            chatMessageSource = ChatMessageSourceUnknown.getInstance();
        }
        else if (!this.getModules().rlvController.canRecvIM((chatMessageSource = new ChatMessageSourceUser(improvedInstantMessage.AgentData_Field.AgentID)).getSourceUUID())) {
            return;
        }
        this.HandleIM(improvedInstantMessage, chatMessageSource);
    }
    
    @Override
    public void HandleImprovedTerseObjectUpdate(final ImprovedTerseObjectUpdate improvedTerseObjectUpdate) {
        final SLParcelInfo parcelInfo = this.gridConn.parcelInfo;
        final Iterator<Object> iterator = improvedTerseObjectUpdate.ObjectData_Fields.iterator();
        RequestMultipleObjects requestMultipleObjects = null;
        while (iterator.hasNext()) {
            final ImprovedTerseObjectUpdate.ObjectData objectData = iterator.next();
            final int localID = SLObjectInfo.getLocalID(objectData);
            final UUID uuid = parcelInfo.uuidsNearby.get(localID);
            SLObjectInfo slObjectInfo2;
            if (uuid != null) {
                final SLObjectInfo slObjectInfo = parcelInfo.allObjectsNearby.get(uuid);
                if ((slObjectInfo2 = slObjectInfo) != null) {
                    slObjectInfo.ApplyTerseObjectUpdate(objectData);
                    if (slObjectInfo instanceof SLObjectAvatarInfo && ((SLObjectAvatarInfo)slObjectInfo).isMyAvatar()) {
                        this.processMyAvatarUpdate((SLObjectAvatarInfo)slObjectInfo);
                        slObjectInfo2 = slObjectInfo;
                    }
                    else {
                        slObjectInfo2 = slObjectInfo;
                        if (slObjectInfo.isMyAttachment()) {
                            this.processMyAttachmentUpdate(slObjectInfo);
                            slObjectInfo2 = slObjectInfo;
                        }
                    }
                }
            }
            else {
                slObjectInfo2 = null;
            }
            if (slObjectInfo2 == null) {
                RequestMultipleObjects requestMultipleObjects2;
                if ((requestMultipleObjects2 = requestMultipleObjects) == null) {
                    requestMultipleObjects2 = new RequestMultipleObjects();
                    requestMultipleObjects2.AgentData_Field.AgentID = this.circuitInfo.agentID;
                    requestMultipleObjects2.AgentData_Field.SessionID = this.circuitInfo.sessionID;
                }
                final RequestMultipleObjects.ObjectData e = new RequestMultipleObjects.ObjectData();
                e.CacheMissType = 0;
                e.ID = localID;
                requestMultipleObjects2.ObjectData_Fields.add(e);
                requestMultipleObjects = requestMultipleObjects2;
            }
        }
        if (requestMultipleObjects != null) {
            Debug.Log("Handing cache miss for terse update: " + requestMultipleObjects.ObjectData_Fields.size() + " objects.");
            requestMultipleObjects.isReliable = true;
            this.SendMessage(requestMultipleObjects);
        }
    }
    
    @Override
    public void HandleKillObject(final KillObject killObject) {
        final SLParcelInfo parcelInfo = this.gridConn.parcelInfo;
        final Iterator<Object> iterator = killObject.ObjectData_Fields.iterator();
        boolean b = false;
        while (iterator.hasNext()) {
            if (parcelInfo.killObject(this, iterator.next().ID)) {
                b = true;
            }
        }
        if (b) {
            this.objectPropertiesRateLimiter.fire();
        }
    }
    
    @Override
    public void HandleLayerData(final LayerData layerData) {
        if (layerData.LayerID_Field.Type == 76) {
            final SLParcelInfo parcelInfo = this.gridConn.parcelInfo;
            if (parcelInfo != null) {
                parcelInfo.terrainData.ProcessLayerData(layerData.LayerDataData_Field.Data);
            }
        }
    }
    
    @Override
    public void HandleLoadURL(final LoadURL loadURL) {
        this.HandleChatEvent(this.localChatterID, new SLChatTextEvent(new ChatMessageSourceObject(loadURL.Data_Field.ObjectID, SLMessage.stringFromVariableOEM(loadURL.Data_Field.ObjectName)), this.agentUUID, loadURL), true);
    }
    
    @Override
    public void HandleObjectProperties(final ObjectProperties objectProperties) {
        Debug.Log("ObjectProperties: " + objectProperties.ObjectData_Fields.size() + " ObjectSelect replies. Reqd " + this.objectNamesRequested.size() + " obj, remains " + this.gridConn.parcelInfo.objectNamesQueue.size() + " objects.");
        for (final ObjectProperties.ObjectData objectData : objectProperties.ObjectData_Fields) {
            final SLObjectInfo slObjectInfo = this.gridConn.parcelInfo.objectNamesQueue.remove(objectData.ObjectID);
            if (slObjectInfo != null) {
                slObjectInfo.ApplyObjectProperties(objectData);
                this.userManager.getObjectsManager().requestObjectProfileUpdate(slObjectInfo.localID);
            }
            final SLObjectInfo slObjectInfo2 = this.forceNeedObjectNames.remove(objectData.ObjectID);
            if (slObjectInfo2 != null) {
                slObjectInfo2.ApplyObjectProperties(objectData);
                this.userManager.getObjectsManager().requestObjectProfileUpdate(slObjectInfo2.localID);
                final SLObjectInfo parentObject = slObjectInfo2.getParentObject();
                if (parentObject != null) {
                    final UUID id = parentObject.getId();
                    if (id != null) {
                        this.userManager.getObjectsManager().requestTouchableChildrenUpdate(id);
                    }
                }
            }
            this.objectNamesRequested.remove(objectData.ObjectID);
        }
        if (this.objectNamesRequested.isEmpty()) {
            this.doingObjectSelection = false;
            this.ProcessObjectSelection();
        }
        this.objectPropertiesRateLimiter.fire();
    }
    
    @Override
    public void HandleObjectUpdate(final ObjectUpdate objectUpdate) {
        final SLParcelInfo parcelInfo = this.gridConn.parcelInfo;
        final Iterator<Object> iterator = objectUpdate.ObjectData_Fields.iterator();
        int n = 0;
        int n2 = 0;
        while (iterator.hasNext()) {
            final ObjectUpdate.ObjectData objectData = iterator.next();
            int n3;
            int n6;
            if (objectData.PCode == 47 || objectData.PCode == 9) {
                SLObjectInfo slObjectInfo = parcelInfo.allObjectsNearby.get(objectData.FullID);
                int n4;
                if (slObjectInfo != null) {
                    final int parentID = slObjectInfo.parentID;
                    slObjectInfo.ApplyObjectUpdate(objectData);
                    parcelInfo.updateObjectParent(parentID, slObjectInfo);
                    n3 = n;
                    if (slObjectInfo.parentID != parentID) {
                        n3 = n;
                        if (slObjectInfo instanceof SLObjectAvatarInfo) {
                            n3 = n;
                            if (((SLObjectAvatarInfo)slObjectInfo).isMyAvatar()) {
                                n3 = 1;
                            }
                        }
                    }
                    n4 = 1;
                }
                else {
                    final SLObjectInfo create = SLObjectInfo.create(this.agentUUID, objectData, this.circuitInfo.agentID);
                    int n5 = n2;
                    if (parcelInfo.addObject(create)) {
                        n5 = 1;
                    }
                    slObjectInfo = create;
                    n3 = n;
                    n4 = n5;
                    if (create instanceof SLObjectAvatarInfo) {
                        slObjectInfo = create;
                        n3 = n;
                        n4 = n5;
                        if (((SLObjectAvatarInfo)create).isMyAvatar()) {
                            Debug.Log("ObjectUpdate: got my avatar (normal)");
                            parcelInfo.setAgentAvatar((SLObjectAvatarInfo)create);
                            this.modules.avatarAppearance.OnMyAvatarCreated((SLObjectAvatarInfo)create);
                            n3 = 1;
                            slObjectInfo = create;
                            n4 = n5;
                        }
                    }
                }
                if (slObjectInfo instanceof SLObjectAvatarInfo && ((SLObjectAvatarInfo)slObjectInfo).isMyAvatar()) {
                    this.processMyAvatarUpdate((SLObjectAvatarInfo)slObjectInfo);
                    n6 = n4;
                }
                else if (slObjectInfo.isMyAttachment()) {
                    this.processMyAttachmentUpdate(slObjectInfo);
                    n6 = n4;
                }
                else {
                    n6 = n4;
                }
            }
            else {
                final int n7 = n2;
                n3 = n;
                n6 = n7;
            }
            final int n8 = n3;
            n2 = n6;
            n = n8;
        }
        if (n != 0) {
            this.userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value);
        }
        if (n2 != 0) {
            this.ProcessObjectSelection();
            this.objectPropertiesRateLimiter.fire();
        }
    }
    
    @Override
    public void HandleObjectUpdateCached(final ObjectUpdateCached objectUpdateCached) {
        final RequestMultipleObjects requestMultipleObjects = new RequestMultipleObjects();
        requestMultipleObjects.AgentData_Field.AgentID = this.circuitInfo.agentID;
        requestMultipleObjects.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        for (final ObjectUpdateCached.ObjectData objectData : objectUpdateCached.ObjectData_Fields) {
            final RequestMultipleObjects.ObjectData e = new RequestMultipleObjects.ObjectData();
            e.CacheMissType = 0;
            e.ID = objectData.ID;
            requestMultipleObjects.ObjectData_Fields.add(e);
        }
        requestMultipleObjects.isReliable = true;
        this.SendMessage(requestMultipleObjects);
    }
    
    @Override
    public void HandleObjectUpdateCompressed(final ObjectUpdateCompressed objectUpdateCompressed) {
        final SLParcelInfo parcelInfo = this.gridConn.parcelInfo;
        final Iterator<Object> iterator = objectUpdateCompressed.ObjectData_Fields.iterator();
        int n = 0;
        int n2 = 0;
    Label_0498:
        while (iterator.hasNext()) {
            while (true) {
                final ObjectUpdateCompressed.ObjectData objectData = iterator.next();
                int n3 = n;
                int n4 = n2;
                int n5 = n;
                int n6 = n2;
            Label_0235_Outer:
                while (true) {
                Label_0542:
                    while (true) {
                        Label_0536: {
                            while (true) {
                                try {
                                    final int localID = SLObjectInfo.getLocalID(objectData);
                                    n3 = n;
                                    n4 = n2;
                                    n5 = n;
                                    n6 = n2;
                                    final UUID uuid = parcelInfo.uuidsNearby.get(localID);
                                    if (uuid != null) {
                                        n3 = n;
                                        n4 = n2;
                                        n5 = n;
                                        n6 = n2;
                                        SLObjectInfo create = parcelInfo.allObjectsNearby.get(uuid);
                                        int n8;
                                        if (create != null) {
                                            n3 = n;
                                            n4 = n2;
                                            n5 = n;
                                            n6 = n2;
                                            final int parentID = create.parentID;
                                            n3 = n;
                                            n4 = n2;
                                            n5 = n;
                                            n6 = n2;
                                            create.ApplyObjectUpdate(objectData);
                                            n3 = n;
                                            n4 = n2;
                                            n5 = n;
                                            n6 = n2;
                                            parcelInfo.updateObjectParent(parentID, create);
                                            n3 = n;
                                            n4 = n2;
                                            n5 = n;
                                            n6 = n2;
                                            if (create.parentID == parentID) {
                                                break Label_0536;
                                            }
                                            final int n7 = 1;
                                            n6 = 1;
                                            n8 = n7;
                                            n2 = n6;
                                        }
                                        else {
                                            n3 = n;
                                            n4 = n2;
                                            n5 = n;
                                            n6 = n2;
                                            create = SLObjectInfo.create(objectData);
                                            int n9 = n2;
                                            n3 = n;
                                            n4 = n2;
                                            n5 = n;
                                            n6 = n2;
                                            if (parcelInfo.addObject(create)) {
                                                n9 = 1;
                                            }
                                            n6 = 0;
                                            n2 = n9;
                                            n8 = n6;
                                        }
                                        n3 = n;
                                        n4 = n2;
                                        n5 = n;
                                        n6 = n2;
                                        int myAvatar;
                                        if (create instanceof SLObjectAvatarInfo) {
                                            n3 = n;
                                            n4 = n2;
                                            n5 = n;
                                            n6 = n2;
                                            myAvatar = (((SLObjectAvatarInfo)create).isMyAvatar() ? 1 : 0);
                                        }
                                        else {
                                            myAvatar = 0;
                                        }
                                        int n10;
                                        if (myAvatar != 0) {
                                            if (n8 != 0) {
                                                n = 1;
                                            }
                                            n3 = n;
                                            n4 = n2;
                                            n5 = n;
                                            n6 = n2;
                                            this.processMyAvatarUpdate((SLObjectAvatarInfo)create);
                                            n10 = n;
                                        }
                                        else {
                                            n10 = n;
                                            n3 = n;
                                            n4 = n2;
                                            n5 = n;
                                            n6 = n2;
                                            if (create.isMyAttachment()) {
                                                n3 = n;
                                                n4 = n2;
                                                n5 = n;
                                                n6 = n2;
                                                this.processMyAttachmentUpdate(create);
                                                n10 = n;
                                            }
                                        }
                                        n = n10;
                                        break;
                                    }
                                    break Label_0542;
                                }
                                catch (final UnsupportedObjectTypeException ex) {
                                    n = n3;
                                    n2 = n4;
                                    continue;
                                }
                                catch (final Exception ex2) {
                                    Debug.Warning(ex2);
                                    n = n5;
                                    n2 = n6;
                                    continue;
                                }
                                break;
                            }
                            break Label_0498;
                        }
                        final int n7 = 0;
                        continue;
                    }
                    SLObjectInfo create = null;
                    continue Label_0235_Outer;
                }
            }
        }
        if (n2 != 0) {
            this.ProcessObjectSelection();
            this.objectPropertiesRateLimiter.fire();
        }
        if (n != 0) {
            this.userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value);
        }
    }
    
    @Override
    public void HandleOfflineNotification(final OfflineNotification offlineNotification) {
        final ArrayList list = new ArrayList(offlineNotification.AgentBlock_Fields.size());
        final Iterator<Object> iterator = offlineNotification.AgentBlock_Fields.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().AgentID);
        }
        this.userManager.getChatterList().getFriendManager().setUsersOnline(list, false);
    }
    
    @Override
    public void HandleOnlineNotification(final OnlineNotification onlineNotification) {
        final ArrayList list = new ArrayList(onlineNotification.AgentBlock_Fields.size());
        final Iterator<Object> iterator = onlineNotification.AgentBlock_Fields.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().AgentID);
        }
        this.userManager.getChatterList().getFriendManager().setUsersOnline(list, true);
    }
    
    @Override
    public void HandlePayPriceReply(final PayPriceReply payPriceReply) {
        final SLObjectInfo slObjectInfo = this.gridConn.parcelInfo.allObjectsNearby.get(payPriceReply.ObjectData_Field.ObjectID);
        if (slObjectInfo != null) {
            final int defaultPayPrice = payPriceReply.ObjectData_Field.DefaultPayPrice;
            final int[] array = new int[payPriceReply.ButtonData_Fields.size()];
            for (int i = 0; i < payPriceReply.ButtonData_Fields.size(); ++i) {
                array[i] = ((PayPriceReply.ButtonData)payPriceReply.ButtonData_Fields.get(i)).PayButton;
            }
            slObjectInfo.setPayInfo(PayInfo.create(defaultPayPrice, array));
            if (this.userManager != null) {
                this.userManager.getObjectsManager().requestObjectProfileUpdate(slObjectInfo.localID);
            }
            this.eventBus.publish(new SLObjectPayInfoEvent(slObjectInfo));
        }
    }
    
    @Override
    public void HandleRegionHandshake(final RegionHandshake regionHandshake) {
        if (!this.authReply.isTemporary) {
            final RegionHandshakeReply regionHandshakeReply = new RegionHandshakeReply();
            regionHandshakeReply.AgentData_Field.AgentID = this.circuitInfo.agentID;
            regionHandshakeReply.AgentData_Field.SessionID = this.circuitInfo.sessionID;
            regionHandshakeReply.RegionInfo_Field.Flags = 0;
            if (this.gridConn != null && this.gridConn.parcelInfo != null) {
                this.gridConn.parcelInfo.terrainData.ApplyRegionInfo(regionHandshake.RegionInfo_Field);
            }
            this.SendMessage(regionHandshakeReply);
            this.regionName = SLMessage.stringFromVariableOEM(regionHandshake.RegionInfo_Field.SimName);
            if (regionHandshake.RegionInfo2_Field != null && regionHandshake.RegionInfo2_Field.RegionID != null) {
                this.regionID = regionHandshake.RegionInfo2_Field.RegionID;
            }
            this.isEstateManager = regionHandshake.RegionInfo_Field.IsEstateManager;
            this.agentNameSubscription = this.userManager.getUserNames().subscribe(this.circuitInfo.agentID, new _$Lambda$K1xWCpEh0d4XNuVVYxGUJwEFRxU(this));
            if (this.eventBus != null) {
                this.eventBus.publish(new SLRegionInfoChangedEvent());
            }
        }
    }
    
    @Override
    public void HandleScriptDialog(final ScriptDialog scriptDialog) {
        int n = 0;
        String[] array = null;
        int n4 = 0;
        Label_0090: {
            if (scriptDialog.Buttons_Fields.size() > 0) {
                array = new String[scriptDialog.Buttons_Fields.size()];
                final Iterator<Object> iterator = scriptDialog.Buttons_Fields.iterator();
                int n2 = 0;
                while (iterator.hasNext()) {
                    array[n2] = SLMessage.stringFromVariableUTF(iterator.next().ButtonLabel);
                    if (array[n2].equals("!!llTextBox!!")) {
                        final int n3 = 1;
                        n = n2;
                        n4 = n3;
                        break Label_0090;
                    }
                    ++n2;
                }
                n4 = 0;
            }
            else {
                array = null;
                n4 = 0;
            }
        }
        if (n4 == 0) {
            this.HandleChatEvent(this.localChatterID, new SLChatScriptDialog(scriptDialog, this.agentUUID, array), true);
        }
        else {
            this.HandleChatEvent(this.localChatterID, new SLChatTextBoxDialog(scriptDialog, this.agentUUID, n), true);
        }
    }
    
    @Override
    public void HandleSimulatorViewerTimeMessage(final SimulatorViewerTimeMessage simulatorViewerTimeMessage) {
        if (!this.authReply.isTemporary && this.gridConn != null && this.gridConn.parcelInfo != null) {
            final float n = simulatorViewerTimeMessage.TimeInfo_Field.SunPhase / 6.2831855f + 0.25f;
            this.gridConn.parcelInfo.setSunHour((float)(n - Math.floor(n)));
        }
    }
    
    @Override
    public void HandleTeleportFailed(final TeleportFailed teleportFailed) {
        Debug.Log("TeleportFailed: reason = " + SLMessage.stringFromVariableOEM(teleportFailed.Info_Field.Reason));
        this.teleportRequestSent = false;
        this.eventBus.publish(new SLTeleportResultEvent(false, SLMessage.stringFromVariableOEM(teleportFailed.Info_Field.Reason)));
    }
    
    @Override
    public void HandleTeleportLocal(final TeleportLocal teleportLocal) {
        this.teleportRequestSent = false;
        this.eventBus.publish(new SLTeleportResultEvent(true, null));
    }
    
    @Override
    public void HandleTeleportProgress(final TeleportProgress teleportProgress) {
        Debug.Log("Teleport progress: flags = " + teleportProgress.Info_Field.TeleportFlags + ", progress = " + SLMessage.stringFromVariableOEM(teleportProgress.Info_Field.Message));
    }
    
    @Override
    public void HandleTeleportStart(final TeleportStart teleportStart) {
        Debug.Log("TeleportStart: flags = " + teleportStart.Info_Field.TeleportFlags);
    }
    
    public void OfferInventoryItem(final UUID uuid, final SLInventoryEntry slInventoryEntry) {
        this.userManager.getInventoryManager().getExecutor().execute(new _$Lambda$K1xWCpEh0d4XNuVVYxGUJwEFRxU$1(this, slInventoryEntry, uuid));
    }
    
    public void OfferTeleport(final UUID targetID, final String s) {
        final StartLure startLure = new StartLure();
        startLure.AgentData_Field.AgentID = this.circuitInfo.agentID;
        startLure.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        startLure.Info_Field.Message = SLMessage.stringToVariableUTF(s);
        final StartLure.TargetData e = new StartLure.TargetData();
        e.TargetID = targetID;
        startLure.TargetData_Fields.add(e);
        startLure.isReliable = true;
        this.SendMessage(startLure);
    }
    
    @Override
    public void OnCapsEvent(final CapsEvent e) {
        try {
            this.capsEventQueue.add(e);
            this.selector.wakeup();
        }
        catch (final Exception ex) {}
    }
    
    @Override
    public void ProcessIdle() {
        if (this.doingObjectSelection && System.currentTimeMillis() > this.lastObjectSelection + 15000L) {
            this.doingObjectSelection = false;
            this.ProcessObjectSelectionTimeout();
        }
        if (!this.teleportRequestSent && this.getNeedObjectNames() && (this.doingObjectSelection ^ true) && System.currentTimeMillis() >= this.lastObjectSelection + 500L) {
            this.ProcessObjectSelection();
        }
        if (!this.agentPaused) {
            final long currentTimeMillis = System.currentTimeMillis();
            if (!GridConnectionService.hasVisibleActivities()) {
                if (currentTimeMillis >= this.lastVisibleActivities + 10000L) {
                    this.DoAgentPause();
                }
            }
            else {
                this.lastVisibleActivities = currentTimeMillis;
            }
        }
        if (this.objectPropertiesRateLimiter != null) {
            this.objectPropertiesRateLimiter.firePending();
        }
    }
    
    @Override
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
    
    @Override
    public void ProcessTimeout() {
        super.ProcessTimeout();
        if (this.modules != null) {
            this.modules.avatarControl.setEnableAgentUpdates(false);
        }
        if (!this.authReply.isTemporary) {
            this.gridConn.processDisconnect(false, "Connection has timed out.");
        }
    }
    
    @Override
    public void ProcessWakeup() {
        super.ProcessWakeup();
        try {
            while (true) {
                final CapsEvent capsEvent = this.capsEventQueue.poll();
                if (capsEvent == null) {
                    break;
                }
                this.HandleCapsEvent(capsEvent);
            }
        }
        catch (final Exception ex) {}
        this.ProcessIdle();
    }
    
    public void RemoveFriend(final UUID otherID) {
        final TerminateFriendship terminateFriendship = new TerminateFriendship();
        terminateFriendship.AgentData_Field.AgentID = this.circuitInfo.agentID;
        terminateFriendship.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        terminateFriendship.ExBlock_Field.OtherID = otherID;
        terminateFriendship.isReliable = true;
        this.SendMessage(terminateFriendship);
        this.userManager.getChatterList().getFriendManager().removeFriend(otherID);
    }
    
    void RequestObjectName(final SLObjectInfo slObjectInfo) {
        if (slObjectInfo.getId() != null && !this.objectNamesRequested.containsKey(slObjectInfo.getId()) && (this.forceNeedObjectNames.containsKey(slObjectInfo.getId()) ^ true)) {
            this.forceNeedObjectNames.put(slObjectInfo.getId(), slObjectInfo);
        }
        this.TryWakeUp();
    }
    
    public void RequestTeleport(final UUID uuid, final String s) {
        this.SendInstantMessage(uuid, s, 26);
    }
    
    public boolean RestartRegion(final int i) {
        if (this.isEstateManager) {
            this.SendEstateOwnerMessage("restart", new String[] { Integer.toString(i) });
            return true;
        }
        return false;
    }
    
    public void RezObject(final SLInventoryEntry slInventoryEntry) {
        final UUID uuid = null;
        final UUID zeroUUID = UUIDPool.ZeroUUID;
        while (true) {
            Label_0640: {
                if (this.userManager == null) {
                    break Label_0640;
                }
                final CurrentLocationInfo currentLocationInfoSnapshot = this.userManager.getCurrentLocationInfoSnapshot();
                if (currentLocationInfoSnapshot == null) {
                    break Label_0640;
                }
                final ParcelData parcelData = currentLocationInfoSnapshot.parcelData();
                if (parcelData == null || !parcelData.isGroupOwned()) {
                    break Label_0640;
                }
                UUID obj = parcelData.getOwnerID();
                if (obj != null) {
                    if (UUIDPool.ZeroUUID.equals(obj)) {
                        obj = uuid;
                    }
                }
                if (obj != null) {
                    final AvatarGroupList avatarGroupList = this.userManager.getChatterList().getGroupManager().getAvatarGroupList();
                    if (avatarGroupList == null || !avatarGroupList.Groups.containsKey(obj)) {
                        obj = zeroUUID;
                    }
                }
                else {
                    obj = this.getActiveGroupID();
                }
                UUID zeroUUID2;
                if ((zeroUUID2 = obj) == null) {
                    zeroUUID2 = UUIDPool.ZeroUUID;
                }
                final RezObject rezObject = new RezObject();
                rezObject.AgentData_Field.AgentID = this.circuitInfo.agentID;
                rezObject.AgentData_Field.SessionID = this.circuitInfo.sessionID;
                rezObject.AgentData_Field.GroupID = zeroUUID2;
                rezObject.RezData_Field.FromTaskID = UUIDPool.ZeroUUID;
                rezObject.RezData_Field.BypassRaycast = 1;
                rezObject.RezData_Field.RayStart = this.modules.avatarControl.getAgentPosition().getPosition();
                rezObject.RezData_Field.RayEnd = rezObject.RezData_Field.RayStart.getRotatedOffset(1.5f, this.getModules().avatarControl.getAgentHeading());
                rezObject.RezData_Field.RayEndIsIntersection = true;
                rezObject.RezData_Field.RayTargetID = UUIDPool.ZeroUUID;
                rezObject.RezData_Field.RezSelected = false;
                rezObject.RezData_Field.RemoveItem = false;
                rezObject.RezData_Field.ItemFlags = 0;
                rezObject.RezData_Field.GroupMask = slInventoryEntry.groupMask;
                rezObject.RezData_Field.EveryoneMask = slInventoryEntry.everyoneMask;
                rezObject.RezData_Field.NextOwnerMask = slInventoryEntry.nextOwnerMask;
                rezObject.InventoryData_Field.ItemID = slInventoryEntry.uuid;
                rezObject.InventoryData_Field.FolderID = slInventoryEntry.parentUUID;
                rezObject.InventoryData_Field.CreatorID = slInventoryEntry.creatorUUID;
                rezObject.InventoryData_Field.OwnerID = slInventoryEntry.ownerUUID;
                rezObject.InventoryData_Field.GroupID = slInventoryEntry.groupUUID;
                rezObject.InventoryData_Field.BaseMask = slInventoryEntry.baseMask;
                rezObject.InventoryData_Field.OwnerMask = slInventoryEntry.ownerMask;
                rezObject.InventoryData_Field.GroupMask = slInventoryEntry.groupMask;
                rezObject.InventoryData_Field.EveryoneMask = slInventoryEntry.everyoneMask;
                rezObject.InventoryData_Field.NextOwnerMask = slInventoryEntry.nextOwnerMask;
                rezObject.InventoryData_Field.GroupOwned = slInventoryEntry.isGroupOwned;
                rezObject.InventoryData_Field.TransactionID = UUID.randomUUID();
                rezObject.InventoryData_Field.Type = slInventoryEntry.assetType;
                rezObject.InventoryData_Field.InvType = slInventoryEntry.invType;
                rezObject.InventoryData_Field.Flags = slInventoryEntry.flags;
                rezObject.InventoryData_Field.SaleType = slInventoryEntry.saleType;
                rezObject.InventoryData_Field.SalePrice = slInventoryEntry.salePrice;
                rezObject.InventoryData_Field.Name = SLMessage.stringToVariableOEM(slInventoryEntry.name);
                rezObject.InventoryData_Field.Description = SLMessage.stringToVariableOEM(slInventoryEntry.description);
                rezObject.InventoryData_Field.CreationDate = slInventoryEntry.creationDate;
                rezObject.InventoryData_Field.CRC = 0;
                rezObject.isReliable = true;
                if ((slInventoryEntry.ownerMask & 0x8000) == 0x0) {
                    rezObject.setEventListener(new SLMessageEventListener() {
                        final /* synthetic */ UUID val$folderUUID = slInventoryEntry.parentUUID;
                        
                        @Override
                        public void onMessageAcknowledged(final SLMessage slMessage) {
                            if (SLAgentCircuit.this.userManager != null) {
                                SLAgentCircuit.this.userManager.getInventoryManager().requestFolderUpdate(this.val$folderUUID);
                            }
                        }
                        
                        @Override
                        public void onMessageTimeout(final SLMessage slMessage) {
                        }
                    });
                }
                this.SendMessage(rezObject);
                return;
            }
            UUID obj = null;
            continue;
        }
    }
    
    public void SendChatMessage(@Nonnull final ChatterID chatterID, final String s) {
        switch (-getcom-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues()[chatterID.getChatterType().ordinal()]) {
            case 2: {
                this.SendLocalChatMessage(s);
                break;
            }
            case 3: {
                this.SendInstantMessage(chatterID.getOptionalChatterUUID(), s);
                break;
            }
            case 1: {
                this.SendGroupInstantMessage(chatterID.getOptionalChatterUUID(), s);
                break;
            }
        }
    }
    
    public void SendGenericMessage(String s, final String[] array) {
        final GenericMessage genericMessage = new GenericMessage();
        genericMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        genericMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        genericMessage.AgentData_Field.TransactionID = new UUID(0L, 0L);
        genericMessage.MethodData_Field.Method = SLMessage.stringToVariableOEM(s);
        genericMessage.MethodData_Field.Invoice = new UUID(0L, 0L);
        for (int i = 0; i < array.length; ++i) {
            s = array[i];
            final GenericMessage.ParamList e = new GenericMessage.ParamList();
            e.Parameter = SLMessage.stringToVariableOEM(s);
            genericMessage.ParamList_Fields.add(e);
        }
        genericMessage.isReliable = true;
        this.SendMessage(genericMessage);
    }
    
    public void SendGroupInstantMessage(final UUID uuid, final String s) {
        final ImprovedInstantMessage improvedInstantMessage = new ImprovedInstantMessage();
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
        improvedInstantMessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF(s);
        improvedInstantMessage.MessageBlock_Field.BinaryBucket = new byte[1];
        improvedInstantMessage.isReliable = true;
        synchronized (this.startedGroupSessions) {
            if (!this.startedGroupSessions.contains(uuid)) {
                this.SendGroupSessionStart(uuid);
                this.pendingGroupMessages.add(improvedInstantMessage);
            }
            else {
                this.SendMessage(improvedInstantMessage);
            }
        }
    }
    
    public boolean SendInstantMessage(final UUID uuid, final String s) {
        return this.SendInstantMessage(uuid, s, 0);
    }
    
    public void SendLocalChatMessage(final String s) {
        final int n = 0;
        int int1;
        final int n2 = int1 = 0;
        String trim = s;
        while (true) {
            if (!s.startsWith("/")) {
                break Label_0102;
            }
            int index = 1;
            int n3 = 0;
            while (index < s.length() && Character.isDigit(s.charAt(index))) {
                ++n3;
                ++index;
            }
            int1 = n2;
            trim = s;
            if (n3 < 0) {
                break Label_0102;
            }
            int1 = n;
            try {
                final int n4 = int1 = Integer.parseInt(s.substring(1, n3 + 1));
                trim = s.substring(n3 + 1).trim();
                int1 = n4;
                if (!this.getModules().rlvController.onSendLocalChat(int1, trim)) {
                    return;
                }
            }
            catch (final Exception ex) {
                ex.printStackTrace();
                trim = s;
                continue;
            }
            break;
        }
        final ChatFromViewer chatFromViewer = new ChatFromViewer();
        chatFromViewer.AgentData_Field.AgentID = this.circuitInfo.agentID;
        chatFromViewer.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        chatFromViewer.ChatData_Field.Channel = int1;
        chatFromViewer.ChatData_Field.Type = 1;
        chatFromViewer.ChatData_Field.Message = SLMessage.stringToVariableUTF(trim);
        chatFromViewer.isReliable = true;
        this.SendMessage(chatFromViewer);
    }
    
    void SendLogoutRequest() {
        Debug.Log("Logout: Sending logout request.");
        this.modules.avatarControl.setEnableAgentUpdates(false);
        final LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        logoutRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        logoutRequest.isReliable = true;
        logoutRequest.setEventListener(new SLMessageEventListener() {
            @Override
            public void onMessageAcknowledged(final SLMessage slMessage) {
                Debug.Log("Logout: Logout request acknowledged.");
                SLAgentCircuit.this.gridConn.processDisconnect(true, "Logged out.");
            }
            
            @Override
            public void onMessageTimeout(final SLMessage slMessage) {
                Debug.Log("Logout: LogoutRequest timed out!");
                SLAgentCircuit.this.gridConn.processDisconnect(false, "Logout request has timed out.");
            }
        });
        this.SendMessage(logoutRequest);
    }
    
    public void SendScriptDialogReply(final UUID objectID, final int chatChannel, final int buttonIndex, final String s) {
        final ScriptDialogReply scriptDialogReply = new ScriptDialogReply();
        scriptDialogReply.AgentData_Field.AgentID = this.circuitInfo.agentID;
        scriptDialogReply.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        scriptDialogReply.isReliable = true;
        scriptDialogReply.Data_Field.ObjectID = objectID;
        scriptDialogReply.Data_Field.ChatChannel = chatChannel;
        scriptDialogReply.Data_Field.ButtonIndex = buttonIndex;
        scriptDialogReply.Data_Field.ButtonLabel = SLMessage.stringToVariableUTF(s);
        this.SendMessage(scriptDialogReply);
    }
    
    void SendUseCode() {
        Debug.Printf("Using circuitCode: %d", this.circuitInfo.circuitCode);
        final UseCircuitCode useCircuitCode = new UseCircuitCode();
        useCircuitCode.CircuitCode_Field.Code = this.circuitInfo.circuitCode;
        useCircuitCode.CircuitCode_Field.SessionID = this.circuitInfo.sessionID;
        useCircuitCode.CircuitCode_Field.ID = this.circuitInfo.agentID;
        useCircuitCode.isReliable = true;
        useCircuitCode.setEventListener(new SLMessageEventListener() {
            @Override
            public void onMessageAcknowledged(final SLMessage slMessage) {
                Debug.Log("SLAgentCircuit: UseCircuitCode acknowledged.");
                if (!SLAgentCircuit.this.authReply.isTemporary) {
                    if (SLAgentCircuit.this.authReply.fromTeleport) {
                        Debug.Log("SLAgentCircuit: Ack from teleport, sending Teleport success.");
                        SLAgentCircuit.this.eventBus.publish(new SLTeleportResultEvent(true, null));
                    }
                    else {
                        SLAgentCircuit.this.gridConn.notifyLoginSuccess();
                    }
                    SLAgentCircuit.this.SendCompleteAgentMovement();
                    if (SLAgentCircuit.this.modules != null) {
                        SLAgentCircuit.this.modules.HandleCircuitReady();
                    }
                }
            }
            
            @Override
            public void onMessageTimeout(final SLMessage slMessage) {
                if (SLAgentCircuit.this.authReply.fromTeleport) {
                    SLAgentCircuit.this.eventBus.publish(new SLTeleportResultEvent(false, "Timed out while connecting to the simulator."));
                }
                else {
                    SLAgentCircuit.this.gridConn.notifyLoginError("Timed out while connecting to the simulator.");
                }
            }
        });
        this.SendMessage(useCircuitCode);
    }
    
    public void StartGroupSessionForVoice(final UUID uuid) {
        boolean b = false;
        synchronized (this.startedGroupSessions) {
            if (!this.startedGroupSessions.contains(uuid)) {
                this.SendGroupSessionStart(uuid);
                b = true;
            }
            monitorexit(this.startedGroupSessions);
            if (!b) {
                this.modules.voice.onGroupSessionReady(uuid);
            }
        }
    }
    
    public void TeleportToGlobalPosition(final LLVector3 llVector3) {
        final int n = (int)Math.floor(llVector3.x);
        final int n2 = (int)Math.floor(llVector3.y);
        final long n3 = (long)(n2 - n2 % 256) | (long)(n - n % 256) << 32;
        final LLVector3 position = new LLVector3(llVector3.x % 256.0f, llVector3.y % 256.0f, llVector3.z);
        final LLVector3 lookAt = new LLVector3(position);
        ++lookAt.x;
        Debug.Printf("regionHandle = %s, globalPos = %s", Long.toHexString(n3), llVector3);
        this.teleportRequestSent = true;
        final TeleportLocationRequest teleportLocationRequest = new TeleportLocationRequest();
        teleportLocationRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        teleportLocationRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        teleportLocationRequest.Info_Field.RegionHandle = n3;
        teleportLocationRequest.Info_Field.Position = position;
        teleportLocationRequest.Info_Field.LookAt = lookAt;
        teleportLocationRequest.isReliable = true;
        teleportLocationRequest.setEventListener(new SLMessageEventListener() {
            @Override
            public void onMessageAcknowledged(final SLMessage slMessage) {
            }
            
            @Override
            public void onMessageTimeout(final SLMessage slMessage) {
                SLAgentCircuit.this.eventBus.publish(new SLTeleportResultEvent(false, "Teleport request has timed out."));
            }
        });
        this.SendMessage(teleportLocationRequest);
    }
    
    public void TeleportToLandmarkAsset(final UUID landmarkID) {
        if (!this.getModules().rlvController.canTeleportToLandmark()) {
            return;
        }
        this.teleportRequestSent = true;
        final TeleportLandmarkRequest teleportLandmarkRequest = new TeleportLandmarkRequest();
        teleportLandmarkRequest.Info_Field.AgentID = this.circuitInfo.agentID;
        teleportLandmarkRequest.Info_Field.SessionID = this.circuitInfo.sessionID;
        teleportLandmarkRequest.Info_Field.LandmarkID = landmarkID;
        teleportLandmarkRequest.isReliable = true;
        teleportLandmarkRequest.setEventListener(new SLMessageEventListener() {
            @Override
            public void onMessageAcknowledged(final SLMessage slMessage) {
            }
            
            @Override
            public void onMessageTimeout(final SLMessage slMessage) {
                SLAgentCircuit.this.eventBus.publish(new SLTeleportResultEvent(false, "Teleport request has timed out."));
            }
        });
        this.SendMessage(teleportLandmarkRequest);
    }
    
    public boolean TeleportToLocalPosition(LLVector3 lookAt) {
        if (this.regionID != null) {
            Debug.Printf("Teleport: localPos = %s, regionHandle = %d", lookAt.toString(), this.regionHandle);
            this.teleportRequestSent = true;
            final TeleportLocationRequest teleportLocationRequest = new TeleportLocationRequest();
            teleportLocationRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
            teleportLocationRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
            teleportLocationRequest.Info_Field.RegionHandle = this.regionHandle;
            teleportLocationRequest.Info_Field.Position = lookAt;
            teleportLocationRequest.Info_Field.LookAt = new LLVector3(lookAt);
            lookAt = teleportLocationRequest.Info_Field.LookAt;
            lookAt.x += 10.0f;
            teleportLocationRequest.isReliable = true;
            teleportLocationRequest.setEventListener(new SLMessageEventListener() {
                @Override
                public void onMessageAcknowledged(final SLMessage slMessage) {
                }
                
                @Override
                public void onMessageTimeout(final SLMessage slMessage) {
                    SLAgentCircuit.this.eventBus.publish(new SLTeleportResultEvent(false, "Teleport request has timed out."));
                }
            });
            this.SendMessage(teleportLocationRequest);
            return true;
        }
        return false;
    }
    
    public void TeleportToLure(final UUID lureID) {
        this.teleportRequestSent = true;
        final TeleportLureRequest teleportLureRequest = new TeleportLureRequest();
        teleportLureRequest.Info_Field.AgentID = this.circuitInfo.agentID;
        teleportLureRequest.Info_Field.SessionID = this.circuitInfo.sessionID;
        teleportLureRequest.Info_Field.LureID = lureID;
        teleportLureRequest.isReliable = true;
        teleportLureRequest.setEventListener(new SLMessageEventListener() {
            @Override
            public void onMessageAcknowledged(final SLMessage slMessage) {
            }
            
            @Override
            public void onMessageTimeout(final SLMessage slMessage) {
                SLAgentCircuit.this.eventBus.publish(new SLTeleportResultEvent(false, "Teleport request has timed out."));
            }
        });
        this.SendMessage(teleportLureRequest);
    }
    
    public void TeleportToRegion(final long n, final int i, final int j, final int k) {
        if (!this.getModules().rlvController.canTeleportToLocation()) {
            return;
        }
        Debug.Log("TeleportToRegion: regionHandle = " + Long.toHexString(n) + ", pos = (" + i + ", " + j + ", " + k + ")");
        this.teleportRequestSent = true;
        final TeleportLocationRequest teleportLocationRequest = new TeleportLocationRequest();
        teleportLocationRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        teleportLocationRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        teleportLocationRequest.Info_Field.RegionHandle = n;
        teleportLocationRequest.Info_Field.Position = new LLVector3((float)i, (float)j, (float)k);
        teleportLocationRequest.Info_Field.LookAt = new LLVector3(0.0f, 1.0f, 0.0f);
        teleportLocationRequest.isReliable = true;
        teleportLocationRequest.setEventListener(new SLMessageEventListener() {
            @Override
            public void onMessageAcknowledged(final SLMessage slMessage) {
            }
            
            @Override
            public void onMessageTimeout(final SLMessage slMessage) {
                SLAgentCircuit.this.eventBus.publish(new SLTeleportResultEvent(false, "Teleport request has timed out."));
            }
        });
        this.SendMessage(teleportLocationRequest);
    }
    
    public void TouchObject(final int n) {
        final ObjectGrab objectGrab = new ObjectGrab();
        objectGrab.AgentData_Field.AgentID = this.circuitInfo.agentID;
        objectGrab.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        objectGrab.ObjectData_Field.LocalID = n;
        objectGrab.ObjectData_Field.GrabOffset = new LLVector3();
        objectGrab.isReliable = true;
        this.SendMessage(objectGrab);
        final ObjectDeGrab objectDeGrab = new ObjectDeGrab();
        objectDeGrab.AgentData_Field.AgentID = this.circuitInfo.agentID;
        objectDeGrab.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        objectDeGrab.ObjectData_Field.LocalID = n;
        objectDeGrab.isReliable = true;
        this.SendMessage(objectDeGrab);
    }
    
    public void TouchObjectFace(final SLObjectInfo slObjectInfo, final int n, final float f, final float f2, final float f3, final float f4, final float f5, final float n2, final float n3) {
        Debug.Printf("Touch: Object %d, face %d, pos (%f, %f, %f), uv (%f, %f)", slObjectInfo.localID, n, f, f2, f3, f4, f5);
        final ObjectGrab objectGrab = new ObjectGrab();
        objectGrab.AgentData_Field.AgentID = this.circuitInfo.agentID;
        objectGrab.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        objectGrab.ObjectData_Field.LocalID = slObjectInfo.localID;
        objectGrab.ObjectData_Field.GrabOffset = new LLVector3();
        final ObjectGrab.SurfaceInfo e = new ObjectGrab.SurfaceInfo();
        e.FaceIndex = n;
        e.Position = new LLVector3(f, f2, f3);
        e.UVCoord = new LLVector3(f4, f5, 0.0f);
        e.STCoord = new LLVector3(n2, n3, 0.0f);
        e.Normal = new LLVector3(1.0f, 0.0f, 0.0f);
        e.Binormal = new LLVector3(0.0f, 0.0f, 1.0f);
        objectGrab.SurfaceInfo_Fields.add(e);
        objectGrab.isReliable = true;
        this.SendMessage(objectGrab);
        final ObjectDeGrab objectDeGrab = new ObjectDeGrab();
        objectDeGrab.AgentData_Field.AgentID = this.circuitInfo.agentID;
        objectDeGrab.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        objectDeGrab.ObjectData_Field.LocalID = slObjectInfo.localID;
        objectDeGrab.isReliable = true;
        this.SendMessage(objectDeGrab);
    }
    
    public void TryWakeUp() {
        try {
            this.selector.wakeup();
        }
        catch (final Exception ex) {}
    }
    
    public void UnpauseAgent() {
        this.lastVisibleActivities = System.currentTimeMillis();
        if (this.agentPaused) {
            this.DoAgentResume();
        }
    }
    
    @Nullable
    public LLVector3d getAgentGlobalPosition() {
        if (this.modules != null) {
            final LLVector3 position = this.modules.avatarControl.getAgentPosition().getPosition();
            final int n = (int)(this.regionHandle >> 32 & 0xFFFFFFFFL);
            final int n2 = (int)(this.regionHandle & 0xFFFFFFFFL);
            final LLVector3d llVector3d = new LLVector3d();
            llVector3d.x = n + (double)position.x;
            llVector3d.y = n2 + (double)position.y;
            llVector3d.z = position.z;
            return llVector3d;
        }
        return null;
    }
    
    @Nullable
    @SuppressLint({ "DefaultLocale" })
    public String getAgentSLURL() {
        if (this.modules != null && Objects.equal(this.authReply.loginURL, "https://login.agni.lindenlab.com/cgi-bin/login.cgi") && this.regionName != null) {
            final LLVector3 position = this.modules.avatarControl.getAgentPosition().getPosition();
            try {
                return String.format("http://maps.secondlife.com/secondlife/%s/%d/%d/%d", URLEncoder.encode(this.regionName, "UTF-8"), (int)position.x, (int)position.y, (int)position.z);
            }
            catch (final UnsupportedEncodingException ex) {
                return null;
            }
        }
        return null;
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
    
    public SLObjectProfileData getObjectProfile(final int n) {
        final SLObjectInfo objectInfo = this.gridConn.parcelInfo.getObjectInfo(n);
        if (objectInfo != null) {
            final SLObjectProfileData create = SLObjectProfileData.create(objectInfo);
            if (!create.name().isPresent() && (objectInfo.isDead ^ true)) {
                this.RequestObjectName(objectInfo);
            }
            return create;
        }
        return null;
    }
    
    public String getRegionName() {
        return this.regionName;
    }
    
    public UUID getSessionID() {
        return this.circuitInfo.sessionID;
    }
    
    public Boolean isUserTyping(final UUID uuid) {
        return this.typingUsers.contains(uuid);
    }
    
    void processMyAttachmentUpdate(final SLObjectInfo slObjectInfo) {
        if (slObjectInfo != null && !slObjectInfo.nameKnown && (slObjectInfo.isDead ^ true)) {
            this.RequestObjectName(slObjectInfo);
        }
        this.getModules().avatarAppearance.UpdateMyAttachments();
    }
    
    public void sendTypingNotify(final UUID uuid, final boolean b) {
        int n;
        if (b) {
            n = 41;
        }
        else {
            n = 42;
        }
        this.SendInstantMessage(uuid, "", n);
    }
}
