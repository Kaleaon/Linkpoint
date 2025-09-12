// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.dao.ChatMessageDao;
import com.lumiyaviewer.lumiya.dao.Chatter;
import com.lumiyaviewer.lumiya.dao.ChatterDao;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.dao.UserName;
import com.lumiyaviewer.lumiya.dao.UserNameDao;
import com.lumiyaviewer.lumiya.react.RequestFinalProcessor;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatSessionMarkEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.OnChatEventListener;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUser;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UserManager, MessageSourceNameResolver, UnreadNotificationManager, ChatMessageLoader, 
//            ObjectPopupsManager, SyncManager, ChatterListType, ChatterList, 
//            ActiveChattersDisplayDataList, OnListUpdated, ChatterDisplayDataList, UnreadMessageInfo

public class ActiveChattersManager
    implements MessageSourceNameResolver.OnMessageSourcesResolvedListener
{
    public static class ChatMessageEvent
    {

        public final ChatMessage chatMessage;
        public final boolean isNewMessage;
        public final boolean isPrivate;

        ChatMessageEvent(ChatMessage chatmessage, boolean flag, boolean flag1)
        {
            chatMessage = chatmessage;
            isNewMessage = flag;
            isPrivate = flag1;
        }
    }


    private static final int CHAT_LOG_CHUNK_SIZE = 100;
    private final EventBus chatEventBus = new EventBus();
    private final Object chatEventLock = new Object();
    private final ChatMessageDao chatMessageDao;
    private final ChatterDao chatterDao;
    private final ChatterList chatterList;
    private final Set displayedChatters = Collections.synchronizedSet(new HashSet());
    private final Query findChatterQuery;
    private final Query findChatterQueryNullUUID;
    private final ChatterID localChatterID;
    private final Map messageLoaders = new HashMap();
    private final Object messageLoadersLock = new Object();
    private final MessageSourceNameResolver messageSourceNameResolver;
    private final Map objectMessageListeners = new WeakHashMap();
    private final Object objectMessageListenersLock = new Object();
    private final OnListUpdated onListUpdated = new OnListUpdated() {

        final ActiveChattersManager this$0;

        public void onListUpdated()
        {
            ActiveChattersManager._2D_get1(ActiveChattersManager.this).notifyListUpdated(ChatterListType.Active);
        }

            
            {
                this$0 = ActiveChattersManager.this;
                super();
            }
    };
    private final SubscriptionPool unreadCountsPool = new SubscriptionPool();
    private final UserManager userManager;

    static ChatMessageDao _2D_get0(ActiveChattersManager activechattersmanager)
    {
        return activechattersmanager.chatMessageDao;
    }

    static ChatterList _2D_get1(ActiveChattersManager activechattersmanager)
    {
        return activechattersmanager.chatterList;
    }

    ActiveChattersManager(UserManager usermanager, DaoSession daosession, ChatterList chatterlist)
    {
        userManager = usermanager;
        chatterList = chatterlist;
        chatterDao = daosession.getChatterDao();
        chatMessageDao = daosession.getChatMessageDao();
        findChatterQuery = chatterDao.queryBuilder().where(com.lumiyaviewer.lumiya.dao.ChatterDao.Properties.Type.eq(null), new WhereCondition[] {
            com.lumiyaviewer.lumiya.dao.ChatterDao.Properties.Uuid.eq("")
        }).build();
        findChatterQueryNullUUID = chatterDao.queryBuilder().where(com.lumiyaviewer.lumiya.dao.ChatterDao.Properties.Type.eq(null), new WhereCondition[] {
            com.lumiyaviewer.lumiya.dao.ChatterDao.Properties.Uuid.isNull()
        }).build();
        localChatterID = ChatterID.getLocalChatterID(usermanager.getUserID());
        messageSourceNameResolver = new MessageSourceNameResolver(usermanager, this);
        new RequestFinalProcessor(usermanager.getDatabaseExecutor(), usermanager) {

            final ActiveChattersManager this$0;
            final UserManager val$userManager;

            protected UnreadMessageInfo processRequest(ChatterID chatterid)
                throws Throwable
            {
                Chatter chatter = getChatter(chatterid);
                if (chatter == null) goto _L2; else goto _L1
_L1:
                if (chatter.getLastMessageID() == null) goto _L4; else goto _L3
_L3:
                chatterid = (ChatMessage)ActiveChattersManager._2D_get0(ActiveChattersManager.this).load(chatter.getLastMessageID());
                if (chatterid == null) goto _L4; else goto _L5
_L5:
                chatterid = SLChatEvent.loadFromDatabaseObject(chatterid, userManager.getUserID());
_L7:
                return UnreadMessageInfo.create(chatter.getUnreadCount(), chatterid);
_L2:
                return UnreadMessageInfo.create(0, null);
_L4:
                chatterid = null;
                if (true) goto _L7; else goto _L6
_L6:
            }

            protected volatile Object processRequest(Object obj)
                throws Throwable
            {
                return processRequest((ChatterID)obj);
            }

            
            {
                this$0 = ActiveChattersManager.this;
                userManager = usermanager;
                super(final_requestsource, executor);
            }
        };
    }

    private void clearChatHistoryInternal(ChatterID chatterid)
    {
        Object obj = chatEventLock;
        obj;
        JVM INSTR monitorenter ;
        Chatter chatter = getChatter(chatterid);
        if (chatter == null)
        {
            break MISSING_BLOCK_LABEL_47;
        }
        chatMessageDao.queryBuilder().where(com.lumiyaviewer.lumiya.dao.ChatMessageDao.Properties.ChatterID.eq(chatter.getId()), new WhereCondition[0]).buildDelete().executeDeleteWithoutDetachingEntities();
        obj;
        JVM INSTR monitorexit ;
        if (chatter != null)
        {
            userManager.getUnreadNotificationManager().clearFreshMessages(chatter);
        }
        chatterid = getLoaders(chatterid);
        if (chatterid != null)
        {
            for (chatterid = chatterid.iterator(); chatterid.hasNext(); ((ChatMessageLoader)chatterid.next()).reload()) { }
        }
        break MISSING_BLOCK_LABEL_110;
        chatterid;
        throw chatterid;
        userManager.getUnreadNotificationManager().updateUnreadNotifications();
        return;
    }

    private void clearUnreadCount(ChatterID chatterid)
    {
        Chatter chatter;
        boolean flag1;
        chatter = null;
        flag1 = false;
        Object obj = chatEventLock;
        obj;
        JVM INSTR monitorenter ;
        boolean flag = flag1;
        Chatter chatter1;
        if (!displayedChatters.contains(chatterid))
        {
            break MISSING_BLOCK_LABEL_78;
        }
        chatter1 = getChatter(chatterid);
        chatter = chatter1;
        flag = flag1;
        if (chatter1 == null)
        {
            break MISSING_BLOCK_LABEL_78;
        }
        chatter = chatter1;
        flag = flag1;
        if (chatter1.getUnreadCount() == 0)
        {
            break MISSING_BLOCK_LABEL_78;
        }
        flag = true;
        chatter1.setUnreadCount(0);
        chatterDao.update(chatter1);
        chatter = chatter1;
        obj;
        JVM INSTR monitorexit ;
        if (flag)
        {
            userManager.getUnreadNotificationManager().clearFreshMessages(chatter);
            unreadCountsPool.requestUpdate(chatterid);
            userManager.getUnreadNotificationManager().updateUnreadNotifications();
        }
        return;
        chatterid;
        throw chatterid;
    }

    private List getLoaders(ChatterID chatterid)
    {
        Object obj;
        ChatMessageLoader chatmessageloader;
        obj = null;
        chatmessageloader = null;
        Object obj1 = messageLoadersLock;
        obj1;
        JVM INSTR monitorenter ;
        chatterid = (List)messageLoaders.get(chatterid);
        if (chatterid == null) goto _L2; else goto _L1
_L1:
        Iterator iterator = chatterid.iterator();
        chatterid = chatmessageloader;
_L3:
        obj = chatterid;
        if (!iterator.hasNext())
        {
            break; /* Loop/switch isn't completed */
        }
        chatmessageloader = (ChatMessageLoader)((WeakReference)iterator.next()).get();
        if (chatmessageloader != null)
        {
            break MISSING_BLOCK_LABEL_84;
        }
        iterator.remove();
        continue; /* Loop/switch isn't completed */
        obj = chatterid;
        if (chatterid != null)
        {
            break MISSING_BLOCK_LABEL_98;
        }
        obj = new LinkedList();
        ((List) (obj)).add(chatmessageloader);
        chatterid = ((ChatterID) (obj));
        if (true) goto _L3; else goto _L2
_L2:
        obj1;
        JVM INSTR monitorexit ;
        return ((List) (obj));
        chatterid;
        throw chatterid;
    }

    private void handleChatEventInternal(ChatterID chatterid, SLChatEvent slchatevent, boolean flag)
    {
        ChatterID chatterid1 = null;
        if (!slchatevent.isObjectPopup()) goto _L2; else goto _L1
_L1:
        userManager.getObjectPopupsManager().addObjectPopup(slchatevent);
_L23:
        userManager.getSyncManager().syncNewMessages();
        userManager.getUnreadNotificationManager().updateUnreadNotifications();
        return;
_L2:
        Object obj1 = slchatevent.getSource();
        if (((ChatMessageSource) (obj1)).getSourceType() != com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType.Object) goto _L4; else goto _L3
_L3:
        Object obj2 = objectMessageListenersLock;
        obj2;
        JVM INSTR monitorenter ;
        if (objectMessageListeners.isEmpty()) goto _L6; else goto _L5
_L5:
        Object obj = ImmutableList.copyOf(objectMessageListeners.entrySet());
_L7:
        obj2;
        JVM INSTR monitorexit ;
        if (obj != null)
        {
            for (obj = ((Iterable) (obj)).iterator(); ((Iterator) (obj)).hasNext();)
            {
                Object obj3 = (java.util.Map.Entry)((Iterator) (obj)).next();
                obj2 = (OnChatEventListener)((java.util.Map.Entry) (obj3)).getKey();
                obj3 = (Executor)((java.util.Map.Entry) (obj3)).getValue();
                if (obj3 != null)
                {
                    ((Executor) (obj3)).execute(new _2D_.Lambda.bC26PUjVA14BMgZPIZxiNFWFltI._cls2(obj2, slchatevent));
                } else
                {
                    ((OnChatEventListener) (obj2)).onChatEvent(slchatevent);
                }
            }

        }
        break; /* Loop/switch isn't completed */
_L6:
        obj = null;
        if (true) goto _L7; else goto _L4
        chatterid;
        throw chatterid;
_L4:
        Object obj4;
        boolean flag1;
        boolean flag2;
        obj = userManager.getActiveAgentCircuit();
        UUID uuid;
        ChatMessageSourceUser chatmessagesourceuser;
        boolean flag3;
        if (obj != null)
        {
            uuid = ((SLAgentCircuit) (obj)).getSessionID();
        } else
        {
            uuid = null;
        }
        obj4 = chatEventLock;
        obj4;
        JVM INSTR monitorenter ;
        if (((ChatMessageSource) (obj1)).getSourceType() != com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType.User) goto _L9; else goto _L8
_L8:
        if (!(obj1 instanceof ChatMessageSourceUser)) goto _L11; else goto _L10
_L10:
        obj = (UserName)userManager.getDaoSession().getUserNameDao().load(((ChatMessageSource) (obj1)).getSourceUUID());
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_808;
        }
        chatmessagesourceuser = (ChatMessageSourceUser)obj1;
        if (((UserName) (obj)).getDisplayName() != null)
        {
            chatmessagesourceuser.setDisplayName(((UserName) (obj)).getDisplayName());
        }
        if (((UserName) (obj)).getUserName() != null)
        {
            chatmessagesourceuser.setLegacyName(((UserName) (obj)).getUserName());
        }
        if (!((UserName) (obj)).isComplete())
        {
            break MISSING_BLOCK_LABEL_808;
        }
        flag1 = true;
_L28:
        if (flag1) goto _L11; else goto _L12
_L12:
        obj1 = ((ChatMessageSource) (obj1)).getSourceUUID();
_L24:
        if (slchatevent.opensNewChatter() || chatterid.getChatterType() == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Local) goto _L14; else goto _L13
_L13:
        obj = getChatter(chatterid);
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_389;
        }
        if (!((Chatter) (obj)).getActive())
        {
            obj = chatterid1;
        }
        if (obj != null) goto _L16; else goto _L15
