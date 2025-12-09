package com.linkpoint.slproto.modules

import com.google.common.base.Objects
import com.linkpoint.Debug
import com.linkpoint.react.AsyncLimitsRequestHandler
import com.linkpoint.react.RequestHandler
import com.linkpoint.react.ResultHandler
import com.linkpoint.react.SimpleRequestHandler
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.SLMessageEventListener
import com.linkpoint.slproto.caps.SLCapEventQueue
import com.linkpoint.slproto.caps.SLCaps
import com.linkpoint.slproto.handler.SLEventQueueMessageHandler
import com.linkpoint.slproto.handler.SLMessageHandler
import com.linkpoint.slproto.https.LLSDXMLRequest
import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.llsd.types.LLSDInt
import com.linkpoint.slproto.llsd.types.LLSDMap
import com.linkpoint.slproto.messages.AgentDataUpdate
import com.linkpoint.slproto.messages.AgentDataUpdateRequest
import com.linkpoint.slproto.messages.AgentGroupDataUpdate
import com.linkpoint.slproto.messages.AvatarGroupsReply
import com.linkpoint.slproto.messages.AvatarNotesReply
import com.linkpoint.slproto.messages.AvatarNotesUpdate
import com.linkpoint.slproto.messages.AvatarPicksReply
import com.linkpoint.slproto.messages.AvatarPropertiesReply
import com.linkpoint.slproto.messages.AvatarPropertiesRequest
import com.linkpoint.slproto.messages.AvatarPropertiesUpdate
import com.linkpoint.slproto.messages.PickDelete
import com.linkpoint.slproto.messages.PickInfoReply
import com.linkpoint.slproto.messages.PickInfoUpdate
import com.linkpoint.slproto.modules.groups.AgentGroupDataInfo
import com.linkpoint.slproto.modules.groups.AvatarGroupList
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.types.LLVector3d
import com.linkpoint.slproto.users.manager.AvatarPickKey
import com.linkpoint.slproto.users.manager.UserManager
import java.io.IOException
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.ThreadSafe

