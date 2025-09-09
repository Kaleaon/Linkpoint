package com.lumiyaviewer.lumiya.dao;

import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.Map;

public class DaoSession extends AbstractDaoSession {
    private final CachedAssetDao cachedAssetDao = new CachedAssetDao(this.cachedAssetDaoConfig, this);
    private final DaoConfig cachedAssetDaoConfig;
    private final CachedResponseDao cachedResponseDao = new CachedResponseDao(this.cachedResponseDaoConfig, this);
    private final DaoConfig cachedResponseDaoConfig;
    private final ChatMessageDao chatMessageDao = new ChatMessageDao(this.chatMessageDaoConfig, this);
    private final DaoConfig chatMessageDaoConfig;
    private final ChatterDao chatterDao = new ChatterDao(this.chatterDaoConfig, this);
    private final DaoConfig chatterDaoConfig;
    private final FriendDao friendDao = new FriendDao(this.friendDaoConfig, this);
    private final DaoConfig friendDaoConfig;
    private final GroupMemberDao groupMemberDao = new GroupMemberDao(this.groupMemberDaoConfig, this);
    private final DaoConfig groupMemberDaoConfig;
    private final GroupMemberListDao groupMemberListDao = new GroupMemberListDao(this.groupMemberListDaoConfig, this);
    private final DaoConfig groupMemberListDaoConfig;
    private final GroupRoleMemberDao groupRoleMemberDao = new GroupRoleMemberDao(this.groupRoleMemberDaoConfig, this);
    private final DaoConfig groupRoleMemberDaoConfig;
    private final GroupRoleMemberListDao groupRoleMemberListDao = new GroupRoleMemberListDao(this.groupRoleMemberListDaoConfig, this);
    private final DaoConfig groupRoleMemberListDaoConfig;
    private final MoneyTransactionDao moneyTransactionDao = new MoneyTransactionDao(this.moneyTransactionDaoConfig, this);
    private final DaoConfig moneyTransactionDaoConfig;
    private final MuteListCachedDataDao muteListCachedDataDao = new MuteListCachedDataDao(this.muteListCachedDataDaoConfig, this);
    private final DaoConfig muteListCachedDataDaoConfig;
    private final SearchGridResultDao searchGridResultDao = new SearchGridResultDao(this.searchGridResultDaoConfig, this);
    private final DaoConfig searchGridResultDaoConfig;
    private final UserDao userDao = new UserDao(this.userDaoConfig, this);
    private final DaoConfig userDaoConfig;
    private final UserNameDao userNameDao = new UserNameDao(this.userNameDaoConfig, this);
    private final DaoConfig userNameDaoConfig;
    private final UserPicDao userPicDao = new UserPicDao(this.userPicDaoConfig, this);
    private final DaoConfig userPicDaoConfig;

