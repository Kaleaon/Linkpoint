// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Environment;
import android.preference.PreferenceManager;
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
import com.lumiyaviewer.lumiya.dao.UserNameDao;
import com.lumiyaviewer.lumiya.dao.UserPic;
import com.lumiyaviewer.lumiya.dao.UserPicDao;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.react.OpportunisticExecutor;
import com.lumiyaviewer.lumiya.react.RateLimitRequestHandler;
import com.lumiyaviewer.lumiya.react.RequestProcessor;
import com.lumiyaviewer.lumiya.react.RequestQueue;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.SubscriptionDataPool;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.SerializableResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.events.EventUserInfoChanged;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetResponseCacher;
import com.lumiyaviewer.lumiya.utils.StringUtils;
import com.lumiyaviewer.lumiya.utils.reqset.WeakPriorityRequestSet;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceAudioProperties;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            InventoryManager, UnreadNotificationManager, ObjectPopupsManager, ObjectsManager, 
//            BalanceManager, SearchManager, ChatterList, UserPicBitmapCache, 
//            SyncManager, CurrentLocationInfo

public class UserManager
{

    private static final SubscriptionDataPool activeAgentCircuitsPool = (new SubscriptionDataPool()).setCanContainNulls(true);
    private static final Object lock = new Object();
    private static final Map userManagers = new WeakHashMap();
    private final AtomicReference activeAgentCircuit = new AtomicReference();
    private final Query activeChattersQuery;
    private final SLMessageResponseCacher agentDataUpdates;
    private final AssetResponseCacher assetResponseCacher;
    private final SerializableResponseCacher avatarGroupLists;
    private final SLMessageResponseCacher avatarNotes;
    private final SLMessageResponseCacher avatarPickInfos;
    private final SLMessageResponseCacher avatarPicks;
    private final SLMessageResponseCacher avatarProperties;
    private final BalanceManager balanceManager;
    private final ChatMessageDao chatMessageDao;
    private final ChatterDao chatterDao;
    private final ChatterList chatterList;
    private final Object chatterUpdateLock = new Object();
    private final SubscriptionSingleDataPool currentLocationInfoPool = new SubscriptionSingleDataPool();
    private final DaoSession daoSession;
    private final OpportunisticExecutor dbExecutor = new OpportunisticExecutor("Database");
    private final EventBus eventBus = EventBus.getInstance();
    private final Query findChatterQuery;
    private final Query findUserPicQuery;
    private final Query findUserQuery;
    private final Query friendsQuery;
    private final SLMessageResponseCacher groupProfiles;
    private final SLMessageResponseCacher groupRoles;
    private final SLMessageResponseCacher groupTitles;
    private final InventoryManager inventoryManager;
    private final Query loadMessageQuery;
    private final SubscriptionSingleDataPool minimapBitmapPool = new SubscriptionSingleDataPool();
    private final SubscriptionPool muteListPool = new SubscriptionPool();
    private final UnreadNotificationManager notificationManager;
    private final ObjectPopupsManager objectPopupsManager;
    private final ObjectsManager objectsManager;
    private final SLMessageResponseCacher parcelInfoData;
    private final SearchManager searchManager;
    private final SyncManager syncManager;
    private final UserDao userDao;
    private final UUID userID;
    private final SubscriptionPool userLocationsPool = new SubscriptionPool();
    private final WeakPriorityRequestSet userNameRequests = new WeakPriorityRequestSet();
    private final RateLimitRequestHandler userNamesHandler;
    private final SubscriptionPool userNamesPool = new SubscriptionPool();
    private final UserPicBitmapCache userPicBitmapCache;
    private final UserPicDao userPicDao;
    private final Object userPicUpdateLock = new Object();
    private final Object userUpdateLock = new Object();
    private final SubscriptionSingleDataPool voiceActiveChatterPool = new SubscriptionSingleDataPool();
    private final SubscriptionSingleDataPool voiceAudioPropertiesPool = new SubscriptionSingleDataPool();
    private final SubscriptionDataPool voiceChatInfoPool = (new SubscriptionDataPool()).setCanContainNulls(true);
    private final SubscriptionSingleDataPool voiceLoggedInPool = new SubscriptionSingleDataPool();
    private final SubscriptionSingleDataPool wornAttachmentsPool = new SubscriptionSingleDataPool();
    private final SubscriptionPool wornItemsPool = new SubscriptionPool();
    private final SubscriptionSingleDataPool wornOutfitLinkPool = new SubscriptionSingleDataPool();
    private final SubscriptionSingleDataPool wornWearablesPool = new SubscriptionSingleDataPool();

