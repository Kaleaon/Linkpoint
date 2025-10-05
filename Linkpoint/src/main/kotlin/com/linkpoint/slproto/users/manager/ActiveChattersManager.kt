package com.linkpoint.slproto.users.manager

import com.google.common.base.Objects
import com.google.common.eventbus.EventBus
import com.linkpoint.Debug
import com.linkpoint.dao.ChatMessage
import com.linkpoint.dao.ChatMessageDao
import com.linkpoint.dao.Chatter
import com.linkpoint.dao.ChatterDao
import com.linkpoint.dao.DaoSession
import com.linkpoint.dao.UserName
import com.linkpoint.react.RequestFinalProcessor
import com.linkpoint.react.SubscriptionPool
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.chat.SLChatSessionMarkEvent
import com.linkpoint.slproto.chat.generic.OnChatEventListener
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.chatsrc.ChatMessageSourceUnknown
import com.linkpoint.slproto.users.manager.MessageSourceNameResolver
import com.linkpoint.utils.wlist.ChunkedListLoader
import de.greenrobot.dao.query.LazyList
import de.greenrobot.dao.query.Query
import de.greenrobot.dao.query.WhereCondition
import java.lang.ref.WeakReference
import java.util.Collections
import java.util.Date
import java.util.HashMap
import java.util.HashSet
import java.util.Iterator
import java.util.LinkedList
import java.util.List
import java.util.Map
import java.util.Set
import java.util.UUID
import java.util.WeakHashMap
import java.util.concurrent.Executor
import javax.annotation.Nonnull
import javax.annotation.Nullable

class ActiveChattersManager : MessageSourceNameResolver.OnMessageSourcesResolvedListener {
    private const val CHAT_LOG_CHUNK_SIZE: Int = 100
    private val EventBus chatEventBus = EventBus()
    private val Object chatEventLock = Object()
    /* access modifiers changed from: private */
    val ChatMessageDao chatMessageDao
    private val ChatterDao chatterDao
    /* access modifiers changed from: private */
    val ChatterList chatterList
    private val Set<ChatterID> displayedChatters = Collections.synchronizedSet(HashSet())
    private val Query<Chatter> findChatterQuery
    private val Query<Chatter> findChatterQueryNullUUID
    private val ChatterID localChatterID
    private val Map<ChatterID, List<WeakReference<ChatMessageLoader>>> messageLoaders = HashMap()
    private val Object messageLoadersLock = Object()
    private val MessageSourceNameResolver messageSourceNameResolver
    private val Map<OnChatEventListener, Executor> objectMessageListeners = WeakHashMap()
    private val Object objectMessageListenersLock = Object()
    private val OnListUpdated onListUpdated = OnListUpdated() {
        public Unit onListUpdated() {
            ActiveChattersManager.this.chatterList.notifyListUpdated(ChatterListType.Active)
        }
    }
    private val SubscriptionPool<ChatterID, UnreadMessageInfo> unreadCountsPool = SubscriptionPool<>()
    private val UserManager userManager

    @JvmStatic
    class ChatMessageEvent {
        val ChatMessage chatMessage
        val Boolean isNewMessage
        val Boolean isPrivate

        ChatMessageEvent(ChatMessage chatMessage2, Boolean z, Boolean z2) {
            this.chatMessage = chatMessage2
            this.isNewMessage = z
            this.isPrivate = z2
        }
    }