    public DaoSession(SQLiteDatabase sQLiteDatabase, IdentityScopeType identityScopeType, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> map) {
        super(sQLiteDatabase);
        this.cachedResponseDaoConfig = map.get(CachedResponseDao.class).clone();
        this.cachedResponseDaoConfig.initIdentityScope(identityScopeType);
        this.cachedAssetDaoConfig = map.get(CachedAssetDao.class).clone();
        this.cachedAssetDaoConfig.initIdentityScope(identityScopeType);
        this.moneyTransactionDaoConfig = map.get(MoneyTransactionDao.class).clone();
        this.moneyTransactionDaoConfig.initIdentityScope(identityScopeType);
        this.muteListCachedDataDaoConfig = map.get(MuteListCachedDataDao.class).clone();
        this.muteListCachedDataDaoConfig.initIdentityScope(identityScopeType);
        this.searchGridResultDaoConfig = map.get(SearchGridResultDao.class).clone();
        this.searchGridResultDaoConfig.initIdentityScope(identityScopeType);
        this.groupMemberDaoConfig = map.get(GroupMemberDao.class).clone();
        this.groupMemberDaoConfig.initIdentityScope(identityScopeType);
        this.groupMemberListDaoConfig = map.get(GroupMemberListDao.class).clone();
        this.groupMemberListDaoConfig.initIdentityScope(identityScopeType);
        this.groupRoleMemberDaoConfig = map.get(GroupRoleMemberDao.class).clone();
        this.groupRoleMemberDaoConfig.initIdentityScope(identityScopeType);
        this.groupRoleMemberListDaoConfig = map.get(GroupRoleMemberListDao.class).clone();
        this.groupRoleMemberListDaoConfig.initIdentityScope(identityScopeType);
        this.userDaoConfig = map.get(UserDao.class).clone();
        this.userDaoConfig.initIdentityScope(identityScopeType);
        this.friendDaoConfig = map.get(FriendDao.class).clone();
        this.friendDaoConfig.initIdentityScope(identityScopeType);
        this.userNameDaoConfig = map.get(UserNameDao.class).clone();
        this.userNameDaoConfig.initIdentityScope(identityScopeType);
        this.userPicDaoConfig = map.get(UserPicDao.class).clone();
        this.userPicDaoConfig.initIdentityScope(identityScopeType);
        this.chatMessageDaoConfig = map.get(ChatMessageDao.class).clone();
        this.chatMessageDaoConfig.initIdentityScope(identityScopeType);
        this.chatterDaoConfig = map.get(ChatterDao.class).clone();
        this.chatterDaoConfig.initIdentityScope(identityScopeType);
        registerDao(CachedResponse.class, this.cachedResponseDao);
        registerDao(CachedAsset.class, this.cachedAssetDao);
        registerDao(MoneyTransaction.class, this.moneyTransactionDao);
        registerDao(MuteListCachedData.class, this.muteListCachedDataDao);
        registerDao(SearchGridResult.class, this.searchGridResultDao);
        registerDao(GroupMember.class, this.groupMemberDao);
        registerDao(GroupMemberList.class, this.groupMemberListDao);
        registerDao(GroupRoleMember.class, this.groupRoleMemberDao);
        registerDao(GroupRoleMemberList.class, this.groupRoleMemberListDao);
        registerDao(User.class, this.userDao);
        registerDao(Friend.class, this.friendDao);
        registerDao(UserName.class, this.userNameDao);
        registerDao(UserPic.class, this.userPicDao);
        registerDao(ChatMessage.class, this.chatMessageDao);
        registerDao(Chatter.class, this.chatterDao);
    }

    public void clear() {
        this.cachedResponseDaoConfig.getIdentityScope().clear();
        this.cachedAssetDaoConfig.getIdentityScope().clear();
        this.moneyTransactionDaoConfig.getIdentityScope().clear();
        this.muteListCachedDataDaoConfig.getIdentityScope().clear();
        this.searchGridResultDaoConfig.getIdentityScope().clear();
        this.groupMemberDaoConfig.getIdentityScope().clear();
        this.groupMemberListDaoConfig.getIdentityScope().clear();
        this.groupRoleMemberDaoConfig.getIdentityScope().clear();
        this.groupRoleMemberListDaoConfig.getIdentityScope().clear();
        this.userDaoConfig.getIdentityScope().clear();
        this.friendDaoConfig.getIdentityScope().clear();
        this.userNameDaoConfig.getIdentityScope().clear();
        this.userPicDaoConfig.getIdentityScope().clear();
        this.chatMessageDaoConfig.getIdentityScope().clear();
        this.chatterDaoConfig.getIdentityScope().clear();
    }

    public CachedAssetDao getCachedAssetDao() {
        return this.cachedAssetDao;
    }

    public CachedResponseDao getCachedResponseDao() {
        return this.cachedResponseDao;
    }

    public ChatMessageDao getChatMessageDao() {
        return this.chatMessageDao;
    }

    public ChatterDao getChatterDao() {
        return this.chatterDao;
    }

    public FriendDao getFriendDao() {
        return this.friendDao;
    }

    public GroupMemberDao getGroupMemberDao() {
        return this.groupMemberDao;
    }

    public GroupMemberListDao getGroupMemberListDao() {
        return this.groupMemberListDao;
    }

    public GroupRoleMemberDao getGroupRoleMemberDao() {
        return this.groupRoleMemberDao;
    }

    public GroupRoleMemberListDao getGroupRoleMemberListDao() {
        return this.groupRoleMemberListDao;
    }

    public MoneyTransactionDao getMoneyTransactionDao() {
        return this.moneyTransactionDao;
    }

    public MuteListCachedDataDao getMuteListCachedDataDao() {
        return this.muteListCachedDataDao;
    }

    public SearchGridResultDao getSearchGridResultDao() {
        return this.searchGridResultDao;
    }

    public UserDao getUserDao() {
        return this.userDao;
    }

    public UserNameDao getUserNameDao() {
        return this.userNameDao;
    }

    public UserPicDao getUserPicDao() {
        return this.userPicDao;
    }
}