_L15:
        chatterid1 = localChatterID;
_L27:
        flag3 = displayedChatters.contains(chatterid1);
        chatterid = ((ChatterID) (obj));
        if (obj != null)
        {
            break MISSING_BLOCK_LABEL_461;
        }
        obj = getChatter(chatterid1);
        chatterid = ((ChatterID) (obj));
        if (obj != null)
        {
            break MISSING_BLOCK_LABEL_461;
        }
        chatterid = new Chatter(null);
        chatterid1.toDatabaseObject(chatterid);
        chatterDao.insert(chatterid);
        if (uuid == null)
        {
            break MISSING_BLOCK_LABEL_506;
        }
        if (Objects.equal(uuid, chatterid.getLastSessionID()) ^ true)
        {
            if (chatterid.getLastSessionID() != null)
            {
                makeSessionMark(chatterid1, chatterid.getId().longValue());
            }
            chatterid.setLastSessionID(uuid);
        }
        slchatevent = slchatevent.getDatabaseObject();
        slchatevent.setChatterID(chatterid.getId().longValue());
        chatMessageDao.insert(slchatevent);
        if (chatterid.getActive()) goto _L18; else goto _L17
_L17:
        if (!(chatterid.getMuted() ^ true)) goto _L20; else goto _L19
_L19:
        chatterid.setActive(true);
        flag1 = true;