    ActiveChattersManager(final UserManager userManager2, DaoSession daoSession, ChatterList chatterList2) {
        this.userManager = userManager2
        this.chatterList = chatterList2
        this.chatterDao = daoSession.getChatterDao()
        this.chatMessageDao = daoSession.getChatMessageDao()
        this.findChatterQuery = this.chatterDao.queryBuilder().where(ChatterDao.Properties.Type.eq((Object) null), ChatterDao.Properties.Uuid.eq("")).build()
        this.findChatterQueryNullUUID = this.chatterDao.queryBuilder().where(ChatterDao.Properties.Type.eq((Object) null), ChatterDao.Properties.Uuid.isNull()).build()
        this.localChatterID = ChatterID.getLocalChatterID(userManager2.getUserID())
        this.messageSourceNameResolver = MessageSourceNameResolver(userManager2, this)
        RequestFinalProcessor<ChatterID, UnreadMessageInfo>(this.unreadCountsPool, userManager2.getDatabaseExecutor()) {
            /* access modifiers changed from: protected */
            /* JADX WARNING: Code restructure failed: missing block: B:4:0x000f, code lost:
                r0 = (com.lumiyaviewer.lumiya.dao.ChatMessage) com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager.m269get0(r4.this$0).load(r2.getLastMessageID())
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public com.lumiyaviewer.lumiya.slproto.users.manager.UnreadMessageInfo processRequest(@javax.annotation.Nonnull com.lumiyaviewer.lumiya.slproto.users.ChatterID r5) throws java.lang.Throwable {
                /*
                    r4 = this
                    r1 = 0
                    com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager r0 = com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager.this
                    com.lumiyaviewer.lumiya.dao.Chatter r2 = r0.getChatter(r5)
                    if (r2 == 0) goto L_0x0034
                    java.lang.Long r0 = r2.getLastMessageID()
                    if (r0 == 0) goto L_0x003a
                    com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager r0 = com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager.this
                    com.lumiyaviewer.lumiya.dao.ChatMessageDao r0 = r0.chatMessageDao
                    java.lang.Long r3 = r2.getLastMessageID()
                    java.lang.Object r0 = r0.load(r3)
                    com.lumiyaviewer.lumiya.dao.ChatMessage r0 = (com.lumiyaviewer.lumiya.dao.ChatMessage) r0
                    if (r0 == 0) goto L_0x003a
                    com.lumiyaviewer.lumiya.slproto.users.manager.UserManager r1 = r9
                    java.util.UUID r1 = r1.getUserID()
                    com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent r0 = com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.loadFromDatabaseObject(r0, r1)
                L_0x002b:
                    Int r1 = r2.getUnreadCount()
                    com.lumiyaviewer.lumiya.slproto.users.manager.UnreadMessageInfo r0 = com.lumiyaviewer.lumiya.slproto.users.manager.UnreadMessageInfo.create(r1, r0)
                    return r0
                L_0x0034:
                    r0 = 0
                    com.lumiyaviewer.lumiya.slproto.users.manager.UnreadMessageInfo r0 = com.lumiyaviewer.lumiya.slproto.users.manager.UnreadMessageInfo.create(r0, r1)
                    return r0
                L_0x003a:
                    r0 = r1
                    goto L_0x002b
                */
                throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager.AnonymousClass2.processRequest(com.lumiyaviewer.lumiya.slproto.users.ChatterID):com.lumiyaviewer.lumiya.slproto.users.manager.UnreadMessageInfo")
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: clearChatHistoryInternal */
    public Unit m274lambda$com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_23064(ChatterID chatterID) {
        Chatter chatter
        synchronized (this.chatEventLock) {
            chatter = getChatter(chatterID)
            if (chatter != null) {
                this.chatMessageDao.queryBuilder().where(ChatMessageDao.Properties.ChatterID.eq(chatter.getId()), WhereCondition[0]).buildDelete().executeDeleteWithoutDetachingEntities()
            }
        }
        if (chatter != null) {
            this.userManager.getUnreadNotificationManager().clearFreshMessages(chatter)
        }
        List<ChatMessageLoader> loaders = getLoaders(chatterID)
        if (loaders != null) {
            for (ChatMessageLoader reload : loaders) {
                reload.reload()
            }
        }
        this.userManager.getUnreadNotificationManager().updateUnreadNotifications()
    }

    /* access modifiers changed from: private */
    /* renamed from: clearUnreadCount */
    public Unit m279lambda$com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_5516(ChatterID chatterID) {
        Chatter chatter = null
        Boolean z = false
        synchronized (this.chatEventLock) {
            if (!(!this.displayedChatters.contains(chatterID) || (chatter = getChatter(chatterID)) == null || chatter.getUnreadCount() == 0)) {
                z = true
                chatter.setUnreadCount(0)
                this.chatterDao.update(chatter)
            }
        }
        if (z) {
            this.userManager.getUnreadNotificationManager().clearFreshMessages(chatter)
            this.unreadCountsPool.requestUpdate(chatterID)
            this.userManager.getUnreadNotificationManager().updateUnreadNotifications()
        }
    }

    private List<ChatMessageLoader> getLoaders(ChatterID chatterID) {
        LinkedList linkedList = null
        synchronized (this.messageLoadersLock) {
            List list = this.messageLoaders.get(chatterID)
            if (list != null) {
                Iterator it = list.iterator()
                while (it.hasNext()) {
                    ChatMessageLoader chatMessageLoader = (ChatMessageLoader) ((WeakReference) it.next()).get()
                    if (chatMessageLoader == null) {
                        it.remove()
                    } else {
                        if (linkedList == null) {
                            linkedList = LinkedList()
                        }
                        linkedList.add(chatMessageLoader)
                    }
                    linkedList = linkedList
                }
            }
        }
        return linkedList
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00d2  */
    /* renamed from: handleChatEventInternal */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public Unit m273lambda$com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_13379(com.lumiyaviewer.lumiya.slproto.users.ChatterID r12, com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent r13, Boolean r14) {
        /*
            r11 = this
            r8 = 0
            r7 = 1
            r5 = 0
            Boolean r2 = r13.isObjectPopup()
            if (r2 == 0) goto L_0x0025
            com.lumiyaviewer.lumiya.slproto.users.manager.UserManager r2 = r11.userManager
            com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager r2 = r2.getObjectPopupsManager()
            r2.addObjectPopup(r13)
        L_0x0012:
            com.lumiyaviewer.lumiya.slproto.users.manager.UserManager r2 = r11.userManager
            com.lumiyaviewer.lumiya.slproto.users.manager.SyncManager r2 = r2.getSyncManager()
            r2.syncNewMessages()
            com.lumiyaviewer.lumiya.slproto.users.manager.UserManager r2 = r11.userManager
            com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationManager r2 = r2.getUnreadNotificationManager()
            r2.updateUnreadNotifications()
            return
        L_0x0025:
            com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource r4 = r13.getSource()
            com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource$ChatMessageSourceType r2 = r4.getSourceType()
            com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource$ChatMessageSourceType r3 = com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType.Object
            if (r2 != r3) goto L_0x0079
            java.lang.Object r3 = r11.objectMessageListenersLock
            monitor-enter(r3)
            java.util.Map<com.lumiyaviewer.lumiya.slproto.chat.generic.OnChatEventListener, java.util.concurrent.Executor> r2 = r11.objectMessageListeners     // Catch:{ all -> 0x0072 }
            Boolean r2 = r2.isEmpty()     // Catch:{ all -> 0x0072 }
            if (r2 != 0) goto L_0x0070
            java.util.Map<com.lumiyaviewer.lumiya.slproto.chat.generic.OnChatEventListener, java.util.concurrent.Executor> r2 = r11.objectMessageListeners     // Catch:{ all -> 0x0072 }
            java.util.Set r2 = r2.entrySet()     // Catch:{ all -> 0x0072 }
            com.google.common.collect.ImmutableList r2 = com.google.common.collect.ImmutableList.copyOf(r2)     // Catch:{ all -> 0x0072 }
        L_0x0046:
            monitor-exit(r3)
            if (r2 == 0) goto L_0x0079
            java.util.Iterator r6 = r2.iterator()
        L_0x004d:
            Boolean r2 = r6.hasNext()
            if (r2 == 0) goto L_0x0079
            java.lang.Object r2 = r6.next()
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2
            java.lang.Object r3 = r2.getKey()
            com.lumiyaviewer.lumiya.slproto.chat.generic.OnChatEventListener r3 = (com.lumiyaviewer.lumiya.slproto.chat.generic.OnChatEventListener) r3
            java.lang.Object r2 = r2.getValue()
            java.util.concurrent.Executor r2 = (java.util.concurrent.Executor) r2
            if (r2 == 0) goto L_0x0075
            com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI$2 r9 = com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI$2
            r9.<init>(r3, r13)
            r2.execute(r9)
            goto L_0x004d
        L_0x0070:
            r2 = r5
            goto L_0x0046
        L_0x0072:
            r2 = move-exception
            monitor-exit(r3)
            throw r2
        L_0x0075:
            r3.onChatEvent(r13)
            goto L_0x004d
        L_0x0079:
            com.lumiyaviewer.lumiya.slproto.users.manager.UserManager r2 = r11.userManager
            com.lumiyaviewer.lumiya.slproto.SLAgentCircuit r2 = r2.getActiveAgentCircuit()
            if (r2 == 0) goto L_0x01d2
            java.util.UUID r2 = r2.getSessionID()
            r6 = r2
        L_0x0086:
            java.lang.Object r10 = r11.chatEventLock
            monitor-enter(r10)
            com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource$ChatMessageSourceType r2 = r4.getSourceType()     // Catch:{ all -> 0x01da }
            com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource$ChatMessageSourceType r3 = com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType.User     // Catch:{ all -> 0x01da }
            if (r2 != r3) goto L_0x01d5
            Boolean r2 = r4 instanceof com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUser     // Catch:{ all -> 0x01da }
            if (r2 == 0) goto L_0x01f0
            com.lumiyaviewer.lumiya.slproto.users.manager.UserManager r2 = r11.userManager     // Catch:{ all -> 0x01da }
            com.lumiyaviewer.lumiya.dao.DaoSession r2 = r2.getDaoSession()     // Catch:{ all -> 0x01da }
            com.lumiyaviewer.lumiya.dao.UserNameDao r2 = r2.getUserNameDao()     // Catch:{ all -> 0x01da }
            java.util.UUID r3 = r4.getSourceUUID()     // Catch:{ all -> 0x01da }
            java.lang.Object r2 = r2.load(r3)     // Catch:{ all -> 0x01da }
            com.lumiyaviewer.lumiya.dao.UserName r2 = (com.lumiyaviewer.lumiya.dao.UserName) r2     // Catch:{ all -> 0x01da }
            if (r2 == 0) goto L_0x01f3
            r0 = r4
            com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUser r0 = (com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUser) r0     // Catch:{ all -> 0x01da }
            r3 = r0
            java.lang.String r9 = r2.getDisplayName()     // Catch:{ all -> 0x01da }
            if (r9 == 0) goto L_0x00bc
            java.lang.String r9 = r2.getDisplayName()     // Catch:{ all -> 0x01da }
            r3.setDisplayName(r9)     // Catch:{ all -> 0x01da }
        L_0x00bc:
            java.lang.String r9 = r2.getUserName()     // Catch:{ all -> 0x01da }
            if (r9 == 0) goto L_0x00c9
            java.lang.String r9 = r2.getUserName()     // Catch:{ all -> 0x01da }
            r3.setLegacyName(r9)     // Catch:{ all -> 0x01da }
        L_0x00c9:
            Boolean r2 = r2.isComplete()     // Catch:{ all -> 0x01da }
            if (r2 == 0) goto L_0x01f3
            r2 = r7
        L_0x00d0:
            if (r2 != 0) goto L_0x01f0
            java.util.UUID r2 = r4.getSourceUUID()     // Catch:{ all -> 0x01da }
            r9 = r2
        L_0x00d7:
            Boolean r2 = r13.opensNewChatter()     // Catch:{ all -> 0x01da }
            if (r2 != 0) goto L_0x01ed
            com.lumiyaviewer.lumiya.slproto.users.ChatterID$ChatterType r2 = r12.getChatterType()     // Catch:{ all -> 0x01da }
            com.lumiyaviewer.lumiya.slproto.users.ChatterID$ChatterType r3 = com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Local     // Catch:{ all -> 0x01da }
            if (r2 == r3) goto L_0x01ed
            com.lumiyaviewer.lumiya.dao.Chatter r2 = r11.getChatter(r12)     // Catch:{ all -> 0x01da }
            if (r2 == 0) goto L_0x01ea
            Boolean r3 = r2.getActive()     // Catch:{ all -> 0x01da }
            if (r3 != 0) goto L_0x01ea
        L_0x00f1:
            if (r5 != 0) goto L_0x01e7
            com.lumiyaviewer.lumiya.slproto.users.ChatterID r12 = r11.localChatterID     // Catch:{ all -> 0x01da }
            r2 = r5
        L_0x00f6:
            java.util.Set<com.lumiyaviewer.lumiya.slproto.users.ChatterID> r3 = r11.displayedChatters     // Catch:{ all -> 0x01da }
            Boolean r5 = r3.contains(r12)     // Catch:{ all -> 0x01da }
            if (r2 != 0) goto L_0x01e4
            com.lumiyaviewer.lumiya.dao.Chatter r2 = r11.getChatter(r12)     // Catch:{ all -> 0x01da }
            if (r2 != 0) goto L_0x01e4
            com.lumiyaviewer.lumiya.dao.Chatter r2 = com.lumiyaviewer.lumiya.dao.Chatter     // Catch:{ all -> 0x01da }
            r3 = 0
            r2.<init>(r3)     // Catch:{ all -> 0x01da }
            r12.toDatabaseObject(r2)     // Catch:{ all -> 0x01da }
            com.lumiyaviewer.lumiya.dao.ChatterDao r3 = r11.chatterDao     // Catch:{ all -> 0x01da }
            r3.insert(r2)     // Catch:{ all -> 0x01da }
            r4 = r2
        L_0x0113:
            if (r6 == 0) goto L_0x0135
            java.util.UUID r2 = r4.getLastSessionID()     // Catch:{ all -> 0x01da }
            Boolean r2 = com.google.common.base.Objects.equal(r6, r2)     // Catch:{ all -> 0x01da }
            r2 = r2 ^ 1
            if (r2 == 0) goto L_0x0135
            java.util.UUID r2 = r4.getLastSessionID()     // Catch:{ all -> 0x01da }
            if (r2 == 0) goto L_0x0132
            java.lang.Long r2 = r4.getId()     // Catch:{ all -> 0x01da }
            Long r2 = r2.longValue()     // Catch:{ all -> 0x01da }
            r11.makeSessionMark(r12, r2)     // Catch:{ all -> 0x01da }
        L_0x0132:
            r4.setLastSessionID(r6)     // Catch:{ all -> 0x01da }
        L_0x0135:
            com.lumiyaviewer.lumiya.dao.ChatMessage r6 = r13.getDatabaseObject()     // Catch:{ all -> 0x01da }
            java.lang.Long r2 = r4.getId()     // Catch:{ all -> 0x01da }
            Long r2 = r2.longValue()     // Catch:{ all -> 0x01da }
            r6.setChatterID(r2)     // Catch:{ all -> 0x01da }
            com.lumiyaviewer.lumiya.dao.ChatMessageDao r2 = r11.chatMessageDao     // Catch:{ all -> 0x01da }
            r2.insert(r6)     // Catch:{ all -> 0x01da }
            Boolean r2 = r4.getActive()     // Catch:{ all -> 0x01da }
            if (r2 != 0) goto L_0x01d8
            Boolean r2 = r4.getMuted()     // Catch:{ all -> 0x01da }
            r2 = r2 ^ 1
            if (r2 == 0) goto L_0x01e1
            r2 = 1
            r4.setActive(r2)     // Catch:{ all -> 0x01da }
            r3 = r7
        L_0x015c:
            if (r14 == 0) goto L_0x01df
            if (r5 != 0) goto L_0x01df
            Int r2 = r4.getUnreadCount()     // Catch:{ all -> 0x01da }
            Int r2 = r2 + 1
            r4.setUnreadCount(r2)     // Catch:{ all -> 0x01da }
            r2 = r7
        L_0x016a:
            java.lang.Long r5 = r6.getId()     // Catch:{ all -> 0x01da }
            r4.setLastMessageID(r5)     // Catch:{ all -> 0x01da }
            com.lumiyaviewer.lumiya.dao.ChatterDao r5 = r11.chatterDao     // Catch:{ all -> 0x01da }
            r5.update(r4)     // Catch:{ all -> 0x01da }
            monitor-exit(r10)
            Boolean r5 = r4.getMuted()
            if (r5 != 0) goto L_0x019f
            if (r2 == 0) goto L_0x019f
            com.lumiyaviewer.lumiya.slproto.users.manager.UserManager r2 = r11.userManager
            com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationManager r2 = r2.getUnreadNotificationManager()
            r2.addFreshMessage(r4)
            com.google.common.eventbus.EventBus r5 = r11.chatEventBus
            com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager$ChatMessageEvent r10 = com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager$ChatMessageEvent
            Int r2 = r4.getType()
            com.lumiyaviewer.lumiya.slproto.users.ChatterID$ChatterType r4 = com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.User
            Int r4 = r4.ordinal()
            if (r2 != r4) goto L_0x01dd
            r2 = r7
        L_0x0199:
            r10.<init>(r6, r7, r2)
            r5.post(r10)
        L_0x019f:
            if (r9 == 0) goto L_0x01aa
            com.lumiyaviewer.lumiya.slproto.users.manager.MessageSourceNameResolver r2 = r11.messageSourceNameResolver
            java.lang.Long r4 = r6.getId()
            r2.requestResolve(r9, r4)
        L_0x01aa:
            com.lumiyaviewer.lumiya.react.SubscriptionPool<com.lumiyaviewer.lumiya.slproto.users.ChatterID, com.lumiyaviewer.lumiya.slproto.users.manager.UnreadMessageInfo> r2 = r11.unreadCountsPool
            r2.requestUpdate(r12)
            if (r3 == 0) goto L_0x01b8
            com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList r2 = r11.chatterList
            com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType r3 = com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType.Active
            r2.updateList(r3)
        L_0x01b8:
            java.util.List r2 = r11.getLoaders(r12)
            if (r2 == 0) goto L_0x0012
            java.util.Iterator r3 = r2.iterator()
        L_0x01c2:
            Boolean r2 = r3.hasNext()
            if (r2 == 0) goto L_0x0012
            java.lang.Object r2 = r3.next()
            com.lumiyaviewer.lumiya.slproto.users.manager.ChatMessageLoader r2 = (com.lumiyaviewer.lumiya.slproto.users.manager.ChatMessageLoader) r2
            r2.addElement(r6)
            goto L_0x01c2
        L_0x01d2:
            r6 = r5
            goto L_0x0086
        L_0x01d5:
            r9 = r5
            goto L_0x00d7
        L_0x01d8:
            r3 = r8
            goto L_0x015c
        L_0x01da:
            r2 = move-exception
            monitor-exit(r10)
            throw r2
        L_0x01dd:
            r2 = r8
            goto L_0x0199
        L_0x01df:
            r2 = r8
            goto L_0x016a
        L_0x01e1:
            r3 = r8
            goto L_0x015c
        L_0x01e4:
            r4 = r2
            goto L_0x0113
        L_0x01e7:
            r2 = r5
            goto L_0x00f6
        L_0x01ea:
            r5 = r2
            goto L_0x00f1
        L_0x01ed:
            r2 = r5
            goto L_0x00f6
        L_0x01f0:
            r9 = r5
            goto L_0x00d7
        L_0x01f3:
            r2 = r8
            goto L_0x00d0
        */
        throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager.m273lambda$com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_13379(com.lumiyaviewer.lumiya.slproto.users.ChatterID, com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent, Boolean):Unit")
    }

    private Unit makeSessionMark(ChatterID chatterID, Long j) {
        ChatMessage chatMessage
        ChatMessage chatMessage2
        Boolean z = false
        synchronized (this.chatEventLock) {
            List list = this.chatMessageDao.queryBuilder().where(ChatMessageDao.Properties.ChatterID.eq(Long.valueOf(j)), WhereCondition[0]).orderDesc(ChatMessageDao.Properties.Id).limit(1).list()
            if (list == null || list.size() <= 0) {
                chatMessage = null
            } else {
                chatMessage = (ChatMessage) list.get(0)
                if (chatMessage == null || chatMessage.getMessageType() != SLChatEvent.ChatMessageType.SessionMark.ordinal()) {
                    chatMessage = null
                } else if (Objects.equal(chatMessage.getChatChannel(), Integer.valueOf(SLChatSessionMarkEvent.SessionMarkType.NewSession.ordinal()))) {
                    chatMessage.setTimestamp(Date())
                    this.chatMessageDao.update(chatMessage)
                    z = true
                } else {
                    chatMessage = null
                }
            }
            if (chatMessage == null) {
                ChatMessage databaseObject = SLChatSessionMarkEvent(ChatMessageSourceUnknown.getInstance(), this.userManager.getUserID(), SLChatSessionMarkEvent.SessionMarkType.NewSession, (String) null).getDatabaseObject()
                databaseObject.setChatterID(j)
                this.chatMessageDao.insert(databaseObject)
                chatMessage2 = databaseObject
            } else {
                chatMessage2 = chatMessage
            }
            Debug.Printf("markNewSession: added session mark, chatterDbID %d", Long.valueOf(j))
        }
        List<ChatMessageLoader> loaders = getLoaders(chatterID)
        if (loaders != null) {
            for (ChatMessageLoader chatMessageLoader : loaders) {
                if (z) {
                    chatMessageLoader.updateElement(chatMessage2)
                } else {
                    chatMessageLoader.addElement(chatMessage2)
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: markChatterInactiveInternal */
    public Unit m275lambda$com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_25047(ChatterID chatterID, Boolean z) {
        Boolean z2 = false
        Boolean z3 = true
        if (chatterID != null && chatterID.getChatterType() != ChatterID.ChatterType.Local) {
            synchronized (this.chatEventLock) {
                Chatter chatter = getChatter(chatterID)
                if (chatter != null) {
                    if (chatter.getActive()) {
                        chatter.setActive(false)
                        if (z && (!chatter.getMuted())) {
                            chatter.setMuted(true)
                        }
                        if (chatter.getLastMessageID() != null) {
                            chatter.setLastMessageID((Long) null)
                            z2 = true
                        }
                    } else {
                        z3 = false
                    }
                    if (z3) {
                        this.chatterDao.update(chatter)
                    }
                } else {
                    z3 = false
                }
            }
            if (z3) {
                this.chatterList.updateList(ChatterListType.Active)
            }
            if (z2) {
                this.unreadCountsPool.requestUpdate(chatterID)
            }
            this.userManager.getUnreadNotificationManager().updateUnreadNotifications()
            this.userManager.getSyncManager().flushChatter(chatterID)
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: notifyChatEventUpdatedInternal */
    public Unit m277lambda$com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_26357(SLChatEvent sLChatEvent) {
        ChatMessage databaseObject
        synchronized (this.chatEventLock) {
            databaseObject = sLChatEvent.getDatabaseObject()
            this.chatMessageDao.update(databaseObject)
        }
        onMessageUpdated(databaseObject)
        this.userManager.getUnreadNotificationManager().updateUnreadNotifications()
    }

    /* access modifiers changed from: private */
    /* renamed from: notifyTeleportCompleteInternal */
    public Unit m278lambda$com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_26532(String str) {
        Chatter chatter = getChatter(this.localChatterID)
        if (chatter != null) {
            ChatMessage databaseObject = SLChatSessionMarkEvent(ChatMessageSourceUnknown.getInstance(), this.userManager.getUserID(), SLChatSessionMarkEvent.SessionMarkType.Teleport, str).getDatabaseObject()
            synchronized (this.chatEventLock) {
                databaseObject.setChatterID(chatter.getId().longValue())
                this.chatMessageDao.insert(databaseObject)
            }
            List<ChatMessageLoader> loaders = getLoaders(this.localChatterID)
            if (loaders != null) {
                for (ChatMessageLoader addElement : loaders) {
                    addElement.addElement(databaseObject)
                }
            }
        }
    }

    private Unit onMessageUpdated(ChatMessage chatMessage) {
        Chatter chatter = (Chatter) this.chatterDao.load(Long.valueOf(chatMessage.getChatterID()))
        ChatterID fromDatabaseObject = chatter != null ? ChatterID.fromDatabaseObject(this.userManager.getUserID(), chatter) : null
        if (fromDatabaseObject != null) {
            List<ChatMessageLoader> loaders = getLoaders(fromDatabaseObject)
            if (loaders != null) {
                for (ChatMessageLoader updateElement : loaders) {
                    updateElement.updateElement(chatMessage)
                }
            }
            if (Objects.equal(chatMessage.getId(), chatter.getLastMessageID())) {
                this.unreadCountsPool.requestUpdate(fromDatabaseObject)
            }
            this.userManager.getUnreadNotificationManager().updateUnreadNotifications()
            this.chatEventBus.post(ChatMessageEvent(chatMessage, false, fromDatabaseObject.getChatterType() == ChatterID.ChatterType.User))
        }
    }

    public Unit HandleChatEvent(ChatterID chatterID, SLChatEvent sLChatEvent, Boolean z) {
        this.userManager.getDatabaseExecutor().execute(Runnable(z, this, chatterID, sLChatEvent) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Boolean f194$f0

            /* renamed from: -$f1 */
            private val /* synthetic */ Object f195$f1

            /* renamed from: -$f2 */
            private val /* synthetic */ Object f196$f2

            /* renamed from: -$f3 */
            private val /* synthetic */ Object f197$f3

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI.8.$m$0():Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI.8.$m$0():Unit, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

    }

    public Unit addDisplayedChatter(ChatterID chatterID) {
        if (this.displayedChatters.add(chatterID)) {
            this.userManager.getDatabaseExecutor().execute($Lambda$bC26PUjVA14BMgZPIZxiNFWFltI(this, chatterID))
        }
    }

    public Unit addObjectMessageListener(OnChatEventListener onChatEventListener, Executor executor) {
        synchronized (this.objectMessageListenersLock) {
            this.objectMessageListeners.put(onChatEventListener, executor)
        }
    }

    public Unit clearChatHistory(ChatterID chatterID) {
        this.userManager.getDatabaseExecutor().execute(Runnable(this, chatterID) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f178$f0

            /* renamed from: -$f1 */
            private val /* synthetic */ Object f179$f1

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI.1.$m$0():Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI.1.$m$0():Unit, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

    }

    /* access modifiers changed from: package-private */
    public ChatterDisplayDataList getActiveChattersList() {
        return ActiveChattersDisplayDataList(this.userManager, this.onListUpdated)
    }

    public EventBus getChatEventBus() {
        return this.chatEventBus
    }

    public ChatMessage getChatMessage(Long j) {
        ChatMessage chatMessage
        synchronized (this.chatEventLock) {
            chatMessage = (ChatMessage) this.chatMessageDao.load(Long.valueOf(j))
        }
        return chatMessage
    }

    public Chatter getChatter(ChatterID chatterID) {
        return getChatter(chatterID, false)
    }

    /* access modifiers changed from: package-private */
    public Chatter getChatter(ChatterID chatterID, Boolean z) {
        Query<Chatter> forCurrentThread
        Chatter unique
        UUID uuid = null
        SLAgentCircuit activeAgentCircuit = this.userManager.getActiveAgentCircuit()
        UUID sessionID = activeAgentCircuit != null ? activeAgentCircuit.getSessionID() : null
        synchronized (this.chatEventLock) {
            UUID optionalChatterUUID = chatterID.getOptionalChatterUUID()
            if (optionalChatterUUID != null) {
                forCurrentThread = this.findChatterQuery.forCurrentThread()
                forCurrentThread.setParameter(0, Integer.valueOf(chatterID.getChatterType().ordinal()))
                forCurrentThread.setParameter(1, optionalChatterUUID.toString())
            } else {
                forCurrentThread = this.findChatterQueryNullUUID.forCurrentThread()
                forCurrentThread.setParameter(0, Integer.valueOf(chatterID.getChatterType().ordinal()))
            }
            unique = forCurrentThread.unique()
            if (z) {
                Object[] objArr = Object[3]
                objArr[0] = Boolean.valueOf(unique != null)
                objArr[1] = sessionID
                if (unique != null) {
                    uuid = unique.getLastSessionID()
                }
                objArr[2] = uuid
                Debug.Printf("markNewSession: result has %b, cur %s, last %s", objArr)
            }
            if (!z || unique == null || sessionID == null) {
                z2 = false
            } else if (!Objects.equal(sessionID, unique.getLastSessionID())) {
                Boolean z3 = unique.getLastSessionID() != null
                unique.setLastSessionID(sessionID)
                this.chatterDao.update(unique)
                z2 = z3
            } else {
                z2 = false
            }
        }
        if (z2) {
            this.userManager.getDatabaseExecutor().execute(Runnable(this, chatterID, unique) {

                /* renamed from: -$f0 */
                private val /* synthetic */ Object f188$f0

                /* renamed from: -$f1 */
                private val /* synthetic */ Object f189$f1

                /* renamed from: -$f2 */
                private val /* synthetic */ Object f190$f2

                private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI.6.$m$0():Unit, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI.6.$m$0():Unit, class status: UNLOADED
                	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
                	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                
*/

        }
        return unique
    }

    public ChatMessageLoader getMessageLoader(ChatterID chatterID, ChunkedListLoader.EventListener eventListener) {
        LinkedList linkedList
        ChatMessageLoader chatMessageLoader = ChatMessageLoader(this.userManager, chatterID, 100, this.userManager.getDatabaseExecutor(), false, eventListener)
        synchronized (this.messageLoadersLock) {
            List list = this.messageLoaders.get(chatterID)
            if (list == null) {
                LinkedList linkedList2 = LinkedList()
                this.messageLoaders.put(chatterID, linkedList2)
                linkedList = linkedList2
            } else {
                linkedList = list
            }
            Iterator it = linkedList.iterator()
            while (it.hasNext()) {
                if (((WeakReference) it.next()).get() == null) {
                    it.remove()
                }
            }
            linkedList.add(WeakReference(chatMessageLoader))
        }
        return chatMessageLoader
    }

    public LazyList<ChatMessage> getMessages(ChatterID chatterID) {
        Chatter chatter = getChatter(chatterID)
        if (chatter == null) {
            return null
        }
        return this.chatMessageDao.queryBuilder().where(ChatMessageDao.Properties.ChatterID.eq(chatter.getId()), WhereCondition[0]).orderAsc(ChatMessageDao.Properties.Id).listLazy()
    }

    public SubscriptionPool<ChatterID, UnreadMessageInfo> getUnreadCounts() {
        return this.unreadCountsPool
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_10179  reason: not valid java name */
    public /* synthetic */ Unit m272lambda$com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_10179(ChatterID chatterID, Chatter chatter) {
        makeSessionMark(chatterID, chatter.getId().longValue())
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_25217  reason: not valid java name */
    public /* synthetic */ Unit m276lambda$com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_25217(ChatterID chatterID) {
        Boolean z = false
        if (chatterID != null && chatterID.getChatterType() != ChatterID.ChatterType.Local) {
            synchronized (this.chatEventLock) {
                Chatter chatter = getChatter(chatterID)
                if (chatter != null && chatter.getMuted()) {
                    chatter.setMuted(false)
                    z = true
                }
                if (z) {
                    this.chatterDao.update(chatter)
                }
            }
        }
    }

    public Unit markChatterInactive(ChatterID chatterID, Boolean z) {
        this.userManager.getDatabaseExecutor().execute(Runnable(z, this, chatterID) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Boolean f191$f0

            /* renamed from: -$f1 */
            private val /* synthetic */ Object f192$f1

            /* renamed from: -$f2 */
            private val /* synthetic */ Object f193$f2

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI.7.$m$0():Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI.7.$m$0():Unit, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

    }

    public Unit notifyChatEventUpdated(SLChatEvent sLChatEvent) {
        this.userManager.getDatabaseExecutor().execute(Runnable(this, sLChatEvent) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f182$f0

            /* renamed from: -$f1 */
            private val /* synthetic */ Object f183$f1

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI.3.$m$0():Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI.3.$m$0():Unit, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

    }

    public Unit notifyTeleportComplete(String str) {
        this.userManager.getDatabaseExecutor().execute(Runnable(this, str) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f184$f0

            /* renamed from: -$f1 */
            private val /* synthetic */ Object f185$f1

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI.4.$m$0():Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI.4.$m$0():Unit, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

    }

    public Unit onMessageSourcesResolved(Set<Long> set, UserName userName) {
        ChatMessage chatMessage
        for (Long l : set) {
            if (!(l == null || (chatMessage = (ChatMessage) this.chatMessageDao.load(l)) == null)) {
                chatMessage.setSenderName(userName.getDisplayName())
                chatMessage.setSenderLegacyName(userName.getUserName())
                this.chatMessageDao.update(chatMessage)
                onMessageUpdated(chatMessage)
            }
        }
    }

    public Unit releaseMessageLoader(ChatterID chatterID, ChatMessageLoader chatMessageLoader) {
        synchronized (this.messageLoadersLock) {
            List list = this.messageLoaders.get(chatterID)
            if (list != null) {
                Iterator it = list.iterator()
                while (it.hasNext()) {
                    ChatMessageLoader chatMessageLoader2 = (ChatMessageLoader) ((WeakReference) it.next()).get()
                    if (chatMessageLoader2 == null || chatMessageLoader2 == chatMessageLoader) {
                        it.remove()
                    }
                }
            }
        }
    }

    public Unit removeDisplayedChatter(ChatterID chatterID) {
        this.displayedChatters.remove(chatterID)
    }

    public Unit removeObjectMessageListener(OnChatEventListener onChatEventListener) {
        synchronized (this.objectMessageListenersLock) {
            this.objectMessageListeners.remove(onChatEventListener)
        }
    }

    public Unit unmuteChatter(ChatterID chatterID) {
        this.userManager.getDatabaseExecutor().execute(Runnable(this, chatterID) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f186$f0

            /* renamed from: -$f1 */
            private val /* synthetic */ Object f187$f1

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI.5.$m$0():Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI.5.$m$0():Unit, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

    }
}
