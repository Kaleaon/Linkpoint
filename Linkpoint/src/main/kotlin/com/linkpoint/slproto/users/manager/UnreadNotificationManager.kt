package com.linkpoint.slproto.users.manager

import android.content.Intent
import android.content.SharedPreferences
import com.google.common.collect.ImmutableMap
import com.linkpoint.Debug
import com.linkpoint.GlobalOptions
import com.linkpoint.LinkpointApp
import com.linkpoint.dao.ChatMessage
import com.linkpoint.dao.ChatMessageDao
import com.linkpoint.dao.Chatter
import com.linkpoint.dao.ChatterDao
import com.linkpoint.dao.DaoSession
import com.linkpoint.eventbus.EventBus
import com.linkpoint.eventbus.EventHandler
import com.linkpoint.react.SimpleRequestHandler
import com.linkpoint.react.Subscribable
import com.linkpoint.react.SubscriptionPool
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.ChatterNameRetriever
import com.linkpoint.slproto.users.manager.UnreadNotificationInfo
import com.linkpoint.ui.notify.NotificationChannels
import com.linkpoint.ui.settings.NotificationType
import de.greenrobot.dao.query.QueryBuilder
import de.greenrobot.dao.query.WhereCondition
import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.Collection
import java.util.HashMap
import java.util.HashSet
import java.util.Iterator
import java.util.LinkedList
import java.util.List
import java.util.Map
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.Nonnull
import javax.annotation.Nullable

class UnreadNotificationManager : ChatterNameRetriever.OnChatterNameUpdated {
    private const val FRESH_MESSAGES_NOTIFICATION_INTERVAL: Long = 3000
    private const val MASK_ENABLED_ALL: Int = 7
    private const val MASK_ENABLED_GROUP: Int = 2
    private const val MASK_ENABLED_IM: Int = 4
    private const val MASK_ENABLED_LOCAL: Int = 1
    private const val MAX_CHATTERS_PER_NOTIFICATION: Int = 3
    private const val MAX_MESSAGES_PER_NOTIFICATION: Int = 3
    const val Boolean unreadNotificationKey = Boolean.FALSE
    private val ChatMessageDao chatMessageDao
    private val ChatterDao chatterDao
    private val Map<Long, ChatterNameRetriever> chatterSources = ConcurrentHashMap(4, 0.75f, 2)
    private val UnreadNotificationInfo emptyNotification
    private val Map<Long, Integer> freshMessageCounts = HashMap()
    private val Object freshMessageCountsLock = Object()
    private val AtomicLong lastFreshMessageNotification = AtomicLong(0)
    private val AtomicInteger maskEnabled = AtomicInteger(7)
    private val AtomicReference<NotificationType> mostImportantNotificationType = AtomicReference<>()
    private WeakReference<NotifyCapture> notifyCapture = null
    private val Object notifyCaptureLock = Object()
    private val AtomicInteger totalSourcesCount = AtomicInteger(0)
    private val AtomicInteger totalUnreadCount = AtomicInteger(0)
    /* access modifiers changed from: private */
    val SubscriptionPool<Boolean, UnreadNotifications> unreadNotificationInfoPool = SubscriptionPool<>()
    /* access modifiers changed from: private */
    val Runnable updateChatterDataRunnable = Runnable() {
        fun run() {
            UnreadNotificationManager.this.updateUnreadChatterData()
            UnreadNotificationManager.this.updateExecutor.execute(UnreadNotificationManager.this.updateNotificationDataRunnable)
        }
    }
    /* access modifiers changed from: private */
    val Executor updateExecutor
    /* access modifiers changed from: private */
    val Runnable updateNotificationDataRunnable = Runnable() {
        fun run() {
            UnreadNotificationManager.this.unreadNotificationInfoPool.onResultData(UnreadNotificationManager.unreadNotificationKey, UnreadNotificationManager.this.getUnreadNotification())
        }
    }
    private val UserManager userManager

