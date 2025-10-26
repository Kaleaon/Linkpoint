package com.lumiyaviewer.lumiya.slproto.users.manager

import android.database.Cursor
import android.os.Environment
import android.preference.PreferenceManager
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Table
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.LumiyaApp
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.dao.ChatMessageDao
import com.lumiyaviewer.lumiya.dao.Chatter
import com.lumiyaviewer.lumiya.dao.ChatterDao
import com.lumiyaviewer.lumiya.dao.DaoManager
import com.lumiyaviewer.lumiya.dao.DaoSession
import com.lumiyaviewer.lumiya.dao.User
import com.lumiyaviewer.lumiya.dao.UserDao
import com.lumiyaviewer.lumiya.dao.UserName
import com.lumiyaviewer.lumiya.dao.UserPic
import com.lumiyaviewer.lumiya.dao.UserPicDao
import com.lumiyaviewer.lumiya.eventbus.EventBus
import com.lumiyaviewer.lumiya.react.OpportunisticExecutor
import com.lumiyaviewer.lumiya.react.RateLimitRequestHandler
import com.lumiyaviewer.lumiya.react.RequestProcessor
import com.lumiyaviewer.lumiya.react.RequestQueue
import com.lumiyaviewer.lumiya.react.Subscribable
import com.lumiyaviewer.lumiya.react.SubscriptionDataPool
import com.lumiyaviewer.lumiya.react.SubscriptionPool
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.SLGridConnection
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType
import com.lumiyaviewer.lumiya.slproto.messages.AgentDataUpdate
import com.lumiyaviewer.lumiya.slproto.messages.AvatarNotesReply
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPicksReply
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply
import com.lumiyaviewer.lumiya.slproto.messages.ParcelInfoReply
import com.lumiyaviewer.lumiya.slproto.messages.PickInfoReply
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
import de.greenrobot.dao.query.WhereCondition
import java.io.File
import java.util.Map
import java.util.UUID
import java.util.WeakHashMap
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicReference
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class UserManager {
    private SubscriptionDataPool<UUID, SLAgentCircuit> activeAgentCircuitsPool = SubscriptionDataPool().setCanContainNulls(true)
    private Any lock = Any()
    private Map<UUID, UserManager> userManagers = WeakHashMap()
    private AtomicReference<SLAgentCircuit> activeAgentCircuit = AtomicReference<>()
    @NonNull
    private Query<Chatter> activeChattersQuery
    private SLMessageResponseCacher<UUID, AgentDataUpdate> agentDataUpdates
    private AssetResponseCacher assetResponseCacher
    private SerializableResponseCacher<UUID, AvatarGroupList> avatarGroupLists
    private SLMessageResponseCacher<UUID, AvatarNotesReply> avatarNotes
    private SLMessageResponseCacher<AvatarPickKey, PickInfoReply> avatarPickInfos
    private SLMessageResponseCacher<UUID, AvatarPicksReply> avatarPicks
    private SLMessageResponseCacher<UUID, AvatarPropertiesReply> avatarProperties
    @NonNull
    private BalanceManager balanceManager
    @NonNull
    private ChatMessageDao chatMessageDao
    @NonNull
    private ChatterDao chatterDao
    @NonNull
    private ChatterList chatterList
    private Any chatterUpdateLock = Any()
    private SubscriptionSingleDataPool<CurrentLocationInfo> currentLocationInfoPool = SubscriptionSingleDataPool<>()
    /* access modifiers changed from: private */
    @NonNull
    DaoSession daoSession
    private OpportunisticExecutor dbExecutor = OpportunisticExecutor("Database")
    private EventBus eventBus = EventBus.getInstance()
    @NonNull
    private Query<Chatter> findChatterQuery
    @NonNull
    private Query<UserPic> findUserPicQuery
    @NonNull
    private Query<User> findUserQuery
    @NonNull
    private Query<User> friendsQuery
    private SLMessageResponseCacher<UUID, GroupProfileReply> groupProfiles
    private SLMessageResponseCacher<UUID, GroupRoleDataReply> groupRoles
    private SLMessageResponseCacher<UUID, GroupTitlesReply> groupTitles
    @NonNull
    private InventoryManager inventoryManager
    @NonNull
    private Query<ChatMessage> loadMessageQuery
    private SubscriptionSingleDataPool<SLMinimap.MinimapBitmap> minimapBitmapPool = SubscriptionSingleDataPool<>()
    private SubscriptionPool<SubscriptionSingleKey, ImmutableList<MuteListEntry>> muteListPool = SubscriptionPool<>()
    @NonNull
    private UnreadNotificationManager notificationManager
    @NonNull
    private ObjectPopupsManager objectPopupsManager
    @NonNull
    private ObjectsManager objectsManager
    private SLMessageResponseCacher<UUID, ParcelInfoReply> parcelInfoData
    @NonNull
    private SearchManager searchManager
    @NonNull
    private SyncManager syncManager
    @NonNull
    private UserDao userDao
    @NonNull
    private UUID userID
    private SubscriptionPool<SubscriptionSingleKey, SLMinimap.UserLocations> userLocationsPool = SubscriptionPool<>()
    private WeakPriorityRequestSet<UUID> userNameRequests = WeakPriorityRequestSet<>()
    private RateLimitRequestHandler<UUID, UserName> userNamesHandler = RateLimitRequestHandler<>(RequestProcessor<UUID, UserName, UserName>(this.userNamesPool, this.dbExecutor) {
        /* access modifiers changed from: protected */
        Boolean isRequestComplete(@NonNull UUID uuid, UserName userName) {
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
        @Nullable
        UserName processRequest(@NonNull UUID uuid) {
            return (UserName) UserManager.this.daoSession.getUserNameDao().load(uuid)
        }

        /* access modifiers changed from: protected */
        UserName processResult(@NonNull UUID uuid, UserName userName) {
            UserName userName2 = (UserName) UserManager.this.daoSession.getUserNameDao().load(uuid)
            if (userName2 != null) {
                if (userName2.mergeWith(userName)) {
                    UserManager.this.daoSession.getUserNameDao().update(userName2)
                }
                return userName2
            }
            UserManager.this.daoSession.getUserNameDao().insertOrReplace(userName)
            return userName
        }
    private SubscriptionPool<UUID, UserName> userNamesPool = SubscriptionPool<>()
    private UserPicBitmapCache userPicBitmapCache
    @NonNull
    private UserPicDao userPicDao
    private Any userPicUpdateLock = Any()
    private Any userUpdateLock = Any()
    private SubscriptionSingleDataPool<ChatterID> voiceActiveChatterPool = SubscriptionSingleDataPool<>()
    private SubscriptionSingleDataPool<VoiceAudioProperties> voiceAudioPropertiesPool = SubscriptionSingleDataPool<>()
    private SubscriptionDataPool<ChatterID, VoiceChatInfo> voiceChatInfoPool = SubscriptionDataPool().setCanContainNulls(true)
    private SubscriptionSingleDataPool<Boolean> voiceLoggedInPool = SubscriptionSingleDataPool<>()
    private SubscriptionSingleDataPool<ImmutableMap<UUID, String>> wornAttachmentsPool = SubscriptionSingleDataPool<>()
    private SubscriptionPool<SubscriptionSingleKey, ImmutableList<SLAvatarAppearance.WornItem>> wornItemsPool = SubscriptionPool<>()
    private SubscriptionSingleDataPool<UUID> wornOutfitLinkPool = SubscriptionSingleDataPool<>()
    private SubscriptionSingleDataPool<Table<SLWearableType, UUID, SLWearable>> wornWearablesPool = SubscriptionSingleDataPool<>()

    private UserManager(@NonNull UUID uuid) throws IllegalArgumentException {
        this.userID = uuid
        DaoSession userDaoSession = DaoManager.getUserDaoSession(uuid)
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
        this.findChatterQuery = this.chatterDao.queryBuilder().where(ChatterDao.Properties.Type.eq((Any) null), ChatterDao.Properties.Uuid.eq("")).build()
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

    @NonNull
    Subscribable<UUID, SLAgentCircuit> agentCircuits() {
        return activeAgentCircuitsPool
    }

    @Nullable
    SLAgentCircuit getActiveAgentCircuit(@Nullable UUID uuid) {
        UserManager userManager
        if (uuid == null || (userManager = getUserManager(uuid)) == null) {
            return null
        }
        return userManager.getActiveAgentCircuit()
    }

    @NonNull
    SLAgentCircuit getConnectedAgentCircuit(@Nullable UUID uuid) throws SLGridConnection.NotConnectedException {
        SLAgentCircuit activeAgentCircuit2 = getActiveAgentCircuit(uuid)
        if (activeAgentCircuit2 != null) {
            return activeAgentCircuit2
        }
        throw SLGridConnection.NotConnectedException()
    }

    private File getInventoryDatabasePath(String str) {
        if (PreferenceManager.getDefaultSharedPreferences(LumiyaApp.getContext()).getString("db_location", "internal").equals("sd")) {
            File file = File(Environment.getExternalStorageDirectory(), "/Android/data/com.lumiyaviewer.lumiya/cache/database")
            file.mkdirs()
            return File(file, str)
        }
        File databasePath = LumiyaApp.getContext().getDatabasePath(str)
        File parentFile = databasePath.getParentFile()
        if (parentFile != null) {
            parentFile.mkdirs()
        }
        return databasePath
    }

    @Nullable
    UserManager getUserManager(@Nullable UUID uuid) {
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

    Unit addChatMessage(@NonNull ChatMessage chatMessage) {
        this.chatMessageDao.insert(chatMessage)
    }

    Unit clearActiveAgentCircuit(@Nullable SLAgentCircuit sLAgentCircuit) {
        if (this.activeAgentCircuit.compareAndSet(sLAgentCircuit, (Any) null)) {
            Debug.Printf("Active agent circuit cleared.", Any[0])
            this.objectPopupsManager.clearObjectPopups()
            this.objectsManager.requestObjectListUpdate()
            activeAgentCircuitsPool.setData(this.userID, null)
        }
    }

    @Nullable
    SLAgentCircuit getActiveAgentCircuit() {
        return this.activeAgentCircuit.get()
    }

    SLMessageResponseCacher<UUID, AgentDataUpdate> getAgentDataUpdates() {
        return this.agentDataUpdates
    }

    AssetResponseCacher getAssetResponseCacher() {
        return this.assetResponseCacher
    }

    SerializableResponseCacher<UUID, AvatarGroupList> getAvatarGroupLists() {
        return this.avatarGroupLists
    }

    SLMessageResponseCacher<UUID, AvatarNotesReply> getAvatarNotes() {
        return this.avatarNotes
    }

    SLMessageResponseCacher<AvatarPickKey, PickInfoReply> getAvatarPickInfos() {
        return this.avatarPickInfos
    }

    SLMessageResponseCacher<UUID, AvatarPicksReply> getAvatarPicks() {
        return this.avatarPicks
    }

    SLMessageResponseCacher<UUID, AvatarPropertiesReply> getAvatarProperties() {
        return this.avatarProperties
    }

    @NonNull
    BalanceManager getBalanceManager() {
        return this.balanceManager
    }

    SLMessageResponseCacher<UUID, GroupProfileReply> getCachedGroupProfiles() {
        return this.groupProfiles
    }

    @Nullable
    ChatMessage getChatMessage(Long j) {
        Query<ChatMessage> forCurrentThread = this.loadMessageQuery.forCurrentThread()
        forCurrentThread.setParameter(0, Long.valueOf(j))
        return forCurrentThread.unique()
    }

    @NonNull
    ChatMessageDao getChatMessageDao() {
        return this.chatMessageDao
    }

    Chatter getChatter(Cursor cursor) {
        return this.chatterDao.readEntity(cursor, 0)
    }

    @NonNull
    ChatterDao getChatterDao() {
        return this.chatterDao
    }

    @NonNull
    ChatterList getChatterList() {
        return this.chatterList
    }

    Subscribable<SubscriptionSingleKey, CurrentLocationInfo> getCurrentLocationInfo() {
        return this.currentLocationInfoPool
    }

    @Nullable
    CurrentLocationInfo getCurrentLocationInfoSnapshot() {
        return this.currentLocationInfoPool.getData()
    }

    @NonNull
    DaoSession getDaoSession() {
        return this.daoSession
    }

    @NonNull
    Executor getDatabaseExecutor() {
        return this.dbExecutor
    }

    @NonNull
    Executor getDatabaseRunOnceExecutor() {
        return this.dbExecutor.getRunOnceExecutor()
    }

    EventBus getEventBus() {
        return this.eventBus
    }

    SLMessageResponseCacher<UUID, GroupRoleDataReply> getGroupRoles() {
        return this.groupRoles
    }

    SLMessageResponseCacher<UUID, GroupTitlesReply> getGroupTitles() {
        return this.groupTitles
    }

    @NonNull
    InventoryManager getInventoryManager() {
        return this.inventoryManager
    }

    SubscriptionSingleDataPool<SLMinimap.MinimapBitmap> getMinimapBitmapPool() {
        return this.minimapBitmapPool
    }

    @NonNull
    ObjectPopupsManager getObjectPopupsManager() {
        return this.objectPopupsManager
    }

    @NonNull
    ObjectsManager getObjectsManager() {
        return this.objectsManager
    }

    @NonNull
    SearchManager getSearchManager() {
        return this.searchManager
    }

    @NonNull
    SyncManager getSyncManager() {
        return this.syncManager
    }

    @NonNull
    UnreadNotificationManager getUnreadNotificationManager() {
        return this.notificationManager
    }

    User getUser(Cursor cursor) {
        return this.userDao.readEntity(cursor, 0)
    }

    @NonNull
    User getUser(@NonNull UUID uuid, @Nullable String str, @Nullable String str2) {
        Query<User> forCurrentThread = this.findUserQuery.forCurrentThread()
        forCurrentThread.setParameter(0, uuid.toString())
        User unique = forCurrentThread.unique()
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

    @NonNull
    UserDao getUserDao() {
        return this.userDao
    }

    @NonNull
    UUID getUserID() {
        return this.userID
    }

    SubscriptionPool<SubscriptionSingleKey, SLMinimap.UserLocations> getUserLocationsPool() {
        return this.userLocationsPool
    }

    RequestQueue<UUID, UserName> getUserNameRequestQueue() {
        return this.userNamesHandler
    }

    WeakPriorityRequestSet<UUID> getUserNameRequests() {
        return this.userNameRequests
    }

    Subscribable<UUID, UserName> getUserNames() {
        return this.userNamesPool
    }

    Byte[] getUserPic(UUID uuid) {
        if (uuid == null) {
            return null
        }
        Query<UserPic> forCurrentThread = this.findUserPicQuery.forCurrentThread()
        forCurrentThread.setParameter(0, uuid.toString())
        UserPic unique = forCurrentThread.unique()
        if (unique == null) {
            return null
        }
        return unique.getBitmap()
    }

    UserPicBitmapCache getUserPicBitmapCache() {
        return this.userPicBitmapCache
    }

    Subscribable<SubscriptionSingleKey, ChatterID> getVoiceActiveChatter() {
        return this.voiceActiveChatterPool
    }

    Subscribable<SubscriptionSingleKey, VoiceAudioProperties> getVoiceAudioProperties() {
        return this.voiceAudioPropertiesPool
    }

    Subscribable<ChatterID, VoiceChatInfo> getVoiceChatInfo() {
        return this.voiceChatInfoPool
    }

    Subscribable<SubscriptionSingleKey, Boolean> getVoiceLoggedIn() {
        return this.voiceLoggedInPool
    }

    SubscriptionSingleDataPool<ImmutableMap<UUID, String>> getWornAttachmentsPool() {
        return this.wornAttachmentsPool
    }

    SubscriptionSingleDataPool<Table<SLWearableType, UUID, SLWearable>> getWornWearablesPool() {
        return this.wornWearablesPool
    }

    Boolean isChatterActive(ChatterID chatterID) {
        if (chatterID.getChatterType() == ChatterID.ChatterType.Local) {
            return true
        }
        synchronized (this.chatterUpdateLock) {
            Query<Chatter> forCurrentThread = this.findChatterQuery.forCurrentThread()
            forCurrentThread.setParameter(0, Int.valueOf(chatterID.getChatterType().ordinal()))
            forCurrentThread.setParameter(1, StringUtils.toString(chatterID.getOptionalChatterUUID()))
            Chatter unique = forCurrentThread.unique()
            if (unique == null) {
                return false
            }
            Boolean active = unique.getActive()
            return active
        }
    }

    Boolean isChatterMuted(ChatterID chatterID) {
        if (chatterID.getChatterType() == ChatterID.ChatterType.Local) {
            return false
        }
        synchronized (this.chatterUpdateLock) {
            Query<Chatter> forCurrentThread = this.findChatterQuery.forCurrentThread()
            forCurrentThread.setParameter(0, Int.valueOf(chatterID.getChatterType().ordinal()))
            forCurrentThread.setParameter(1, StringUtils.toString(chatterID.getOptionalChatterUUID()))
            Chatter unique = forCurrentThread.unique()
            if (unique == null) {
                return false
            }
            Boolean muted = unique.getMuted()
            return muted
        }
    }

    SubscriptionPool<SubscriptionSingleKey, ImmutableList<MuteListEntry>> muteListPool() {
        return this.muteListPool
    }

    SLMessageResponseCacher<UUID, ParcelInfoReply> parcelInfoData() {
        return this.parcelInfoData
    }

    Unit setActiveAgentCircuit(@Nullable SLAgentCircuit sLAgentCircuit) {
        this.activeAgentCircuit.set(sLAgentCircuit)
        if (sLAgentCircuit == null) {
            this.objectPopupsManager.clearObjectPopups()
        }
        this.objectsManager.requestObjectListUpdate()
        activeAgentCircuitsPool.setData(this.userID, sLAgentCircuit)
    }

    Unit setChatterMuted(ChatterID chatterID, Boolean z) {
        if (chatterID.getChatterType() != ChatterID.ChatterType.Local) {
            synchronized (this.chatterUpdateLock) {
                Query<Chatter> forCurrentThread = this.findChatterQuery.forCurrentThread()
                forCurrentThread.setParameter(0, Int.valueOf(chatterID.getChatterType().ordinal()))
                forCurrentThread.setParameter(1, StringUtils.toString(chatterID.getOptionalChatterUUID()))
                Chatter unique = forCurrentThread.unique()
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

    Unit setCurrentLocationInfo(CurrentLocationInfo currentLocationInfo) {
        this.currentLocationInfoPool.setData(this.currentLocationInfoPool.getKey(), currentLocationInfo)
    }

    Unit setUserBadUUID(UUID uuid) {
        updateUserNames(uuid, (String) null, (String) null, true)
    }

    Unit setUserPic(UUID uuid, Byte[] bArr) {
        if (uuid != null) {
            Query<UserPic> forCurrentThread = this.findUserPicQuery.forCurrentThread()
            forCurrentThread.setParameter(0, uuid.toString())
            synchronized (this.userPicUpdateLock) {
                UserPic unique = forCurrentThread.unique()
                if (unique == null) {
                    unique = UserPic((Long) null)
                    unique.setUuid(uuid.toString())
                }
                unique.setBitmap(bArr)
                this.userPicDao.insertOrReplace(unique)
            }
        }
    }

    Unit setVoiceActiveChatter(@Nullable ChatterID chatterID) {
        this.voiceActiveChatterPool.setData(SubscriptionSingleKey.Value, chatterID)
    }

    Unit setVoiceAudioProperties(VoiceAudioProperties voiceAudioProperties) {
        this.voiceAudioPropertiesPool.setData(SubscriptionSingleKey.Value, voiceAudioProperties)
    }

    Unit setVoiceChatInfo(@NonNull ChatterID chatterID, @Nullable VoiceChatInfo voiceChatInfo) {
        this.voiceChatInfoPool.setData(chatterID, voiceChatInfo)
    }

    Unit setVoiceLoggedIn(Boolean z) {
        this.voiceLoggedInPool.setData(SubscriptionSingleKey.Value, Boolean.valueOf(z))
    }

    Unit updateUserNames(@NonNull UUID uuid, @Nullable String str, @Nullable String str2) {
        updateUserNames(uuid, str, str2, false)
    }

    Unit updateUserNames(@NonNull UUID uuid, @Nullable String str, @Nullable String str2, Boolean z) {
        Query<User> forCurrentThread = this.findUserQuery.forCurrentThread()
        forCurrentThread.setParameter(0, uuid.toString())
        synchronized (this.userUpdateLock) {
            User unique = forCurrentThread.unique()
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
                User user = User((Long) null)
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

    SubscriptionPool<SubscriptionSingleKey, ImmutableList<SLAvatarAppearance.WornItem>> wornItems() {
        return this.wornItemsPool
    }

    SubscriptionSingleDataPool<UUID> wornOutfitLink() {
        return this.wornOutfitLinkPool
    }
}
