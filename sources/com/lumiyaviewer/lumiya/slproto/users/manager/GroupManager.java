// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.collect.ImmutableSet;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.dao.GroupMemberDao;
import com.lumiyaviewer.lumiya.dao.GroupMemberList;
import com.lumiyaviewer.lumiya.dao.GroupMemberListDao;
import com.lumiyaviewer.lumiya.dao.GroupRoleMember;
import com.lumiyaviewer.lumiya.dao.GroupRoleMemberDao;
import com.lumiyaviewer.lumiya.dao.GroupRoleMemberList;
import com.lumiyaviewer.lumiya.dao.GroupRoleMemberListDao;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import com.lumiyaviewer.lumiya.react.RateLimitRequestHandler;
import com.lumiyaviewer.lumiya.react.RequestProcessor;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.users.SerializableResponseCacher;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UserManager, ChatterListType, ChatterList, GroupDisplayDataList, 
//            OnListUpdated, ChatterDisplayDataList, AutoValue_GroupManager_GroupMemberRolesQuery, AutoValue_GroupManager_GroupMembersQuery, 
//            AutoValue_GroupManager_GroupRoleMembersQuery

public class GroupManager
{
    public static abstract class GroupMemberRolesQuery
    {

        public static GroupMemberRolesQuery create(UUID uuid, UUID uuid1, UUID uuid2)
        {
            return new AutoValue_GroupManager_GroupMemberRolesQuery(uuid, uuid1, uuid2);
        }

        public abstract UUID groupID();

        public abstract UUID memberID();

        public abstract UUID requestID();

        public GroupMemberRolesQuery()
        {
        }
    }

    public static abstract class GroupMembersQuery
    {

        public static GroupMembersQuery create(UUID uuid, UUID uuid1)
        {
            return new AutoValue_GroupManager_GroupMembersQuery(uuid, uuid1);
        }

        public abstract UUID groupID();

        public abstract UUID requestID();

        public GroupMembersQuery()
        {
        }
    }

    public static abstract class GroupRoleMembersQuery
    {

        public static GroupRoleMembersQuery create(UUID uuid, UUID uuid1, UUID uuid2)
        {
            return new AutoValue_GroupManager_GroupRoleMembersQuery(uuid, uuid1, uuid2);
        }

        public abstract UUID groupID();

        public abstract UUID requestID();

        public abstract UUID roleID();

        public GroupRoleMembersQuery()
        {
        }
    }


    private final AtomicReference avatarGroupListRef = new AtomicReference();
    private final ChatterList chatterList;
    private final GroupMemberDao groupMemberDao;
    private final RateLimitRequestHandler groupMemberDataSetHandler;
    private final SubscriptionPool groupMemberDataSetPool = new SubscriptionPool();
    private final GroupMemberListDao groupMemberListDao;
    private final SubscriptionPool groupMemberRolesSubscriptionPool = new SubscriptionPool();
    private final SubscriptionPool groupMembersSubscriptionPool = new SubscriptionPool();
    private final GroupRoleMemberDao groupRoleMemberDao;
    private final RateLimitRequestHandler groupRoleMemberDataSetHandler;
    private final SubscriptionPool groupRoleMemberDataSetPool = new SubscriptionPool();
    private final GroupRoleMemberListDao groupRoleMemberListDao;
    private final SubscriptionPool groupRoleMemberSubscriptionPool = new SubscriptionPool();
    private final OnListUpdated onGroupListUpdated = new OnListUpdated() {

        final GroupManager this$0;

        public void onListUpdated()
        {
            GroupManager._2D_get0(GroupManager.this).notifyListUpdated(ChatterListType.Groups);
        }

            
            {
                this$0 = GroupManager.this;
                super();
            }
    };
    private final Subscription subscription;
    private final UserManager userManager;

    static void _2D_com_lumiyaviewer_lumiya_slproto_users_manager_GroupManager_2D_mthref_2D_1(LazyList lazylist)
    {
        lazylist.close();
    }

    static void _2D_com_lumiyaviewer_lumiya_slproto_users_manager_GroupManager_2D_mthref_2D_2(LazyList lazylist)
    {
        lazylist.close();
    }

    static ChatterList _2D_get0(GroupManager groupmanager)
    {
        return groupmanager.chatterList;
    }

    static GroupMemberDao _2D_get1(GroupManager groupmanager)
    {
        return groupmanager.groupMemberDao;
    }

    static GroupMemberListDao _2D_get2(GroupManager groupmanager)
    {
        return groupmanager.groupMemberListDao;
    }

    static SubscriptionPool _2D_get3(GroupManager groupmanager)
    {
        return groupmanager.groupMemberRolesSubscriptionPool;
    }

    static SubscriptionPool _2D_get4(GroupManager groupmanager)
    {
        return groupmanager.groupMembersSubscriptionPool;
    }

    static GroupRoleMemberDao _2D_get5(GroupManager groupmanager)
    {
        return groupmanager.groupRoleMemberDao;
    }

