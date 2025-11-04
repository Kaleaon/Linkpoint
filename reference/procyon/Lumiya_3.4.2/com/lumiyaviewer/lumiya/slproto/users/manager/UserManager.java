// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import de.greenrobot.dao.AbstractDao;
import com.lumiyaviewer.lumiya.slproto.users.events.EventUserInfoChanged;
import com.lumiyaviewer.lumiya.utils.StringUtils;
import com.lumiyaviewer.lumiya.react.RequestQueue;
import android.database.Cursor;
import com.lumiyaviewer.lumiya.Debug;
import android.os.Environment;
import android.preference.PreferenceManager;
import com.lumiyaviewer.lumiya.LumiyaApp;
import java.io.File;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.react.Subscribable;
import de.greenrobot.dao.query.WhereCondition;
import com.lumiyaviewer.lumiya.dao.DaoManager;
import javax.annotation.Nullable;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.RequestProcessor;
import java.util.WeakHashMap;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.google.common.collect.Table;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceAudioProperties;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.dao.UserPicDao;
import com.lumiyaviewer.lumiya.dao.UserName;
import com.lumiyaviewer.lumiya.react.RateLimitRequestHandler;
import com.lumiyaviewer.lumiya.utils.reqset.WeakPriorityRequestSet;
import com.lumiyaviewer.lumiya.dao.UserDao;
import com.lumiyaviewer.lumiya.slproto.messages.ParcelInfoReply;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListEntry;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.slproto.modules.SLMinimap;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.dao.User;
import com.lumiyaviewer.lumiya.dao.UserPic;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.react.OpportunisticExecutor;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import com.lumiyaviewer.lumiya.dao.ChatterDao;
import com.lumiyaviewer.lumiya.dao.ChatMessageDao;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPicksReply;
import com.lumiyaviewer.lumiya.slproto.messages.PickInfoReply;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarNotesReply;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.users.SerializableResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetResponseCacher;
import com.lumiyaviewer.lumiya.slproto.messages.AgentDataUpdate;
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.dao.Chatter;
import de.greenrobot.dao.query.Query;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Map;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionDataPool;

public class UserManager
{
    private static final SubscriptionDataPool<UUID, SLAgentCircuit> activeAgentCircuitsPool;
    private static final Object lock;
    private static final Map<UUID, UserManager> userManagers;
    private final AtomicReference<SLAgentCircuit> activeAgentCircuit;
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
    private final Object chatterUpdateLock;
    private final SubscriptionSingleDataPool<CurrentLocationInfo> currentLocationInfoPool;
    @Nonnull
    private final DaoSession daoSession;
    private final OpportunisticExecutor dbExecutor;
    private final EventBus eventBus;
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
    private final SubscriptionSingleDataPool<SLMinimap.MinimapBitmap> minimapBitmapPool;
    private final SubscriptionPool<SubscriptionSingleKey, ImmutableList<MuteListEntry>> muteListPool;
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
    private final SubscriptionPool<SubscriptionSingleKey, SLMinimap.UserLocations> userLocationsPool;
    private final WeakPriorityRequestSet<UUID> userNameRequests;
    private final RateLimitRequestHandler<UUID, UserName> userNamesHandler;
    private final SubscriptionPool<UUID, UserName> userNamesPool;
    private final UserPicBitmapCache userPicBitmapCache;
    @Nonnull
    private final UserPicDao userPicDao;
    private final Object userPicUpdateLock;
    private final Object userUpdateLock;
    private final SubscriptionSingleDataPool<ChatterID> voiceActiveChatterPool;
    private final SubscriptionSingleDataPool<VoiceAudioProperties> voiceAudioPropertiesPool;
    private final SubscriptionDataPool<ChatterID, VoiceChatInfo> voiceChatInfoPool;
    private final SubscriptionSingleDataPool<Boolean> voiceLoggedInPool;
    private final SubscriptionSingleDataPool<ImmutableMap<UUID, String>> wornAttachmentsPool;
    private final SubscriptionPool<SubscriptionSingleKey, ImmutableList<SLAvatarAppearance.WornItem>> wornItemsPool;
    private final SubscriptionSingleDataPool<UUID> wornOutfitLinkPool;
    private final SubscriptionSingleDataPool<Table<SLWearableType, UUID, SLWearable>> wornWearablesPool;
    
