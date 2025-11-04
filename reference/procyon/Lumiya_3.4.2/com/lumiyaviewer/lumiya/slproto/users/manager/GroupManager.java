// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import de.greenrobot.dao.AbstractDao;
import com.lumiyaviewer.lumiya.react.Subscribable;
import java.util.Iterator;
import com.google.common.collect.ImmutableSet;
import com.lumiyaviewer.lumiya.react.DisposeHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import de.greenrobot.dao.query.WhereCondition;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.dao.GroupRoleMemberList;
import com.lumiyaviewer.lumiya.dao.GroupMemberList;
import javax.annotation.Nullable;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.RequestProcessor;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.dao.GroupRoleMember;
import com.lumiyaviewer.lumiya.dao.GroupRoleMemberListDao;
import com.lumiyaviewer.lumiya.dao.GroupRoleMemberDao;
import com.lumiyaviewer.lumiya.dao.GroupMember;
import de.greenrobot.dao.query.LazyList;
import java.util.Set;
import com.lumiyaviewer.lumiya.dao.GroupMemberListDao;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.RateLimitRequestHandler;
import com.lumiyaviewer.lumiya.dao.GroupMemberDao;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import java.util.concurrent.atomic.AtomicReference;

public class GroupManager
{
    private final AtomicReference<AvatarGroupList> avatarGroupListRef;
    @Nonnull
    private final ChatterList chatterList;
    private final GroupMemberDao groupMemberDao;
    private final RateLimitRequestHandler<UUID, UUID> groupMemberDataSetHandler;
    private final SubscriptionPool<UUID, UUID> groupMemberDataSetPool;
    private final GroupMemberListDao groupMemberListDao;
    private final SubscriptionPool<GroupMemberRolesQuery, Set<UUID>> groupMemberRolesSubscriptionPool;
    private final SubscriptionPool<GroupMembersQuery, LazyList<GroupMember>> groupMembersSubscriptionPool;
    private final GroupRoleMemberDao groupRoleMemberDao;
    private final RateLimitRequestHandler<UUID, UUID> groupRoleMemberDataSetHandler;
    private final SubscriptionPool<UUID, UUID> groupRoleMemberDataSetPool;
    private final GroupRoleMemberListDao groupRoleMemberListDao;
    private final SubscriptionPool<GroupRoleMembersQuery, LazyList<GroupRoleMember>> groupRoleMemberSubscriptionPool;
    private final OnListUpdated onGroupListUpdated;
    private final Subscription<UUID, AvatarGroupList> subscription;
    @Nonnull
    private final UserManager userManager;
    
