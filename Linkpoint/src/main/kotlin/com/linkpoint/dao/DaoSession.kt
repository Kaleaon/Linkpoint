package com.linkpoint.dao

import android.database.sqlite.SQLiteDatabase
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.AbstractDaoSession
import de.greenrobot.dao.identityscope.IdentityScopeType
import de.greenrobot.dao.internal.DaoConfig
import java.util.Map

class DaoSession : AbstractDaoSession() {
    private val CachedAssetDao cachedAssetDao = CachedAssetDao(this.cachedAssetDaoConfig, this)
    private val DaoConfig cachedAssetDaoConfig
    private val CachedResponseDao cachedResponseDao = CachedResponseDao(this.cachedResponseDaoConfig, this)
    private val DaoConfig cachedResponseDaoConfig
    private val ChatMessageDao chatMessageDao = ChatMessageDao(this.chatMessageDaoConfig, this)
    private val DaoConfig chatMessageDaoConfig
    private val ChatterDao chatterDao = ChatterDao(this.chatterDaoConfig, this)
    private val DaoConfig chatterDaoConfig
    private val FriendDao friendDao = FriendDao(this.friendDaoConfig, this)
    private val DaoConfig friendDaoConfig
    private val GroupMemberDao groupMemberDao = GroupMemberDao(this.groupMemberDaoConfig, this)
    private val DaoConfig groupMemberDaoConfig
    private val GroupMemberListDao groupMemberListDao = GroupMemberListDao(this.groupMemberListDaoConfig, this)
    private val DaoConfig groupMemberListDaoConfig
    private val GroupRoleMemberDao groupRoleMemberDao = GroupRoleMemberDao(this.groupRoleMemberDaoConfig, this)
    private val DaoConfig groupRoleMemberDaoConfig
    private val GroupRoleMemberListDao groupRoleMemberListDao = GroupRoleMemberListDao(this.groupRoleMemberListDaoConfig, this)
    private val DaoConfig groupRoleMemberListDaoConfig
    private val MoneyTransactionDao moneyTransactionDao = MoneyTransactionDao(this.moneyTransactionDaoConfig, this)
    private val DaoConfig moneyTransactionDaoConfig
    private val MuteListCachedDataDao muteListCachedDataDao = MuteListCachedDataDao(this.muteListCachedDataDaoConfig, this)
    private val DaoConfig muteListCachedDataDaoConfig
    private val SearchGridResultDao searchGridResultDao = SearchGridResultDao(this.searchGridResultDaoConfig, this)
    private val DaoConfig searchGridResultDaoConfig
    private val UserDao userDao = UserDao(this.userDaoConfig, this)
    private val DaoConfig userDaoConfig
    private val UserNameDao userNameDao = UserNameDao(this.userNameDaoConfig, this)
    private val DaoConfig userNameDaoConfig
    private val UserPicDao userPicDao = UserPicDao(this.userPicDaoConfig, this)
    private val DaoConfig userPicDaoConfig

