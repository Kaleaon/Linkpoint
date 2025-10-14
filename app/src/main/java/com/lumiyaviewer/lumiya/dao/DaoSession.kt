package com.lumiyaviewer.lumiya.dao

import android.database.sqlite.SQLiteDatabase
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.AbstractDaoSession
import de.greenrobot.dao.identityscope.IdentityScopeType
import de.greenrobot.dao.internal.DaoConfig
import java.util.Map

class DaoSession : AbstractDaoSession {
    private val cachedAssetDao: CachedAssetDao = CachedAssetDao(this.cachedAssetDaoConfig, this)
    private DaoConfig cachedAssetDaoConfig
    private val cachedResponseDao: CachedResponseDao = CachedResponseDao(this.cachedResponseDaoConfig, this)
    private DaoConfig cachedResponseDaoConfig
    private val chatMessageDao: ChatMessageDao = ChatMessageDao(this.chatMessageDaoConfig, this)
    private DaoConfig chatMessageDaoConfig
    private val chatterDao: ChatterDao = ChatterDao(this.chatterDaoConfig, this)
    private DaoConfig chatterDaoConfig
    private val friendDao: FriendDao = FriendDao(this.friendDaoConfig, this)
    private DaoConfig friendDaoConfig
    private val groupMemberDao: GroupMemberDao = GroupMemberDao(this.groupMemberDaoConfig, this)
    private DaoConfig groupMemberDaoConfig
    private val groupMemberListDao: GroupMemberListDao = GroupMemberListDao(this.groupMemberListDaoConfig, this)
    private DaoConfig groupMemberListDaoConfig
    private val groupRoleMemberDao: GroupRoleMemberDao = GroupRoleMemberDao(this.groupRoleMemberDaoConfig, this)
    private DaoConfig groupRoleMemberDaoConfig
    private val groupRoleMemberListDao: GroupRoleMemberListDao = GroupRoleMemberListDao(this.groupRoleMemberListDaoConfig, this)
    private DaoConfig groupRoleMemberListDaoConfig
    private val moneyTransactionDao: MoneyTransactionDao = MoneyTransactionDao(this.moneyTransactionDaoConfig, this)
    private DaoConfig moneyTransactionDaoConfig
    private val muteListCachedDataDao: MuteListCachedDataDao = MuteListCachedDataDao(this.muteListCachedDataDaoConfig, this)
    private DaoConfig muteListCachedDataDaoConfig
    private val searchGridResultDao: SearchGridResultDao = SearchGridResultDao(this.searchGridResultDaoConfig, this)
    private DaoConfig searchGridResultDaoConfig
    private val userDao: UserDao = UserDao(this.userDaoConfig, this)
    private DaoConfig userDaoConfig
    private val userNameDao: UserNameDao = UserNameDao(this.userNameDaoConfig, this)
    private DaoConfig userNameDaoConfig
    private val userPicDao: UserPicDao = UserPicDao(this.userPicDaoConfig, this)
    private DaoConfig userPicDaoConfig

    constructor(sQLiteDatabase: SQLiteDatabase, identityScopeType: IdentityScopeType, :: Map<Class<?, map: DaoConfig>) {
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

    fun clear(): Unit {
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

    fun getCachedAssetDao(): CachedAssetDao {
        return this.cachedAssetDao
    }

    fun getCachedResponseDao(): CachedResponseDao {
        return this.cachedResponseDao
    }

    fun getChatMessageDao(): ChatMessageDao {
        return this.chatMessageDao
    }

    fun getChatterDao(): ChatterDao {
        return this.chatterDao
    }

    fun getFriendDao(): FriendDao {
        return this.friendDao
    }

    fun getGroupMemberDao(): GroupMemberDao {
        return this.groupMemberDao
    }

    fun getGroupMemberListDao(): GroupMemberListDao {
        return this.groupMemberListDao
    }

    fun getGroupRoleMemberDao(): GroupRoleMemberDao {
        return this.groupRoleMemberDao
    }

    fun getGroupRoleMemberListDao(): GroupRoleMemberListDao {
        return this.groupRoleMemberListDao
    }

    fun getMoneyTransactionDao(): MoneyTransactionDao {
        return this.moneyTransactionDao
    }

    fun getMuteListCachedDataDao(): MuteListCachedDataDao {
        return this.muteListCachedDataDao
    }

    fun getSearchGridResultDao(): SearchGridResultDao {
        return this.searchGridResultDao
    }

    fun getUserDao(): UserDao {
        return this.userDao
    }

    fun getUserNameDao(): UserNameDao {
        return this.userNameDao
    }

    fun getUserPicDao(): UserPicDao {
        return this.userPicDao
    }
}
