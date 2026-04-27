package com.lumiyaviewer.lumiya.slproto.modules.groups;

import com.google.common.base.Strings;
import com.google.common.primitives.UnsignedLong;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.GroupMember;
import com.lumiyaviewer.lumiya.dao.GroupMemberDao;
import com.lumiyaviewer.lumiya.dao.GroupRoleMember;
import com.lumiyaviewer.lumiya.dao.GroupRoleMemberDao;
import com.lumiyaviewer.lumiya.react.AsyncCancellableRequestHandler;
import com.lumiyaviewer.lumiya.react.AsyncLimitsRequestHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.SLMessageEventListener;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatGroupInvitationSentEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLJoinLeaveGroupEvent;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.https.GenericHTTPExecutor;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID;
import com.lumiyaviewer.lumiya.slproto.messages.ActivateGroup;
import com.lumiyaviewer.lumiya.slproto.messages.AgentDataUpdateRequest;
import com.lumiyaviewer.lumiya.slproto.messages.EjectGroupMemberReply;
import com.lumiyaviewer.lumiya.slproto.messages.EjectGroupMemberRequest;
import com.lumiyaviewer.lumiya.slproto.messages.GroupMembersReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupMembersRequest;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileRequest;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleChanges;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataRequest;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleMembersReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleMembersRequest;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitleUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesRequest;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.messages.InviteGroupRequest;
import com.lumiyaviewer.lumiya.slproto.messages.JoinGroupReply;
import com.lumiyaviewer.lumiya.slproto.messages.JoinGroupRequest;
import com.lumiyaviewer.lumiya.slproto.messages.LeaveGroupReply;
import com.lumiyaviewer.lumiya.slproto.messages.LeaveGroupRequest;
import com.lumiyaviewer.lumiya.slproto.messages.SetGroupAcceptNotices;
import com.lumiyaviewer.lumiya.slproto.messages.SetGroupContribution;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUser;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SLGroupManager extends SLModule {
    private static final int RoleMemberChange_Add = 0;
    private static final int RoleMemberChange_Remove = 1;
    private static final int Role_Update_All = 3;
    private static final int Role_Update_Create = 4;
    private static final int Role_Update_Delete = 5;
    private volatile UUID activeGroupID = null;
    /* access modifiers changed from: private */
    public final GroupMemberDao groupMemberDao;
    /* access modifiers changed from: private */
    public final String groupMemberDataURL;
    private final RequestHandler<UUID> groupMemberListHTTPRequestHandler = new AsyncCancellableRequestHandler(GenericHTTPExecutor.getInstance(), new SimpleRequestHandler<UUID>() {
        public void onRequest(@Nonnull UUID uuid) {
            int i;
            Debug.Printf("GroupMemberList: [%s] network requesting for group %s", Thread.currentThread().getName(), uuid.toString());
            try {
                UUID randomUUID = UUID.randomUUID();
                LLSDNode PerformRequest = new LLSDXMLRequest().PerformRequest(SLGroupManager.this.groupMemberDataURL, new LLSDMap(new LLSDMap.LLSDMapEntry("group_id", new LLSDUUID(uuid))));
                if (PerformRequest != null) {
                    LLSDNode byKey = PerformRequest.byKey("titles");
                    LLSDNode byKey2 = PerformRequest.byKey("defaults");
                    LLSDNode byKey3 = PerformRequest.byKey("members");
                    long j = 0;
                    if (byKey2.keyExists("default_powers")) {
                        try {
                            String asString = byKey2.byKey("default_powers").asString();
                            j = asString.startsWith("0x") ? Long.decode(asString).longValue() : UnsignedLong.valueOf(asString, 16).longValue();
                        } catch (NumberFormatException e) {
                            Debug.Warning(e);
                        }
                    }
                    if (byKey3 instanceof LLSDMap) {
                        int i2 = 0;
                        for (Map.Entry entry : ((LLSDMap) byKey3).entrySet()) {
                            UUID uuid2 = UUIDPool.getUUID((String) entry.getKey());
                            LLSDNode lLSDNode = (LLSDNode) entry.getValue();
                            boolean z = false;
                            String asString2 = lLSDNode.keyExists("title") ? byKey.byIndex(lLSDNode.byKey("title").asInt()).asString() : byKey.byIndex(0).asString();
                            long longValue = lLSDNode.keyExists("powers") ? UnsignedLong.valueOf(lLSDNode.byKey("powers").asString(), 16).longValue() : j;
                            String asString3 = lLSDNode.keyExists("last_login") ? lLSDNode.byKey("last_login").asString() : "Unknown";
                            int asInt = lLSDNode.keyExists("donated_square_meters") ? lLSDNode.byKey("donated_square_meters").asInt() : 0;
                            if (lLSDNode.keyExists("owner")) {
                                if (lLSDNode.byKey("owner").isString()) {
                                    String asString4 = lLSDNode.byKey("owner").asString();
                                    if (asString4.equalsIgnoreCase("y") || asString4.equalsIgnoreCase("yes") || asString4.equalsIgnoreCase("true") || asString4.equalsIgnoreCase("1")) {
                                        z = true;
                                    }
                                } else if (lLSDNode.byKey("owner").isBoolean()) {
                                    z = lLSDNode.byKey("owner").asBoolean();
                                }
                            }
                            i2++;
                            if (SLGroupManager.this.groupMemberDao != null) {
                                SLGroupManager.this.groupMemberDao.insert(new GroupMember(uuid, randomUUID, uuid2, asInt, asString3, longValue, asString2, z));
                            }
                        }
                        i = i2;
                    } else {
                        i = 0;
                    }
                    Debug.Printf("GroupMemberList: parsed list for group: %s requestID %s memberCount %d", uuid, randomUUID, Integer.valueOf(i));
                    SLGroupManager.this.groupMemberListResultHandler.onResultData(uuid, randomUUID);
                    return;
                }
                SLGroupManager.this.groupMemberListResultHandler.onResultError(uuid, new LLSDException("No data"));
            } catch (Exception e2) {
                Debug.Warning(e2);
                SLGroupManager.this.groupMemberListResultHandler.onResultError(uuid, e2);
            }
        }
    });
    private final RequestHandler<UUID> groupMemberListRequestHandler = new AsyncLimitsRequestHandler(this.agentCircuit, new SimpleRequestHandler<UUID>() {
        public void onRequest(@Nonnull UUID uuid) {
            Debug.Printf("GroupMemberList: [%s] network requesting for group %s", Thread.currentThread().getName(), uuid.toString());
            GroupMembersRequest groupMembersRequest = new GroupMembersRequest();
            groupMembersRequest.AgentData_Field.AgentID = SLGroupManager.this.circuitInfo.agentID;
            groupMembersRequest.AgentData_Field.SessionID = SLGroupManager.this.circuitInfo.sessionID;
            groupMembersRequest.GroupData_Field.GroupID = uuid;
            groupMembersRequest.GroupData_Field.RequestID = UUID.randomUUID();
            groupMembersRequest.isReliable = true;
            SLGroupManager.this.SendMessage(groupMembersRequest);
        }
    }, false, 3, 15000);
    /* access modifiers changed from: private */
    public ResultHandler<UUID, UUID> groupMemberListResultHandler;
    private final RequestHandler<UUID> groupProfileRequestHandler = new AsyncLimitsRequestHandler(this.agentCircuit, new SimpleRequestHandler<UUID>() {
        public void onRequest(@Nonnull UUID uuid) {
            Debug.Printf("GroupManager: [%s] network requesting for group %s", Thread.currentThread().getName(), uuid.toString());
            SLGroupManager.this.RequestGroupProfileData(uuid);
        }
    }, false, 3, 15000);
    private ResultHandler<UUID, GroupProfileReply> groupProfileResultHandler;
    private final GroupRoleMemberDao groupRoleMemberDao;
    private final RequestHandler<UUID> groupRoleMemberListRequestHandler = new AsyncLimitsRequestHandler(this.agentCircuit, new SimpleRequestHandler<UUID>() {
        public void onRequest(@Nonnull UUID uuid) {
            Debug.Printf("GroupRoleMemberList: [%s] network requesting for %s", Thread.currentThread().getName(), uuid.toString());
            GroupRoleMembersRequest groupRoleMembersRequest = new GroupRoleMembersRequest();
            groupRoleMembersRequest.AgentData_Field.AgentID = SLGroupManager.this.circuitInfo.agentID;
            groupRoleMembersRequest.AgentData_Field.SessionID = SLGroupManager.this.circuitInfo.sessionID;
            groupRoleMembersRequest.GroupData_Field.GroupID = uuid;
            groupRoleMembersRequest.GroupData_Field.RequestID = UUID.randomUUID();
            groupRoleMembersRequest.isReliable = true;
            SLGroupManager.this.SendMessage(groupRoleMembersRequest);
        }
    }, false, 3, 15000);
    private ResultHandler<UUID, UUID> groupRoleMemberListResultHandler;
    private final RequestHandler<UUID> groupRolesRequestHandler = new AsyncLimitsRequestHandler(this.agentCircuit, new SimpleRequestHandler<UUID>() {
        public void onRequest(@Nonnull UUID uuid) {
            Debug.Printf("GroupRoles: [%s] network requesting for group %s", Thread.currentThread().getName(), uuid.toString());
            GroupRoleDataRequest groupRoleDataRequest = new GroupRoleDataRequest();
            groupRoleDataRequest.AgentData_Field.AgentID = SLGroupManager.this.circuitInfo.agentID;
            groupRoleDataRequest.AgentData_Field.SessionID = SLGroupManager.this.circuitInfo.sessionID;
            groupRoleDataRequest.GroupData_Field.GroupID = uuid;
            groupRoleDataRequest.GroupData_Field.RequestID = UUID.randomUUID();
            groupRoleDataRequest.isReliable = true;
            SLGroupManager.this.SendMessage(groupRoleDataRequest);
        }
    }, false, 3, 15000);
    private ResultHandler<UUID, GroupRoleDataReply> groupRolesResultHandler;
    private final RequestHandler<UUID> groupTitlesRequestHandler = new AsyncLimitsRequestHandler(this.agentCircuit, new SimpleRequestHandler<UUID>() {
        public void onRequest(@Nonnull UUID uuid) {
            Debug.Printf("GroupTitles: [%s] network requesting for group %s", Thread.currentThread().getName(), uuid.toString());
            SLGroupManager.this.requestGroupTitles(uuid);
        }
    }, false, 3, 15000);
    private ResultHandler<UUID, GroupTitlesReply> groupTitlesResultHandler;
    /* access modifiers changed from: private */
    public final UserManager userManager;

    public SLGroupManager(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit);
        this.groupMemberDataURL = sLAgentCircuit.getCaps().getCapability(SLCaps.SLCapability.GroupMemberData);
        this.userManager = UserManager.getUserManager(sLAgentCircuit.circuitInfo.agentID);
        if (this.userManager != null) {
            this.groupMemberDao = this.userManager.getDaoSession().getGroupMemberDao();
            this.groupRoleMemberDao = this.userManager.getDaoSession().getGroupRoleMemberDao();
            return;
        }
        this.groupMemberDao = null;
        this.groupRoleMemberDao = null;
    }

    /* access modifiers changed from: private */
    public void RequestGroupProfileData(UUID uuid) {
        GroupProfileRequest groupProfileRequest = new GroupProfileRequest();
        groupProfileRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        groupProfileRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        groupProfileRequest.GroupData_Field.GroupID = uuid;
        groupProfileRequest.isReliable = true;
        SendMessage(groupProfileRequest);
    }

    private void RequestRoleMemberChange(final UUID uuid, UUID uuid2, UUID uuid3, int i) {
        GroupRoleChanges groupRoleChanges = new GroupRoleChanges();
        groupRoleChanges.AgentData_Field.AgentID = this.circuitInfo.agentID;
        groupRoleChanges.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        groupRoleChanges.AgentData_Field.GroupID = uuid;
        GroupRoleChanges.RoleChange roleChange = new GroupRoleChanges.RoleChange();
        roleChange.RoleID = uuid2;
        roleChange.MemberID = uuid3;
        roleChange.Change = i;
        groupRoleChanges.RoleChange_Fields.add(roleChange);
        groupRoleChanges.isReliable = true;
        groupRoleChanges.setEventListener(new SLMessageEventListener() {
            public void onMessageAcknowledged(SLMessage sLMessage) {
                SLGroupManager.this.userManager.getChatterList().getGroupManager().requestGroupRoleMembersRefresh(uuid);
            }

            public void onMessageTimeout(SLMessage sLMessage) {
            }
        });
        SendMessage(groupRoleChanges);
    }

    /* access modifiers changed from: private */
    public void requestGroupTitles(UUID uuid) {
        GroupTitlesRequest groupTitlesRequest = new GroupTitlesRequest();
        groupTitlesRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        groupTitlesRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        groupTitlesRequest.AgentData_Field.GroupID = uuid;
        groupTitlesRequest.AgentData_Field.RequestID = UUID.randomUUID();
        groupTitlesRequest.isReliable = true;
        SendMessage(groupTitlesRequest);
    }

    public void AcceptGroupInvite(UUID uuid, UUID uuid2, boolean z) {
        ImprovedInstantMessage improvedInstantMessage = new ImprovedInstantMessage();
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        improvedInstantMessage.MessageBlock_Field.FromGroup = false;
        improvedInstantMessage.MessageBlock_Field.ToAgentID = uuid;
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0;
        improvedInstantMessage.MessageBlock_Field.RegionID = new UUID(0, 0);
        improvedInstantMessage.MessageBlock_Field.Position = new LLVector3();
        improvedInstantMessage.MessageBlock_Field.Offline = 0;
        improvedInstantMessage.MessageBlock_Field.Dialog = z ? 35 : 36;
        improvedInstantMessage.MessageBlock_Field.ID = uuid2;
        improvedInstantMessage.MessageBlock_Field.Timestamp = 0;
        improvedInstantMessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo");
        improvedInstantMessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF("todo");
        improvedInstantMessage.MessageBlock_Field.BinaryBucket = new byte[0];
        improvedInstantMessage.isReliable = true;
        SendMessage(improvedInstantMessage);
    }

    public void ActivateGroup(UUID uuid) {
        ActivateGroup activateGroup = new ActivateGroup();
        activateGroup.AgentData_Field.AgentID = this.circuitInfo.agentID;
        activateGroup.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        activateGroup.AgentData_Field.GroupID = uuid;
        activateGroup.isReliable = true;
        SendMessage(activateGroup);
    }

    public void AddMemberToRole(UUID uuid, UUID uuid2, UUID uuid3) {
        RequestRoleMemberChange(uuid, uuid2, uuid3, 0);
    }

    public void DeleteRole(final UUID uuid, UUID uuid2) {
        GroupRoleUpdate groupRoleUpdate = new GroupRoleUpdate();
        groupRoleUpdate.AgentData_Field.AgentID = this.circuitInfo.agentID;
        groupRoleUpdate.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        groupRoleUpdate.AgentData_Field.GroupID = uuid;
        GroupRoleUpdate.RoleData roleData = new GroupRoleUpdate.RoleData();
        roleData.RoleID = uuid2;
        roleData.Name = SLMessage.stringToVariableOEM("");
        roleData.Title = SLMessage.stringToVariableOEM("");
        roleData.Description = SLMessage.stringToVariableOEM("");
        roleData.Powers = 0;
        roleData.UpdateType = 5;
        groupRoleUpdate.RoleData_Fields.add(roleData);
        groupRoleUpdate.isReliable = true;
        groupRoleUpdate.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() {
            public void onMessageAcknowledged(SLMessage sLMessage) {
                SLGroupManager.this.userManager.getGroupRoles().requestUpdate(uuid);
            }
        });
        SendMessage(groupRoleUpdate);
    }

    public void HandleCircuitReady() {
        if (this.userManager != null) {
            this.groupProfileResultHandler = this.userManager.getCachedGroupProfiles().getRequestSource().attachRequestHandler(this.groupProfileRequestHandler);
            this.groupTitlesResultHandler = this.userManager.getGroupTitles().getRequestSource().attachRequestHandler(this.groupTitlesRequestHandler);
            this.groupRolesResultHandler = this.userManager.getGroupRoles().getRequestSource().attachRequestHandler(this.groupRolesRequestHandler);
            this.groupMemberListResultHandler = this.userManager.getChatterList().getGroupManager().getGroupMemberDataSetRequestSource().attachRequestHandler(Strings.isNullOrEmpty(this.groupMemberDataURL) ? this.groupMemberListRequestHandler : this.groupMemberListHTTPRequestHandler);
            this.groupRoleMemberListResultHandler = this.userManager.getChatterList().getGroupManager().getGroupRoleMemberDataSetRequestSource().attachRequestHandler(this.groupRoleMemberListRequestHandler);
        }
    }

    public void HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.getCachedGroupProfiles().getRequestSource().detachRequestHandler(this.groupProfileRequestHandler);
            this.userManager.getGroupTitles().getRequestSource().detachRequestHandler(this.groupTitlesRequestHandler);
            this.userManager.getGroupRoles().getRequestSource().detachRequestHandler(this.groupRolesRequestHandler);
            this.userManager.getChatterList().getGroupManager().getGroupMemberDataSetRequestSource().detachRequestHandler(this.groupMemberListRequestHandler);
        }
    }

    @SLMessageHandler
    public void HandleEjectGroupMemberReply(EjectGroupMemberReply ejectGroupMemberReply) {
        this.userManager.getChatterList().getGroupManager().requestRefreshMemberList(ejectGroupMemberReply.GroupData_Field.GroupID);
    }

    @SLMessageHandler
    public void HandleGroupMembersReply(GroupMembersReply groupMembersReply) {
        Debug.Printf("GroupMember: got reply, %d members, memberCount %d", Integer.valueOf(groupMembersReply.MemberData_Fields.size()), Integer.valueOf(groupMembersReply.GroupData_Field.MemberCount));
        for (GroupMembersReply.MemberData memberData : groupMembersReply.MemberData_Fields) {
            this.groupMemberDao.insert(new GroupMember(groupMembersReply.GroupData_Field.GroupID, groupMembersReply.GroupData_Field.RequestID, memberData.AgentID, memberData.Contribution, SLMessage.stringFromVariableOEM(memberData.OnlineStatus), memberData.AgentPowers, SLMessage.stringFromVariableOEM(memberData.Title), memberData.IsOwner));
            Debug.Printf("GroupMember: userID = %s", memberData.AgentID);
        }
        long count = this.groupMemberDao.queryBuilder().where(GroupMemberDao.Properties.GroupID.eq(groupMembersReply.GroupData_Field.GroupID), GroupMemberDao.Properties.RequestID.eq(groupMembersReply.GroupData_Field.RequestID)).count();
        Debug.Printf("GroupMemberList: count = %d", Long.valueOf(count));
        if (count >= ((long) groupMembersReply.GroupData_Field.MemberCount)) {
            this.groupMemberListResultHandler.onResultData(groupMembersReply.GroupData_Field.GroupID, groupMembersReply.GroupData_Field.RequestID);
        }
    }

    @SLMessageHandler
    public void HandleGroupProfileReply(GroupProfileReply groupProfileReply) {
        if (this.groupProfileResultHandler != null) {
            this.groupProfileResultHandler.onResultData(groupProfileReply.GroupData_Field.GroupID, groupProfileReply);
        }
    }

    @SLMessageHandler
    public void HandleGroupRoleDataReply(GroupRoleDataReply groupRoleDataReply) {
        if (this.groupRolesResultHandler != null) {
            this.groupRolesResultHandler.onResultData(groupRoleDataReply.GroupData_Field.GroupID, groupRoleDataReply);
        }
    }

    @SLMessageHandler
    public void HandleGroupRoleMembersReply(GroupRoleMembersReply groupRoleMembersReply) {
        Debug.Printf("GroupRoleMember: got reply, %d members, total pairs %d", Integer.valueOf(groupRoleMembersReply.MemberData_Fields.size()), Integer.valueOf(groupRoleMembersReply.AgentData_Field.TotalPairs));
        for (GroupRoleMembersReply.MemberData memberData : groupRoleMembersReply.MemberData_Fields) {
            this.groupRoleMemberDao.insert(new GroupRoleMember(groupRoleMembersReply.AgentData_Field.GroupID, groupRoleMembersReply.AgentData_Field.RequestID, memberData.RoleID, memberData.MemberID));
        }
        long count = this.groupRoleMemberDao.queryBuilder().where(GroupRoleMemberDao.Properties.GroupID.eq(groupRoleMembersReply.AgentData_Field.GroupID), GroupRoleMemberDao.Properties.RequestID.eq(groupRoleMembersReply.AgentData_Field.RequestID)).count();
        Debug.Printf("GroupRoleMemberList: count = %d", Long.valueOf(count));
        if (count >= ((long) groupRoleMembersReply.AgentData_Field.TotalPairs)) {
            this.groupRoleMemberListResultHandler.onResultData(groupRoleMembersReply.AgentData_Field.GroupID, groupRoleMembersReply.AgentData_Field.RequestID);
        }
    }

    @SLMessageHandler
    public void HandleGroupTitlesReply(GroupTitlesReply groupTitlesReply) {
        if (this.groupTitlesResultHandler != null) {
            this.groupTitlesResultHandler.onResultData(groupTitlesReply.AgentData_Field.GroupID, groupTitlesReply);
        }
    }

    @SLMessageHandler
    public void HandleJoinGroupReply(JoinGroupReply joinGroupReply) {
        AgentDataUpdateRequest agentDataUpdateRequest = new AgentDataUpdateRequest();
        agentDataUpdateRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentDataUpdateRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        agentDataUpdateRequest.isReliable = true;
        SendMessage(agentDataUpdateRequest);
        this.eventBus.publish(new SLJoinLeaveGroupEvent(joinGroupReply.GroupData_Field.GroupID, true, joinGroupReply.GroupData_Field.Success));
    }

    @SLMessageHandler
    public void HandleLeaveGroupReply(LeaveGroupReply leaveGroupReply) {
        AgentDataUpdateRequest agentDataUpdateRequest = new AgentDataUpdateRequest();
        agentDataUpdateRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentDataUpdateRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        agentDataUpdateRequest.isReliable = true;
        SendMessage(agentDataUpdateRequest);
        this.eventBus.publish(new SLJoinLeaveGroupEvent(leaveGroupReply.GroupData_Field.GroupID, false, leaveGroupReply.GroupData_Field.Success));
    }

    public void RemoveMemberFromRole(UUID uuid, UUID uuid2, UUID uuid3) {
        RequestRoleMemberChange(uuid, uuid2, uuid3, 1);
    }

    public void RequestEjectFromGroup(UUID uuid, UUID uuid2) {
        EjectGroupMemberRequest ejectGroupMemberRequest = new EjectGroupMemberRequest();
        ejectGroupMemberRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        ejectGroupMemberRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        ejectGroupMemberRequest.GroupData_Field.GroupID = uuid;
        EjectGroupMemberRequest.EjectData ejectData = new EjectGroupMemberRequest.EjectData();
        ejectData.EjecteeID = uuid2;
        ejectGroupMemberRequest.EjectData_Fields.add(ejectData);
        ejectGroupMemberRequest.isReliable = true;
        SendMessage(ejectGroupMemberRequest);
    }

    public void RequestJoinGroup(UUID uuid) {
        JoinGroupRequest joinGroupRequest = new JoinGroupRequest();
        joinGroupRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        joinGroupRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        joinGroupRequest.GroupData_Field.GroupID = uuid;
        joinGroupRequest.isReliable = true;
        SendMessage(joinGroupRequest);
    }

    public void RequestLeaveGroup(UUID uuid) {
        LeaveGroupRequest leaveGroupRequest = new LeaveGroupRequest();
        leaveGroupRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        leaveGroupRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        leaveGroupRequest.GroupData_Field.GroupID = uuid;
        leaveGroupRequest.isReliable = true;
        SendMessage(leaveGroupRequest);
    }

    public void RequestMemberRoleChanges(final UUID uuid, final UUID uuid2, Collection<UUID> collection, Collection<UUID> collection2) {
        final boolean equals = this.circuitInfo.agentID.equals(uuid2);
        GroupRoleChanges groupRoleChanges = new GroupRoleChanges();
        groupRoleChanges.AgentData_Field.AgentID = this.circuitInfo.agentID;
        groupRoleChanges.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        groupRoleChanges.AgentData_Field.GroupID = uuid;
        for (UUID uuid3 : collection) {
            Debug.Printf("GroupRoleChange: groupID %s memberID %s add %s", uuid, uuid2, uuid3);
            GroupRoleChanges.RoleChange roleChange = new GroupRoleChanges.RoleChange();
            roleChange.RoleID = uuid3;
            roleChange.MemberID = uuid2;
            roleChange.Change = 0;
            groupRoleChanges.RoleChange_Fields.add(roleChange);
        }
        for (UUID uuid4 : collection2) {
            Debug.Printf("GroupRoleChange: groupID %s memberID %s remove %s", uuid, uuid2, uuid4);
            GroupRoleChanges.RoleChange roleChange2 = new GroupRoleChanges.RoleChange();
            roleChange2.RoleID = uuid4;
            roleChange2.MemberID = uuid2;
            roleChange2.Change = 1;
            groupRoleChanges.RoleChange_Fields.add(roleChange2);
        }
        groupRoleChanges.isReliable = true;
        groupRoleChanges.setEventListener(new SLMessageEventListener() {
            public void onMessageAcknowledged(SLMessage sLMessage) {
                SLGroupManager.this.userManager.getChatterList().getGroupManager().requestGroupRoleMembersRefresh(uuid);
                if (equals) {
                    SLGroupManager.this.userManager.getCachedGroupProfiles().requestUpdate(uuid);
                    SLGroupManager.this.userManager.getGroupTitles().requestUpdate(uuid);
                    SLGroupManager.this.userManager.getAvatarGroupLists().requestUpdate(uuid2);
                }
            }

            public void onMessageTimeout(SLMessage sLMessage) {
            }
        });
        SendMessage(groupRoleChanges);
    }

    public void SendGroupInvite(UUID uuid, UUID uuid2, UUID uuid3) {
        InviteGroupRequest inviteGroupRequest = new InviteGroupRequest();
        inviteGroupRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        inviteGroupRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        inviteGroupRequest.GroupData_Field.GroupID = uuid2;
        InviteGroupRequest.InviteData inviteData = new InviteGroupRequest.InviteData();
        inviteData.InviteeID = uuid;
        inviteData.RoleID = uuid3;
        inviteGroupRequest.InviteData_Fields.add(inviteData);
        inviteGroupRequest.isReliable = true;
        SendMessage(inviteGroupRequest);
        this.agentCircuit.HandleChatEvent(ChatterID.getGroupChatterID(this.agentCircuit.getAgentUUID(), uuid2), new SLChatGroupInvitationSentEvent((ChatMessageSource) new ChatMessageSourceUser(uuid), this.agentCircuit.getAgentUUID()), true);
    }

    public void SendGroupNotice(UUID uuid, String str, String str2, SLInventoryEntry sLInventoryEntry) {
        ImprovedInstantMessage improvedInstantMessage = new ImprovedInstantMessage();
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        improvedInstantMessage.MessageBlock_Field.FromGroup = false;
        improvedInstantMessage.MessageBlock_Field.ToAgentID = uuid;
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0;
        improvedInstantMessage.MessageBlock_Field.RegionID = UUIDPool.ZeroUUID;
        improvedInstantMessage.MessageBlock_Field.Position = new LLVector3();
        improvedInstantMessage.MessageBlock_Field.Offline = 0;
        improvedInstantMessage.MessageBlock_Field.Dialog = 32;
        improvedInstantMessage.MessageBlock_Field.ID = UUIDPool.ZeroUUID;
        improvedInstantMessage.MessageBlock_Field.Timestamp = 0;
        improvedInstantMessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo");
        improvedInstantMessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF(str + "|" + str2);
        if (sLInventoryEntry != null) {
            try {
                String serializeToXML = new LLSDMap(new LLSDMap.LLSDMapEntry("item_id", new LLSDUUID(sLInventoryEntry.uuid)), new LLSDMap.LLSDMapEntry("owner_id", new LLSDUUID(sLInventoryEntry.ownerUUID))).serializeToXML();
                improvedInstantMessage.MessageBlock_Field.BinaryBucket = SLMessage.stringToVariableOEM(serializeToXML);
            } catch (IOException e) {
                e.printStackTrace();
                improvedInstantMessage.MessageBlock_Field.BinaryBucket = new byte[0];
            }
        } else {
            improvedInstantMessage.MessageBlock_Field.BinaryBucket = new byte[0];
        }
        improvedInstantMessage.isReliable = true;
        SendMessage(improvedInstantMessage);
    }

    public void SetGroupContribution(UUID uuid, int i) {
        SetGroupContribution setGroupContribution = new SetGroupContribution();
        setGroupContribution.AgentData_Field.AgentID = this.circuitInfo.agentID;
        setGroupContribution.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        setGroupContribution.Data_Field.GroupID = uuid;
        setGroupContribution.Data_Field.Contribution = i;
        setGroupContribution.isReliable = true;
        setGroupContribution.setEventListener(new SLMessageEventListener() {
            public void onMessageAcknowledged(SLMessage sLMessage) {
                AgentDataUpdateRequest agentDataUpdateRequest = new AgentDataUpdateRequest();
                agentDataUpdateRequest.AgentData_Field.AgentID = SLGroupManager.this.circuitInfo.agentID;
                agentDataUpdateRequest.AgentData_Field.SessionID = SLGroupManager.this.circuitInfo.sessionID;
                agentDataUpdateRequest.isReliable = true;
                SLGroupManager.this.SendMessage(agentDataUpdateRequest);
            }

            public void onMessageTimeout(SLMessage sLMessage) {
            }
        });
        SendMessage(setGroupContribution);
    }

    public void SetGroupOptions(UUID uuid, boolean z, boolean z2) {
        SetGroupAcceptNotices setGroupAcceptNotices = new SetGroupAcceptNotices();
        setGroupAcceptNotices.AgentData_Field.AgentID = this.circuitInfo.agentID;
        setGroupAcceptNotices.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        setGroupAcceptNotices.Data_Field.GroupID = uuid;
        setGroupAcceptNotices.Data_Field.AcceptNotices = z;
        setGroupAcceptNotices.NewData_Field.ListInProfile = z2;
        setGroupAcceptNotices.isReliable = true;
        SendMessage(setGroupAcceptNotices);
        this.agentCircuit.getModules().userProfiles.requestAgentDataUpdate();
    }

    public void SetGroupRole(UUID uuid, UUID uuid2) {
        GroupTitleUpdate groupTitleUpdate = new GroupTitleUpdate();
        groupTitleUpdate.AgentData_Field.AgentID = this.circuitInfo.agentID;
        groupTitleUpdate.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        groupTitleUpdate.AgentData_Field.GroupID = uuid;
        groupTitleUpdate.AgentData_Field.TitleRoleID = uuid2;
        groupTitleUpdate.isReliable = true;
        SendMessage(groupTitleUpdate);
        requestGroupTitles(uuid);
    }

    public void SetRoleProperties(final UUID uuid, @Nullable final UUID uuid2, String str, String str2, String str3, long j) {
        Debug.Printf("GroupRole: setting role properties for role %s", uuid2);
        GroupRoleUpdate groupRoleUpdate = new GroupRoleUpdate();
        groupRoleUpdate.AgentData_Field.AgentID = this.circuitInfo.agentID;
        groupRoleUpdate.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        groupRoleUpdate.AgentData_Field.GroupID = uuid;
        GroupRoleUpdate.RoleData roleData = new GroupRoleUpdate.RoleData();
        roleData.RoleID = uuid2 != null ? uuid2 : UUID.randomUUID();
        roleData.Name = SLMessage.stringToVariableOEM(str);
        roleData.Title = SLMessage.stringToVariableOEM(str2);
        roleData.Description = SLMessage.stringToVariableOEM(str3);
        roleData.Powers = j;
        roleData.UpdateType = uuid2 != null ? 3 : 4;
        groupRoleUpdate.RoleData_Fields.add(roleData);
        groupRoleUpdate.isReliable = true;
        groupRoleUpdate.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() {
            public void onMessageAcknowledged(SLMessage sLMessage) {
                Debug.Printf("GroupRole: ack set properties for role %s", uuid2);
                SLGroupManager.this.userManager.getGroupRoles().requestUpdate(uuid);
            }
        });
        SendMessage(groupRoleUpdate);
    }

    public UUID getActiveGroupID() {
        return this.activeGroupID;
    }
}