    interface NotifyCapture {
         fun onGetNotifyCaptureIntent(unreadNotificationInfo: UnreadNotificationInfo, Intent intent): Intent)
    }

    public UnreadNotificationManager(UserManager userManager2, DaoSession daoSession) {
        this.userManager = userManager2
        this.chatterDao = daoSession.getChatterDao()
        this.chatMessageDao = daoSession.getChatMessageDao()
        this.updateExecutor = userManager2.getDatabaseRunOnceExecutor()
        this.emptyNotification = UnreadNotificationInfo.create(userManager2.getUserID(), 0, (List<UnreadNotificationInfo.UnreadMessageSource>) null, (NotificationType) null, 0, (NotificationType) null, (UnreadNotificationInfo.UnreadMessageSource) null, UnreadNotificationInfo.ObjectPopupNotification.create(0, 0, (UnreadNotificationInfo.ObjectPopupMessage) null))
        this.unreadNotificationInfoPool.attachRequestHandler(SimpleRequestHandler<Boolean>() {
            fun onRequest(bool: Boolean) {
                UnreadNotificationManager.this.updateExecutor.execute(UnreadNotificationManager.this.updateChatterDataRunnable)
            }
        updateTypesFromPreferences(LinkpointApp.getDefaultSharedPreferences())
        EventBus.getInstance().subscribe((Object) this)
    }

    /* access modifiers changed from: private */
     public fun getUnreadNotification(): UnreadNotifications {
        NotificationType notificationType
        ArrayList arrayList
        UnreadNotificationInfo.UnreadMessageSource unreadMessageSource
        Int intValue
        val z: Boolean = System.currentTimeMillis() >= this.lastFreshMessageNotification.get() + FRESH_MESSAGES_NOTIFICATION_INTERVAL
        ImmutableMap.Builder builder = ImmutableMap.builder()
        for (NotificationType notificationType2 : NotificationType.VALUES) {
            val i2: Int = 0
            val i3: Int = 0
            val notificationType3: NotificationType = null
            val notificationType4: NotificationType = null
            val l: Long = null
            val z2: Boolean = false
            if (!this.chatterSources.isEmpty()) {
                val hashMap: HashMap = HashMap()
                val it: Iterator<T> = this.chatterSources.entrySet().iterator()
                while (true) {
                    val i4: Int = i3
                    val i5: Int = i2
                    val notificationType5: NotificationType = notificationType4
                    val notificationType6: NotificationType = notificationType3
                    val z3: Boolean = z2
                    val l2: Long = l
                    if (it.hasNext()) {
                        Map.Entry entry = (Map.Entry) it.next()
                        val chatterNameRetriever: ChatterNameRetriever = (ChatterNameRetriever) entry.getValue()
                        val chatterID: ChatterID = chatterNameRetriever.chatterID
                        val notificationType7: NotificationType = chatterID.getChatterType().getNotificationType()
                        if (notificationType7 == notificationType2) {
                            if (chatterID.getChatterType() == ChatterID.ChatterType.Local || chatterNameRetriever.getResolvedName() != null) {
                                val chatter: Chatter = (Chatter) this.chatterDao.load((Long) entry.getKey())
                                if (chatter != null) {
                                    val unreadCount: Int = chatter.getUnreadCount()
                                    val i6: Int = i5 + unreadCount
                                    hashMap.put(chatter.getId(), UnreadNotificationInfo.UnreadMessageSource.create(chatterID, chatterNameRetriever.getResolvedName(), (List<SLChatEvent>) null, unreadCount))
                                    val notificationType8: NotificationType = (notificationType6 == null || notificationType7.compareTo(notificationType6) > 0) ? notificationType7 : notificationType6
                                    if (z) {
                                        synchronized (this.freshMessageCountsLock) {
                                            val remove: Integer = this.freshMessageCounts.remove(chatter.getId())
                                            intValue = remove != null ? remove.intValue() : 0
                                        }
                                        i4 += intValue
                                        if (intValue != 0) {
                                            if (l2 == null && (!z3)) {
                                                l2 = chatter.getId()
                                            } else if (l2 != null) {
                                                l2 = null
                                                z3 = true
                                            }
                                            if (notificationType5 == null || notificationType7.compareTo(notificationType5) > 0) {
                                                z2 = z3
                                                l = l2
                                                i3 = i4
                                                i2 = i6
                                                notificationType4 = notificationType7
                                                notificationType3 = notificationType8
                                            } else {
                                                z2 = z3
                                                l = l2
                                                i2 = i6
                                                notificationType3 = notificationType8
                                                notificationType4 = notificationType5
                                                i3 = i4
                                            }
                                        }
                                    }
                                    z2 = z3
                                    l = l2
                                    i2 = i6
                                    notificationType3 = notificationType8
                                    notificationType4 = notificationType5
                                    i3 = i4
                                }
                            } else {
                                z2 = z3
                                l = l2
                                notificationType4 = notificationType5
                                notificationType3 = notificationType6
                                i3 = i4
                                i2 = i5
                            }
                        }
                        z2 = z3
                        l = l2
                        notificationType4 = notificationType5
                        notificationType3 = notificationType6
                        i3 = i4
                        i2 = i5
                    } else {
                        val i7: Int = hashMap.size() <= 1 ? 3 : 1
                        val arrayList2: ArrayList = ArrayList(hashMap.size())
                        UnreadNotificationInfo.UnreadMessageSource unreadMessageSource2 = null
                        for (Map.Entry entry2 : hashMap.entrySet()) {
                            val linkedList: LinkedList = LinkedList()
                            val unreadMessagesCount: Int = ((UnreadNotificationInfo.UnreadMessageSource) entry2.getValue()).unreadMessagesCount()
                            if (unreadMessagesCount > i7) {
                                unreadMessagesCount = i7
                            }
                            for (ChatMessage loadFromDatabaseObject : this.chatMessageDao.queryBuilder().where(ChatMessageDao.Properties.ChatterID.eq(entry2.getKey()), WhereCondition[0]).orderDesc(ChatMessageDao.Properties.Id).limit(unreadMessagesCount).list()) {
                                val loadFromDatabaseObject2: SLChatEvent = SLChatEvent.loadFromDatabaseObject(loadFromDatabaseObject, this.userManager.getUserID())
                                if (loadFromDatabaseObject2 != null) {
                                    linkedList.add(0, loadFromDatabaseObject2)
                                }
                            }
                            UnreadNotificationInfo.UnreadMessageSource withMessages = ((UnreadNotificationInfo.UnreadMessageSource) entry2.getValue()).withMessages(linkedList)
                            UnreadNotificationInfo.UnreadMessageSource unreadMessageSource3 = (l2 == null || !((Long) entry2.getKey()).equals(l2)) ? unreadMessageSource2 : withMessages
                            arrayList2.add(withMessages)
                            unreadMessageSource2 = unreadMessageSource3
                        }
                        arrayList = arrayList2
                        notificationType3 = notificationType6
                        i = i5
                        notificationType = notificationType5
                        i3 = i4
                        unreadMessageSource = unreadMessageSource2
                    }
                }
            } else {
                i = 0
                notificationType = null
                arrayList = null
                unreadMessageSource = null
            }
            UnreadNotificationInfo.ObjectPopupNotification notification = this.userManager.getObjectPopupsManager().getNotification(z)
            if (z && !(i3 == 0 && notification.freshObjectPopupsCount() == 0)) {
                this.lastFreshMessageNotification.set(System.currentTimeMillis())
            }
            val z4: Boolean = false
            if (arrayList != null && !arrayList.isEmpty()) {
                z4 = true
            }
            if (!((i == 0 && z4 && i3 == 0) ? notification.isEmpty() : false)) {
                builder.put(notificationType2, UnreadNotificationInfo.create(this.userManager.getUserID(), i, arrayList, notificationType3, i3, notificationType, unreadMessageSource, notification))
            }
        }
        return UnreadNotifications.create(this.userManager.getUserID(), builder.build())
    }

     private fun setEnabledMask(i: Int) {
        if (this.maskEnabled.getAndSet(i) != i) {
            updateUnreadNotifications()
        }
    }

     private fun updateTypesFromPreferences(sharedPreferences: SharedPreferences) {
        val i: Int = 0
        if (NotificationChannels.getInstance().areNotificationsSystemControlled()) {
            i = 7
        } else {
            if (sharedPreferences.getBoolean(NotificationType.LocalChat.getEnableKey(), true)) {
                i = 1
            }
            if (sharedPreferences.getBoolean(NotificationType.Group.getEnableKey(), true)) {
                i |= 2
            }
            if (sharedPreferences.getBoolean(NotificationType.Private.getEnableKey(), true)) {
                i |= 4
            }
        }
        setEnabledMask(i)
    }

    /* access modifiers changed from: private */
    fun updateUnreadChatterData() {
        val i: Int = this.maskEnabled.get()
        if (i == 0) {
            this.totalUnreadCount.set(0)
            this.totalSourcesCount.set(0)
            Iterator<Map.Entry<Long, ChatterNameRetriever>> it = this.chatterSources.entrySet().iterator()
            while (it.hasNext()) {
                ((ChatterNameRetriever) it.next().getValue()).dispose()
                it.remove()
            }
            this.mostImportantNotificationType.set((Object) null)
            return
        }
        val where: QueryBuilder = this.chatterDao.queryBuilder().where(ChatterDao.Properties.UnreadCount.gt(0), ChatterDao.Properties.Muted.notEq(true))
        if (i != 7) {
            val arrayList: ArrayList = ArrayList(3)
            if ((i & 1) != 0) {
                arrayList.add(Integer.valueOf(ChatterID.ChatterType.Local.ordinal()))
            }
            if ((i & 2) != 0) {
                arrayList.add(Integer.valueOf(ChatterID.ChatterType.Group.ordinal()))
            }
            if ((i & 4) != 0) {
                arrayList.add(Integer.valueOf(ChatterID.ChatterType.User.ordinal()))
            }
            where = where.where(ChatterDao.Properties.Type.in((Collection<?>) arrayList), WhereCondition[0])
        }
        val hashSet: HashSet = null
        val i2: Int = 0
        val i3: Int = 0
        val notificationType: NotificationType = null
        for (Chatter chatter : where.orderDesc(ChatterDao.Properties.LastMessageID).listLazy()) {
            val fromDatabaseObject: ChatterID = ChatterID.fromDatabaseObject(this.userManager.getUserID(), chatter)
            if (fromDatabaseObject != null) {
                if (hashSet == null) {
                    hashSet = HashSet()
                }
                if (hashSet.size() < 3) {
                    hashSet.add(chatter.getId())
                    if (!this.chatterSources.containsKey(chatter.getId())) {
                        this.chatterSources.put(chatter.getId(), ChatterNameRetriever(fromDatabaseObject, this, (Executor) null))
                    }
                }
                val notificationType2: NotificationType = fromDatabaseObject.getChatterType().getNotificationType()
                if (notificationType == null || notificationType2.compareTo(notificationType) > 0) {
                    notificationType = notificationType2
                }
                i3 += chatter.getUnreadCount()
                i2++
            }
            val notificationType3: NotificationType = notificationType
            i3 = i3
            i2 = i2
            hashSet = hashSet
            notificationType = notificationType3
        }
        this.totalUnreadCount.set(i3)
        this.totalSourcesCount.set(i2)
        this.mostImportantNotificationType.set(notificationType)
        Iterator<Map.Entry<Long, ChatterNameRetriever>> it2 = this.chatterSources.entrySet().iterator()
        while (it2.hasNext()) {
            Map.Entry next = it2.next()
            if (hashSet == null || (!hashSet.contains(next.getKey()))) {
                ((ChatterNameRetriever) next.getValue()).dispose()
                it2.remove()
            }
        }
    }

    fun addFreshMessage(chatter: Chatter) {
        ChatterID.ChatterType chatterType
        val z: Boolean = true
        val id: Long = chatter.getId()
        if (id != null) {
            val i: Int = this.maskEnabled.get()
            if (i == 0) {
                z = false
            } else if (i != 7 && (((chatterType = ChatterID.ChatterType.VALUES[chatter.getType()]) != ChatterID.ChatterType.User || (i & 4) == 0) && ((chatterType != ChatterID.ChatterType.Group || (i & 2) == 0) && (chatterType != ChatterID.ChatterType.Local || (i & 1) == 0)))) {
                z = false
            }
            if (z) {
                synchronized (this.freshMessageCountsLock) {
                    val num: Integer = this.freshMessageCounts.get(id)
                    this.freshMessageCounts.put(id, Integer.valueOf((num != null ? num.intValue() : 0) + 1))
                }
                return
            }
            synchronized (this.freshMessageCountsLock) {
                this.freshMessageCounts.remove(id)
            }
        }
    }

     public fun captureNotify(unreadNotificationInfo: UnreadNotificationInfo, intent: Intent): Intent {
        NotifyCapture notifyCapture2
        synchronized (this.notifyCaptureLock) {
            notifyCapture2 = this.notifyCapture != null ? (NotifyCapture) this.notifyCapture.get() : null
        }
        Debug.Printf("NotifyCapture: capture = %s", notifyCapture2)
        if (notifyCapture2 != null) {
            return notifyCapture2.onGetNotifyCaptureIntent(unreadNotificationInfo, intent)
        }
        return null
    }

    fun clearFreshMessages(chatter: Chatter) {
        val id: Long = chatter.getId()
        if (id != null) {
            synchronized (this.freshMessageCountsLock) {
                this.freshMessageCounts.remove(id)
            }
        }
    }

    fun clearNotifyCapture(notifyCapture2: NotifyCapture) {
        synchronized (this.notifyCaptureLock) {
            if (this.notifyCapture != null && this.notifyCapture.get() == notifyCapture2) {
                this.notifyCapture = null
                updateUnreadNotifications()
            }
        }
    }

    public Subscribable<Boolean, UnreadNotifications> getUnreadNotifications() {
        return this.unreadNotificationInfoPool
    }

    fun onChatterNameUpdated(chatterNameRetriever: ChatterNameRetriever) {
        this.updateExecutor.execute(this.updateNotificationDataRunnable)
    }

    @EventHandler
    fun onGlobalPreferencesChanged(GlobalOptions.GlobalOptionsChangedEvent globalOptionsChangedEvent) {
        if (globalOptionsChangedEvent.preferences != null) {
            updateTypesFromPreferences(globalOptionsChangedEvent.preferences)
        }
    }

    fun setNotifyCapture(notifyCapture2: NotifyCapture) {
        synchronized (this.notifyCaptureLock) {
            this.notifyCapture = WeakReference<>(notifyCapture2)
            updateUnreadNotifications()
        }
    }

    /* access modifiers changed from: package-private */
    fun updateUnreadNotifications() {
        this.unreadNotificationInfoPool.requestUpdate(unreadNotificationKey)
    }
}
