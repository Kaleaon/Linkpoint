package com.lumiyaviewer.lumiya.slproto.modules;

import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.AsyncLimitsRequestHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.SLMessageEventListener;
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.handler.SLEventQueueMessageHandler;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import com.lumiyaviewer.lumiya.slproto.messages.AgentDataUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.AgentDataUpdateRequest;
import com.lumiyaviewer.lumiya.slproto.messages.AgentGroupDataUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarGroupsReply;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarNotesReply;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarNotesUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPicksReply;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesRequest;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.PickDelete;
import com.lumiyaviewer.lumiya.slproto.messages.PickInfoReply;
import com.lumiyaviewer.lumiya.slproto.messages.PickInfoUpdate;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AgentGroupDataInfo;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import com.lumiyaviewer.lumiya.slproto.users.manager.AvatarPickKey;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.io.IOException;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class SLUserProfiles extends SLModule {
    public static final int AVATAR_AGEVERIFIED = 32;
    public static final int AVATAR_ALLOW_PUBLISH = 1;
    public static final int AVATAR_IDENTIFIED = 4;
    public static final int AVATAR_MATURE_PUBLISH = 2;
    public static final int AVATAR_ONLINE = 16;
    public static final int AVATAR_TRANSACTED = 8;
    private final RequestHandler<UUID> agentDataUpdateRequestHandler = new AsyncLimitsRequestHandler(this.agentCircuit, new SimpleRequestHandler<UUID>() {
        public void onRequest(@Nonnull UUID uuid) {
            SLUserProfiles.this.requestAgentDataUpdate();
        }
    }, false, 3, 15000);
    private ResultHandler<UUID, AgentDataUpdate> agentDataUpdateResultHandler;
    private ResultHandler<UUID, AvatarGroupList> avatarGroupListsResultHandler;
    private final RequestHandler<UUID> avatarNotesRequestHandler = new AsyncLimitsRequestHandler(this.agentCircuit, new SimpleRequestHandler<UUID>() {
        public void onRequest(@Nonnull UUID uuid) {
            SLUserProfiles.this.agentCircuit.SendGenericMessage("avatarnotesrequest", new String[]{uuid.toString()});
        }
    }, false, 3, 15000);
    private ResultHandler<UUID, AvatarNotesReply> avatarNotesResultHandler;
    private final RequestHandler<AvatarPickKey> avatarPickInfosRequestHandler = new AsyncLimitsRequestHandler(this.agentCircuit, new SimpleRequestHandler<AvatarPickKey>() {
        public void onRequest(@Nonnull AvatarPickKey avatarPickKey) {
            SLUserProfiles.this.agentCircuit.SendGenericMessage("pickinforequest", new String[]{avatarPickKey.avatarID.toString(), avatarPickKey.pickID.toString()});
        }
    }, false, 3, 15000);
    private ResultHandler<AvatarPickKey, PickInfoReply> avatarPickInfosResultHandler;
    private final RequestHandler<UUID> avatarPicksRequestHandler = new AsyncLimitsRequestHandler(this.agentCircuit, new SimpleRequestHandler<UUID>() {
        public void onRequest(@Nonnull UUID uuid) {
            SLUserProfiles.this.agentCircuit.SendGenericMessage("avatarpicksrequest", new String[]{uuid.toString()});
        }
    }, false, 3, 15000);
    private ResultHandler<UUID, AvatarPicksReply> avatarPicksResultHandler;
    private final RequestHandler<UUID> avatarPropertiesRequestHandler = new AsyncLimitsRequestHandler(this.agentCircuit, new SimpleRequestHandler<UUID>() {
        public void onRequest(@Nonnull UUID uuid) {
            Debug.Printf("AvatarGroupList: Requesting avatar properties for %s", uuid.toString());
            AvatarPropertiesRequest avatarPropertiesRequest = new AvatarPropertiesRequest();
            avatarPropertiesRequest.AgentData_Field.AgentID = SLUserProfiles.this.circuitInfo.agentID;
            avatarPropertiesRequest.AgentData_Field.SessionID = SLUserProfiles.this.circuitInfo.sessionID;
            avatarPropertiesRequest.AgentData_Field.AvatarID = uuid;
            avatarPropertiesRequest.isReliable = true;
            SLUserProfiles.this.SendMessage(avatarPropertiesRequest);
            if (uuid.equals(SLUserProfiles.this.circuitInfo.agentID)) {
                SLUserProfiles.this.requestAgentDataUpdate();
            }
        }
    }, false, 3, 15000);
    private ResultHandler<UUID, AvatarPropertiesReply> avatarPropertiesResultHandler;
    private boolean requestedNewGroupData = false;
    @Nullable
    private final String setHomeLocationCap;
    /* access modifiers changed from: private */
    public final UserManager userManager;

    SLUserProfiles(SLAgentCircuit sLAgentCircuit, SLCaps sLCaps) {
        super(sLAgentCircuit);
        this.userManager = UserManager.getUserManager(sLAgentCircuit.circuitInfo.agentID);
        this.setHomeLocationCap = sLCaps.getCapability(SLCaps.SLCapability.HomeLocation);
    }

    public void DeletePick(UUID uuid) {
        PickDelete pickDelete = new PickDelete();
        pickDelete.AgentData_Field.AgentID = this.circuitInfo.agentID;
        pickDelete.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        pickDelete.Data_Field.PickID = uuid;
        pickDelete.isReliable = true;
        pickDelete.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() {
            public void onMessageAcknowledged(SLMessage sLMessage) {
                super.onMessageAcknowledged(sLMessage);
                if (SLUserProfiles.this.userManager != null) {
                    SLUserProfiles.this.userManager.getAvatarPicks().requestUpdate(SLUserProfiles.this.userManager.getUserID());
                }
            }
        SendMessage(pickDelete);
    }

    @SLMessageHandler
    public void HandleAgentDataUpdate(AgentDataUpdate agentDataUpdate) {
        if (this.agentDataUpdateResultHandler != null) {
            this.agentDataUpdateResultHandler.onResultData(agentDataUpdate.AgentData_Field.AgentID, agentDataUpdate);
        }
    }

    @SLEventQueueMessageHandler(eventName = SLCapEventQueue.CapsEventType.AgentGroupDataUpdate)
    public void HandleAgentGroupDataUpdate(LLSDNode lLSDNode) {
        try {
            AgentGroupDataInfo agentGroupDataInfo = (AgentGroupDataInfo) lLSDNode.toObject(AgentGroupDataInfo.class);
            if (this.avatarGroupListsResultHandler != null) {
                AvatarGroupList avatarGroupList = new AvatarGroupList(agentGroupDataInfo);
                this.avatarGroupListsResultHandler.onResultData(avatarGroupList.avatarID, avatarGroupList);
                if (!avatarGroupList.newGroupDataValid && (!this.requestedNewGroupData)) {
                    this.requestedNewGroupData = true;
                    requestAgentDataUpdate();
                }
            }
        } catch (LLSDException e) {
            Debug.Warning(e);
        }
    }

    @SLMessageHandler
    public void HandleAgentGroupDataUpdate(AgentGroupDataUpdate agentGroupDataUpdate) {
        if (this.avatarGroupListsResultHandler != null) {
            AvatarGroupList avatarGroupList = new AvatarGroupList(agentGroupDataUpdate);
            this.avatarGroupListsResultHandler.onResultData(avatarGroupList.avatarID, avatarGroupList);
        }
    }

    @SLEventQueueMessageHandler(eventName = SLCapEventQueue.CapsEventType.AvatarGroupsReply)
    public void HandleAvatarGroupsReply(LLSDNode lLSDNode) {
        try {
            AgentGroupDataInfo agentGroupDataInfo = (AgentGroupDataInfo) lLSDNode.toObject(AgentGroupDataInfo.class);
            if (this.avatarGroupListsResultHandler != null) {
                AvatarGroupList avatarGroupList = new AvatarGroupList(agentGroupDataInfo);
                if (!Objects.equal(avatarGroupList.avatarID, this.circuitInfo.agentID)) {
                    this.avatarGroupListsResultHandler.onResultData(avatarGroupList.avatarID, avatarGroupList);
                }
            }
        } catch (LLSDException e) {
            e.printStackTrace();
        }
    }

    @SLMessageHandler
    public void HandleAvatarGroupsReply(AvatarGroupsReply avatarGroupsReply) {
        if (!Objects.equal(avatarGroupsReply.AgentData_Field.AvatarID, this.circuitInfo.agentID) && this.avatarGroupListsResultHandler != null) {
            AvatarGroupList avatarGroupList = new AvatarGroupList(avatarGroupsReply);
            this.avatarGroupListsResultHandler.onResultData(avatarGroupList.avatarID, avatarGroupList);
        }
    }

    @SLMessageHandler
    public void HandleAvatarNotesReply(AvatarNotesReply avatarNotesReply) {
        if (this.avatarNotesResultHandler != null) {
            this.avatarNotesResultHandler.onResultData(avatarNotesReply.Data_Field.TargetID, avatarNotesReply);
        }
    }

    @SLMessageHandler
    public void HandleAvatarPicksReply(AvatarPicksReply avatarPicksReply) {
        if (this.avatarPicksResultHandler != null) {
            this.avatarPicksResultHandler.onResultData(avatarPicksReply.AgentData_Field.TargetID, avatarPicksReply);
        }
    }

    @SLMessageHandler
    public void HandleAvatarPropertiesReply(AvatarPropertiesReply avatarPropertiesReply) {
        if (this.avatarPropertiesResultHandler != null) {
            this.avatarPropertiesResultHandler.onResultData(avatarPropertiesReply.AgentData_Field.AvatarID, avatarPropertiesReply);
        }
    }

    public void HandleCircuitReady() {
        if (this.userManager != null) {
            this.avatarPropertiesResultHandler = this.userManager.getAvatarProperties().getRequestSource().attachRequestHandler(this.avatarPropertiesRequestHandler);
            this.avatarNotesResultHandler = this.userManager.getAvatarNotes().getRequestSource().attachRequestHandler(this.avatarNotesRequestHandler);
            this.avatarPicksResultHandler = this.userManager.getAvatarPicks().getRequestSource().attachRequestHandler(this.avatarPicksRequestHandler);
            this.avatarPickInfosResultHandler = this.userManager.getAvatarPickInfos().getRequestSource().attachRequestHandler(this.avatarPickInfosRequestHandler);
            this.avatarGroupListsResultHandler = this.userManager.getAvatarGroupLists().getRequestSource().attachRequestHandler(this.avatarPropertiesRequestHandler);
            this.agentDataUpdateResultHandler = this.userManager.getAgentDataUpdates().getRequestSource().attachRequestHandler(this.agentDataUpdateRequestHandler);
        }
    }

    public void HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.getAvatarProperties().getRequestSource().detachRequestHandler(this.avatarPropertiesRequestHandler);
            this.userManager.getAvatarNotes().getRequestSource().detachRequestHandler(this.avatarNotesRequestHandler);
            this.userManager.getAvatarPicks().getRequestSource().detachRequestHandler(this.avatarPicksRequestHandler);
            this.userManager.getAvatarPickInfos().getRequestSource().detachRequestHandler(this.avatarPickInfosRequestHandler);
        }
    }

    @SLMessageHandler
    public void HandlePickInfoReply(PickInfoReply pickInfoReply) {
        if (this.avatarPickInfosResultHandler != null) {
            this.avatarPickInfosResultHandler.onResultData(new AvatarPickKey(pickInfoReply.Data_Field.CreatorID, pickInfoReply.Data_Field.PickID), pickInfoReply);
        }
    }

    public void SaveUserNotes(UUID uuid, String str) {
        AvatarNotesUpdate avatarNotesUpdate = new AvatarNotesUpdate();
        avatarNotesUpdate.AgentData_Field.AgentID = this.circuitInfo.agentID;
        avatarNotesUpdate.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        avatarNotesUpdate.Data_Field.TargetID = uuid;
        avatarNotesUpdate.Data_Field.Notes = SLMessage.stringToVariableUTF(str);
        avatarNotesUpdate.isReliable = true;
        SendMessage(avatarNotesUpdate);
        if (this.avatarNotesResultHandler != null) {
            AvatarNotesReply avatarNotesReply = new AvatarNotesReply();
            avatarNotesReply.AgentData_Field.AgentID = this.circuitInfo.agentID;
            avatarNotesReply.Data_Field.Notes = SLMessage.stringToVariableUTF(str);
            avatarNotesReply.Data_Field.TargetID = uuid;
            this.avatarNotesResultHandler.onResultData(uuid, avatarNotesReply);
        }
    }

    public boolean SetHomeLocation() {
        if (this.setHomeLocationCap == null) {
            return false;
        }
        LLVector3 position = this.agentCircuit.getModules().avatarControl.getAgentPosition().getPosition();
        double agentHeading = (((double) this.agentCircuit.getModules().avatarControl.getAgentHeading()) * 3.141592653589793d) / 180.0d;
        LLSDMap lLSDMap = new LLSDMap(new LLSDMap.LLSDMapEntry("HomeLocation", new LLSDMap(new LLSDMap.LLSDMapEntry("LocationId", new LLSDInt(1)), new LLSDMap.LLSDMapEntry("LocationPos", position.toLLSD()), new LLSDMap.LLSDMapEntry("LocationLookAt", new LLVector3((float) Math.cos(agentHeading), (float) Math.sin(agentHeading), 0.0f).toLLSD()))));
        try {
            LLSDNode PerformRequest = new LLSDXMLRequest().PerformRequest(this.setHomeLocationCap, lLSDMap);
            if (PerformRequest == null) {
                return false;
            }
            Debug.Printf("SetHomeLocation: result %s", PerformRequest.serializeToXML());
            return PerformRequest.byKey("success").asBoolean();
        } catch (IOException e) {
            Debug.Warning(e);
            return false;
        } catch (LLSDException e2) {
            Debug.Warning(e2);
            return false;
        }
    }

    public void UpdateAvatarProperties(UUID uuid, UUID uuid2, String str, String str2, boolean z, boolean z2, String str3) {
        AvatarPropertiesUpdate avatarPropertiesUpdate = new AvatarPropertiesUpdate();
        avatarPropertiesUpdate.AgentData_Field.AgentID = this.circuitInfo.agentID;
        avatarPropertiesUpdate.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        avatarPropertiesUpdate.PropertiesData_Field.ImageID = uuid;
        avatarPropertiesUpdate.PropertiesData_Field.FLImageID = uuid2;
        avatarPropertiesUpdate.PropertiesData_Field.AboutText = SLMessage.stringToVariableUTF(str);
        avatarPropertiesUpdate.PropertiesData_Field.FLAboutText = SLMessage.stringToVariableOEM(str2);
        avatarPropertiesUpdate.PropertiesData_Field.AllowPublish = z;
        avatarPropertiesUpdate.PropertiesData_Field.MaturePublish = z2;
        avatarPropertiesUpdate.PropertiesData_Field.ProfileURL = SLMessage.stringToVariableOEM(str3);
        avatarPropertiesUpdate.isReliable = true;
        avatarPropertiesUpdate.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() {
            public void onMessageAcknowledged(SLMessage sLMessage) {
                super.onMessageAcknowledged(sLMessage);
                if (SLUserProfiles.this.userManager != null) {
                    SLUserProfiles.this.userManager.getAvatarProperties().requestUpdate(SLUserProfiles.this.userManager.getUserID());
                }
            }
        SendMessage(avatarPropertiesUpdate);
    }

    public void UpdatePickInfo(final UUID uuid, UUID uuid2, UUID uuid3, String str, String str2, UUID uuid4, LLVector3d lLVector3d, int i, boolean z) {
        PickInfoUpdate pickInfoUpdate = new PickInfoUpdate();
        pickInfoUpdate.AgentData_Field.AgentID = this.circuitInfo.agentID;
        pickInfoUpdate.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        pickInfoUpdate.Data_Field.PickID = uuid;
        pickInfoUpdate.Data_Field.CreatorID = uuid2;
        pickInfoUpdate.Data_Field.TopPick = false;
        pickInfoUpdate.Data_Field.ParcelID = uuid3;
        pickInfoUpdate.Data_Field.Name = SLMessage.stringToVariableOEM(str);
        pickInfoUpdate.Data_Field.Desc = SLMessage.stringToVariableUTF(str2);
        pickInfoUpdate.Data_Field.SnapshotID = uuid4;
        pickInfoUpdate.Data_Field.PosGlobal = lLVector3d;
        pickInfoUpdate.Data_Field.SortOrder = i;
        pickInfoUpdate.Data_Field.Enabled = z;
        pickInfoUpdate.isReliable = true;
        pickInfoUpdate.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() {
            public void onMessageAcknowledged(SLMessage sLMessage) {
                super.onMessageAcknowledged(sLMessage);
                if (SLUserProfiles.this.userManager != null) {
                    SLUserProfiles.this.userManager.getAvatarPickInfos().requestUpdate(new AvatarPickKey(SLUserProfiles.this.userManager.getUserID(), uuid));
                    SLUserProfiles.this.userManager.getAvatarPicks().requestUpdate(SLUserProfiles.this.userManager.getUserID());
                }
            }
        SendMessage(pickInfoUpdate);
    }

    public void requestAgentDataUpdate() {
        AgentDataUpdateRequest agentDataUpdateRequest = new AgentDataUpdateRequest();
        agentDataUpdateRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentDataUpdateRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        agentDataUpdateRequest.isReliable = true;
        SendMessage(agentDataUpdateRequest);
    }
}
