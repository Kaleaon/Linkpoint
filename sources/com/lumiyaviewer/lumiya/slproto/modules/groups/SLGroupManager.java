// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.groups;

import com.google.common.base.Strings;
import com.google.common.primitives.UnsignedLong;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.dao.GroupMember;
import com.lumiyaviewer.lumiya.dao.GroupMemberDao;
import com.lumiyaviewer.lumiya.dao.GroupRoleMember;
import com.lumiyaviewer.lumiya.dao.GroupRoleMemberDao;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.react.AsyncCancellableRequestHandler;
import com.lumiyaviewer.lumiya.react.AsyncLimitsRequestHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.SLMessageEventListener;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatGroupInvitationSentEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLJoinLeaveGroupEvent;
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
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.SLUserProfiles;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.SerializableResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUser;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList;
import com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

public class SLGroupManager extends SLModule
{

    private static final int RoleMemberChange_Add = 0;
    private static final int RoleMemberChange_Remove = 1;
    private static final int Role_Update_All = 3;
    private static final int Role_Update_Create = 4;
    private static final int Role_Update_Delete = 5;
    private volatile UUID activeGroupID;
    private final GroupMemberDao groupMemberDao;
    private final String groupMemberDataURL;
    private final RequestHandler groupMemberListHTTPRequestHandler = new AsyncCancellableRequestHandler(GenericHTTPExecutor.getInstance(), new SimpleRequestHandler() {

        final SLGroupManager this$0;

        public volatile void onRequest(Object obj)
        {
            onRequest((UUID)obj);
        }

        public void onRequest(UUID uuid)
        {
            Object obj;
            Object obj1;
            UUID uuid1;
            LLSDNode llsdnode;
            Object obj2;
            int i;
            int j;
            long l;
            long l1;
            boolean flag;
            boolean flag1;
            Debug.Printf("GroupMemberList: [%s] network requesting for group %s", new Object[] {
                Thread.currentThread().getName(), uuid.toString()
            });
            Iterator iterator;
            UUID uuid2;
            int k;
            try
            {
                uuid1 = UUID.randomUUID();
                obj = (new LLSDXMLRequest()).PerformRequest(SLGroupManager._2D_get2(SLGroupManager.this), new LLSDMap(new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry[] {
                    new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("group_id", new LLSDUUID(uuid))
                }));
            }
            // Misplaced declaration of an exception variable
            catch (Object obj)
            {
                Debug.Warning(((Throwable) (obj)));
                SLGroupManager._2D_get3(SLGroupManager.this).onResultError(uuid, ((Throwable) (obj)));
                return;
            }
            if (obj == null) goto _L2; else goto _L1
_L1:
            llsdnode = ((LLSDNode) (obj)).byKey("titles");
            obj1 = ((LLSDNode) (obj)).byKey("defaults");
            obj = ((LLSDNode) (obj)).byKey("members");
            l1 = 0L;
            flag = ((LLSDNode) (obj1)).keyExists("default_powers");
            l = l1;
            if (!flag) goto _L4; else goto _L3
_L3:
            obj1 = ((LLSDNode) (obj1)).byKey("default_powers").asString();
            if (!((String) (obj1)).startsWith("0x")) goto _L6; else goto _L5
_L5:
            l = Long.decode(((String) (obj1))).longValue();
_L4:
            if (!(obj instanceof LLSDMap)) goto _L8; else goto _L7
_L7:
            iterator = ((LLSDMap)obj).entrySet().iterator();
            i = 0;
_L24:
            if (!iterator.hasNext()) goto _L10; else goto _L9
_L9:
            obj = (java.util.Map.Entry)iterator.next();
            uuid2 = UUIDPool.getUUID((String)((java.util.Map.Entry) (obj)).getKey());
            obj2 = (LLSDNode)((java.util.Map.Entry) (obj)).getValue();
            flag1 = false;
            if (!((LLSDNode) (obj2)).keyExists("title")) goto _L12; else goto _L11
_L11:
            obj = llsdnode.byIndex(((LLSDNode) (obj2)).byKey("title").asInt()).asString();
_L25:
            if (!((LLSDNode) (obj2)).keyExists("powers")) goto _L14; else goto _L13
_L13:
            l1 = UnsignedLong.valueOf(((LLSDNode) (obj2)).byKey("powers").asString(), 16).longValue();
_L27:
            if (!((LLSDNode) (obj2)).keyExists("last_login")) goto _L16; else goto _L15
_L15:
            obj1 = ((LLSDNode) (obj2)).byKey("last_login").asString();
_L28:
            if (!((LLSDNode) (obj2)).keyExists("donated_square_meters"))
            {
                break MISSING_BLOCK_LABEL_655;
            }
            j = ((LLSDNode) (obj2)).byKey("donated_square_meters").asInt();
_L29:
            flag = flag1;
            if (!((LLSDNode) (obj2)).keyExists("owner")) goto _L18; else goto _L17
_L17:
            if (!((LLSDNode) (obj2)).byKey("owner").isString()) goto _L20; else goto _L19
_L19:
            obj2 = ((LLSDNode) (obj2)).byKey("owner").asString();
            if (((String) (obj2)).equalsIgnoreCase("y") || ((String) (obj2)).equalsIgnoreCase("yes") || ((String) (obj2)).equalsIgnoreCase("true")) goto _L22; else goto _L21
_L21:
            flag = flag1;
            if (!((String) (obj2)).equalsIgnoreCase("1")) goto _L18; else goto _L22
_L18:
            k = i + 1;
            i = k;
            if (SLGroupManager._2D_get1(SLGroupManager.this) == null) goto _L24; else goto _L23
_L23:
            obj = new GroupMember(uuid, uuid1, uuid2, j, ((String) (obj1)), l1, ((String) (obj)), flag);
            SLGroupManager._2D_get1(SLGroupManager.this).insert(obj);
            i = k;
              goto _L24
_L6:
            l = UnsignedLong.valueOf(((String) (obj1)), 16).longValue();
              goto _L4
            obj1;
            Debug.Warning(((Throwable) (obj1)));
            l = l1;
              goto _L4
_L12:
            obj = llsdnode.byIndex(0).asString();
              goto _L25
_L20:
            flag = flag1;
            if (!((LLSDNode) (obj2)).byKey("owner").isBoolean()) goto _L18; else goto _L26
_L26:
            flag = ((LLSDNode) (obj2)).byKey("owner").asBoolean();
              goto _L18
_L10:
            Debug.Printf("GroupMemberList: parsed list for group: %s requestID %s memberCount %d", new Object[] {
                uuid, uuid1, Integer.valueOf(i)
            });
            SLGroupManager._2D_get3(SLGroupManager.this).onResultData(uuid, uuid1);
            return;
_L2:
            SLGroupManager._2D_get3(SLGroupManager.this).onResultError(uuid, new LLSDException("No data"));
            return;
_L8:
            i = 0;
            if (true) goto _L10; else goto _L22
_L22:
            flag = true;
              goto _L18
_L14:
            l1 = l;
              goto _L27
_L16:
            obj1 = "Unknown";
              goto _L28
            j = 0;
              goto _L29
        }

            
            {
                this$0 = SLGroupManager.this;
                super();
            }
    });
    private final RequestHandler groupMemberListRequestHandler;
    private ResultHandler groupMemberListResultHandler;
    private final RequestHandler groupProfileRequestHandler;
    private ResultHandler groupProfileResultHandler;
    private final GroupRoleMemberDao groupRoleMemberDao;
    private final RequestHandler groupRoleMemberListRequestHandler;
    private ResultHandler groupRoleMemberListResultHandler;
    private final RequestHandler groupRolesRequestHandler;
    private ResultHandler groupRolesResultHandler;
    private final RequestHandler groupTitlesRequestHandler;
    private ResultHandler groupTitlesResultHandler;
    private final UserManager userManager;

