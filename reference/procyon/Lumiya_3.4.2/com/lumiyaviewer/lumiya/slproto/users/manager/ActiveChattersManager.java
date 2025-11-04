// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.LazyList;
import com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown;
import java.util.Date;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatSessionMarkEvent;
import de.greenrobot.dao.Property;
import java.util.UUID;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.dao.UserName;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUser;
import java.util.Collection;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.RequestFinalProcessor;
import de.greenrobot.dao.query.WhereCondition;
import java.util.WeakHashMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.slproto.chat.generic.OnChatEventListener;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import com.lumiyaviewer.lumiya.dao.Chatter;
import de.greenrobot.dao.query.Query;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.Set;
import com.lumiyaviewer.lumiya.dao.ChatterDao;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.dao.ChatMessageDao;
import com.google.common.eventbus.EventBus;

public class ActiveChattersManager implements OnMessageSourcesResolvedListener
{
    private static final int CHAT_LOG_CHUNK_SIZE = 100;
    private final EventBus chatEventBus;
    private final Object chatEventLock;
    @Nonnull
    private final ChatMessageDao chatMessageDao;
    @Nonnull
    private final ChatterDao chatterDao;
    @Nonnull
    private final ChatterList chatterList;
    private final Set<ChatterID> displayedChatters;
    @Nonnull
    private final Query<Chatter> findChatterQuery;
    @Nonnull
    private final Query<Chatter> findChatterQueryNullUUID;
    @Nonnull
    private final ChatterID localChatterID;
    private final Map<ChatterID, List<WeakReference<ChatMessageLoader>>> messageLoaders;
    private final Object messageLoadersLock;
    private final MessageSourceNameResolver messageSourceNameResolver;
    private final Map<OnChatEventListener, Executor> objectMessageListeners;
    private final Object objectMessageListenersLock;
    private final OnListUpdated onListUpdated;
    private final SubscriptionPool<ChatterID, UnreadMessageInfo> unreadCountsPool;
    @Nonnull
    private final UserManager userManager;
    
    ActiveChattersManager(@Nonnull final UserManager userManager, @Nonnull final DaoSession daoSession, @Nonnull final ChatterList chatterList) {
        this.chatEventLock = new Object();
        this.messageLoadersLock = new Object();
        this.messageLoaders = new HashMap<ChatterID, List<WeakReference<ChatMessageLoader>>>();
        this.displayedChatters = Collections.synchronizedSet(new HashSet<ChatterID>());
        this.objectMessageListenersLock = new Object();
        this.objectMessageListeners = new WeakHashMap<OnChatEventListener, Executor>();
        this.chatEventBus = new EventBus();
        this.unreadCountsPool = new SubscriptionPool<ChatterID, UnreadMessageInfo>();
        this.onListUpdated = new OnListUpdated() {
            @Override
            public void onListUpdated() {
                ActiveChattersManager.this.chatterList.notifyListUpdated(ChatterListType.Active);
            }
        };
        this.userManager = userManager;
        this.chatterList = chatterList;
        this.chatterDao = daoSession.getChatterDao();
        this.chatMessageDao = daoSession.getChatMessageDao();
        this.findChatterQuery = ((AbstractDao<Chatter, K>)this.chatterDao).queryBuilder().where(ChatterDao.Properties.Type.eq(null), ChatterDao.Properties.Uuid.eq("")).build();
        this.findChatterQueryNullUUID = ((AbstractDao<Chatter, K>)this.chatterDao).queryBuilder().where(ChatterDao.Properties.Type.eq(null), ChatterDao.Properties.Uuid.isNull()).build();
        this.localChatterID = ChatterID.getLocalChatterID(userManager.getUserID());
        this.messageSourceNameResolver = new MessageSourceNameResolver(userManager, (MessageSourceNameResolver.OnMessageSourcesResolvedListener)this);
        new RequestFinalProcessor<ChatterID, UnreadMessageInfo>(this.unreadCountsPool, userManager.getDatabaseExecutor()) {
            @Override
            protected UnreadMessageInfo processRequest(@Nonnull final ChatterID chatterID) throws Throwable {
                final Chatter chatter = ActiveChattersManager.this.getChatter(chatterID);
                if (chatter != null) {
                    Label_0069: {
                        if (chatter.getLastMessageID() == null) {
                            break Label_0069;
                        }
                        final ChatMessage chatMessage = ActiveChattersManager.this.chatMessageDao.load(chatter.getLastMessageID());
                        if (chatMessage == null) {
                            break Label_0069;
                        }
                        final SLChatEvent loadFromDatabaseObject = SLChatEvent.loadFromDatabaseObject(chatMessage, userManager.getUserID());
                        return UnreadMessageInfo.create(chatter.getUnreadCount(), loadFromDatabaseObject);
                    }
                    final SLChatEvent loadFromDatabaseObject = null;
                    return UnreadMessageInfo.create(chatter.getUnreadCount(), loadFromDatabaseObject);
                }
                return UnreadMessageInfo.create(0, null);
            }
        };
    }
    
