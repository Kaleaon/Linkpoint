// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.AbstractDao;
import java.util.Map;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.AbstractDaoSession;

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
    
    public DaoSession(final SQLiteDatabase sqLiteDatabase, final IdentityScopeType identityScopeType, final Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> map) {
        super(sqLiteDatabase);
        (this.cachedResponseDaoConfig = map.get(CachedResponseDao.class).clone()).initIdentityScope(identityScopeType);
        (this.cachedAssetDaoConfig = map.get(CachedAssetDao.class).clone()).initIdentityScope(identityScopeType);
        (this.moneyTransactionDaoConfig = map.get(MoneyTransactionDao.class).clone()).initIdentityScope(identityScopeType);
        (this.muteListCachedDataDaoConfig = map.get(MuteListCachedDataDao.class).clone()).initIdentityScope(identityScopeType);
        (this.searchGridResultDaoConfig = map.get(SearchGridResultDao.class).clone()).initIdentityScope(identityScopeType);
        (this.groupMemberDaoConfig = map.get(GroupMemberDao.class).clone()).initIdentityScope(identityScopeType);
        (this.groupMemberListDaoConfig = map.get(GroupMemberListDao.class).clone()).initIdentityScope(identityScopeType);
        (this.groupRoleMemberDaoConfig = map.get(GroupRoleMemberDao.class).clone()).initIdentityScope(identityScopeType);
        (this.groupRoleMemberListDaoConfig = map.get(GroupRoleMemberListDao.class).clone()).initIdentityScope(identityScopeType);
        (this.userDaoConfig = map.get(UserDao.class).clone()).initIdentityScope(identityScopeType);
        (this.friendDaoConfig = map.get(FriendDao.class).clone()).initIdentityScope(identityScopeType);
        (this.userNameDaoConfig = map.get(UserNameDao.class).clone()).initIdentityScope(identityScopeType);
        (this.userPicDaoConfig = map.get(UserPicDao.class).clone()).initIdentityScope(identityScopeType);
        (this.chatMessageDaoConfig = map.get(ChatMessageDao.class).clone()).initIdentityScope(identityScopeType);
        (this.chatterDaoConfig = map.get(ChatterDao.class).clone()).initIdentityScope(identityScopeType);
        this.cachedResponseDao = new CachedResponseDao(this.cachedResponseDaoConfig, this);
        this.cachedAssetDao = new CachedAssetDao(this.cachedAssetDaoConfig, this);
        this.moneyTransactionDao = new MoneyTransactionDao(this.moneyTransactionDaoConfig, this);
        this.muteListCachedDataDao = new MuteListCachedDataDao(this.muteListCachedDataDaoConfig, this);
        this.searchGridResultDao = new SearchGridResultDao(this.searchGridResultDaoConfig, this);
        this.groupMemberDao = new GroupMemberDao(this.groupMemberDaoConfig, this);
        this.groupMemberListDao = new GroupMemberListDao(this.groupMemberListDaoConfig, this);
        this.groupRoleMemberDao = new GroupRoleMemberDao(this.groupRoleMemberDaoConfig, this);
        this.groupRoleMemberListDao = new GroupRoleMemberListDao(this.groupRoleMemberListDaoConfig, this);
        this.userDao = new UserDao(this.userDaoConfig, this);
        this.friendDao = new FriendDao(this.friendDaoConfig, this);
        this.userNameDao = new UserNameDao(this.userNameDaoConfig, this);
        this.userPicDao = new UserPicDao(this.userPicDaoConfig, this);
        this.chatMessageDao = new ChatMessageDao(this.chatMessageDaoConfig, this);
        this.chatterDao = new ChatterDao(this.chatterDaoConfig, this);
        this.registerDao(CachedResponse.class, this.cachedResponseDao);
        this.registerDao(CachedAsset.class, this.cachedAssetDao);
        this.registerDao(MoneyTransaction.class, this.moneyTransactionDao);
        this.registerDao(MuteListCachedData.class, this.muteListCachedDataDao);
        this.registerDao(SearchGridResult.class, this.searchGridResultDao);
        this.registerDao(GroupMember.class, this.groupMemberDao);
        this.registerDao(GroupMemberList.class, this.groupMemberListDao);
        this.registerDao(GroupRoleMember.class, this.groupRoleMemberDao);
        this.registerDao(GroupRoleMemberList.class, this.groupRoleMemberListDao);
        this.registerDao(User.class, this.userDao);
        this.registerDao(Friend.class, this.friendDao);
        this.registerDao(UserName.class, this.userNameDao);
        this.registerDao(UserPic.class, this.userPicDao);
        this.registerDao(ChatMessage.class, this.chatMessageDao);
        this.registerDao(Chatter.class, this.chatterDao);
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