    static DaoSession _2D_get0(UserManager usermanager)
    {
        return usermanager.daoSession;
    }

    private UserManager(UUID uuid)
        throws IllegalArgumentException
    {
        userNamesHandler = new RateLimitRequestHandler(new RequestProcessor(userNamesPool, dbExecutor) {

            final UserManager this$0;

            protected volatile boolean isRequestComplete(Object obj, Object obj1)
            {
                return isRequestComplete((UUID)obj, (UserName)obj1);
            }

            protected boolean isRequestComplete(UUID uuid1, UserName username)
            {
                return username != null && (username.getIsBadUUID() || username.getDisplayName() != null && username.getUserName() != null);
            }

            protected UserName processRequest(UUID uuid1)
            {
                return (UserName)UserManager._2D_get0(UserManager.this).getUserNameDao().load(uuid1);
            }

            protected volatile Object processRequest(Object obj)
            {
                return processRequest((UUID)obj);
            }

            protected UserName processResult(UUID uuid1, UserName username)
            {
                uuid1 = (UserName)UserManager._2D_get0(UserManager.this).getUserNameDao().load(uuid1);
                if (uuid1 != null)
                {
                    if (uuid1.mergeWith(username))
                    {
                        UserManager._2D_get0(UserManager.this).getUserNameDao().update(uuid1);
                    }
                    return uuid1;
                } else
                {
                    UserManager._2D_get0(UserManager.this).getUserNameDao().insertOrReplace(username);
                    return username;
                }
            }

            protected volatile Object processResult(Object obj, Object obj1)
            {
                return processResult((UUID)obj, (UserName)obj1);
            }

            
            {
                this$0 = UserManager.this;
                super(requestsource, executor);
            }
        });
        userID = uuid;
        DaoSession daosession = DaoManager.getUserDaoSession(uuid);
        if (daosession == null)
        {
            throw new IllegalArgumentException("Null DAO session");
        } else
        {
            daoSession = daosession;
            userDao = daosession.getUserDao();
            userPicDao = daosession.getUserPicDao();
            chatMessageDao = daosession.getChatMessageDao();
            chatterDao = daosession.getChatterDao();
            avatarPicks = new SLMessageResponseCacher(daosession, dbExecutor, "AvatarPicks");
            avatarPickInfos = new SLMessageResponseCacher(daosession, dbExecutor, "AvatarPickInfos");
            avatarGroupLists = new SerializableResponseCacher(daosession, dbExecutor, "AvatarGroupLists");
            groupProfiles = new SLMessageResponseCacher(daosession, dbExecutor, "GroupProfiles");
            groupTitles = new SLMessageResponseCacher(daosession, dbExecutor, "GroupTitles");
            agentDataUpdates = new SLMessageResponseCacher(daosession, dbExecutor, "AgentDataUpdates");
            groupRoles = new SLMessageResponseCacher(daosession, dbExecutor, "GroupRoles");
            avatarNotes = new SLMessageResponseCacher(daosession, dbExecutor, "AvatarNotes");
            avatarProperties = new SLMessageResponseCacher(daosession, dbExecutor, "AvatarProperties");
            parcelInfoData = new SLMessageResponseCacher(daosession, dbExecutor, "ParcelInfoReply");
            assetResponseCacher = new AssetResponseCacher(daosession, dbExecutor);
            findUserQuery = userDao.queryBuilder().where(com.lumiyaviewer.lumiya.dao.UserDao.Properties.Uuid.eq(""), new WhereCondition[0]).build();
            friendsQuery = userDao.queryBuilder().where(com.lumiyaviewer.lumiya.dao.UserDao.Properties.IsFriend.eq(Boolean.TRUE), new WhereCondition[0]).build();
            findUserPicQuery = userPicDao.queryBuilder().where(com.lumiyaviewer.lumiya.dao.UserPicDao.Properties.Uuid.eq(""), new WhereCondition[0]).build();
            loadMessageQuery = chatMessageDao.queryBuilder().where(com.lumiyaviewer.lumiya.dao.ChatMessageDao.Properties.Id.eq(""), new WhereCondition[0]).build();
            findChatterQuery = chatterDao.queryBuilder().where(com.lumiyaviewer.lumiya.dao.ChatterDao.Properties.Type.eq(null), new WhereCondition[] {
                com.lumiyaviewer.lumiya.dao.ChatterDao.Properties.Uuid.eq("")
            }).build();
            activeChattersQuery = chatterDao.queryBuilder().where(com.lumiyaviewer.lumiya.dao.ChatterDao.Properties.Active.eq(Boolean.valueOf(true)), new WhereCondition[0]).build();
            inventoryManager = new InventoryManager(uuid);
            notificationManager = new UnreadNotificationManager(this, daosession);
            objectPopupsManager = new ObjectPopupsManager(this);
            objectsManager = new ObjectsManager(this);
            balanceManager = new BalanceManager(this);
            searchManager = new SearchManager(this, daosession);
            chatterList = new ChatterList(this);
            userPicBitmapCache = new UserPicBitmapCache(this);
            syncManager = new SyncManager(this);
            return;
        }
    }