    static GroupRoleMemberListDao _2D_get6(GroupManager groupmanager)
    {
        return groupmanager.groupRoleMemberListDao;
    }

    static SubscriptionPool _2D_get7(GroupManager groupmanager)
    {
        return groupmanager.groupRoleMemberSubscriptionPool;
    }

    GroupManager(UserManager usermanager, DaoSession daosession, ChatterList chatterlist)
    {
        userManager = usermanager;
        chatterList = chatterlist;
        groupMemberDao = daosession.getGroupMemberDao();
        groupMemberListDao = daosession.getGroupMemberListDao();
        groupRoleMemberDao = daosession.getGroupRoleMemberDao();
        groupRoleMemberListDao = daosession.getGroupRoleMemberListDao();
        subscription = usermanager.getAvatarGroupLists().getPool().subscribe(usermanager.getUserID(), new _2D_.Lambda.u_XXTkSOKCgaVXhhU_2D_plrxzPP28._cls2(this));
        groupMemberDataSetHandler = new RateLimitRequestHandler(new RequestProcessor(groupMemberDataSetPool, usermanager.getDatabaseExecutor()) {

            final GroupManager this$0;

            protected volatile Object processRequest(Object obj)
            {
                return processRequest((UUID)obj);
            }

            protected UUID processRequest(UUID uuid)
            {
                uuid = (GroupMemberList)GroupManager._2D_get2(GroupManager.this).load(uuid);
                if (uuid != null)
                {
                    return uuid.getRequestID();
                } else
                {
                    return null;
                }
            }

            protected volatile Object processResult(Object obj, Object obj1)
            {
                return processResult((UUID)obj, (UUID)obj1);
            }

            protected UUID processResult(UUID uuid, UUID uuid1)
            {
                uuid = new GroupMemberList(uuid, uuid1);
                GroupManager._2D_get2(GroupManager.this).insertOrReplace(uuid);
                return uuid1;
            }

            
            {
                this$0 = GroupManager.this;
                super(requestsource, executor);
            }
        });
        groupRoleMemberDataSetHandler = new RateLimitRequestHandler(new RequestProcessor(groupRoleMemberDataSetPool, usermanager.getDatabaseExecutor()) {

            final GroupManager this$0;

            protected volatile boolean isRequestComplete(Object obj, Object obj1)
            {
                return isRequestComplete((UUID)obj, (UUID)obj1);
            }

            protected boolean isRequestComplete(UUID uuid, UUID uuid1)
            {
                uuid = (GroupRoleMemberList)GroupManager._2D_get6(GroupManager.this).load(uuid);
                if (uuid != null)
                {
                    return uuid.getMustRevalidate() ^ true;
                } else
                {
                    return false;
                }
            }

            protected volatile Object processRequest(Object obj)
            {
                return processRequest((UUID)obj);
            }

            protected UUID processRequest(UUID uuid)
            {
                uuid = (GroupRoleMemberList)GroupManager._2D_get6(GroupManager.this).load(uuid);
                if (uuid != null)
                {
                    return uuid.getRequestID();
                } else
                {
                    return null;
                }
            }

            protected volatile Object processResult(Object obj, Object obj1)
            {
                return processResult((UUID)obj, (UUID)obj1);
            }

            protected UUID processResult(UUID uuid, UUID uuid1)
            {
                uuid = new GroupRoleMemberList(uuid, uuid1, false);
                GroupManager._2D_get6(GroupManager.this).insertOrReplace(uuid);
                return uuid1;
            }

            
            {
                this$0 = GroupManager.this;
                super(requestsource, executor);
            }
        });
        groupRoleMemberSubscriptionPool.attachRequestHandler(new AsyncRequestHandler(usermanager.getDatabaseExecutor(), new SimpleRequestHandler() {

            final GroupManager this$0;

            public void onRequest(GroupRoleMembersQuery grouprolemembersquery)
            {
                LazyList lazylist = GroupManager._2D_get5(GroupManager.this).queryBuilder().where(com.lumiyaviewer.lumiya.dao.GroupRoleMemberDao.Properties.GroupID.eq(grouprolemembersquery.groupID()), new WhereCondition[] {
                    com.lumiyaviewer.lumiya.dao.GroupRoleMemberDao.Properties.RoleID.eq(grouprolemembersquery.roleID()), com.lumiyaviewer.lumiya.dao.GroupRoleMemberDao.Properties.RequestID.eq(grouprolemembersquery.requestID())
                }).listLazyUncached();
                GroupManager._2D_get7(GroupManager.this).onResultData(grouprolemembersquery, lazylist);
            }

            public volatile void onRequest(Object obj)
            {
                onRequest((GroupRoleMembersQuery)obj);
            }

            
            {
                this$0 = GroupManager.this;
                super();
            }
        }));
        groupRoleMemberSubscriptionPool.setDisposeHandler(new _2D_.Lambda.u_XXTkSOKCgaVXhhU_2D_plrxzPP28(), usermanager.getDatabaseExecutor());
        groupMembersSubscriptionPool.attachRequestHandler(new AsyncRequestHandler(usermanager.getDatabaseExecutor(), new SimpleRequestHandler() {

            final GroupManager this$0;

            public void onRequest(GroupMembersQuery groupmembersquery)
            {
                GroupManager._2D_get4(GroupManager.this).onResultData(groupmembersquery, GroupManager._2D_get1(GroupManager.this).queryBuilder().where(com.lumiyaviewer.lumiya.dao.GroupMemberDao.Properties.GroupID.eq(groupmembersquery.groupID()), new WhereCondition[] {
                    com.lumiyaviewer.lumiya.dao.GroupMemberDao.Properties.RequestID.eq(groupmembersquery.requestID())
                }).listLazyUncached());
            }

            public volatile void onRequest(Object obj)
            {
                onRequest((GroupMembersQuery)obj);
            }

            
            {
                this$0 = GroupManager.this;
                super();
            }
        }));
        groupMembersSubscriptionPool.setDisposeHandler(new _2D_.Lambda.u_XXTkSOKCgaVXhhU_2D_plrxzPP28._cls1(), usermanager.getDatabaseExecutor());
        groupMemberRolesSubscriptionPool.attachRequestHandler(new AsyncRequestHandler(usermanager.getDatabaseExecutor(), new SimpleRequestHandler() {

            final GroupManager this$0;

            public void onRequest(GroupMemberRolesQuery groupmemberrolesquery)
            {
                LazyList lazylist = GroupManager._2D_get5(GroupManager.this).queryBuilder().where(com.lumiyaviewer.lumiya.dao.GroupRoleMemberDao.Properties.GroupID.eq(groupmemberrolesquery.groupID()), new WhereCondition[] {
                    com.lumiyaviewer.lumiya.dao.GroupRoleMemberDao.Properties.UserID.eq(groupmemberrolesquery.memberID()), com.lumiyaviewer.lumiya.dao.GroupRoleMemberDao.Properties.RequestID.eq(groupmemberrolesquery.requestID())
                }).listLazyUncached();
                com.google.common.collect.ImmutableSet.Builder builder = ImmutableSet.builder();
                for (Iterator iterator = lazylist.iterator(); iterator.hasNext(); builder.add(((GroupRoleMember)iterator.next()).getRoleID())) { }
                lazylist.close();
                GroupManager._2D_get3(GroupManager.this).onResultData(groupmemberrolesquery, builder.build());
            }

            public volatile void onRequest(Object obj)
            {
                onRequest((GroupMemberRolesQuery)obj);
            }

            
            {
                this$0 = GroupManager.this;
                super();
            }
        }));
    }