    static SLCircuitInfo _2D_get0(SLGroupManager slgroupmanager)
    {
        return slgroupmanager.circuitInfo;
    }

    static GroupMemberDao _2D_get1(SLGroupManager slgroupmanager)
    {
        return slgroupmanager.groupMemberDao;
    }

    static String _2D_get2(SLGroupManager slgroupmanager)
    {
        return slgroupmanager.groupMemberDataURL;
    }

    static ResultHandler _2D_get3(SLGroupManager slgroupmanager)
    {
        return slgroupmanager.groupMemberListResultHandler;
    }

    static UserManager _2D_get4(SLGroupManager slgroupmanager)
    {
        return slgroupmanager.userManager;
    }

    static void _2D_wrap0(SLGroupManager slgroupmanager, UUID uuid)
    {
        slgroupmanager.RequestGroupProfileData(uuid);
    }

    static void _2D_wrap1(SLGroupManager slgroupmanager, UUID uuid)
    {
        slgroupmanager.requestGroupTitles(uuid);
    }

    public SLGroupManager(SLAgentCircuit slagentcircuit)
    {
        super(slagentcircuit);
        activeGroupID = null;
        groupProfileRequestHandler = new AsyncLimitsRequestHandler(agentCircuit, new SimpleRequestHandler() {

            final SLGroupManager this$0;

            public volatile void onRequest(Object obj)
            {
                onRequest((UUID)obj);
            }

            public void onRequest(UUID uuid)
            {
                Debug.Printf("GroupManager: [%s] network requesting for group %s", new Object[] {
                    Thread.currentThread().getName(), uuid.toString()
                });
                SLGroupManager._2D_wrap0(SLGroupManager.this, uuid);
            }

            
            {
                this$0 = SLGroupManager.this;
                super();
            }
        }, false, 3, 15000L);
        groupTitlesRequestHandler = new AsyncLimitsRequestHandler(agentCircuit, new SimpleRequestHandler() {

            final SLGroupManager this$0;

            public volatile void onRequest(Object obj)
            {
                onRequest((UUID)obj);
            }

            public void onRequest(UUID uuid)
            {
                Debug.Printf("GroupTitles: [%s] network requesting for group %s", new Object[] {
                    Thread.currentThread().getName(), uuid.toString()
                });
                SLGroupManager._2D_wrap1(SLGroupManager.this, uuid);
            }

            
            {
                this$0 = SLGroupManager.this;
                super();
            }
        }, false, 3, 15000L);
        groupRolesRequestHandler = new AsyncLimitsRequestHandler(agentCircuit, new SimpleRequestHandler() {

            final SLGroupManager this$0;

            public volatile void onRequest(Object obj)
            {
                onRequest((UUID)obj);
            }

            public void onRequest(UUID uuid)
            {
                Debug.Printf("GroupRoles: [%s] network requesting for group %s", new Object[] {
                    Thread.currentThread().getName(), uuid.toString()
                });
                GroupRoleDataRequest grouproledatarequest = new GroupRoleDataRequest();
                grouproledatarequest.AgentData_Field.AgentID = SLGroupManager._2D_get0(SLGroupManager.this).agentID;
                grouproledatarequest.AgentData_Field.SessionID = SLGroupManager._2D_get0(SLGroupManager.this).sessionID;
                grouproledatarequest.GroupData_Field.GroupID = uuid;
                grouproledatarequest.GroupData_Field.RequestID = UUID.randomUUID();
                grouproledatarequest.isReliable = true;
                SendMessage(grouproledatarequest);
            }

            
            {
                this$0 = SLGroupManager.this;
                super();
            }
        }, false, 3, 15000L);
        groupRoleMemberListRequestHandler = new AsyncLimitsRequestHandler(agentCircuit, new SimpleRequestHandler() {

            final SLGroupManager this$0;

            public volatile void onRequest(Object obj)
            {
                onRequest((UUID)obj);
            }

            public void onRequest(UUID uuid)
            {
                Debug.Printf("GroupRoleMemberList: [%s] network requesting for %s", new Object[] {
                    Thread.currentThread().getName(), uuid.toString()
                });
                GroupRoleMembersRequest grouprolemembersrequest = new GroupRoleMembersRequest();
                grouprolemembersrequest.AgentData_Field.AgentID = SLGroupManager._2D_get0(SLGroupManager.this).agentID;
                grouprolemembersrequest.AgentData_Field.SessionID = SLGroupManager._2D_get0(SLGroupManager.this).sessionID;
                grouprolemembersrequest.GroupData_Field.GroupID = uuid;
                grouprolemembersrequest.GroupData_Field.RequestID = UUID.randomUUID();
                grouprolemembersrequest.isReliable = true;
                SendMessage(grouprolemembersrequest);
            }

            
            {
                this$0 = SLGroupManager.this;
                super();
            }
        }, false, 3, 15000L);
        groupMemberListRequestHandler = new AsyncLimitsRequestHandler(agentCircuit, new SimpleRequestHandler() {

            final SLGroupManager this$0;

            public volatile void onRequest(Object obj)
            {
                onRequest((UUID)obj);
            }

            public void onRequest(UUID uuid)
            {
                Debug.Printf("GroupMemberList: [%s] network requesting for group %s", new Object[] {
                    Thread.currentThread().getName(), uuid.toString()
                });
                GroupMembersRequest groupmembersrequest = new GroupMembersRequest();
                groupmembersrequest.AgentData_Field.AgentID = SLGroupManager._2D_get0(SLGroupManager.this).agentID;
                groupmembersrequest.AgentData_Field.SessionID = SLGroupManager._2D_get0(SLGroupManager.this).sessionID;
                groupmembersrequest.GroupData_Field.GroupID = uuid;
                groupmembersrequest.GroupData_Field.RequestID = UUID.randomUUID();
                groupmembersrequest.isReliable = true;
                SendMessage(groupmembersrequest);
            }

            
            {
                this$0 = SLGroupManager.this;
                super();
            }
        }, false, 3, 15000L);
        groupMemberDataURL = slagentcircuit.getCaps().getCapability(com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.GroupMemberData);
        userManager = UserManager.getUserManager(slagentcircuit.circuitInfo.agentID);
        if (userManager != null)
        {
            groupMemberDao = userManager.getDaoSession().getGroupMemberDao();
            groupRoleMemberDao = userManager.getDaoSession().getGroupRoleMemberDao();
            return;
        } else
        {
            groupMemberDao = null;
            groupRoleMemberDao = null;
            return;
        }
    }