    private void clearChatHistoryInternal(@Nonnull final ChatterID chatterID) {
        synchronized (this.chatEventLock) {
            final Chatter chatter = this.getChatter(chatterID);
            if (chatter != null) {
                ((AbstractDao<ChatMessage, K>)this.chatMessageDao).queryBuilder().where(ChatMessageDao.Properties.ChatterID.eq(chatter.getId()), new WhereCondition[0]).buildDelete().executeDeleteWithoutDetachingEntities();
            }
            monitorexit(this.chatEventLock);
            if (chatter != null) {
                this.userManager.getUnreadNotificationManager().clearFreshMessages(chatter);
            }
            final List<ChatMessageLoader> loaders = this.getLoaders(chatterID);
            if (loaders != null) {
                final Iterator<Object> iterator = loaders.iterator();
                while (iterator.hasNext()) {
                    iterator.next().reload();
                }
            }
        }
        this.userManager.getUnreadNotificationManager().updateUnreadNotifications();
    }
    
    private void clearUnreadCount(@Nonnull final ChatterID chatterID) {
        Chatter chatter = null;
        final boolean b = false;
        final Object chatEventLock = this.chatEventLock;
        monitorenter(chatEventLock);
        int n = b ? 1 : 0;
        try {
            if (this.displayedChatters.contains(chatterID)) {
                final Chatter chatter2 = chatter = this.getChatter(chatterID);
                n = (b ? 1 : 0);
                if (chatter2 != null) {
                    chatter = chatter2;
                    n = (b ? 1 : 0);
                    if (chatter2.getUnreadCount() != 0) {
                        n = 1;
                        chatter2.setUnreadCount(0);
                        ((AbstractDao<Chatter, K>)this.chatterDao).update(chatter2);
                        chatter = chatter2;
                    }
                }
            }
            monitorexit(chatEventLock);
            if (n != 0) {
                this.userManager.getUnreadNotificationManager().clearFreshMessages(chatter);
                this.unreadCountsPool.requestUpdate(chatterID);
                this.userManager.getUnreadNotificationManager().updateUnreadNotifications();
            }
        }
        finally {
            monitorexit(chatEventLock);
        }
    }
    
    @Nullable
    private List<ChatMessageLoader> getLoaders(@Nonnull final ChatterID chatterID) {
        List<ChatMessageLoader> list = null;
        final List<ChatMessageLoader> list2 = null;
        synchronized (this.messageLoadersLock) {
            final List list3 = this.messageLoaders.get(chatterID);
            if (list3 != null) {
                final Iterator iterator = list3.iterator();
                List<ChatMessageLoader> list4 = list2;
                while (true) {
                    list = list4;
                    if (!iterator.hasNext()) {
                        break;
                    }
                    final ChatMessageLoader chatMessageLoader = ((WeakReference<ChatMessageLoader>)iterator.next()).get();
                    if (chatMessageLoader == null) {
                        iterator.remove();
                    }
                    else {
                        List<ChatMessageLoader> list5;
                        if ((list5 = list4) == null) {
                            list5 = new LinkedList<ChatMessageLoader>();
                        }
                        list5.add(chatMessageLoader);
                        list4 = list5;
                    }
                }
            }
            return list;
        }
    }
    
