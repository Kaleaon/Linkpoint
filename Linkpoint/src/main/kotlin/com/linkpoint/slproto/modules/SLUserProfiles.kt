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
import javax.annotation.Nonnull
import javax.annotation.Nullable
import javax.annotation.concurrent.ThreadSafe

@ThreadSafe
class SLUserProfiles : SLModule() {
    const val AVATAR_AGEVERIFIED: Int = 32
    const val AVATAR_ALLOW_PUBLISH: Int = 1
    const val AVATAR_IDENTIFIED: Int = 4
    const val AVATAR_MATURE_PUBLISH: Int = 2
    const val AVATAR_ONLINE: Int = 16
    const val AVATAR_TRANSACTED: Int = 8
    private val RequestHandler<UUID> agentDataUpdateRequestHandler = AsyncLimitsRequestHandler(this.agentCircuit, SimpleRequestHandler<UUID>() {
        fun onRequest(UUID uuid) {
            SLUserProfiles.this.requestAgentDataUpdate()
        }
    }, false, 3, 15000)
    private ResultHandler<UUID, AgentDataUpdate> agentDataUpdateResultHandler
    private ResultHandler<UUID, AvatarGroupList> avatarGroupListsResultHandler
    private val RequestHandler<UUID> avatarNotesRequestHandler = AsyncLimitsRequestHandler(this.agentCircuit, SimpleRequestHandler<UUID>() {
        fun onRequest(UUID uuid) {
            SLUserProfiles.this.agentCircuit.SendGenericMessage("avatarnotesrequest", String[]{uuid.toString()})
        }
    }, false, 3, 15000)
    private ResultHandler<UUID, AvatarNotesReply> avatarNotesResultHandler
    private val RequestHandler<AvatarPickKey> avatarPickInfosRequestHandler = AsyncLimitsRequestHandler(this.agentCircuit, SimpleRequestHandler<AvatarPickKey>() {
        fun onRequest(AvatarPickKey avatarPickKey) {
            SLUserProfiles.this.agentCircuit.SendGenericMessage("pickinforequest", String[]{avatarPickKey.avatarID.toString(), avatarPickKey.pickID.toString()})
        }
    }, false, 3, 15000)
    private ResultHandler<AvatarPickKey, PickInfoReply> avatarPickInfosResultHandler
    private val RequestHandler<UUID> avatarPicksRequestHandler = AsyncLimitsRequestHandler(this.agentCircuit, SimpleRequestHandler<UUID>() {
        fun onRequest(UUID uuid) {
            SLUserProfiles.this.agentCircuit.SendGenericMessage("avatarpicksrequest", String[]{uuid.toString()})
        }
    }, false, 3, 15000)
    private ResultHandler<UUID, AvatarPicksReply> avatarPicksResultHandler
    private val RequestHandler<UUID> avatarPropertiesRequestHandler = AsyncLimitsRequestHandler(this.agentCircuit, SimpleRequestHandler<UUID>() {
        fun onRequest(UUID uuid) {
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
    private val String setHomeLocationCap
    /* access modifiers changed from: private */
    val UserManager userManager

    SLUserProfiles(SLAgentCircuit sLAgentCircuit, SLCaps sLCaps) {
        super(sLAgentCircuit)
        this.userManager = UserManager.getUserManager(sLAgentCircuit.circuitInfo.agentID)
        this.setHomeLocationCap = sLCaps.getCapability(SLCaps.SLCapability.HomeLocation)
    }

    fun DeletePick(UUID uuid) {
        PickDelete pickDelete = PickDelete()
        pickDelete.AgentData_Field.AgentID = this.circuitInfo.agentID
        pickDelete.AgentData_Field.SessionID = this.circuitInfo.sessionID
        pickDelete.Data_Field.PickID = uuid
        pickDelete.isReliable = true
        pickDelete.setEventListener(SLMessageEventListener.SLMessageBaseEventListener() {
            fun onMessageAcknowledged(SLMessage sLMessage) {
                super.onMessageAcknowledged(sLMessage)
                if (SLUserProfiles.this.userManager != null) {
                    SLUserProfiles.this.userManager.getAvatarPicks().requestUpdate(SLUserProfiles.this.userManager.getUserID())
                }
            }
        SendMessage(pickDelete)
    }

    @SLMessageHandler
    fun HandleAgentDataUpdate(AgentDataUpdate agentDataUpdate) {
        if (this.agentDataUpdateResultHandler != null) {
            this.agentDataUpdateResultHandler.onResultData(agentDataUpdate.AgentData_Field.AgentID, agentDataUpdate)
        }
    }

    @SLEventQueueMessageHandler(eventName = SLCapEventQueue.CapsEventType.AgentGroupDataUpdate)
    fun HandleAgentGroupDataUpdate(LLSDNode lLSDNode) {
        try {
            AgentGroupDataInfo agentGroupDataInfo = (AgentGroupDataInfo) lLSDNode.toObject(AgentGroupDataInfo.class)
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
    fun HandleAgentGroupDataUpdate(AgentGroupDataUpdate agentGroupDataUpdate) {
        if (this.avatarGroupListsResultHandler != null) {
            AvatarGroupList avatarGroupList = AvatarGroupList(agentGroupDataUpdate)
            this.avatarGroupListsResultHandler.onResultData(avatarGroupList.avatarID, avatarGroupList)
        }
    }

    @SLEventQueueMessageHandler(eventName = SLCapEventQueue.CapsEventType.AvatarGroupsReply)
    fun HandleAvatarGroupsReply(LLSDNode lLSDNode) {
        try {
            AgentGroupDataInfo agentGroupDataInfo = (AgentGroupDataInfo) lLSDNode.toObject(AgentGroupDataInfo.class)
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
    fun HandleAvatarGroupsReply(AvatarGroupsReply avatarGroupsReply) {
        if (!Objects.equal(avatarGroupsReply.AgentData_Field.AvatarID, this.circuitInfo.agentID) && this.avatarGroupListsResultHandler != null) {
            AvatarGroupList avatarGroupList = AvatarGroupList(avatarGroupsReply)
            this.avatarGroupListsResultHandler.onResultData(avatarGroupList.avatarID, avatarGroupList)
        }
    }

    @SLMessageHandler
    fun HandleAvatarNotesReply(AvatarNotesReply avatarNotesReply) {
        if (this.avatarNotesResultHandler != null) {
            this.avatarNotesResultHandler.onResultData(avatarNotesReply.Data_Field.TargetID, avatarNotesReply)
        }
    }

    @SLMessageHandler
    fun HandleAvatarPicksReply(AvatarPicksReply avatarPicksReply) {
        if (this.avatarPicksResultHandler != null) {
            this.avatarPicksResultHandler.onResultData(avatarPicksReply.AgentData_Field.TargetID, avatarPicksReply)
        }
    }

    @SLMessageHandler
    fun HandleAvatarPropertiesReply(AvatarPropertiesReply avatarPropertiesReply) {
        if (this.avatarPropertiesResultHandler != null) {
            this.avatarPropertiesResultHandler.onResultData(avatarPropertiesReply.AgentData_Field.AvatarID, avatarPropertiesReply)
        }
    }

    fun HandleCircuitReady() {
        if (this.userManager != null) {
            this.avatarPropertiesResultHandler = this.userManager.getAvatarProperties().getRequestSource().attachRequestHandler(this.avatarPropertiesRequestHandler)
            this.avatarNotesResultHandler = this.userManager.getAvatarNotes().getRequestSource().attachRequestHandler(this.avatarNotesRequestHandler)
            this.avatarPicksResultHandler = this.userManager.getAvatarPicks().getRequestSource().attachRequestHandler(this.avatarPicksRequestHandler)
            this.avatarPickInfosResultHandler = this.userManager.getAvatarPickInfos().getRequestSource().attachRequestHandler(this.avatarPickInfosRequestHandler)
            this.avatarGroupListsResultHandler = this.userManager.getAvatarGroupLists().getRequestSource().attachRequestHandler(this.avatarPropertiesRequestHandler)
            this.agentDataUpdateResultHandler = this.userManager.getAgentDataUpdates().getRequestSource().attachRequestHandler(this.agentDataUpdateRequestHandler)
        }
    }

    fun HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.getAvatarProperties().getRequestSource().detachRequestHandler(this.avatarPropertiesRequestHandler)
            this.userManager.getAvatarNotes().getRequestSource().detachRequestHandler(this.avatarNotesRequestHandler)
            this.userManager.getAvatarPicks().getRequestSource().detachRequestHandler(this.avatarPicksRequestHandler)
            this.userManager.getAvatarPickInfos().getRequestSource().detachRequestHandler(this.avatarPickInfosRequestHandler)
        }
    }

    @SLMessageHandler
    fun HandlePickInfoReply(PickInfoReply pickInfoReply) {
        if (this.avatarPickInfosResultHandler != null) {
            this.avatarPickInfosResultHandler.onResultData(AvatarPickKey(pickInfoReply.Data_Field.CreatorID, pickInfoReply.Data_Field.PickID), pickInfoReply)
        }
    }

    fun SaveUserNotes(UUID uuid, String str) {
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

    public Boolean SetHomeLocation() {
        if (this.setHomeLocationCap == null) {
            return false
        }
        LLVector3 position = this.agentCircuit.getModules().avatarControl.getAgentPosition().getPosition()
        Double agentHeading = (((Double) this.agentCircuit.getModules().avatarControl.getAgentHeading()) * 3.141592653589793d) / 180.0d
        LLSDMap lLSDMap = LLSDMap(LLSDMap.LLSDMapEntry("HomeLocation", LLSDMap(LLSDMap.LLSDMapEntry("LocationId", LLSDInt(1)), LLSDMap.LLSDMapEntry("LocationPos", position.toLLSD()), LLSDMap.LLSDMapEntry("LocationLookAt", LLVector3((Float) Math.cos(agentHeading), (Float) Math.sin(agentHeading), 0.0f).toLLSD()))))
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

    fun UpdateAvatarProperties(UUID uuid, UUID uuid2, String str, String str2, Boolean z, Boolean z2, String str3) {
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
            fun onMessageAcknowledged(SLMessage sLMessage) {
                super.onMessageAcknowledged(sLMessage)
                if (SLUserProfiles.this.userManager != null) {
                    SLUserProfiles.this.userManager.getAvatarProperties().requestUpdate(SLUserProfiles.this.userManager.getUserID())
                }
            }
        SendMessage(avatarPropertiesUpdate)
    }

    fun UpdatePickInfo(final UUID uuid, UUID uuid2, UUID uuid3, String str, String str2, UUID uuid4, LLVector3d lLVector3d, Int i, Boolean z) {
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
            fun onMessageAcknowledged(SLMessage sLMessage) {
                super.onMessageAcknowledged(sLMessage)
                if (SLUserProfiles.this.userManager != null) {
                    SLUserProfiles.this.userManager.getAvatarPickInfos().requestUpdate(AvatarPickKey(SLUserProfiles.this.userManager.getUserID(), uuid))
                    SLUserProfiles.this.userManager.getAvatarPicks().requestUpdate(SLUserProfiles.this.userManager.getUserID())
                }
            }
        SendMessage(pickInfoUpdate)
    }

    fun requestAgentDataUpdate() {
        AgentDataUpdateRequest agentDataUpdateRequest = AgentDataUpdateRequest()
        agentDataUpdateRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        agentDataUpdateRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        agentDataUpdateRequest.isReliable = true
        SendMessage(agentDataUpdateRequest)
    }
}