    static {
        lock = new Object();
        userManagers = new WeakHashMap<UUID, UserManager>();
        activeAgentCircuitsPool = new SubscriptionDataPool<UUID, SLAgentCircuit>().setCanContainNulls(true);
    }
    
    private UserManager(@Nonnull final UUID userID) throws IllegalArgumentException {
        this.eventBus = EventBus.getInstance();
        this.dbExecutor = new OpportunisticExecutor("Database");
        this.userUpdateLock = new Object();
        this.userPicUpdateLock = new Object();
        this.chatterUpdateLock = new Object();
        this.userNameRequests = new WeakPriorityRequestSet<UUID>();
        this.activeAgentCircuit = new AtomicReference<SLAgentCircuit>();
        this.minimapBitmapPool = new SubscriptionSingleDataPool<SLMinimap.MinimapBitmap>();
        this.userLocationsPool = new SubscriptionPool<SubscriptionSingleKey, SLMinimap.UserLocations>();
        this.wornAttachmentsPool = new SubscriptionSingleDataPool<ImmutableMap<UUID, String>>();
        this.wornWearablesPool = new SubscriptionSingleDataPool<Table<SLWearableType, UUID, SLWearable>>();
        this.wornItemsPool = new SubscriptionPool<SubscriptionSingleKey, ImmutableList<SLAvatarAppearance.WornItem>>();
        this.wornOutfitLinkPool = new SubscriptionSingleDataPool<UUID>();
        this.muteListPool = new SubscriptionPool<SubscriptionSingleKey, ImmutableList<MuteListEntry>>();
        this.currentLocationInfoPool = new SubscriptionSingleDataPool<CurrentLocationInfo>();
        this.voiceLoggedInPool = new SubscriptionSingleDataPool<Boolean>();
        this.voiceChatInfoPool = new SubscriptionDataPool<ChatterID, VoiceChatInfo>().setCanContainNulls(true);
        this.voiceActiveChatterPool = new SubscriptionSingleDataPool<ChatterID>();
        this.voiceAudioPropertiesPool = new SubscriptionSingleDataPool<VoiceAudioProperties>();
        this.userNamesPool = new SubscriptionPool<UUID, UserName>();
        this.userNamesHandler = new RateLimitRequestHandler<UUID, UserName>(new RequestProcessor<UUID, UserName, UserName>((RequestSource)this.userNamesPool, (Executor)this.dbExecutor) {
            @Override
            protected boolean isRequestComplete(@Nonnull final UUID uuid, final UserName userName) {
                final boolean b = true;
                if (userName == null) {
                    return false;
                }
                boolean b2 = b;
                if (!userName.getIsBadUUID()) {
                    if (userName.getDisplayName() == null || userName.getUserName() == null) {
                        return false;
                    }
                    b2 = b;
                }
                return b2;
                b2 = false;
                return b2;
            }
            
            @Nullable
            @Override
            protected UserName processRequest(@Nonnull final UUID uuid) {
                return UserManager.this.daoSession.getUserNameDao().load(uuid);
            }
            
            @Override
            protected UserName processResult(@Nonnull final UUID uuid, final UserName userName) {
                final UserName userName2 = UserManager.this.daoSession.getUserNameDao().load(uuid);
                if (userName2 != null) {
                    if (userName2.mergeWith(userName)) {
                        ((AbstractDao<UserName, K>)UserManager.this.daoSession.getUserNameDao()).update(userName2);
                    }
                    return userName2;
                }
                ((AbstractDao<UserName, K>)UserManager.this.daoSession.getUserNameDao()).insertOrReplace(userName);
                return userName;
            }
        });
        this.userID = userID;
        final DaoSession userDaoSession = DaoManager.getUserDaoSession(userID);
        if (userDaoSession == null) {
            throw new IllegalArgumentException("Null DAO session");
        }
        this.daoSession = userDaoSession;
        this.userDao = userDaoSession.getUserDao();
        this.userPicDao = userDaoSession.getUserPicDao();
        this.chatMessageDao = userDaoSession.getChatMessageDao();
        this.chatterDao = userDaoSession.getChatterDao();
        this.avatarPicks = new SLMessageResponseCacher<UUID, AvatarPicksReply>(userDaoSession, this.dbExecutor, "AvatarPicks");
        this.avatarPickInfos = new SLMessageResponseCacher<AvatarPickKey, PickInfoReply>(userDaoSession, this.dbExecutor, "AvatarPickInfos");
        this.avatarGroupLists = new SerializableResponseCacher<UUID, AvatarGroupList>(userDaoSession, this.dbExecutor, "AvatarGroupLists");
        this.groupProfiles = new SLMessageResponseCacher<UUID, GroupProfileReply>(userDaoSession, this.dbExecutor, "GroupProfiles");
        this.groupTitles = new SLMessageResponseCacher<UUID, GroupTitlesReply>(userDaoSession, this.dbExecutor, "GroupTitles");
        this.agentDataUpdates = new SLMessageResponseCacher<UUID, AgentDataUpdate>(userDaoSession, this.dbExecutor, "AgentDataUpdates");
        this.groupRoles = new SLMessageResponseCacher<UUID, GroupRoleDataReply>(userDaoSession, this.dbExecutor, "GroupRoles");
        this.avatarNotes = new SLMessageResponseCacher<UUID, AvatarNotesReply>(userDaoSession, this.dbExecutor, "AvatarNotes");
        this.avatarProperties = new SLMessageResponseCacher<UUID, AvatarPropertiesReply>(userDaoSession, this.dbExecutor, "AvatarProperties");
        this.parcelInfoData = new SLMessageResponseCacher<UUID, ParcelInfoReply>(userDaoSession, this.dbExecutor, "ParcelInfoReply");
        this.assetResponseCacher = new AssetResponseCacher(userDaoSession, this.dbExecutor);
        this.findUserQuery = ((AbstractDao<User, K>)this.userDao).queryBuilder().where(UserDao.Properties.Uuid.eq(""), new WhereCondition[0]).build();
        this.friendsQuery = ((AbstractDao<User, K>)this.userDao).queryBuilder().where(UserDao.Properties.IsFriend.eq(Boolean.TRUE), new WhereCondition[0]).build();
        this.findUserPicQuery = ((AbstractDao<UserPic, K>)this.userPicDao).queryBuilder().where(UserPicDao.Properties.Uuid.eq(""), new WhereCondition[0]).build();
        this.loadMessageQuery = ((AbstractDao<ChatMessage, K>)this.chatMessageDao).queryBuilder().where(ChatMessageDao.Properties.Id.eq(""), new WhereCondition[0]).build();
        this.findChatterQuery = ((AbstractDao<Chatter, K>)this.chatterDao).queryBuilder().where(ChatterDao.Properties.Type.eq(null), ChatterDao.Properties.Uuid.eq("")).build();
        this.activeChattersQuery = ((AbstractDao<Chatter, K>)this.chatterDao).queryBuilder().where(ChatterDao.Properties.Active.eq(true), new WhereCondition[0]).build();
        this.inventoryManager = new InventoryManager(userID);
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
        return UserManager.activeAgentCircuitsPool;
    }
    