    public static Subscribable agentCircuits()
    {
        return activeAgentCircuitsPool;
    }

    public static SLAgentCircuit getActiveAgentCircuit(UUID uuid)
    {
        if (uuid != null)
        {
            uuid = getUserManager(uuid);
            if (uuid != null)
            {
                return uuid.getActiveAgentCircuit();
            }
        }
        return null;
    }

    public static SLAgentCircuit getConnectedAgentCircuit(UUID uuid)
        throws com.lumiyaviewer.lumiya.slproto.SLGridConnection.NotConnectedException
    {
        uuid = getActiveAgentCircuit(uuid);
        if (uuid != null)
        {
            return uuid;
        } else
        {
            throw new com.lumiyaviewer.lumiya.slproto.SLGridConnection.NotConnectedException();
        }
    }

    private static File getInventoryDatabasePath(String s)
    {
        if (PreferenceManager.getDefaultSharedPreferences(LumiyaApp.getContext()).getString("db_location", "internal").equals("sd"))
        {
            File file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.lumiyaviewer.lumiya/cache/database");
            file.mkdirs();
            return new File(file, s);
        }
        s = LumiyaApp.getContext().getDatabasePath(s);
        File file1 = s.getParentFile();
        if (file1 != null)
        {
            file1.mkdirs();
        }
        return s;
    }

    public static UserManager getUserManager(UUID uuid)
    {
        if (uuid == null)
        {
            return null;
        }
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        UserManager usermanager1 = (UserManager)userManagers.get(uuid);
        UserManager usermanager;
        usermanager = usermanager1;
        if (usermanager1 != null)
        {
            break MISSING_BLOCK_LABEL_51;
        }
        usermanager = new UserManager(uuid);
        userManagers.put(uuid, usermanager);
        obj;
        JVM INSTR monitorexit ;
        return usermanager;
        uuid;
        Debug.Warning(uuid);
        obj;
        JVM INSTR monitorexit ;
        return null;
        uuid;
        throw uuid;
    }

    public void addChatMessage(ChatMessage chatmessage)
    {
        chatMessageDao.insert(chatmessage);
    }

    public void clearActiveAgentCircuit(SLAgentCircuit slagentcircuit)
    {
        if (activeAgentCircuit.compareAndSet(slagentcircuit, null))
        {
            Debug.Printf("Active agent circuit cleared.", new Object[0]);
            objectPopupsManager.clearObjectPopups();
            objectsManager.requestObjectListUpdate();
            activeAgentCircuitsPool.setData(userID, null);
        }
    }

    public SLAgentCircuit getActiveAgentCircuit()
    {
        return (SLAgentCircuit)activeAgentCircuit.get();
    }

    public SLMessageResponseCacher getAgentDataUpdates()
    {
        return agentDataUpdates;
    }

    public AssetResponseCacher getAssetResponseCacher()
    {
        return assetResponseCacher;
    }

    public SerializableResponseCacher getAvatarGroupLists()
    {
        return avatarGroupLists;
    }

    public SLMessageResponseCacher getAvatarNotes()
    {
        return avatarNotes;
    }