    public DaoSession(SQLiteDatabase sQLiteDatabase, IdentityScopeType identityScopeType, Map<Class<? : AbstractDao<?, ?>>, DaoConfig> map) {
        super(sQLiteDatabase)
        this.cachedResponseDaoConfig = ((DaoConfig) map.get(CachedResponseDao.class)).clone()
        this.cachedResponseDaoConfig.initIdentityScope(identityScopeType)
        this.cachedAssetDaoConfig = ((DaoConfig) map.get(CachedAssetDao.class)).clone()
        this.cachedAssetDaoConfig.initIdentityScope(identityScopeType)
        this.moneyTransactionDaoConfig = ((DaoConfig) map.get(MoneyTransactionDao.class)).clone()
        this.moneyTransactionDaoConfig.initIdentityScope(identityScopeType)
        this.muteListCachedDataDaoConfig = ((DaoConfig) map.get(MuteListCachedDataDao.class)).clone()
        this.muteListCachedDataDaoConfig.initIdentityScope(identityScopeType)
        this.searchGridResultDaoConfig = ((DaoConfig) map.get(SearchGridResultDao.class)).clone()
        this.searchGridResultDaoConfig.initIdentityScope(identityScopeType)
        this.groupMemberDaoConfig = ((DaoConfig) map.get(GroupMemberDao.class)).clone()
        this.groupMemberDaoConfig.initIdentityScope(identityScopeType)
        this.groupMemberListDaoConfig = ((DaoConfig) map.get(GroupMemberListDao.class)).clone()
        this.groupMemberListDaoConfig.initIdentityScope(identityScopeType)
        this.groupRoleMemberDaoConfig = ((DaoConfig) map.get(GroupRoleMemberDao.class)).clone()
        this.groupRoleMemberDaoConfig.initIdentityScope(identityScopeType)
        this.groupRoleMemberListDaoConfig = ((DaoConfig) map.get(GroupRoleMemberListDao.class)).clone()
        this.groupRoleMemberListDaoConfig.initIdentityScope(identityScopeType)
        this.userDaoConfig = ((DaoConfig) map.get(UserDao.class)).clone()
        this.userDaoConfig.initIdentityScope(identityScopeType)
        this.friendDaoConfig = ((DaoConfig) map.get(FriendDao.class)).clone()
        this.friendDaoConfig.initIdentityScope(identityScopeType)
        this.userNameDaoConfig = ((DaoConfig) map.get(UserNameDao.class)).clone()
        this.userNameDaoConfig.initIdentityScope(identityScopeType)
        this.userPicDaoConfig = ((DaoConfig) map.get(UserPicDao.class)).clone()
        this.userPicDaoConfig.initIdentityScope(identityScopeType)
        this.chatMessageDaoConfig = ((DaoConfig) map.get(ChatMessageDao.class)).clone()
        this.chatMessageDaoConfig.initIdentityScope(identityScopeType)
        this.chatterDaoConfig = ((DaoConfig) map.get(ChatterDao.class)).clone()
        this.chatterDaoConfig.initIdentityScope(identityScopeType)
        registerDao(CachedResponse.class, this.cachedResponseDao)
        registerDao(CachedAsset.class, this.cachedAssetDao)
        registerDao(MoneyTransaction.class, this.moneyTransactionDao)
        registerDao(MuteListCachedData.class, this.muteListCachedDataDao)
        registerDao(SearchGridResult.class, this.searchGridResultDao)
        registerDao(GroupMember.class, this.groupMemberDao)
        registerDao(GroupMemberList.class, this.groupMemberListDao)
        registerDao(GroupRoleMember.class, this.groupRoleMemberDao)
        registerDao(GroupRoleMemberList.class, this.groupRoleMemberListDao)
        registerDao(User.class, this.userDao)
        registerDao(Friend.class, this.friendDao)
        registerDao(UserName.class, this.userNameDao)
        registerDao(UserPic.class, this.userPicDao)
        registerDao(ChatMessage.class, this.chatMessageDao)
        registerDao(Chatter.class, this.chatterDao)
    }

    fun clear() {
        this.cachedResponseDaoConfig.getIdentityScope().clear()
        this.cachedAssetDaoConfig.getIdentityScope().clear()
        this.moneyTransactionDaoConfig.getIdentityScope().clear()
        this.muteListCachedDataDaoConfig.getIdentityScope().clear()
        this.searchGridResultDaoConfig.getIdentityScope().clear()
        this.groupMemberDaoConfig.getIdentityScope().clear()
        this.groupMemberListDaoConfig.getIdentityScope().clear()
        this.groupRoleMemberDaoConfig.getIdentityScope().clear()
        this.groupRoleMemberListDaoConfig.getIdentityScope().clear()
        this.userDaoConfig.getIdentityScope().clear()
        this.friendDaoConfig.getIdentityScope().clear()
        this.userNameDaoConfig.getIdentityScope().clear()
        this.userPicDaoConfig.getIdentityScope().clear()
        this.chatMessageDaoConfig.getIdentityScope().clear()
        this.chatterDaoConfig.getIdentityScope().clear()
    }

     public fun getCachedAssetDao(): CachedAssetDao {
        return this.cachedAssetDao
    }

     public fun getCachedResponseDao(): CachedResponseDao {
        return this.cachedResponseDao
    }

     public fun getChatMessageDao(): ChatMessageDao {
        return this.chatMessageDao
    }

     public fun getChatterDao(): ChatterDao {
        return this.chatterDao
    }

     public fun getFriendDao(): FriendDao {
        return this.friendDao
    }

     public fun getGroupMemberDao(): GroupMemberDao {
        return this.groupMemberDao
    }

     public fun getGroupMemberListDao(): GroupMemberListDao {
        return this.groupMemberListDao
    }

     public fun getGroupRoleMemberDao(): GroupRoleMemberDao {
        return this.groupRoleMemberDao
    }

     public fun getGroupRoleMemberListDao(): GroupRoleMemberListDao {
        return this.groupRoleMemberListDao
    }

     public fun getMoneyTransactionDao(): MoneyTransactionDao {
        return this.moneyTransactionDao
    }

     public fun getMuteListCachedDataDao(): MuteListCachedDataDao {
        return this.muteListCachedDataDao
    }

     public fun getSearchGridResultDao(): SearchGridResultDao {
        return this.searchGridResultDao
    }

     public fun getUserDao(): UserDao {
        return this.userDao
    }

     public fun getUserNameDao(): UserNameDao {
        return this.userNameDao
    }

     public fun getUserPicDao(): UserPicDao {
        return this.userPicDao
    }
}