    private void handleChatEventInternal(ChatterID chatterID, SLChatEvent databaseObject, boolean b) {
        Object localChatterID = null;
        if (databaseObject.isObjectPopup()) {
            this.userManager.getObjectPopupsManager().addObjectPopup(databaseObject);
        }
        else {
            Object o = databaseObject.getSource();
            if (((ChatMessageSource)o).getSourceType() == ChatMessageSource.ChatMessageSourceType.Object) {
                while (true) {
                    Object lastSessionID = this.objectMessageListenersLock;
                    while (true) {
                        Object copy = null;
                        Label_0194: {
                            synchronized (lastSessionID) {
                                if (!this.objectMessageListeners.isEmpty()) {
                                    copy = ImmutableList.copyOf((Collection<?>)this.objectMessageListeners.entrySet());
                                }
                                else {
                                    copy = null;
                                }
                                monitorexit(lastSessionID);
                                if (copy != null) {
                                    lastSessionID = ((Iterable)copy).iterator();
                                    while (((Iterator)lastSessionID).hasNext()) {
                                        Object o2 = ((Iterator)lastSessionID).next();
                                        copy = ((Map.Entry)o2).getKey();
                                        o2 = ((Map.Entry)o2).getValue();
                                        if (o2 == null) {
                                            break Label_0194;
                                        }
                                        ((Executor)o2).execute(new _$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI$2(copy, databaseObject));
                                    }
                                    break;
                                }
                                break;
                            }
                        }
                        ((OnChatEventListener)copy).onChatEvent(databaseObject);
                        continue;
                    }
                }
            }
            Object o3 = this.userManager.getActiveAgentCircuit();
            Label_0740: {
                if (o3 == null) {
                    break Label_0740;
                }
                Object lastSessionID = ((SLAgentCircuit)o3).getSessionID();
            Label_0336_Outer:
                while (true) {
                    while (true) {
                    Label_0400_Outer:
                        while (true) {
                        Label_0400:
                            while (true) {
                                Label_0793: {
                                    while (true) {
                                        Label_0790: {
                                            Label_0784: {
                                                while (true) {
                                                    Label_0781: {
                                                        while (true) {
                                                            Label_0775: {
                                                                while (true) {
                                                                    Label_0769: {
                                                                        while (true) {
                                                                            Label_0764: {
                                                                                synchronized (this.chatEventLock) {
                                                                                    if (((ChatMessageSource)o).getSourceType() == ChatMessageSource.ChatMessageSourceType.User) {
                                                                                        if (!(o instanceof ChatMessageSourceUser)) {
                                                                                            break Label_0400;
                                                                                        }
                                                                                        final UserName userName = this.userManager.getDaoSession().getUserNameDao().load(((ChatMessageSource)o).getSourceUUID());
                                                                                        if (userName == null) {
                                                                                            break Label_0400_Outer;
                                                                                        }
                                                                                        o3 = o;
                                                                                        if (userName.getDisplayName() != null) {
                                                                                            ((ChatMessageSourceUser)o3).setDisplayName(userName.getDisplayName());
                                                                                        }
                                                                                        if (userName.getUserName() != null) {
                                                                                            ((ChatMessageSourceUser)o3).setLegacyName(userName.getUserName());
                                                                                        }
                                                                                        if (!userName.isComplete()) {
                                                                                            break Label_0400_Outer;
                                                                                        }
                                                                                        final int n = 1;
                                                                                        if (n != 0) {
                                                                                            break Label_0400;
                                                                                        }
                                                                                        o = ((ChatMessageSource)o).getSourceUUID();
                                                                                    }
                                                                                    else {
                                                                                        o = null;
                                                                                    }
                                                                                    if (databaseObject.opensNewChatter() || chatterID.getChatterType() == ChatterID.ChatterType.Local) {
                                                                                        break Label_0793;
                                                                                    }
                                                                                    o3 = this.getChatter(chatterID);
                                                                                    if (o3 == null || ((Chatter)o3).getActive()) {
                                                                                        break Label_0790;
                                                                                    }
                                                                                    o3 = localChatterID;
                                                                                    if (o3 != null) {
                                                                                        break Label_0784;
                                                                                    }
                                                                                    localChatterID = this.localChatterID;
                                                                                    final boolean contains = this.displayedChatters.contains(localChatterID);
                                                                                    if (o3 != null) {
                                                                                        break Label_0781;
                                                                                    }
                                                                                    o3 = this.getChatter((ChatterID)localChatterID);
                                                                                    final Chatter chatter;
                                                                                    if ((chatter = (Chatter)o3) != null) {
                                                                                        break Label_0781;
                                                                                    }
                                                                                    chatterID = (ChatterID)new Chatter(null);
                                                                                    ((ChatterID)localChatterID).toDatabaseObject((Chatter)chatterID);
                                                                                    ((AbstractDao<Chatter, K>)this.chatterDao).insert((Chatter)chatterID);
                                                                                    if (lastSessionID != null && (Objects.equal(lastSessionID, ((Chatter)chatterID).getLastSessionID()) ^ true)) {
                                                                                        if (((Chatter)chatterID).getLastSessionID() != null) {
                                                                                            this.makeSessionMark((ChatterID)localChatterID, ((Chatter)chatterID).getId());
                                                                                        }
                                                                                        ((Chatter)chatterID).setLastSessionID((UUID)lastSessionID);
                                                                                    }
                                                                                    databaseObject = (SLChatEvent)databaseObject.getDatabaseObject();
                                                                                    ((ChatMessage)databaseObject).setChatterID(((Chatter)chatterID).getId());
                                                                                    ((AbstractDao<ChatMessage, K>)this.chatMessageDao).insert((ChatMessage)databaseObject);
                                                                                    int n;
                                                                                    if (!((Chatter)chatterID).getActive()) {
                                                                                        if (!(((Chatter)chatterID).getMuted() ^ true)) {
                                                                                            break Label_0775;
                                                                                        }
                                                                                        ((Chatter)chatterID).setActive(true);
                                                                                        n = 1;
                                                                                    }
                                                                                    else {
                                                                                        n = 0;
                                                                                    }
                                                                                    if (!b || contains) {
                                                                                        break Label_0769;
                                                                                    }
                                                                                    ((Chatter)chatterID).setUnreadCount(((Chatter)chatterID).getUnreadCount() + 1);
                                                                                    final int n2 = 1;
                                                                                    ((Chatter)chatterID).setLastMessageID(((ChatMessage)databaseObject).getId());
                                                                                    ((AbstractDao<Chatter, K>)this.chatterDao).update((Chatter)chatterID);
                                                                                    monitorexit(this.chatEventLock);
                                                                                    if (!((Chatter)chatterID).getMuted() && n2 != 0) {
                                                                                        this.userManager.getUnreadNotificationManager().addFreshMessage((Chatter)chatterID);
                                                                                        o3 = this.chatEventBus;
                                                                                        if (((Chatter)chatterID).getType() != ChatterID.ChatterType.User.ordinal()) {
                                                                                            break Label_0764;
                                                                                        }
                                                                                        b = true;
                                                                                        ((EventBus)o3).post(new ChatMessageEvent((ChatMessage)databaseObject, true, b));
                                                                                    }
                                                                                    if (o != null) {
                                                                                        this.messageSourceNameResolver.requestResolve((UUID)o, ((ChatMessage)databaseObject).getId());
                                                                                    }
                                                                                    this.unreadCountsPool.requestUpdate((ChatterID)localChatterID);
                                                                                    if (n != 0) {
                                                                                        this.chatterList.updateList(ChatterListType.Active);
                                                                                    }
                                                                                    final List<ChatMessageLoader> loaders = this.getLoaders((ChatterID)localChatterID);
                                                                                    if (loaders != null) {
                                                                                        final Iterator<Object> iterator = loaders.iterator();
                                                                                        while (iterator.hasNext()) {
                                                                                            iterator.next().addElement((ChatMessage)databaseObject);
                                                                                        }
                                                                                        break;
                                                                                    }
                                                                                    break;
                                                                                    lastSessionID = null;
                                                                                    continue Label_0336_Outer;
                                                                                }
                                                                            }
                                                                            b = false;
                                                                            continue;
                                                                        }
                                                                    }
                                                                    final int n2 = 0;
                                                                    continue;
                                                                }
                                                            }
                                                            int n = 0;
                                                            continue;
                                                        }
                                                    }
                                                    continue;
                                                }
                                            }
                                            localChatterID = chatterID;
                                            continue Label_0400;
                                        }
                                        continue Label_0400_Outer;
                                    }
                                }
                                o3 = null;
                                localChatterID = chatterID;
                                continue Label_0400;
                            }
                            o = null;
                            continue;
                        }
                        int n = 0;
                        continue;
                    }
                }
            }
        }
        this.userManager.getSyncManager().syncNewMessages();
        this.userManager.getUnreadNotificationManager().updateUnreadNotifications();
    }
    