    @Nullable
    public static SLAgentCircuit getActiveAgentCircuit(@Nullable final UUID uuid) {
        if (uuid != null) {
            final UserManager userManager = getUserManager(uuid);
            if (userManager != null) {
                return userManager.getActiveAgentCircuit();
            }
        }
        return null;
    }
    
    @Nonnull
    public static SLAgentCircuit getConnectedAgentCircuit(@Nullable final UUID uuid) throws SLGridConnection.NotConnectedException {
        final SLAgentCircuit activeAgentCircuit = getActiveAgentCircuit(uuid);
        if (activeAgentCircuit != null) {
            return activeAgentCircuit;
        }
        throw new SLGridConnection.NotConnectedException();
    }
    
    private static File getInventoryDatabasePath(final String child) {
        if (PreferenceManager.getDefaultSharedPreferences(LumiyaApp.getContext()).getString("db_location", "internal").equals("sd")) {
            final File parent = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.lumiyaviewer.lumiya/cache/database");
            parent.mkdirs();
            return new File(parent, child);
        }
        final File databasePath = LumiyaApp.getContext().getDatabasePath(child);
        final File parentFile = databasePath.getParentFile();
        if (parentFile != null) {
            parentFile.mkdirs();
        }
        return databasePath;
    }
    
    @Nullable
    public static UserManager getUserManager(@Nullable final UUID uuid) {
        if (uuid == null) {
            return null;
        }
        synchronized (UserManager.lock) {
            UserManager userManager;
            if ((userManager = UserManager.userManagers.get(uuid)) != null) {
                return userManager;
            }
            try {
                userManager = new UserManager(uuid);
                UserManager.userManagers.put(uuid, userManager);
                return userManager;
            }
            catch (final IllegalArgumentException ex) {
                Debug.Warning(ex);
                return null;
            }
        }
    }
    