    GroupManager(@Nonnull final UserManager userManager, @Nonnull final DaoSession daoSession, @Nonnull final ChatterList chatterList) {
        this.avatarGroupListRef = new AtomicReference<AvatarGroupList>();
        this.groupMemberDataSetPool = new SubscriptionPool<UUID, UUID>();
        this.groupRoleMemberDataSetPool = new SubscriptionPool<UUID, UUID>();
        this.groupMembersSubscriptionPool = new SubscriptionPool<GroupMembersQuery, LazyList<GroupMember>>();
        this.groupRoleMemberSubscriptionPool = new SubscriptionPool<GroupRoleMembersQuery, LazyList<GroupRoleMember>>();
        this.groupMemberRolesSubscriptionPool = new SubscriptionPool<GroupMemberRolesQuery, Set<UUID>>();
        this.onGroupListUpdated = new OnListUpdated() {
            @Override
            public void onListUpdated() {
                GroupManager.this.chatterList.notifyListUpdated(ChatterListType.Groups);
            }
        };
        this.userManager = userManager;
        this.chatterList = chatterList;
        this.groupMemberDao = daoSession.getGroupMemberDao();
        this.groupMemberListDao = daoSession.getGroupMemberListDao();
        this.groupRoleMemberDao = daoSession.getGroupRoleMemberDao();
        this.groupRoleMemberListDao = daoSession.getGroupRoleMemberListDao();
        this.subscription = userManager.getAvatarGroupLists().getPool().subscribe(userManager.getUserID(), new _$Lambda$u_XXTkSOKCgaVXhhU_plrxzPP28$2(this));
        this.groupMemberDataSetHandler = new RateLimitRequestHandler<UUID, UUID>(new RequestProcessor<UUID, UUID, UUID>(this.groupMemberDataSetPool, userManager.getDatabaseExecutor()) {
            @Nullable
            @Override
            protected UUID processRequest(@Nonnull final UUID uuid) {
                final GroupMemberList list = GroupManager.this.groupMemberListDao.load(uuid);
                if (list != null) {
                    return list.getRequestID();
                }
                return null;
            }
            
            @Override
            protected UUID processResult(@Nonnull final UUID uuid, final UUID uuid2) {
                ((AbstractDao<GroupMemberList, K>)GroupManager.this.groupMemberListDao).insertOrReplace(new GroupMemberList(uuid, uuid2));
                return uuid2;
            }
        });
        this.groupRoleMemberDataSetHandler = new RateLimitRequestHandler<UUID, UUID>(new RequestProcessor<UUID, UUID, UUID>(this.groupRoleMemberDataSetPool, userManager.getDatabaseExecutor()) {
            @Override
            protected boolean isRequestComplete(@Nonnull final UUID uuid, final UUID uuid2) {
                final GroupRoleMemberList list = GroupManager.this.groupRoleMemberListDao.load(uuid);
                return list != null && (list.getMustRevalidate() ^ true);
            }
            
            @Nullable
            @Override
            protected UUID processRequest(@Nonnull final UUID uuid) {
                final GroupRoleMemberList list = GroupManager.this.groupRoleMemberListDao.load(uuid);
                if (list != null) {
                    return list.getRequestID();
                }
                return null;
            }
            
            @Override
            protected UUID processResult(@Nonnull final UUID uuid, final UUID uuid2) {
                ((AbstractDao<GroupRoleMemberList, K>)GroupManager.this.groupRoleMemberListDao).insertOrReplace(new GroupRoleMemberList(uuid, uuid2, false));
                return uuid2;
            }
        });
        this.groupRoleMemberSubscriptionPool.attachRequestHandler(new AsyncRequestHandler<GroupRoleMembersQuery>(userManager.getDatabaseExecutor(), new SimpleRequestHandler<GroupRoleMembersQuery>() {
            @Override
            public void onRequest(@Nonnull final GroupRoleMembersQuery groupRoleMembersQuery) {
                GroupManager.this.groupRoleMemberSubscriptionPool.onResultData(groupRoleMembersQuery, ((AbstractDao<GroupRoleMember, K>)GroupManager.this.groupRoleMemberDao).queryBuilder().where(GroupRoleMemberDao.Properties.GroupID.eq(groupRoleMembersQuery.groupID()), GroupRoleMemberDao.Properties.RoleID.eq(groupRoleMembersQuery.roleID()), GroupRoleMemberDao.Properties.RequestID.eq(groupRoleMembersQuery.requestID())).listLazyUncached());
            }
        }));
        this.groupRoleMemberSubscriptionPool.setDisposeHandler(new _$Lambda$u_XXTkSOKCgaVXhhU_plrxzPP28(), userManager.getDatabaseExecutor());
        this.groupMembersSubscriptionPool.attachRequestHandler(new AsyncRequestHandler<GroupMembersQuery>(userManager.getDatabaseExecutor(), new SimpleRequestHandler<GroupMembersQuery>() {
            @Override
            public void onRequest(@Nonnull final GroupMembersQuery groupMembersQuery) {
                GroupManager.this.groupMembersSubscriptionPool.onResultData(groupMembersQuery, ((AbstractDao<GroupMember, K>)GroupManager.this.groupMemberDao).queryBuilder().where(GroupMemberDao.Properties.GroupID.eq(groupMembersQuery.groupID()), GroupMemberDao.Properties.RequestID.eq(groupMembersQuery.requestID())).listLazyUncached());
            }
        }));
        this.groupMembersSubscriptionPool.setDisposeHandler(new _$Lambda$u_XXTkSOKCgaVXhhU_plrxzPP28$1(), userManager.getDatabaseExecutor());
        this.groupMemberRolesSubscriptionPool.attachRequestHandler(new AsyncRequestHandler<GroupMemberRolesQuery>(userManager.getDatabaseExecutor(), new SimpleRequestHandler<GroupMemberRolesQuery>() {
            @Override
            public void onRequest(@Nonnull final GroupMemberRolesQuery groupMemberRolesQuery) {
                final LazyList<GroupRoleMember> listLazyUncached = ((AbstractDao<GroupRoleMember, K>)GroupManager.this.groupRoleMemberDao).queryBuilder().where(GroupRoleMemberDao.Properties.GroupID.eq(groupMemberRolesQuery.groupID()), GroupRoleMemberDao.Properties.UserID.eq(groupMemberRolesQuery.memberID()), GroupRoleMemberDao.Properties.RequestID.eq(groupMemberRolesQuery.requestID())).listLazyUncached();
                final ImmutableSet.Builder<UUID> builder = ImmutableSet.builder();
                final Iterator<Object> iterator = listLazyUncached.iterator();
                while (iterator.hasNext()) {
                    builder.add(iterator.next().getRoleID());
                }
                listLazyUncached.close();
                GroupManager.this.groupMemberRolesSubscriptionPool.onResultData(groupMemberRolesQuery, builder.build());
            }
        }));
    }
    
