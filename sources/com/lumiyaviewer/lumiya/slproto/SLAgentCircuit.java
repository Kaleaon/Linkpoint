// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GridConnectionService;
import com.lumiyaviewer.lumiya.dao.UserName;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.eventbus.EventRateLimiter;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
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
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
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
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl;
import com.lumiyaviewer.lumiya.slproto.modules.SLDrawDistance;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.finance.SLFinancialInfo;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.modules.groups.SLGroupManager;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteType;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.SLMuteList;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.voice.SLVoice;
import com.lumiyaviewer.lumiya.slproto.objects.PayInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import com.lumiyaviewer.lumiya.slproto.objects.UnsupportedObjectTypeException;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainData;
import com.lumiyaviewer.lumiya.slproto.types.AgentPosition;
import com.lumiyaviewer.lumiya.slproto.types.EDeRezDestination;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import com.lumiyaviewer.lumiya.slproto.types.Vector3Array;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUser;
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.FriendManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Selector;
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
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto:
//            SLThreadingCircuit, SLCircuitInfo, SLTempCircuit, SLMessage, 
//            SLGridConnection, SLParcelInfo, SLMessageEventListener

public class SLAgentCircuit extends SLThreadingCircuit
    implements com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.ICapsEventHandler
{

    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_caps_2D_SLCapEventQueue$CapsEventTypeSwitchesValues[];
    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues[];
    private Subscription agentNameSubscription;
    private boolean agentPaused;
    private final UUID agentUUID;
    private final AtomicReference agentUserName = new AtomicReference(null);
    private final SLCaps caps;
    private final ConcurrentLinkedQueue capsEventQueue = new ConcurrentLinkedQueue();
    private boolean doingObjectSelection;
    private final EventBus eventBus = EventBus.getInstance();
    private final Map forceNeedObjectNames = new ConcurrentHashMap();
    private boolean isEstateManager;
    private long lastObjectSelection;
    private int lastPauseId;
    private long lastVisibleActivities;
    private final ChatterID localChatterID;
    private final SLModules modules;
    private final Map objectNamesRequested = new ConcurrentHashMap();
    private final EventRateLimiter objectPropertiesRateLimiter;
    private List pendingGroupMessages;
    private long regionHandle;
    private UUID regionID;
    private String regionName;
    private final Set startedGroupSessions = new HashSet();
    private boolean teleportRequestSent;
    private final Set typingUsers = Collections.synchronizedSet(new HashSet());
    private final UserManager userManager;

    static EventBus _2D_get0(SLAgentCircuit slagentcircuit)
    {
        return slagentcircuit.eventBus;
    }

    static SLModules _2D_get1(SLAgentCircuit slagentcircuit)
    {
        return slagentcircuit.modules;
    }

    static UserManager _2D_get2(SLAgentCircuit slagentcircuit)
    {
        return slagentcircuit.userManager;
    }

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_caps_2D_SLCapEventQueue$CapsEventTypeSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_caps_2D_SLCapEventQueue$CapsEventTypeSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_caps_2D_SLCapEventQueue$CapsEventTypeSwitchesValues;
        }
        int ai[] = new int[com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType.values().length];
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType.AgentGroupDataUpdate.ordinal()] = 9;
        }
        catch (NoSuchFieldError nosuchfielderror9) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType.AvatarGroupsReply.ordinal()] = 10;
        }
        catch (NoSuchFieldError nosuchfielderror8) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType.BulkUpdateInventory.ordinal()] = 11;
        }
        catch (NoSuchFieldError nosuchfielderror7) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType.ChatterBoxInvitation.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror6) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType.ChatterBoxSessionStartReply.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror5) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType.EstablishAgentCommunication.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror4) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType.ParcelProperties.ordinal()] = 12;
        }
        catch (NoSuchFieldError nosuchfielderror3) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType.TeleportFailed.ordinal()] = 4;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType.TeleportFinish.ordinal()] = 5;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType.UnknownCapsEvent.ordinal()] = 13;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_caps_2D_SLCapEventQueue$CapsEventTypeSwitchesValues = ai;
        return ai;
    }

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues;
        }
        int ai[] = new int[com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.values().length];
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Group.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Local.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.User.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues = ai;
        return ai;
    }

    static void _2D_wrap0(SLAgentCircuit slagentcircuit)
    {
        slagentcircuit.SendCompleteAgentMovement();
    }

    static void _2D_wrap1(SLAgentCircuit slagentcircuit)
    {
        slagentcircuit.notifyObjectPropertiesChange();
    }

    public SLAgentCircuit(SLGridConnection slgridconnection, SLCircuitInfo slcircuitinfo, SLAuthReply slauthreply, SLCaps slcaps, SLTempCircuit sltempcircuit)
        throws IOException
    {
        super(slgridconnection, slcircuitinfo, slauthreply, sltempcircuit);
        pendingGroupMessages = new LinkedList();
        teleportRequestSent = false;
        regionID = null;
        regionName = null;
        regionHandle = 0L;
        isEstateManager = false;
        lastObjectSelection = 0L;
        doingObjectSelection = false;
        objectPropertiesRateLimiter = new EventRateLimiter(eventBus, 500L) {

            final SLAgentCircuit this$0;

            protected Object getEventToFire()
            {
                return null;
            }

            protected void onActualFire()
            {
                SLAgentCircuit._2D_wrap1(SLAgentCircuit.this);
            }

            
            {
                this$0 = SLAgentCircuit.this;
                super(eventbus, l);
            }
        };
        agentPaused = false;
        lastVisibleActivities = 0L;
        lastPauseId = 0;
        caps = slcaps;
        agentUUID = slcircuitinfo.agentID;
        localChatterID = ChatterID.getLocalChatterID(agentUUID);
        lastVisibleActivities = System.currentTimeMillis();
        userManager = UserManager.getUserManager(slcircuitinfo.agentID);
        if (slcaps != null && slauthreply.isTemporary ^ true)
        {
            modules = new SLModules(this, slcaps, slgridconnection);
        } else
        {
            modules = null;
        }
        if (!slauthreply.isTemporary && userManager != null)
        {
            userManager.setActiveAgentCircuit(this);
        }
        if (sltempcircuit != null)
        {
            for (slgridconnection = sltempcircuit.getPendingMessages().iterator(); slgridconnection.hasNext(); ((SLMessage)slgridconnection.next()).Handle(this)) { }
        }
    }

    private void DoAgentPause()
    {
        agentPaused = true;
        Debug.Log((new StringBuilder()).append("AgentPause: Sending agentPause with ID = ").append(lastPauseId).toString());
        AgentPause agentpause = new AgentPause();
        agentpause.AgentData_Field.AgentID = circuitInfo.agentID;
        agentpause.AgentData_Field.SessionID = circuitInfo.sessionID;
        agentpause.AgentData_Field.SerialNum = lastPauseId;
        agentpause.isReliable = true;
        SendMessage(agentpause);
        lastPauseId = lastPauseId + 1;
    }

    private void DoAgentResume()
    {
        agentPaused = false;
        Debug.Log((new StringBuilder()).append("AgentPause: Sending agentResume with ID = ").append(lastPauseId).toString());
        AgentResume agentresume = new AgentResume();
        agentresume.AgentData_Field.AgentID = circuitInfo.agentID;
        agentresume.AgentData_Field.SessionID = circuitInfo.sessionID;
        agentresume.AgentData_Field.SerialNum = lastPauseId;
        agentresume.isReliable = true;
        SendMessage(agentresume);
        lastPauseId = lastPauseId + 1;
    }

    private void HandleCapsEvent(com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEvent capsevent)
    {
        switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_caps_2D_SLCapEventQueue$CapsEventTypeSwitchesValues()[capsevent.eventType.ordinal()])
        {
        default:
            DefaultEventQueueHandler(capsevent.eventType, capsevent.eventBody);
            return;

        case 1: // '\001'
            HandleChatterBoxInvitation(capsevent.eventBody);
            return;

        case 2: // '\002'
            HandleChatterBoxSessionStartReply(capsevent.eventBody);
            return;

        case 4: // '\004'
            HandleTeleportFailed(capsevent.eventBody);
            return;

        case 5: // '\005'
            HandleTeleportFinish(capsevent.eventBody);
            return;

        case 3: // '\003'
            HandleEstablishAgentCommunication(capsevent.eventBody);
            return;
        }
    }

    private void HandleChatterBoxInvitation(LLSDNode llsdnode)
    {
        Object obj;
        UUID uuid;
        AvatarGroupList avatargrouplist;
        UUID uuid1;
        Object obj1;
        try
        {
            Debug.Log((new StringBuilder()).append("ChatterBoxInvitation: event = ").append(llsdnode.serializeToXML()).toString());
        }
        // Misplaced declaration of an exception variable
        catch (Object obj)
        {
            ((IOException) (obj)).printStackTrace();
        }
        uuid = UUID.fromString(llsdnode.byKey("session_id").asString());
        avatargrouplist = userManager.getChatterList().getGroupManager().getAvatarGroupList();
        if (avatargrouplist == null)
        {
            break MISSING_BLOCK_LABEL_289;
        }
        obj = (com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry)avatargrouplist.Groups.get(uuid);
_L6:
        obj1 = llsdnode.byKey("instantmessage").byKey("message_params");
        if (!((LLSDNode) (obj1)).keyExists("from_id")) goto _L2; else goto _L1
_L1:
        llsdnode = ((LLSDNode) (obj1)).byKey("from_id").asUUID();
_L5:
        uuid1 = ((LLSDNode) (obj1)).byKey("to_id").asUUID();
        obj1 = ((LLSDNode) (obj1)).byKey("message").asString();
        if (obj == null) goto _L4; else goto _L3
_L3:
        for (; obj != null && llsdnode != null; obj = null)
        {
            try
            {
                HandleChatEvent(ChatterID.getGroupChatterID(agentUUID, ((com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry) (obj)).GroupID), new SLChatTextEvent(new ChatMessageSourceUser(llsdnode), agentUUID, ((String) (obj1))), true);
                return;
            }
            // Misplaced declaration of an exception variable
            catch (LLSDNode llsdnode)
            {
                Debug.Log((new StringBuilder()).append("ChatterBoxInvitation: LLSDException ").append(llsdnode.getMessage()).toString());
            }
            llsdnode.printStackTrace();
            return;
        }

        break MISSING_BLOCK_LABEL_217;
_L4:
        if (avatargrouplist == null)
        {
            break MISSING_BLOCK_LABEL_294;
        }
        obj = (com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry)avatargrouplist.Groups.get(uuid1);
          goto _L3
        Debug.Log((new StringBuilder()).append("ChatterBoxInvitation: chat from unknown group (").append(uuid).append("), to_id = ").append(uuid1).toString());
        return;
_L2:
        llsdnode = null;
          goto _L5
        obj = null;
          goto _L6
    }

    private void HandleChatterBoxSessionStartReply(LLSDNode llsdnode)
    {
        UUID uuid;
        Exception exception;
        try
        {
            Debug.Log((new StringBuilder()).append("ChatterBoxSessionStartReply: event = ").append(llsdnode.serializeToXML()).toString());
        }
        catch (IOException ioexception)
        {
            ioexception.printStackTrace();
        }
        uuid = llsdnode.byKey("session_id").asUUID();
        modules.voice.onGroupSessionReady(uuid);
        llsdnode = startedGroupSessions;
        llsdnode;
        JVM INSTR monitorenter ;
        startedGroupSessions.add(uuid);
        Iterator iterator = pendingGroupMessages.iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            ImprovedInstantMessage improvedinstantmessage = (ImprovedInstantMessage)iterator.next();
            if (improvedinstantmessage.MessageBlock_Field.ID.equals(uuid))
            {
                iterator.remove();
                SendMessage(improvedinstantmessage);
            }
        } while (true);
        break MISSING_BLOCK_LABEL_171;
        exception;
        llsdnode;
        JVM INSTR monitorexit ;
        throw exception;
        llsdnode;
        Debug.Log((new StringBuilder()).append("ChatterBoxSessionStartReply: LLSDException ").append(llsdnode.getMessage()).toString());
        llsdnode.printStackTrace();
        return;
        llsdnode;
        JVM INSTR monitorexit ;
    }

    private void HandleChatterOnlineStatus(ChatterID chatterid, boolean flag)
    {
        if (userManager.isChatterActive(chatterid) && (chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser))
        {
            HandleChatEvent(chatterid, new SLChatOnlineOfflineEvent(new ChatMessageSourceUser(((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterid).getChatterUUID()), agentUUID, flag), false);
        }
    }

    private void HandleEstablishAgentCommunication(LLSDNode llsdnode)
    {
        if (!teleportRequestSent)
        {
            break MISSING_BLOCK_LABEL_107;
        }
        String s;
        String s1;
        String as[];
        try
        {
            Debug.Log((new StringBuilder()).append("EstablishAgentCommunication: event = ").append(llsdnode.serializeToXML()).toString());
        }
        catch (IOException ioexception)
        {
            ioexception.printStackTrace();
        }
        s1 = llsdnode.byKey("sim-ip-and-port").asString();
        s = llsdnode.byKey("seed-capability").asString();
        llsdnode = llsdnode.byKey("agent-id").asUUID();
        as = s1.split(":");
        llsdnode = new SLAuthReply(authReply, true, true, llsdnode, as[0], Integer.parseInt(as[1]), s);
        gridConn.addTempCircuit(llsdnode);
        return;
        llsdnode;
        llsdnode.printStackTrace();
        return;
    }

    private void HandleGroupNotice(ImprovedInstantMessage improvedinstantmessage, ChatMessageSource chatmessagesource)
    {
        Object obj2 = ByteBuffer.wrap(improvedinstantmessage.MessageBlock_Field.BinaryBucket);
        if (((ByteBuffer) (obj2)).limit() < 18)
        {
            return;
        }
        ((ByteBuffer) (obj2)).order(ByteOrder.BIG_ENDIAN);
        byte byte0 = ((ByteBuffer) (obj2)).get();
        byte byte1 = ((ByteBuffer) (obj2)).get();
        Object obj = new UUID(((ByteBuffer) (obj2)).getLong(), ((ByteBuffer) (obj2)).getLong());
        Object obj1 = "";
        if (byte0 != 0)
        {
            obj1 = new byte[((ByteBuffer) (obj2)).remaining()];
            ((ByteBuffer) (obj2)).get(((byte []) (obj1)));
            obj1 = SLMessage.stringFromVariableOEM(((byte []) (obj1)));
        }
        Debug.Log((new StringBuilder()).append("HandleGroupNotice: group UUID = ").append(((UUID) (obj)).toString()).toString());
        ChatterID chatterid = ChatterID.getGroupChatterID(agentUUID, ((UUID) (obj)));
        boolean flag = Objects.equal(chatmessagesource.getSourceUUID(), circuitInfo.agentID);
        obj2 = SLMessage.stringFromVariableUTF(improvedinstantmessage.MessageBlock_Field.Message);
        int i = ((String) (obj2)).indexOf('|');
        obj = obj2;
        if (i >= 0)
        {
            obj = ((String) (obj2)).substring(0, i);
            obj2 = ((String) (obj2)).substring(i + 1);
            obj = (new StringBuilder()).append(((String) (obj))).append("\n").append(((String) (obj2))).toString();
        }
        obj2 = obj;
        if (flag)
        {
            obj2 = obj;
            if (byte0 != 0)
            {
                obj2 = (new StringBuilder()).append(((String) (obj))).append("\n").append("(This notice contains attached item '").append(((String) (obj1))).append("')").toString();
            }
        }
        HandleChatEvent(chatterid, new SLChatTextEvent(chatmessagesource, agentUUID, improvedinstantmessage, ((String) (obj2))), true);
        if (byte0 != 0 && flag ^ true)
        {
            HandleChatEvent(chatterid, new SLChatInventoryItemOfferedByGroupNoticeEvent(chatmessagesource, agentUUID, improvedinstantmessage, ((String) (obj1)), SLAssetType.getByType(byte1)), false);
        }
    }

    private void HandleIM(ImprovedInstantMessage improvedinstantmessage, ChatMessageSource chatmessagesource)
    {
        SLModules slmodules;
        int i;
        slmodules = getModules();
        if (slmodules != null && slmodules.rlvController.onIncomingIM(improvedinstantmessage))
        {
            return;
        }
        i = improvedinstantmessage.MessageBlock_Field.Dialog;
        i;
        JVM INSTR tableswitch 0 42: default 220
    //                   0 754
    //                   1 342
    //                   2 342
    //                   3 727
    //                   4 404
    //                   5 220
    //                   6 220
    //                   7 220
    //                   8 220
    //                   9 431
    //                   10 220
    //                   11 220
    //                   12 220
    //                   13 220
    //                   14 220
    //                   15 220
    //                   16 220
    //                   17 397
    //                   18 220
    //                   19 873
    //                   20 754
    //                   21 220
    //                   22 477
    //                   23 220
    //                   24 220
    //                   25 220
    //                   26 560
    //                   27 220
    //                   28 220
    //                   29 220
    //                   30 220
    //                   31 873
    //                   32 390
    //                   33 220
    //                   34 220
    //                   35 220
    //                   36 220
    //                   37 390
    //                   38 615
    //                   39 642
    //                   40 642
    //                   41 376
    //                   42 383;
           goto _L1 _L2 _L3 _L3 _L4 _L5 _L1 _L1 _L1 _L1 _L6 _L1 _L1 _L1 _L1 _L1 _L1 _L1 _L7 _L1 _L8 _L2 _L1 _L9 _L1 _L1 _L1 _L10 _L1 _L1 _L1 _L1 _L8 _L11 _L1 _L1 _L1 _L1 _L11 _L12 _L13 _L13 _L14 _L15
_L1:
        Debug.Log((new StringBuilder()).append("HandleIM: unknown type = ").append(i).append(", sessionId = ").append(improvedinstantmessage.AgentData_Field.SessionID.toString()).append(", ").append("toAgentID = ").append(improvedinstantmessage.MessageBlock_Field.ToAgentID.toString()).append(", ").append("fromGroup = ").append(improvedinstantmessage.MessageBlock_Field.FromGroup).append(", ").append("message = '").append(SLMessage.stringFromVariableUTF(improvedinstantmessage.MessageBlock_Field.Message)).append("'").toString());
_L17:
        return;
_L3:
        HandleChatEvent(localChatterID, new SLChatSystemMessageEvent(ChatMessageSourceUnknown.getInstance(), agentUUID, SLMessage.stringFromVariableUTF(improvedinstantmessage.MessageBlock_Field.Message)), true);
        return;
_L14:
        HandleTypingNotification(chatmessagesource, true);
        return;
_L15:
        HandleTypingNotification(chatmessagesource, false);
        return;
_L11:
        HandleGroupNotice(improvedinstantmessage, chatmessagesource);
        return;
_L7:
        HandleSessionIM(improvedinstantmessage, chatmessagesource);
        return;
_L5:
        HandleChatEvent(chatmessagesource.getDefaultChatter(agentUUID), new SLChatInventoryItemOfferedEvent(chatmessagesource, agentUUID, improvedinstantmessage), true);
        return;
_L6:
        HandleChatEvent(localChatterID, new SLChatInventoryItemOfferedEvent(new ChatMessageSourceObject(improvedinstantmessage.AgentData_Field.AgentID, SLMessage.stringFromVariableOEM(improvedinstantmessage.MessageBlock_Field.FromAgentName)), agentUUID, improvedinstantmessage), true);
        return;
_L9:
        UUID uuid;
        if (chatmessagesource.getSourceType() != com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType.User)
        {
            break; /* Loop/switch isn't completed */
        }
        uuid = chatmessagesource.getSourceUUID();
        if (slmodules == null)
        {
            break; /* Loop/switch isn't completed */
        }
        if (slmodules.rlvController.autoAcceptTeleport(uuid))
        {
            TeleportToLure(improvedinstantmessage.MessageBlock_Field.ID);
            return;
        }
        if (!slmodules.rlvController.canTeleportToLure(uuid)) goto _L17; else goto _L16
_L16:
        HandleChatEvent(chatmessagesource.getDefaultChatter(agentUUID), new SLChatLureEvent(chatmessagesource, agentUUID, improvedinstantmessage), true);
        return;
_L10:
        if (chatmessagesource.getSourceType() == com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType.User && slmodules != null && !slmodules.rlvController.canTeleportToLure(chatmessagesource.getSourceUUID())) goto _L17; else goto _L18
_L18:
        HandleChatEvent(chatmessagesource.getDefaultChatter(agentUUID), new SLChatLureRequestEvent(chatmessagesource, agentUUID, improvedinstantmessage), true);
        return;
_L12:
        HandleChatEvent(chatmessagesource.getDefaultChatter(agentUUID), new SLChatFriendshipOfferedEvent(chatmessagesource, agentUUID, improvedinstantmessage), true);
        return;
_L13:
        HandleChatEvent(chatmessagesource.getDefaultChatter(agentUUID), new SLChatFriendshipResultEvent(chatmessagesource, agentUUID, improvedinstantmessage), true);
        if (i != 39 || chatmessagesource.getSourceType() != com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType.User) goto _L17; else goto _L19
_L19:
        improvedinstantmessage = chatmessagesource.getSourceUUID();
        if (improvedinstantmessage == null) goto _L17; else goto _L20
_L20:
        userManager.getChatterList().getFriendManager().addFriend(improvedinstantmessage);
        SendGenericMessage("requestonlinenotification", new String[] {
            improvedinstantmessage.toString()
        });
        return;
_L4:
        HandleChatEvent(chatmessagesource.getDefaultChatter(agentUUID), new SLChatGroupInvitationEvent(chatmessagesource, agentUUID, improvedinstantmessage), true);
        return;
_L2:
        boolean flag;
        SLChatTextEvent slchattextevent = new SLChatTextEvent(chatmessagesource, agentUUID, improvedinstantmessage, null);
        chatmessagesource = chatmessagesource.getDefaultChatter(agentUUID);
        flag = userManager.isChatterActive(chatmessagesource);
        HandleChatEvent(chatmessagesource, slchattextevent, true);
        if (userManager.isChatterMuted(chatmessagesource) || i == 20 || improvedinstantmessage.MessageBlock_Field.Offline != 0 || improvedinstantmessage.MessageBlock_Field.Message.length == 0 || flag || !(chatmessagesource instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)) goto _L17; else goto _L21
_L21:
        improvedinstantmessage = SLGridConnection.getAutoresponse();
        if (Strings.isNullOrEmpty(improvedinstantmessage)) goto _L17; else goto _L22
_L22:
        SendInstantMessage(((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatmessagesource).getChatterUUID(), improvedinstantmessage, 20);
        return;
_L8:
        HandleChatEvent(chatmessagesource.getDefaultChatter(agentUUID), new SLChatTextEvent(chatmessagesource, agentUUID, improvedinstantmessage, null), true);
        return;
    }

    private void HandleSessionIM(ImprovedInstantMessage improvedinstantmessage, ChatMessageSource chatmessagesource)
    {
        HandleChatEvent(ChatterID.getGroupChatterID(agentUUID, improvedinstantmessage.MessageBlock_Field.ID), new SLChatTextEvent(chatmessagesource, agentUUID, improvedinstantmessage, null), true);
    }

    private void HandleTeleportFailed(LLSDNode llsdnode)
    {
        try
        {
            Debug.Log((new StringBuilder()).append("TeleportFailed: event = ").append(llsdnode.serializeToXML()).toString());
        }
        // Misplaced declaration of an exception variable
        catch (LLSDNode llsdnode)
        {
            llsdnode.printStackTrace();
        }
        if (teleportRequestSent)
        {
            teleportRequestSent = false;
            eventBus.publish(new SLTeleportResultEvent(false, "Teleport has failed."));
        }
    }

    private void HandleTeleportFinish(LLSDNode llsdnode)
    {
        try
        {
            Debug.Log((new StringBuilder()).append("TeleportFinish: event = ").append(llsdnode.serializeToXML()).toString());
        }
        catch (IOException ioexception)
        {
            ioexception.printStackTrace();
        }
        if (teleportRequestSent)
        {
            teleportRequestSent = false;
            try
            {
                llsdnode = llsdnode.byKey("Info").byIndex(0);
                String s = llsdnode.byKey("SeedCapability").asString();
                byte abyte0[] = llsdnode.byKey("SimIP").asBinary();
                String s1 = String.format("%d.%d.%d.%d", new Object[] {
                    Integer.valueOf(abyte0[0] & 0xff), Integer.valueOf(abyte0[1] & 0xff), Integer.valueOf(abyte0[2] & 0xff), Integer.valueOf(abyte0[3] & 0xff)
                });
                int i = llsdnode.byKey("SimPort").asInt();
                llsdnode = new SLAuthReply(authReply, true, false, authReply.agentID, s1, i, s);
                Debug.Printf("new sim address: %s", new Object[] {
                    ((SLAuthReply) (llsdnode)).simAddress
                });
                modules.avatarControl.setEnableAgentUpdates(false);
                gridConn.HandleTeleportFinish(llsdnode);
                return;
            }
            // Misplaced declaration of an exception variable
            catch (LLSDNode llsdnode)
            {
                Debug.Log("TeleportFinish: LLSDException, teleport apparently failed");
            }
            llsdnode.printStackTrace();
            return;
        } else
        {
            Debug.Log("TeleportFinish: stale teleport finish?");
            return;
        }
    }

    private void HandleTypingNotification(ChatMessageSource chatmessagesource, boolean flag)
    {
        if (chatmessagesource instanceof ChatMessageSourceUser)
        {
            chatmessagesource = chatmessagesource.getSourceUUID();
            if (chatmessagesource != null)
            {
                if (flag)
                {
                    if (typingUsers.add(chatmessagesource))
                    {
                        userManager.getChatterList().updateUserTypingStatus(chatmessagesource);
                    }
                } else
                if (typingUsers.remove(chatmessagesource))
                {
                    userManager.getChatterList().updateUserTypingStatus(chatmessagesource);
                    return;
                }
            }
        }
    }

    private void ProcessObjectSelection()
    {
        if (!getNeedObjectNames() || !(doingObjectSelection ^ true)) goto _L2; else goto _L1
_L1:
        Object obj;
        Object obj2;
        obj2 = forceNeedObjectNames.values().iterator();
        obj = null;
_L8:
        Object obj1 = obj;
        if (!((Iterator) (obj2)).hasNext()) goto _L4; else goto _L3
_L3:
        Object obj3;
        obj3 = (SLObjectInfo)((Iterator) (obj2)).next();
        obj1 = obj;
        if (obj == null)
        {
            obj1 = new ObjectSelect();
            ((ObjectSelect) (obj1)).AgentData_Field.AgentID = circuitInfo.agentID;
            ((ObjectSelect) (obj1)).AgentData_Field.SessionID = circuitInfo.sessionID;
        }
        if (((ObjectSelect) (obj1)).ObjectData_Fields.size() <= 16) goto _L5; else goto _L4
_L4:
        obj2 = gridConn.parcelInfo.objectNamesQueue;
        obj2;
        JVM INSTR monitorenter ;
        obj3 = gridConn.parcelInfo.objectNamesQueue.values().iterator();
_L9:
        SLObjectInfo slobjectinfo;
        if (!((Iterator) (obj3)).hasNext())
        {
            break MISSING_BLOCK_LABEL_441;
        }
        slobjectinfo = (SLObjectInfo)((Iterator) (obj3)).next();
        obj = obj1;
        if (obj1 != null)
        {
            break MISSING_BLOCK_LABEL_208;
        }
        obj = new ObjectSelect();
        ((ObjectSelect) (obj)).AgentData_Field.AgentID = circuitInfo.agentID;
        ((ObjectSelect) (obj)).AgentData_Field.SessionID = circuitInfo.sessionID;
        int i = ((ObjectSelect) (obj)).ObjectData_Fields.size();
        if (i <= 16) goto _L7; else goto _L6
_L6:
        obj2;
        JVM INSTR monitorexit ;
        if (obj != null)
        {
            Debug.Log((new StringBuilder()).append("ObjectSelect: Sending ObjectSelect for ").append(((ObjectSelect) (obj)).ObjectData_Fields.size()).append(" objects, ").append(gridConn.parcelInfo.objectNamesQueue.size()).append(" remains.").toString());
            obj.isReliable = true;
            SendMessage(((SLMessage) (obj)));
            lastObjectSelection = System.currentTimeMillis();
            doingObjectSelection = true;
        }
_L2:
        return;
_L5:
        obj = new com.lumiyaviewer.lumiya.slproto.messages.ObjectSelect.ObjectData();
        obj.ObjectLocalID = ((SLObjectInfo) (obj3)).localID;
        ((ObjectSelect) (obj1)).ObjectData_Fields.add(obj);
        obj3.nameRequested = true;
        obj3.nameRequestedAt = System.currentTimeMillis();
        objectNamesRequested.put(((SLObjectInfo) (obj3)).getId(), obj3);
        obj = obj1;
          goto _L8
_L7:
        obj1 = new com.lumiyaviewer.lumiya.slproto.messages.ObjectSelect.ObjectData();
        obj1.ObjectLocalID = slobjectinfo.localID;
        ((ObjectSelect) (obj)).ObjectData_Fields.add(obj1);
        slobjectinfo.nameRequested = true;
        slobjectinfo.nameRequestedAt = System.currentTimeMillis();
        objectNamesRequested.put(slobjectinfo.getId(), slobjectinfo);
        obj1 = obj;
          goto _L9
        obj;
        throw obj;
        obj = obj1;
          goto _L6
    }

    private void ProcessObjectSelectionTimeout()
    {
        SLObjectInfo slobjectinfo;
        for (Iterator iterator = objectNamesRequested.values().iterator(); iterator.hasNext(); forceNeedObjectNames.remove(slobjectinfo.getId()))
        {
            slobjectinfo = (SLObjectInfo)iterator.next();
            SLObjectInfo slobjectinfo1 = (SLObjectInfo)gridConn.parcelInfo.objectNamesQueue.remove(slobjectinfo.getId());
            if (slobjectinfo1 != null)
            {
                gridConn.parcelInfo.objectNamesQueue.put(slobjectinfo1.getId(), slobjectinfo1);
            }
        }

        objectNamesRequested.clear();
    }

    private void SendAgentFOV()
    {
        AgentFOV agentfov = new AgentFOV();
        agentfov.AgentData_Field.AgentID = circuitInfo.agentID;
        agentfov.AgentData_Field.SessionID = circuitInfo.sessionID;
        agentfov.AgentData_Field.CircuitCode = circuitInfo.circuitCode;
        agentfov.FOVBlock_Field.GenCounter = 0;
        agentfov.FOVBlock_Field.VerticalAngle = 3.054326F;
        agentfov.isReliable = true;
        SendMessage(agentfov);
    }

    private void SendCompleteAgentMovement()
    {
        CompleteAgentMovement completeagentmovement = new CompleteAgentMovement();
        completeagentmovement.AgentData_Field.CircuitCode = circuitInfo.circuitCode;
        completeagentmovement.AgentData_Field.AgentID = circuitInfo.agentID;
        completeagentmovement.AgentData_Field.SessionID = circuitInfo.sessionID;
        completeagentmovement.isReliable = true;
        SendMessage(completeagentmovement);
    }

    private void SendEstateOwnerMessage(String s, String as[])
    {
        EstateOwnerMessage estateownermessage = new EstateOwnerMessage();
        estateownermessage.AgentData_Field.AgentID = circuitInfo.agentID;
        estateownermessage.AgentData_Field.SessionID = circuitInfo.sessionID;
        estateownermessage.AgentData_Field.TransactionID = new UUID(0L, 0L);
        estateownermessage.MethodData_Field.Method = SLMessage.stringToVariableOEM(s);
        estateownermessage.MethodData_Field.Invoice = new UUID(0L, 0L);
        int i = 0;
        for (int j = as.length; i < j; i++)
        {
            s = as[i];
            com.lumiyaviewer.lumiya.slproto.messages.EstateOwnerMessage.ParamList paramlist = new com.lumiyaviewer.lumiya.slproto.messages.EstateOwnerMessage.ParamList();
            paramlist.Parameter = SLMessage.stringToVariableOEM(s);
            estateownermessage.ParamList_Fields.add(paramlist);
        }

        estateownermessage.isReliable = true;
        SendMessage(estateownermessage);
    }

    private void SendGroupSessionStart(UUID uuid)
    {
        ImprovedInstantMessage improvedinstantmessage = new ImprovedInstantMessage();
        improvedinstantmessage.AgentData_Field.AgentID = circuitInfo.agentID;
        improvedinstantmessage.AgentData_Field.SessionID = circuitInfo.sessionID;
        improvedinstantmessage.MessageBlock_Field.FromGroup = false;
        improvedinstantmessage.MessageBlock_Field.ToAgentID = uuid;
        improvedinstantmessage.MessageBlock_Field.ParentEstateID = 0;
        improvedinstantmessage.MessageBlock_Field.RegionID = new UUID(0L, 0L);
        improvedinstantmessage.MessageBlock_Field.Position = modules.avatarControl.getAgentPosition().getPosition();
        improvedinstantmessage.MessageBlock_Field.Offline = 0;
        improvedinstantmessage.MessageBlock_Field.Dialog = 15;
        improvedinstantmessage.MessageBlock_Field.ID = uuid;
        improvedinstantmessage.MessageBlock_Field.Timestamp = 0;
        improvedinstantmessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo");
        improvedinstantmessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF("");
        improvedinstantmessage.MessageBlock_Field.BinaryBucket = new byte[1];
        improvedinstantmessage.isReliable = true;
        SendMessage(improvedinstantmessage);
    }

    private boolean SendInstantMessage(UUID uuid, String s, int i)
    {
label0:
        {
            if (!getModules().rlvController.canSendIM(uuid))
            {
                return false;
            }
            ImprovedInstantMessage improvedinstantmessage = new ImprovedInstantMessage();
            improvedinstantmessage.AgentData_Field.AgentID = circuitInfo.agentID;
            improvedinstantmessage.AgentData_Field.SessionID = circuitInfo.sessionID;
            improvedinstantmessage.MessageBlock_Field.FromGroup = false;
            improvedinstantmessage.MessageBlock_Field.ToAgentID = uuid;
            improvedinstantmessage.MessageBlock_Field.ParentEstateID = 0;
            improvedinstantmessage.MessageBlock_Field.RegionID = new UUID(0L, 0L);
            improvedinstantmessage.MessageBlock_Field.Position = new LLVector3();
            improvedinstantmessage.MessageBlock_Field.Offline = 0;
            improvedinstantmessage.MessageBlock_Field.Dialog = i;
            improvedinstantmessage.MessageBlock_Field.ID = new UUID(uuid.getMostSignificantBits() ^ circuitInfo.agentID.getMostSignificantBits(), uuid.getLeastSignificantBits() ^ circuitInfo.agentID.getLeastSignificantBits());
            improvedinstantmessage.MessageBlock_Field.Timestamp = 0;
            improvedinstantmessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo");
            improvedinstantmessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF(s);
            improvedinstantmessage.MessageBlock_Field.BinaryBucket = new byte[0];
            improvedinstantmessage.isReliable = true;
            SendMessage(improvedinstantmessage);
            if (i != 20 && i != 41 && i != 42)
            {
                if (i != 26)
                {
                    break label0;
                }
                HandleChatEvent(ChatterID.getUserChatterID(agentUUID, uuid), new SLChatLureRequestedEvent(s, agentUUID), false);
            }
            return true;
        }
        HandleChatEvent(ChatterID.getUserChatterID(agentUUID, uuid), new SLChatTextEvent(new ChatMessageSourceUser(circuitInfo.agentID), agentUUID, s), false);
        return true;
    }

    private void SendRetrieveInstantMessages()
    {
        RetrieveInstantMessages retrieveinstantmessages = new RetrieveInstantMessages();
        retrieveinstantmessages.AgentData_Field.AgentID = circuitInfo.agentID;
        retrieveinstantmessages.AgentData_Field.SessionID = circuitInfo.sessionID;
        retrieveinstantmessages.isReliable = true;
        SendMessage(retrieveinstantmessages);
    }

    private UUID getActiveGroupID()
    {
        if (modules != null)
        {
            return modules.groupManager.getActiveGroupID();
        } else
        {
            return null;
        }
    }

    private boolean getNeedObjectNames()
    {
        if (forceNeedObjectNames != null && !forceNeedObjectNames.isEmpty())
        {
            return true;
        }
        if (modules != null)
        {
            return modules.drawDistance.isObjectSelectEnabled();
        } else
        {
            return false;
        }
    }

    private boolean isEventMuted(ChatterID chatterid, SLChatEvent slchatevent)
    {
        if (modules != null)
        {
            SLMuteList slmutelist = modules.muteList;
            slchatevent = slchatevent.getSource();
            if (slchatevent.getSourceType() == com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType.User)
            {
                if (slmutelist.isMuted(slchatevent.getSourceUUID(), MuteType.AGENT))
                {
                    return true;
                }
            } else
            if (slchatevent.getSourceType() == com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType.Object)
            {
                UUID uuid = slchatevent.getSourceUUID();
                if (uuid != null && !uuid.equals(UUIDPool.ZeroUUID) && slmutelist.isMuted(uuid, MuteType.OBJECT))
                {
                    return true;
                }
                slchatevent = slchatevent.getSourceName(userManager);
                if (slchatevent != null && slmutelist.isMutedByName(slchatevent))
                {
                    return true;
                }
            }
            if (chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)
            {
                chatterid = ((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)chatterid).getChatterUUID();
                if (!chatterid.equals(UUIDPool.ZeroUUID) && slmutelist.isMuted(chatterid, MuteType.GROUP))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private void notifyObjectPropertiesChange()
    {
        if (userManager != null)
        {
            userManager.getObjectsManager().requestObjectListUpdate();
        }
    }

    private void processMyAvatarUpdate(SLObjectAvatarInfo slobjectavatarinfo)
    {
        if (modules != null)
        {
            modules.avatarControl.setAgentPosition(slobjectavatarinfo.getAbsolutePosition(), slobjectavatarinfo.getObjectCoords().get(2));
        }
    }

    public void AcceptFriendship(UUID uuid, UUID uuid1)
    {
        UUID uuid2 = null;
        userManager.getChatterList().getFriendManager().addFriend(uuid);
        AcceptFriendship acceptfriendship = new AcceptFriendship();
        acceptfriendship.AgentData_Field.AgentID = circuitInfo.agentID;
        acceptfriendship.AgentData_Field.SessionID = circuitInfo.sessionID;
        uuid = uuid2;
        if (modules != null)
        {
            uuid = modules.inventory.getCallingCardsFolderUUID();
        }
        com.lumiyaviewer.lumiya.slproto.messages.AcceptFriendship.FolderData folderdata = new com.lumiyaviewer.lumiya.slproto.messages.AcceptFriendship.FolderData();
        uuid2 = uuid;
        if (uuid == null)
        {
            uuid2 = UUIDPool.ZeroUUID;
        }
        folderdata.FolderID = uuid2;
        acceptfriendship.FolderData_Fields.add(folderdata);
        acceptfriendship.TransactionBlock_Field.TransactionID = uuid1;
        acceptfriendship.isReliable = true;
        SendMessage(acceptfriendship);
    }

    public void AcceptInventoryOffer(int i, boolean flag, UUID uuid, UUID uuid1, UUID uuid2)
    {
        ImprovedInstantMessage improvedinstantmessage = new ImprovedInstantMessage();
        improvedinstantmessage.AgentData_Field.AgentID = circuitInfo.agentID;
        improvedinstantmessage.AgentData_Field.SessionID = circuitInfo.sessionID;
        improvedinstantmessage.MessageBlock_Field.FromGroup = false;
        improvedinstantmessage.MessageBlock_Field.ToAgentID = uuid;
        improvedinstantmessage.MessageBlock_Field.ParentEstateID = 0;
        improvedinstantmessage.MessageBlock_Field.RegionID = new UUID(0L, 0L);
        improvedinstantmessage.MessageBlock_Field.Position = new LLVector3();
        improvedinstantmessage.MessageBlock_Field.Offline = 0;
        if (flag)
        {
            improvedinstantmessage.MessageBlock_Field.Dialog = i + 1;
        } else
        {
            improvedinstantmessage.MessageBlock_Field.Dialog = i + 2;
        }
        improvedinstantmessage.MessageBlock_Field.ID = uuid1;
        improvedinstantmessage.MessageBlock_Field.Timestamp = 0;
        improvedinstantmessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo");
        improvedinstantmessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF("");
        if (uuid2 != null)
        {
            uuid = ByteBuffer.wrap(new byte[16]);
            uuid.order(ByteOrder.BIG_ENDIAN);
            uuid.putLong(uuid2.getMostSignificantBits());
            uuid.putLong(uuid2.getLeastSignificantBits());
            uuid.position(0);
            improvedinstantmessage.MessageBlock_Field.BinaryBucket = uuid.array();
        } else
        {
            improvedinstantmessage.MessageBlock_Field.BinaryBucket = new byte[0];
        }
        improvedinstantmessage.isReliable = true;
        SendMessage(improvedinstantmessage);
    }

    public void AddFriend(UUID uuid, String s)
    {
        ImprovedInstantMessage improvedinstantmessage = new ImprovedInstantMessage();
        improvedinstantmessage.AgentData_Field.AgentID = circuitInfo.agentID;
        improvedinstantmessage.AgentData_Field.SessionID = circuitInfo.sessionID;
        improvedinstantmessage.MessageBlock_Field.FromGroup = false;
        improvedinstantmessage.MessageBlock_Field.ToAgentID = uuid;
        improvedinstantmessage.MessageBlock_Field.ParentEstateID = 0;
        improvedinstantmessage.MessageBlock_Field.RegionID = new UUID(0L, 0L);
        improvedinstantmessage.MessageBlock_Field.Position = new LLVector3();
        improvedinstantmessage.MessageBlock_Field.Offline = 0;
        improvedinstantmessage.MessageBlock_Field.Dialog = 38;
        improvedinstantmessage.MessageBlock_Field.ID = new UUID(uuid.getMostSignificantBits() ^ circuitInfo.agentID.getMostSignificantBits(), uuid.getLeastSignificantBits() ^ circuitInfo.agentID.getLeastSignificantBits());
        improvedinstantmessage.MessageBlock_Field.Timestamp = 0;
        improvedinstantmessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo");
        improvedinstantmessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF(s);
        improvedinstantmessage.MessageBlock_Field.BinaryBucket = new byte[0];
        improvedinstantmessage.isReliable = true;
        SendMessage(improvedinstantmessage);
    }

    public void BuyObject(int i, byte byte0, int j)
    {
        Object obj = getActiveGroupID();
        ObjectBuy objectbuy = new ObjectBuy();
        objectbuy.AgentData_Field.AgentID = circuitInfo.agentID;
        objectbuy.AgentData_Field.SessionID = circuitInfo.sessionID;
        com.lumiyaviewer.lumiya.slproto.messages.ObjectBuy.AgentData agentdata = objectbuy.AgentData_Field;
        if (obj == null)
        {
            obj = UUIDPool.ZeroUUID;
        }
        agentdata.GroupID = ((UUID) (obj));
        objectbuy.AgentData_Field.CategoryID = getModules().inventory.rootFolder.uuid;
        obj = new com.lumiyaviewer.lumiya.slproto.messages.ObjectBuy.ObjectData();
        obj.ObjectLocalID = i;
        obj.SaleType = byte0;
        obj.SalePrice = j;
        objectbuy.ObjectData_Fields.add(obj);
        objectbuy.isReliable = true;
        SendMessage(objectbuy);
    }

    public void CloseCircuit()
    {
        Debug.Printf("AgentCircuit: closing circuit.", new Object[0]);
        if (modules != null)
        {
            modules.HandleCloseCircuit();
        }
        if (userManager != null)
        {
            userManager.clearActiveAgentCircuit(this);
        }
        if (agentNameSubscription != null)
        {
            agentNameSubscription.unsubscribe();
            agentNameSubscription = null;
        }
        super.CloseCircuit();
    }

    public void DerezObject(int i, EDeRezDestination ederezdestination)
    {
        UUID uuid = getActiveGroupID();
        DeRezObject derezobject = new DeRezObject();
        derezobject.AgentData_Field.AgentID = circuitInfo.agentID;
        derezobject.AgentData_Field.SessionID = circuitInfo.sessionID;
        com.lumiyaviewer.lumiya.slproto.messages.DeRezObject.AgentBlock agentblock = derezobject.AgentBlock_Field;
        if (uuid == null)
        {
            uuid = new UUID(0L, 0L);
        }
        agentblock.GroupID = uuid;
        derezobject.AgentBlock_Field.Destination = ederezdestination.getCode();
        derezobject.AgentBlock_Field.DestinationID = new UUID(0L, 0L);
        derezobject.AgentBlock_Field.PacketCount = 1;
        derezobject.AgentBlock_Field.PacketNumber = 0;
        derezobject.AgentBlock_Field.TransactionID = UUID.randomUUID();
        ederezdestination = new com.lumiyaviewer.lumiya.slproto.messages.DeRezObject.ObjectData();
        ederezdestination.ObjectLocalID = i;
        derezobject.ObjectData_Fields.add(ederezdestination);
        derezobject.isReliable = true;
        SendMessage(derezobject);
    }

    public void DoRequestPayPrice(UUID uuid)
    {
label0:
        {
            SLObjectInfo slobjectinfo = (SLObjectInfo)gridConn.parcelInfo.allObjectsNearby.get(uuid);
            if (slobjectinfo != null)
            {
                if (slobjectinfo.getPayInfo() == null)
                {
                    break label0;
                }
                eventBus.publish(new SLObjectPayInfoEvent(slobjectinfo));
            }
            return;
        }
        RequestPayPrice requestpayprice = new RequestPayPrice();
        requestpayprice.ObjectData_Field.ObjectID = uuid;
        requestpayprice.isReliable = true;
        SendMessage(requestpayprice);
    }

    public void GenerateChatMoneyEvent(UUID uuid, int i, int j)
    {
        Object obj;
        Object obj1;
        if (uuid != null)
        {
            obj = new ChatMessageSourceUser(uuid);
        } else
        {
            obj = ChatMessageSourceUnknown.getInstance();
        }
        if (uuid != null)
        {
            obj1 = ChatterID.getUserChatterID(agentUUID, uuid);
        } else
        {
            obj1 = localChatterID;
        }
        HandleChatEvent(((ChatterID) (obj1)), new SLChatBalanceChangedEvent(((ChatMessageSource) (obj)), agentUUID, true, i, j), true);
        if (modules != null)
        {
            modules.financialInfo.RecordChatEvent(uuid, i, j);
        }
    }

    public void HandleAgentMovementComplete(AgentMovementComplete agentmovementcomplete)
    {
        regionHandle = agentmovementcomplete.Data_Field.RegionHandle;
        modules.avatarControl.setAgentPosition(agentmovementcomplete.Data_Field.Position, null);
        Debug.Printf("Got agentPosition: %s", new Object[] {
            modules.avatarControl.getAgentPosition().getImmutablePosition()
        });
        SendAgentFOV();
        modules.avatarAppearance.SendAgentWearablesRequest();
        SendRetrieveInstantMessages();
        modules.avatarControl.setEnableAgentUpdates(true);
    }

    public void HandleAlertMessage(AlertMessage alertmessage)
    {
        alertmessage = SLMessage.stringFromVariableOEM(alertmessage.AlertData_Field.Message);
        alertmessage = new SLChatSystemMessageEvent(ChatMessageSourceUnknown.getInstance(), agentUUID, alertmessage);
        HandleChatEvent(localChatterID, alertmessage, true);
    }

    public void HandleAvatarAnimation(AvatarAnimation avataranimation)
    {
        SLParcelInfo slparcelinfo = gridConn.parcelInfo;
        if (slparcelinfo != null && modules != null)
        {
            slparcelinfo.ApplyAvatarAnimation(avataranimation, modules.avatarControl);
        }
    }

    public void HandleAvatarAppearance(AvatarAppearance avatarappearance)
    {
        Debug.Log((new StringBuilder()).append("Got AvatarAppearance, ID = ").append(avatarappearance.Sender_Field.ID.toString()).append(" isTrial = ").append(avatarappearance.Sender_Field.IsTrial).append(", our ID = ").append(circuitInfo.agentID.toString()).toString());
        if (avatarappearance.Sender_Field.ID.equals(circuitInfo.agentID) && modules != null)
        {
            modules.avatarAppearance.HandleAvatarAppearance(avatarappearance);
        }
        SLParcelInfo slparcelinfo = gridConn.parcelInfo;
        if (slparcelinfo != null)
        {
            slparcelinfo.ApplyAvatarAppearance(avatarappearance);
        }
    }

    public void HandleAvatarInterestsReply(AvatarInterestsReply avatarinterestsreply)
    {
        Debug.Log((new StringBuilder()).append("got AvatarInterestsReply: wantToText = ").append(SLMessage.stringFromVariableOEM(avatarinterestsreply.PropertiesData_Field.WantToText)).toString());
        Debug.Log((new StringBuilder()).append("got AvatarInterestsReply: skillText = ").append(SLMessage.stringFromVariableOEM(avatarinterestsreply.PropertiesData_Field.SkillsText)).toString());
    }

    public void HandleChatEvent(ChatterID chatterid, SLChatEvent slchatevent, boolean flag)
    {
        if (isEventMuted(chatterid, slchatevent))
        {
            return;
        } else
        {
            userManager.getChatterList().getActiveChattersManager().HandleChatEvent(chatterid, slchatevent, flag);
            return;
        }
    }

    public void HandleChatFromSimulator(ChatFromSimulator chatfromsimulator)
    {
        SLModules slmodules = getModules();
        if (slmodules != null && slmodules.rlvController.onIncomingChat(chatfromsimulator))
        {
            return;
        }
        UUID uuid = chatfromsimulator.ChatData_Field.SourceID;
        String s = SLMessage.stringFromVariableOEM(chatfromsimulator.ChatData_Field.FromName);
        String s1 = SLMessage.stringFromVariableUTF(chatfromsimulator.ChatData_Field.Message);
        if (chatfromsimulator.ChatData_Field.ChatType == 8 && chatfromsimulator.ChatData_Field.SourceType == 2 && s.startsWith("#Firestorm LSL Bridge") && s1.startsWith("<bridgeURL>"))
        {
            return;
        }
        if (chatfromsimulator.ChatData_Field.SourceType == 1 && slmodules != null && !slmodules.rlvController.canRecvChat(s1, uuid))
        {
            return;
        }
        if (chatfromsimulator.ChatData_Field.Audible != 1)
        {
            return;
        }
        for (int i = chatfromsimulator.ChatData_Field.ChatType; i == 6 || i == 4 || i == 5;)
        {
            return;
        }

        switch (chatfromsimulator.ChatData_Field.SourceType)
        {
        default:
            HandleChatEvent(localChatterID, new SLChatTextEvent(ChatMessageSourceUnknown.getInstance(), agentUUID, s1), true);
            return;

        case 1: // '\001'
            HandleChatEvent(localChatterID, new SLChatTextEvent(new ChatMessageSourceUser(uuid), agentUUID, s1), true);
            return;

        case 2: // '\002'
            HandleChatEvent(localChatterID, new SLChatTextEvent(new ChatMessageSourceObject(uuid, s), agentUUID, s1), true);
            break;
        }
    }

    public void HandleImprovedInstantMessage(ImprovedInstantMessage improvedinstantmessage)
    {
        int i = improvedinstantmessage.MessageBlock_Field.Dialog;
        if (i != 19 && i != 31) goto _L2; else goto _L1
_L1:
        Object obj = new ChatMessageSourceObject(improvedinstantmessage.AgentData_Field.AgentID, SLMessage.stringFromVariableOEM(improvedinstantmessage.MessageBlock_Field.FromAgentName));
_L4:
        HandleIM(improvedinstantmessage, ((ChatMessageSource) (obj)));
        return;
_L2:
        if (i == 3)
        {
            obj = ChatMessageSourceUnknown.getInstance();
            continue; /* Loop/switch isn't completed */
        }
        if (!UUIDPool.ZeroUUID.equals(improvedinstantmessage.AgentData_Field.AgentID))
        {
            break; /* Loop/switch isn't completed */
        }
        obj = ChatMessageSourceUnknown.getInstance();
        if (true) goto _L4; else goto _L3
_L3:
        ChatMessageSourceUser chatmessagesourceuser = new ChatMessageSourceUser(improvedinstantmessage.AgentData_Field.AgentID);
        obj = chatmessagesourceuser;
        if (!getModules().rlvController.canRecvIM(chatmessagesourceuser.getSourceUUID()))
        {
            return;
        }
        if (true) goto _L4; else goto _L5
_L5:
    }

    public void HandleImprovedTerseObjectUpdate(ImprovedTerseObjectUpdate improvedterseobjectupdate)
    {
        SLParcelInfo slparcelinfo = gridConn.parcelInfo;
        Iterator iterator = improvedterseobjectupdate.ObjectData_Fields.iterator();
        improvedterseobjectupdate = null;
        while (iterator.hasNext()) 
        {
            com.lumiyaviewer.lumiya.slproto.messages.ImprovedTerseObjectUpdate.ObjectData objectdata = (com.lumiyaviewer.lumiya.slproto.messages.ImprovedTerseObjectUpdate.ObjectData)iterator.next();
            int i = SLObjectInfo.getLocalID(objectdata);
            Object obj = (UUID)slparcelinfo.uuidsNearby.get(Integer.valueOf(i));
            if (obj != null)
            {
                SLObjectInfo slobjectinfo = (SLObjectInfo)slparcelinfo.allObjectsNearby.get(obj);
                obj = slobjectinfo;
                if (slobjectinfo != null)
                {
                    slobjectinfo.ApplyTerseObjectUpdate(objectdata);
                    boolean flag;
                    if (slobjectinfo instanceof SLObjectAvatarInfo)
                    {
                        flag = ((SLObjectAvatarInfo)slobjectinfo).isMyAvatar();
                    } else
                    {
                        flag = false;
                    }
                    if (flag)
                    {
                        processMyAvatarUpdate((SLObjectAvatarInfo)slobjectinfo);
                        obj = slobjectinfo;
                    } else
                    {
                        obj = slobjectinfo;
                        if (slobjectinfo.isMyAttachment())
                        {
                            processMyAttachmentUpdate(slobjectinfo);
                            obj = slobjectinfo;
                        }
                    }
                }
            } else
            {
                obj = null;
            }
            if (obj == null)
            {
                obj = improvedterseobjectupdate;
                if (improvedterseobjectupdate == null)
                {
                    obj = new RequestMultipleObjects();
                    ((RequestMultipleObjects) (obj)).AgentData_Field.AgentID = circuitInfo.agentID;
                    ((RequestMultipleObjects) (obj)).AgentData_Field.SessionID = circuitInfo.sessionID;
                }
                improvedterseobjectupdate = new com.lumiyaviewer.lumiya.slproto.messages.RequestMultipleObjects.ObjectData();
                improvedterseobjectupdate.CacheMissType = 0;
                improvedterseobjectupdate.ID = i;
                ((RequestMultipleObjects) (obj)).ObjectData_Fields.add(improvedterseobjectupdate);
                improvedterseobjectupdate = ((ImprovedTerseObjectUpdate) (obj));
            }
        }
        if (improvedterseobjectupdate != null)
        {
            Debug.Log((new StringBuilder()).append("Handing cache miss for terse update: ").append(((RequestMultipleObjects) (improvedterseobjectupdate)).ObjectData_Fields.size()).append(" objects.").toString());
            improvedterseobjectupdate.isReliable = true;
            SendMessage(improvedterseobjectupdate);
        }
    }

    public void HandleKillObject(KillObject killobject)
    {
        SLParcelInfo slparcelinfo = gridConn.parcelInfo;
        killobject = killobject.ObjectData_Fields.iterator();
        boolean flag = false;
        do
        {
            if (!killobject.hasNext())
            {
                break;
            }
            if (slparcelinfo.killObject(this, ((com.lumiyaviewer.lumiya.slproto.messages.KillObject.ObjectData)killobject.next()).ID))
            {
                flag = true;
            }
        } while (true);
        if (flag)
        {
            objectPropertiesRateLimiter.fire();
        }
    }

    public void HandleLayerData(LayerData layerdata)
    {
        if (layerdata.LayerID_Field.Type == 76)
        {
            SLParcelInfo slparcelinfo = gridConn.parcelInfo;
            if (slparcelinfo != null)
            {
                slparcelinfo.terrainData.ProcessLayerData(layerdata.LayerDataData_Field.Data);
            }
        }
    }

    public void HandleLoadURL(LoadURL loadurl)
    {
        ChatMessageSourceObject chatmessagesourceobject = new ChatMessageSourceObject(loadurl.Data_Field.ObjectID, SLMessage.stringFromVariableOEM(loadurl.Data_Field.ObjectName));
        HandleChatEvent(localChatterID, new SLChatTextEvent(chatmessagesourceobject, agentUUID, loadurl), true);
    }

    public void HandleObjectProperties(ObjectProperties objectproperties)
    {
        Debug.Log((new StringBuilder()).append("ObjectProperties: ").append(objectproperties.ObjectData_Fields.size()).append(" ObjectSelect replies. Reqd ").append(objectNamesRequested.size()).append(" obj, remains ").append(gridConn.parcelInfo.objectNamesQueue.size()).append(" objects.").toString());
        com.lumiyaviewer.lumiya.slproto.messages.ObjectProperties.ObjectData objectdata;
        for (objectproperties = objectproperties.ObjectData_Fields.iterator(); objectproperties.hasNext(); objectNamesRequested.remove(objectdata.ObjectID))
        {
            objectdata = (com.lumiyaviewer.lumiya.slproto.messages.ObjectProperties.ObjectData)objectproperties.next();
            Object obj = (SLObjectInfo)gridConn.parcelInfo.objectNamesQueue.remove(objectdata.ObjectID);
            if (obj != null)
            {
                ((SLObjectInfo) (obj)).ApplyObjectProperties(objectdata);
                userManager.getObjectsManager().requestObjectProfileUpdate(((SLObjectInfo) (obj)).localID);
            }
            obj = (SLObjectInfo)forceNeedObjectNames.remove(objectdata.ObjectID);
            if (obj == null)
            {
                continue;
            }
            ((SLObjectInfo) (obj)).ApplyObjectProperties(objectdata);
            userManager.getObjectsManager().requestObjectProfileUpdate(((SLObjectInfo) (obj)).localID);
            obj = ((SLObjectInfo) (obj)).getParentObject();
            if (obj == null)
            {
                continue;
            }
            obj = ((SLObjectInfo) (obj)).getId();
            if (obj != null)
            {
                userManager.getObjectsManager().requestTouchableChildrenUpdate(((UUID) (obj)));
            }
        }

        if (objectNamesRequested.isEmpty())
        {
            doingObjectSelection = false;
            ProcessObjectSelection();
        }
        objectPropertiesRateLimiter.fire();
    }

    public void HandleObjectUpdate(ObjectUpdate objectupdate)
    {
        SLParcelInfo slparcelinfo;
        Iterator iterator;
        boolean flag;
        boolean flag1;
        slparcelinfo = gridConn.parcelInfo;
        iterator = objectupdate.ObjectData_Fields.iterator();
        flag1 = false;
        flag = false;
_L8:
        Object obj;
        if (!iterator.hasNext())
        {
            break MISSING_BLOCK_LABEL_344;
        }
        obj = (com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate.ObjectData)iterator.next();
        if (((com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate.ObjectData) (obj)).PCode != 47 && ((com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate.ObjectData) (obj)).PCode != 9) goto _L2; else goto _L1
_L1:
        objectupdate = (SLObjectInfo)slparcelinfo.allObjectsNearby.get(((com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate.ObjectData) (obj)).FullID);
        if (objectupdate == null) goto _L4; else goto _L3
_L3:
        int i;
        i = ((SLObjectInfo) (objectupdate)).parentID;
        objectupdate.ApplyObjectUpdate(((com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate.ObjectData) (obj)));
        slparcelinfo.updateObjectParent(i, objectupdate);
        flag = flag1;
        if (((SLObjectInfo) (objectupdate)).parentID != i)
        {
            flag = flag1;
            if (objectupdate instanceof SLObjectAvatarInfo)
            {
                flag = flag1;
                if (((SLObjectAvatarInfo)objectupdate).isMyAvatar())
                {
                    flag = true;
                }
            }
        }
        i = 1;
_L6:
        boolean flag2;
        boolean flag3;
        if (objectupdate instanceof SLObjectAvatarInfo)
        {
            flag3 = ((SLObjectAvatarInfo)objectupdate).isMyAvatar();
        } else
        {
            flag3 = false;
        }
        if (flag3)
        {
            processMyAvatarUpdate((SLObjectAvatarInfo)objectupdate);
            flag1 = i;
        } else
        if (objectupdate.isMyAttachment())
        {
            processMyAttachmentUpdate(objectupdate);
            flag1 = i;
        } else
        {
            flag1 = i;
        }
_L5:
        i = ((flag1) ? 1 : 0);
        flag1 = flag;
        flag = i;
        continue; /* Loop/switch isn't completed */
_L2:
        i = ((flag) ? 1 : 0);
        flag = flag1;
        flag1 = i;
        if (true) goto _L5; else goto _L4
_L4:
        obj = SLObjectInfo.create(agentUUID, ((com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate.ObjectData) (obj)), circuitInfo.agentID);
        flag2 = flag;
        if (slparcelinfo.addObject(((SLObjectInfo) (obj))))
        {
            flag2 = true;
        }
        objectupdate = ((ObjectUpdate) (obj));
        flag = flag1;
        i = ((flag2) ? 1 : 0);
        if (obj instanceof SLObjectAvatarInfo)
        {
            objectupdate = ((ObjectUpdate) (obj));
            flag = flag1;
            i = ((flag2) ? 1 : 0);
            if (((SLObjectAvatarInfo)obj).isMyAvatar())
            {
                Debug.Log("ObjectUpdate: got my avatar (normal)");
                slparcelinfo.setAgentAvatar((SLObjectAvatarInfo)obj);
                modules.avatarAppearance.OnMyAvatarCreated((SLObjectAvatarInfo)obj);
                flag = true;
                objectupdate = ((ObjectUpdate) (obj));
                i = ((flag2) ? 1 : 0);
            }
        }
          goto _L6
        if (flag1)
        {
            userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value);
        }
        if (flag)
        {
            ProcessObjectSelection();
            objectPropertiesRateLimiter.fire();
        }
        return;
        if (true) goto _L8; else goto _L7
_L7:
    }

    public void HandleObjectUpdateCached(ObjectUpdateCached objectupdatecached)
    {
        RequestMultipleObjects requestmultipleobjects = new RequestMultipleObjects();
        requestmultipleobjects.AgentData_Field.AgentID = circuitInfo.agentID;
        requestmultipleobjects.AgentData_Field.SessionID = circuitInfo.sessionID;
        com.lumiyaviewer.lumiya.slproto.messages.RequestMultipleObjects.ObjectData objectdata1;
        for (objectupdatecached = objectupdatecached.ObjectData_Fields.iterator(); objectupdatecached.hasNext(); requestmultipleobjects.ObjectData_Fields.add(objectdata1))
        {
            com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCached.ObjectData objectdata = (com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCached.ObjectData)objectupdatecached.next();
            objectdata1 = new com.lumiyaviewer.lumiya.slproto.messages.RequestMultipleObjects.ObjectData();
            objectdata1.CacheMissType = 0;
            objectdata1.ID = objectdata.ID;
        }

        requestmultipleobjects.isReliable = true;
        SendMessage(requestmultipleobjects);
    }

    public void HandleObjectUpdateCompressed(ObjectUpdateCompressed objectupdatecompressed)
    {
        SLParcelInfo slparcelinfo;
        Iterator iterator;
        boolean flag;
        boolean flag1;
        slparcelinfo = gridConn.parcelInfo;
        iterator = objectupdatecompressed.ObjectData_Fields.iterator();
        flag = false;
        flag1 = false;
_L10:
        if (!iterator.hasNext()) goto _L2; else goto _L1
_L1:
        com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCompressed.ObjectData objectdata;
        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag5;
        objectdata = (com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCompressed.ObjectData)iterator.next();
        flag4 = flag;
        flag2 = flag1;
        flag5 = flag;
        flag3 = flag1;
        int i = SLObjectInfo.getLocalID(objectdata);
        flag4 = flag;
        flag2 = flag1;
        flag5 = flag;
        flag3 = flag1;
        objectupdatecompressed = (UUID)slparcelinfo.uuidsNearby.get(Integer.valueOf(i));
        if (objectupdatecompressed == null) goto _L4; else goto _L3
_L3:
        flag4 = flag;
        flag2 = flag1;
        flag5 = flag;
        flag3 = flag1;
        objectupdatecompressed = (SLObjectInfo)slparcelinfo.allObjectsNearby.get(objectupdatecompressed);
_L11:
        if (objectupdatecompressed == null) goto _L6; else goto _L5
_L5:
        flag4 = flag;
        flag2 = flag1;
        flag5 = flag;
        flag3 = flag1;
        i = ((SLObjectInfo) (objectupdatecompressed)).parentID;
        flag4 = flag;
        flag2 = flag1;
        flag5 = flag;
        flag3 = flag1;
        objectupdatecompressed.ApplyObjectUpdate(objectdata);
        flag4 = flag;
        flag2 = flag1;
        flag5 = flag;
        flag3 = flag1;
        slparcelinfo.updateObjectParent(i, objectupdatecompressed);
        flag4 = flag;
        flag2 = flag1;
        flag5 = flag;
        flag3 = flag1;
        break MISSING_BLOCK_LABEL_223;
_L12:
        flag4 = flag;
        flag2 = flag1;
        flag5 = flag;
        flag3 = flag1;
        if (!(objectupdatecompressed instanceof SLObjectAvatarInfo))
        {
            break MISSING_BLOCK_LABEL_543;
        }
        flag4 = flag;
        flag2 = flag1;
        flag5 = flag;
        flag3 = flag1;
        boolean flag7 = ((SLObjectAvatarInfo)objectupdatecompressed).isMyAvatar();
          goto _L7
_L13:
        boolean flag6;
        if (flag6)
        {
            flag = true;
        }
        flag4 = flag;
        flag2 = flag1;
        flag5 = flag;
        flag3 = flag1;
        processMyAvatarUpdate((SLObjectAvatarInfo)objectupdatecompressed);
        flag6 = flag;
          goto _L8
_L6:
        flag4 = flag;
        flag2 = flag1;
        flag5 = flag;
        flag3 = flag1;
        objectupdatecompressed = SLObjectInfo.create(objectdata);
        flag4 = flag;
        flag2 = flag1;
        flag5 = flag;
        flag3 = flag1;
        break MISSING_BLOCK_LABEL_358;
_L14:
        flag4 = flag;
        flag2 = flag1;
        flag5 = flag;
        flag3 = flag1;
        flag6 = flag;
        if (!objectupdatecompressed.isMyAttachment()) goto _L8; else goto _L9
_L9:
        flag4 = flag;
        flag2 = flag1;
        flag5 = flag;
        flag3 = flag1;
        processMyAttachmentUpdate(objectupdatecompressed);
        flag6 = flag;
          goto _L8
        objectupdatecompressed;
        flag = flag4;
        flag1 = flag2;
          goto _L10
        objectupdatecompressed;
        Debug.Warning(objectupdatecompressed);
        flag = flag5;
        flag1 = flag3;
          goto _L10
_L2:
        if (flag1)
        {
            ProcessObjectSelection();
            objectPropertiesRateLimiter.fire();
        }
        if (flag)
        {
            userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value);
        }
        return;
_L4:
        objectupdatecompressed = null;
          goto _L11
        if (((SLObjectInfo) (objectupdatecompressed)).parentID != i)
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        flag6 = flag1;
        flag1 = true;
          goto _L12
_L7:
        if (!flag7) goto _L14; else goto _L13
_L8:
        flag = flag6;
          goto _L10
        if (slparcelinfo.addObject(objectupdatecompressed))
        {
            flag1 = true;
        }
        flag6 = false;
          goto _L12
        flag7 = false;
          goto _L7
    }

    public void HandleOfflineNotification(OfflineNotification offlinenotification)
    {
        ArrayList arraylist = new ArrayList(offlinenotification.AgentBlock_Fields.size());
        for (offlinenotification = offlinenotification.AgentBlock_Fields.iterator(); offlinenotification.hasNext(); arraylist.add(((com.lumiyaviewer.lumiya.slproto.messages.OfflineNotification.AgentBlock)offlinenotification.next()).AgentID)) { }
        userManager.getChatterList().getFriendManager().setUsersOnline(arraylist, false);
    }

    public void HandleOnlineNotification(OnlineNotification onlinenotification)
    {
        ArrayList arraylist = new ArrayList(onlinenotification.AgentBlock_Fields.size());
        for (onlinenotification = onlinenotification.AgentBlock_Fields.iterator(); onlinenotification.hasNext(); arraylist.add(((com.lumiyaviewer.lumiya.slproto.messages.OnlineNotification.AgentBlock)onlinenotification.next()).AgentID)) { }
        userManager.getChatterList().getFriendManager().setUsersOnline(arraylist, true);
    }

    public void HandlePayPriceReply(PayPriceReply paypricereply)
    {
        SLObjectInfo slobjectinfo = (SLObjectInfo)gridConn.parcelInfo.allObjectsNearby.get(paypricereply.ObjectData_Field.ObjectID);
        if (slobjectinfo != null)
        {
            int j = paypricereply.ObjectData_Field.DefaultPayPrice;
            int ai[] = new int[paypricereply.ButtonData_Fields.size()];
            for (int i = 0; i < paypricereply.ButtonData_Fields.size(); i++)
            {
                ai[i] = ((com.lumiyaviewer.lumiya.slproto.messages.PayPriceReply.ButtonData)paypricereply.ButtonData_Fields.get(i)).PayButton;
            }

            slobjectinfo.setPayInfo(PayInfo.create(j, ai));
            if (userManager != null)
            {
                userManager.getObjectsManager().requestObjectProfileUpdate(slobjectinfo.localID);
            }
            eventBus.publish(new SLObjectPayInfoEvent(slobjectinfo));
        }
    }

    public void HandleRegionHandshake(RegionHandshake regionhandshake)
    {
        if (!authReply.isTemporary)
        {
            RegionHandshakeReply regionhandshakereply = new RegionHandshakeReply();
            regionhandshakereply.AgentData_Field.AgentID = circuitInfo.agentID;
            regionhandshakereply.AgentData_Field.SessionID = circuitInfo.sessionID;
            regionhandshakereply.RegionInfo_Field.Flags = 0;
            if (gridConn != null && gridConn.parcelInfo != null)
            {
                gridConn.parcelInfo.terrainData.ApplyRegionInfo(regionhandshake.RegionInfo_Field);
            }
            SendMessage(regionhandshakereply);
            regionName = SLMessage.stringFromVariableOEM(regionhandshake.RegionInfo_Field.SimName);
            if (regionhandshake.RegionInfo2_Field != null && regionhandshake.RegionInfo2_Field.RegionID != null)
            {
                regionID = regionhandshake.RegionInfo2_Field.RegionID;
            }
            isEstateManager = regionhandshake.RegionInfo_Field.IsEstateManager;
            agentNameSubscription = userManager.getUserNames().subscribe(circuitInfo.agentID, new _2D_.Lambda.K1xWCpEh0d4XNuVVYxGUJwEFRxU(this));
            if (eventBus != null)
            {
                eventBus.publish(new SLRegionInfoChangedEvent());
            }
        }
    }

    public void HandleScriptDialog(ScriptDialog scriptdialog)
    {
        String as[];
        Iterator iterator;
        int i;
        int j;
        j = 0;
        if (scriptdialog.Buttons_Fields.size() <= 0)
        {
            break MISSING_BLOCK_LABEL_156;
        }
        as = new String[scriptdialog.Buttons_Fields.size()];
        iterator = scriptdialog.Buttons_Fields.iterator();
        i = 0;
_L5:
        if (!iterator.hasNext()) goto _L2; else goto _L1
_L1:
        as[i] = SLMessage.stringFromVariableUTF(((com.lumiyaviewer.lumiya.slproto.messages.ScriptDialog.Buttons)iterator.next()).ButtonLabel);
        if (!as[i].equals("!!llTextBox!!")) goto _L4; else goto _L3
_L3:
        boolean flag = true;
        j = i;
        i = ((flag) ? 1 : 0);
_L6:
        if (i == 0)
        {
            HandleChatEvent(localChatterID, new SLChatScriptDialog(scriptdialog, agentUUID, as), true);
            return;
        } else
        {
            HandleChatEvent(localChatterID, new SLChatTextBoxDialog(scriptdialog, agentUUID, j), true);
            return;
        }
_L4:
        i++;
          goto _L5
_L2:
        i = 0;
          goto _L6
        as = null;
        i = 0;
          goto _L6
    }

    public void HandleSimulatorViewerTimeMessage(SimulatorViewerTimeMessage simulatorviewertimemessage)
    {
        if (!authReply.isTemporary && gridConn != null && gridConn.parcelInfo != null)
        {
            float f = simulatorviewertimemessage.TimeInfo_Field.SunPhase / 6.283185F + 0.25F;
            f = (float)((double)f - Math.floor(f));
            gridConn.parcelInfo.setSunHour(f);
        }
    }

    public void HandleTeleportFailed(TeleportFailed teleportfailed)
    {
        Debug.Log((new StringBuilder()).append("TeleportFailed: reason = ").append(SLMessage.stringFromVariableOEM(teleportfailed.Info_Field.Reason)).toString());
        teleportRequestSent = false;
        teleportfailed = SLMessage.stringFromVariableOEM(teleportfailed.Info_Field.Reason);
        eventBus.publish(new SLTeleportResultEvent(false, teleportfailed));
    }

    public void HandleTeleportLocal(TeleportLocal teleportlocal)
    {
        teleportRequestSent = false;
        eventBus.publish(new SLTeleportResultEvent(true, null));
    }

    public void HandleTeleportProgress(TeleportProgress teleportprogress)
    {
        Debug.Log((new StringBuilder()).append("Teleport progress: flags = ").append(teleportprogress.Info_Field.TeleportFlags).append(", progress = ").append(SLMessage.stringFromVariableOEM(teleportprogress.Info_Field.Message)).toString());
    }

    public void HandleTeleportStart(TeleportStart teleportstart)
    {
        Debug.Log((new StringBuilder()).append("TeleportStart: flags = ").append(teleportstart.Info_Field.TeleportFlags).toString());
    }

    public void OfferInventoryItem(UUID uuid, SLInventoryEntry slinventoryentry)
    {
        userManager.getInventoryManager().getExecutor().execute(new _2D_.Lambda.K1xWCpEh0d4XNuVVYxGUJwEFRxU._cls1(this, slinventoryentry, uuid));
    }

    public void OfferTeleport(UUID uuid, String s)
    {
        StartLure startlure = new StartLure();
        startlure.AgentData_Field.AgentID = circuitInfo.agentID;
        startlure.AgentData_Field.SessionID = circuitInfo.sessionID;
        startlure.Info_Field.Message = SLMessage.stringToVariableUTF(s);
        s = new com.lumiyaviewer.lumiya.slproto.messages.StartLure.TargetData();
        s.TargetID = uuid;
        startlure.TargetData_Fields.add(s);
        startlure.isReliable = true;
        SendMessage(startlure);
    }

    public void OnCapsEvent(com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEvent capsevent)
    {
        try
        {
            capsEventQueue.add(capsevent);
            selector.wakeup();
            return;
        }
        // Misplaced declaration of an exception variable
        catch (com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEvent capsevent)
        {
            return;
        }
    }

    public void ProcessIdle()
    {
        if (doingObjectSelection && System.currentTimeMillis() > lastObjectSelection + 15000L)
        {
            doingObjectSelection = false;
            ProcessObjectSelectionTimeout();
        }
        if (!teleportRequestSent && getNeedObjectNames() && doingObjectSelection ^ true && System.currentTimeMillis() >= lastObjectSelection + 500L)
        {
            ProcessObjectSelection();
        }
        if (!agentPaused)
        {
            long l = System.currentTimeMillis();
            if (!GridConnectionService.hasVisibleActivities())
            {
                if (l >= lastVisibleActivities + 10000L)
                {
                    DoAgentPause();
                }
            } else
            {
                lastVisibleActivities = l;
            }
        }
        if (objectPropertiesRateLimiter != null)
        {
            objectPropertiesRateLimiter.firePending();
        }
    }

    public void ProcessNetworkError()
    {
        super.ProcessNetworkError();
        Debug.Printf("Network: Network error.", new Object[0]);
        if (modules != null)
        {
            modules.avatarControl.setEnableAgentUpdates(false);
        }
        if (!authReply.isTemporary)
        {
            gridConn.processDisconnect(false, "Network connection lost.");
        }
    }

    public void ProcessTimeout()
    {
        super.ProcessTimeout();
        if (modules != null)
        {
            modules.avatarControl.setEnableAgentUpdates(false);
        }
        if (!authReply.isTemporary)
        {
            gridConn.processDisconnect(false, "Connection has timed out.");
        }
    }

    public void ProcessWakeup()
    {
        super.ProcessWakeup();
_L1:
        com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEvent capsevent = (com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEvent)capsEventQueue.poll();
        if (capsevent == null)
        {
            break MISSING_BLOCK_LABEL_28;
        }
        HandleCapsEvent(capsevent);
          goto _L1
        Exception exception;
        exception;
        ProcessIdle();
        return;
    }

    public void RemoveFriend(UUID uuid)
    {
        TerminateFriendship terminatefriendship = new TerminateFriendship();
        terminatefriendship.AgentData_Field.AgentID = circuitInfo.agentID;
        terminatefriendship.AgentData_Field.SessionID = circuitInfo.sessionID;
        terminatefriendship.ExBlock_Field.OtherID = uuid;
        terminatefriendship.isReliable = true;
        SendMessage(terminatefriendship);
        userManager.getChatterList().getFriendManager().removeFriend(uuid);
    }

    void RequestObjectName(SLObjectInfo slobjectinfo)
    {
        if (slobjectinfo.getId() != null && !objectNamesRequested.containsKey(slobjectinfo.getId()) && forceNeedObjectNames.containsKey(slobjectinfo.getId()) ^ true)
        {
            forceNeedObjectNames.put(slobjectinfo.getId(), slobjectinfo);
        }
        TryWakeUp();
    }

    public void RequestTeleport(UUID uuid, String s)
    {
        SendInstantMessage(uuid, s, 26);
    }

    public boolean RestartRegion(int i)
    {
        if (isEstateManager)
        {
            SendEstateOwnerMessage("restart", new String[] {
                Integer.toString(i)
            });
            return true;
        } else
        {
            return false;
        }
    }

    public void RezObject(SLInventoryEntry slinventoryentry)
    {
        UUID uuid;
        Object obj1;
        obj1 = null;
        uuid = UUIDPool.ZeroUUID;
        if (userManager == null) goto _L2; else goto _L1
_L1:
        Object obj = userManager.getCurrentLocationInfoSnapshot();
        if (obj == null) goto _L2; else goto _L3
_L3:
        obj = ((CurrentLocationInfo) (obj)).parcelData();
        if (obj == null || !((ParcelData) (obj)).isGroupOwned()) goto _L2; else goto _L4
_L4:
        obj = ((ParcelData) (obj)).getOwnerID();
_L6:
        if (obj != null && UUIDPool.ZeroUUID.equals(obj))
        {
            obj = obj1;
        }
        if (obj != null)
        {
            AvatarGroupList avatargrouplist = userManager.getChatterList().getGroupManager().getAvatarGroupList();
            if (avatargrouplist == null || !avatargrouplist.Groups.containsKey(obj))
            {
                obj = uuid;
            }
        } else
        {
            obj = getActiveGroupID();
        }
        uuid = ((UUID) (obj));
        if (obj == null)
        {
            uuid = UUIDPool.ZeroUUID;
        }
        obj = new RezObject();
        ((RezObject) (obj)).AgentData_Field.AgentID = circuitInfo.agentID;
        ((RezObject) (obj)).AgentData_Field.SessionID = circuitInfo.sessionID;
        ((RezObject) (obj)).AgentData_Field.GroupID = uuid;
        ((RezObject) (obj)).RezData_Field.FromTaskID = UUIDPool.ZeroUUID;
        ((RezObject) (obj)).RezData_Field.BypassRaycast = 1;
        ((RezObject) (obj)).RezData_Field.RayStart = modules.avatarControl.getAgentPosition().getPosition();
        ((RezObject) (obj)).RezData_Field.RayEnd = ((RezObject) (obj)).RezData_Field.RayStart.getRotatedOffset(1.5F, getModules().avatarControl.getAgentHeading());
        ((RezObject) (obj)).RezData_Field.RayEndIsIntersection = true;
        ((RezObject) (obj)).RezData_Field.RayTargetID = UUIDPool.ZeroUUID;
        ((RezObject) (obj)).RezData_Field.RezSelected = false;
        ((RezObject) (obj)).RezData_Field.RemoveItem = false;
        ((RezObject) (obj)).RezData_Field.ItemFlags = 0;
        ((RezObject) (obj)).RezData_Field.GroupMask = slinventoryentry.groupMask;
        ((RezObject) (obj)).RezData_Field.EveryoneMask = slinventoryentry.everyoneMask;
        ((RezObject) (obj)).RezData_Field.NextOwnerMask = slinventoryentry.nextOwnerMask;
        ((RezObject) (obj)).InventoryData_Field.ItemID = slinventoryentry.uuid;
        ((RezObject) (obj)).InventoryData_Field.FolderID = slinventoryentry.parentUUID;
        ((RezObject) (obj)).InventoryData_Field.CreatorID = slinventoryentry.creatorUUID;
        ((RezObject) (obj)).InventoryData_Field.OwnerID = slinventoryentry.ownerUUID;
        ((RezObject) (obj)).InventoryData_Field.GroupID = slinventoryentry.groupUUID;
        ((RezObject) (obj)).InventoryData_Field.BaseMask = slinventoryentry.baseMask;
        ((RezObject) (obj)).InventoryData_Field.OwnerMask = slinventoryentry.ownerMask;
        ((RezObject) (obj)).InventoryData_Field.GroupMask = slinventoryentry.groupMask;
        ((RezObject) (obj)).InventoryData_Field.EveryoneMask = slinventoryentry.everyoneMask;
        ((RezObject) (obj)).InventoryData_Field.NextOwnerMask = slinventoryentry.nextOwnerMask;
        ((RezObject) (obj)).InventoryData_Field.GroupOwned = slinventoryentry.isGroupOwned;
        ((RezObject) (obj)).InventoryData_Field.TransactionID = UUID.randomUUID();
        ((RezObject) (obj)).InventoryData_Field.Type = slinventoryentry.assetType;
        ((RezObject) (obj)).InventoryData_Field.InvType = slinventoryentry.invType;
        ((RezObject) (obj)).InventoryData_Field.Flags = slinventoryentry.flags;
        ((RezObject) (obj)).InventoryData_Field.SaleType = slinventoryentry.saleType;
        ((RezObject) (obj)).InventoryData_Field.SalePrice = slinventoryentry.salePrice;
        ((RezObject) (obj)).InventoryData_Field.Name = SLMessage.stringToVariableOEM(slinventoryentry.name);
        ((RezObject) (obj)).InventoryData_Field.Description = SLMessage.stringToVariableOEM(slinventoryentry.description);
        ((RezObject) (obj)).InventoryData_Field.CreationDate = slinventoryentry.creationDate;
        ((RezObject) (obj)).InventoryData_Field.CRC = 0;
        obj.isReliable = true;
        if ((slinventoryentry.ownerMask & 0x8000) == 0)
        {
            ((RezObject) (obj)).setEventListener(new SLMessageEventListener() {

                final SLAgentCircuit this$0;
                final UUID val$folderUUID;

                public void onMessageAcknowledged(SLMessage slmessage)
                {
                    if (SLAgentCircuit._2D_get2(SLAgentCircuit.this) != null)
                    {
                        SLAgentCircuit._2D_get2(SLAgentCircuit.this).getInventoryManager().requestFolderUpdate(folderUUID);
                    }
                }

                public void onMessageTimeout(SLMessage slmessage)
                {
                }

            
            {
                this$0 = SLAgentCircuit.this;
                folderUUID = uuid;
                super();
            }
            });
        }
        SendMessage(((SLMessage) (obj)));
        return;
_L2:
        obj = null;
        if (true) goto _L6; else goto _L5
_L5:
    }

    public void SendChatMessage(ChatterID chatterid, String s)
    {
        switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues()[chatterid.getChatterType().ordinal()])
        {
        default:
            return;

        case 2: // '\002'
            SendLocalChatMessage(s);
            return;

        case 3: // '\003'
            SendInstantMessage(chatterid.getOptionalChatterUUID(), s);
            return;

        case 1: // '\001'
            SendGroupInstantMessage(chatterid.getOptionalChatterUUID(), s);
            return;
        }
    }

    public void SendGenericMessage(String s, String as[])
    {
        GenericMessage genericmessage = new GenericMessage();
        genericmessage.AgentData_Field.AgentID = circuitInfo.agentID;
        genericmessage.AgentData_Field.SessionID = circuitInfo.sessionID;
        genericmessage.AgentData_Field.TransactionID = new UUID(0L, 0L);
        genericmessage.MethodData_Field.Method = SLMessage.stringToVariableOEM(s);
        genericmessage.MethodData_Field.Invoice = new UUID(0L, 0L);
        int i = 0;
        for (int j = as.length; i < j; i++)
        {
            s = as[i];
            com.lumiyaviewer.lumiya.slproto.messages.GenericMessage.ParamList paramlist = new com.lumiyaviewer.lumiya.slproto.messages.GenericMessage.ParamList();
            paramlist.Parameter = SLMessage.stringToVariableOEM(s);
            genericmessage.ParamList_Fields.add(paramlist);
        }

        genericmessage.isReliable = true;
        SendMessage(genericmessage);
    }

    public void SendGroupInstantMessage(UUID uuid, String s)
    {
        ImprovedInstantMessage improvedinstantmessage;
        improvedinstantmessage = new ImprovedInstantMessage();
        improvedinstantmessage.AgentData_Field.AgentID = circuitInfo.agentID;
        improvedinstantmessage.AgentData_Field.SessionID = circuitInfo.sessionID;
        improvedinstantmessage.MessageBlock_Field.FromGroup = false;
        improvedinstantmessage.MessageBlock_Field.ToAgentID = uuid;
        improvedinstantmessage.MessageBlock_Field.ParentEstateID = 0;
        improvedinstantmessage.MessageBlock_Field.RegionID = new UUID(0L, 0L);
        improvedinstantmessage.MessageBlock_Field.Position = modules.avatarControl.getAgentPosition().getPosition();
        improvedinstantmessage.MessageBlock_Field.Offline = 0;
        improvedinstantmessage.MessageBlock_Field.Dialog = 17;
        improvedinstantmessage.MessageBlock_Field.ID = uuid;
        improvedinstantmessage.MessageBlock_Field.Timestamp = 0;
        improvedinstantmessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo");
        improvedinstantmessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF(s);
        improvedinstantmessage.MessageBlock_Field.BinaryBucket = new byte[1];
        improvedinstantmessage.isReliable = true;
        s = startedGroupSessions;
        s;
        JVM INSTR monitorenter ;
        if (startedGroupSessions.contains(uuid)) goto _L2; else goto _L1
_L1:
        SendGroupSessionStart(uuid);
        pendingGroupMessages.add(improvedinstantmessage);
_L4:
        s;
        JVM INSTR monitorexit ;
        return;
_L2:
        SendMessage(improvedinstantmessage);
        if (true) goto _L4; else goto _L3
_L3:
        uuid;
        throw uuid;
    }

    public boolean SendInstantMessage(UUID uuid, String s)
    {
        return SendInstantMessage(uuid, s, 0);
    }

    public void SendLocalChatMessage(String s)
    {
        Object obj;
        int i;
        int j;
        boolean flag1 = false;
        boolean flag = false;
        i = ((flag) ? 1 : 0);
        obj = s;
        if (!s.startsWith("/"))
        {
            break MISSING_BLOCK_LABEL_103;
        }
        i = 1;
        j = 0;
        for (; i < s.length() && Character.isDigit(s.charAt(i)); i++)
        {
            j++;
        }

        i = ((flag) ? 1 : 0);
        obj = s;
        if (j < 0)
        {
            break MISSING_BLOCK_LABEL_103;
        }
        i = ((flag1) ? 1 : 0);
        int k = Integer.parseInt(s.substring(1, j + 1));
        i = k;
        obj = s.substring(j + 1).trim();
        i = k;
_L1:
        if (!getModules().rlvController.onSendLocalChat(i, ((String) (obj))))
        {
            return;
        } else
        {
            s = new ChatFromViewer();
            ((ChatFromViewer) (s)).AgentData_Field.AgentID = circuitInfo.agentID;
            ((ChatFromViewer) (s)).AgentData_Field.SessionID = circuitInfo.sessionID;
            ((ChatFromViewer) (s)).ChatData_Field.Channel = i;
            ((ChatFromViewer) (s)).ChatData_Field.Type = 1;
            ((ChatFromViewer) (s)).ChatData_Field.Message = SLMessage.stringToVariableUTF(((String) (obj)));
            s.isReliable = true;
            SendMessage(s);
            return;
        }
        obj;
        ((Exception) (obj)).printStackTrace();
        obj = s;
          goto _L1
    }

    void SendLogoutRequest()
    {
        Debug.Log("Logout: Sending logout request.");
        modules.avatarControl.setEnableAgentUpdates(false);
        LogoutRequest logoutrequest = new LogoutRequest();
        logoutrequest.AgentData_Field.AgentID = circuitInfo.agentID;
        logoutrequest.AgentData_Field.SessionID = circuitInfo.sessionID;
        logoutrequest.isReliable = true;
        logoutrequest.setEventListener(new SLMessageEventListener() {

            final SLAgentCircuit this$0;

            public void onMessageAcknowledged(SLMessage slmessage)
            {
                Debug.Log("Logout: Logout request acknowledged.");
                gridConn.processDisconnect(true, "Logged out.");
            }

            public void onMessageTimeout(SLMessage slmessage)
            {
                Debug.Log("Logout: LogoutRequest timed out!");
                gridConn.processDisconnect(false, "Logout request has timed out.");
            }

            
            {
                this$0 = SLAgentCircuit.this;
                super();
            }
        });
        SendMessage(logoutrequest);
    }

    public void SendScriptDialogReply(UUID uuid, int i, int j, String s)
    {
        ScriptDialogReply scriptdialogreply = new ScriptDialogReply();
        scriptdialogreply.AgentData_Field.AgentID = circuitInfo.agentID;
        scriptdialogreply.AgentData_Field.SessionID = circuitInfo.sessionID;
        scriptdialogreply.isReliable = true;
        scriptdialogreply.Data_Field.ObjectID = uuid;
        scriptdialogreply.Data_Field.ChatChannel = i;
        scriptdialogreply.Data_Field.ButtonIndex = j;
        scriptdialogreply.Data_Field.ButtonLabel = SLMessage.stringToVariableUTF(s);
        SendMessage(scriptdialogreply);
    }

    void SendUseCode()
    {
        Debug.Printf("Using circuitCode: %d", new Object[] {
            Integer.valueOf(circuitInfo.circuitCode)
        });
        UseCircuitCode usecircuitcode = new UseCircuitCode();
        usecircuitcode.CircuitCode_Field.Code = circuitInfo.circuitCode;
        usecircuitcode.CircuitCode_Field.SessionID = circuitInfo.sessionID;
        usecircuitcode.CircuitCode_Field.ID = circuitInfo.agentID;
        usecircuitcode.isReliable = true;
        usecircuitcode.setEventListener(new SLMessageEventListener() {

            final SLAgentCircuit this$0;

            public void onMessageAcknowledged(SLMessage slmessage)
            {
                Debug.Log("SLAgentCircuit: UseCircuitCode acknowledged.");
                if (!authReply.isTemporary)
                {
                    if (authReply.fromTeleport)
                    {
                        Debug.Log("SLAgentCircuit: Ack from teleport, sending Teleport success.");
                        SLAgentCircuit._2D_get0(SLAgentCircuit.this).publish(new SLTeleportResultEvent(true, null));
                    } else
                    {
                        gridConn.notifyLoginSuccess();
                    }
                    SLAgentCircuit._2D_wrap0(SLAgentCircuit.this);
                    if (SLAgentCircuit._2D_get1(SLAgentCircuit.this) != null)
                    {
                        SLAgentCircuit._2D_get1(SLAgentCircuit.this).HandleCircuitReady();
                    }
                }
            }

            public void onMessageTimeout(SLMessage slmessage)
            {
                if (authReply.fromTeleport)
                {
                    SLAgentCircuit._2D_get0(SLAgentCircuit.this).publish(new SLTeleportResultEvent(false, "Timed out while connecting to the simulator."));
                    return;
                } else
                {
                    gridConn.notifyLoginError("Timed out while connecting to the simulator.");
                    return;
                }
            }

            
            {
                this$0 = SLAgentCircuit.this;
                super();
            }
        });
        SendMessage(usecircuitcode);
    }

    public void StartGroupSessionForVoice(UUID uuid)
    {
        boolean flag = false;
        Set set = startedGroupSessions;
        set;
        JVM INSTR monitorenter ;
        if (startedGroupSessions.contains(uuid))
        {
            break MISSING_BLOCK_LABEL_29;
        }
        SendGroupSessionStart(uuid);
        flag = true;
        set;
        JVM INSTR monitorexit ;
        if (!flag)
        {
            modules.voice.onGroupSessionReady(uuid);
        }
        return;
        uuid;
        throw uuid;
    }

    public void TeleportToGlobalPosition(LLVector3 llvector3)
    {
        int i = (int)Math.floor(llvector3.x);
        int j = (int)Math.floor(llvector3.y);
        long l = i - i % 256;
        l = (long)(j - j % 256) | l << 32;
        LLVector3 llvector3_1 = new LLVector3(llvector3.x % 256F, llvector3.y % 256F, llvector3.z);
        LLVector3 llvector3_2 = new LLVector3(llvector3_1);
        llvector3_2.x = llvector3_2.x + 1.0F;
        Debug.Printf("regionHandle = %s, globalPos = %s", new Object[] {
            Long.toHexString(l), llvector3
        });
        teleportRequestSent = true;
        llvector3 = new TeleportLocationRequest();
        ((TeleportLocationRequest) (llvector3)).AgentData_Field.AgentID = circuitInfo.agentID;
        ((TeleportLocationRequest) (llvector3)).AgentData_Field.SessionID = circuitInfo.sessionID;
        ((TeleportLocationRequest) (llvector3)).Info_Field.RegionHandle = l;
        ((TeleportLocationRequest) (llvector3)).Info_Field.Position = llvector3_1;
        ((TeleportLocationRequest) (llvector3)).Info_Field.LookAt = llvector3_2;
        llvector3.isReliable = true;
        llvector3.setEventListener(new SLMessageEventListener() {

            final SLAgentCircuit this$0;

            public void onMessageAcknowledged(SLMessage slmessage)
            {
            }

            public void onMessageTimeout(SLMessage slmessage)
            {
                SLAgentCircuit._2D_get0(SLAgentCircuit.this).publish(new SLTeleportResultEvent(false, "Teleport request has timed out."));
            }

            
            {
                this$0 = SLAgentCircuit.this;
                super();
            }
        });
        SendMessage(llvector3);
    }

    public void TeleportToLandmarkAsset(UUID uuid)
    {
        if (!getModules().rlvController.canTeleportToLandmark())
        {
            return;
        } else
        {
            teleportRequestSent = true;
            TeleportLandmarkRequest teleportlandmarkrequest = new TeleportLandmarkRequest();
            teleportlandmarkrequest.Info_Field.AgentID = circuitInfo.agentID;
            teleportlandmarkrequest.Info_Field.SessionID = circuitInfo.sessionID;
            teleportlandmarkrequest.Info_Field.LandmarkID = uuid;
            teleportlandmarkrequest.isReliable = true;
            teleportlandmarkrequest.setEventListener(new SLMessageEventListener() {

                final SLAgentCircuit this$0;

                public void onMessageAcknowledged(SLMessage slmessage)
                {
                }

                public void onMessageTimeout(SLMessage slmessage)
                {
                    SLAgentCircuit._2D_get0(SLAgentCircuit.this).publish(new SLTeleportResultEvent(false, "Teleport request has timed out."));
                }

            
            {
                this$0 = SLAgentCircuit.this;
                super();
            }
            });
            SendMessage(teleportlandmarkrequest);
            return;
        }
    }

    public boolean TeleportToLocalPosition(LLVector3 llvector3)
    {
        if (regionID != null)
        {
            Debug.Printf("Teleport: localPos = %s, regionHandle = %d", new Object[] {
                llvector3.toString(), Long.valueOf(regionHandle)
            });
            teleportRequestSent = true;
            TeleportLocationRequest teleportlocationrequest = new TeleportLocationRequest();
            teleportlocationrequest.AgentData_Field.AgentID = circuitInfo.agentID;
            teleportlocationrequest.AgentData_Field.SessionID = circuitInfo.sessionID;
            teleportlocationrequest.Info_Field.RegionHandle = regionHandle;
            teleportlocationrequest.Info_Field.Position = llvector3;
            teleportlocationrequest.Info_Field.LookAt = new LLVector3(llvector3);
            llvector3 = teleportlocationrequest.Info_Field.LookAt;
            llvector3.x = llvector3.x + 10F;
            teleportlocationrequest.isReliable = true;
            teleportlocationrequest.setEventListener(new SLMessageEventListener() {

                final SLAgentCircuit this$0;

                public void onMessageAcknowledged(SLMessage slmessage)
                {
                }

                public void onMessageTimeout(SLMessage slmessage)
                {
                    SLAgentCircuit._2D_get0(SLAgentCircuit.this).publish(new SLTeleportResultEvent(false, "Teleport request has timed out."));
                }

            
            {
                this$0 = SLAgentCircuit.this;
                super();
            }
            });
            SendMessage(teleportlocationrequest);
            return true;
        } else
        {
            return false;
        }
    }

    public void TeleportToLure(UUID uuid)
    {
        teleportRequestSent = true;
        TeleportLureRequest teleportlurerequest = new TeleportLureRequest();
        teleportlurerequest.Info_Field.AgentID = circuitInfo.agentID;
        teleportlurerequest.Info_Field.SessionID = circuitInfo.sessionID;
        teleportlurerequest.Info_Field.LureID = uuid;
        teleportlurerequest.isReliable = true;
        teleportlurerequest.setEventListener(new SLMessageEventListener() {

            final SLAgentCircuit this$0;

            public void onMessageAcknowledged(SLMessage slmessage)
            {
            }

            public void onMessageTimeout(SLMessage slmessage)
            {
                SLAgentCircuit._2D_get0(SLAgentCircuit.this).publish(new SLTeleportResultEvent(false, "Teleport request has timed out."));
            }

            
            {
                this$0 = SLAgentCircuit.this;
                super();
            }
        });
        SendMessage(teleportlurerequest);
    }

    public void TeleportToRegion(long l, int i, int j, int k)
    {
        if (!getModules().rlvController.canTeleportToLocation())
        {
            return;
        } else
        {
            Debug.Log((new StringBuilder()).append("TeleportToRegion: regionHandle = ").append(Long.toHexString(l)).append(", pos = (").append(i).append(", ").append(j).append(", ").append(k).append(")").toString());
            teleportRequestSent = true;
            TeleportLocationRequest teleportlocationrequest = new TeleportLocationRequest();
            teleportlocationrequest.AgentData_Field.AgentID = circuitInfo.agentID;
            teleportlocationrequest.AgentData_Field.SessionID = circuitInfo.sessionID;
            teleportlocationrequest.Info_Field.RegionHandle = l;
            teleportlocationrequest.Info_Field.Position = new LLVector3(i, j, k);
            teleportlocationrequest.Info_Field.LookAt = new LLVector3(0.0F, 1.0F, 0.0F);
            teleportlocationrequest.isReliable = true;
            teleportlocationrequest.setEventListener(new SLMessageEventListener() {

                final SLAgentCircuit this$0;

                public void onMessageAcknowledged(SLMessage slmessage)
                {
                }

                public void onMessageTimeout(SLMessage slmessage)
                {
                    SLAgentCircuit._2D_get0(SLAgentCircuit.this).publish(new SLTeleportResultEvent(false, "Teleport request has timed out."));
                }

            
            {
                this$0 = SLAgentCircuit.this;
                super();
            }
            });
            SendMessage(teleportlocationrequest);
            return;
        }
    }

    public void TouchObject(int i)
    {
        Object obj = new ObjectGrab();
        ((ObjectGrab) (obj)).AgentData_Field.AgentID = circuitInfo.agentID;
        ((ObjectGrab) (obj)).AgentData_Field.SessionID = circuitInfo.sessionID;
        ((ObjectGrab) (obj)).ObjectData_Field.LocalID = i;
        ((ObjectGrab) (obj)).ObjectData_Field.GrabOffset = new LLVector3();
        obj.isReliable = true;
        SendMessage(((SLMessage) (obj)));
        obj = new ObjectDeGrab();
        ((ObjectDeGrab) (obj)).AgentData_Field.AgentID = circuitInfo.agentID;
        ((ObjectDeGrab) (obj)).AgentData_Field.SessionID = circuitInfo.sessionID;
        ((ObjectDeGrab) (obj)).ObjectData_Field.LocalID = i;
        obj.isReliable = true;
        SendMessage(((SLMessage) (obj)));
    }

    public void TouchObjectFace(SLObjectInfo slobjectinfo, int i, float f, float f1, float f2, float f3, float f4, 
            float f5, float f6)
    {
        Debug.Printf("Touch: Object %d, face %d, pos (%f, %f, %f), uv (%f, %f)", new Object[] {
            Integer.valueOf(slobjectinfo.localID), Integer.valueOf(i), Float.valueOf(f), Float.valueOf(f1), Float.valueOf(f2), Float.valueOf(f3), Float.valueOf(f4)
        });
        Object obj = new ObjectGrab();
        ((ObjectGrab) (obj)).AgentData_Field.AgentID = circuitInfo.agentID;
        ((ObjectGrab) (obj)).AgentData_Field.SessionID = circuitInfo.sessionID;
        ((ObjectGrab) (obj)).ObjectData_Field.LocalID = slobjectinfo.localID;
        ((ObjectGrab) (obj)).ObjectData_Field.GrabOffset = new LLVector3();
        com.lumiyaviewer.lumiya.slproto.messages.ObjectGrab.SurfaceInfo surfaceinfo = new com.lumiyaviewer.lumiya.slproto.messages.ObjectGrab.SurfaceInfo();
        surfaceinfo.FaceIndex = i;
        surfaceinfo.Position = new LLVector3(f, f1, f2);
        surfaceinfo.UVCoord = new LLVector3(f3, f4, 0.0F);
        surfaceinfo.STCoord = new LLVector3(f5, f6, 0.0F);
        surfaceinfo.Normal = new LLVector3(1.0F, 0.0F, 0.0F);
        surfaceinfo.Binormal = new LLVector3(0.0F, 0.0F, 1.0F);
        ((ObjectGrab) (obj)).SurfaceInfo_Fields.add(surfaceinfo);
        obj.isReliable = true;
        SendMessage(((SLMessage) (obj)));
        obj = new ObjectDeGrab();
        ((ObjectDeGrab) (obj)).AgentData_Field.AgentID = circuitInfo.agentID;
        ((ObjectDeGrab) (obj)).AgentData_Field.SessionID = circuitInfo.sessionID;
        ((ObjectDeGrab) (obj)).ObjectData_Field.LocalID = slobjectinfo.localID;
        obj.isReliable = true;
        SendMessage(((SLMessage) (obj)));
    }

    public void TryWakeUp()
    {
        try
        {
            selector.wakeup();
            return;
        }
        catch (Exception exception)
        {
            return;
        }
    }

    public void UnpauseAgent()
    {
        lastVisibleActivities = System.currentTimeMillis();
        if (agentPaused)
        {
            DoAgentResume();
        }
    }

    public LLVector3d getAgentGlobalPosition()
    {
        if (modules != null)
        {
            LLVector3 llvector3 = modules.avatarControl.getAgentPosition().getPosition();
            int i = (int)(regionHandle >> 32 & 0xffffffffL);
            int j = (int)(regionHandle & 0xffffffffL);
            LLVector3d llvector3d = new LLVector3d();
            llvector3d.x = (double)i + (double)llvector3.x;
            llvector3d.y = (double)j + (double)llvector3.y;
            llvector3d.z = llvector3.z;
            return llvector3d;
        } else
        {
            return null;
        }
    }

    public String getAgentSLURL()
    {
        if (modules != null && Objects.equal(authReply.loginURL, "https://login.agni.lindenlab.com/cgi-bin/login.cgi") && regionName != null)
        {
            Object obj = modules.avatarControl.getAgentPosition().getPosition();
            try
            {
                obj = String.format("http://maps.secondlife.com/secondlife/%s/%d/%d/%d", new Object[] {
                    URLEncoder.encode(regionName, "UTF-8"), Integer.valueOf((int)((LLVector3) (obj)).x), Integer.valueOf((int)((LLVector3) (obj)).y), Integer.valueOf((int)((LLVector3) (obj)).z)
                });
            }
            catch (UnsupportedEncodingException unsupportedencodingexception)
            {
                return null;
            }
            return ((String) (obj));
        } else
        {
            return null;
        }
    }

    public UUID getAgentUUID()
    {
        return agentUUID;
    }

    public SLCaps getCaps()
    {
        return caps;
    }

    public boolean getIsEstateManager()
    {
        return isEstateManager;
    }

    public ChatterID getLocalChatterID()
    {
        return localChatterID;
    }

    public SLModules getModules()
    {
        return modules;
    }

    public SLObjectProfileData getObjectProfile(int i)
    {
        SLObjectInfo slobjectinfo = gridConn.parcelInfo.getObjectInfo(i);
        if (slobjectinfo != null)
        {
            SLObjectProfileData slobjectprofiledata = SLObjectProfileData.create(slobjectinfo);
            if (!slobjectprofiledata.name().isPresent() && slobjectinfo.isDead ^ true)
            {
                RequestObjectName(slobjectinfo);
            }
            return slobjectprofiledata;
        } else
        {
            return null;
        }
    }

    public String getRegionName()
    {
        return regionName;
    }

    public UUID getSessionID()
    {
        return circuitInfo.sessionID;
    }

    public Boolean isUserTyping(UUID uuid)
    {
        return Boolean.valueOf(typingUsers.contains(uuid));
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_SLAgentCircuit_14593(UserName username)
    {
        agentUserName.set(username);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_SLAgentCircuit_77024(SLInventoryEntry slinventoryentry, UUID uuid)
    {
        Object obj = new ArrayList();
        ((List) (obj)).add(slinventoryentry);
        if (slinventoryentry.isFolder)
        {
            ((List) (obj)).addAll(modules.inventory.CollectGiveableItems(slinventoryentry));
        }
        ImprovedInstantMessage improvedinstantmessage = new ImprovedInstantMessage();
        improvedinstantmessage.AgentData_Field.AgentID = circuitInfo.agentID;
        improvedinstantmessage.AgentData_Field.SessionID = circuitInfo.sessionID;
        improvedinstantmessage.MessageBlock_Field.FromGroup = false;
        improvedinstantmessage.MessageBlock_Field.ToAgentID = uuid;
        improvedinstantmessage.MessageBlock_Field.ParentEstateID = 0;
        improvedinstantmessage.MessageBlock_Field.RegionID = new UUID(0L, 0L);
        improvedinstantmessage.MessageBlock_Field.Position = new LLVector3();
        improvedinstantmessage.MessageBlock_Field.Offline = 0;
        improvedinstantmessage.MessageBlock_Field.Dialog = 4;
        improvedinstantmessage.MessageBlock_Field.ID = UUID.randomUUID();
        improvedinstantmessage.MessageBlock_Field.Timestamp = 0;
        improvedinstantmessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo");
        improvedinstantmessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF(slinventoryentry.name);
        ByteBuffer bytebuffer = ByteBuffer.wrap(new byte[((List) (obj)).size() * 17]);
        bytebuffer.order(ByteOrder.BIG_ENDIAN);
        obj = ((Iterable) (obj)).iterator();
        while (((Iterator) (obj)).hasNext()) 
        {
            SLInventoryEntry slinventoryentry1 = (SLInventoryEntry)((Iterator) (obj)).next();
            int i;
            if (slinventoryentry1.isFolder)
            {
                i = SLAssetType.AT_CATEGORY.getTypeCode();
            } else
            {
                i = slinventoryentry1.assetType;
            }
            bytebuffer.put((byte)i);
            bytebuffer.putLong(slinventoryentry1.uuid.getMostSignificantBits());
            bytebuffer.putLong(slinventoryentry1.uuid.getLeastSignificantBits());
        }
        bytebuffer.position(0);
        improvedinstantmessage.MessageBlock_Field.BinaryBucket = bytebuffer.array();
        improvedinstantmessage.isReliable = true;
        SendMessage(improvedinstantmessage);
        HandleChatEvent(ChatterID.getUserChatterID(agentUUID, uuid), new SLChatInventoryItemOfferedByYouEvent(agentUUID, slinventoryentry.name), false);
    }

    void processMyAttachmentUpdate(SLObjectInfo slobjectinfo)
    {
        if (slobjectinfo != null && !slobjectinfo.nameKnown && slobjectinfo.isDead ^ true)
        {
            RequestObjectName(slobjectinfo);
        }
        getModules().avatarAppearance.UpdateMyAttachments();
    }

    public void sendTypingNotify(UUID uuid, boolean flag)
    {
        byte byte0;
        if (flag)
        {
            byte0 = 41;
        } else
        {
            byte0 = 42;
        }
        SendInstantMessage(uuid, "", byte0);
    }
}