_L25:
        if (!flag || flag3) goto _L22; else goto _L21
_L21:
        chatterid.setUnreadCount(chatterid.getUnreadCount() + 1);
        flag2 = true;
_L26:
        chatterid.setLastMessageID(slchatevent.getId());
        chatterDao.update(chatterid);
        obj4;
        JVM INSTR monitorexit ;
        if (!chatterid.getMuted() && flag2)
        {
            userManager.getUnreadNotificationManager().addFreshMessage(chatterid);
            obj = chatEventBus;
            if (chatterid.getType() == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.User.ordinal())
            {
                flag = true;
            } else
            {
                flag = false;
            }
            ((EventBus) (obj)).post(new ChatMessageEvent(slchatevent, true, flag));
        }
        if (obj1 != null)
        {
            messageSourceNameResolver.requestResolve(((UUID) (obj1)), slchatevent.getId());
        }
        unreadCountsPool.requestUpdate(chatterid1);
        if (flag1)
        {
            chatterList.updateList(ChatterListType.Active);
        }
        chatterid = getLoaders(chatterid1);
        if (chatterid != null)
        {
            chatterid = chatterid.iterator();
            while (chatterid.hasNext()) 
            {
                ((ChatMessageLoader)chatterid.next()).addElement(slchatevent);
            }
        }
          goto _L23