    private void makeSessionMark(@Nonnull ChatterID iterator, final long l) {
        while (true) {
            boolean b = false;
            Object chatEventLock = this.chatEventLock;
            while (true) {
                Label_0310: {
                    while (true) {
                        Label_0307: {
                            while (true) {
                                Object o = null;
                                Label_0296: {
                                    synchronized (chatEventLock) {
                                        o = ((AbstractDao<ChatMessage, K>)this.chatMessageDao).queryBuilder().where(ChatMessageDao.Properties.ChatterID.eq(l), new WhereCondition[0]).orderDesc(ChatMessageDao.Properties.Id).limit(1).list();
                                        if (o != null && ((List)o).size() > 0) {
                                            o = ((List)o).get(0);
                                            if (o != null && ((ChatMessage)o).getMessageType() == SLChatEvent.ChatMessageType.SessionMark.ordinal()) {
                                                if (!Objects.equal(((ChatMessage)o).getChatChannel(), SLChatSessionMarkEvent.SessionMarkType.NewSession.ordinal())) {
                                                    break Label_0310;
                                                }
                                                ((ChatMessage)o).setTimestamp(new Date());
                                                ((AbstractDao<ChatMessage, K>)this.chatMessageDao).update((ChatMessage)o);
                                                b = true;
                                            }
                                            else {
                                                o = null;
                                            }
                                        }
                                        else {
                                            o = null;
                                        }
                                        if (o != null) {
                                            break Label_0307;
                                        }
                                        o = new SLChatSessionMarkEvent(ChatMessageSourceUnknown.getInstance(), this.userManager.getUserID(), SLChatSessionMarkEvent.SessionMarkType.NewSession, null);
                                        o = ((SLChatEvent)o).getDatabaseObject();
                                        ((ChatMessage)o).setChatterID(l);
                                        ((AbstractDao<ChatMessage, K>)this.chatMessageDao).insert((ChatMessage)o);
                                        Debug.Printf("markNewSession: added session mark, chatterDbID %d", l);
                                        monitorexit(chatEventLock);
                                        final List<ChatMessageLoader> loaders = this.getLoaders(iterator);
                                        if (loaders != null) {
                                            iterator = (ChatterID)loaders.iterator();
                                            while (((Iterator)iterator).hasNext()) {
                                                chatEventLock = ((Iterator<ChatMessageLoader>)iterator).next();
                                                if (!b) {
                                                    break Label_0296;
                                                }
                                                ((ChunkedListLoader<ChatMessage>)chatEventLock).updateElement((ChatMessage)o);
                                            }
                                            break;
                                        }
                                        break;
                                    }
                                }
                                ((ChunkedListLoader<ChatMessage>)chatEventLock).addElement((ChatMessage)o);
                                continue;
                            }
                        }
                        continue;
                    }
                }
                Object o = null;
                continue;
            }
        }
    }
    