    public SLMessageResponseCacher getAvatarPickInfos()
    {
        return avatarPickInfos;
    }

    public SLMessageResponseCacher getAvatarPicks()
    {
        return avatarPicks;
    }

    public SLMessageResponseCacher getAvatarProperties()
    {
        return avatarProperties;
    }

    public BalanceManager getBalanceManager()
    {
        return balanceManager;
    }

    public SLMessageResponseCacher getCachedGroupProfiles()
    {
        return groupProfiles;
    }

    public ChatMessage getChatMessage(long l)
    {
        Query query = loadMessageQuery.forCurrentThread();
        query.setParameter(0, Long.valueOf(l));
        return (ChatMessage)query.unique();
    }

    public ChatMessageDao getChatMessageDao()
    {
        return chatMessageDao;
    }

    public Chatter getChatter(Cursor cursor)
    {
        return chatterDao.readEntity(cursor, 0);
    }

    public ChatterDao getChatterDao()
    {
        return chatterDao;
    }

    public ChatterList getChatterList()
    {
        return chatterList;
    }

    public Subscribable getCurrentLocationInfo()
    {
        return currentLocationInfoPool;
    }

    public CurrentLocationInfo getCurrentLocationInfoSnapshot()
    {
        return (CurrentLocationInfo)currentLocationInfoPool.getData();
    }

    public DaoSession getDaoSession()
    {
        return daoSession;
    }

    public Executor getDatabaseExecutor()
    {
        return dbExecutor;
    }

    public Executor getDatabaseRunOnceExecutor()
    {
        return dbExecutor.getRunOnceExecutor();
    }

    public EventBus getEventBus()
    {
        return eventBus;
    }

    public SLMessageResponseCacher getGroupRoles()
    {
        return groupRoles;
    }

    public SLMessageResponseCacher getGroupTitles()
    {
        return groupTitles;
    }

    public InventoryManager getInventoryManager()
    {
        return inventoryManager;
    }

    public SubscriptionSingleDataPool getMinimapBitmapPool()
    {
        return minimapBitmapPool;
    }

    public ObjectPopupsManager getObjectPopupsManager()
    {
        return objectPopupsManager;
    }

    public ObjectsManager getObjectsManager()
    {
        return objectsManager;
    }

    public SearchManager getSearchManager()
    {
        return searchManager;
    }

    public SyncManager getSyncManager()
    {
        return syncManager;
    }

    public UnreadNotificationManager getUnreadNotificationManager()
    {
        return notificationManager;
    }

    public User getUser(Cursor cursor)
    {
        return userDao.readEntity(cursor, 0);
    }

