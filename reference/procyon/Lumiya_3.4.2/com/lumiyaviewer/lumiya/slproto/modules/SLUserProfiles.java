// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.slproto.messages.AgentDataUpdateRequest;
import com.lumiyaviewer.lumiya.slproto.messages.PickInfoUpdate;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesUpdate;
import java.io.IOException;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarNotesUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarGroupsReply;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.slproto.messages.AgentGroupDataUpdate;
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue;
import com.lumiyaviewer.lumiya.slproto.handler.SLEventQueueMessageHandler;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AgentGroupDataInfo;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.SLMessageEventListener;
import com.lumiyaviewer.lumiya.slproto.messages.PickDelete;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.react.AsyncLimitsRequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesRequest;
import com.lumiyaviewer.lumiya.Debug;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPicksReply;
import com.lumiyaviewer.lumiya.slproto.messages.PickInfoReply;
import com.lumiyaviewer.lumiya.slproto.users.manager.AvatarPickKey;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarNotesReply;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.messages.AgentDataUpdate;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class SLUserProfiles extends SLModule
{
    public static final int AVATAR_AGEVERIFIED = 32;
    public static final int AVATAR_ALLOW_PUBLISH = 1;
    public static final int AVATAR_IDENTIFIED = 4;
    public static final int AVATAR_MATURE_PUBLISH = 2;
    public static final int AVATAR_ONLINE = 16;
    public static final int AVATAR_TRANSACTED = 8;
    private final RequestHandler<UUID> agentDataUpdateRequestHandler;
    private ResultHandler<UUID, AgentDataUpdate> agentDataUpdateResultHandler;
    private ResultHandler<UUID, AvatarGroupList> avatarGroupListsResultHandler;
    private final RequestHandler<UUID> avatarNotesRequestHandler;
    private ResultHandler<UUID, AvatarNotesReply> avatarNotesResultHandler;
    private final RequestHandler<AvatarPickKey> avatarPickInfosRequestHandler;
    private ResultHandler<AvatarPickKey, PickInfoReply> avatarPickInfosResultHandler;
    private final RequestHandler<UUID> avatarPicksRequestHandler;
    private ResultHandler<UUID, AvatarPicksReply> avatarPicksResultHandler;
    private final RequestHandler<UUID> avatarPropertiesRequestHandler;
    private ResultHandler<UUID, AvatarPropertiesReply> avatarPropertiesResultHandler;
    private boolean requestedNewGroupData;
    @Nullable
    private final String setHomeLocationCap;
    private final UserManager userManager;
    
    SLUserProfiles(final SLAgentCircuit slAgentCircuit, final SLCaps slCaps) {
        super(slAgentCircuit);
        this.requestedNewGroupData = false;
        this.avatarPropertiesRequestHandler = new AsyncLimitsRequestHandler<UUID>(this.agentCircuit, new SimpleRequestHandler<UUID>() {
            @Override
            public void onRequest(@Nonnull final UUID avatarID) {
                Debug.Printf("AvatarGroupList: Requesting avatar properties for %s", avatarID.toString());
                final AvatarPropertiesRequest avatarPropertiesRequest = new AvatarPropertiesRequest();
                avatarPropertiesRequest.AgentData_Field.AgentID = SLUserProfiles.this.circuitInfo.agentID;
                avatarPropertiesRequest.AgentData_Field.SessionID = SLUserProfiles.this.circuitInfo.sessionID;
                avatarPropertiesRequest.AgentData_Field.AvatarID = avatarID;
                avatarPropertiesRequest.isReliable = true;
                SLUserProfiles.this.SendMessage(avatarPropertiesRequest);
                if (avatarID.equals(SLUserProfiles.this.circuitInfo.agentID)) {
                    SLUserProfiles.this.requestAgentDataUpdate();
                }
            }
        }, false, 3, 15000L);
        this.agentDataUpdateRequestHandler = new AsyncLimitsRequestHandler<UUID>(this.agentCircuit, new SimpleRequestHandler<UUID>() {
            @Override
            public void onRequest(@Nonnull final UUID uuid) {
                SLUserProfiles.this.requestAgentDataUpdate();
            }
        }, false, 3, 15000L);
        this.avatarNotesRequestHandler = new AsyncLimitsRequestHandler<UUID>(this.agentCircuit, new SimpleRequestHandler<UUID>() {
            @Override
            public void onRequest(@Nonnull final UUID uuid) {
                SLUserProfiles.this.agentCircuit.SendGenericMessage("avatarnotesrequest", new String[] { uuid.toString() });
            }
        }, false, 3, 15000L);
        this.avatarPicksRequestHandler = new AsyncLimitsRequestHandler<UUID>(this.agentCircuit, new SimpleRequestHandler<UUID>() {
            @Override
            public void onRequest(@Nonnull final UUID uuid) {
                SLUserProfiles.this.agentCircuit.SendGenericMessage("avatarpicksrequest", new String[] { uuid.toString() });
            }
        }, false, 3, 15000L);
        this.avatarPickInfosRequestHandler = new AsyncLimitsRequestHandler<AvatarPickKey>(this.agentCircuit, new SimpleRequestHandler<AvatarPickKey>() {
            @Override
            public void onRequest(@Nonnull final AvatarPickKey avatarPickKey) {
                SLUserProfiles.this.agentCircuit.SendGenericMessage("pickinforequest", new String[] { avatarPickKey.avatarID.toString(), avatarPickKey.pickID.toString() });
            }
        }, false, 3, 15000L);
        this.userManager = UserManager.getUserManager(slAgentCircuit.circuitInfo.agentID);
        this.setHomeLocationCap = slCaps.getCapability(SLCaps.SLCapability.HomeLocation);
    }
    
    public void DeletePick(final UUID pickID) {
        final PickDelete pickDelete = new PickDelete();
        pickDelete.AgentData_Field.AgentID = this.circuitInfo.agentID;
        pickDelete.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        pickDelete.Data_Field.PickID = pickID;
        pickDelete.isReliable = true;
        pickDelete.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() {
            @Override
            public void onMessageAcknowledged(final SLMessage slMessage) {
                super.onMessageAcknowledged(slMessage);
                if (SLUserProfiles.this.userManager != null) {
                    SLUserProfiles.this.userManager.getAvatarPicks().requestUpdate((Object)SLUserProfiles.this.userManager.getUserID());
                }
            }
        });
        this.SendMessage(pickDelete);
    }
    
    @SLMessageHandler
    public void HandleAgentDataUpdate(final AgentDataUpdate agentDataUpdate) {
        if (this.agentDataUpdateResultHandler != null) {
            this.agentDataUpdateResultHandler.onResultData(agentDataUpdate.AgentData_Field.AgentID, agentDataUpdate);
        }
    }
    
    @SLEventQueueMessageHandler(eventName = SLCapEventQueue.CapsEventType.AgentGroupDataUpdate)
    public void HandleAgentGroupDataUpdate(final LLSDNode llsdNode) {
        try {
            final AgentGroupDataInfo agentGroupDataInfo = llsdNode.toObject((Class<? extends AgentGroupDataInfo>)AgentGroupDataInfo.class);
            if (this.avatarGroupListsResultHandler != null) {
                final AvatarGroupList list = new AvatarGroupList(agentGroupDataInfo);
                this.avatarGroupListsResultHandler.onResultData(list.avatarID, list);
                if (!list.newGroupDataValid && (this.requestedNewGroupData ^ true)) {
                    this.requestedNewGroupData = true;
                    this.requestAgentDataUpdate();
                }
            }
        }
        catch (final LLSDException ex) {
            Debug.Warning(ex);
        }
    }
    
    @SLMessageHandler
    public void HandleAgentGroupDataUpdate(final AgentGroupDataUpdate agentGroupDataUpdate) {
        if (this.avatarGroupListsResultHandler != null) {
            final AvatarGroupList list = new AvatarGroupList(agentGroupDataUpdate);
            this.avatarGroupListsResultHandler.onResultData(list.avatarID, list);
        }
    }
    
    @SLEventQueueMessageHandler(eventName = SLCapEventQueue.CapsEventType.AvatarGroupsReply)
    public void HandleAvatarGroupsReply(final LLSDNode llsdNode) {
        try {
            final AgentGroupDataInfo agentGroupDataInfo = llsdNode.toObject((Class<? extends AgentGroupDataInfo>)AgentGroupDataInfo.class);
            if (this.avatarGroupListsResultHandler != null) {
                final AvatarGroupList list = new AvatarGroupList(agentGroupDataInfo);
                if (!Objects.equal(list.avatarID, this.circuitInfo.agentID)) {
                    this.avatarGroupListsResultHandler.onResultData(list.avatarID, list);
                }
            }
        }
        catch (final LLSDException ex) {
            ex.printStackTrace();
        }
    }
    
    @SLMessageHandler
    public void HandleAvatarGroupsReply(final AvatarGroupsReply avatarGroupsReply) {
        if (!Objects.equal(avatarGroupsReply.AgentData_Field.AvatarID, this.circuitInfo.agentID) && this.avatarGroupListsResultHandler != null) {
            final AvatarGroupList list = new AvatarGroupList(avatarGroupsReply);
            this.avatarGroupListsResultHandler.onResultData(list.avatarID, list);
        }
    }
    
    @SLMessageHandler
    public void HandleAvatarNotesReply(final AvatarNotesReply avatarNotesReply) {
        if (this.avatarNotesResultHandler != null) {
            this.avatarNotesResultHandler.onResultData(avatarNotesReply.Data_Field.TargetID, avatarNotesReply);
        }
    }
    
    @SLMessageHandler
    public void HandleAvatarPicksReply(final AvatarPicksReply avatarPicksReply) {
        if (this.avatarPicksResultHandler != null) {
            this.avatarPicksResultHandler.onResultData(avatarPicksReply.AgentData_Field.TargetID, avatarPicksReply);
        }
    }
    
    @SLMessageHandler
    public void HandleAvatarPropertiesReply(final AvatarPropertiesReply avatarPropertiesReply) {
        if (this.avatarPropertiesResultHandler != null) {
            this.avatarPropertiesResultHandler.onResultData(avatarPropertiesReply.AgentData_Field.AvatarID, avatarPropertiesReply);
        }
    }
    
    @Override
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
    
    @Override
    public void HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.getAvatarProperties().getRequestSource().detachRequestHandler(this.avatarPropertiesRequestHandler);
            this.userManager.getAvatarNotes().getRequestSource().detachRequestHandler(this.avatarNotesRequestHandler);
            this.userManager.getAvatarPicks().getRequestSource().detachRequestHandler(this.avatarPicksRequestHandler);
            this.userManager.getAvatarPickInfos().getRequestSource().detachRequestHandler(this.avatarPickInfosRequestHandler);
        }
    }
    
    @SLMessageHandler
    public void HandlePickInfoReply(final PickInfoReply pickInfoReply) {
        if (this.avatarPickInfosResultHandler != null) {
            this.avatarPickInfosResultHandler.onResultData(new AvatarPickKey(pickInfoReply.Data_Field.CreatorID, pickInfoReply.Data_Field.PickID), pickInfoReply);
        }
    }
    
    public void SaveUserNotes(final UUID uuid, final String s) {
        final AvatarNotesUpdate avatarNotesUpdate = new AvatarNotesUpdate();
        avatarNotesUpdate.AgentData_Field.AgentID = this.circuitInfo.agentID;
        avatarNotesUpdate.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        avatarNotesUpdate.Data_Field.TargetID = uuid;
        avatarNotesUpdate.Data_Field.Notes = SLMessage.stringToVariableUTF(s);
        avatarNotesUpdate.isReliable = true;
        this.SendMessage(avatarNotesUpdate);
        if (this.avatarNotesResultHandler != null) {
            final AvatarNotesReply avatarNotesReply = new AvatarNotesReply();
            avatarNotesReply.AgentData_Field.AgentID = this.circuitInfo.agentID;
            avatarNotesReply.Data_Field.Notes = SLMessage.stringToVariableUTF(s);
            avatarNotesReply.Data_Field.TargetID = uuid;
            this.avatarNotesResultHandler.onResultData(uuid, avatarNotesReply);
        }
    }
    
    public boolean SetHomeLocation() {
        if (this.setHomeLocationCap == null) {
            return false;
        }
        final LLVector3 position = this.agentCircuit.getModules().avatarControl.getAgentPosition().getPosition();
        final double n = this.agentCircuit.getModules().avatarControl.getAgentHeading() * 3.141592653589793 / 180.0;
        final LLSDMap llsdMap = new LLSDMap(new LLSDMap.LLSDMapEntry[] { new LLSDMap.LLSDMapEntry("HomeLocation", new LLSDMap(new LLSDMap.LLSDMapEntry[] { new LLSDMap.LLSDMapEntry("LocationId", new LLSDInt(1)), new LLSDMap.LLSDMapEntry("LocationPos", position.toLLSD()), new LLSDMap.LLSDMapEntry("LocationLookAt", new LLVector3((float)Math.cos(n), (float)Math.sin(n), 0.0f).toLLSD()) })) });
        final LLSDXMLRequest llsdxmlRequest = new LLSDXMLRequest();
        try {
            final LLSDNode performRequest = llsdxmlRequest.PerformRequest(this.setHomeLocationCap, llsdMap);
            if (performRequest != null) {
                Debug.Printf("SetHomeLocation: result %s", performRequest.serializeToXML());
                return performRequest.byKey("success").asBoolean();
            }
            return false;
        }
        catch (final LLSDException ex) {
            Debug.Warning(ex);
            return false;
        }
        catch (final IOException ex2) {
            Debug.Warning(ex2);
            return false;
        }
    }
    
    public void UpdateAvatarProperties(final UUID imageID, final UUID flImageID, final String s, final String s2, final boolean allowPublish, final boolean maturePublish, final String s3) {
        final AvatarPropertiesUpdate avatarPropertiesUpdate = new AvatarPropertiesUpdate();
        avatarPropertiesUpdate.AgentData_Field.AgentID = this.circuitInfo.agentID;
        avatarPropertiesUpdate.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        avatarPropertiesUpdate.PropertiesData_Field.ImageID = imageID;
        avatarPropertiesUpdate.PropertiesData_Field.FLImageID = flImageID;
        avatarPropertiesUpdate.PropertiesData_Field.AboutText = SLMessage.stringToVariableUTF(s);
        avatarPropertiesUpdate.PropertiesData_Field.FLAboutText = SLMessage.stringToVariableOEM(s2);
        avatarPropertiesUpdate.PropertiesData_Field.AllowPublish = allowPublish;
        avatarPropertiesUpdate.PropertiesData_Field.MaturePublish = maturePublish;
        avatarPropertiesUpdate.PropertiesData_Field.ProfileURL = SLMessage.stringToVariableOEM(s3);
        avatarPropertiesUpdate.isReliable = true;
        avatarPropertiesUpdate.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() {
            @Override
            public void onMessageAcknowledged(final SLMessage slMessage) {
                super.onMessageAcknowledged(slMessage);
                if (SLUserProfiles.this.userManager != null) {
                    SLUserProfiles.this.userManager.getAvatarProperties().requestUpdate((Object)SLUserProfiles.this.userManager.getUserID());
                }
            }
        });
        this.SendMessage(avatarPropertiesUpdate);
    }
    
    public void UpdatePickInfo(final UUID pickID, final UUID creatorID, final UUID parcelID, final String s, final String s2, final UUID snapshotID, final LLVector3d posGlobal, final int sortOrder, final boolean enabled) {
        final PickInfoUpdate pickInfoUpdate = new PickInfoUpdate();
        pickInfoUpdate.AgentData_Field.AgentID = this.circuitInfo.agentID;
        pickInfoUpdate.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        pickInfoUpdate.Data_Field.PickID = pickID;
        pickInfoUpdate.Data_Field.CreatorID = creatorID;
        pickInfoUpdate.Data_Field.TopPick = false;
        pickInfoUpdate.Data_Field.ParcelID = parcelID;
        pickInfoUpdate.Data_Field.Name = SLMessage.stringToVariableOEM(s);
        pickInfoUpdate.Data_Field.Desc = SLMessage.stringToVariableUTF(s2);
        pickInfoUpdate.Data_Field.SnapshotID = snapshotID;
        pickInfoUpdate.Data_Field.PosGlobal = posGlobal;
        pickInfoUpdate.Data_Field.SortOrder = sortOrder;
        pickInfoUpdate.Data_Field.Enabled = enabled;
        pickInfoUpdate.isReliable = true;
        pickInfoUpdate.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() {
            @Override
            public void onMessageAcknowledged(final SLMessage slMessage) {
                super.onMessageAcknowledged(slMessage);
                if (SLUserProfiles.this.userManager != null) {
                    SLUserProfiles.this.userManager.getAvatarPickInfos().requestUpdate((Object)new AvatarPickKey(SLUserProfiles.this.userManager.getUserID(), pickID));
                    SLUserProfiles.this.userManager.getAvatarPicks().requestUpdate((Object)SLUserProfiles.this.userManager.getUserID());
                }
            }
        });
        this.SendMessage(pickInfoUpdate);
    }
    
    public void requestAgentDataUpdate() {
        final AgentDataUpdateRequest agentDataUpdateRequest = new AgentDataUpdateRequest();
        agentDataUpdateRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentDataUpdateRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        agentDataUpdateRequest.isReliable = true;
        this.SendMessage(agentDataUpdateRequest);
    }
}