    private void onAvatarGroupListsReply(final AvatarGroupList newValue) {
        this.avatarGroupListRef.set(newValue);
        this.chatterList.notifyListUpdated(ChatterListType.Groups);
    }
    
    @Nullable
    public AvatarGroupList getAvatarGroupList() {
        return this.avatarGroupListRef.get();
    }
    
    ChatterDisplayDataList getGroupList() {
        return new GroupDisplayDataList(this.userManager, this.onGroupListUpdated);
    }
    
    public RequestSource<UUID, UUID> getGroupMemberDataSetRequestSource() {
        return this.groupMemberDataSetHandler;
    }
    
    public Subscribable<GroupMemberRolesQuery, Set<UUID>> getGroupMemberRoleList() {
        return this.groupMemberRolesSubscriptionPool;
    }
    
    public Subscribable<UUID, UUID> getGroupMembers() {
        return this.groupMemberDataSetPool;
    }
    
    public Subscribable<GroupMembersQuery, LazyList<GroupMember>> getGroupMembersList() {
        return this.groupMembersSubscriptionPool;
    }
    
    public RequestSource<UUID, UUID> getGroupRoleMemberDataSetRequestSource() {
        return this.groupRoleMemberDataSetHandler;
    }
    
    public Subscribable<GroupRoleMembersQuery, LazyList<GroupRoleMember>> getGroupRoleMemberList() {
        return this.groupRoleMemberSubscriptionPool;
    }
    
    public Subscribable<UUID, UUID> getGroupRoleMembers() {
        return this.groupRoleMemberDataSetPool;
    }
    
    public void requestGroupRoleMembersRefresh(final UUID uuid) {
        this.userManager.getDatabaseExecutor().execute(new _$Lambda$u_XXTkSOKCgaVXhhU_plrxzPP28$3(this, uuid));
    }
    
    public void requestRefreshMemberList(final UUID uuid) {
        this.groupMemberDataSetPool.requestUpdate(uuid);
    }
    
    public abstract static class GroupMemberRolesQuery
    {
        public static GroupMemberRolesQuery create(final UUID uuid, final UUID uuid2, final UUID uuid3) {
            return (GroupMemberRolesQuery)new AutoValue_GroupManager_GroupMemberRolesQuery(uuid, uuid2, uuid3);
        }
        
        public abstract UUID groupID();
        
        public abstract UUID memberID();
        
        public abstract UUID requestID();
    }
    
    public abstract static class GroupMembersQuery
    {
        public static GroupMembersQuery create(final UUID uuid, final UUID uuid2) {
            return (GroupMembersQuery)new AutoValue_GroupManager_GroupMembersQuery(uuid, uuid2);
        }
        
        public abstract UUID groupID();
        
        public abstract UUID requestID();
    }
    
    public abstract static class GroupRoleMembersQuery
    {
        public static GroupRoleMembersQuery create(final UUID uuid, final UUID uuid2, final UUID uuid3) {
            return (GroupRoleMembersQuery)new AutoValue_GroupManager_GroupRoleMembersQuery(uuid, uuid2, uuid3);
        }
        
        public abstract UUID groupID();
        
        public abstract UUID requestID();
        
        public abstract UUID roleID();
    }
}
