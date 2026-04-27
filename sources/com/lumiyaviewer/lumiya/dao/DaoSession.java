// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScope;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.Map;

// Referenced classes of package com.lumiyaviewer.lumiya.dao:
//            CachedResponseDao, CachedAssetDao, MoneyTransactionDao, MuteListCachedDataDao, 
//            SearchGridResultDao, GroupMemberDao, GroupMemberListDao, GroupRoleMemberDao, 
//            GroupRoleMemberListDao, UserDao, FriendDao, UserNameDao, 
//            UserPicDao, ChatMessageDao, ChatterDao, CachedResponse, 
//            CachedAsset, MoneyTransaction, MuteListCachedData, SearchGridResult, 
//            GroupMember, GroupMemberList, GroupRoleMember, GroupRoleMemberList, 
//            User, Friend, UserName, UserPic, 
//            ChatMessage, Chatter

public class DaoSession extends AbstractDaoSession
{

    private final CachedAssetDao cachedAssetDao;
    private final DaoConfig cachedAssetDaoConfig;
    private final CachedResponseDao cachedResponseDao;
    private final DaoConfig cachedResponseDaoConfig;
    private final ChatMessageDao chatMessageDao;
    private final DaoConfig chatMessageDaoConfig;
    private final ChatterDao chatterDao;
    private final DaoConfig chatterDaoConfig;
    private final FriendDao friendDao;
    private final DaoConfig friendDaoConfig;
    private final GroupMemberDao groupMemberDao;
    private final DaoConfig groupMemberDaoConfig;
    private final GroupMemberListDao groupMemberListDao;
    private final DaoConfig groupMemberListDaoConfig;
    private final GroupRoleMemberDao groupRoleMemberDao;
    private final DaoConfig groupRoleMemberDaoConfig;
    private final GroupRoleMemberListDao groupRoleMemberListDao;
    private final DaoConfig groupRoleMemberListDaoConfig;
    private final MoneyTransactionDao moneyTransactionDao;
    private final DaoConfig moneyTransactionDaoConfig;
    private final MuteListCachedDataDao muteListCachedDataDao;
    private final DaoConfig muteListCachedDataDaoConfig;
    private final SearchGridResultDao searchGridResultDao;
    private final DaoConfig searchGridResultDaoConfig;
    private final UserDao userDao;
    private final DaoConfig userDaoConfig;
    private final UserNameDao userNameDao;
    private final DaoConfig userNameDaoConfig;
    private final UserPicDao userPicDao;
    private final DaoConfig userPicDaoConfig;

