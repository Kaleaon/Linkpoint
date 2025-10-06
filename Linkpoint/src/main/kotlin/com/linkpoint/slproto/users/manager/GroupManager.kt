package com.linkpoint.slproto.users.manager

import com.google.common.collect.ImmutableSet
import com.linkpoint.dao.DaoSession
import com.linkpoint.dao.GroupMember
import com.linkpoint.dao.GroupMemberDao
import com.linkpoint.dao.GroupMemberList
import com.linkpoint.dao.GroupMemberListDao
import com.linkpoint.dao.GroupRoleMember
import com.linkpoint.dao.GroupRoleMemberDao
import com.linkpoint.dao.GroupRoleMemberList
import com.linkpoint.dao.GroupRoleMemberListDao
import com.linkpoint.react.AsyncRequestHandler
import com.linkpoint.react.DisposeHandler
import com.linkpoint.react.RateLimitRequestHandler
import com.linkpoint.react.RequestProcessor
import com.linkpoint.react.RequestSource
import com.linkpoint.react.SimpleRequestHandler
import com.linkpoint.react.Subscribable
import com.linkpoint.react.Subscription
import com.linkpoint.react.SubscriptionPool
import com.linkpoint.slproto.modules.groups.AvatarGroupList
import de.greenrobot.dao.query.LazyList
import java.util.Set
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.Nonnull
import javax.annotation.Nullable

class GroupManager {
    private val AtomicReference<AvatarGroupList> avatarGroupListRef = AtomicReference<>()
    /* access modifiers changed from: private */
    val ChatterList chatterList
    /* access modifiers changed from: private */
    val GroupMemberDao groupMemberDao
    private val RateLimitRequestHandler<UUID, UUID> groupMemberDataSetHandler
    private val SubscriptionPool<UUID, UUID> groupMemberDataSetPool = SubscriptionPool<>()
    /* access modifiers changed from: private */
    val GroupMemberListDao groupMemberListDao
    /* access modifiers changed from: private */
    val SubscriptionPool<GroupMemberRolesQuery, Set<UUID>> groupMemberRolesSubscriptionPool = SubscriptionPool<>()
    /* access modifiers changed from: private */
    val SubscriptionPool<GroupMembersQuery, LazyList<GroupMember>> groupMembersSubscriptionPool = SubscriptionPool<>()
    /* access modifiers changed from: private */
    val GroupRoleMemberDao groupRoleMemberDao
    private val RateLimitRequestHandler<UUID, UUID> groupRoleMemberDataSetHandler
    private val SubscriptionPool<UUID, UUID> groupRoleMemberDataSetPool = SubscriptionPool<>()
    /* access modifiers changed from: private */
    val GroupRoleMemberListDao groupRoleMemberListDao
    /* access modifiers changed from: private */
    val SubscriptionPool<GroupRoleMembersQuery, LazyList<GroupRoleMember>> groupRoleMemberSubscriptionPool = SubscriptionPool<>()
    private val OnListUpdated onGroupListUpdated = OnListUpdated() {
        fun onListUpdated() {
            GroupManager.this.chatterList.notifyListUpdated(ChatterListType.Groups)
        }
    }
    private val Subscription<UUID, AvatarGroupList> subscription
    private val UserManager userManager

    @JvmStatic
    abstract class GroupMemberRolesQuery {
        @JvmStatic
    GroupMemberRolesQuery create(UUID uuid, UUID uuid2, UUID uuid3) {
            return AutoValue_GroupManager_GroupMemberRolesQuery(uuid, uuid2, uuid3)
        }

        public abstract UUID groupID()

        public abstract UUID memberID()

        public abstract UUID requestID()
    }

    @JvmStatic
    abstract class GroupMembersQuery {
        @JvmStatic
    GroupMembersQuery create(UUID uuid, UUID uuid2) {
            return AutoValue_GroupManager_GroupMembersQuery(uuid, uuid2)
        }

        public abstract UUID groupID()

        public abstract UUID requestID()
    }

    @JvmStatic
    abstract class GroupRoleMembersQuery {
        @JvmStatic
    GroupRoleMembersQuery create(UUID uuid, UUID uuid2, UUID uuid3) {
            return AutoValue_GroupManager_GroupRoleMembersQuery(uuid, uuid2, uuid3)
        }

        public abstract UUID groupID()

        public abstract UUID requestID()

        public abstract UUID roleID()
    }