@ThreadSafe
class SLUserProfiles : SLModule {
    Int AVATAR_AGEVERIFIED = 32
    Int AVATAR_ALLOW_PUBLISH = 1
    Int AVATAR_IDENTIFIED = 4
    Int AVATAR_MATURE_PUBLISH = 2
    Int AVATAR_ONLINE = 16
    Int AVATAR_TRANSACTED = 8
    private RequestHandler<UUID> agentDataUpdateRequestHandler = AsyncLimitsRequestHandler(this.agentCircuit, SimpleRequestHandler<UUID>() {
        fun onRequest(@NonNull UUID uuid): Unit {
            SLUserProfiles.this.requestAgentDataUpdate()
        }
    }, false, 3, 15000)
    private ResultHandler<UUID, AgentDataUpdate> agentDataUpdateResultHandler
    private ResultHandler<UUID, AvatarGroupList> avatarGroupListsResultHandler
    private RequestHandler<UUID> avatarNotesRequestHandler = AsyncLimitsRequestHandler(this.agentCircuit, SimpleRequestHandler<UUID>() {
        fun onRequest(@NonNull UUID uuid): Unit {
            SLUserProfiles.this.agentCircuit.SendGenericMessage("avatarnotesrequest", String[]{uuid.toString()})
        }
    }, false, 3, 15000)
    private ResultHandler<UUID, AvatarNotesReply> avatarNotesResultHandler
    private RequestHandler<AvatarPickKey> avatarPickInfosRequestHandler = AsyncLimitsRequestHandler(this.agentCircuit, SimpleRequestHandler<AvatarPickKey>() {
        fun onRequest(@NonNull AvatarPickKey avatarPickKey): Unit {
            SLUserProfiles.this.agentCircuit.SendGenericMessage("pickinforequest", String[]{avatarPickKey.avatarID.toString(), avatarPickKey.pickID.toString()})
        }
    }, false, 3, 15000)
    private ResultHandler<AvatarPickKey, PickInfoReply> avatarPickInfosResultHandler
    private RequestHandler<UUID> avatarPicksRequestHandler = AsyncLimitsRequestHandler(this.agentCircuit, SimpleRequestHandler<UUID>() {
        fun onRequest(@NonNull UUID uuid): Unit {
            SLUserProfiles.this.agentCircuit.SendGenericMessage("avatarpicksrequest", String[]{uuid.toString()})
        }
    }, false, 3, 15000)
    private ResultHandler<UUID, AvatarPicksReply> avatarPicksResultHandler
    private RequestHandler<UUID> avatarPropertiesRequestHandler = AsyncLimitsRequestHandler(this.agentCircuit, SimpleRequestHandler<UUID>() {
        fun onRequest(@NonNull UUID uuid): Unit {
            Debug.Printf("AvatarGroupList: Requesting avatar properties for %s", uuid.toString())
            AvatarPropertiesRequest avatarPropertiesRequest = AvatarPropertiesRequest()
            avatarPropertiesRequest.AgentData_Field.AgentID = SLUserProfiles.this.circuitInfo.agentID
            avatarPropertiesRequest.AgentData_Field.SessionID = SLUserProfiles.this.circuitInfo.sessionID
            avatarPropertiesRequest.AgentData_Field.AvatarID = uuid
            avatarPropertiesRequest.isReliable = true
            SLUserProfiles.this.SendMessage(avatarPropertiesRequest)
            if (uuid.equals(SLUserProfiles.this.circuitInfo.agentID)) {
                SLUserProfiles.this.requestAgentDataUpdate()
            }
        }
    }, false, 3, 15000)
    private ResultHandler<UUID, AvatarPropertiesReply> avatarPropertiesResultHandler
    private Boolean requestedNewGroupData = false
    @Nullable
    private String setHomeLocationCap
    /* access modifiers changed from: private */
    UserManager userManager

    SLUserProfiles(SLAgentCircuit sLAgentCircuit, SLCaps sLCaps) {
        super(sLAgentCircuit)
        this.userManager = UserManager.getUserManager(sLAgentCircuit.circuitInfo.agentID)
        this.setHomeLocationCap = sLCaps.getCapability(SLCaps.SLCapability.HomeLocation)
    }

