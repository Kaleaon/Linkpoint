package com.lumiyaviewer.lumiya.slproto.users.manager

import android.database.Cursor
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Table
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.LumiyaApp
import com.lumiyaviewer.lumiya.dao.*
import com.lumiyaviewer.lumiya.eventbus.EventBus
import com.lumiyaviewer.lumiya.react.*
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.SLGridConnection
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType
import com.lumiyaviewer.lumiya.slproto.messages.*
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance
import com.lumiyaviewer.lumiya.slproto.modules.SLMinimap
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListEntry
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher
import com.lumiyaviewer.lumiya.slproto.users.SerializableResponseCacher
import com.lumiyaviewer.lumiya.slproto.users.events.EventUserInfoChanged
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetResponseCacher
import com.lumiyaviewer.lumiya.utils.StringUtils
import com.lumiyaviewer.lumiya.utils.reqset.WeakPriorityRequestSet
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceAudioProperties
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo
import de.greenrobot.dao.query.Query
import java.io.File
import java.util.UUID
import java.util.WeakHashMap
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicReference

class UserManager private constructor(val userID: UUID) {
    
    private val activeAgentCircuitsPool = SubscriptionDataPool<UUID, SLAgentCircuit>().setCanContainNulls(true)
    private val activeAgentCircuit = AtomicReference<SLAgentCircuit>()
    
    private val activeChattersQuery: Query<Chatter>
    private val agentDataUpdates: SLMessageResponseCacher<UUID, AgentDataUpdate>
    private val assetResponseCacher: AssetResponseCacher
    private val avatarGroupLists: SerializableResponseCacher<UUID, AvatarGroupList>
    private val avatarNotes: SLMessageResponseCacher<UUID, AvatarNotesReply>
    private val avatarPickInfos: SLMessageResponseCacher<AvatarPickKey, PickInfoReply>
    private val avatarPicks: SLMessageResponseCacher<UUID, AvatarPicksReply>
    private val avatarProperties: SLMessageResponseCacher<UUID, AvatarPropertiesReply>
    
    private val balanceManager: BalanceManager
    private val chatMessageDao: ChatMessageDao
    private val chatterDao: ChatterDao
    private val chatterList: ChatterList
    private val chatterUpdateLock = Any()
    private val currentLocationInfoPool = SubscriptionSingleDataPool<CurrentLocationInfo>()
    
    private val daoSession: DaoSession
    private val dbExecutor = OpportunisticExecutor("Database")
    private val eventBus = EventBus.getInstance()
    
    private val findChatterQuery: Query<Chatter>
    private val findUserPicQuery: Query<UserPic>
    private val findUserQuery: Query<User>
    private val friendsQuery: Query<User>
    
    private val groupProfiles: SLMessageResponseCacher<UUID, GroupProfileReply>
    private val groupRoles: SLMessageResponseCacher<UUID, GroupRoleDataReply>
    private val groupTitles: SLMessageResponseCacher<UUID, GroupTitlesReply>
    
    private val inventoryManager: InventoryManager
    private val loadMessageQuery: Query<ChatMessage>
    private val minimapBitmapPool = SubscriptionSingleDataPool<SLMinimap.MinimapBitmap>()
    private val muteListPool = SubscriptionPool<SubscriptionSingleKey, ImmutableList<MuteListEntry>>()
    
    private val notificationManager: UnreadNotificationManager
    private val objectPopupsManager: ObjectPopupsManager
    private val objectsManager: ObjectsManager
    
    private val parcelInfoData: SLMessageResponseCacher<UUID, ParcelInfoReply>
    
    private val searchManager: SearchManager
    private val syncManager: SyncManager
    private val userDao: UserDao
    
    private val userLocationsPool = SubscriptionPool<SubscriptionSingleKey, SLMinimap.UserLocations>()
    private val userNameRequests = WeakPriorityRequestSet<UUID>()
    private val userNamesPool = SubscriptionPool<UUID, UserName>()
    
    private val userNamesHandler: RateLimitRequestHandler<UUID, UserName>
    
    private val userPicBitmapCache: UserPicBitmapCache
    private val userPicDao: UserPicDao
    private val userPicUpdateLock = Any()
    private val userUpdateLock = Any()
    
    private val voiceActiveChatterPool = SubscriptionSingleDataPool<ChatterID>()
    private val voiceAudioPropertiesPool = SubscriptionSingleDataPool<VoiceAudioProperties>()
    private val voiceChatInfoPool = SubscriptionDataPool<ChatterID, VoiceChatInfo>().setCanContainNulls(true)
    private val voiceLoggedInPool = SubscriptionSingleDataPool<Boolean>()
    
    private val wornAttachmentsPool = SubscriptionSingleDataPool<ImmutableMap<UUID, String>>()
    private val wornItemsPool = SubscriptionPool<SubscriptionSingleKey, ImmutableList<SLAvatarAppearance.WornItem>>()
    private val wornOutfitLinkPool = SubscriptionSingleDataPool<UUID>()
    private val wornWearablesPool = SubscriptionSingleDataPool<Table<SLWearableType, UUID, SLWearable>>()