    public void addChatMessage(@Nonnull final ChatMessage chatMessage) {
        ((AbstractDao<ChatMessage, K>)this.chatMessageDao).insert(chatMessage);
    }
    
    public void clearActiveAgentCircuit(@Nullable final SLAgentCircuit expectedValue) {
        if (this.activeAgentCircuit.compareAndSet(expectedValue, null)) {
            Debug.Printf("Active agent circuit cleared.", new Object[0]);
            this.objectPopupsManager.clearObjectPopups();
            this.objectsManager.requestObjectListUpdate();
            UserManager.activeAgentCircuitsPool.setData(this.userID, null);
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
    public ChatMessage getChatMessage(final long l) {
        final Query<ChatMessage> forCurrentThread = this.loadMessageQuery.forCurrentThread();
        forCurrentThread.setParameter(0, l);
        return forCurrentThread.unique();
    }
    
    @Nonnull
    public ChatMessageDao getChatMessageDao() {
        return this.chatMessageDao;
    }
    
    public Chatter getChatter(final Cursor cursor) {
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
        return (Subscribable<SubscriptionSingleKey, CurrentLocationInfo>)this.currentLocationInfoPool;
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
    
    public User getUser(final Cursor cursor) {
        return this.userDao.readEntity(cursor, 0);
    }
    
    @Nonnull
    public User getUser(@Nonnull final UUID uuid, @Nullable final String userName, @Nullable final String displayName) {
        final Query<User> forCurrentThread = this.findUserQuery.forCurrentThread();
        forCurrentThread.setParameter(0, uuid.toString());
        User user;
        if ((user = forCurrentThread.unique()) != null) {
            return user;
        }
        synchronized (this.userUpdateLock) {
            if ((user = forCurrentThread.unique()) == null) {
                user = new User(null);
                user.setUuid(uuid);
                if (userName != null) {
                    user.setUserName(userName);
                }
                if (displayName != null) {
                    user.setDisplayName(displayName);
                }
                ((AbstractDao<User, K>)this.userDao).insert(user);
            }
            return user;
        }
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
    
    public byte[] getUserPic(final UUID uuid) {
        if (uuid == null) {
            return null;
        }
        final Query<UserPic> forCurrentThread = this.findUserPicQuery.forCurrentThread();
        forCurrentThread.setParameter(0, uuid.toString());
        final UserPic userPic = forCurrentThread.unique();
        if (userPic == null) {
            return null;
        }
        return userPic.getBitmap();
    }
    
    public UserPicBitmapCache getUserPicBitmapCache() {
        return this.userPicBitmapCache;
    }
    
    public Subscribable<SubscriptionSingleKey, ChatterID> getVoiceActiveChatter() {
        return (Subscribable<SubscriptionSingleKey, ChatterID>)this.voiceActiveChatterPool;
    }
    
    public Subscribable<SubscriptionSingleKey, VoiceAudioProperties> getVoiceAudioProperties() {
        return (Subscribable<SubscriptionSingleKey, VoiceAudioProperties>)this.voiceAudioPropertiesPool;
    }
    
    public Subscribable<ChatterID, VoiceChatInfo> getVoiceChatInfo() {
        return this.voiceChatInfoPool;
    }
    
    public Subscribable<SubscriptionSingleKey, Boolean> getVoiceLoggedIn() {
        return (Subscribable<SubscriptionSingleKey, Boolean>)this.voiceLoggedInPool;
    }
    
    public SubscriptionSingleDataPool<ImmutableMap<UUID, String>> getWornAttachmentsPool() {
        return this.wornAttachmentsPool;
    }
    
    public SubscriptionSingleDataPool<Table<SLWearableType, UUID, SLWearable>> getWornWearablesPool() {
        return this.wornWearablesPool;
    }
    
    public boolean isChatterActive(final ChatterID chatterID) {
        if (chatterID.getChatterType() == ChatterID.ChatterType.Local) {
            return true;
        }
        synchronized (this.chatterUpdateLock) {
            final Query<Chatter> forCurrentThread = this.findChatterQuery.forCurrentThread();
            forCurrentThread.setParameter(0, chatterID.getChatterType().ordinal());
            forCurrentThread.setParameter(1, StringUtils.toString(chatterID.getOptionalChatterUUID()));
            final Chatter chatter = forCurrentThread.unique();
            return chatter != null && chatter.getActive();
        }
    }
    
    public boolean isChatterMuted(final ChatterID chatterID) {
        if (chatterID.getChatterType() == ChatterID.ChatterType.Local) {
            return false;
        }
        synchronized (this.chatterUpdateLock) {
            final Query<Chatter> forCurrentThread = this.findChatterQuery.forCurrentThread();
            forCurrentThread.setParameter(0, chatterID.getChatterType().ordinal());
            forCurrentThread.setParameter(1, StringUtils.toString(chatterID.getOptionalChatterUUID()));
            final Chatter chatter = forCurrentThread.unique();
            return chatter != null && chatter.getMuted();
        }
    }
    
    public SubscriptionPool<SubscriptionSingleKey, ImmutableList<MuteListEntry>> muteListPool() {
        return this.muteListPool;
    }
    
    public SLMessageResponseCacher<UUID, ParcelInfoReply> parcelInfoData() {
        return this.parcelInfoData;
    }
    
    public void setActiveAgentCircuit(@Nullable final SLAgentCircuit newValue) {
        this.activeAgentCircuit.set(newValue);
        if (newValue == null) {
            this.objectPopupsManager.clearObjectPopups();
        }
        this.objectsManager.requestObjectListUpdate();
        UserManager.activeAgentCircuitsPool.setData(this.userID, newValue);
    }
    
    public void setChatterMuted(final ChatterID chatterID, final boolean muted) {
        if (chatterID.getChatterType() == ChatterID.ChatterType.Local) {
            return;
        }
        while (true) {
            synchronized (this.chatterUpdateLock) {
                final Query<Chatter> forCurrentThread = this.findChatterQuery.forCurrentThread();
                forCurrentThread.setParameter(0, chatterID.getChatterType().ordinal());
                forCurrentThread.setParameter(1, StringUtils.toString(chatterID.getOptionalChatterUUID()));
                final Chatter chatter = forCurrentThread.unique();
                if (chatter != null) {
                    if (chatter.getMuted() != muted) {
                        chatter.setMuted(muted);
                        if (!muted && (chatter.getActive() ^ true)) {
                            ((AbstractDao<Chatter, K>)this.chatterDao).delete(chatter);
                        }
                        else {
                            ((AbstractDao<Chatter, K>)this.chatterDao).update(chatter);
                        }
                    }
                    return;
                }
            }
            if (muted) {
                final ChatterID chatterID2;
                ((AbstractDao<Chatter, K>)this.chatterDao).insert(new Chatter(null, chatterID2.getChatterType().ordinal(), chatterID2.getOptionalChatterUUID(), false, true, 0, null, null));
            }
        }
    }
    
    public void setCurrentLocationInfo(final CurrentLocationInfo currentLocationInfo) {
        this.currentLocationInfoPool.setData(this.currentLocationInfoPool.getKey(), currentLocationInfo);
    }
    
    public void setUserBadUUID(final UUID uuid) {
        this.updateUserNames(uuid, null, null, true);
    }
    
    public void setUserPic(final UUID uuid, final byte[] bitmap) {
        if (uuid == null) {
            return;
        }
        final Query<UserPic> forCurrentThread = this.findUserPicQuery.forCurrentThread();
        forCurrentThread.setParameter(0, uuid.toString());
        synchronized (this.userPicUpdateLock) {
            UserPic userPic;
            if ((userPic = forCurrentThread.unique()) == null) {
                userPic = new UserPic(null);
                userPic.setUuid(uuid.toString());
            }
            userPic.setBitmap(bitmap);
            ((AbstractDao<UserPic, K>)this.userPicDao).insertOrReplace(userPic);
        }
    }
    
    public void setVoiceActiveChatter(@Nullable final ChatterID chatterID) {
        this.voiceActiveChatterPool.setData(SubscriptionSingleKey.Value, chatterID);
    }
    
    public void setVoiceAudioProperties(final VoiceAudioProperties voiceAudioProperties) {
        this.voiceAudioPropertiesPool.setData(SubscriptionSingleKey.Value, voiceAudioProperties);
    }
    
    public void setVoiceChatInfo(@Nonnull final ChatterID chatterID, @Nullable final VoiceChatInfo voiceChatInfo) {
        this.voiceChatInfoPool.setData(chatterID, voiceChatInfo);
    }
    
    public void setVoiceLoggedIn(final boolean b) {
        this.voiceLoggedInPool.setData(SubscriptionSingleKey.Value, Boolean.valueOf(b));
    }
    
    public void updateUserNames(@Nonnull final UUID uuid, @Nullable final String s, @Nullable final String s2) {
        this.updateUserNames(uuid, s, s2, false);
    }
    
    public void updateUserNames(@Nonnull final UUID uuid, @Nullable final String s, @Nullable final String s2, final boolean b) {
        final Query<User> forCurrentThread = this.findUserQuery.forCurrentThread();
        forCurrentThread.setParameter(0, uuid.toString());
        synchronized (this.userUpdateLock) {
            final User user = forCurrentThread.unique();
            if (user != null) {
                if (user.getUserName() == null && s != null) {
                    user.setUserName(s);
                }
                if (user.getDisplayName() == null && s2 != null) {
                    user.setDisplayName(s2);
                }
                user.setBadUUID(b);
                ((AbstractDao<User, K>)this.userDao).update(user);
            }
            else {
                final User user2 = new User(null);
                user2.setUuid(uuid);
                if (s != null) {
                    user2.setUserName(s);
                }
                if (s2 != null) {
                    user2.setDisplayName(s2);
                }
                user2.setBadUUID(b);
                ((AbstractDao<User, K>)this.userDao).insert(user2);
            }
            monitorexit(this.userUpdateLock);
            this.eventBus.publish(new EventUserInfoChanged(this.userID, uuid, 2));
        }
    }
    
    public SubscriptionPool<SubscriptionSingleKey, ImmutableList<SLAvatarAppearance.WornItem>> wornItems() {
        return this.wornItemsPool;
    }
    
    public SubscriptionSingleDataPool<UUID> wornOutfitLink() {
        return this.wornOutfitLinkPool;
    }
}