    private void RequestGroupProfileData(UUID uuid)
    {
        GroupProfileRequest groupprofilerequest = new GroupProfileRequest();
        groupprofilerequest.AgentData_Field.AgentID = circuitInfo.agentID;
        groupprofilerequest.AgentData_Field.SessionID = circuitInfo.sessionID;
        groupprofilerequest.GroupData_Field.GroupID = uuid;
        groupprofilerequest.isReliable = true;
        SendMessage(groupprofilerequest);
    }

    private void RequestRoleMemberChange(final UUID groupID, UUID uuid, UUID uuid1, int i)
    {
        GroupRoleChanges grouprolechanges = new GroupRoleChanges();
        grouprolechanges.AgentData_Field.AgentID = circuitInfo.agentID;
        grouprolechanges.AgentData_Field.SessionID = circuitInfo.sessionID;
        grouprolechanges.AgentData_Field.GroupID = groupID;
        com.lumiyaviewer.lumiya.slproto.messages.GroupRoleChanges.RoleChange rolechange = new com.lumiyaviewer.lumiya.slproto.messages.GroupRoleChanges.RoleChange();
        rolechange.RoleID = uuid;
        rolechange.MemberID = uuid1;
        rolechange.Change = i;
        grouprolechanges.RoleChange_Fields.add(rolechange);
        grouprolechanges.isReliable = true;
        grouprolechanges.setEventListener(new SLMessageEventListener() {

            final SLGroupManager this$0;
            final UUID val$groupID;

            public void onMessageAcknowledged(SLMessage slmessage)
            {
                SLGroupManager._2D_get4(SLGroupManager.this).getChatterList().getGroupManager().requestGroupRoleMembersRefresh(groupID);
            }

            public void onMessageTimeout(SLMessage slmessage)
            {
            }

            
            {
                this$0 = SLGroupManager.this;
                groupID = uuid;
                super();
            }
        });
        SendMessage(grouprolechanges);
    }

