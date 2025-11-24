package com.lumiyaviewer.lumiya.dao

import android.database.sqlite.SQLiteDatabase
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.AbstractDaoSession
import de.greenrobot.dao.identityscope.IdentityScopeType
import de.greenrobot.dao.internal.DaoConfig

class DaoSession(
    db: SQLiteDatabase,
    type: IdentityScopeType,
    daoConfigMap: java.util.Map<Class<out AbstractDao<*, *>>, DaoConfig>
) : AbstractDaoSession(db) {

    private val cachedAssetDaoConfig: DaoConfig = daoConfigMap[CachedAssetDao::class.java]!!.clone()
    private val cachedResponseDaoConfig: DaoConfig = daoConfigMap[CachedResponseDao::class.java]!!.clone()
    private val chatMessageDaoConfig: DaoConfig = daoConfigMap[ChatMessageDao::class.java]!!.clone()
    private val chatterDaoConfig: DaoConfig = daoConfigMap[ChatterDao::class.java]!!.clone()
    private val friendDaoConfig: DaoConfig = daoConfigMap[FriendDao::class.java]!!.clone()
    private val groupMemberDaoConfig: DaoConfig = daoConfigMap[GroupMemberDao::class.java]!!.clone()
    private val groupMemberListDaoConfig: DaoConfig = daoConfigMap[GroupMemberListDao::class.java]!!.clone()
    private val groupRoleMemberDaoConfig: DaoConfig = daoConfigMap[GroupRoleMemberDao::class.java]!!.clone()
    private val groupRoleMemberListDaoConfig: DaoConfig = daoConfigMap[GroupRoleMemberListDao::class.java]!!.clone()
    private val moneyTransactionDaoConfig: DaoConfig = daoConfigMap[MoneyTransactionDao::class.java]!!.clone()
    private val muteListCachedDataDaoConfig: DaoConfig = daoConfigMap[MuteListCachedDataDao::class.java]!!.clone()
    private val searchGridResultDaoConfig: DaoConfig = daoConfigMap[SearchGridResultDao::class.java]!!.clone()
    private val userDaoConfig: DaoConfig = daoConfigMap[UserDao::class.java]!!.clone()
    private val userNameDaoConfig: DaoConfig = daoConfigMap[UserNameDao::class.java]!!.clone()
    private val userPicDaoConfig: DaoConfig = daoConfigMap[UserPicDao::class.java]!!.clone()

    private val cachedAssetDao: CachedAssetDao
    private val cachedResponseDao: CachedResponseDao
    private val chatMessageDao: ChatMessageDao
    private val chatterDao: ChatterDao
    private val friendDao: FriendDao
    private val groupMemberDao: GroupMemberDao
    private val groupMemberListDao: GroupMemberListDao
    private val groupRoleMemberDao: GroupRoleMemberDao
    private val groupRoleMemberListDao: GroupRoleMemberListDao
    private val moneyTransactionDao: MoneyTransactionDao
    private val muteListCachedDataDao: MuteListCachedDataDao
    private val searchGridResultDao: SearchGridResultDao
    private val userDao: UserDao
    private val userNameDao: UserNameDao
    private val userPicDao: UserPicDao

    init {
        cachedAssetDaoConfig.initIdentityScope(type)
        cachedResponseDaoConfig.initIdentityScope(type)
        chatMessageDaoConfig.initIdentityScope(type)
        chatterDaoConfig.initIdentityScope(type)
        friendDaoConfig.initIdentityScope(type)
        groupMemberDaoConfig.initIdentityScope(type)
        groupMemberListDaoConfig.initIdentityScope(type)
        groupRoleMemberDaoConfig.initIdentityScope(type)
        groupRoleMemberListDaoConfig.initIdentityScope(type)
        moneyTransactionDaoConfig.initIdentityScope(type)
        muteListCachedDataDaoConfig.initIdentityScope(type)
        searchGridResultDaoConfig.initIdentityScope(type)
        userDaoConfig.initIdentityScope(type)
        userNameDaoConfig.initIdentityScope(type)
        userPicDaoConfig.initIdentityScope(type)

        cachedAssetDao = CachedAssetDao(cachedAssetDaoConfig, this)
        cachedResponseDao = CachedResponseDao(cachedResponseDaoConfig, this)
        chatMessageDao = ChatMessageDao(chatMessageDaoConfig, this)
        chatterDao = ChatterDao(chatterDaoConfig, this)
        friendDao = FriendDao(friendDaoConfig, this)
        groupMemberDao = GroupMemberDao(groupMemberDaoConfig, this)
        groupMemberListDao = GroupMemberListDao(groupMemberListDaoConfig, this)
        groupRoleMemberDao = GroupRoleMemberDao(groupRoleMemberDaoConfig, this)
        groupRoleMemberListDao = GroupRoleMemberListDao(groupRoleMemberListDaoConfig, this)
        moneyTransactionDao = MoneyTransactionDao(moneyTransactionDaoConfig, this)
        muteListCachedDataDao = MuteListCachedDataDao(muteListCachedDataDaoConfig, this)
        searchGridResultDao = SearchGridResultDao(searchGridResultDaoConfig, this)
        userDao = UserDao(userDaoConfig, this)
        userNameDao = UserNameDao(userNameDaoConfig, this)
        userPicDao = UserPicDao(userPicDaoConfig, this)

        registerDao(CachedAsset::class.java, cachedAssetDao)
        registerDao(CachedResponse::class.java, cachedResponseDao)
        registerDao(ChatMessage::class.java, chatMessageDao)
        registerDao(Chatter::class.java, chatterDao)
        registerDao(Friend::class.java, friendDao)
        registerDao(GroupMember::class.java, groupMemberDao)
        registerDao(GroupMemberList::class.java, groupMemberListDao)
        registerDao(GroupRoleMember::class.java, groupRoleMemberDao)
        registerDao(GroupRoleMemberList::class.java, groupRoleMemberListDao)
        registerDao(MoneyTransaction::class.java, moneyTransactionDao)
        registerDao(MuteListCachedData::class.java, muteListCachedDataDao)
        registerDao(SearchGridResult::class.java, searchGridResultDao)
        registerDao(User::class.java, userDao)
        registerDao(UserName::class.java, userNameDao)
        registerDao(UserPic::class.java, userPicDao)
    }

    fun clear() {
        cachedAssetDaoConfig.identityScope.clear()
        cachedResponseDaoConfig.identityScope.clear()
        chatMessageDaoConfig.identityScope.clear()
        chatterDaoConfig.identityScope.clear()
        friendDaoConfig.identityScope.clear()
        groupMemberDaoConfig.identityScope.clear()
        groupMemberListDaoConfig.identityScope.clear()
        groupRoleMemberDaoConfig.identityScope.clear()
        groupRoleMemberListDaoConfig.identityScope.clear()
        moneyTransactionDaoConfig.identityScope.clear()
        muteListCachedDataDaoConfig.identityScope.clear()
        searchGridResultDaoConfig.identityScope.clear()
        userDaoConfig.identityScope.clear()
        userNameDaoConfig.identityScope.clear()
        userPicDaoConfig.identityScope.clear()
    }

    fun getCachedAssetDao(): CachedAssetDao = cachedAssetDao
    fun getCachedResponseDao(): CachedResponseDao = cachedResponseDao
    fun getChatMessageDao(): ChatMessageDao = chatMessageDao
    fun getChatterDao(): ChatterDao = chatterDao
    fun getFriendDao(): FriendDao = friendDao
    fun getGroupMemberDao(): GroupMemberDao = groupMemberDao
    fun getGroupMemberListDao(): GroupMemberListDao = groupMemberListDao
    fun getGroupRoleMemberDao(): GroupRoleMemberDao = groupRoleMemberDao
    fun getGroupRoleMemberListDao(): GroupRoleMemberListDao = groupRoleMemberListDao
    fun getMoneyTransactionDao(): MoneyTransactionDao = moneyTransactionDao
    fun getMuteListCachedDataDao(): MuteListCachedDataDao = muteListCachedDataDao
    fun getSearchGridResultDao(): SearchGridResultDao = searchGridResultDao
    fun getUserDao(): UserDao = userDao
    fun getUserNameDao(): UserNameDao = userNameDao
    fun getUserPicDao(): UserPicDao = userPicDao
}