    private void onAvatarGroupListsReply(AvatarGroupList avatargrouplist)
    {
        avatarGroupListRef.set(avatargrouplist);
        chatterList.notifyListUpdated(ChatterListType.Groups);
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_users_manager_GroupManager_2D_mthref_2D_0(AvatarGroupList avatargrouplist)
    {
        onAvatarGroupListsReply(avatargrouplist);
    }

    public AvatarGroupList getAvatarGroupList()
    {
        return (AvatarGroupList)avatarGroupListRef.get();
    }

    ChatterDisplayDataList getGroupList()
    {
        return new GroupDisplayDataList(userManager, onGroupListUpdated);
    }

    public RequestSource getGroupMemberDataSetRequestSource()
    {
        return groupMemberDataSetHandler;
    }

    public Subscribable getGroupMemberRoleList()
    {
        return groupMemberRolesSubscriptionPool;
    }

    public Subscribable getGroupMembers()
    {
        return groupMemberDataSetPool;
    }

    public Subscribable getGroupMembersList()
    {
        return groupMembersSubscriptionPool;
    }

    public RequestSource getGroupRoleMemberDataSetRequestSource()
    {
        return groupRoleMemberDataSetHandler;
    }

    public Subscribable getGroupRoleMemberList()
    {
        return groupRoleMemberSubscriptionPool;
    }

    public Subscribable getGroupRoleMembers()
    {
        return groupRoleMemberDataSetPool;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_GroupManager_10304(UUID uuid)
    {
        GroupRoleMemberList grouprolememberlist = (GroupRoleMemberList)groupRoleMemberListDao.load(uuid);
        if (grouprolememberlist != null)
        {
            grouprolememberlist.setMustRevalidate(true);
            groupRoleMemberListDao.update(grouprolememberlist);
        }
        groupRoleMemberDataSetPool.requestUpdate(uuid);
    }

    public void requestGroupRoleMembersRefresh(UUID uuid)
    {
        userManager.getDatabaseExecutor().execute(new _2D_.Lambda.u_XXTkSOKCgaVXhhU_2D_plrxzPP28._cls3(this, uuid));
    }

    public void requestRefreshMemberList(UUID uuid)
    {
        groupMemberDataSetPool.requestUpdate(uuid);
    }
}
