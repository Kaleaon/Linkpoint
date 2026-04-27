// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.groups;

import de.greenrobot.dao.AbstractDao;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitleUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.SetGroupAcceptNotices;
import com.lumiyaviewer.lumiya.slproto.messages.SetGroupContribution;
import java.io.IOException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatGroupInvitationSentEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUser;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.messages.InviteGroupRequest;
import java.util.Collection;
import com.lumiyaviewer.lumiya.slproto.messages.LeaveGroupRequest;
import com.lumiyaviewer.lumiya.slproto.messages.JoinGroupRequest;
import com.lumiyaviewer.lumiya.slproto.messages.EjectGroupMemberRequest;
import com.lumiyaviewer.lumiya.slproto.messages.LeaveGroupReply;
import com.lumiyaviewer.lumiya.slproto.events.SLJoinLeaveGroupEvent;
import com.lumiyaviewer.lumiya.slproto.messages.AgentDataUpdateRequest;
import com.lumiyaviewer.lumiya.slproto.messages.JoinGroupReply;
import com.lumiyaviewer.lumiya.dao.GroupRoleMember;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleMembersReply;
import java.util.Iterator;
import de.greenrobot.dao.query.WhereCondition;
import com.lumiyaviewer.lumiya.dao.GroupMember;
import com.lumiyaviewer.lumiya.slproto.messages.GroupMembersReply;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.messages.EjectGroupMemberReply;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.ActivateGroup;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesRequest;
import com.lumiyaviewer.lumiya.slproto.SLMessageEventListener;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleChanges;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileRequest;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.react.AsyncCancellableRequestHandler;
import com.lumiyaviewer.lumiya.slproto.https.GenericHTTPExecutor;
import com.lumiyaviewer.lumiya.slproto.messages.GroupMembersRequest;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleMembersRequest;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataRequest;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.react.AsyncLimitsRequestHandler;
import com.lumiyaviewer.lumiya.Debug;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.dao.GroupRoleMemberDao;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.dao.GroupMemberDao;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;

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
    private final RequestHandler<UUID> groupMemberListHTTPRequestHandler;
    private final RequestHandler<UUID> groupMemberListRequestHandler;
    private ResultHandler<UUID, UUID> groupMemberListResultHandler;
    private final RequestHandler<UUID> groupProfileRequestHandler;
    private ResultHandler<UUID, GroupProfileReply> groupProfileResultHandler;
    private final GroupRoleMemberDao groupRoleMemberDao;
    private final RequestHandler<UUID> groupRoleMemberListRequestHandler;
    private ResultHandler<UUID, UUID> groupRoleMemberListResultHandler;
    private final RequestHandler<UUID> groupRolesRequestHandler;
    private ResultHandler<UUID, GroupRoleDataReply> groupRolesResultHandler;
    private final RequestHandler<UUID> groupTitlesRequestHandler;
    private ResultHandler<UUID, GroupTitlesReply> groupTitlesResultHandler;
    private final UserManager userManager;
    
    public SLGroupManager(final SLAgentCircuit slAgentCircuit) {
        super(slAgentCircuit);
        this.activeGroupID = null;
        this.groupProfileRequestHandler = new AsyncLimitsRequestHandler<UUID>(this.agentCircuit, new SimpleRequestHandler<UUID>() {
            @Override
            public void onRequest(@Nonnull final UUID uuid) {
                Debug.Printf("GroupManager: [%s] network requesting for group %s", Thread.currentThread().getName(), uuid.toString());
                SLGroupManager.this.RequestGroupProfileData(uuid);
            }
        }, false, 3, 15000L);
        this.groupTitlesRequestHandler = new AsyncLimitsRequestHandler<UUID>(this.agentCircuit, new SimpleRequestHandler<UUID>() {
            @Override
            public void onRequest(@Nonnull final UUID uuid) {
                Debug.Printf("GroupTitles: [%s] network requesting for group %s", Thread.currentThread().getName(), uuid.toString());
                SLGroupManager.this.requestGroupTitles(uuid);
            }
        }, false, 3, 15000L);
        this.groupRolesRequestHandler = new AsyncLimitsRequestHandler<UUID>(this.agentCircuit, new SimpleRequestHandler<UUID>() {
            @Override
            public void onRequest(@Nonnull final UUID groupID) {
                Debug.Printf("GroupRoles: [%s] network requesting for group %s", Thread.currentThread().getName(), groupID.toString());
                final GroupRoleDataRequest groupRoleDataRequest = new GroupRoleDataRequest();
                groupRoleDataRequest.AgentData_Field.AgentID = SLGroupManager.this.circuitInfo.agentID;
                groupRoleDataRequest.AgentData_Field.SessionID = SLGroupManager.this.circuitInfo.sessionID;
                groupRoleDataRequest.GroupData_Field.GroupID = groupID;
                groupRoleDataRequest.GroupData_Field.RequestID = UUID.randomUUID();
                groupRoleDataRequest.isReliable = true;
                SLGroupManager.this.SendMessage(groupRoleDataRequest);
            }
        }, false, 3, 15000L);
        this.groupRoleMemberListRequestHandler = new AsyncLimitsRequestHandler<UUID>(this.agentCircuit, new SimpleRequestHandler<UUID>() {
            @Override
            public void onRequest(@Nonnull final UUID groupID) {
                Debug.Printf("GroupRoleMemberList: [%s] network requesting for %s", Thread.currentThread().getName(), groupID.toString());
                final GroupRoleMembersRequest groupRoleMembersRequest = new GroupRoleMembersRequest();
                groupRoleMembersRequest.AgentData_Field.AgentID = SLGroupManager.this.circuitInfo.agentID;
                groupRoleMembersRequest.AgentData_Field.SessionID = SLGroupManager.this.circuitInfo.sessionID;
                groupRoleMembersRequest.GroupData_Field.GroupID = groupID;
                groupRoleMembersRequest.GroupData_Field.RequestID = UUID.randomUUID();
                groupRoleMembersRequest.isReliable = true;
                SLGroupManager.this.SendMessage(groupRoleMembersRequest);
            }
        }, false, 3, 15000L);
        this.groupMemberListRequestHandler = new AsyncLimitsRequestHandler<UUID>(this.agentCircuit, new SimpleRequestHandler<UUID>() {
            @Override
            public void onRequest(@Nonnull final UUID groupID) {
                Debug.Printf("GroupMemberList: [%s] network requesting for group %s", Thread.currentThread().getName(), groupID.toString());
                final GroupMembersRequest groupMembersRequest = new GroupMembersRequest();
                groupMembersRequest.AgentData_Field.AgentID = SLGroupManager.this.circuitInfo.agentID;
                groupMembersRequest.AgentData_Field.SessionID = SLGroupManager.this.circuitInfo.sessionID;
                groupMembersRequest.GroupData_Field.GroupID = groupID;
                groupMembersRequest.GroupData_Field.RequestID = UUID.randomUUID();
                groupMembersRequest.isReliable = true;
                SLGroupManager.this.SendMessage(groupMembersRequest);
            }
        }, false, 3, 15000L);
        this.groupMemberListHTTPRequestHandler = new AsyncCancellableRequestHandler<UUID>(GenericHTTPExecutor.getInstance(), new SimpleRequestHandler<UUID>() {
            @Override
            public void onRequest(@Nonnull final UUID p0) {
                // 
                // This method could not be decompiled.
                // 
                // Original Bytecode:
                // 
                //     2: iconst_2       
                //     3: anewarray       Ljava/lang/Object;
                //     6: dup            
                //     7: iconst_0       
                //     8: invokestatic    java/lang/Thread.currentThread:()Ljava/lang/Thread;
                //    11: invokevirtual   java/lang/Thread.getName:()Ljava/lang/String;
                //    14: aastore        
                //    15: dup            
                //    16: iconst_1       
                //    17: aload_1        
                //    18: invokevirtual   java/util/UUID.toString:()Ljava/lang/String;
                //    21: aastore        
                //    22: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
                //    25: invokestatic    java/util/UUID.randomUUID:()Ljava/util/UUID;
                //    28: astore_2       
                //    29: new             Lcom/lumiyaviewer/lumiya/slproto/https/LLSDXMLRequest;
                //    32: astore_3       
                //    33: aload_3        
                //    34: invokespecial   com/lumiyaviewer/lumiya/slproto/https/LLSDXMLRequest.<init>:()V
                //    37: aload_0        
                //    38: getfield        com/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager$6.this$0:Lcom/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager;
                //    41: invokestatic    com/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager.-get2:(Lcom/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager;)Ljava/lang/String;
                //    44: astore          4
                //    46: new             Lcom/lumiyaviewer/lumiya/slproto/llsd/types/LLSDMap;
                //    49: astore          5
                //    51: new             Lcom/lumiyaviewer/lumiya/slproto/llsd/types/LLSDMap$LLSDMapEntry;
                //    54: astore          6
                //    56: new             Lcom/lumiyaviewer/lumiya/slproto/llsd/types/LLSDUUID;
                //    59: astore          7
                //    61: aload           7
                //    63: aload_1        
                //    64: invokespecial   com/lumiyaviewer/lumiya/slproto/llsd/types/LLSDUUID.<init>:(Ljava/util/UUID;)V
                //    67: aload           6
                //    69: ldc             "group_id"
                //    71: aload           7
                //    73: invokespecial   com/lumiyaviewer/lumiya/slproto/llsd/types/LLSDMap$LLSDMapEntry.<init>:(Ljava/lang/String;Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;)V
                //    76: aload           5
                //    78: iconst_1       
                //    79: anewarray       Lcom/lumiyaviewer/lumiya/slproto/llsd/types/LLSDMap$LLSDMapEntry;
                //    82: dup            
                //    83: iconst_0       
                //    84: aload           6
                //    86: aastore        
                //    87: invokespecial   com/lumiyaviewer/lumiya/slproto/llsd/types/LLSDMap.<init>:([Lcom/lumiyaviewer/lumiya/slproto/llsd/types/LLSDMap$LLSDMapEntry;)V
                //    90: aload_3        
                //    91: aload           4
                //    93: aload           5
                //    95: invokevirtual   com/lumiyaviewer/lumiya/slproto/https/LLSDXMLRequest.PerformRequest:(Ljava/lang/String;Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
                //    98: astore          5
                //   100: aload           5
                //   102: ifnull          665
                //   105: aload           5
                //   107: ldc             "titles"
                //   109: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
                //   112: astore          6
                //   114: aload           5
                //   116: ldc             "defaults"
                //   118: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
                //   121: astore_3       
                //   122: aload           5
                //   124: ldc             "members"
                //   126: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
                //   129: astore          5
                //   131: lconst_0       
                //   132: lstore          8
                //   134: aload_3        
                //   135: ldc             "default_powers"
                //   137: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.keyExists:(Ljava/lang/String;)Z
                //   140: istore          10
                //   142: lload           8
                //   144: lstore          11
                //   146: iload           10
                //   148: ifeq            179
                //   151: aload_3        
                //   152: ldc             "default_powers"
                //   154: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
                //   157: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asString:()Ljava/lang/String;
                //   160: astore_3       
                //   161: aload_3        
                //   162: ldc             "0x"
                //   164: invokevirtual   java/lang/String.startsWith:(Ljava/lang/String;)Z
                //   167: ifeq            532
                //   170: aload_3        
                //   171: invokestatic    java/lang/Long.decode:(Ljava/lang/String;)Ljava/lang/Long;
                //   174: invokevirtual   java/lang/Long.longValue:()J
                //   177: lstore          11
                //   179: aload           5
                //   181: instanceof      Lcom/lumiyaviewer/lumiya/slproto/llsd/types/LLSDMap;
                //   184: ifeq            696
                //   187: aload           5
                //   189: checkcast       Lcom/lumiyaviewer/lumiya/slproto/llsd/types/LLSDMap;
                //   192: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/types/LLSDMap.entrySet:()Ljava/util/Set;
                //   195: invokeinterface java/lang/Iterable.iterator:()Ljava/util/Iterator;
                //   200: astore          4
                //   202: iconst_0       
                //   203: istore          13
                //   205: aload           4
                //   207: invokeinterface java/util/Iterator.hasNext:()Z
                //   212: ifeq            623
                //   215: aload           4
                //   217: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
                //   222: checkcast       Ljava/util/Map$Entry;
                //   225: astore          5
                //   227: aload           5
                //   229: invokeinterface java/util/Map$Entry.getKey:()Ljava/lang/Object;
                //   234: checkcast       Ljava/lang/String;
                //   237: invokestatic    com/lumiyaviewer/lumiya/utils/UUIDPool.getUUID:(Ljava/lang/String;)Ljava/util/UUID;
                //   240: astore          7
                //   242: aload           5
                //   244: invokeinterface java/util/Map$Entry.getValue:()Ljava/lang/Object;
                //   249: checkcast       Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
                //   252: astore          14
                //   254: iconst_0       
                //   255: istore          15
                //   257: aload           14
                //   259: ldc             "title"
                //   261: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.keyExists:(Ljava/lang/String;)Z
                //   264: ifeq            558
                //   267: aload           6
                //   269: aload           14
                //   271: ldc             "title"
                //   273: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
                //   276: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asInt:()I
                //   279: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byIndex:(I)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
                //   282: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asString:()Ljava/lang/String;
                //   285: astore          5
                //   287: aload           14
                //   289: ldc             "powers"
                //   291: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.keyExists:(Ljava/lang/String;)Z
                //   294: ifeq            572
                //   297: aload           14
                //   299: ldc             "powers"
                //   301: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
                //   304: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asString:()Ljava/lang/String;
                //   307: bipush          16
                //   309: invokestatic    com/google/common/primitives/UnsignedLong.valueOf:(Ljava/lang/String;I)Lcom/google/common/primitives/UnsignedLong;
                //   312: invokevirtual   com/google/common/primitives/UnsignedLong.longValue:()J
                //   315: lstore          8
                //   317: aload           14
                //   319: ldc             "last_login"
                //   321: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.keyExists:(Ljava/lang/String;)Z
                //   324: ifeq            579
                //   327: aload           14
                //   329: ldc             "last_login"
                //   331: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
                //   334: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asString:()Ljava/lang/String;
                //   337: astore_3       
                //   338: aload           14
                //   340: ldc             "donated_square_meters"
                //   342: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.keyExists:(Ljava/lang/String;)Z
                //   345: ifeq            585
                //   348: aload           14
                //   350: ldc             "donated_square_meters"
                //   352: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
                //   355: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asInt:()I
                //   358: istore          16
                //   360: iload           15
                //   362: istore          10
                //   364: aload           14
                //   366: ldc             "owner"
                //   368: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.keyExists:(Ljava/lang/String;)Z
                //   371: ifeq            446
                //   374: aload           14
                //   376: ldc             "owner"
                //   378: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
                //   381: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.isString:()Z
                //   384: ifeq            591
                //   387: aload           14
                //   389: ldc             "owner"
                //   391: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
                //   394: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asString:()Ljava/lang/String;
                //   397: astore          14
                //   399: aload           14
                //   401: ldc             "y"
                //   403: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
                //   406: ifne            443
                //   409: aload           14
                //   411: ldc             "yes"
                //   413: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
                //   416: ifne            443
                //   419: aload           14
                //   421: ldc             "true"
                //   423: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
                //   426: ifne            443
                //   429: iload           15
                //   431: istore          10
                //   433: aload           14
                //   435: ldc             "1"
                //   437: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
                //   440: ifeq            446
                //   443: iconst_1       
                //   444: istore          10
                //   446: iload           13
                //   448: iconst_1       
                //   449: iadd           
                //   450: istore          17
                //   452: iload           17
                //   454: istore          13
                //   456: aload_0        
                //   457: getfield        com/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager$6.this$0:Lcom/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager;
                //   460: invokestatic    com/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager.-get1:(Lcom/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager;)Lcom/lumiyaviewer/lumiya/dao/GroupMemberDao;
                //   463: ifnull          205
                //   466: new             Lcom/lumiyaviewer/lumiya/dao/GroupMember;
                //   469: astore          14
                //   471: aload           14
                //   473: aload_1        
                //   474: aload_2        
                //   475: aload           7
                //   477: iload           16
                //   479: aload_3        
                //   480: lload           8
                //   482: aload           5
                //   484: iload           10
                //   486: invokespecial   com/lumiyaviewer/lumiya/dao/GroupMember.<init>:(Ljava/util/UUID;Ljava/util/UUID;Ljava/util/UUID;ILjava/lang/String;JLjava/lang/String;Z)V
                //   489: aload_0        
                //   490: getfield        com/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager$6.this$0:Lcom/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager;
                //   493: invokestatic    com/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager.-get1:(Lcom/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager;)Lcom/lumiyaviewer/lumiya/dao/GroupMemberDao;
                //   496: aload           14
                //   498: invokevirtual   com/lumiyaviewer/lumiya/dao/GroupMemberDao.insert:(Ljava/lang/Object;)J
                //   501: pop2           
                //   502: iload           17
                //   504: istore          13
                //   506: goto            205
                //   509: astore          5
                //   511: aload           5
                //   513: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
                //   516: aload_0        
                //   517: getfield        com/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager$6.this$0:Lcom/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager;
                //   520: invokestatic    com/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager.-get3:(Lcom/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager;)Lcom/lumiyaviewer/lumiya/react/ResultHandler;
                //   523: aload_1        
                //   524: aload           5
                //   526: invokeinterface com/lumiyaviewer/lumiya/react/ResultHandler.onResultError:(Ljava/lang/Object;Ljava/lang/Throwable;)V
                //   531: return         
                //   532: aload_3        
                //   533: bipush          16
                //   535: invokestatic    com/google/common/primitives/UnsignedLong.valueOf:(Ljava/lang/String;I)Lcom/google/common/primitives/UnsignedLong;
                //   538: invokevirtual   com/google/common/primitives/UnsignedLong.longValue:()J
                //   541: lstore          11
                //   543: goto            179
                //   546: astore_3       
                //   547: aload_3        
                //   548: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
                //   551: lload           8
                //   553: lstore          11
                //   555: goto            179
                //   558: aload           6
                //   560: iconst_0       
                //   561: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byIndex:(I)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
                //   564: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asString:()Ljava/lang/String;
                //   567: astore          5
                //   569: goto            287
                //   572: lload           11
                //   574: lstore          8
                //   576: goto            317
                //   579: ldc             "Unknown"
                //   581: astore_3       
                //   582: goto            338
                //   585: iconst_0       
                //   586: istore          16
                //   588: goto            360
                //   591: iload           15
                //   593: istore          10
                //   595: aload           14
                //   597: ldc             "owner"
                //   599: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
                //   602: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.isBoolean:()Z
                //   605: ifeq            446
                //   608: aload           14
                //   610: ldc             "owner"
                //   612: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
                //   615: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asBoolean:()Z
                //   618: istore          10
                //   620: goto            446
                //   623: ldc             "GroupMemberList: parsed list for group: %s requestID %s memberCount %d"
                //   625: iconst_3       
                //   626: anewarray       Ljava/lang/Object;
                //   629: dup            
                //   630: iconst_0       
                //   631: aload_1        
                //   632: aastore        
                //   633: dup            
                //   634: iconst_1       
                //   635: aload_2        
                //   636: aastore        
                //   637: dup            
                //   638: iconst_2       
                //   639: iload           13
                //   641: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
                //   644: aastore        
                //   645: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
                //   648: aload_0        
                //   649: getfield        com/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager$6.this$0:Lcom/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager;
                //   652: invokestatic    com/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager.-get3:(Lcom/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager;)Lcom/lumiyaviewer/lumiya/react/ResultHandler;
                //   655: aload_1        
                //   656: aload_2        
                //   657: invokeinterface com/lumiyaviewer/lumiya/react/ResultHandler.onResultData:(Ljava/lang/Object;Ljava/lang/Object;)V
                //   662: goto            531
                //   665: aload_0        
                //   666: getfield        com/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager$6.this$0:Lcom/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager;
                //   669: invokestatic    com/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager.-get3:(Lcom/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager;)Lcom/lumiyaviewer/lumiya/react/ResultHandler;
                //   672: astore          5
                //   674: new             Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDException;
                //   677: astore_3       
                //   678: aload_3        
                //   679: ldc             "No data"
                //   681: invokespecial   com/lumiyaviewer/lumiya/slproto/llsd/LLSDException.<init>:(Ljava/lang/String;)V
                //   684: aload           5
                //   686: aload_1        
                //   687: aload_3        
                //   688: invokeinterface com/lumiyaviewer/lumiya/react/ResultHandler.onResultError:(Ljava/lang/Object;Ljava/lang/Throwable;)V
                //   693: goto            531
                //   696: iconst_0       
                //   697: istore          13
                //   699: goto            623
                //    Exceptions:
                //  Try           Handler
                //  Start  End    Start  End    Type                             
                //  -----  -----  -----  -----  ---------------------------------
                //  25     100    509    531    Ljava/lang/Exception;
                //  105    131    509    531    Ljava/lang/Exception;
                //  134    142    509    531    Ljava/lang/Exception;
                //  151    179    546    558    Ljava/lang/NumberFormatException;
                //  151    179    509    531    Ljava/lang/Exception;
                //  179    202    509    531    Ljava/lang/Exception;
                //  205    254    509    531    Ljava/lang/Exception;
                //  257    287    509    531    Ljava/lang/Exception;
                //  287    317    509    531    Ljava/lang/Exception;
                //  317    338    509    531    Ljava/lang/Exception;
                //  338    360    509    531    Ljava/lang/Exception;
                //  364    429    509    531    Ljava/lang/Exception;
                //  433    443    509    531    Ljava/lang/Exception;
                //  456    502    509    531    Ljava/lang/Exception;
                //  532    543    546    558    Ljava/lang/NumberFormatException;
                //  532    543    509    531    Ljava/lang/Exception;
                //  547    551    509    531    Ljava/lang/Exception;
                //  558    569    509    531    Ljava/lang/Exception;
                //  595    620    509    531    Ljava/lang/Exception;
                //  623    662    509    531    Ljava/lang/Exception;
                //  665    693    509    531    Ljava/lang/Exception;
                // 
                // The error that occurred was:
                // 
                // java.lang.IllegalStateException: Expression is linked from several locations: Label_0179:
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
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformCall(AstMethodBodyBuilder.java:1151)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:993)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:548)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:548)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:377)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:318)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:213)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:799)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:635)
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
        });
        this.groupMemberDataURL = slAgentCircuit.getCaps().getCapability(SLCaps.SLCapability.GroupMemberData);
        this.userManager = UserManager.getUserManager(slAgentCircuit.circuitInfo.agentID);
        if (this.userManager != null) {
            this.groupMemberDao = this.userManager.getDaoSession().getGroupMemberDao();
            this.groupRoleMemberDao = this.userManager.getDaoSession().getGroupRoleMemberDao();
        }
        else {
            this.groupMemberDao = null;
            this.groupRoleMemberDao = null;
        }
    }
    
    private void RequestGroupProfileData(final UUID groupID) {
        final GroupProfileRequest groupProfileRequest = new GroupProfileRequest();
        groupProfileRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        groupProfileRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        groupProfileRequest.GroupData_Field.GroupID = groupID;
        groupProfileRequest.isReliable = true;
        this.SendMessage(groupProfileRequest);
    }
    
    private void RequestRoleMemberChange(final UUID groupID, final UUID roleID, final UUID memberID, final int change) {
        final GroupRoleChanges groupRoleChanges = new GroupRoleChanges();
        groupRoleChanges.AgentData_Field.AgentID = this.circuitInfo.agentID;
        groupRoleChanges.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        groupRoleChanges.AgentData_Field.GroupID = groupID;
        final GroupRoleChanges.RoleChange e = new GroupRoleChanges.RoleChange();
        e.RoleID = roleID;
        e.MemberID = memberID;
        e.Change = change;
        groupRoleChanges.RoleChange_Fields.add(e);
        groupRoleChanges.isReliable = true;
        groupRoleChanges.setEventListener(new SLMessageEventListener() {
            @Override
            public void onMessageAcknowledged(final SLMessage slMessage) {
                SLGroupManager.this.userManager.getChatterList().getGroupManager().requestGroupRoleMembersRefresh(groupID);
            }
            
            @Override
            public void onMessageTimeout(final SLMessage slMessage) {
            }
        });
        this.SendMessage(groupRoleChanges);
    }
    
    private void requestGroupTitles(final UUID groupID) {
        final GroupTitlesRequest groupTitlesRequest = new GroupTitlesRequest();
        groupTitlesRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        groupTitlesRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        groupTitlesRequest.AgentData_Field.GroupID = groupID;
        groupTitlesRequest.AgentData_Field.RequestID = UUID.randomUUID();
        groupTitlesRequest.isReliable = true;
        this.SendMessage(groupTitlesRequest);
    }
    
    public void AcceptGroupInvite(final UUID toAgentID, final UUID id, final boolean b) {
        final ImprovedInstantMessage improvedInstantMessage = new ImprovedInstantMessage();
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        improvedInstantMessage.MessageBlock_Field.FromGroup = false;
        improvedInstantMessage.MessageBlock_Field.ToAgentID = toAgentID;
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0;
        improvedInstantMessage.MessageBlock_Field.RegionID = new UUID(0L, 0L);
        improvedInstantMessage.MessageBlock_Field.Position = new LLVector3();
        improvedInstantMessage.MessageBlock_Field.Offline = 0;
        final ImprovedInstantMessage.MessageBlock messageBlock_Field = improvedInstantMessage.MessageBlock_Field;
        int dialog;
        if (b) {
            dialog = 35;
        }
        else {
            dialog = 36;
        }
        messageBlock_Field.Dialog = dialog;
        improvedInstantMessage.MessageBlock_Field.ID = id;
        improvedInstantMessage.MessageBlock_Field.Timestamp = 0;
        improvedInstantMessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo");
        improvedInstantMessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF("todo");
        improvedInstantMessage.MessageBlock_Field.BinaryBucket = new byte[0];
        improvedInstantMessage.isReliable = true;
        this.SendMessage(improvedInstantMessage);
    }
    
    public void ActivateGroup(final UUID groupID) {
        final ActivateGroup activateGroup = new ActivateGroup();
        activateGroup.AgentData_Field.AgentID = this.circuitInfo.agentID;
        activateGroup.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        activateGroup.AgentData_Field.GroupID = groupID;
        activateGroup.isReliable = true;
        this.SendMessage(activateGroup);
    }
    
    public void AddMemberToRole(final UUID uuid, final UUID uuid2, final UUID uuid3) {
        this.RequestRoleMemberChange(uuid, uuid2, uuid3, 0);
    }
    
    public void DeleteRole(final UUID groupID, final UUID roleID) {
        final GroupRoleUpdate groupRoleUpdate = new GroupRoleUpdate();
        groupRoleUpdate.AgentData_Field.AgentID = this.circuitInfo.agentID;
        groupRoleUpdate.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        groupRoleUpdate.AgentData_Field.GroupID = groupID;
        final GroupRoleUpdate.RoleData e = new GroupRoleUpdate.RoleData();
        e.RoleID = roleID;
        e.Name = SLMessage.stringToVariableOEM("");
        e.Title = SLMessage.stringToVariableOEM("");
        e.Description = SLMessage.stringToVariableOEM("");
        e.Powers = 0L;
        e.UpdateType = 5;
        groupRoleUpdate.RoleData_Fields.add(e);
        groupRoleUpdate.isReliable = true;
        groupRoleUpdate.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() {
            @Override
            public void onMessageAcknowledged(final SLMessage slMessage) {
                SLGroupManager.this.userManager.getGroupRoles().requestUpdate((Object)groupID);
            }
        });
        this.SendMessage(groupRoleUpdate);
    }
    
    @Override
    public void HandleCircuitReady() {
        if (this.userManager != null) {
            this.groupProfileResultHandler = this.userManager.getCachedGroupProfiles().getRequestSource().attachRequestHandler(this.groupProfileRequestHandler);
            this.groupTitlesResultHandler = this.userManager.getGroupTitles().getRequestSource().attachRequestHandler(this.groupTitlesRequestHandler);
            this.groupRolesResultHandler = this.userManager.getGroupRoles().getRequestSource().attachRequestHandler(this.groupRolesRequestHandler);
            final RequestSource<UUID, UUID> groupMemberDataSetRequestSource = this.userManager.getChatterList().getGroupManager().getGroupMemberDataSetRequestSource();
            RequestHandler<UUID> requestHandler;
            if (Strings.isNullOrEmpty(this.groupMemberDataURL)) {
                requestHandler = this.groupMemberListRequestHandler;
            }
            else {
                requestHandler = this.groupMemberListHTTPRequestHandler;
            }
            this.groupMemberListResultHandler = groupMemberDataSetRequestSource.attachRequestHandler(requestHandler);
            this.groupRoleMemberListResultHandler = this.userManager.getChatterList().getGroupManager().getGroupRoleMemberDataSetRequestSource().attachRequestHandler(this.groupRoleMemberListRequestHandler);
        }
    }
    
    @Override
    public void HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.getCachedGroupProfiles().getRequestSource().detachRequestHandler(this.groupProfileRequestHandler);
            this.userManager.getGroupTitles().getRequestSource().detachRequestHandler(this.groupTitlesRequestHandler);
            this.userManager.getGroupRoles().getRequestSource().detachRequestHandler(this.groupRolesRequestHandler);
            this.userManager.getChatterList().getGroupManager().getGroupMemberDataSetRequestSource().detachRequestHandler(this.groupMemberListRequestHandler);
        }
    }
    
    @SLMessageHandler
    public void HandleEjectGroupMemberReply(final EjectGroupMemberReply ejectGroupMemberReply) {
        this.userManager.getChatterList().getGroupManager().requestRefreshMemberList(ejectGroupMemberReply.GroupData_Field.GroupID);
    }
    
    @SLMessageHandler
    public void HandleGroupMembersReply(final GroupMembersReply groupMembersReply) {
        Debug.Printf("GroupMember: got reply, %d members, memberCount %d", groupMembersReply.MemberData_Fields.size(), groupMembersReply.GroupData_Field.MemberCount);
        for (final GroupMembersReply.MemberData memberData : groupMembersReply.MemberData_Fields) {
            ((AbstractDao<GroupMember, K>)this.groupMemberDao).insert(new GroupMember(groupMembersReply.GroupData_Field.GroupID, groupMembersReply.GroupData_Field.RequestID, memberData.AgentID, memberData.Contribution, SLMessage.stringFromVariableOEM(memberData.OnlineStatus), memberData.AgentPowers, SLMessage.stringFromVariableOEM(memberData.Title), memberData.IsOwner));
            Debug.Printf("GroupMember: userID = %s", memberData.AgentID);
        }
        final long count = ((AbstractDao<GroupMember, K>)this.groupMemberDao).queryBuilder().where(GroupMemberDao.Properties.GroupID.eq(groupMembersReply.GroupData_Field.GroupID), GroupMemberDao.Properties.RequestID.eq(groupMembersReply.GroupData_Field.RequestID)).count();
        Debug.Printf("GroupMemberList: count = %d", count);
        if (count >= groupMembersReply.GroupData_Field.MemberCount) {
            this.groupMemberListResultHandler.onResultData(groupMembersReply.GroupData_Field.GroupID, groupMembersReply.GroupData_Field.RequestID);
        }
    }
    
    @SLMessageHandler
    public void HandleGroupProfileReply(final GroupProfileReply groupProfileReply) {
        if (this.groupProfileResultHandler != null) {
            this.groupProfileResultHandler.onResultData(groupProfileReply.GroupData_Field.GroupID, groupProfileReply);
        }
    }
    
    @SLMessageHandler
    public void HandleGroupRoleDataReply(final GroupRoleDataReply groupRoleDataReply) {
        if (this.groupRolesResultHandler != null) {
            this.groupRolesResultHandler.onResultData(groupRoleDataReply.GroupData_Field.GroupID, groupRoleDataReply);
        }
    }
    
    @SLMessageHandler
    public void HandleGroupRoleMembersReply(final GroupRoleMembersReply groupRoleMembersReply) {
        Debug.Printf("GroupRoleMember: got reply, %d members, total pairs %d", groupRoleMembersReply.MemberData_Fields.size(), groupRoleMembersReply.AgentData_Field.TotalPairs);
        for (final GroupRoleMembersReply.MemberData memberData : groupRoleMembersReply.MemberData_Fields) {
            ((AbstractDao<GroupRoleMember, K>)this.groupRoleMemberDao).insert(new GroupRoleMember(groupRoleMembersReply.AgentData_Field.GroupID, groupRoleMembersReply.AgentData_Field.RequestID, memberData.RoleID, memberData.MemberID));
        }
        final long count = ((AbstractDao<GroupRoleMember, K>)this.groupRoleMemberDao).queryBuilder().where(GroupRoleMemberDao.Properties.GroupID.eq(groupRoleMembersReply.AgentData_Field.GroupID), GroupRoleMemberDao.Properties.RequestID.eq(groupRoleMembersReply.AgentData_Field.RequestID)).count();
        Debug.Printf("GroupRoleMemberList: count = %d", count);
        if (count >= groupRoleMembersReply.AgentData_Field.TotalPairs) {
            this.groupRoleMemberListResultHandler.onResultData(groupRoleMembersReply.AgentData_Field.GroupID, groupRoleMembersReply.AgentData_Field.RequestID);
        }
    }
    
    @SLMessageHandler
    public void HandleGroupTitlesReply(final GroupTitlesReply groupTitlesReply) {
        if (this.groupTitlesResultHandler != null) {
            this.groupTitlesResultHandler.onResultData(groupTitlesReply.AgentData_Field.GroupID, groupTitlesReply);
        }
    }
    
    @SLMessageHandler
    public void HandleJoinGroupReply(final JoinGroupReply joinGroupReply) {
        final AgentDataUpdateRequest agentDataUpdateRequest = new AgentDataUpdateRequest();
        agentDataUpdateRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentDataUpdateRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        agentDataUpdateRequest.isReliable = true;
        this.SendMessage(agentDataUpdateRequest);
        this.eventBus.publish(new SLJoinLeaveGroupEvent(joinGroupReply.GroupData_Field.GroupID, true, joinGroupReply.GroupData_Field.Success));
    }
    
    @SLMessageHandler
    public void HandleLeaveGroupReply(final LeaveGroupReply leaveGroupReply) {
        final AgentDataUpdateRequest agentDataUpdateRequest = new AgentDataUpdateRequest();
        agentDataUpdateRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentDataUpdateRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        agentDataUpdateRequest.isReliable = true;
        this.SendMessage(agentDataUpdateRequest);
        this.eventBus.publish(new SLJoinLeaveGroupEvent(leaveGroupReply.GroupData_Field.GroupID, false, leaveGroupReply.GroupData_Field.Success));
    }
    
    public void RemoveMemberFromRole(final UUID uuid, final UUID uuid2, final UUID uuid3) {
        this.RequestRoleMemberChange(uuid, uuid2, uuid3, 1);
    }
    
    public void RequestEjectFromGroup(final UUID groupID, final UUID ejecteeID) {
        final EjectGroupMemberRequest ejectGroupMemberRequest = new EjectGroupMemberRequest();
        ejectGroupMemberRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        ejectGroupMemberRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        ejectGroupMemberRequest.GroupData_Field.GroupID = groupID;
        final EjectGroupMemberRequest.EjectData e = new EjectGroupMemberRequest.EjectData();
        e.EjecteeID = ejecteeID;
        ejectGroupMemberRequest.EjectData_Fields.add(e);
        ejectGroupMemberRequest.isReliable = true;
        this.SendMessage(ejectGroupMemberRequest);
    }
    
    public void RequestJoinGroup(final UUID groupID) {
        final JoinGroupRequest joinGroupRequest = new JoinGroupRequest();
        joinGroupRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        joinGroupRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        joinGroupRequest.GroupData_Field.GroupID = groupID;
        joinGroupRequest.isReliable = true;
        this.SendMessage(joinGroupRequest);
    }
    
    public void RequestLeaveGroup(final UUID groupID) {
        final LeaveGroupRequest leaveGroupRequest = new LeaveGroupRequest();
        leaveGroupRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        leaveGroupRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        leaveGroupRequest.GroupData_Field.GroupID = groupID;
        leaveGroupRequest.isReliable = true;
        this.SendMessage(leaveGroupRequest);
    }
    
    public void RequestMemberRoleChanges(final UUID groupID, final UUID memberID, final Collection<UUID> collection, final Collection<UUID> collection2) {
        final boolean equals = this.circuitInfo.agentID.equals(memberID);
        final GroupRoleChanges groupRoleChanges = new GroupRoleChanges();
        groupRoleChanges.AgentData_Field.AgentID = this.circuitInfo.agentID;
        groupRoleChanges.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        groupRoleChanges.AgentData_Field.GroupID = groupID;
        for (final UUID roleID : collection) {
            Debug.Printf("GroupRoleChange: groupID %s memberID %s add %s", groupID, memberID, roleID);
            final GroupRoleChanges.RoleChange e = new GroupRoleChanges.RoleChange();
            e.RoleID = roleID;
            e.MemberID = memberID;
            e.Change = 0;
            groupRoleChanges.RoleChange_Fields.add(e);
        }
        for (final UUID roleID2 : collection2) {
            Debug.Printf("GroupRoleChange: groupID %s memberID %s remove %s", groupID, memberID, roleID2);
            final GroupRoleChanges.RoleChange e2 = new GroupRoleChanges.RoleChange();
            e2.RoleID = roleID2;
            e2.MemberID = memberID;
            e2.Change = 1;
            groupRoleChanges.RoleChange_Fields.add(e2);
        }
        groupRoleChanges.isReliable = true;
        groupRoleChanges.setEventListener(new SLMessageEventListener() {
            @Override
            public void onMessageAcknowledged(final SLMessage slMessage) {
                SLGroupManager.this.userManager.getChatterList().getGroupManager().requestGroupRoleMembersRefresh(groupID);
                if (equals) {
                    SLGroupManager.this.userManager.getCachedGroupProfiles().requestUpdate((Object)groupID);
                    SLGroupManager.this.userManager.getGroupTitles().requestUpdate((Object)groupID);
                    SLGroupManager.this.userManager.getAvatarGroupLists().requestUpdate((Object)memberID);
                }
            }
            
            @Override
            public void onMessageTimeout(final SLMessage slMessage) {
            }
        });
        this.SendMessage(groupRoleChanges);
    }
    
    public void SendGroupInvite(final UUID inviteeID, final UUID groupID, final UUID roleID) {
        final InviteGroupRequest inviteGroupRequest = new InviteGroupRequest();
        inviteGroupRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        inviteGroupRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        inviteGroupRequest.GroupData_Field.GroupID = groupID;
        final InviteGroupRequest.InviteData e = new InviteGroupRequest.InviteData();
        e.InviteeID = inviteeID;
        e.RoleID = roleID;
        inviteGroupRequest.InviteData_Fields.add(e);
        inviteGroupRequest.isReliable = true;
        this.SendMessage(inviteGroupRequest);
        this.agentCircuit.HandleChatEvent(ChatterID.getGroupChatterID(this.agentCircuit.getAgentUUID(), groupID), new SLChatGroupInvitationSentEvent(new ChatMessageSourceUser(inviteeID), this.agentCircuit.getAgentUUID()), true);
    }
    
    public void SendGroupNotice(final UUID toAgentID, final String str, final String str2, final SLInventoryEntry slInventoryEntry) {
        final ImprovedInstantMessage improvedInstantMessage = new ImprovedInstantMessage();
        improvedInstantMessage.AgentData_Field.AgentID = this.circuitInfo.agentID;
        improvedInstantMessage.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        improvedInstantMessage.MessageBlock_Field.FromGroup = false;
        improvedInstantMessage.MessageBlock_Field.ToAgentID = toAgentID;
        improvedInstantMessage.MessageBlock_Field.ParentEstateID = 0;
        improvedInstantMessage.MessageBlock_Field.RegionID = UUIDPool.ZeroUUID;
        improvedInstantMessage.MessageBlock_Field.Position = new LLVector3();
        improvedInstantMessage.MessageBlock_Field.Offline = 0;
        improvedInstantMessage.MessageBlock_Field.Dialog = 32;
        improvedInstantMessage.MessageBlock_Field.ID = UUIDPool.ZeroUUID;
        improvedInstantMessage.MessageBlock_Field.Timestamp = 0;
        improvedInstantMessage.MessageBlock_Field.FromAgentName = SLMessage.stringToVariableOEM("todo");
        improvedInstantMessage.MessageBlock_Field.Message = SLMessage.stringToVariableUTF(str + "|" + str2);
        while (true) {
            Label_0296: {
                if (slInventoryEntry == null) {
                    break Label_0296;
                }
                final LLSDMap llsdMap = new LLSDMap(new LLSDMap.LLSDMapEntry[] { new LLSDMap.LLSDMapEntry("item_id", new LLSDUUID(slInventoryEntry.uuid)), new LLSDMap.LLSDMapEntry("owner_id", new LLSDUUID(slInventoryEntry.ownerUUID)) });
                try {
                    improvedInstantMessage.MessageBlock_Field.BinaryBucket = SLMessage.stringToVariableOEM(llsdMap.serializeToXML());
                    improvedInstantMessage.isReliable = true;
                    this.SendMessage(improvedInstantMessage);
                    return;
                }
                catch (final IOException ex) {
                    ex.printStackTrace();
                    improvedInstantMessage.MessageBlock_Field.BinaryBucket = new byte[0];
                    continue;
                }
            }
            improvedInstantMessage.MessageBlock_Field.BinaryBucket = new byte[0];
            continue;
        }
    }
    
    public void SetGroupContribution(final UUID groupID, final int contribution) {
        final SetGroupContribution setGroupContribution = new SetGroupContribution();
        setGroupContribution.AgentData_Field.AgentID = this.circuitInfo.agentID;
        setGroupContribution.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        setGroupContribution.Data_Field.GroupID = groupID;
        setGroupContribution.Data_Field.Contribution = contribution;
        setGroupContribution.isReliable = true;
        setGroupContribution.setEventListener(new SLMessageEventListener() {
            @Override
            public void onMessageAcknowledged(final SLMessage slMessage) {
                final AgentDataUpdateRequest agentDataUpdateRequest = new AgentDataUpdateRequest();
                agentDataUpdateRequest.AgentData_Field.AgentID = SLGroupManager.this.circuitInfo.agentID;
                agentDataUpdateRequest.AgentData_Field.SessionID = SLGroupManager.this.circuitInfo.sessionID;
                agentDataUpdateRequest.isReliable = true;
                SLGroupManager.this.SendMessage(agentDataUpdateRequest);
            }
            
            @Override
            public void onMessageTimeout(final SLMessage slMessage) {
            }
        });
        this.SendMessage(setGroupContribution);
    }
    
    public void SetGroupOptions(final UUID groupID, final boolean acceptNotices, final boolean listInProfile) {
        final SetGroupAcceptNotices setGroupAcceptNotices = new SetGroupAcceptNotices();
        setGroupAcceptNotices.AgentData_Field.AgentID = this.circuitInfo.agentID;
        setGroupAcceptNotices.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        setGroupAcceptNotices.Data_Field.GroupID = groupID;
        setGroupAcceptNotices.Data_Field.AcceptNotices = acceptNotices;
        setGroupAcceptNotices.NewData_Field.ListInProfile = listInProfile;
        setGroupAcceptNotices.isReliable = true;
        this.SendMessage(setGroupAcceptNotices);
        this.agentCircuit.getModules().userProfiles.requestAgentDataUpdate();
    }
    
    public void SetGroupRole(final UUID groupID, final UUID titleRoleID) {
        final GroupTitleUpdate groupTitleUpdate = new GroupTitleUpdate();
        groupTitleUpdate.AgentData_Field.AgentID = this.circuitInfo.agentID;
        groupTitleUpdate.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        groupTitleUpdate.AgentData_Field.GroupID = groupID;
        groupTitleUpdate.AgentData_Field.TitleRoleID = titleRoleID;
        groupTitleUpdate.isReliable = true;
        this.SendMessage(groupTitleUpdate);
        this.requestGroupTitles(groupID);
    }
    
    public void SetRoleProperties(final UUID groupID, @Nullable final UUID uuid, final String s, final String s2, final String s3, final long powers) {
        Debug.Printf("GroupRole: setting role properties for role %s", uuid);
        final GroupRoleUpdate groupRoleUpdate = new GroupRoleUpdate();
        groupRoleUpdate.AgentData_Field.AgentID = this.circuitInfo.agentID;
        groupRoleUpdate.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        groupRoleUpdate.AgentData_Field.GroupID = groupID;
        final GroupRoleUpdate.RoleData e = new GroupRoleUpdate.RoleData();
        UUID randomUUID;
        if (uuid != null) {
            randomUUID = uuid;
        }
        else {
            randomUUID = UUID.randomUUID();
        }
        e.RoleID = randomUUID;
        e.Name = SLMessage.stringToVariableOEM(s);
        e.Title = SLMessage.stringToVariableOEM(s2);
        e.Description = SLMessage.stringToVariableOEM(s3);
        e.Powers = powers;
        int updateType;
        if (uuid != null) {
            updateType = 3;
        }
        else {
            updateType = 4;
        }
        e.UpdateType = updateType;
        groupRoleUpdate.RoleData_Fields.add(e);
        groupRoleUpdate.isReliable = true;
        groupRoleUpdate.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() {
            @Override
            public void onMessageAcknowledged(final SLMessage slMessage) {
                Debug.Printf("GroupRole: ack set properties for role %s", uuid);
                SLGroupManager.this.userManager.getGroupRoles().requestUpdate((Object)groupID);
            }
        });
        this.SendMessage(groupRoleUpdate);
    }
    
    public UUID getActiveGroupID() {
        return this.activeGroupID;
    }
}