    public DaoSession(SQLiteDatabase sqlitedatabase, IdentityScopeType identityscopetype, Map map)
    {
        super(sqlitedatabase);
        cachedResponseDaoConfig = ((DaoConfig)map.get(com/lumiyaviewer/lumiya/dao/CachedResponseDao)).clone();
        cachedResponseDaoConfig.initIdentityScope(identityscopetype);
        cachedAssetDaoConfig = ((DaoConfig)map.get(com/lumiyaviewer/lumiya/dao/CachedAssetDao)).clone();
        cachedAssetDaoConfig.initIdentityScope(identityscopetype);
        moneyTransactionDaoConfig = ((DaoConfig)map.get(com/lumiyaviewer/lumiya/dao/MoneyTransactionDao)).clone();
        moneyTransactionDaoConfig.initIdentityScope(identityscopetype);
        muteListCachedDataDaoConfig = ((DaoConfig)map.get(com/lumiyaviewer/lumiya/dao/MuteListCachedDataDao)).clone();
        muteListCachedDataDaoConfig.initIdentityScope(identityscopetype);
        searchGridResultDaoConfig = ((DaoConfig)map.get(com/lumiyaviewer/lumiya/dao/SearchGridResultDao)).clone();
        searchGridResultDaoConfig.initIdentityScope(identityscopetype);
        groupMemberDaoConfig = ((DaoConfig)map.get(com/lumiyaviewer/lumiya/dao/GroupMemberDao)).clone();
        groupMemberDaoConfig.initIdentityScope(identityscopetype);
        groupMemberListDaoConfig = ((DaoConfig)map.get(com/lumiyaviewer/lumiya/dao/GroupMemberListDao)).clone();
        groupMemberListDaoConfig.initIdentityScope(identityscopetype);
        groupRoleMemberDaoConfig = ((DaoConfig)map.get(com/lumiyaviewer/lumiya/dao/GroupRoleMemberDao)).clone();
        groupRoleMemberDaoConfig.initIdentityScope(identityscopetype);
        groupRoleMemberListDaoConfig = ((DaoConfig)map.get(com/lumiyaviewer/lumiya/dao/GroupRoleMemberListDao)).clone();
        groupRoleMemberListDaoConfig.initIdentityScope(identityscopetype);
        userDaoConfig = ((DaoConfig)map.get(com/lumiyaviewer/lumiya/dao/UserDao)).clone();
        userDaoConfig.initIdentityScope(identityscopetype);
        friendDaoConfig = ((DaoConfig)map.get(com/lumiyaviewer/lumiya/dao/FriendDao)).clone();
        friendDaoConfig.initIdentityScope(identityscopetype);
        userNameDaoConfig = ((DaoConfig)map.get(com/lumiyaviewer/lumiya/dao/UserNameDao)).clone();
        userNameDaoConfig.initIdentityScope(identityscopetype);
        userPicDaoConfig = ((DaoConfig)map.get(com/lumiyaviewer/lumiya/dao/UserPicDao)).clone();
        userPicDaoConfig.initIdentityScope(identityscopetype);
        chatMessageDaoConfig = ((DaoConfig)map.get(com/lumiyaviewer/lumiya/dao/ChatMessageDao)).clone();
        chatMessageDaoConfig.initIdentityScope(identityscopetype);
        chatterDaoConfig = ((DaoConfig)map.get(com/lumiyaviewer/lumiya/dao/ChatterDao)).clone();
        chatterDaoConfig.initIdentityScope(identityscopetype);
        cachedResponseDao = new CachedResponseDao(cachedResponseDaoConfig, this);
        cachedAssetDao = new CachedAssetDao(cachedAssetDaoConfig, this);
        moneyTransactionDao = new MoneyTransactionDao(moneyTransactionDaoConfig, this);
        muteListCachedDataDao = new MuteListCachedDataDao(muteListCachedDataDaoConfig, this);
        searchGridResultDao = new SearchGridResultDao(searchGridResultDaoConfig, this);
        groupMemberDao = new GroupMemberDao(groupMemberDaoConfig, this);
        groupMemberListDao = new GroupMemberListDao(groupMemberListDaoConfig, this);
        groupRoleMemberDao = new GroupRoleMemberDao(groupRoleMemberDaoConfig, this);
        groupRoleMemberListDao = new GroupRoleMemberListDao(groupRoleMemberListDaoConfig, this);
        userDao = new UserDao(userDaoConfig, this);
        friendDao = new FriendDao(friendDaoConfig, this);
        userNameDao = new UserNameDao(userNameDaoConfig, this);
        userPicDao = new UserPicDao(userPicDaoConfig, this);
        chatMessageDao = new ChatMessageDao(chatMessageDaoConfig, this);
        chatterDao = new ChatterDao(chatterDaoConfig, this);
        registerDao(com/lumiyaviewer/lumiya/dao/CachedResponse, cachedResponseDao);
        registerDao(com/lumiyaviewer/lumiya/dao/CachedAsset, cachedAssetDao);
        registerDao(com/lumiyaviewer/lumiya/dao/MoneyTransaction, moneyTransactionDao);
        registerDao(com/lumiyaviewer/lumiya/dao/MuteListCachedData, muteListCachedDataDao);
        registerDao(com/lumiyaviewer/lumiya/dao/SearchGridResult, searchGridResultDao);
        registerDao(com/lumiyaviewer/lumiya/dao/GroupMember, groupMemberDao);
        registerDao(com/lumiyaviewer/lumiya/dao/GroupMemberList, groupMemberListDao);
        registerDao(com/lumiyaviewer/lumiya/dao/GroupRoleMember, groupRoleMemberDao);
        registerDao(com/lumiyaviewer/lumiya/dao/GroupRoleMemberList, groupRoleMemberListDao);
        registerDao(com/lumiyaviewer/lumiya/dao/User, userDao);
        registerDao(com/lumiyaviewer/lumiya/dao/Friend, friendDao);
        registerDao(com/lumiyaviewer/lumiya/dao/UserName, userNameDao);
        registerDao(com/lumiyaviewer/lumiya/dao/UserPic, userPicDao);
        registerDao(com/lumiyaviewer/lumiya/dao/ChatMessage, chatMessageDao);
        registerDao(com/lumiyaviewer/lumiya/dao/Chatter, chatterDao);
    }

    public void clear()
    {
        cachedResponseDaoConfig.getIdentityScope().clear();
        cachedAssetDaoConfig.getIdentityScope().clear();
        moneyTransactionDaoConfig.getIdentityScope().clear();
        muteListCachedDataDaoConfig.getIdentityScope().clear();
        searchGridResultDaoConfig.getIdentityScope().clear();
        groupMemberDaoConfig.getIdentityScope().clear();
        groupMemberListDaoConfig.getIdentityScope().clear();
        groupRoleMemberDaoConfig.getIdentityScope().clear();
        groupRoleMemberListDaoConfig.getIdentityScope().clear();
        userDaoConfig.getIdentityScope().clear();
        friendDaoConfig.getIdentityScope().clear();
        userNameDaoConfig.getIdentityScope().clear();
        userPicDaoConfig.getIdentityScope().clear();
        chatMessageDaoConfig.getIdentityScope().clear();
        chatterDaoConfig.getIdentityScope().clear();
    }

    public CachedAssetDao getCachedAssetDao()
    {
        return cachedAssetDao;
    }

    public CachedResponseDao getCachedResponseDao()
    {
        return cachedResponseDao;
    }

    public ChatMessageDao getChatMessageDao()
    {
        return chatMessageDao;
    }

    public ChatterDao getChatterDao()
    {
        return chatterDao;
    }

    public FriendDao getFriendDao()
    {
        return friendDao;
    }

    public GroupMemberDao getGroupMemberDao()
    {
        return groupMemberDao;
    }

    public GroupMemberListDao getGroupMemberListDao()
    {
        return groupMemberListDao;
    }

    public GroupRoleMemberDao getGroupRoleMemberDao()
    {
        return groupRoleMemberDao;
    }

    public GroupRoleMemberListDao getGroupRoleMemberListDao()
    {
        return groupRoleMemberListDao;
    }

    public MoneyTransactionDao getMoneyTransactionDao()
    {
        return moneyTransactionDao;
    }

    public MuteListCachedDataDao getMuteListCachedDataDao()
    {
        return muteListCachedDataDao;
    }

    public SearchGridResultDao getSearchGridResultDao()
    {
        return searchGridResultDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public UserNameDao getUserNameDao()
    {
        return userNameDao;
    }

    public UserPicDao getUserPicDao()
    {
        return userPicDao;
    }
}