    fun DeletePick(UUID uuid): Unit {
        PickDelete pickDelete = PickDelete()
        pickDelete.AgentData_Field.AgentID = this.circuitInfo.agentID
        pickDelete.AgentData_Field.SessionID = this.circuitInfo.sessionID
        pickDelete.Data_Field.PickID = uuid
        pickDelete.isReliable = true
        pickDelete.setEventListener(SLMessageEventListener.SLMessageBaseEventListener() {
            fun onMessageAcknowledged(SLMessage sLMessage): Unit {
                super.onMessageAcknowledged(sLMessage)
                if (SLUserProfiles.this.userManager != null) {
                    SLUserProfiles.this.userManager.getAvatarPicks().requestUpdate(SLUserProfiles.this.userManager.getUserID())
                }
            }
        SendMessage(pickDelete)
    }

    @SLMessageHandler
    fun HandleAgentDataUpdate(AgentDataUpdate agentDataUpdate): Unit {
        if (this.agentDataUpdateResultHandler != null) {
            this.agentDataUpdateResultHandler.onResultData(agentDataUpdate.AgentData_Field.AgentID, agentDataUpdate)
        }
    }

    @SLEventQueueMessageHandler(eventName = SLCapEventQueue.CapsEventType.AgentGroupDataUpdate)
    fun HandleAgentGroupDataUpdate(LLSDNode lLSDNode): Unit {
        try {
            AgentGroupDataInfo agentGroupDataInfo = (lLSDNode as AgentGroupDataInfo).toObject(AgentGroupDataInfo.class)
            if (this.avatarGroupListsResultHandler != null) {
                AvatarGroupList avatarGroupList = AvatarGroupList(agentGroupDataInfo)
                this.avatarGroupListsResultHandler.onResultData(avatarGroupList.avatarID, avatarGroupList)
                if (!avatarGroupList.newGroupDataValid && (!this.requestedNewGroupData)) {
                    this.requestedNewGroupData = true
                    requestAgentDataUpdate()
                }
            }
        } catch (LLSDException e) {
            Debug.Warning(e)
        }
    }

    @SLMessageHandler
    fun HandleAgentGroupDataUpdate(AgentGroupDataUpdate agentGroupDataUpdate): Unit {
        if (this.avatarGroupListsResultHandler != null) {
            AvatarGroupList avatarGroupList = AvatarGroupList(agentGroupDataUpdate)
            this.avatarGroupListsResultHandler.onResultData(avatarGroupList.avatarID, avatarGroupList)
        }
    }

    @SLEventQueueMessageHandler(eventName = SLCapEventQueue.CapsEventType.AvatarGroupsReply)
    fun HandleAvatarGroupsReply(LLSDNode lLSDNode): Unit {
        try {
            AgentGroupDataInfo agentGroupDataInfo = (lLSDNode as AgentGroupDataInfo).toObject(AgentGroupDataInfo.class)
            if (this.avatarGroupListsResultHandler != null) {
                AvatarGroupList avatarGroupList = AvatarGroupList(agentGroupDataInfo)
                if (!Objects.equal(avatarGroupList.avatarID, this.circuitInfo.agentID)) {
                    this.avatarGroupListsResultHandler.onResultData(avatarGroupList.avatarID, avatarGroupList)
                }
            }
        } catch (LLSDException e) {
            e.printStackTrace()
        }
    }

    @SLMessageHandler
    fun HandleAvatarGroupsReply(AvatarGroupsReply avatarGroupsReply): Unit {
        if (!Objects.equal(avatarGroupsReply.AgentData_Field.AvatarID, this.circuitInfo.agentID) && this.avatarGroupListsResultHandler != null) {
            AvatarGroupList avatarGroupList = AvatarGroupList(avatarGroupsReply)
            this.avatarGroupListsResultHandler.onResultData(avatarGroupList.avatarID, avatarGroupList)
        }
    }

    @SLMessageHandler
    fun HandleAvatarNotesReply(AvatarNotesReply avatarNotesReply): Unit {
        if (this.avatarNotesResultHandler != null) {
            this.avatarNotesResultHandler.onResultData(avatarNotesReply.Data_Field.TargetID, avatarNotesReply)
        }
    }

    @SLMessageHandler
    fun HandleAvatarPicksReply(AvatarPicksReply avatarPicksReply): Unit {
        if (this.avatarPicksResultHandler != null) {
            this.avatarPicksResultHandler.onResultData(avatarPicksReply.AgentData_Field.TargetID, avatarPicksReply)
        }
    }

    @SLMessageHandler
    fun HandleAvatarPropertiesReply(AvatarPropertiesReply avatarPropertiesReply): Unit {
        if (this.avatarPropertiesResultHandler != null) {
            this.avatarPropertiesResultHandler.onResultData(avatarPropertiesReply.AgentData_Field.AvatarID, avatarPropertiesReply)
        }
    }

    fun HandleCircuitReady(): Unit {
        if (this.userManager != null) {
            this.avatarPropertiesResultHandler = this.userManager.getAvatarProperties().getRequestSource().attachRequestHandler(this.avatarPropertiesRequestHandler)
            this.avatarNotesResultHandler = this.userManager.getAvatarNotes().getRequestSource().attachRequestHandler(this.avatarNotesRequestHandler)
            this.avatarPicksResultHandler = this.userManager.getAvatarPicks().getRequestSource().attachRequestHandler(this.avatarPicksRequestHandler)
            this.avatarPickInfosResultHandler = this.userManager.getAvatarPickInfos().getRequestSource().attachRequestHandler(this.avatarPickInfosRequestHandler)
            this.avatarGroupListsResultHandler = this.userManager.getAvatarGroupLists().getRequestSource().attachRequestHandler(this.avatarPropertiesRequestHandler)
            this.agentDataUpdateResultHandler = this.userManager.getAgentDataUpdates().getRequestSource().attachRequestHandler(this.agentDataUpdateRequestHandler)
        }
    }

    fun HandleCloseCircuit(): Unit {
        if (this.userManager != null) {
            this.userManager.getAvatarProperties().getRequestSource().detachRequestHandler(this.avatarPropertiesRequestHandler)
            this.userManager.getAvatarNotes().getRequestSource().detachRequestHandler(this.avatarNotesRequestHandler)
            this.userManager.getAvatarPicks().getRequestSource().detachRequestHandler(this.avatarPicksRequestHandler)
            this.userManager.getAvatarPickInfos().getRequestSource().detachRequestHandler(this.avatarPickInfosRequestHandler)
        }
    }

    @SLMessageHandler
    fun HandlePickInfoReply(PickInfoReply pickInfoReply): Unit {
        if (this.avatarPickInfosResultHandler != null) {
            this.avatarPickInfosResultHandler.onResultData(AvatarPickKey(pickInfoReply.Data_Field.CreatorID, pickInfoReply.Data_Field.PickID), pickInfoReply)
        }
    }

    fun SaveUserNotes(UUID uuid, String str): Unit {
        AvatarNotesUpdate avatarNotesUpdate = AvatarNotesUpdate()
        avatarNotesUpdate.AgentData_Field.AgentID = this.circuitInfo.agentID
        avatarNotesUpdate.AgentData_Field.SessionID = this.circuitInfo.sessionID
        avatarNotesUpdate.Data_Field.TargetID = uuid
        avatarNotesUpdate.Data_Field.Notes = SLMessage.stringToVariableUTF(str)
        avatarNotesUpdate.isReliable = true
        SendMessage(avatarNotesUpdate)
        if (this.avatarNotesResultHandler != null) {
            AvatarNotesReply avatarNotesReply = AvatarNotesReply()
            avatarNotesReply.AgentData_Field.AgentID = this.circuitInfo.agentID
            avatarNotesReply.Data_Field.Notes = SLMessage.stringToVariableUTF(str)
            avatarNotesReply.Data_Field.TargetID = uuid
            this.avatarNotesResultHandler.onResultData(uuid, avatarNotesReply)
        }
    }

    fun SetHomeLocation(): Boolean {
        if (this.setHomeLocationCap == null) {
            return false
        }
        LLVector3 position = this.agentCircuit.getModules().avatarControl.getAgentPosition().getPosition()
        Double agentHeading = (((this as Double).agentCircuit.getModules().avatarControl.getAgentHeading()) * 3.141592653589793d) / 180.0d
        LLSDMap lLSDMap = LLSDMap(LLSDMap.LLSDMapEntry("HomeLocation", LLSDMap(LLSDMap.LLSDMapEntry("LocationId", LLSDInt(1)), LLSDMap.LLSDMapEntry("LocationPos", position.toLLSD()), LLSDMap.LLSDMapEntry("LocationLookAt", LLVector3((Math as Float).cos(agentHeading), (Math as Float).sin(agentHeading), 0.0f).toLLSD()))))
        try {
            LLSDNode PerformRequest = LLSDXMLRequest().PerformRequest(this.setHomeLocationCap, lLSDMap)
            if (PerformRequest == null) {
                return false
            }
            Debug.Printf("SetHomeLocation: result %s", PerformRequest.serializeToXML())
            return PerformRequest.byKey("success").asBoolean()
        } catch (IOException e) {
            Debug.Warning(e)
            return false
        } catch (LLSDException e2) {
            Debug.Warning(e2)
            return false
        }
    }

    fun UpdateAvatarProperties(UUID uuid, UUID uuid2, String str, String str2, Boolean z, Boolean z2, String str3): Unit {
        AvatarPropertiesUpdate avatarPropertiesUpdate = AvatarPropertiesUpdate()
        avatarPropertiesUpdate.AgentData_Field.AgentID = this.circuitInfo.agentID
        avatarPropertiesUpdate.AgentData_Field.SessionID = this.circuitInfo.sessionID
        avatarPropertiesUpdate.PropertiesData_Field.ImageID = uuid
        avatarPropertiesUpdate.PropertiesData_Field.FLImageID = uuid2
        avatarPropertiesUpdate.PropertiesData_Field.AboutText = SLMessage.stringToVariableUTF(str)
        avatarPropertiesUpdate.PropertiesData_Field.FLAboutText = SLMessage.stringToVariableOEM(str2)
        avatarPropertiesUpdate.PropertiesData_Field.AllowPublish = z
        avatarPropertiesUpdate.PropertiesData_Field.MaturePublish = z2
        avatarPropertiesUpdate.PropertiesData_Field.ProfileURL = SLMessage.stringToVariableOEM(str3)
        avatarPropertiesUpdate.isReliable = true
        avatarPropertiesUpdate.setEventListener(SLMessageEventListener.SLMessageBaseEventListener() {
            fun onMessageAcknowledged(SLMessage sLMessage): Unit {
                super.onMessageAcknowledged(sLMessage)
                if (SLUserProfiles.this.userManager != null) {
                    SLUserProfiles.this.userManager.getAvatarProperties().requestUpdate(SLUserProfiles.this.userManager.getUserID())
                }
            }
        SendMessage(avatarPropertiesUpdate)
    }

    fun UpdatePickInfo(UUID uuid, UUID uuid2, UUID uuid3, String str, String str2, UUID uuid4, LLVector3d lLVector3d, Int i, Boolean z): Unit {
        PickInfoUpdate pickInfoUpdate = PickInfoUpdate()
        pickInfoUpdate.AgentData_Field.AgentID = this.circuitInfo.agentID
        pickInfoUpdate.AgentData_Field.SessionID = this.circuitInfo.sessionID
        pickInfoUpdate.Data_Field.PickID = uuid
        pickInfoUpdate.Data_Field.CreatorID = uuid2
        pickInfoUpdate.Data_Field.TopPick = false
        pickInfoUpdate.Data_Field.ParcelID = uuid3
        pickInfoUpdate.Data_Field.Name = SLMessage.stringToVariableOEM(str)
        pickInfoUpdate.Data_Field.Desc = SLMessage.stringToVariableUTF(str2)
        pickInfoUpdate.Data_Field.SnapshotID = uuid4
        pickInfoUpdate.Data_Field.PosGlobal = lLVector3d
        pickInfoUpdate.Data_Field.SortOrder = i
        pickInfoUpdate.Data_Field.Enabled = z
        pickInfoUpdate.isReliable = true
        pickInfoUpdate.setEventListener(SLMessageEventListener.SLMessageBaseEventListener() {
            fun onMessageAcknowledged(SLMessage sLMessage): Unit {
                super.onMessageAcknowledged(sLMessage)
                if (SLUserProfiles.this.userManager != null) {
                    SLUserProfiles.this.userManager.getAvatarPickInfos().requestUpdate(AvatarPickKey(SLUserProfiles.this.userManager.getUserID(), uuid))
                    SLUserProfiles.this.userManager.getAvatarPicks().requestUpdate(SLUserProfiles.this.userManager.getUserID())
                }
            }
        SendMessage(pickInfoUpdate)
    }

    fun requestAgentDataUpdate(): Unit {
        AgentDataUpdateRequest agentDataUpdateRequest = AgentDataUpdateRequest()
        agentDataUpdateRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        agentDataUpdateRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        agentDataUpdateRequest.isReliable = true
        SendMessage(agentDataUpdateRequest)
    }
}