    public User getUser(UUID uuid, String s, String s1)
    {
        User user;
        User user1;
        Query query;
        query = findUserQuery.forCurrentThread();
        query.setParameter(0, uuid.toString());
        user1 = (User)query.unique();
        user = user1;
        if (user1 != null) goto _L2; else goto _L1
_L1:
        Object obj = userUpdateLock;
        obj;
        JVM INSTR monitorenter ;
        user1 = (User)query.unique();
        user = user1;
        if (user1 != null)
        {
            break MISSING_BLOCK_LABEL_112;
        }
        user = new User(null);
        user.setUuid(uuid);
        if (s == null)
        {
            break MISSING_BLOCK_LABEL_92;
        }
        user.setUserName(s);
        if (s1 == null)
        {
            break MISSING_BLOCK_LABEL_102;
        }
        user.setDisplayName(s1);
        userDao.insert(user);
        obj;
        JVM INSTR monitorexit ;
_L2:
        return user;
        uuid;
        throw uuid;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public UUID getUserID()
    {
        return userID;
    }

    public SubscriptionPool getUserLocationsPool()
    {
        return userLocationsPool;
    }

    public RequestQueue getUserNameRequestQueue()
    {
        return userNamesHandler;
    }

    public WeakPriorityRequestSet getUserNameRequests()
    {
        return userNameRequests;
    }

    public Subscribable getUserNames()
    {
        return userNamesPool;
    }

    public byte[] getUserPic(UUID uuid)
    {
        if (uuid == null)
        {
            return null;
        }
        Query query = findUserPicQuery.forCurrentThread();
        query.setParameter(0, uuid.toString());
        uuid = (UserPic)query.unique();
        if (uuid == null)
        {
            return null;
        } else
        {
            return uuid.getBitmap();
        }
    }

    public UserPicBitmapCache getUserPicBitmapCache()
    {
        return userPicBitmapCache;
    }

    public Subscribable getVoiceActiveChatter()
    {
        return voiceActiveChatterPool;
    }

    public Subscribable getVoiceAudioProperties()
    {
        return voiceAudioPropertiesPool;
    }

    public Subscribable getVoiceChatInfo()
    {
        return voiceChatInfoPool;
    }

    public Subscribable getVoiceLoggedIn()
    {
        return voiceLoggedInPool;
    }

    public SubscriptionSingleDataPool getWornAttachmentsPool()
    {
        return wornAttachmentsPool;
    }

    public SubscriptionSingleDataPool getWornWearablesPool()
    {
        return wornWearablesPool;
    }

    public boolean isChatterActive(ChatterID chatterid)
    {
        if (chatterid.getChatterType() == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Local)
        {
            return true;
        }
        Object obj = chatterUpdateLock;
        obj;
        JVM INSTR monitorenter ;
        Query query = findChatterQuery.forCurrentThread();
        query.setParameter(0, Integer.valueOf(chatterid.getChatterType().ordinal()));
        query.setParameter(1, StringUtils.toString(chatterid.getOptionalChatterUUID()));
        chatterid = (Chatter)query.unique();
        if (chatterid != null)
        {
            break MISSING_BLOCK_LABEL_70;
        }
        obj;
        JVM INSTR monitorexit ;
        return false;
        boolean flag = chatterid.getActive();
        obj;
        JVM INSTR monitorexit ;
        return flag;
        chatterid;
        throw chatterid;
    }

    public boolean isChatterMuted(ChatterID chatterid)
    {
        if (chatterid.getChatterType() == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Local)
        {
            return false;
        }
        Object obj = chatterUpdateLock;
        obj;
        JVM INSTR monitorenter ;
        Query query = findChatterQuery.forCurrentThread();
        query.setParameter(0, Integer.valueOf(chatterid.getChatterType().ordinal()));
        query.setParameter(1, StringUtils.toString(chatterid.getOptionalChatterUUID()));
        chatterid = (Chatter)query.unique();
        if (chatterid != null)
        {
            break MISSING_BLOCK_LABEL_70;
        }
        obj;
        JVM INSTR monitorexit ;
        return false;
        boolean flag = chatterid.getMuted();
        obj;
        JVM INSTR monitorexit ;
        return flag;
        chatterid;
        throw chatterid;
    }

    public SubscriptionPool muteListPool()
    {
        return muteListPool;
    }

    public SLMessageResponseCacher parcelInfoData()
    {
        return parcelInfoData;
    }

    public void setActiveAgentCircuit(SLAgentCircuit slagentcircuit)
    {
        activeAgentCircuit.set(slagentcircuit);
        if (slagentcircuit == null)
        {
            objectPopupsManager.clearObjectPopups();
        }
        objectsManager.requestObjectListUpdate();
        activeAgentCircuitsPool.setData(userID, slagentcircuit);
    }

    public void setChatterMuted(ChatterID chatterid, boolean flag)
    {
        if (chatterid.getChatterType() == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Local)
        {
            return;
        }
        Object obj = chatterUpdateLock;
        obj;
        JVM INSTR monitorenter ;
        Object obj1;
        obj1 = findChatterQuery.forCurrentThread();
        ((Query) (obj1)).setParameter(0, Integer.valueOf(chatterid.getChatterType().ordinal()));
        ((Query) (obj1)).setParameter(1, StringUtils.toString(chatterid.getOptionalChatterUUID()));
        obj1 = (Chatter)((Query) (obj1)).unique();
        if (obj1 == null) goto _L2; else goto _L1
_L1:
        if (((Chatter) (obj1)).getMuted() == flag) goto _L4; else goto _L3
_L3:
        ((Chatter) (obj1)).setMuted(flag);
        if (flag) goto _L6; else goto _L5
_L5:
        if (!(((Chatter) (obj1)).getActive() ^ true)) goto _L6; else goto _L7
_L7:
        chatterDao.delete(obj1);
_L4:
        obj;
        JVM INSTR monitorexit ;
        return;
_L6:
        chatterDao.update(obj1);
          goto _L4
        chatterid;
        throw chatterid;
_L2:
        if (!flag) goto _L4; else goto _L8
_L8:
        chatterid = new Chatter(null, chatterid.getChatterType().ordinal(), chatterid.getOptionalChatterUUID(), false, true, 0, null, null);
        chatterDao.insert(chatterid);
          goto _L4
    }

    public void setCurrentLocationInfo(CurrentLocationInfo currentlocationinfo)
    {
        currentLocationInfoPool.setData(currentLocationInfoPool.getKey(), currentlocationinfo);
    }

    public void setUserBadUUID(UUID uuid)
    {
        updateUserNames(uuid, null, null, true);
    }

    public void setUserPic(UUID uuid, byte abyte0[])
    {
        if (uuid == null) goto _L2; else goto _L1
_L1:
        Object obj;
        obj = findUserPicQuery.forCurrentThread();
        ((Query) (obj)).setParameter(0, uuid.toString());
        Object obj1 = userPicUpdateLock;
        obj1;
        JVM INSTR monitorenter ;
        UserPic userpic = (UserPic)((Query) (obj)).unique();
        obj = userpic;
        if (userpic != null)
        {
            break MISSING_BLOCK_LABEL_64;
        }
        obj = new UserPic(null);
        ((UserPic) (obj)).setUuid(uuid.toString());
        ((UserPic) (obj)).setBitmap(abyte0);
        userPicDao.insertOrReplace(obj);
        obj1;
        JVM INSTR monitorexit ;
_L2:
        return;
        uuid;
        throw uuid;
    }

    public void setVoiceActiveChatter(ChatterID chatterid)
    {
        voiceActiveChatterPool.setData(SubscriptionSingleKey.Value, chatterid);
    }

    public void setVoiceAudioProperties(VoiceAudioProperties voiceaudioproperties)
    {
        voiceAudioPropertiesPool.setData(SubscriptionSingleKey.Value, voiceaudioproperties);
    }

    public void setVoiceChatInfo(ChatterID chatterid, VoiceChatInfo voicechatinfo)
    {
        voiceChatInfoPool.setData(chatterid, voicechatinfo);
    }

    public void setVoiceLoggedIn(boolean flag)
    {
        voiceLoggedInPool.setData(SubscriptionSingleKey.Value, Boolean.valueOf(flag));
    }

    public void updateUserNames(UUID uuid, String s, String s1)
    {
        updateUserNames(uuid, s, s1, false);
    }

    public void updateUserNames(UUID uuid, String s, String s1, boolean flag)
    {
        Object obj1;
        obj1 = findUserQuery.forCurrentThread();
        ((Query) (obj1)).setParameter(0, uuid.toString());
        Object obj = userUpdateLock;
        obj;
        JVM INSTR monitorenter ;
        obj1 = (User)((Query) (obj1)).unique();
        if (obj1 == null) goto _L2; else goto _L1
_L1:
        if (((User) (obj1)).getUserName() != null || s == null)
        {
            break MISSING_BLOCK_LABEL_61;
        }
        ((User) (obj1)).setUserName(s);
        if (((User) (obj1)).getDisplayName() != null || s1 == null)
        {
            break MISSING_BLOCK_LABEL_79;
        }
        ((User) (obj1)).setDisplayName(s1);
        ((User) (obj1)).setBadUUID(flag);
        userDao.update(obj1);
_L4:
        obj;
        JVM INSTR monitorexit ;
        eventBus.publish(new EventUserInfoChanged(userID, uuid, 2));
        return;
_L2:
        obj1 = new User(null);
        ((User) (obj1)).setUuid(uuid);
        if (s == null)
        {
            break MISSING_BLOCK_LABEL_145;
        }
        ((User) (obj1)).setUserName(s);
        if (s1 == null)
        {
            break MISSING_BLOCK_LABEL_155;
        }
        ((User) (obj1)).setDisplayName(s1);
        ((User) (obj1)).setBadUUID(flag);
        userDao.insert(obj1);
        if (true) goto _L4; else goto _L3
_L3:
        uuid;
        throw uuid;
    }

    public SubscriptionPool wornItems()
    {
        return wornItemsPool;
    }

    public SubscriptionSingleDataPool wornOutfitLink()
    {
        return wornOutfitLinkPool;
    }

}