    private void requestGroupTitles(UUID uuid)
    {
        GroupTitlesRequest grouptitlesrequest = new GroupTitlesRequest();
        grouptitlesrequest.AgentData_Field.AgentID = circuitInfo.agentID;
        grouptitlesrequest.AgentData_Field.SessionID = circuitInfo.sessionID;
        grouptitlesrequest.AgentData_Field.GroupID = uuid;
        grouptitlesrequest.AgentData_Field.RequestID = UUID.randomUUID();
        grouptitlesrequest.isReliable = true;
        SendMessage(grouptitlesrequest);
    }

    public void AcceptGroupInvite(UUID uuid, UUID uuid1, boolean flag)
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
        uuid = improvedinstantmessage.MessageBlock_Field;
        int i;
        if (flag)
        {
            i = 35;
        } else
        {
            i = 36;
        }
        uuid.Dialog = i;
        improvedinstantmessage.MessageBlock_Field.ID = uuid1;
        improvedinstantmessage.MessageBlock_Field.Timestamp = 0;
        improvedinstantmessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo");
        improvedinstantmessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF("todo");
        improvedinstantmessage.MessageBlock_Field.BinaryBucket = new byte[0];
        improvedinstantmessage.isReliable = true;
        SendMessage(improvedinstantmessage);
    }

    public void ActivateGroup(UUID uuid)
    {
        ActivateGroup activategroup = new ActivateGroup();
        activategroup.AgentData_Field.AgentID = circuitInfo.agentID;
        activategroup.AgentData_Field.SessionID = circuitInfo.sessionID;
        activategroup.AgentData_Field.GroupID = uuid;
        activategroup.isReliable = true;
        SendMessage(activategroup);
    }

    public void AddMemberToRole(UUID uuid, UUID uuid1, UUID uuid2)
    {
        RequestRoleMemberChange(uuid, uuid1, uuid2, 0);
    }

    public void DeleteRole(final UUID groupID, UUID uuid)
    {
        GroupRoleUpdate grouproleupdate = new GroupRoleUpdate();
        grouproleupdate.AgentData_Field.AgentID = circuitInfo.agentID;
        grouproleupdate.AgentData_Field.SessionID = circuitInfo.sessionID;
        grouproleupdate.AgentData_Field.GroupID = groupID;
        com.lumiyaviewer.lumiya.slproto.messages.GroupRoleUpdate.RoleData roledata = new com.lumiyaviewer.lumiya.slproto.messages.GroupRoleUpdate.RoleData();
        roledata.RoleID = uuid;
        roledata.Name = SLMessage.stringToVariableOEM("");
        roledata.Title = SLMessage.stringToVariableOEM("");
        roledata.Description = SLMessage.stringToVariableOEM("");
        roledata.Powers = 0L;
        roledata.UpdateType = 5;
        grouproleupdate.RoleData_Fields.add(roledata);
        grouproleupdate.isReliable = true;
        grouproleupdate.setEventListener(new com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener() {

            final SLGroupManager this$0;
            final UUID val$groupID;

            public void onMessageAcknowledged(SLMessage slmessage)
            {
                SLGroupManager._2D_get4(SLGroupManager.this).getGroupRoles().requestUpdate(groupID);
            }

            
            {
                this$0 = SLGroupManager.this;
                groupID = uuid;
                super();
            }
        });
        SendMessage(grouproleupdate);
    }

    public void HandleCircuitReady()
    {
        if (userManager != null)
        {
            groupProfileResultHandler = userManager.getCachedGroupProfiles().getRequestSource().attachRequestHandler(groupProfileRequestHandler);
            groupTitlesResultHandler = userManager.getGroupTitles().getRequestSource().attachRequestHandler(groupTitlesRequestHandler);
            groupRolesResultHandler = userManager.getGroupRoles().getRequestSource().attachRequestHandler(groupRolesRequestHandler);
            RequestSource requestsource = userManager.getChatterList().getGroupManager().getGroupMemberDataSetRequestSource();
            RequestHandler requesthandler;
            if (Strings.isNullOrEmpty(groupMemberDataURL))
            {
                requesthandler = groupMemberListRequestHandler;
            } else
            {
                requesthandler = groupMemberListHTTPRequestHandler;
            }
            groupMemberListResultHandler = requestsource.attachRequestHandler(requesthandler);
            groupRoleMemberListResultHandler = userManager.getChatterList().getGroupManager().getGroupRoleMemberDataSetRequestSource().attachRequestHandler(groupRoleMemberListRequestHandler);
        }
    }

    public void HandleCloseCircuit()
    {
        if (userManager != null)
        {
            userManager.getCachedGroupProfiles().getRequestSource().detachRequestHandler(groupProfileRequestHandler);
            userManager.getGroupTitles().getRequestSource().detachRequestHandler(groupTitlesRequestHandler);
            userManager.getGroupRoles().getRequestSource().detachRequestHandler(groupRolesRequestHandler);
            userManager.getChatterList().getGroupManager().getGroupMemberDataSetRequestSource().detachRequestHandler(groupMemberListRequestHandler);
        }
    }

    public void HandleEjectGroupMemberReply(EjectGroupMemberReply ejectgroupmemberreply)
    {
        userManager.getChatterList().getGroupManager().requestRefreshMemberList(ejectgroupmemberreply.GroupData_Field.GroupID);
    }

    public void HandleGroupMembersReply(GroupMembersReply groupmembersreply)
    {
        Debug.Printf("GroupMember: got reply, %d members, memberCount %d", new Object[] {
            Integer.valueOf(groupmembersreply.MemberData_Fields.size()), Integer.valueOf(groupmembersreply.GroupData_Field.MemberCount)
        });
        com.lumiyaviewer.lumiya.slproto.messages.GroupMembersReply.MemberData memberdata;
        for (Iterator iterator = groupmembersreply.MemberData_Fields.iterator(); iterator.hasNext(); Debug.Printf("GroupMember: userID = %s", new Object[] {
    memberdata.AgentID
}))
        {
            memberdata = (com.lumiyaviewer.lumiya.slproto.messages.GroupMembersReply.MemberData)iterator.next();
            GroupMember groupmember = new GroupMember(groupmembersreply.GroupData_Field.GroupID, groupmembersreply.GroupData_Field.RequestID, memberdata.AgentID, memberdata.Contribution, SLMessage.stringFromVariableOEM(memberdata.OnlineStatus), memberdata.AgentPowers, SLMessage.stringFromVariableOEM(memberdata.Title), memberdata.IsOwner);
            groupMemberDao.insert(groupmember);
        }

        long l = groupMemberDao.queryBuilder().where(com.lumiyaviewer.lumiya.dao.GroupMemberDao.Properties.GroupID.eq(groupmembersreply.GroupData_Field.GroupID), new WhereCondition[] {
            com.lumiyaviewer.lumiya.dao.GroupMemberDao.Properties.RequestID.eq(groupmembersreply.GroupData_Field.RequestID)
        }).count();
        Debug.Printf("GroupMemberList: count = %d", new Object[] {
            Long.valueOf(l)
        });
        if (l >= (long)groupmembersreply.GroupData_Field.MemberCount)
        {
            groupMemberListResultHandler.onResultData(groupmembersreply.GroupData_Field.GroupID, groupmembersreply.GroupData_Field.RequestID);
        }
    }

    public void HandleGroupProfileReply(GroupProfileReply groupprofilereply)
    {
        if (groupProfileResultHandler != null)
        {
            groupProfileResultHandler.onResultData(groupprofilereply.GroupData_Field.GroupID, groupprofilereply);
        }
    }

    public void HandleGroupRoleDataReply(GroupRoleDataReply grouproledatareply)
    {
        if (groupRolesResultHandler != null)
        {
            groupRolesResultHandler.onResultData(grouproledatareply.GroupData_Field.GroupID, grouproledatareply);
        }
    }

    public void HandleGroupRoleMembersReply(GroupRoleMembersReply grouprolemembersreply)
    {
        Debug.Printf("GroupRoleMember: got reply, %d members, total pairs %d", new Object[] {
            Integer.valueOf(grouprolemembersreply.MemberData_Fields.size()), Integer.valueOf(grouprolemembersreply.AgentData_Field.TotalPairs)
        });
        Object obj;
        for (Iterator iterator = grouprolemembersreply.MemberData_Fields.iterator(); iterator.hasNext(); groupRoleMemberDao.insert(obj))
        {
            obj = (com.lumiyaviewer.lumiya.slproto.messages.GroupRoleMembersReply.MemberData)iterator.next();
            obj = new GroupRoleMember(grouprolemembersreply.AgentData_Field.GroupID, grouprolemembersreply.AgentData_Field.RequestID, ((com.lumiyaviewer.lumiya.slproto.messages.GroupRoleMembersReply.MemberData) (obj)).RoleID, ((com.lumiyaviewer.lumiya.slproto.messages.GroupRoleMembersReply.MemberData) (obj)).MemberID);
        }

        long l = groupRoleMemberDao.queryBuilder().where(com.lumiyaviewer.lumiya.dao.GroupRoleMemberDao.Properties.GroupID.eq(grouprolemembersreply.AgentData_Field.GroupID), new WhereCondition[] {
            com.lumiyaviewer.lumiya.dao.GroupRoleMemberDao.Properties.RequestID.eq(grouprolemembersreply.AgentData_Field.RequestID)
        }).count();
        Debug.Printf("GroupRoleMemberList: count = %d", new Object[] {
            Long.valueOf(l)
        });
        if (l >= (long)grouprolemembersreply.AgentData_Field.TotalPairs)
        {
            groupRoleMemberListResultHandler.onResultData(grouprolemembersreply.AgentData_Field.GroupID, grouprolemembersreply.AgentData_Field.RequestID);
        }
    }

    public void HandleGroupTitlesReply(GroupTitlesReply grouptitlesreply)
    {
        if (groupTitlesResultHandler != null)
        {
            groupTitlesResultHandler.onResultData(grouptitlesreply.AgentData_Field.GroupID, grouptitlesreply);
        }
    }

    public void HandleJoinGroupReply(JoinGroupReply joingroupreply)
    {
        AgentDataUpdateRequest agentdataupdaterequest = new AgentDataUpdateRequest();
        agentdataupdaterequest.AgentData_Field.AgentID = circuitInfo.agentID;
        agentdataupdaterequest.AgentData_Field.SessionID = circuitInfo.sessionID;
        agentdataupdaterequest.isReliable = true;
        SendMessage(agentdataupdaterequest);
        eventBus.publish(new SLJoinLeaveGroupEvent(joingroupreply.GroupData_Field.GroupID, true, joingroupreply.GroupData_Field.Success));
    }

    public void HandleLeaveGroupReply(LeaveGroupReply leavegroupreply)
    {
        AgentDataUpdateRequest agentdataupdaterequest = new AgentDataUpdateRequest();
        agentdataupdaterequest.AgentData_Field.AgentID = circuitInfo.agentID;
        agentdataupdaterequest.AgentData_Field.SessionID = circuitInfo.sessionID;
        agentdataupdaterequest.isReliable = true;
        SendMessage(agentdataupdaterequest);
        eventBus.publish(new SLJoinLeaveGroupEvent(leavegroupreply.GroupData_Field.GroupID, false, leavegroupreply.GroupData_Field.Success));
    }

    public void RemoveMemberFromRole(UUID uuid, UUID uuid1, UUID uuid2)
    {
        RequestRoleMemberChange(uuid, uuid1, uuid2, 1);
    }

    public void RequestEjectFromGroup(UUID uuid, UUID uuid1)
    {
        EjectGroupMemberRequest ejectgroupmemberrequest = new EjectGroupMemberRequest();
        ejectgroupmemberrequest.AgentData_Field.AgentID = circuitInfo.agentID;
        ejectgroupmemberrequest.AgentData_Field.SessionID = circuitInfo.sessionID;
        ejectgroupmemberrequest.GroupData_Field.GroupID = uuid;
        uuid = new com.lumiyaviewer.lumiya.slproto.messages.EjectGroupMemberRequest.EjectData();
        uuid.EjecteeID = uuid1;
        ejectgroupmemberrequest.EjectData_Fields.add(uuid);
        ejectgroupmemberrequest.isReliable = true;
        SendMessage(ejectgroupmemberrequest);
    }

    public void RequestJoinGroup(UUID uuid)
    {
        JoinGroupRequest joingrouprequest = new JoinGroupRequest();
        joingrouprequest.AgentData_Field.AgentID = circuitInfo.agentID;
        joingrouprequest.AgentData_Field.SessionID = circuitInfo.sessionID;
        joingrouprequest.GroupData_Field.GroupID = uuid;
        joingrouprequest.isReliable = true;
        SendMessage(joingrouprequest);
    }

    public void RequestLeaveGroup(UUID uuid)
    {
        LeaveGroupRequest leavegrouprequest = new LeaveGroupRequest();
        leavegrouprequest.AgentData_Field.AgentID = circuitInfo.agentID;
        leavegrouprequest.AgentData_Field.SessionID = circuitInfo.sessionID;
        leavegrouprequest.GroupData_Field.GroupID = uuid;
        leavegrouprequest.isReliable = true;
        SendMessage(leavegrouprequest);
    }

    public void RequestMemberRoleChanges(final UUID groupUUID, final UUID memberID, Collection collection, Collection collection1)
    {
        final boolean isMyRoles = circuitInfo.agentID.equals(memberID);
        GroupRoleChanges grouprolechanges = new GroupRoleChanges();
        grouprolechanges.AgentData_Field.AgentID = circuitInfo.agentID;
        grouprolechanges.AgentData_Field.SessionID = circuitInfo.sessionID;
        grouprolechanges.AgentData_Field.GroupID = groupUUID;
        com.lumiyaviewer.lumiya.slproto.messages.GroupRoleChanges.RoleChange rolechange1;
        for (collection = collection.iterator(); collection.hasNext(); grouprolechanges.RoleChange_Fields.add(rolechange1))
        {
            UUID uuid = (UUID)collection.next();
            Debug.Printf("GroupRoleChange: groupID %s memberID %s add %s", new Object[] {
                groupUUID, memberID, uuid
            });
            rolechange1 = new com.lumiyaviewer.lumiya.slproto.messages.GroupRoleChanges.RoleChange();
            rolechange1.RoleID = uuid;
            rolechange1.MemberID = memberID;
            rolechange1.Change = 0;
        }

        com.lumiyaviewer.lumiya.slproto.messages.GroupRoleChanges.RoleChange rolechange;
        for (collection = collection1.iterator(); collection.hasNext(); grouprolechanges.RoleChange_Fields.add(rolechange))
        {
            collection1 = (UUID)collection.next();
            Debug.Printf("GroupRoleChange: groupID %s memberID %s remove %s", new Object[] {
                groupUUID, memberID, collection1
            });
            rolechange = new com.lumiyaviewer.lumiya.slproto.messages.GroupRoleChanges.RoleChange();
            rolechange.RoleID = collection1;
            rolechange.MemberID = memberID;
            rolechange.Change = 1;
        }

        grouprolechanges.isReliable = true;
        grouprolechanges.setEventListener(new SLMessageEventListener() {

            final SLGroupManager this$0;
            final UUID val$groupUUID;
            final boolean val$isMyRoles;
            final UUID val$memberID;

            public void onMessageAcknowledged(SLMessage slmessage)
            {
                SLGroupManager._2D_get4(SLGroupManager.this).getChatterList().getGroupManager().requestGroupRoleMembersRefresh(groupUUID);
                if (isMyRoles)
                {
                    SLGroupManager._2D_get4(SLGroupManager.this).getCachedGroupProfiles().requestUpdate(groupUUID);
                    SLGroupManager._2D_get4(SLGroupManager.this).getGroupTitles().requestUpdate(groupUUID);
                    SLGroupManager._2D_get4(SLGroupManager.this).getAvatarGroupLists().requestUpdate(memberID);
                }
            }

            public void onMessageTimeout(SLMessage slmessage)
            {
            }

            
            {
                this$0 = SLGroupManager.this;
                groupUUID = uuid;
                isMyRoles = flag;
                memberID = uuid1;
                super();
            }
        });
        SendMessage(grouprolechanges);
    }

    public void SendGroupInvite(UUID uuid, UUID uuid1, UUID uuid2)
    {
        InviteGroupRequest invitegrouprequest = new InviteGroupRequest();
        invitegrouprequest.AgentData_Field.AgentID = circuitInfo.agentID;
        invitegrouprequest.AgentData_Field.SessionID = circuitInfo.sessionID;
        invitegrouprequest.GroupData_Field.GroupID = uuid1;
        com.lumiyaviewer.lumiya.slproto.messages.InviteGroupRequest.InviteData invitedata = new com.lumiyaviewer.lumiya.slproto.messages.InviteGroupRequest.InviteData();
        invitedata.InviteeID = uuid;
        invitedata.RoleID = uuid2;
        invitegrouprequest.InviteData_Fields.add(invitedata);
        invitegrouprequest.isReliable = true;
        SendMessage(invitegrouprequest);
        agentCircuit.HandleChatEvent(ChatterID.getGroupChatterID(agentCircuit.getAgentUUID(), uuid1), new SLChatGroupInvitationSentEvent(new ChatMessageSourceUser(uuid), agentCircuit.getAgentUUID()), true);
    }

    public void SendGroupNotice(UUID uuid, String s, String s1, SLInventoryEntry slinventoryentry)
    {
        ImprovedInstantMessage improvedinstantmessage = new ImprovedInstantMessage();
        improvedinstantmessage.AgentData_Field.AgentID = circuitInfo.agentID;
        improvedinstantmessage.AgentData_Field.SessionID = circuitInfo.sessionID;
        improvedinstantmessage.MessageBlock_Field.FromGroup = false;
        improvedinstantmessage.MessageBlock_Field.ToAgentID = uuid;
        improvedinstantmessage.MessageBlock_Field.ParentEstateID = 0;
        improvedinstantmessage.MessageBlock_Field.RegionID = UUIDPool.ZeroUUID;
        improvedinstantmessage.MessageBlock_Field.Position = new LLVector3();
        improvedinstantmessage.MessageBlock_Field.Offline = 0;
        improvedinstantmessage.MessageBlock_Field.Dialog = 32;
        improvedinstantmessage.MessageBlock_Field.ID = UUIDPool.ZeroUUID;
        improvedinstantmessage.MessageBlock_Field.Timestamp = 0;
        improvedinstantmessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo");
        improvedinstantmessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF((new StringBuilder()).append(s).append("|").append(s1).toString());
        if (slinventoryentry != null)
        {
            uuid = new LLSDMap(new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry[] {
                new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("item_id", new LLSDUUID(slinventoryentry.uuid)), new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("owner_id", new LLSDUUID(slinventoryentry.ownerUUID))
            });
            try
            {
                uuid = uuid.serializeToXML();
                improvedinstantmessage.MessageBlock_Field.BinaryBucket = SLMessage.stringToVariableOEM(uuid);
            }
            // Misplaced declaration of an exception variable
            catch (UUID uuid)
            {
                uuid.printStackTrace();
                improvedinstantmessage.MessageBlock_Field.BinaryBucket = new byte[0];
            }
        } else
        {
            improvedinstantmessage.MessageBlock_Field.BinaryBucket = new byte[0];
        }
        improvedinstantmessage.isReliable = true;
        SendMessage(improvedinstantmessage);
    }

    public void SetGroupContribution(UUID uuid, int i)
    {
        SetGroupContribution setgroupcontribution = new SetGroupContribution();
        setgroupcontribution.AgentData_Field.AgentID = circuitInfo.agentID;
        setgroupcontribution.AgentData_Field.SessionID = circuitInfo.sessionID;
        setgroupcontribution.Data_Field.GroupID = uuid;
        setgroupcontribution.Data_Field.Contribution = i;
        setgroupcontribution.isReliable = true;
        setgroupcontribution.setEventListener(new SLMessageEventListener() {

            final SLGroupManager this$0;

            public void onMessageAcknowledged(SLMessage slmessage)
            {
                slmessage = new AgentDataUpdateRequest();
                ((AgentDataUpdateRequest) (slmessage)).AgentData_Field.AgentID = SLGroupManager._2D_get0(SLGroupManager.this).agentID;
                ((AgentDataUpdateRequest) (slmessage)).AgentData_Field.SessionID = SLGroupManager._2D_get0(SLGroupManager.this).sessionID;
                slmessage.isReliable = true;
                SendMessage(slmessage);
            }

            public void onMessageTimeout(SLMessage slmessage)
            {
            }

            
            {
                this$0 = SLGroupManager.this;
                super();
            }
        });
        SendMessage(setgroupcontribution);
    }

    public void SetGroupOptions(UUID uuid, boolean flag, boolean flag1)
    {
        SetGroupAcceptNotices setgroupacceptnotices = new SetGroupAcceptNotices();
        setgroupacceptnotices.AgentData_Field.AgentID = circuitInfo.agentID;
        setgroupacceptnotices.AgentData_Field.SessionID = circuitInfo.sessionID;
        setgroupacceptnotices.Data_Field.GroupID = uuid;
        setgroupacceptnotices.Data_Field.AcceptNotices = flag;
        setgroupacceptnotices.NewData_Field.ListInProfile = flag1;
        setgroupacceptnotices.isReliable = true;
        SendMessage(setgroupacceptnotices);
        agentCircuit.getModules().userProfiles.requestAgentDataUpdate();
    }

    public void SetGroupRole(UUID uuid, UUID uuid1)
    {
        GroupTitleUpdate grouptitleupdate = new GroupTitleUpdate();
        grouptitleupdate.AgentData_Field.AgentID = circuitInfo.agentID;
        grouptitleupdate.AgentData_Field.SessionID = circuitInfo.sessionID;
        grouptitleupdate.AgentData_Field.GroupID = uuid;
        grouptitleupdate.AgentData_Field.TitleRoleID = uuid1;
        grouptitleupdate.isReliable = true;
        SendMessage(grouptitleupdate);
        requestGroupTitles(uuid);
    }

    public void SetRoleProperties(final UUID groupID, final UUID roleID, String s, String s1, String s2, long l)
    {
        Debug.Printf("GroupRole: setting role properties for role %s", new Object[] {
            roleID
        });
        GroupRoleUpdate grouproleupdate = new GroupRoleUpdate();
        grouproleupdate.AgentData_Field.AgentID = circuitInfo.agentID;
        grouproleupdate.AgentData_Field.SessionID = circuitInfo.sessionID;
        grouproleupdate.AgentData_Field.GroupID = groupID;
        com.lumiyaviewer.lumiya.slproto.messages.GroupRoleUpdate.RoleData roledata = new com.lumiyaviewer.lumiya.slproto.messages.GroupRoleUpdate.RoleData();
        UUID uuid;
        int i;
        if (roleID != null)
        {
            uuid = roleID;
        } else
        {
            uuid = UUID.randomUUID();
        }
        roledata.RoleID = uuid;
        roledata.Name = SLMessage.stringToVariableOEM(s);
        roledata.Title = SLMessage.stringToVariableOEM(s1);
        roledata.Description = SLMessage.stringToVariableOEM(s2);
        roledata.Powers = l;
        if (roleID != null)
        {
            i = 3;
        } else
        {
            i = 4;
        }
        roledata.UpdateType = i;
        grouproleupdate.RoleData_Fields.add(roledata);
        grouproleupdate.isReliable = true;
        grouproleupdate.setEventListener(new com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener() {

            final SLGroupManager this$0;
            final UUID val$groupID;
            final UUID val$roleID;

            public void onMessageAcknowledged(SLMessage slmessage)
            {
                Debug.Printf("GroupRole: ack set properties for role %s", new Object[] {
                    roleID
                });
                SLGroupManager._2D_get4(SLGroupManager.this).getGroupRoles().requestUpdate(groupID);
            }

            
            {
                this$0 = SLGroupManager.this;
                roleID = uuid;
                groupID = uuid1;
                super();
            }
        });
        SendMessage(grouproleupdate);
    }

    public UUID getActiveGroupID()
    {
        return activeGroupID;
    }
}
