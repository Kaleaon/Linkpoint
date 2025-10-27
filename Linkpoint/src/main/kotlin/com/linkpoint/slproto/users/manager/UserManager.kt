package com.linkpoint.slproto.users.manager

import android.database.Cursor
import android.os.Environment
import android.preference.PreferenceManager
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Table
import com.linkpoint.Debug
import com.linkpoint.LinkpointApp
import com.linkpoint.dao.ChatMessage
import com.linkpoint.dao.ChatMessageDao
import com.linkpoint.dao.Chatter
import com.linkpoint.dao.ChatterDao
import com.linkpoint.dao.DaoManager
import com.linkpoint.dao.DaoSession
import com.linkpoint.dao.User
import com.linkpoint.dao.UserDao
import com.linkpoint.dao.UserName
import com.linkpoint.dao.UserPic
import com.linkpoint.dao.UserPicDao
import com.linkpoint.eventbus.EventBus
import com.linkpoint.react.OpportunisticExecutor
import com.linkpoint.react.RateLimitRequestHandler
import com.linkpoint.react.RequestProcessor
import com.linkpoint.react.RequestQueue
import com.linkpoint.react.Subscribable
import com.linkpoint.react.SubscriptionDataPool
import com.linkpoint.react.SubscriptionPool
import com.linkpoint.react.SubscriptionSingleDataPool
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLGridConnection
import com.linkpoint.slproto.assets.SLWearable
import com.linkpoint.slproto.assets.SLWearableType
import com.linkpoint.slproto.messages.AgentDataUpdate
import com.linkpoint.slproto.messages.AvatarNotesReply
import com.linkpoint.slproto.messages.AvatarPicksReply
import com.linkpoint.slproto.messages.AvatarPropertiesReply
import com.linkpoint.slproto.messages.GroupProfileReply
import com.linkpoint.slproto.messages.GroupRoleDataReply
import com.linkpoint.slproto.messages.GroupTitlesReply
import com.linkpoint.slproto.messages.ParcelInfoReply
import com.linkpoint.slproto.messages.PickInfoReply
import com.linkpoint.slproto.modules.SLAvatarAppearance
import com.linkpoint.slproto.modules.SLMinimap
import com.linkpoint.slproto.modules.groups.AvatarGroupList
import com.linkpoint.slproto.modules.mutelist.MuteListEntry
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.SLMessageResponseCacher
import com.linkpoint.slproto.users.SerializableResponseCacher
import com.linkpoint.slproto.users.events.EventUserInfoChanged
import com.linkpoint.slproto.users.manager.assets.AssetResponseCacher
import com.linkpoint.utils.StringUtils
import com.linkpoint.utils.reqset.WeakPriorityRequestSet
import com.linkpoint.voice.common.messages.VoiceAudioProperties
import com.linkpoint.voice.common.model.VoiceChatInfo
import de.greenrobot.dao.query.Query
import de.greenrobot.dao.query.WhereCondition
import java.io.File
import java.util.Map
import java.util.UUID
import java.util.WeakHashMap
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.Nonnull
import javax.annotation.Nullable

