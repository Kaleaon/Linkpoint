package com.lumiyaviewer.lumiya.slproto.users.manager;

import android.database.Cursor;
import android.os.Environment;
import android.preference.PreferenceManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.dao.ChatMessageDao;
import com.lumiyaviewer.lumiya.dao.Chatter;
import com.lumiyaviewer.lumiya.dao.ChatterDao;
import com.lumiyaviewer.lumiya.dao.DaoManager;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.dao.User;
import com.lumiyaviewer.lumiya.dao.UserDao;
import com.lumiyaviewer.lumiya.dao.UserName;
import com.lumiyaviewer.lumiya.dao.UserPic;
import com.lumiyaviewer.lumiya.dao.UserPicDao;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.react.OpportunisticExecutor;
import com.lumiyaviewer.lumiya.react.RateLimitRequestHandler;
import com.lumiyaviewer.lumiya.react.RequestProcessor;
import com.lumiyaviewer.lumiya.react.RequestQueue;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.SubscriptionDataPool;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.lumiyaviewer.lumiya.slproto.messages.AgentDataUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarNotesReply;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPicksReply;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
import com.lumiyaviewer.lumiya.slproto.messages.ParcelInfoReply;
import com.lumiyaviewer.lumiya.slproto.messages.PickInfoReply;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.modules.SLMinimap;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListEntry;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.SerializableResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.events.EventUserInfoChanged;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetResponseCacher;
import com.lumiyaviewer.lumiya.utils.StringUtils;
import com.lumiyaviewer.lumiya.utils.reqset.WeakPriorityRequestSet;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceAudioProperties;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.WhereCondition;
import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UserManager {
    private static final SubscriptionDataPool<UUID, SLAgentCircuit> activeAgentCircuitsPool = new SubscriptionDataPool().setCanContainNulls(true);
    private static final Object lock = new Object();
    private static final Map<UUID, UserManager> userManagers = new WeakHashMap();
    private final AtomicReference<SLAgentCircuit> activeAgentCircuit = new AtomicReference<>();
    @Nonnull
    private final Query<Chatter> activeChattersQuery;
    private final SLMessageResponseCacher<UUID, AgentDataUpdate> agentDataUpdates;
    private final AssetResponseCacher assetResponseCacher;
    private final SerializableResponseCacher<UUID, AvatarGroupList> avatarGroupLists;
    private final SLMessageResponseCacher<UUID, AvatarNotesReply> avatarNotes;
    private final SLMessageResponseCacher<AvatarPickKey, PickInfoReply> avatarPickInfos;
    private final SLMessageResponseCacher<UUID, AvatarPicksReply> avatarPicks;
    private final SLMessageResponseCacher<UUID, AvatarPropertiesReply> avatarProperties;
    @Nonnull
    private final BalanceManager balanceManager;
    @Nonnull
    private final ChatMessageDao chatMessageDao;
    @Nonnull
    private final ChatterDao chatterDao;
    @Nonnull
    private final ChatterList chatterList;
    private final Object chatterUpdateLock = new Object();
    private final SubscriptionSingleDataPool<CurrentLocationInfo> currentLocationInfoPool = new SubscriptionSingleDataPool<>();
    /* access modifiers changed from: private */
    @Nonnull
    public final DaoSession daoSession;
    private final OpportunisticExecutor dbExecutor = new OpportunisticExecutor("Database");
    private final EventBus eventBus = EventBus.getInstance();
    @Nonnull
    private final Query<Chatter> findChatterQuery;
    @Nonnull
    private final Query<UserPic> findUserPicQuery;
    @Nonnull
    private final Query<User> findUserQuery;
    @Nonnull
    private final Query<User> friendsQuery;
    private final SLMessageResponseCacher<UUID, GroupProfileReply> groupProfiles;
    private final SLMessageResponseCacher<UUID, GroupRoleDataReply> groupRoles;
    private final SLMessageResponseCacher<UUID, GroupTitlesReply> groupTitles;
    @Nonnull
    private final InventoryManager inventoryManager;
    @Nonnull
    private final Query<ChatMessage> loadMessageQuery;
    private final SubscriptionSingleDataPool<SLMinimap.MinimapBitmap> minimapBitmapPool = new SubscriptionSingleDataPool<>();
    private final SubscriptionPool<SubscriptionSingleKey, ImmutableList<MuteListEntry>> muteListPool = new SubscriptionPool<>();
    @Nonnull
    private final UnreadNotificationManager notificationManager;
    @Nonnull
    private final ObjectPopupsManager objectPopupsManager;
    @Nonnull
    private final ObjectsManager objectsManager;
    private final SLMessageResponseCacher<UUID, ParcelInfoReply> parcelInfoData;
    @Nonnull
    private final SearchManager searchManager;
    @Nonnull
    private final SyncManager syncManager;
    @Nonnull
    private final UserDao userDao;
    @Nonnull
    private final UUID userID;
    private final SubscriptionPool<SubscriptionSingleKey, SLMinimap.UserLocations> userLocationsPool = new SubscriptionPool<>();
    private final WeakPriorityRequestSet<UUID> userNameRequests = new WeakPriorityRequestSet<>();
    private final RateLimitRequestHandler<UUID, UserName> userNamesHandler = new RateLimitRequestHandler<>(new RequestProcessor<UUID, UserName, UserName>(this.userNamesPool, this.dbExecutor) {
        /* access modifiers changed from: protected */
        public boolean isRequestComplete(@Nonnull UUID uuid, UserName userName) {
            if (userName != null) {
                if (userName.getIsBadUUID()) {
                    return true;
                }
                if (!(userName.getDisplayName() == null || userName.getUserName() == null)) {
                    return true;
                }
            }
            return false;
        }

        /* access modifiers changed from: protected */
        @Nullable
        public UserName processRequest(@Nonnull UUID uuid) {
            return (UserName) UserManager.this.daoSession.getUserNameDao().load(uuid);
        }

        /* access modifiers changed from: protected */
        public UserName processResult(@Nonnull UUID uuid, UserName userName) {
            UserName userName2 = (UserName) UserManager.this.daoSession.getUserNameDao().load(uuid);
            if (userName2 != null) {
                if (userName2.mergeWith(userName)) {
                    UserManager.this.daoSession.getUserNameDao().update(userName2);
                }
                return userName2;
            }
            UserManager.this.daoSession.getUserNameDao().insertOrReplace(userName);
            return userName;
        }
    private final SubscriptionPool<UUID, UserName> userNamesPool = new SubscriptionPool<>();
    private final UserPicBitmapCache userPicBitmapCache;
    @Nonnull
    private final UserPicDao userPicDao;
    private final Object userPicUpdateLock = new Object();
    private final Object userUpdateLock = new Object();
    private final SubscriptionSingleDataPool<ChatterID> voiceActiveChatterPool = new SubscriptionSingleDataPool<>();
    private final SubscriptionSingleDataPool<VoiceAudioProperties> voiceAudioPropertiesPool = new SubscriptionSingleDataPool<>();
    private final SubscriptionDataPool<ChatterID, VoiceChatInfo> voiceChatInfoPool = new SubscriptionDataPool().setCanContainNulls(true);
    private final SubscriptionSingleDataPool<Boolean> voiceLoggedInPool = new SubscriptionSingleDataPool<>();
    private final SubscriptionSingleDataPool<ImmutableMap<UUID, String>> wornAttachmentsPool = new SubscriptionSingleDataPool<>();
    private final SubscriptionPool<SubscriptionSingleKey, ImmutableList<SLAvatarAppearance.WornItem>> wornItemsPool = new SubscriptionPool<>();
    private final SubscriptionSingleDataPool<UUID> wornOutfitLinkPool = new SubscriptionSingleDataPool<>();
    private final SubscriptionSingleDataPool<Table<SLWearableType, UUID, SLWearable>> wornWearablesPool = new SubscriptionSingleDataPool<>();

    private UserManager(@Nonnull UUID uuid) throws IllegalArgumentException {
        this.userID = uuid;
        DaoSession userDaoSession = DaoManager.getUserDaoSession(uuid);
        if (userDaoSession == null) {
            throw new IllegalArgumentException("Null DAO session");
        }
        this.daoSession = userDaoSession;
        this.userDao = userDaoSession.getUserDao();
        this.userPicDao = userDaoSession.getUserPicDao();
        this.chatMessageDao = userDaoSession.getChatMessageDao();
        this.chatterDao = userDaoSession.getChatterDao();
        this.avatarPicks = new SLMessageResponseCacher<>(userDaoSession, this.dbExecutor, "AvatarPicks");
        this.avatarPickInfos = new SLMessageResponseCacher<>(userDaoSession, this.dbExecutor, "AvatarPickInfos");
        this.avatarGroupLists = new SerializableResponseCacher<>(userDaoSession, this.dbExecutor, "AvatarGroupLists");
        this.groupProfiles = new SLMessageResponseCacher<>(userDaoSession, this.dbExecutor, "GroupProfiles");
        this.groupTitles = new SLMessageResponseCacher<>(userDaoSession, this.dbExecutor, "GroupTitles");
        this.agentDataUpdates = new SLMessageResponseCacher<>(userDaoSession, this.dbExecutor, "AgentDataUpdates");
        this.groupRoles = new SLMessageResponseCacher<>(userDaoSession, this.dbExecutor, "GroupRoles");
        this.avatarNotes = new SLMessageResponseCacher<>(userDaoSession, this.dbExecutor, "AvatarNotes");
        this.avatarProperties = new SLMessageResponseCacher<>(userDaoSession, this.dbExecutor, "AvatarProperties");
        this.parcelInfoData = new SLMessageResponseCacher<>(userDaoSession, this.dbExecutor, "ParcelInfoReply");
        this.assetResponseCacher = new AssetResponseCacher(userDaoSession, this.dbExecutor);
        this.findUserQuery = this.userDao.queryBuilder().where(UserDao.Properties.Uuid.eq(""), new WhereCondition[0]).build();
        this.friendsQuery = this.userDao.queryBuilder().where(UserDao.Properties.IsFriend.eq(Boolean.TRUE), new WhereCondition[0]).build();
        this.findUserPicQuery = this.userPicDao.queryBuilder().where(UserPicDao.Properties.Uuid.eq(""), new WhereCondition[0]).build();
        this.loadMessageQuery = this.chatMessageDao.queryBuilder().where(ChatMessageDao.Properties.Id.eq(""), new WhereCondition[0]).build();
        this.findChatterQuery = this.chatterDao.queryBuilder().where(ChatterDao.Properties.Type.eq((Object) null), ChatterDao.Properties.Uuid.eq("")).build();
        this.activeChattersQuery = this.chatterDao.queryBuilder().where(ChatterDao.Properties.Active.eq(true), new WhereCondition[0]).build();
        this.inventoryManager = new InventoryManager(uuid);
        this.notificationManager = new UnreadNotificationManager(this, userDaoSession);
        this.objectPopupsManager = new ObjectPopupsManager(this);
        this.objectsManager = new ObjectsManager(this);
        this.balanceManager = new BalanceManager(this);
        this.searchManager = new SearchManager(this, userDaoSession);
        this.chatterList = new ChatterList(this);
        this.userPicBitmapCache = new UserPicBitmapCache(this);
        this.syncManager = new SyncManager(this);
    }

    @Nonnull
    public static Subscribable<UUID, SLAgentCircuit> agentCircuits() {
        return activeAgentCircuitsPool;
    }

    @Nullable
    public static SLAgentCircuit getActiveAgentCircuit(@Nullable UUID uuid) {
        UserManager userManager;
        if (uuid == null || (userManager = getUserManager(uuid)) == null) {
            return null;
        }
        return userManager.getActiveAgentCircuit();
    }

    @Nonnull
    public static SLAgentCircuit getConnectedAgentCircuit(@Nullable UUID uuid) throws SLGridConnection.NotConnectedException {
        SLAgentCircuit activeAgentCircuit2 = getActiveAgentCircuit(uuid);
        if (activeAgentCircuit2 != null) {
            return activeAgentCircuit2;
        }
        throw new SLGridConnection.NotConnectedException();
    }

    private static File getInventoryDatabasePath(String str) {
        if (PreferenceManager.getDefaultSharedPreferences(LumiyaApp.getContext()).getString("db_location", "internal").equals("sd")) {
            File file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.lumiyaviewer.lumiya/cache/database");
            file.mkdirs();
            return new File(file, str);
        }
        File databasePath = LumiyaApp.getContext().getDatabasePath(str);
        File parentFile = databasePath.getParentFile();
        if (parentFile != null) {
            parentFile.mkdirs();
        }
        return databasePath;
    }

    @Nullable
    public static UserManager getUserManager(@Nullable UUID uuid) {
        UserManager userManager;
        if (uuid == null) {
            return null;
        }
        synchronized (lock) {
            userManager = userManagers.get(uuid);
            if (userManager == null) {
                try {
                    userManager = new UserManager(uuid);
                    userManagers.put(uuid, userManager);
                } catch (IllegalArgumentException e) {
                    Debug.Warning(e);
                    return null;
                }
            }
        }
        return userManager;
    }

    public void addChatMessage(@Nonnull ChatMessage chatMessage) {
        this.chatMessageDao.insert(chatMessage);
    }

    public void clearActiveAgentCircuit(@Nullable SLAgentCircuit sLAgentCircuit) {
        if (this.activeAgentCircuit.compareAndSet(sLAgentCircuit, (Object) null)) {
            Debug.Printf("Active agent circuit cleared.", new Object[0]);
            this.objectPopupsManager.clearObjectPopups();
            this.objectsManager.requestObjectListUpdate();
            activeAgentCircuitsPool.setData(this.userID, null);
        }
    }

    @Nullable
    public SLAgentCircuit getActiveAgentCircuit() {
        return this.activeAgentCircuit.get();
    }

    public SLMessageResponseCacher<UUID, AgentDataUpdate> getAgentDataUpdates() {
        return this.agentDataUpdates;
    }

    public AssetResponseCacher getAssetResponseCacher() {
        return this.assetResponseCacher;
    }

    public SerializableResponseCacher<UUID, AvatarGroupList> getAvatarGroupLists() {
        return this.avatarGroupLists;
    }

    public SLMessageResponseCacher<UUID, AvatarNotesReply> getAvatarNotes() {
        return this.avatarNotes;
    }

    public SLMessageResponseCacher<AvatarPickKey, PickInfoReply> getAvatarPickInfos() {
        return this.avatarPickInfos;
    }

    public SLMessageResponseCacher<UUID, AvatarPicksReply> getAvatarPicks() {
        return this.avatarPicks;
    }

    public SLMessageResponseCacher<UUID, AvatarPropertiesReply> getAvatarProperties() {
        return this.avatarProperties;
    }

    @Nonnull
    public BalanceManager getBalanceManager() {
        return this.balanceManager;
    }

    public SLMessageResponseCacher<UUID, GroupProfileReply> getCachedGroupProfiles() {
        return this.groupProfiles;
    }

    @Nullable
    public ChatMessage getChatMessage(long j) {
        Query<ChatMessage> forCurrentThread = this.loadMessageQuery.forCurrentThread();
        forCurrentThread.setParameter(0, Long.valueOf(j));
        return forCurrentThread.unique();
    }

    @Nonnull
    public ChatMessageDao getChatMessageDao() {
        return this.chatMessageDao;
    }

    public Chatter getChatter(Cursor cursor) {
        return this.chatterDao.readEntity(cursor, 0);
    }

    @Nonnull
    public ChatterDao getChatterDao() {
        return this.chatterDao;
    }

    @Nonnull
    public ChatterList getChatterList() {
        return this.chatterList;
    }

    public Subscribable<SubscriptionSingleKey, CurrentLocationInfo> getCurrentLocationInfo() {
        return this.currentLocationInfoPool;
    }

    @Nullable
    public CurrentLocationInfo getCurrentLocationInfoSnapshot() {
        return this.currentLocationInfoPool.getData();
    }

    @Nonnull
    public DaoSession getDaoSession() {
        return this.daoSession;
    }

    @Nonnull
    public Executor getDatabaseExecutor() {
        return this.dbExecutor;
    }

    @Nonnull
    public Executor getDatabaseRunOnceExecutor() {
        return this.dbExecutor.getRunOnceExecutor();
    }

    public EventBus getEventBus() {
        return this.eventBus;
    }

    public SLMessageResponseCacher<UUID, GroupRoleDataReply> getGroupRoles() {
        return this.groupRoles;
    }

    public SLMessageResponseCacher<UUID, GroupTitlesReply> getGroupTitles() {
        return this.groupTitles;
    }

    @Nonnull
    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    public SubscriptionSingleDataPool<SLMinimap.MinimapBitmap> getMinimapBitmapPool() {
        return this.minimapBitmapPool;
    }

    @Nonnull
    public ObjectPopupsManager getObjectPopupsManager() {
        return this.objectPopupsManager;
    }

    @Nonnull
    public ObjectsManager getObjectsManager() {
        return this.objectsManager;
    }

    @Nonnull
    public SearchManager getSearchManager() {
        return this.searchManager;
    }

    @Nonnull
    public SyncManager getSyncManager() {
        return this.syncManager;
    }

    @Nonnull
    public UnreadNotificationManager getUnreadNotificationManager() {
        return this.notificationManager;
    }

    public User getUser(Cursor cursor) {
        return this.userDao.readEntity(cursor, 0);
    }

    @Nonnull
    public User getUser(@Nonnull UUID uuid, @Nullable String str, @Nullable String str2) {
        Query<User> forCurrentThread = this.findUserQuery.forCurrentThread();
        forCurrentThread.setParameter(0, uuid.toString());
        User unique = forCurrentThread.unique();
        if (unique == null) {
            synchronized (this.userUpdateLock) {
                unique = forCurrentThread.unique();
                if (unique == null) {
                    unique = new User((Long) null);
                    unique.setUuid(uuid);
                    if (str != null) {
                        unique.setUserName(str);
                    }
                    if (str2 != null) {
                        unique.setDisplayName(str2);
                    }
                    this.userDao.insert(unique);
                }
            }
        }
        return unique;
    }

    @Nonnull
    public UserDao getUserDao() {
        return this.userDao;
    }

    @Nonnull
    public UUID getUserID() {
        return this.userID;
    }

    public SubscriptionPool<SubscriptionSingleKey, SLMinimap.UserLocations> getUserLocationsPool() {
        return this.userLocationsPool;
    }

    public RequestQueue<UUID, UserName> getUserNameRequestQueue() {
        return this.userNamesHandler;
    }

    public WeakPriorityRequestSet<UUID> getUserNameRequests() {
        return this.userNameRequests;
    }

    public Subscribable<UUID, UserName> getUserNames() {
        return this.userNamesPool;
    }

    public byte[] getUserPic(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        Query<UserPic> forCurrentThread = this.findUserPicQuery.forCurrentThread();
        forCurrentThread.setParameter(0, uuid.toString());
        UserPic unique = forCurrentThread.unique();
        if (unique == null) {
            return null;
        }
        return unique.getBitmap();
    }

    public UserPicBitmapCache getUserPicBitmapCache() {
        return this.userPicBitmapCache;
    }

    public Subscribable<SubscriptionSingleKey, ChatterID> getVoiceActiveChatter() {
        return this.voiceActiveChatterPool;
    }

    public Subscribable<SubscriptionSingleKey, VoiceAudioProperties> getVoiceAudioProperties() {
        return this.voiceAudioPropertiesPool;
    }

    public Subscribable<ChatterID, VoiceChatInfo> getVoiceChatInfo() {
        return this.voiceChatInfoPool;
    }

    public Subscribable<SubscriptionSingleKey, Boolean> getVoiceLoggedIn() {
        return this.voiceLoggedInPool;
    }

    public SubscriptionSingleDataPool<ImmutableMap<UUID, String>> getWornAttachmentsPool() {
        return this.wornAttachmentsPool;
    }

    public SubscriptionSingleDataPool<Table<SLWearableType, UUID, SLWearable>> getWornWearablesPool() {
        return this.wornWearablesPool;
    }

    public boolean isChatterActive(ChatterID chatterID) {
        if (chatterID.getChatterType() == ChatterID.ChatterType.Local) {
            return true;
        }
        synchronized (this.chatterUpdateLock) {
            Query<Chatter> forCurrentThread = this.findChatterQuery.forCurrentThread();
            forCurrentThread.setParameter(0, Integer.valueOf(chatterID.getChatterType().ordinal()));
            forCurrentThread.setParameter(1, StringUtils.toString(chatterID.getOptionalChatterUUID()));
            Chatter unique = forCurrentThread.unique();
            if (unique == null) {
                return false;
            }
            boolean active = unique.getActive();
            return active;
        }
    }

    public boolean isChatterMuted(ChatterID chatterID) {
        if (chatterID.getChatterType() == ChatterID.ChatterType.Local) {
            return false;
        }
        synchronized (this.chatterUpdateLock) {
            Query<Chatter> forCurrentThread = this.findChatterQuery.forCurrentThread();
            forCurrentThread.setParameter(0, Integer.valueOf(chatterID.getChatterType().ordinal()));
            forCurrentThread.setParameter(1, StringUtils.toString(chatterID.getOptionalChatterUUID()));
            Chatter unique = forCurrentThread.unique();
            if (unique == null) {
                return false;
            }
            boolean muted = unique.getMuted();
            return muted;
        }
    }

    public SubscriptionPool<SubscriptionSingleKey, ImmutableList<MuteListEntry>> muteListPool() {
        return this.muteListPool;
    }

    public SLMessageResponseCacher<UUID, ParcelInfoReply> parcelInfoData() {
        return this.parcelInfoData;
    }

    public void setActiveAgentCircuit(@Nullable SLAgentCircuit sLAgentCircuit) {
        this.activeAgentCircuit.set(sLAgentCircuit);
        if (sLAgentCircuit == null) {
            this.objectPopupsManager.clearObjectPopups();
        }
        this.objectsManager.requestObjectListUpdate();
        activeAgentCircuitsPool.setData(this.userID, sLAgentCircuit);
    }

    public void setChatterMuted(ChatterID chatterID, boolean z) {
        if (chatterID.getChatterType() != ChatterID.ChatterType.Local) {
            synchronized (this.chatterUpdateLock) {
                Query<Chatter> forCurrentThread = this.findChatterQuery.forCurrentThread();
                forCurrentThread.setParameter(0, Integer.valueOf(chatterID.getChatterType().ordinal()));
                forCurrentThread.setParameter(1, StringUtils.toString(chatterID.getOptionalChatterUUID()));
                Chatter unique = forCurrentThread.unique();
                if (unique != null) {
                    if (unique.getMuted() != z) {
                        unique.setMuted(z);
                        if (z || !(!unique.getActive())) {
                            this.chatterDao.update(unique);
                        } else {
                            this.chatterDao.delete(unique);
                        }
                    }
                } else if (z) {
                    this.chatterDao.insert(new Chatter((Long) null, chatterID.getChatterType().ordinal(), chatterID.getOptionalChatterUUID(), false, true, 0, (Long) null, (UUID) null));
                }
            }
        }
    }

    public void setCurrentLocationInfo(CurrentLocationInfo currentLocationInfo) {
        this.currentLocationInfoPool.setData(this.currentLocationInfoPool.getKey(), currentLocationInfo);
    }

    public void setUserBadUUID(UUID uuid) {
        updateUserNames(uuid, (String) null, (String) null, true);
    }

    public void setUserPic(UUID uuid, byte[] bArr) {
        if (uuid != null) {
            Query<UserPic> forCurrentThread = this.findUserPicQuery.forCurrentThread();
            forCurrentThread.setParameter(0, uuid.toString());
            synchronized (this.userPicUpdateLock) {
                UserPic unique = forCurrentThread.unique();
                if (unique == null) {
                    unique = new UserPic((Long) null);
                    unique.setUuid(uuid.toString());
                }
                unique.setBitmap(bArr);
                this.userPicDao.insertOrReplace(unique);
            }
        }
    }

    public void setVoiceActiveChatter(@Nullable ChatterID chatterID) {
        this.voiceActiveChatterPool.setData(SubscriptionSingleKey.Value, chatterID);
    }

    public void setVoiceAudioProperties(VoiceAudioProperties voiceAudioProperties) {
        this.voiceAudioPropertiesPool.setData(SubscriptionSingleKey.Value, voiceAudioProperties);
    }

    public void setVoiceChatInfo(@Nonnull ChatterID chatterID, @Nullable VoiceChatInfo voiceChatInfo) {
        this.voiceChatInfoPool.setData(chatterID, voiceChatInfo);
    }

    public void setVoiceLoggedIn(boolean z) {
        this.voiceLoggedInPool.setData(SubscriptionSingleKey.Value, Boolean.valueOf(z));
    }

    public void updateUserNames(@Nonnull UUID uuid, @Nullable String str, @Nullable String str2) {
        updateUserNames(uuid, str, str2, false);
    }

    public void updateUserNames(@Nonnull UUID uuid, @Nullable String str, @Nullable String str2, boolean z) {
        Query<User> forCurrentThread = this.findUserQuery.forCurrentThread();
        forCurrentThread.setParameter(0, uuid.toString());
        synchronized (this.userUpdateLock) {
            User unique = forCurrentThread.unique();
            if (unique != null) {
                if (unique.getUserName() == null && str != null) {
                    unique.setUserName(str);
                }
                if (unique.getDisplayName() == null && str2 != null) {
                    unique.setDisplayName(str2);
                }
                unique.setBadUUID(z);
                this.userDao.update(unique);
            } else {
                User user = new User((Long) null);
                user.setUuid(uuid);
                if (str != null) {
                    user.setUserName(str);
                }
                if (str2 != null) {
                    user.setDisplayName(str2);
                }
                user.setBadUUID(z);
                this.userDao.insert(user);
            }
        }
        this.eventBus.publish(new EventUserInfoChanged(this.userID, uuid, 2));
    }

    public SubscriptionPool<SubscriptionSingleKey, ImmutableList<SLAvatarAppearance.WornItem>> wornItems() {
        return this.wornItemsPool;
    }

    public SubscriptionSingleDataPool<UUID> wornOutfitLink() {
        return this.wornOutfitLinkPool;
    }
}