    private void markChatterInactiveInternal(final ChatterID chatterID, final boolean b) {
        int n = 0;
        boolean b2 = false;
        final int n2 = 1;
        if (chatterID == null || chatterID.getChatterType() == ChatterID.ChatterType.Local) {
            return;
        }
    Label_0127_Outer:
        while (true) {
            while (true) {
                Label_0191: {
                    while (true) {
                        synchronized (this.chatEventLock) {
                            final Chatter chatter = this.getChatter(chatterID);
                            if (chatter == null) {
                                break Label_0191;
                            }
                            if (chatter.getActive()) {
                                chatter.setActive(false);
                                if (b && (chatter.getMuted() ^ true)) {
                                    chatter.setMuted(true);
                                }
                                n = n2;
                                if (chatter.getLastMessageID() != null) {
                                    chatter.setLastMessageID(null);
                                    b2 = true;
                                    n = n2;
                                }
                                int n3 = b2 ? 1 : 0;
                                int n4;
                                if ((n4 = n) != 0) {
                                    ((AbstractDao<Chatter, K>)this.chatterDao).update(chatter);
                                    n4 = n;
                                    n3 = (b2 ? 1 : 0);
                                }
                                monitorexit(this.chatEventLock);
                                if (n4 != 0) {
                                    this.chatterList.updateList(ChatterListType.Active);
                                }
                                if (n3 != 0) {
                                    this.unreadCountsPool.requestUpdate(chatterID);
                                }
                                this.userManager.getUnreadNotificationManager().updateUnreadNotifications();
                                this.userManager.getSyncManager().flushChatter(chatterID);
                                return;
                            }
                        }
                        n = 0;
                        continue Label_0127_Outer;
                    }
                }
                int n4 = 0;
                int n3 = n;
                continue;
            }
        }
    }
    