_L9:
        obj1 = null;
          goto _L24
_L18:
        flag1 = false;
          goto _L25
        chatterid;
        throw chatterid;
_L22:
        flag2 = false;
          goto _L26
_L20:
        flag1 = false;
          goto _L25
_L16:
        chatterid1 = chatterid;
          goto _L27
_L14:
        obj = null;
        chatterid1 = chatterid;
          goto _L27
_L11:
        obj1 = null;
          goto _L24
        flag1 = false;
          goto _L28
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_16177(OnChatEventListener onchateventlistener, SLChatEvent slchatevent)
    {
        onchateventlistener.onChatEvent(slchatevent);
    }

    private void makeSessionMark(ChatterID chatterid, long l)
    {
        boolean flag = false;
        Object obj1 = chatEventLock;
        obj1;
        JVM INSTR monitorenter ;
        Object obj = chatMessageDao.queryBuilder().where(com.lumiyaviewer.lumiya.dao.ChatMessageDao.Properties.ChatterID.eq(Long.valueOf(l)), new WhereCondition[0]).orderDesc(new Property[] {
            com.lumiyaviewer.lumiya.dao.ChatMessageDao.Properties.Id
        }).limit(1).list();
        if (obj == null) goto _L2; else goto _L1
_L1:
        if (((List) (obj)).size() <= 0) goto _L2; else goto _L3
_L3:
        obj = (ChatMessage)((List) (obj)).get(0);
        if (obj == null) goto _L5; else goto _L4
_L4:
        if (((ChatMessage) (obj)).getMessageType() != com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType.SessionMark.ordinal()) goto _L5; else goto _L6
_L6:
        if (!Objects.equal(((ChatMessage) (obj)).getChatChannel(), Integer.valueOf(com.lumiyaviewer.lumiya.slproto.chat.SLChatSessionMarkEvent.SessionMarkType.NewSession.ordinal()))) goto _L8; else goto _L7
_L7:
        ((ChatMessage) (obj)).setTimestamp(new Date());
        chatMessageDao.update(obj);
        flag = true;
_L11:
        if (obj != null)
        {
            break MISSING_BLOCK_LABEL_196;
        }
        obj = (new SLChatSessionMarkEvent(ChatMessageSourceUnknown.getInstance(), userManager.getUserID(), com.lumiyaviewer.lumiya.slproto.chat.SLChatSessionMarkEvent.SessionMarkType.NewSession, null)).getDatabaseObject();
        ((ChatMessage) (obj)).setChatterID(l);
        chatMessageDao.insert(obj);
        Debug.Printf("markNewSession: added session mark, chatterDbID %d", new Object[] {
            Long.valueOf(l)
        });
        obj1;
        JVM INSTR monitorexit ;
          goto _L9
_L2:
        obj = null;
        continue; /* Loop/switch isn't completed */
_L5:
        obj = null;
        continue; /* Loop/switch isn't completed */
        chatterid;
        throw chatterid;
_L9:
        chatterid = getLoaders(chatterid);
        if (chatterid != null)
        {
            for (chatterid = chatterid.iterator(); chatterid.hasNext();)
            {
                obj1 = (ChatMessageLoader)chatterid.next();
                if (flag)
                {
                    ((ChatMessageLoader) (obj1)).updateElement(((com.lumiyaviewer.lumiya.utils.Identifiable) (obj)));
                } else
                {
                    ((ChatMessageLoader) (obj1)).addElement(((com.lumiyaviewer.lumiya.utils.Identifiable) (obj)));
                }
            }

        }
        return;
_L8:
        obj = null;
        if (true) goto _L11; else goto _L10
_L10:
    }

    private void markChatterInactiveInternal(ChatterID chatterid, boolean flag)
    {
        boolean flag1;
        boolean flag2;
        boolean flag3;
        flag2 = false;
        flag1 = false;
        flag3 = true;
        if (chatterid == null || chatterid.getChatterType() == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Local) goto _L2; else goto _L1
_L1:
        Object obj = chatEventLock;
        obj;
        JVM INSTR monitorenter ;
        Chatter chatter = getChatter(chatterid);
        if (chatter == null)
        {
            break MISSING_BLOCK_LABEL_194;
        }
        if (!chatter.getActive()) goto _L4; else goto _L3
_L3:
        chatter.setActive(false);
        if (!flag)
        {
            break MISSING_BLOCK_LABEL_76;
        }
        if (chatter.getMuted() ^ true)
        {
            chatter.setMuted(true);
        }
        flag2 = flag3;
        if (chatter.getLastMessageID() == null)
        {
            break MISSING_BLOCK_LABEL_101;
        }
        chatter.setLastMessageID(null);
        flag1 = true;
        flag2 = flag3;
_L5:
        boolean flag4;
        flag3 = flag1;
        flag4 = flag2;
        if (!flag2)
        {
            break MISSING_BLOCK_LABEL_131;
        }
        chatterDao.update(chatter);
        flag4 = flag2;
        flag3 = flag1;
_L6:
        obj;
        JVM INSTR monitorexit ;
        if (flag4)
        {
            chatterList.updateList(ChatterListType.Active);
        }
        if (flag3)
        {
            unreadCountsPool.requestUpdate(chatterid);
        }
        userManager.getUnreadNotificationManager().updateUnreadNotifications();
        userManager.getSyncManager().flushChatter(chatterid);
_L2:
        return;
        chatterid;
        throw chatterid;
_L4:
        flag2 = false;
          goto _L5
        flag4 = false;
        flag3 = flag2;
          goto _L6
    }

    private void notifyChatEventUpdatedInternal(SLChatEvent slchatevent)
    {
        Object obj = chatEventLock;
        obj;
        JVM INSTR monitorenter ;
        slchatevent = slchatevent.getDatabaseObject();
        chatMessageDao.update(slchatevent);
        obj;
        JVM INSTR monitorexit ;
        onMessageUpdated(slchatevent);
        userManager.getUnreadNotificationManager().updateUnreadNotifications();
        return;
        slchatevent;
        throw slchatevent;
    }

    private void notifyTeleportCompleteInternal(String s)
    {
        Chatter chatter;
        chatter = getChatter(localChatterID);
        if (chatter == null)
        {
            break MISSING_BLOCK_LABEL_117;
        }
        s = (new SLChatSessionMarkEvent(ChatMessageSourceUnknown.getInstance(), userManager.getUserID(), com.lumiyaviewer.lumiya.slproto.chat.SLChatSessionMarkEvent.SessionMarkType.Teleport, s)).getDatabaseObject();
        Object obj1 = chatEventLock;
        obj1;
        JVM INSTR monitorenter ;
        s.setChatterID(chatter.getId().longValue());
        chatMessageDao.insert(s);
        obj1;
        JVM INSTR monitorexit ;
        Object obj = getLoaders(localChatterID);
        if (obj != null)
        {
            for (obj = ((Iterable) (obj)).iterator(); ((Iterator) (obj)).hasNext(); ((ChatMessageLoader)((Iterator) (obj)).next()).addElement(s)) { }
        }
        break MISSING_BLOCK_LABEL_117;
        s;
        throw s;
    }

    private void onMessageUpdated(ChatMessage chatmessage)
    {
        Object obj = (Chatter)chatterDao.load(Long.valueOf(chatmessage.getChatterID()));
        ChatterID chatterid;
        if (obj != null)
        {
            chatterid = ChatterID.fromDatabaseObject(userManager.getUserID(), ((Chatter) (obj)));
        } else
        {
            chatterid = null;
        }
        if (chatterid != null)
        {
            Object obj1 = getLoaders(chatterid);
            if (obj1 != null)
            {
                for (obj1 = ((Iterable) (obj1)).iterator(); ((Iterator) (obj1)).hasNext(); ((ChatMessageLoader)((Iterator) (obj1)).next()).updateElement(chatmessage)) { }
            }
            if (Objects.equal(chatmessage.getId(), ((Chatter) (obj)).getLastMessageID()))
            {
                unreadCountsPool.requestUpdate(chatterid);
            }
            userManager.getUnreadNotificationManager().updateUnreadNotifications();
            obj = chatEventBus;
            boolean flag;
            if (chatterid.getChatterType() == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.User)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            ((EventBus) (obj)).post(new ChatMessageEvent(chatmessage, false, flag));
        }
    }

    public void HandleChatEvent(ChatterID chatterid, SLChatEvent slchatevent, boolean flag)
    {
        userManager.getDatabaseExecutor().execute(new _2D_.Lambda.bC26PUjVA14BMgZPIZxiNFWFltI._cls8(flag, this, chatterid, slchatevent));
    }

    public void addDisplayedChatter(ChatterID chatterid)
    {
        if (displayedChatters.add(chatterid))
        {
            userManager.getDatabaseExecutor().execute(new _2D_.Lambda.bC26PUjVA14BMgZPIZxiNFWFltI(this, chatterid));
        }
    }

    public void addObjectMessageListener(OnChatEventListener onchateventlistener, Executor executor)
    {
        Object obj = objectMessageListenersLock;
        obj;
        JVM INSTR monitorenter ;
        objectMessageListeners.put(onchateventlistener, executor);
        obj;
        JVM INSTR monitorexit ;
        return;
        onchateventlistener;
        throw onchateventlistener;
    }

    public void clearChatHistory(ChatterID chatterid)
    {
        userManager.getDatabaseExecutor().execute(new _2D_.Lambda.bC26PUjVA14BMgZPIZxiNFWFltI._cls1(this, chatterid));
    }

    ChatterDisplayDataList getActiveChattersList()
    {
        return new ActiveChattersDisplayDataList(userManager, onListUpdated);
    }

    public EventBus getChatEventBus()
    {
        return chatEventBus;
    }

    public ChatMessage getChatMessage(long l)
    {
        Object obj = chatEventLock;
        obj;
        JVM INSTR monitorenter ;
        ChatMessage chatmessage = (ChatMessage)chatMessageDao.load(Long.valueOf(l));
        obj;
        JVM INSTR monitorexit ;
        return chatmessage;
        Exception exception;
        exception;
        throw exception;
    }

    public Chatter getChatter(ChatterID chatterid)
    {
        return getChatter(chatterid, false);
    }

    Chatter getChatter(ChatterID chatterid, boolean flag)
    {
        Object obj1;
        Object obj3;
        boolean flag1;
        Object obj2 = null;
        Object obj = userManager.getActiveAgentCircuit();
        Object obj4;
        if (obj != null)
        {
            obj = ((SLAgentCircuit) (obj)).getSessionID();
        } else
        {
            obj = null;
        }
        obj3 = chatEventLock;
        obj3;
        JVM INSTR monitorenter ;
        obj4 = chatterid.getOptionalChatterUUID();
        if (obj4 == null) goto _L2; else goto _L1
_L1:
        obj1 = findChatterQuery.forCurrentThread();
        ((Query) (obj1)).setParameter(0, Integer.valueOf(chatterid.getChatterType().ordinal()));
        ((Query) (obj1)).setParameter(1, ((UUID) (obj4)).toString());
_L5:
        obj4 = (Chatter)((Query) (obj1)).unique();
        if (!flag)
        {
            break MISSING_BLOCK_LABEL_141;
        }
        boolean flag2;
        if (obj4 != null)
        {
            flag2 = true;
        } else
        {
            flag2 = false;
        }
        obj1 = obj2;
        if (obj4 == null)
        {
            break MISSING_BLOCK_LABEL_114;
        }
        obj1 = ((Chatter) (obj4)).getLastSessionID();
        Debug.Printf("markNewSession: result has %b, cur %s, last %s", new Object[] {
            Boolean.valueOf(flag2), obj, obj1
        });
        if (!flag || obj4 == null || obj == null) goto _L4; else goto _L3
_L3:
        if (!(Objects.equal(obj, ((Chatter) (obj4)).getLastSessionID()) ^ true))
        {
            break MISSING_BLOCK_LABEL_285;
        }
        if (((Chatter) (obj4)).getLastSessionID() != null)
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        ((Chatter) (obj4)).setLastSessionID(((UUID) (obj)));
        chatterDao.update(obj4);
_L6:
        obj3;
        JVM INSTR monitorexit ;
        if (flag1)
        {
            userManager.getDatabaseExecutor().execute(new _2D_.Lambda.bC26PUjVA14BMgZPIZxiNFWFltI._cls6(this, chatterid, obj4));
        }
        return ((Chatter) (obj4));
_L2:
        obj1 = findChatterQueryNullUUID.forCurrentThread();
        ((Query) (obj1)).setParameter(0, Integer.valueOf(chatterid.getChatterType().ordinal()));
          goto _L5
        chatterid;
        throw chatterid;
_L4:
        flag1 = false;
          goto _L6
        flag1 = false;
          goto _L6
    }

    public ChatMessageLoader getMessageLoader(ChatterID chatterid, com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader.EventListener eventlistener)
    {
        ChatMessageLoader chatmessageloader = new ChatMessageLoader(userManager, chatterid, 100, userManager.getDatabaseExecutor(), false, eventlistener);
        Object obj = messageLoadersLock;
        obj;
        JVM INSTR monitorenter ;
        eventlistener = (List)messageLoaders.get(chatterid);
        if (eventlistener != null) goto _L2; else goto _L1
_L1:
        eventlistener = new LinkedList();
        messageLoaders.put(chatterid, eventlistener);
        chatterid = eventlistener;
_L4:
        eventlistener = chatterid.iterator();
        do
        {
            if (!eventlistener.hasNext())
            {
                break;
            }
            if (((WeakReference)eventlistener.next()).get() == null)
            {
                eventlistener.remove();
            }
        } while (true);
        break MISSING_BLOCK_LABEL_117;
        chatterid;
        throw chatterid;
        chatterid.add(new WeakReference(chatmessageloader));
        obj;
        JVM INSTR monitorexit ;
        return chatmessageloader;
_L2:
        chatterid = eventlistener;
        if (true) goto _L4; else goto _L3
_L3:
    }

    public LazyList getMessages(ChatterID chatterid)
    {
        chatterid = getChatter(chatterid);
        if (chatterid == null)
        {
            return null;
        } else
        {
            return chatMessageDao.queryBuilder().where(com.lumiyaviewer.lumiya.dao.ChatMessageDao.Properties.ChatterID.eq(chatterid.getId()), new WhereCondition[0]).orderAsc(new Property[] {
                com.lumiyaviewer.lumiya.dao.ChatMessageDao.Properties.Id
            }).listLazy();
        }
    }

    public SubscriptionPool getUnreadCounts()
    {
        return unreadCountsPool;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_10179(ChatterID chatterid, Chatter chatter)
    {
        makeSessionMark(chatterid, chatter.getId().longValue());
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_13379(ChatterID chatterid, SLChatEvent slchatevent, boolean flag)
    {
        handleChatEventInternal(chatterid, slchatevent, flag);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_23064(ChatterID chatterid)
    {
        clearChatHistoryInternal(chatterid);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_25047(ChatterID chatterid, boolean flag)
    {
        markChatterInactiveInternal(chatterid, flag);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_25217(ChatterID chatterid)
    {
        boolean flag1 = false;
        if (chatterid == null || chatterid.getChatterType() == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Local) goto _L2; else goto _L1
_L1:
        Object obj = chatEventLock;
        obj;
        JVM INSTR monitorenter ;
        chatterid = getChatter(chatterid);
        boolean flag;
        flag = flag1;
        if (chatterid == null)
        {
            break MISSING_BLOCK_LABEL_54;
        }
        flag = flag1;
        if (!chatterid.getMuted())
        {
            break MISSING_BLOCK_LABEL_54;
        }
        chatterid.setMuted(false);
        flag = true;
        if (!flag)
        {
            break MISSING_BLOCK_LABEL_66;
        }
        chatterDao.update(chatterid);
        obj;
        JVM INSTR monitorexit ;
_L2:
        return;
        chatterid;
        throw chatterid;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_26357(SLChatEvent slchatevent)
    {
        notifyChatEventUpdatedInternal(slchatevent);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_26532(String s)
    {
        notifyTeleportCompleteInternal(s);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_5516(ChatterID chatterid)
    {
        clearUnreadCount(chatterid);
    }

    public void markChatterInactive(ChatterID chatterid, boolean flag)
    {
        userManager.getDatabaseExecutor().execute(new _2D_.Lambda.bC26PUjVA14BMgZPIZxiNFWFltI._cls7(flag, this, chatterid));
    }

    public void notifyChatEventUpdated(SLChatEvent slchatevent)
    {
        userManager.getDatabaseExecutor().execute(new _2D_.Lambda.bC26PUjVA14BMgZPIZxiNFWFltI._cls3(this, slchatevent));
    }

    public void notifyTeleportComplete(String s)
    {
        userManager.getDatabaseExecutor().execute(new _2D_.Lambda.bC26PUjVA14BMgZPIZxiNFWFltI._cls4(this, s));
    }

    public void onMessageSourcesResolved(Set set, UserName username)
    {
        set = set.iterator();
        do
        {
            if (!set.hasNext())
            {
                break;
            }
            Object obj = (Long)set.next();
            if (obj != null)
            {
                obj = (ChatMessage)chatMessageDao.load(obj);
                if (obj != null)
                {
                    ((ChatMessage) (obj)).setSenderName(username.getDisplayName());
                    ((ChatMessage) (obj)).setSenderLegacyName(username.getUserName());
                    chatMessageDao.update(obj);
                    onMessageUpdated(((ChatMessage) (obj)));
                }
            }
        } while (true);
    }

    public void releaseMessageLoader(ChatterID chatterid, ChatMessageLoader chatmessageloader)
    {
        Object obj = messageLoadersLock;
        obj;
        JVM INSTR monitorenter ;
        chatterid = (List)messageLoaders.get(chatterid);
        if (chatterid == null)
        {
            break MISSING_BLOCK_LABEL_83;
        }
        chatterid = chatterid.iterator();
_L2:
        ChatMessageLoader chatmessageloader1;
        do
        {
            if (!chatterid.hasNext())
            {
                break MISSING_BLOCK_LABEL_83;
            }
            chatmessageloader1 = (ChatMessageLoader)((WeakReference)chatterid.next()).get();
        } while (chatmessageloader1 != null && chatmessageloader1 != chatmessageloader);
        chatterid.remove();
        if (true) goto _L2; else goto _L1
_L1:
        chatterid;
        throw chatterid;
        obj;
        JVM INSTR monitorexit ;
    }

    public void removeDisplayedChatter(ChatterID chatterid)
    {
        displayedChatters.remove(chatterid);
    }

    public void removeObjectMessageListener(OnChatEventListener onchateventlistener)
    {
        Object obj = objectMessageListenersLock;
        obj;
        JVM INSTR monitorenter ;
        objectMessageListeners.remove(onchateventlistener);
        obj;
        JVM INSTR monitorexit ;
        return;
        onchateventlistener;
        throw onchateventlistener;
    }

    public void unmuteChatter(ChatterID chatterid)
    {
        userManager.getDatabaseExecutor().execute(new _2D_.Lambda.bC26PUjVA14BMgZPIZxiNFWFltI._cls5(this, chatterid));
    }
}
