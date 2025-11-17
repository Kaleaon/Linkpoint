package com.linkpoint.slproto.modules.groups

import com.google.common.base.Strings
import com.google.common.primitives.UnsignedLong
import com.linkpoint.Debug
import com.linkpoint.dao.GroupMember
import com.linkpoint.dao.GroupMemberDao
import com.linkpoint.dao.GroupRoleMember
import com.linkpoint.dao.GroupRoleMemberDao
import com.linkpoint.react.AsyncCancellableRequestHandler
import com.linkpoint.react.AsyncLimitsRequestHandler
import com.linkpoint.react.RequestHandler
import com.linkpoint.react.ResultHandler
import com.linkpoint.react.SimpleRequestHandler
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.SLMessageEventListener
import com.linkpoint.slproto.caps.SLCaps
import com.linkpoint.slproto.chat.SLChatGroupInvitationSentEvent
import com.linkpoint.slproto.events.SLJoinLeaveGroupEvent
import com.linkpoint.slproto.handler.SLMessageHandler
import com.linkpoint.slproto.https.GenericHTTPExecutor
import com.linkpoint.slproto.https.LLSDXMLRequest
import com.linkpoint.slproto.inventory.SLInventoryEntry
import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.llsd.types.LLSDMap
import com.linkpoint.slproto.llsd.types.LLSDUUID
import com.linkpoint.slproto.messages.ActivateGroup
import com.linkpoint.slproto.messages.AgentDataUpdateRequest
import com.linkpoint.slproto.messages.EjectGroupMemberReply
import com.linkpoint.slproto.messages.EjectGroupMemberRequest
import com.linkpoint.slproto.messages.GroupMembersReply
import com.linkpoint.slproto.messages.GroupMembersRequest
import com.linkpoint.slproto.messages.GroupProfileReply
import com.linkpoint.slproto.messages.GroupProfileRequest
import com.linkpoint.slproto.messages.GroupRoleChanges
import com.linkpoint.slproto.messages.GroupRoleDataReply
import com.linkpoint.slproto.messages.GroupRoleDataRequest
import com.linkpoint.slproto.messages.GroupRoleMembersReply
import com.linkpoint.slproto.messages.GroupRoleMembersRequest
import com.linkpoint.slproto.messages.GroupRoleUpdate
import com.linkpoint.slproto.messages.GroupTitleUpdate
import com.linkpoint.slproto.messages.GroupTitlesReply
import com.linkpoint.slproto.messages.GroupTitlesRequest
import com.linkpoint.slproto.messages.ImprovedInstantMessage
import com.linkpoint.slproto.messages.InviteGroupRequest
import com.linkpoint.slproto.messages.JoinGroupReply
import com.linkpoint.slproto.messages.JoinGroupRequest
import com.linkpoint.slproto.messages.LeaveGroupReply
import com.linkpoint.slproto.messages.LeaveGroupRequest
import com.linkpoint.slproto.messages.SetGroupAcceptNotices
import com.linkpoint.slproto.messages.SetGroupContribution
import com.linkpoint.slproto.modules.SLModule
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.chatsrc.ChatMessageSourceUser
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.utils.UUIDPool
import java.io.IOException
import java.util.Collection
import java.util.Map
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class SLGroupManager : SLModule {
    private Int RoleMemberChange_Add = 0
    private Int RoleMemberChange_Remove = 1
    private Int Role_Update_All = 3
    private Int Role_Update_Create = 4
    private Int Role_Update_Delete = 5
    private volatile UUID activeGroupID = null
    /* access modifiers changed from: private */
    GroupMemberDao groupMemberDao
    /* access modifiers changed from: private */
    String groupMemberDataURL
    private RequestHandler<UUID> groupMemberListHTTPRequestHandler = AsyncCancellableRequestHandler(GenericHTTPExecutor.getInstance(), SimpleRequestHandler<UUID>() {
        Unit onRequest(@NonNull UUID uuid) {
            Debug.Printf("GroupMemberList: [%s] network requesting for group %s", Thread.currentThread().getName(), uuid.toString())
            try {
                UUID randomUUID = UUID.randomUUID()
                LLSDNode PerformRequest = LLSDXMLRequest().PerformRequest(SLGroupManager.this.groupMemberDataURL, LLSDMap(LLSDMap.LLSDMapEntry("group_id", LLSDUUID(uuid))))
                if (PerformRequest != null) {
                    LLSDNode byKey = PerformRequest.byKey("titles")
                    LLSDNode byKey2 = PerformRequest.byKey("defaults")
                    LLSDNode byKey3 = PerformRequest.byKey("members")
                    Long j = 0
                    if (byKey2.keyExists("default_powers")) {
                        try {
                            String asString = byKey2.byKey("default_powers").asString()
                            j = asString.startsWith("0x") ? Long.decode(asString).longValue() : UnsignedLong.valueOf(asString, 16).longValue()
                        } catch (NumberFormatException e) {
                            Debug.Warning(e)
                        }
                    }
                    if (byKey3 instanceof LLSDMap) {
                        Int i2 = 0
                        for (Map.Entry entry : ((LLSDMap) byKey3).entrySet()) {
                            UUID uuid2 = UUIDPool.getUUID((String) entry.getKey())
                            LLSDNode lLSDNode = (LLSDNode) entry.getValue()
                            Boolean z = false
                            String asString2 = lLSDNode.keyExists("title") ? byKey.byIndex(lLSDNode.byKey("title").asInt()).asString() : byKey.byIndex(0).asString()
                            Long longValue = lLSDNode.keyExists("powers") ? UnsignedLong.valueOf(lLSDNode.byKey("powers").asString(), 16).longValue() : j
                            String asString3 = lLSDNode.keyExists("last_login") ? lLSDNode.byKey("last_login").asString() : "Unknown"
                            Int asInt = lLSDNode.keyExists("donated_square_meters") ? lLSDNode.byKey("donated_square_meters").asInt() : 0
                            if (lLSDNode.keyExists("owner")) {
                                if (lLSDNode.byKey("owner").isString()) {
                                    String asString4 = lLSDNode.byKey("owner").asString()
                                    if (asString4.equalsIgnoreCase("y") || asString4.equalsIgnoreCase("yes") || asString4.equalsIgnoreCase("true") || asString4.equalsIgnoreCase("1")) {
                                        z = true
                                    }
                                } else if (lLSDNode.byKey("owner").isBoolean()) {
                                    z = lLSDNode.byKey("owner").asBoolean()
                                }
                            }
                            i2++
                            if (SLGroupManager.this.groupMemberDao != null) {
                                SLGroupManager.this.groupMemberDao.insert(GroupMember(uuid, randomUUID, uuid2, asInt, asString3, longValue, asString2, z))
                            }
                        }
                        i = i2
                    } else {
                        i = 0
                    }
                    Debug.Printf("GroupMemberList: parsed list for group: %s requestID %s memberCount %d", uuid, randomUUID, Int.valueOf(i))
                    SLGroupManager.this.groupMemberListResultHandler.onResultData(uuid, randomUUID)
                    return
                }
                SLGroupManager.this.groupMemberListResultHandler.onResultError(uuid, LLSDException("No data"))
            } catch (Exception e2) {
                Debug.Warning(e2)
                SLGroupManager.this.groupMemberListResultHandler.onResultError(uuid, e2)
            }
        }
    private RequestHandler<UUID> groupMemberListRequestHandler = AsyncLimitsRequestHandler(this.agentCircuit, SimpleRequestHandler<UUID>() {
        Unit onRequest(@NonNull UUID uuid) {
            Debug.Printf("GroupMemberList: [%s] network requesting for group %s", Thread.currentThread().getName(), uuid.toString())
            GroupMembersRequest groupMembersRequest = GroupMembersRequest()
            groupMembersRequest.AgentData_Field.AgentID = SLGroupManager.this.circuitInfo.agentID
            groupMembersRequest.AgentData_Field.SessionID = SLGroupManager.this.circuitInfo.sessionID
            groupMembersRequest.GroupData_Field.GroupID = uuid
            groupMembersRequest.GroupData_Field.RequestID = UUID.randomUUID()
            groupMembersRequest.isReliable = true
            SLGroupManager.this.SendMessage(groupMembersRequest)
        }
    }, false, 3, 15000)
    /* access modifiers changed from: private */
    ResultHandler<UUID, UUID> groupMemberListResultHandler
    private RequestHandler<UUID> groupProfileRequestHandler = AsyncLimitsRequestHandler(this.agentCircuit, SimpleRequestHandler<UUID>() {
        Unit onRequest(@NonNull UUID uuid) {
            Debug.Printf("GroupManager: [%s] network requesting for group %s", Thread.currentThread().getName(), uuid.toString())
            SLGroupManager.this.RequestGroupProfileData(uuid)
        }
    }, false, 3, 15000)
    private ResultHandler<UUID, GroupProfileReply> groupProfileResultHandler
    private GroupRoleMemberDao groupRoleMemberDao
    private RequestHandler<UUID> groupRoleMemberListRequestHandler = AsyncLimitsRequestHandler(this.agentCircuit, SimpleRequestHandler<UUID>() {
        Unit onRequest(@NonNull UUID uuid) {
            Debug.Printf("GroupRoleMemberList: [%s] network requesting for %s", Thread.currentThread().getName(), uuid.toString())
            GroupRoleMembersRequest groupRoleMembersRequest = GroupRoleMembersRequest()
            groupRoleMembersRequest.AgentData_Field.AgentID = SLGroupManager.this.circuitInfo.agentID
            groupRoleMembersRequest.AgentData_Field.SessionID = SLGroupManager.this.circuitInfo.sessionID
            groupRoleMembersRequest.GroupData_Field.GroupID = uuid
            groupRoleMembersRequest.GroupData_Field.RequestID = UUID.randomUUID()
            groupRoleMembersRequest.isReliable = true
            SLGroupManager.this.SendMessage(groupRoleMembersRequest)
        }
    }, false, 3, 15000)
    private ResultHandler<UUID, UUID> groupRoleMemberListResultHandler
    private RequestHandler<UUID> groupRolesRequestHandler = AsyncLimitsRequestHandler(this.agentCircuit, SimpleRequestHandler<UUID>() {
        Unit onRequest(@NonNull UUID uuid) {
            Debug.Printf("GroupRoles: [%s] network requesting for group %s", Thread.currentThread().getName(), uuid.toString())
            GroupRoleDataRequest groupRoleDataRequest = GroupRoleDataRequest()
            groupRoleDataRequest.AgentData_Field.AgentID = SLGroupManager.this.circuitInfo.agentID
            groupRoleDataRequest.AgentData_Field.SessionID = SLGroupManager.this.circuitInfo.sessionID
            groupRoleDataRequest.GroupData_Field.GroupID = uuid
            groupRoleDataRequest.GroupData_Field.RequestID = UUID.randomUUID()
            groupRoleDataRequest.isReliable = true
            SLGroupManager.this.SendMessage(groupRoleDataRequest)
        }
    }, false, 3, 15000)
    private ResultHandler<UUID, GroupRoleDataReply> groupRolesResultHandler
    private RequestHandler<UUID> groupTitlesRequestHandler = AsyncLimitsRequestHandler(this.agentCircuit, SimpleRequestHandler<UUID>() {
        Unit onRequest(@NonNull UUID uuid) {
            Debug.Printf("GroupTitles: [%s] network requesting for group %s", Thread.currentThread().getName(), uuid.toString())
            SLGroupManager.this.requestGroupTitles(uuid)
        }
    }, false, 3, 15000)
    private ResultHandler<UUID, GroupTitlesReply> groupTitlesResultHandler
    /* access modifiers changed from: private */
    UserManager userManager

    SLGroupManager(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit)
        this.groupMemberDataURL = sLAgentCircuit.getCaps().getCapability(SLCaps.SLCapability.GroupMemberData)
        this.userManager = UserManager.getUserManager(sLAgentCircuit.circuitInfo.agentID)
        if (this.userManager != null) {
            this.groupMemberDao = this.userManager.getDaoSession().getGroupMemberDao()
            this.groupRoleMemberDao = this.userManager.getDaoSession().getGroupRoleMemberDao()
            return
        }
        this.groupMemberDao = null
        this.groupRoleMemberDao = null
    }

    /* access modifiers changed from: private */
    Unit RequestGroupProfileData(UUID uuid) {
        GroupProfileRequest groupProfileRequest = GroupProfileRequest()
        groupProfileRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        groupProfileRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        groupProfileRequest.GroupData_Field.GroupID = uuid
        groupProfileRequest.isReliable = true
        SendMessage(groupProfileRequest)
    }

    private Unit RequestRoleMemberChange(UUID uuid, UUID uuid2, UUID uuid3, Int i) {
        GroupRoleChanges groupRoleChanges = GroupRoleChanges()
        groupRoleChanges.AgentData_Field.AgentID = this.circuitInfo.agentID
        groupRoleChanges.AgentData_Field.SessionID = this.circuitInfo.sessionID
        groupRoleChanges.AgentData_Field.GroupID = uuid
        GroupRoleChanges.RoleChange roleChange = GroupRoleChanges.RoleChange()
        roleChange.RoleID = uuid2
        roleChange.MemberID = uuid3
        roleChange.Change = i
        groupRoleChanges.RoleChange_Fields.add(roleChange)
        groupRoleChanges.isReliable = true
        groupRoleChanges.setEventListener(SLMessageEventListener() {
            Unit onMessageAcknowledged(SLMessage sLMessage) {
                SLGroupManager.this.userManager.getChatterList().getGroupManager().requestGroupRoleMembersRefresh(uuid)
            }

            Unit onMessageTimeout(SLMessage sLMessage) {
            }
        SendMessage(groupRoleChanges)
    }

    /* access modifiers changed from: private */
    Unit requestGroupTitles(UUID uuid) {
        GroupTitlesRequest groupTitlesRequest = GroupTitlesRequest()
        groupTitlesRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        groupTitlesRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        groupTitlesRequest.AgentData_Field.GroupID = uuid
        groupTitlesRequest.AgentData_Field.RequestID = UUID.randomUUID()
        groupTitlesRequest.isReliable = true
        SendMessage(groupTitlesRequest)
    }

    Unit AcceptGroupInvite(UUID uuid, UUID uuid2, Boolean z) {
        ImprovedInstantMessage improvedInstantMessage = ImprovedInstantMessage()
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID
        improvedInstantMessage.MessageBlock_Field.FromGroup = false
        improvedInstantMessage.MessageBlock_Field.ToAgentID = uuid
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0
        improvedInstantMessage.MessageBlock_Field.RegionID = UUID(0, 0)
        improvedInstantMessage.MessageBlock_Field.Position = LLVector3()
        improvedInstantMessage.MessageBlock_Field.Offline = 0
        improvedInstantMessage.MessageBlock_Field.Dialog = z ? 35 : 36
        improvedInstantMessage.MessageBlock_Field.ID = uuid2
        improvedInstantMessage.MessageBlock_Field.Timestamp = 0
        improvedInstantMessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM(this.agentCircuit.getAgentName() ?: "Unknown")
        improvedInstantMessage.MessageBlock_Field.Message = if (z) SLMessage.stringToVariableUTF("Group invitation accepted") else SLMessage.stringToVariableUTF("Group invitation declined")
        improvedInstantMessage.MessageBlock_Field.BinaryBucket = ByteArray(0)
        improvedInstantMessage.isReliable = true
        SendMessage(improvedInstantMessage)
    }

    Unit ActivateGroup(UUID uuid) {
        ActivateGroup activateGroup = ActivateGroup()
        activateGroup.AgentData_Field.AgentID = this.circuitInfo.agentID
        activateGroup.AgentData_Field.SessionID = this.circuitInfo.sessionID
        activateGroup.AgentData_Field.GroupID = uuid
        activateGroup.isReliable = true
        SendMessage(activateGroup)
    }

    Unit AddMemberToRole(UUID uuid, UUID uuid2, UUID uuid3) {
        RequestRoleMemberChange(uuid, uuid2, uuid3, 0)
    }

    Unit DeleteRole(UUID uuid, UUID uuid2) {
        GroupRoleUpdate groupRoleUpdate = GroupRoleUpdate()
        groupRoleUpdate.AgentData_Field.AgentID = this.circuitInfo.agentID
        groupRoleUpdate.AgentData_Field.SessionID = this.circuitInfo.sessionID
        groupRoleUpdate.AgentData_Field.GroupID = uuid
        GroupRoleUpdate.RoleData roleData = GroupRoleUpdate.RoleData()
        roleData.RoleID = uuid2
        roleData.Name = SLMessage.stringToVariableOEM("")
        roleData.Title = SLMessage.stringToVariableOEM("")
        roleData.Description = SLMessage.stringToVariableOEM("")
        roleData.Powers = 0
        roleData.UpdateType = 5
        groupRoleUpdate.RoleData_Fields.add(roleData)
        groupRoleUpdate.isReliable = true
        groupRoleUpdate.setEventListener(SLMessageEventListener.SLMessageBaseEventListener() {
            Unit onMessageAcknowledged(SLMessage sLMessage) {
                SLGroupManager.this.userManager.getGroupRoles().requestUpdate(uuid)
            }
        SendMessage(groupRoleUpdate)
    }

    Unit HandleCircuitReady() {
        if (this.userManager != null) {
            this.groupProfileResultHandler = this.userManager.getCachedGroupProfiles().getRequestSource().attachRequestHandler(this.groupProfileRequestHandler)
            this.groupTitlesResultHandler = this.userManager.getGroupTitles().getRequestSource().attachRequestHandler(this.groupTitlesRequestHandler)
            this.groupRolesResultHandler = this.userManager.getGroupRoles().getRequestSource().attachRequestHandler(this.groupRolesRequestHandler)
            this.groupMemberListResultHandler = this.userManager.getChatterList().getGroupManager().getGroupMemberDataSetRequestSource().attachRequestHandler(Strings.isNullOrEmpty(this.groupMemberDataURL) ? this.groupMemberListRequestHandler : this.groupMemberListHTTPRequestHandler)
            this.groupRoleMemberListResultHandler = this.userManager.getChatterList().getGroupManager().getGroupRoleMemberDataSetRequestSource().attachRequestHandler(this.groupRoleMemberListRequestHandler)
        }
    }

    Unit HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.getCachedGroupProfiles().getRequestSource().detachRequestHandler(this.groupProfileRequestHandler)
            this.userManager.getGroupTitles().getRequestSource().detachRequestHandler(this.groupTitlesRequestHandler)
            this.userManager.getGroupRoles().getRequestSource().detachRequestHandler(this.groupRolesRequestHandler)
            this.userManager.getChatterList().getGroupManager().getGroupMemberDataSetRequestSource().detachRequestHandler(this.groupMemberListRequestHandler)
        }
    }

    @SLMessageHandler
    Unit HandleEjectGroupMemberReply(EjectGroupMemberReply ejectGroupMemberReply) {
        this.userManager.getChatterList().getGroupManager().requestRefreshMemberList(ejectGroupMemberReply.GroupData_Field.GroupID)
    }

    @SLMessageHandler
    Unit HandleGroupMembersReply(GroupMembersReply groupMembersReply) {
        Debug.Printf("GroupMember: got reply, %d members, memberCount %d", Int.valueOf(groupMembersReply.MemberData_Fields.size()), Int.valueOf(groupMembersReply.GroupData_Field.MemberCount))
        for (GroupMembersReply.MemberData memberData : groupMembersReply.MemberData_Fields) {
            this.groupMemberDao.insert(GroupMember(groupMembersReply.GroupData_Field.GroupID, groupMembersReply.GroupData_Field.RequestID, memberData.AgentID, memberData.Contribution, SLMessage.stringFromVariableOEM(memberData.OnlineStatus), memberData.AgentPowers, SLMessage.stringFromVariableOEM(memberData.Title), memberData.IsOwner))
            Debug.Printf("GroupMember: userID = %s", memberData.AgentID)
        }
        Long count = this.groupMemberDao.queryBuilder().where(GroupMemberDao.Properties.GroupID.eq(groupMembersReply.GroupData_Field.GroupID), GroupMemberDao.Properties.RequestID.eq(groupMembersReply.GroupData_Field.RequestID)).count()
        Debug.Printf("GroupMemberList: count = %d", Long.valueOf(count))
        if (count >= ((Long) groupMembersReply.GroupData_Field.MemberCount)) {
            this.groupMemberListResultHandler.onResultData(groupMembersReply.GroupData_Field.GroupID, groupMembersReply.GroupData_Field.RequestID)
        }
    }

    @SLMessageHandler
    Unit HandleGroupProfileReply(GroupProfileReply groupProfileReply) {
        if (this.groupProfileResultHandler != null) {
            this.groupProfileResultHandler.onResultData(groupProfileReply.GroupData_Field.GroupID, groupProfileReply)
        }
    }

    @SLMessageHandler
    Unit HandleGroupRoleDataReply(GroupRoleDataReply groupRoleDataReply) {
        if (this.groupRolesResultHandler != null) {
            this.groupRolesResultHandler.onResultData(groupRoleDataReply.GroupData_Field.GroupID, groupRoleDataReply)
        }
    }

    @SLMessageHandler
    Unit HandleGroupRoleMembersReply(GroupRoleMembersReply groupRoleMembersReply) {
        Debug.Printf("GroupRoleMember: got reply, %d members, total pairs %d", Int.valueOf(groupRoleMembersReply.MemberData_Fields.size()), Int.valueOf(groupRoleMembersReply.AgentData_Field.TotalPairs))
        for (GroupRoleMembersReply.MemberData memberData : groupRoleMembersReply.MemberData_Fields) {
            this.groupRoleMemberDao.insert(GroupRoleMember(groupRoleMembersReply.AgentData_Field.GroupID, groupRoleMembersReply.AgentData_Field.RequestID, memberData.RoleID, memberData.MemberID))
        }
        Long count = this.groupRoleMemberDao.queryBuilder().where(GroupRoleMemberDao.Properties.GroupID.eq(groupRoleMembersReply.AgentData_Field.GroupID), GroupRoleMemberDao.Properties.RequestID.eq(groupRoleMembersReply.AgentData_Field.RequestID)).count()
        Debug.Printf("GroupRoleMemberList: count = %d", Long.valueOf(count))
        if (count >= ((Long) groupRoleMembersReply.AgentData_Field.TotalPairs)) {
            this.groupRoleMemberListResultHandler.onResultData(groupRoleMembersReply.AgentData_Field.GroupID, groupRoleMembersReply.AgentData_Field.RequestID)
        }
    }

    @SLMessageHandler
    Unit HandleGroupTitlesReply(GroupTitlesReply groupTitlesReply) {
        if (this.groupTitlesResultHandler != null) {
            this.groupTitlesResultHandler.onResultData(groupTitlesReply.AgentData_Field.GroupID, groupTitlesReply)
        }
    }

    @SLMessageHandler
    Unit HandleJoinGroupReply(JoinGroupReply joinGroupReply) {
        AgentDataUpdateRequest agentDataUpdateRequest = AgentDataUpdateRequest()
        agentDataUpdateRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        agentDataUpdateRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        agentDataUpdateRequest.isReliable = true
        SendMessage(agentDataUpdateRequest)
        this.eventBus.publish(SLJoinLeaveGroupEvent(joinGroupReply.GroupData_Field.GroupID, true, joinGroupReply.GroupData_Field.Success))
    }

    @SLMessageHandler
    Unit HandleLeaveGroupReply(LeaveGroupReply leaveGroupReply) {
        AgentDataUpdateRequest agentDataUpdateRequest = AgentDataUpdateRequest()
        agentDataUpdateRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        agentDataUpdateRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        agentDataUpdateRequest.isReliable = true
        SendMessage(agentDataUpdateRequest)
        this.eventBus.publish(SLJoinLeaveGroupEvent(leaveGroupReply.GroupData_Field.GroupID, false, leaveGroupReply.GroupData_Field.Success))
    }

    Unit RemoveMemberFromRole(UUID uuid, UUID uuid2, UUID uuid3) {
        RequestRoleMemberChange(uuid, uuid2, uuid3, 1)
    }

    Unit RequestEjectFromGroup(UUID uuid, UUID uuid2) {
        EjectGroupMemberRequest ejectGroupMemberRequest = EjectGroupMemberRequest()
        ejectGroupMemberRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        ejectGroupMemberRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        ejectGroupMemberRequest.GroupData_Field.GroupID = uuid
        EjectGroupMemberRequest.EjectData ejectData = EjectGroupMemberRequest.EjectData()
        ejectData.EjecteeID = uuid2
        ejectGroupMemberRequest.EjectData_Fields.add(ejectData)
        ejectGroupMemberRequest.isReliable = true
        SendMessage(ejectGroupMemberRequest)
    }

    Unit RequestJoinGroup(UUID uuid) {
        JoinGroupRequest joinGroupRequest = JoinGroupRequest()
        joinGroupRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        joinGroupRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        joinGroupRequest.GroupData_Field.GroupID = uuid
        joinGroupRequest.isReliable = true
        SendMessage(joinGroupRequest)
    }

    Unit RequestLeaveGroup(UUID uuid) {
        LeaveGroupRequest leaveGroupRequest = LeaveGroupRequest()
        leaveGroupRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        leaveGroupRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        leaveGroupRequest.GroupData_Field.GroupID = uuid
        leaveGroupRequest.isReliable = true
        SendMessage(leaveGroupRequest)
    }

    Unit RequestMemberRoleChanges(UUID uuid, UUID uuid2, Collection<UUID> collection, Collection<UUID> collection2) {
        Boolean equals = this.circuitInfo.agentID.equals(uuid2)
        GroupRoleChanges groupRoleChanges = GroupRoleChanges()
        groupRoleChanges.AgentData_Field.AgentID = this.circuitInfo.agentID
        groupRoleChanges.AgentData_Field.SessionID = this.circuitInfo.sessionID
        groupRoleChanges.AgentData_Field.GroupID = uuid
        for (UUID uuid3 : collection) {
            Debug.Printf("GroupRoleChange: groupID %s memberID %s add %s", uuid, uuid2, uuid3)
            GroupRoleChanges.RoleChange roleChange = GroupRoleChanges.RoleChange()
            roleChange.RoleID = uuid3
            roleChange.MemberID = uuid2
            roleChange.Change = 0
            groupRoleChanges.RoleChange_Fields.add(roleChange)
        }
        for (UUID uuid4 : collection2) {
            Debug.Printf("GroupRoleChange: groupID %s memberID %s remove %s", uuid, uuid2, uuid4)
            GroupRoleChanges.RoleChange roleChange2 = GroupRoleChanges.RoleChange()
            roleChange2.RoleID = uuid4
            roleChange2.MemberID = uuid2
            roleChange2.Change = 1
            groupRoleChanges.RoleChange_Fields.add(roleChange2)
        }
        groupRoleChanges.isReliable = true
        groupRoleChanges.setEventListener(SLMessageEventListener() {
            Unit onMessageAcknowledged(SLMessage sLMessage) {
                SLGroupManager.this.userManager.getChatterList().getGroupManager().requestGroupRoleMembersRefresh(uuid)
                if (equals) {
                    SLGroupManager.this.userManager.getCachedGroupProfiles().requestUpdate(uuid)
                    SLGroupManager.this.userManager.getGroupTitles().requestUpdate(uuid)
                    SLGroupManager.this.userManager.getAvatarGroupLists().requestUpdate(uuid2)
                }
            }

            Unit onMessageTimeout(SLMessage sLMessage) {
            }
        SendMessage(groupRoleChanges)
    }

    Unit SendGroupInvite(UUID uuid, UUID uuid2, UUID uuid3) {
        InviteGroupRequest inviteGroupRequest = InviteGroupRequest()
        inviteGroupRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        inviteGroupRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        inviteGroupRequest.GroupData_Field.GroupID = uuid2
        InviteGroupRequest.InviteData inviteData = InviteGroupRequest.InviteData()
        inviteData.InviteeID = uuid
        inviteData.RoleID = uuid3
        inviteGroupRequest.InviteData_Fields.add(inviteData)
        inviteGroupRequest.isReliable = true
        SendMessage(inviteGroupRequest)
        this.agentCircuit.HandleChatEvent(ChatterID.getGroupChatterID(this.agentCircuit.getAgentUUID(), uuid2), SLChatGroupInvitationSentEvent((ChatMessageSource) ChatMessageSourceUser(uuid), this.agentCircuit.getAgentUUID()), true)
    }

    Unit SendGroupNotice(UUID uuid, String str, String str2, SLInventoryEntry sLInventoryEntry) {
        ImprovedInstantMessage improvedInstantMessage = ImprovedInstantMessage()
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID
        improvedInstantMessage.MessageBlock_Field.FromGroup = false
        improvedInstantMessage.MessageBlock_Field.ToAgentID = uuid
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0
        improvedInstantMessage.MessageBlock_Field.RegionID = UUIDPool.ZeroUUID
        improvedInstantMessage.MessageBlock_Field.Position = LLVector3()
        improvedInstantMessage.MessageBlock_Field.Offline = 0
        improvedInstantMessage.MessageBlock_Field.Dialog = 32
        improvedInstantMessage.MessageBlock_Field.ID = UUIDPool.ZeroUUID
        improvedInstantMessage.MessageBlock_Field.Timestamp = 0
        improvedInstantMessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM(this.agentCircuit.getAgentName() ?: "Unknown")
        improvedInstantMessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF(str + "|" + str2)
        if (sLInventoryEntry != null) {
            try {
                String serializeToXML = LLSDMap(LLSDMap.LLSDMapEntry("item_id", LLSDUUID(sLInventoryEntry.uuid)), LLSDMap.LLSDMapEntry("owner_id", LLSDUUID(sLInventoryEntry.ownerUUID))).serializeToXML()
                improvedInstantMessage.MessageBlock_Field.BinaryBucket = SLMessage.stringToVariableOEM(serializeToXML)
            } catch (IOException e) {
                e.printStackTrace()
                improvedInstantMessage.MessageBlock_Field.BinaryBucket = ByteArray(0)
            }
        } else {
            improvedInstantMessage.MessageBlock_Field.BinaryBucket = ByteArray(0)
        }
        improvedInstantMessage.isReliable = true
        SendMessage(improvedInstantMessage)
    }

    Unit SetGroupContribution(UUID uuid, Int i) {
        SetGroupContribution setGroupContribution = SetGroupContribution()
        setGroupContribution.AgentData_Field.AgentID = this.circuitInfo.agentID
        setGroupContribution.AgentData_Field.SessionID = this.circuitInfo.sessionID
        setGroupContribution.Data_Field.GroupID = uuid
        setGroupContribution.Data_Field.Contribution = i
        setGroupContribution.isReliable = true
        setGroupContribution.setEventListener(SLMessageEventListener() {
            Unit onMessageAcknowledged(SLMessage sLMessage) {
                AgentDataUpdateRequest agentDataUpdateRequest = AgentDataUpdateRequest()
                agentDataUpdateRequest.AgentData_Field.AgentID = SLGroupManager.this.circuitInfo.agentID
                agentDataUpdateRequest.AgentData_Field.SessionID = SLGroupManager.this.circuitInfo.sessionID
                agentDataUpdateRequest.isReliable = true
                SLGroupManager.this.SendMessage(agentDataUpdateRequest)
            }

            Unit onMessageTimeout(SLMessage sLMessage) {
            }
        SendMessage(setGroupContribution)
    }

    Unit SetGroupOptions(UUID uuid, Boolean z, Boolean z2) {
        SetGroupAcceptNotices setGroupAcceptNotices = SetGroupAcceptNotices()
        setGroupAcceptNotices.AgentData_Field.AgentID = this.circuitInfo.agentID
        setGroupAcceptNotices.AgentData_Field.SessionID = this.circuitInfo.sessionID
        setGroupAcceptNotices.Data_Field.GroupID = uuid
        setGroupAcceptNotices.Data_Field.AcceptNotices = z
        setGroupAcceptNotices.NewData_Field.ListInProfile = z2
        setGroupAcceptNotices.isReliable = true
        SendMessage(setGroupAcceptNotices)
        this.agentCircuit.getModules().userProfiles.requestAgentDataUpdate()
    }

    Unit SetGroupRole(UUID uuid, UUID uuid2) {
        GroupTitleUpdate groupTitleUpdate = GroupTitleUpdate()
        groupTitleUpdate.AgentData_Field.AgentID = this.circuitInfo.agentID
        groupTitleUpdate.AgentData_Field.SessionID = this.circuitInfo.sessionID
        groupTitleUpdate.AgentData_Field.GroupID = uuid
        groupTitleUpdate.AgentData_Field.TitleRoleID = uuid2
        groupTitleUpdate.isReliable = true
        SendMessage(groupTitleUpdate)
        requestGroupTitles(uuid)
    }

    Unit SetRoleProperties(UUID uuid, @Nullable UUID uuid2, String str, String str2, String str3, Long j) {
        Debug.Printf("GroupRole: setting role properties for role %s", uuid2)
        GroupRoleUpdate groupRoleUpdate = GroupRoleUpdate()
        groupRoleUpdate.AgentData_Field.AgentID = this.circuitInfo.agentID
        groupRoleUpdate.AgentData_Field.SessionID = this.circuitInfo.sessionID
        groupRoleUpdate.AgentData_Field.GroupID = uuid
        GroupRoleUpdate.RoleData roleData = GroupRoleUpdate.RoleData()
        roleData.RoleID = uuid2 != null ? uuid2 : UUID.randomUUID()
        roleData.Name = SLMessage.stringToVariableOEM(str)
        roleData.Title = SLMessage.stringToVariableOEM(str2)
        roleData.Description = SLMessage.stringToVariableOEM(str3)
        roleData.Powers = j
        roleData.UpdateType = uuid2 != null ? 3 : 4
        groupRoleUpdate.RoleData_Fields.add(roleData)
        groupRoleUpdate.isReliable = true
        groupRoleUpdate.setEventListener(SLMessageEventListener.SLMessageBaseEventListener() {
            Unit onMessageAcknowledged(SLMessage sLMessage) {
                Debug.Printf("GroupRole: ack set properties for role %s", uuid2)
                SLGroupManager.this.userManager.getGroupRoles().requestUpdate(uuid)
            }
        SendMessage(groupRoleUpdate)
    }

    UUID getActiveGroupID() {
        return this.activeGroupID
    }
}