    private void notifyChatEventUpdatedInternal(final SLChatEvent slChatEvent) {
        synchronized (this.chatEventLock) {
            final ChatMessage databaseObject = slChatEvent.getDatabaseObject();
            ((AbstractDao<ChatMessage, K>)this.chatMessageDao).update(databaseObject);
            monitorexit(this.chatEventLock);
            this.onMessageUpdated(databaseObject);
            this.userManager.getUnreadNotificationManager().updateUnreadNotifications();
        }
    }
    
    private void notifyTeleportCompleteInternal(final String s) {
        final Chatter chatter = this.getChatter(this.localChatterID);
        if (chatter != null) {
            final ChatMessage databaseObject = new SLChatSessionMarkEvent(ChatMessageSourceUnknown.getInstance(), this.userManager.getUserID(), SLChatSessionMarkEvent.SessionMarkType.Teleport, s).getDatabaseObject();
            synchronized (this.chatEventLock) {
                databaseObject.setChatterID(chatter.getId());
                ((AbstractDao<ChatMessage, K>)this.chatMessageDao).insert(databaseObject);
                monitorexit(this.chatEventLock);
                final List<ChatMessageLoader> loaders = this.getLoaders(this.localChatterID);
                if (loaders != null) {
                    final Iterator<Object> iterator = loaders.iterator();
                    while (iterator.hasNext()) {
                        iterator.next().addElement(databaseObject);
                    }
                }
            }
        }
    }
    
    private void onMessageUpdated(final ChatMessage chatMessage) {
        final Chatter chatter = this.chatterDao.load(chatMessage.getChatterID());
        ChatterID fromDatabaseObject;
        if (chatter != null) {
            fromDatabaseObject = ChatterID.fromDatabaseObject(this.userManager.getUserID(), chatter);
        }
        else {
            fromDatabaseObject = null;
        }
        if (fromDatabaseObject != null) {
            final List<ChatMessageLoader> loaders = this.getLoaders(fromDatabaseObject);
            if (loaders != null) {
                final Iterator<Object> iterator = loaders.iterator();
                while (iterator.hasNext()) {
                    iterator.next().updateElement(chatMessage);
                }
            }
            if (Objects.equal(chatMessage.getId(), chatter.getLastMessageID())) {
                this.unreadCountsPool.requestUpdate(fromDatabaseObject);
            }
            this.userManager.getUnreadNotificationManager().updateUnreadNotifications();
            this.chatEventBus.post(new ChatMessageEvent(chatMessage, false, fromDatabaseObject.getChatterType() == ChatterID.ChatterType.User));
        }
    }
    
    public void HandleChatEvent(final ChatterID chatterID, final SLChatEvent slChatEvent, final boolean b) {
        this.userManager.getDatabaseExecutor().execute(new _$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI$8(b, this, chatterID, slChatEvent));
    }
    
    public void addDisplayedChatter(@Nonnull final ChatterID chatterID) {
        if (this.displayedChatters.add(chatterID)) {
            this.userManager.getDatabaseExecutor().execute(new _$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI(this, chatterID));
        }
    }
    
    public void addObjectMessageListener(final OnChatEventListener onChatEventListener, @Nullable final Executor executor) {
        synchronized (this.objectMessageListenersLock) {
            this.objectMessageListeners.put(onChatEventListener, executor);
        }
    }
    
    public void clearChatHistory(@Nonnull final ChatterID chatterID) {
        this.userManager.getDatabaseExecutor().execute(new _$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI$1(this, chatterID));
    }
    