    companion object {
        private val userManagers = WeakHashMap<UUID, UserManager>()
        private val lock = Any()
        
        @JvmStatic
        fun getUserManager(uuid: UUID?): UserManager? {
            if (uuid == null) return null
            synchronized(lock) {
                var manager = userManagers[uuid]
                if (manager == null) {
                    try {
                        manager = UserManager(uuid)
                        userManagers[uuid] = manager
                    } catch (e: IllegalArgumentException) {
                        Debug.Warning(e)
                        return null
                    }
                }
                return manager
            }
        }
    }

    init {
        val userDaoSession = DaoManager.getUserDaoSession(userID) ?: throw IllegalArgumentException("Null DAO session")
        daoSession = userDaoSession
        userDao = userDaoSession.getUserDao()
        userPicDao = userDaoSession.getUserPicDao()
        chatMessageDao = userDaoSession.getChatMessageDao()
        chatterDao = userDaoSession.getChatterDao()
        
        avatarPicks = SLMessageResponseCacher(userDaoSession, dbExecutor, "AvatarPicks")
        avatarPickInfos = SLMessageResponseCacher(userDaoSession, dbExecutor, "AvatarPickInfos")
        avatarGroupLists = SerializableResponseCacher(userDaoSession, dbExecutor, "AvatarGroupLists")
        groupProfiles = SLMessageResponseCacher(userDaoSession, dbExecutor, "GroupProfiles")
        groupTitles = SLMessageResponseCacher(userDaoSession, dbExecutor, "GroupTitles")
        agentDataUpdates = SLMessageResponseCacher(userDaoSession, dbExecutor, "AgentDataUpdates")
        groupRoles = SLMessageResponseCacher(userDaoSession, dbExecutor, "GroupRoles")
        avatarNotes = SLMessageResponseCacher(userDaoSession, dbExecutor, "AvatarNotes")
        avatarProperties = SLMessageResponseCacher(userDaoSession, dbExecutor, "AvatarProperties")
        parcelInfoData = SLMessageResponseCacher(userDaoSession, dbExecutor, "ParcelInfoReply")
        assetResponseCacher = AssetResponseCacher(userDaoSession, dbExecutor)
        
        findUserQuery = userDao.queryBuilder().where(UserDao.Properties.Uuid.eq("")).build()
        friendsQuery = userDao.queryBuilder().where(UserDao.Properties.IsFriend.eq(true)).build()
        findUserPicQuery = userPicDao.queryBuilder().where(UserPicDao.Properties.Uuid.eq("")).build()
        loadMessageQuery = chatMessageDao.queryBuilder().where(ChatMessageDao.Properties.Id.eq("")).build()
        findChatterQuery = chatterDao.queryBuilder().where(ChatterDao.Properties.Type.eq(null), ChatterDao.Properties.Uuid.eq("")).build()
        activeChattersQuery = chatterDao.queryBuilder().where(ChatterDao.Properties.Active.eq(true)).build()
        
        inventoryManager = InventoryManager(userID)
        notificationManager = UnreadNotificationManager(this, userDaoSession)
        objectPopupsManager = ObjectPopupsManager(this)
        objectsManager = ObjectsManager(this)
        balanceManager = BalanceManager(this)
        searchManager = SearchManager(this, userDaoSession)
        chatterList = ChatterList(this)
        userPicBitmapCache = UserPicBitmapCache(this)
        syncManager = SyncManager(this)
        
        val requestProcessor = object : RequestProcessor<UUID, UserName, UserName>(userNamesPool, dbExecutor) {
            override fun isRequestComplete(uuid: UUID, userName: UserName?): Boolean {
                if (userName != null) {
                    if (userName.getIsBadUUID()) return true
                    if (userName.getDisplayName() != null && userName.getUserName() != null) return true
                }
                return false
            }

            override fun processRequest(uuid: UUID): UserName? {
                return daoSession.getUserNameDao().load(uuid)
            }

            override fun processResult(uuid: UUID, userName: UserName): UserName {
                val existing = daoSession.getUserNameDao().load(uuid)
                if (existing != null) {
                    if (existing.mergeWith(userName)) {
                        daoSession.getUserNameDao().update(existing)
                    }
                    return existing
                }
                daoSession.getUserNameDao().insertOrReplace(userName)
                return userName
            }
        }
        
        userNamesHandler = RateLimitRequestHandler(requestProcessor)
    }

    fun agentCircuits(): Subscribable<UUID, SLAgentCircuit> = activeAgentCircuitsPool

    fun getActiveAgentCircuit(): SLAgentCircuit? = activeAgentCircuit.get()

    fun getCurrentLocationInfoSnapshot(): CurrentLocationInfo? {
        return currentLocationInfoPool.getData()
    }
    
    fun getUserID(): UUID = userID
    
    fun getInventoryManager(): InventoryManager = inventoryManager
    
    // Stub implementations for other getters if needed
}