    GroupManager(UserManager userManager2, DaoSession daoSession, ChatterList chatterList2) {
        this.userManager = userManager2
        this.chatterList = chatterList2
        this.groupMemberDao = daoSession.getGroupMemberDao()
        this.groupMemberListDao = daoSession.getGroupMemberListDao()
        this.groupRoleMemberDao = daoSession.getGroupRoleMemberDao()
        this.groupRoleMemberListDao = daoSession.getGroupRoleMemberListDao()
        this.subscription = userManager2.getAvatarGroupLists().getPool().subscribe(userManager2.getUserID(), Subscription.OnData(this) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f218$f0

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$u_XXTkSOKCgaVXhhU-plrxzPP28.2.$m$0(java.lang.Object):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$u_XXTkSOKCgaVXhhU-plrxzPP28.2.$m$0(java.lang.Object):Unit, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

        this.groupMemberDataSetHandler = RateLimitRequestHandler<>(RequestProcessor<UUID, UUID, UUID>(this.groupMemberDataSetPool, userManager2.getDatabaseExecutor()) {
            /* access modifiers changed from: protected */
            public UUID processRequest(UUID uuid) {
                GroupMemberList groupMemberList = (GroupMemberList) GroupManager.this.groupMemberListDao.load(uuid)
                if (groupMemberList != null) {
                    return groupMemberList.getRequestID()
                }
                return null
            }

            /* access modifiers changed from: protected */
            public UUID processResult(UUID uuid, UUID uuid2) {
                GroupManager.this.groupMemberListDao.insertOrReplace(GroupMemberList(uuid, uuid2))
                return uuid2
            }
        this.groupRoleMemberDataSetHandler = RateLimitRequestHandler<>(RequestProcessor<UUID, UUID, UUID>(this.groupRoleMemberDataSetPool, userManager2.getDatabaseExecutor()) {
            /* access modifiers changed from: protected */
            public Boolean isRequestComplete(UUID uuid, UUID uuid2) {
                GroupRoleMemberList groupRoleMemberList = (GroupRoleMemberList) GroupManager.this.groupRoleMemberListDao.load(uuid)
                if (groupRoleMemberList != null) {
                    return !groupRoleMemberList.getMustRevalidate()
                }
                return false
            }

            /* access modifiers changed from: protected */
            public UUID processRequest(UUID uuid) {
                GroupRoleMemberList groupRoleMemberList = (GroupRoleMemberList) GroupManager.this.groupRoleMemberListDao.load(uuid)
                if (groupRoleMemberList != null) {
                    return groupRoleMemberList.getRequestID()
                }
                return null
            }

            /* access modifiers changed from: protected */
            public UUID processResult(UUID uuid, UUID uuid2) {
                GroupManager.this.groupRoleMemberListDao.insertOrReplace(GroupRoleMemberList(uuid, uuid2, false))
                return uuid2
            }
        this.groupRoleMemberSubscriptionPool.attachRequestHandler(AsyncRequestHandler(userManager2.getDatabaseExecutor(), SimpleRequestHandler<GroupRoleMembersQuery>() {
            fun onRequest(GroupRoleMembersQuery groupRoleMembersQuery) {
                GroupManager.this.groupRoleMemberSubscriptionPool.onResultData(groupRoleMembersQuery, GroupManager.this.groupRoleMemberDao.queryBuilder().where(GroupRoleMemberDao.Properties.GroupID.eq(groupRoleMembersQuery.groupID()), GroupRoleMemberDao.Properties.RoleID.eq(groupRoleMembersQuery.roleID()), GroupRoleMemberDao.Properties.RequestID.eq(groupRoleMembersQuery.requestID())).listLazyUncached())
            }
        }))
        this.groupRoleMemberSubscriptionPool.setDisposeHandler($Lambda$u_XXTkSOKCgaVXhhUplrxzPP28(), userManager2.getDatabaseExecutor())
        this.groupMembersSubscriptionPool.attachRequestHandler(AsyncRequestHandler(userManager2.getDatabaseExecutor(), SimpleRequestHandler<GroupMembersQuery>() {
            fun onRequest(GroupMembersQuery groupMembersQuery) {
                GroupManager.this.groupMembersSubscriptionPool.onResultData(groupMembersQuery, GroupManager.this.groupMemberDao.queryBuilder().where(GroupMemberDao.Properties.GroupID.eq(groupMembersQuery.groupID()), GroupMemberDao.Properties.RequestID.eq(groupMembersQuery.requestID())).listLazyUncached())
            }
        }))
        this.groupMembersSubscriptionPool.setDisposeHandler(DisposeHandler() {
            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$u_XXTkSOKCgaVXhhU-plrxzPP28.1.$m$0(java.lang.Object):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$u_XXTkSOKCgaVXhhU-plrxzPP28.1.$m$0(java.lang.Object):Unit, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

        }, userManager2.getDatabaseExecutor())
        this.groupMemberRolesSubscriptionPool.attachRequestHandler(AsyncRequestHandler(userManager2.getDatabaseExecutor(), SimpleRequestHandler<GroupMemberRolesQuery>() {
            fun onRequest(GroupMemberRolesQuery groupMemberRolesQuery) {
                LazyList<GroupRoleMember> listLazyUncached = GroupManager.this.groupRoleMemberDao.queryBuilder().where(GroupRoleMemberDao.Properties.GroupID.eq(groupMemberRolesQuery.groupID()), GroupRoleMemberDao.Properties.UserID.eq(groupMemberRolesQuery.memberID()), GroupRoleMemberDao.Properties.RequestID.eq(groupMemberRolesQuery.requestID())).listLazyUncached()
                ImmutableSet.Builder builder = ImmutableSet.builder()
                for (GroupRoleMember roleID : listLazyUncached) {
                    builder.add((Object) roleID.getRoleID())
                }
                listLazyUncached.close()
                GroupManager.this.groupMemberRolesSubscriptionPool.onResultData(groupMemberRolesQuery, builder.build())
            }
        }))
    }

    /* access modifiers changed from: private */
    /* renamed from: onAvatarGroupListsReply */
    fun m314com_lumiyaviewer_lumiya_slproto_users_manager_GroupManagermthref0(AvatarGroupList avatarGroupList) {
        this.avatarGroupListRef.set(avatarGroupList)
        this.chatterList.notifyListUpdated(ChatterListType.Groups)
    }

    public AvatarGroupList getAvatarGroupList() {
        return this.avatarGroupListRef.get()
    }

    /* access modifiers changed from: package-private */
    public ChatterDisplayDataList getGroupList() {
        return GroupDisplayDataList(this.userManager, this.onGroupListUpdated)
    }

    public RequestSource<UUID, UUID> getGroupMemberDataSetRequestSource() {
        return this.groupMemberDataSetHandler
    }

    public Subscribable<GroupMemberRolesQuery, Set<UUID>> getGroupMemberRoleList() {
        return this.groupMemberRolesSubscriptionPool
    }

    public Subscribable<UUID, UUID> getGroupMembers() {
        return this.groupMemberDataSetPool
    }

    public Subscribable<GroupMembersQuery, LazyList<GroupMember>> getGroupMembersList() {
        return this.groupMembersSubscriptionPool
    }

    public RequestSource<UUID, UUID> getGroupRoleMemberDataSetRequestSource() {
        return this.groupRoleMemberDataSetHandler
    }

    public Subscribable<GroupRoleMembersQuery, LazyList<GroupRoleMember>> getGroupRoleMemberList() {
        return this.groupRoleMemberSubscriptionPool
    }

    public Subscribable<UUID, UUID> getGroupRoleMembers() {
        return this.groupRoleMemberDataSetPool
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_GroupManager_10304  reason: not valid java name */
    public /* synthetic */ Unit m315lambda$com_lumiyaviewer_lumiya_slproto_users_manager_GroupManager_10304(UUID uuid) {
        GroupRoleMemberList groupRoleMemberList = (GroupRoleMemberList) this.groupRoleMemberListDao.load(uuid)
        if (groupRoleMemberList != null) {
            groupRoleMemberList.setMustRevalidate(true)
            this.groupRoleMemberListDao.update(groupRoleMemberList)
        }
        this.groupRoleMemberDataSetPool.requestUpdate(uuid)
    }

    fun requestGroupRoleMembersRefresh(UUID uuid) {
        this.userManager.getDatabaseExecutor().execute(Runnable(this, uuid) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f219$f0

            /* renamed from: -$f1 */
            private val /* synthetic */ Object f220$f1

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$u_XXTkSOKCgaVXhhU-plrxzPP28.3.$m$0():Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$u_XXTkSOKCgaVXhhU-plrxzPP28.3.$m$0():Unit, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

    }

    fun requestRefreshMemberList(UUID uuid) {
        this.groupMemberDataSetPool.requestUpdate(uuid)
    }
}