    ChatterDisplayDataList getActiveChattersList() {
        return new ActiveChattersDisplayDataList(this.userManager, this.onListUpdated);
    }
    
    public EventBus getChatEventBus() {
        return this.chatEventBus;
    }
    
    public ChatMessage getChatMessage(final long l) {
        synchronized (this.chatEventLock) {
            return this.chatMessageDao.load(l);
        }
    }
    
    public Chatter getChatter(@Nonnull final ChatterID chatterID) {
        return this.getChatter(chatterID, false);
    }
    
    Chatter getChatter(@Nonnull final ChatterID chatterID, final boolean b) {
        final Object o = null;
        Object lastSessionID = this.userManager.getActiveAgentCircuit();
        Label_0234: {
            if (lastSessionID == null) {
                break Label_0234;
            }
            lastSessionID = ((SLAgentCircuit)lastSessionID).getSessionID();
            Object optionalChatterUUID;
            Query<Chatter> query;
            boolean b2;
            Object lastSessionID2;
            int n;
            Label_0101_Outer:Label_0184_Outer:Label_0200_Outer:
            while (true) {
            Label_0200:
                while (true) {
                    Label_0292: {
                        while (true) {
                            Label_0286: {
                            Label_0280:
                                while (true) {
                                    Label_0274: {
                                        synchronized (this.chatEventLock) {
                                            optionalChatterUUID = chatterID.getOptionalChatterUUID();
                                            if (optionalChatterUUID != null) {
                                                query = this.findChatterQuery.forCurrentThread();
                                                query.setParameter(0, chatterID.getChatterType().ordinal());
                                                query.setParameter(1, ((UUID)optionalChatterUUID).toString());
                                            }
                                            else {
                                                query = this.findChatterQueryNullUUID.forCurrentThread();
                                                query.setParameter(0, chatterID.getChatterType().ordinal());
                                            }
                                            optionalChatterUUID = query.unique();
                                            if (b) {
                                                if (optionalChatterUUID == null) {
                                                    break Label_0274;
                                                }
                                                b2 = true;
                                                lastSessionID2 = o;
                                                if (optionalChatterUUID != null) {
                                                    lastSessionID2 = ((Chatter)optionalChatterUUID).getLastSessionID();
                                                }
                                                Debug.Printf("markNewSession: result has %b, cur %s, last %s", b2, lastSessionID, lastSessionID2);
                                            }
                                            if (!b || optionalChatterUUID == null || lastSessionID == null) {
                                                break Label_0280;
                                            }
                                            if (!(Objects.equal(lastSessionID, ((Chatter)optionalChatterUUID).getLastSessionID()) ^ true)) {
                                                break Label_0292;
                                            }
                                            if (((Chatter)optionalChatterUUID).getLastSessionID() != null) {
                                                n = 1;
                                                ((Chatter)optionalChatterUUID).setLastSessionID((UUID)lastSessionID);
                                                ((AbstractDao<Chatter, K>)this.chatterDao).update((Chatter)optionalChatterUUID);
                                                monitorexit(this.chatEventLock);
                                                if (n != 0) {
                                                    this.userManager.getDatabaseExecutor().execute(new _$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI$6(this, chatterID, optionalChatterUUID));
                                                }
                                                return (Chatter)optionalChatterUUID;
                                            }
                                            break Label_0286;
                                            lastSessionID = null;
                                            continue Label_0101_Outer;
                                        }
                                    }
                                    b2 = false;
                                    continue Label_0184_Outer;
                                }
                                n = 0;
                                continue Label_0200;
                            }
                            n = 0;
                            continue Label_0200_Outer;
                        }
                    }
                    n = 0;
                    continue Label_0200;
                }
            }
        }
    }
    