class UserManager {
    private const val SubscriptionDataPool<UUID, SLAgentCircuit> activeAgentCircuitsPool = SubscriptionDataPool().setCanContainNulls(true)
    private const val Object lock = Object()
    private const val Map<UUID, UserManager> userManagers = WeakHashMap()
    private val AtomicReference<SLAgentCircuit> activeAgentCircuit = AtomicReference<>()
    private val Query<Chatter> activeChattersQuery
    private val SLMessageResponseCacher<UUID, AgentDataUpdate> agentDataUpdates
    private val AssetResponseCacher assetResponseCacher
    private val SerializableResponseCacher<UUID, AvatarGroupList> avatarGroupLists
    private val SLMessageResponseCacher<UUID, AvatarNotesReply> avatarNotes
    private val SLMessageResponseCacher<AvatarPickKey, PickInfoReply> avatarPickInfos
    private val SLMessageResponseCacher<UUID, AvatarPicksReply> avatarPicks
    private val SLMessageResponseCacher<UUID, AvatarPropertiesReply> avatarProperties
    private val BalanceManager balanceManager
    private val ChatMessageDao chatMessageDao
    private val ChatterDao chatterDao
    private val ChatterList chatterList
    private val Object chatterUpdateLock = Object()
    private val SubscriptionSingleDataPool<CurrentLocationInfo> currentLocationInfoPool = SubscriptionSingleDataPool<>()
    /* access modifiers changed from: private */
    val DaoSession daoSession
    private val OpportunisticExecutor dbExecutor = OpportunisticExecutor("Database")
    private val EventBus eventBus = EventBus.getInstance()
    private val Query<Chatter> findChatterQuery
    private val Query<UserPic> findUserPicQuery
    private val Query<User> findUserQuery
    private val Query<User> friendsQuery
    private val SLMessageResponseCacher<UUID, GroupProfileReply> groupProfiles
    private val SLMessageResponseCacher<UUID, GroupRoleDataReply> groupRoles
    private val SLMessageResponseCacher<UUID, GroupTitlesReply> groupTitles
    private val InventoryManager inventoryManager
    private val Query<ChatMessage> loadMessageQuery
    private val SubscriptionSingleDataPool<SLMinimap.MinimapBitmap> minimapBitmapPool = SubscriptionSingleDataPool<>()
    private val SubscriptionPool<SubscriptionSingleKey, ImmutableList<MuteListEntry>> muteListPool = SubscriptionPool<>()
    private val UnreadNotificationManager notificationManager
    private val ObjectPopupsManager objectPopupsManager
    private val ObjectsManager objectsManager
    private val SLMessageResponseCacher<UUID, ParcelInfoReply> parcelInfoData
    private val SearchManager searchManager
    private val SyncManager syncManager
    private val UserDao userDao
    private val UUID userID
    private val SubscriptionPool<SubscriptionSingleKey, SLMinimap.UserLocations> userLocationsPool = SubscriptionPool<>()
    private val WeakPriorityRequestSet<UUID> userNameRequests = WeakPriorityRequestSet<>()
    private val RateLimitRequestHandler<UUID, UserName> userNamesHandler = RateLimitRequestHandler<>(RequestProcessor<UUID, UserName, UserName>(this.userNamesPool, this.dbExecutor) {
        /* access modifiers changed from: protected */
         public fun isRequestComplete(uuid: UUID, userName: UserName): Boolean {
            if (userName != null) {
                if (userName.getIsBadUUID()) {
                    return true
                }
                if (!(userName.getDisplayName() == null || userName.getUserName() == null)) {
                    return true
                }
            }
            return false
        }

        /* access modifiers changed from: protected */
         public fun processRequest(uuid: UUID): UserName {
            return (UserName) UserManager.this.daoSession.getUserNameDao().load(uuid)
        }

        /* access modifiers changed from: protected */
         public fun processResult(uuid: UUID, userName: UserName): UserName {
            val userName2: UserName = (UserName) UserManager.this.daoSession.getUserNameDao().load(uuid)
            if (userName2 != null) {
                if (userName2.mergeWith(userName)) {
                    UserManager.this.daoSession.getUserNameDao().update(userName2)
                }
                return userName2
            }
            UserManager.this.daoSession.getUserNameDao().insertOrReplace(userName)
            return userName
        }
    private val SubscriptionPool<UUID, UserName> userNamesPool = SubscriptionPool<>()
    private val UserPicBitmapCache userPicBitmapCache
    private val UserPicDao userPicDao
    private val Object userPicUpdateLock = Object()
    private val Object userUpdateLock = Object()
    private val SubscriptionSingleDataPool<ChatterID> voiceActiveChatterPool = SubscriptionSingleDataPool<>()
    private val SubscriptionSingleDataPool<VoiceAudioProperties> voiceAudioPropertiesPool = SubscriptionSingleDataPool<>()
    private val SubscriptionDataPool<ChatterID, VoiceChatInfo> voiceChatInfoPool = SubscriptionDataPool().setCanContainNulls(true)
    private val SubscriptionSingleDataPool<Boolean> voiceLoggedInPool = SubscriptionSingleDataPool<>()
    private val SubscriptionSingleDataPool<ImmutableMap<UUID, String>> wornAttachmentsPool = SubscriptionSingleDataPool<>()
    private val SubscriptionPool<SubscriptionSingleKey, ImmutableList<SLAvatarAppearance.WornItem>> wornItemsPool = SubscriptionPool<>()
    private val SubscriptionSingleDataPool<UUID> wornOutfitLinkPool = SubscriptionSingleDataPool<>()
    private val SubscriptionSingleDataPool<Table<SLWearableType, UUID, SLWearable>> wornWearablesPool = SubscriptionSingleDataPool<>()

    private UserManager(UUID uuid) throws IllegalArgumentException {
        this.userID = uuid
        val userDaoSession: DaoSession = DaoManager.getUserDaoSession(uuid)
        if (userDaoSession == null) {
            throw IllegalArgumentException("Null DAO session")
        }
        this.daoSession = userDaoSession
        this.userDao = userDaoSession.getUserDao()
        this.userPicDao = userDaoSession.getUserPicDao()
        this.chatMessageDao = userDaoSession.getChatMessageDao()
        this.chatterDao = userDaoSession.getChatterDao()
        this.avatarPicks = SLMessageResponseCacher<>(userDaoSession, this.dbExecutor, "AvatarPicks")
        this.avatarPickInfos = SLMessageResponseCacher<>(userDaoSession, this.dbExecutor, "AvatarPickInfos")
        this.avatarGroupLists = SerializableResponseCacher<>(userDaoSession, this.dbExecutor, "AvatarGroupLists")
        this.groupProfiles = SLMessageResponseCacher<>(userDaoSession, this.dbExecutor, "GroupProfiles")
        this.groupTitles = SLMessageResponseCacher<>(userDaoSession, this.dbExecutor, "GroupTitles")
        this.agentDataUpdates = SLMessageResponseCacher<>(userDaoSession, this.dbExecutor, "AgentDataUpdates")
        this.groupRoles = SLMessageResponseCacher<>(userDaoSession, this.dbExecutor, "GroupRoles")
        this.avatarNotes = SLMessageResponseCacher<>(userDaoSession, this.dbExecutor, "AvatarNotes")
        this.avatarProperties = SLMessageResponseCacher<>(userDaoSession, this.dbExecutor, "AvatarProperties")
        this.parcelInfoData = SLMessageResponseCacher<>(userDaoSession, this.dbExecutor, "ParcelInfoReply")
        this.assetResponseCacher = AssetResponseCacher(userDaoSession, this.dbExecutor)
        this.findUserQuery = this.userDao.queryBuilder().where(UserDao.Properties.Uuid.eq(""), WhereCondition[0]).build()
        this.friendsQuery = this.userDao.queryBuilder().where(UserDao.Properties.IsFriend.eq(Boolean.TRUE), WhereCondition[0]).build()
        this.findUserPicQuery = this.userPicDao.queryBuilder().where(UserPicDao.Properties.Uuid.eq(""), WhereCondition[0]).build()
        this.loadMessageQuery = this.chatMessageDao.queryBuilder().where(ChatMessageDao.Properties.Id.eq(""), WhereCondition[0]).build()
        this.findChatterQuery = this.chatterDao.queryBuilder().where(ChatterDao.Properties.Type.eq((Object) null), ChatterDao.Properties.Uuid.eq("")).build()
        this.activeChattersQuery = this.chatterDao.queryBuilder().where(ChatterDao.Properties.Active.eq(true), WhereCondition[0]).build()
        this.inventoryManager = InventoryManager(uuid)
        this.notificationManager = UnreadNotificationManager(this, userDaoSession)
        this.objectPopupsManager = ObjectPopupsManager(this)
        this.objectsManager = ObjectsManager(this)
        this.balanceManager = BalanceManager(this)
        this.searchManager = SearchManager(this, userDaoSession)
        this.chatterList = ChatterList(this)
        this.userPicBitmapCache = UserPicBitmapCache(this)
        this.syncManager = SyncManager(this)
    }

    @JvmStatic
    Subscribable<UUID, SLAgentCircuit> agentCircuits() {
        return activeAgentCircuitsPool
    }

    @JvmStatic
     fun getActiveAgentCircuit(uuid: UUID): SLAgentCircuit {
        UserManager userManager
        if (uuid == null || (userManager = getUserManager(uuid)) == null) {
            return null
        }
        return userManager.getActiveAgentCircuit()
    }

    @JvmStatic
     fun getConnectedAgentCircuit(uuid: UUID) throws SLGridConnection.NotConnectedException {
        val activeAgentCircuit2: SLAgentCircuit = getActiveAgentCircuit(uuid)
        if (activeAgentCircuit2 != null) {
            return activeAgentCircuit2
        }
        throw SLGridConnection.NotConnectedException()
    }

    @JvmStatic
 private fun getInventoryDatabasePath(str: String): File {
        if (PreferenceManager.getDefaultSharedPreferences(LinkpointApp.getContext()).getString("db_location", "internal").equals("sd")) {
            val file: File = File(Environment.getExternalStorageDirectory(), "/Android/data/com.lumiyaviewer.lumiya/cache/database")
            file.mkdirs()
            return File(file, str)
        }
        val databasePath: File = LinkpointApp.getContext().getDatabasePath(str)
        val parentFile: File = databasePath.getParentFile()
        if (parentFile != null) {
            parentFile.mkdirs()
        }
        return databasePath
    }

    @JvmStatic
     fun getUserManager(uuid: UUID): UserManager {
        UserManager userManager
        if (uuid == null) {
            return null
        }
        synchronized (lock) {
            userManager = userManagers.get(uuid)
            if (userManager == null) {
                try {
                    userManager = UserManager(uuid)
                    userManagers.put(uuid, userManager)
                } catch (IllegalArgumentException e) {
                    Debug.Warning(e)
                    return null
                }
            }
        }
        return userManager
    }

    fun addChatMessage(chatMessage: ChatMessage) {
        this.chatMessageDao.insert(chatMessage)
    }

    fun clearActiveAgentCircuit(sLAgentCircuit: SLAgentCircuit) {
        if (this.activeAgentCircuit.compareAndSet(sLAgentCircuit, (Object) null)) {
            Debug.Printf("Active agent circuit cleared.", Object[0])
            this.objectPopupsManager.clearObjectPopups()
            this.objectsManager.requestObjectListUpdate()
            activeAgentCircuitsPool.setData(this.userID, null)
        }
    }

     public fun getActiveAgentCircuit(): SLAgentCircuit {
        return this.activeAgentCircuit.get()
    }

    public SLMessageResponseCacher<UUID, AgentDataUpdate> getAgentDataUpdates() {
        return this.agentDataUpdates
    }

     public fun getAssetResponseCacher(): AssetResponseCacher {
        return this.assetResponseCacher
    }

    public SerializableResponseCacher<UUID, AvatarGroupList> getAvatarGroupLists() {
        return this.avatarGroupLists
    }

    public SLMessageResponseCacher<UUID, AvatarNotesReply> getAvatarNotes() {
        return this.avatarNotes
    }

    public SLMessageResponseCacher<AvatarPickKey, PickInfoReply> getAvatarPickInfos() {
        return this.avatarPickInfos
    }

    public SLMessageResponseCacher<UUID, AvatarPicksReply> getAvatarPicks() {
        return this.avatarPicks
    }

    public SLMessageResponseCacher<UUID, AvatarPropertiesReply> getAvatarProperties() {
        return this.avatarProperties
    }

     public fun getBalanceManager(): BalanceManager {
        return this.balanceManager
    }

    public SLMessageResponseCacher<UUID, GroupProfileReply> getCachedGroupProfiles() {
        return this.groupProfiles
    }

     public fun getChatMessage(j: Long): ChatMessage {
        val forCurrentThread: Query<ChatMessage> = this.loadMessageQuery.forCurrentThread()
        forCurrentThread.setParameter(0, Long.valueOf(j))
        return forCurrentThread.unique()
    }

     public fun getChatMessageDao(): ChatMessageDao {
        return this.chatMessageDao
    }

     public fun getChatter(cursor: Cursor): Chatter {
        return this.chatterDao.readEntity(cursor, 0)
    }

     public fun getChatterDao(): ChatterDao {
        return this.chatterDao
    }

     public fun getChatterList(): ChatterList {
        return this.chatterList
    }

    public Subscribable<SubscriptionSingleKey, CurrentLocationInfo> getCurrentLocationInfo() {
        return this.currentLocationInfoPool
    }

     public fun getCurrentLocationInfoSnapshot(): CurrentLocationInfo {
        return this.currentLocationInfoPool.getData()
    }

     public fun getDaoSession(): DaoSession {
        return this.daoSession
    }

     public fun getDatabaseExecutor(): Executor {
        return this.dbExecutor
    }

     public fun getDatabaseRunOnceExecutor(): Executor {
        return this.dbExecutor.getRunOnceExecutor()
    }

     public fun getEventBus(): EventBus {
        return this.eventBus
    }

    public SLMessageResponseCacher<UUID, GroupRoleDataReply> getGroupRoles() {
        return this.groupRoles
    }

    public SLMessageResponseCacher<UUID, GroupTitlesReply> getGroupTitles() {
        return this.groupTitles
    }

     public fun getInventoryManager(): InventoryManager {
        return this.inventoryManager
    }

    public SubscriptionSingleDataPool<SLMinimap.MinimapBitmap> getMinimapBitmapPool() {
        return this.minimapBitmapPool
    }

     public fun getObjectPopupsManager(): ObjectPopupsManager {
        return this.objectPopupsManager
    }

     public fun getObjectsManager(): ObjectsManager {
        return this.objectsManager
    }

     public fun getSearchManager(): SearchManager {
        return this.searchManager
    }

     public fun getSyncManager(): SyncManager {
        return this.syncManager
    }

     public fun getUnreadNotificationManager(): UnreadNotificationManager {
        return this.notificationManager
    }

     public fun getUser(cursor: Cursor): User {
        return this.userDao.readEntity(cursor, 0)
    }

     public fun getUser(uuid: UUID, str: String, str2: String): User {
        val forCurrentThread: Query<User> = this.findUserQuery.forCurrentThread()
        forCurrentThread.setParameter(0, uuid.toString())
        val unique: User = forCurrentThread.unique()
        if (unique == null) {
            synchronized (this.userUpdateLock) {
                unique = forCurrentThread.unique()
                if (unique == null) {
                    unique = User((Long) null)
                    unique.setUuid(uuid)
                    if (str != null) {
                        unique.setUserName(str)
                    }
                    if (str2 != null) {
                        unique.setDisplayName(str2)
                    }
                    this.userDao.insert(unique)
                }
            }
        }
        return unique
    }

     public fun getUserDao(): UserDao {
        return this.userDao
    }

     public fun getUserID(): UUID {
        return this.userID
    }

    public SubscriptionPool<SubscriptionSingleKey, SLMinimap.UserLocations> getUserLocationsPool() {
        return this.userLocationsPool
    }

    public RequestQueue<UUID, UserName> getUserNameRequestQueue() {
        return this.userNamesHandler
    }

    public WeakPriorityRequestSet<UUID> getUserNameRequests() {
        return this.userNameRequests
    }

    public Subscribable<UUID, UserName> getUserNames() {
        return this.userNamesPool
    }

     public fun getUserPic(uuid: UUID): ByteArray {
        if (uuid == null) {
            return null
        }
        val forCurrentThread: Query<UserPic> = this.findUserPicQuery.forCurrentThread()
        forCurrentThread.setParameter(0, uuid.toString())
        val unique: UserPic = forCurrentThread.unique()
        if (unique == null) {
            return null
        }
        return unique.getBitmap()
    }

     public fun getUserPicBitmapCache(): UserPicBitmapCache {
        return this.userPicBitmapCache
    }

    public Subscribable<SubscriptionSingleKey, ChatterID> getVoiceActiveChatter() {
        return this.voiceActiveChatterPool
    }

    public Subscribable<SubscriptionSingleKey, VoiceAudioProperties> getVoiceAudioProperties() {
        return this.voiceAudioPropertiesPool
    }

    public Subscribable<ChatterID, VoiceChatInfo> getVoiceChatInfo() {
        return this.voiceChatInfoPool
    }

    public Subscribable<SubscriptionSingleKey, Boolean> getVoiceLoggedIn() {
        return this.voiceLoggedInPool
    }

    public SubscriptionSingleDataPool<ImmutableMap<UUID, String>> getWornAttachmentsPool() {
        return this.wornAttachmentsPool
    }

    public SubscriptionSingleDataPool<Table<SLWearableType, UUID, SLWearable>> getWornWearablesPool() {
        return this.wornWearablesPool
    }

     public fun isChatterActive(chatterID: ChatterID): Boolean {
        if (chatterID.getChatterType() == ChatterID.ChatterType.Local) {
            return true
        }
        synchronized (this.chatterUpdateLock) {
            val forCurrentThread: Query<Chatter> = this.findChatterQuery.forCurrentThread()
            forCurrentThread.setParameter(0, Integer.valueOf(chatterID.getChatterType().ordinal()))
            forCurrentThread.setParameter(1, StringUtils.toString(chatterID.getOptionalChatterUUID()))
            val unique: Chatter = forCurrentThread.unique()
            if (unique == null) {
                return false
            }
            val active: Boolean = unique.getActive()
            return active
        }
    }

     public fun isChatterMuted(chatterID: ChatterID): Boolean {
        if (chatterID.getChatterType() == ChatterID.ChatterType.Local) {
            return false
        }
        synchronized (this.chatterUpdateLock) {
            val forCurrentThread: Query<Chatter> = this.findChatterQuery.forCurrentThread()
            forCurrentThread.setParameter(0, Integer.valueOf(chatterID.getChatterType().ordinal()))
            forCurrentThread.setParameter(1, StringUtils.toString(chatterID.getOptionalChatterUUID()))
            val unique: Chatter = forCurrentThread.unique()
            if (unique == null) {
                return false
            }
            val muted: Boolean = unique.getMuted()
            return muted
        }
    }

    public SubscriptionPool<SubscriptionSingleKey, ImmutableList<MuteListEntry>> muteListPool() {
        return this.muteListPool
    }

    public SLMessageResponseCacher<UUID, ParcelInfoReply> parcelInfoData() {
        return this.parcelInfoData
    }

    fun setActiveAgentCircuit(sLAgentCircuit: SLAgentCircuit) {
        this.activeAgentCircuit.set(sLAgentCircuit)
        if (sLAgentCircuit == null) {
            this.objectPopupsManager.clearObjectPopups()
        }
        this.objectsManager.requestObjectListUpdate()
        activeAgentCircuitsPool.setData(this.userID, sLAgentCircuit)
    }

    fun setChatterMuted(chatterID: ChatterID, z: Boolean) {
        if (chatterID.getChatterType() != ChatterID.ChatterType.Local) {
            synchronized (this.chatterUpdateLock) {
                val forCurrentThread: Query<Chatter> = this.findChatterQuery.forCurrentThread()
                forCurrentThread.setParameter(0, Integer.valueOf(chatterID.getChatterType().ordinal()))
                forCurrentThread.setParameter(1, StringUtils.toString(chatterID.getOptionalChatterUUID()))
                val unique: Chatter = forCurrentThread.unique()
                if (unique != null) {
                    if (unique.getMuted() != z) {
                        unique.setMuted(z)
                        if (z || !(!unique.getActive())) {
                            this.chatterDao.update(unique)
                        } else {
                            this.chatterDao.delete(unique)
                        }
                    }
                } else if (z) {
                    this.chatterDao.insert(Chatter((Long) null, chatterID.getChatterType().ordinal(), chatterID.getOptionalChatterUUID(), false, true, 0, (Long) null, (UUID) null))
                }
            }
        }
    }

    fun setCurrentLocationInfo(currentLocationInfo: CurrentLocationInfo) {
        this.currentLocationInfoPool.setData(this.currentLocationInfoPool.getKey(), currentLocationInfo)
    }

    fun setUserBadUUID(uuid: UUID) {
        updateUserNames(uuid, (String) null, (String) null, true)
    }

    fun setUserPic(uuid: UUID, bArr: ByteArray) {
        if (uuid != null) {
            val forCurrentThread: Query<UserPic> = this.findUserPicQuery.forCurrentThread()
            forCurrentThread.setParameter(0, uuid.toString())
            synchronized (this.userPicUpdateLock) {
                val unique: UserPic = forCurrentThread.unique()
                if (unique == null) {
                    unique = UserPic((Long) null)
                    unique.setUuid(uuid.toString())
                }
                unique.setBitmap(bArr)
                this.userPicDao.insertOrReplace(unique)
            }
        }
    }

    fun setVoiceActiveChatter(chatterID: ChatterID) {
        this.voiceActiveChatterPool.setData(SubscriptionSingleKey.Value, chatterID)
    }

    fun setVoiceAudioProperties(voiceAudioProperties: VoiceAudioProperties) {
        this.voiceAudioPropertiesPool.setData(SubscriptionSingleKey.Value, voiceAudioProperties)
    }

    fun setVoiceChatInfo(chatterID: ChatterID, voiceChatInfo: VoiceChatInfo) {
        this.voiceChatInfoPool.setData(chatterID, voiceChatInfo)
    }

    fun setVoiceLoggedIn(z: Boolean) {
        this.voiceLoggedInPool.setData(SubscriptionSingleKey.Value, Boolean.valueOf(z))
    }

    fun updateUserNames(uuid: UUID, str: String, str2: String) {
        updateUserNames(uuid, str, str2, false)
    }

    fun updateUserNames(uuid: UUID, str: String, str2: String, z: Boolean) {
        val forCurrentThread: Query<User> = this.findUserQuery.forCurrentThread()
        forCurrentThread.setParameter(0, uuid.toString())
        synchronized (this.userUpdateLock) {
            val unique: User = forCurrentThread.unique()
            if (unique != null) {
                if (unique.getUserName() == null && str != null) {
                    unique.setUserName(str)
                }
                if (unique.getDisplayName() == null && str2 != null) {
                    unique.setDisplayName(str2)
                }
                unique.setBadUUID(z)
                this.userDao.update(unique)
            } else {
                val user: User = User((Long) null)
                user.setUuid(uuid)
                if (str != null) {
                    user.setUserName(str)
                }
                if (str2 != null) {
                    user.setDisplayName(str2)
                }
                user.setBadUUID(z)
                this.userDao.insert(user)
            }
        }
        this.eventBus.publish(EventUserInfoChanged(this.userID, uuid, 2))
    }

    public SubscriptionPool<SubscriptionSingleKey, ImmutableList<SLAvatarAppearance.WornItem>> wornItems() {
        return this.wornItemsPool
    }

    public SubscriptionSingleDataPool<UUID> wornOutfitLink() {
        return this.wornOutfitLinkPool
    }
}