    public ChatMessageLoader getMessageLoader(@Nonnull final ChatterID chatterID, ChunkedListLoader.EventListener iterator) {
        ChatMessageLoader referent;
        while (true) {
            referent = new ChatMessageLoader(this.userManager, chatterID, 100, this.userManager.getDatabaseExecutor(), false, iterator);
            while (true) {
                Label_0141: {
                    synchronized (this.messageLoadersLock) {
                        iterator = (ChunkedListLoader.EventListener)this.messageLoaders.get(chatterID);
                        if (iterator != null) {
                            break Label_0141;
                        }
                        iterator = (ChunkedListLoader.EventListener)new LinkedList();
                        this.messageLoaders.put(chatterID, (List<WeakReference<ChatMessageLoader>>)iterator);
                        final ChunkedListLoader.EventListener eventListener = iterator;
                        iterator = (ChunkedListLoader.EventListener)((List)eventListener).iterator();
                        while (((Iterator)iterator).hasNext()) {
                            if (((Iterator<WeakReference>)iterator).next().get() == null) {
                                ((Iterator)iterator).remove();
                            }
                        }
                    }
                    break;
                }
                final ChunkedListLoader.EventListener eventListener = iterator;
                continue;
            }
        }
        final List<WeakReference> list;
        list.add(new WeakReference(referent));
        final Object o;
        monitorexit(o);
        return referent;
    }
    
    @Nullable
    public LazyList<ChatMessage> getMessages(final ChatterID chatterID) {
        final Chatter chatter = this.getChatter(chatterID);
        if (chatter == null) {
            return null;
        }
        return ((AbstractDao<ChatMessage, K>)this.chatMessageDao).queryBuilder().where(ChatMessageDao.Properties.ChatterID.eq(chatter.getId()), new WhereCondition[0]).orderAsc(ChatMessageDao.Properties.Id).listLazy();
    }
    
    public SubscriptionPool<ChatterID, UnreadMessageInfo> getUnreadCounts() {
        return this.unreadCountsPool;
    }
    
    public void markChatterInactive(final ChatterID chatterID, final boolean b) {
        this.userManager.getDatabaseExecutor().execute(new _$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI$7(b, this, chatterID));
    }
    
    public void notifyChatEventUpdated(final SLChatEvent slChatEvent) {
        this.userManager.getDatabaseExecutor().execute(new _$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI$3(this, slChatEvent));
    }
    
    public void notifyTeleportComplete(final String s) {
        this.userManager.getDatabaseExecutor().execute(new _$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI$4(this, s));
    }
    
    @Override
    public void onMessageSourcesResolved(final Set<Long> set, final UserName userName) {
        for (final Long n : set) {
            if (n != null) {
                final ChatMessage chatMessage = this.chatMessageDao.load(n);
                if (chatMessage == null) {
                    continue;
                }
                chatMessage.setSenderName(userName.getDisplayName());
                chatMessage.setSenderLegacyName(userName.getUserName());
                ((AbstractDao<ChatMessage, K>)this.chatMessageDao).update(chatMessage);
                this.onMessageUpdated(chatMessage);
            }
        }
    }
    
    public void releaseMessageLoader(final ChatterID chatterID, final ChatMessageLoader chatMessageLoader) {
        synchronized (this.messageLoadersLock) {
            final List list = this.messageLoaders.get(chatterID);
            if (list != null) {
                final Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    final ChatMessageLoader chatMessageLoader2 = ((WeakReference<ChatMessageLoader>)iterator.next()).get();
                    if (chatMessageLoader2 == null || chatMessageLoader2 == chatMessageLoader) {
                        iterator.remove();
                    }
                }
            }
        }
        final Object o;
        monitorexit(o);
    }
    
    public void removeDisplayedChatter(@Nonnull final ChatterID chatterID) {
        this.displayedChatters.remove(chatterID);
    }
    
    public void removeObjectMessageListener(final OnChatEventListener onChatEventListener) {
        synchronized (this.objectMessageListenersLock) {
            this.objectMessageListeners.remove(onChatEventListener);
        }
    }
    
    public void unmuteChatter(final ChatterID chatterID) {
        this.userManager.getDatabaseExecutor().execute(new _$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI$5(this, chatterID));
    }
    
    public static class ChatMessageEvent
    {
        @Nonnull
        public final ChatMessage chatMessage;
        public final boolean isNewMessage;
        public final boolean isPrivate;
        
        ChatMessageEvent(@Nonnull final ChatMessage chatMessage, final boolean isNewMessage, final boolean isPrivate) {
            this.chatMessage = chatMessage;
            this.isNewMessage = isNewMessage;
            this.isPrivate = isPrivate;
        }
    }
}
