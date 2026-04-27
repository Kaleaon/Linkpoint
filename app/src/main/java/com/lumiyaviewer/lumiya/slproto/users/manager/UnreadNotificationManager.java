package com.lumiyaviewer.lumiya.slproto.users.manager;

import android.content.Intent;
import android.content.SharedPreferences;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.dao.ChatMessageDao;
import com.lumiyaviewer.lumiya.dao.Chatter;
import com.lumiyaviewer.lumiya.dao.ChatterDao;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo;
import com.lumiyaviewer.lumiya.ui.notify.NotificationChannels;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UnreadNotificationManager implements ChatterNameRetriever.OnChatterNameUpdated {
    private static final long FRESH_MESSAGES_NOTIFICATION_INTERVAL = 3000;
    private static final int MASK_ENABLED_ALL = 7;
    private static final int MASK_ENABLED_GROUP = 2;
    private static final int MASK_ENABLED_IM = 4;
    private static final int MASK_ENABLED_LOCAL = 1;
    private static final int MAX_CHATTERS_PER_NOTIFICATION = 3;
    private static final int MAX_MESSAGES_PER_NOTIFICATION = 3;
    public static final Boolean unreadNotificationKey = Boolean.FALSE;
    @Nonnull
    private final ChatMessageDao chatMessageDao;
    @Nonnull
    private final ChatterDao chatterDao;
    private final Map<Long, ChatterNameRetriever> chatterSources = new ConcurrentHashMap(4, 0.75f, 2);
    private final UnreadNotificationInfo emptyNotification;
    private final Map<Long, Integer> freshMessageCounts = new HashMap();
    private final Object freshMessageCountsLock = new Object();
    private final AtomicLong lastFreshMessageNotification = new AtomicLong(0);
    private final AtomicInteger maskEnabled = new AtomicInteger(7);
    private final AtomicReference<NotificationType> mostImportantNotificationType = new AtomicReference<>();
    @Nullable
    private WeakReference<NotifyCapture> notifyCapture = null;
    private final Object notifyCaptureLock = new Object();
    private final AtomicInteger totalSourcesCount = new AtomicInteger(0);
    private final AtomicInteger totalUnreadCount = new AtomicInteger(0);
    /* access modifiers changed from: private */
    public final SubscriptionPool<Boolean, UnreadNotifications> unreadNotificationInfoPool = new SubscriptionPool<>();
    /* access modifiers changed from: private */
    public final Runnable updateChatterDataRunnable = new Runnable() {
        public void run() {
            UnreadNotificationManager.this.updateUnreadChatterData();
            UnreadNotificationManager.this.updateExecutor.execute(UnreadNotificationManager.this.updateNotificationDataRunnable);
        }
    };
    /* access modifiers changed from: private */
    public final Executor updateExecutor;
    /* access modifiers changed from: private */
    public final Runnable updateNotificationDataRunnable = new Runnable() {
        public void run() {
            UnreadNotificationManager.this.unreadNotificationInfoPool.onResultData(UnreadNotificationManager.unreadNotificationKey, UnreadNotificationManager.this.getUnreadNotification());
        }
    };
    @Nonnull
    private final UserManager userManager;

    public interface NotifyCapture {
        @Nullable
        Intent onGetNotifyCaptureIntent(@Nonnull UnreadNotificationInfo unreadNotificationInfo, Intent intent);
    }

    public UnreadNotificationManager(@Nonnull UserManager userManager2, @Nonnull DaoSession daoSession) {
        this.userManager = userManager2;
        this.chatterDao = daoSession.getChatterDao();
        this.chatMessageDao = daoSession.getChatMessageDao();
        this.updateExecutor = userManager2.getDatabaseRunOnceExecutor();
        this.emptyNotification = UnreadNotificationInfo.create(userManager2.getUserID(), 0, (List<UnreadNotificationInfo.UnreadMessageSource>) null, (NotificationType) null, 0, (NotificationType) null, (UnreadNotificationInfo.UnreadMessageSource) null, UnreadNotificationInfo.ObjectPopupNotification.create(0, 0, (UnreadNotificationInfo.ObjectPopupMessage) null));
        this.unreadNotificationInfoPool.attachRequestHandler(new SimpleRequestHandler<Boolean>() {
            public void onRequest(@Nonnull Boolean bool) {
                UnreadNotificationManager.this.updateExecutor.execute(UnreadNotificationManager.this.updateChatterDataRunnable);
            }
        updateTypesFromPreferences(LumiyaApp.getDefaultSharedPreferences());
        EventBus.getInstance().subscribe((Object) this);
    }

    /* access modifiers changed from: private */
    @Nonnull
    public UnreadNotifications getUnreadNotification() {
        NotificationType notificationType;
        ArrayList arrayList;
        UnreadNotificationInfo.UnreadMessageSource unreadMessageSource;
        int intValue;
        boolean z = System.currentTimeMillis() >= this.lastFreshMessageNotification.get() + FRESH_MESSAGES_NOTIFICATION_INTERVAL;
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (NotificationType notificationType2 : NotificationType.VALUES) {
            int i2 = 0;
            int i3 = 0;
            NotificationType notificationType3 = null;
            NotificationType notificationType4 = null;
            Long l = null;
            boolean z2 = false;
            if (!this.chatterSources.isEmpty()) {
                HashMap hashMap = new HashMap();
                Iterator<T> it = this.chatterSources.entrySet().iterator();
                while (true) {
                    int i4 = i3;
                    int i5 = i2;
                    NotificationType notificationType5 = notificationType4;
                    NotificationType notificationType6 = notificationType3;
                    boolean z3 = z2;
                    Long l2 = l;
                    if (it.hasNext()) {
                        Map.Entry entry = (Map.Entry) it.next();
                        ChatterNameRetriever chatterNameRetriever = (ChatterNameRetriever) entry.getValue();
                        ChatterID chatterID = chatterNameRetriever.chatterID;
                        NotificationType notificationType7 = chatterID.getChatterType().getNotificationType();
                        if (notificationType7 == notificationType2) {
                            if (chatterID.getChatterType() == ChatterID.ChatterType.Local || chatterNameRetriever.getResolvedName() != null) {
                                Chatter chatter = (Chatter) this.chatterDao.load((Long) entry.getKey());
                                if (chatter != null) {
                                    int unreadCount = chatter.getUnreadCount();
                                    int i6 = i5 + unreadCount;
                                    hashMap.put(chatter.getId(), UnreadNotificationInfo.UnreadMessageSource.create(chatterID, chatterNameRetriever.getResolvedName(), (List<SLChatEvent>) null, unreadCount));
                                    NotificationType notificationType8 = (notificationType6 == null || notificationType7.compareTo(notificationType6) > 0) ? notificationType7 : notificationType6;
                                    if (z) {
                                        synchronized (this.freshMessageCountsLock) {
                                            Integer remove = this.freshMessageCounts.remove(chatter.getId());
                                            intValue = remove != null ? remove.intValue() : 0;
                                        }
                                        i4 += intValue;
                                        if (intValue != 0) {
                                            if (l2 == null && (!z3)) {
                                                l2 = chatter.getId();
                                            } else if (l2 != null) {
                                                l2 = null;
                                                z3 = true;
                                            }
                                            if (notificationType5 == null || notificationType7.compareTo(notificationType5) > 0) {
                                                z2 = z3;
                                                l = l2;
                                                i3 = i4;
                                                i2 = i6;
                                                notificationType4 = notificationType7;
                                                notificationType3 = notificationType8;
                                            } else {
                                                z2 = z3;
                                                l = l2;
                                                i2 = i6;
                                                notificationType3 = notificationType8;
                                                notificationType4 = notificationType5;
                                                i3 = i4;
                                            }
                                        }
                                    }
                                    z2 = z3;
                                    l = l2;
                                    i2 = i6;
                                    notificationType3 = notificationType8;
                                    notificationType4 = notificationType5;
                                    i3 = i4;
                                }
                            } else {
                                z2 = z3;
                                l = l2;
                                notificationType4 = notificationType5;
                                notificationType3 = notificationType6;
                                i3 = i4;
                                i2 = i5;
                            }
                        }
                        z2 = z3;
                        l = l2;
                        notificationType4 = notificationType5;
                        notificationType3 = notificationType6;
                        i3 = i4;
                        i2 = i5;
                    } else {
                        int i7 = hashMap.size() <= 1 ? 3 : 1;
                        ArrayList arrayList2 = new ArrayList(hashMap.size());
                        UnreadNotificationInfo.UnreadMessageSource unreadMessageSource2 = null;
                        for (Map.Entry entry2 : hashMap.entrySet()) {
                            LinkedList linkedList = new LinkedList();
                            int unreadMessagesCount = ((UnreadNotificationInfo.UnreadMessageSource) entry2.getValue()).unreadMessagesCount();
                            if (unreadMessagesCount > i7) {
                                unreadMessagesCount = i7;
                            }
                            for (ChatMessage loadFromDatabaseObject : this.chatMessageDao.queryBuilder().where(ChatMessageDao.Properties.ChatterID.eq(entry2.getKey()), new WhereCondition[0]).orderDesc(ChatMessageDao.Properties.Id).limit(unreadMessagesCount).list()) {
                                SLChatEvent loadFromDatabaseObject2 = SLChatEvent.loadFromDatabaseObject(loadFromDatabaseObject, this.userManager.getUserID());
                                if (loadFromDatabaseObject2 != null) {
                                    linkedList.add(0, loadFromDatabaseObject2);
                                }
                            }
                            UnreadNotificationInfo.UnreadMessageSource withMessages = ((UnreadNotificationInfo.UnreadMessageSource) entry2.getValue()).withMessages(linkedList);
                            UnreadNotificationInfo.UnreadMessageSource unreadMessageSource3 = (l2 == null || !((Long) entry2.getKey()).equals(l2)) ? unreadMessageSource2 : withMessages;
                            arrayList2.add(withMessages);
                            unreadMessageSource2 = unreadMessageSource3;
                        }
                        arrayList = arrayList2;
                        notificationType3 = notificationType6;
                        i = i5;
                        notificationType = notificationType5;
                        i3 = i4;
                        unreadMessageSource = unreadMessageSource2;
                    }
                }
            } else {
                i = 0;
                notificationType = null;
                arrayList = null;
                unreadMessageSource = null;
            }
            UnreadNotificationInfo.ObjectPopupNotification notification = this.userManager.getObjectPopupsManager().getNotification(z);
            if (z && !(i3 == 0 && notification.freshObjectPopupsCount() == 0)) {
                this.lastFreshMessageNotification.set(System.currentTimeMillis());
            }
            boolean z4 = false;
            if (arrayList != null && !arrayList.isEmpty()) {
                z4 = true;
            }
            if (!((i == 0 && z4 && i3 == 0) ? notification.isEmpty() : false)) {
                builder.put(notificationType2, UnreadNotificationInfo.create(this.userManager.getUserID(), i, arrayList, notificationType3, i3, notificationType, unreadMessageSource, notification));
            }
        }
        return UnreadNotifications.create(this.userManager.getUserID(), builder.build());
    }

    private void setEnabledMask(int i) {
        if (this.maskEnabled.getAndSet(i) != i) {
            updateUnreadNotifications();
        }
    }

    private void updateTypesFromPreferences(SharedPreferences sharedPreferences) {
        int i = 0;
        if (NotificationChannels.getInstance().areNotificationsSystemControlled()) {
            i = 7;
        } else {
            if (sharedPreferences.getBoolean(NotificationType.LocalChat.getEnableKey(), true)) {
                i = 1;
            }
            if (sharedPreferences.getBoolean(NotificationType.Group.getEnableKey(), true)) {
                i |= 2;
            }
            if (sharedPreferences.getBoolean(NotificationType.Private.getEnableKey(), true)) {
                i |= 4;
            }
        }
        setEnabledMask(i);
    }

    /* access modifiers changed from: private */
    public void updateUnreadChatterData() {
        int i = this.maskEnabled.get();
        if (i == 0) {
            this.totalUnreadCount.set(0);
            this.totalSourcesCount.set(0);
            Iterator<Map.Entry<Long, ChatterNameRetriever>> it = this.chatterSources.entrySet().iterator();
            while (it.hasNext()) {
                ((ChatterNameRetriever) it.next().getValue()).dispose();
                it.remove();
            }
            this.mostImportantNotificationType.set((Object) null);
            return;
        }
        QueryBuilder where = this.chatterDao.queryBuilder().where(ChatterDao.Properties.UnreadCount.gt(0), ChatterDao.Properties.Muted.notEq(true));
        if (i != 7) {
            ArrayList arrayList = new ArrayList(3);
            if ((i & 1) != 0) {
                arrayList.add(Integer.valueOf(ChatterID.ChatterType.Local.ordinal()));
            }
            if ((i & 2) != 0) {
                arrayList.add(Integer.valueOf(ChatterID.ChatterType.Group.ordinal()));
            }
            if ((i & 4) != 0) {
                arrayList.add(Integer.valueOf(ChatterID.ChatterType.User.ordinal()));
            }
            where = where.where(ChatterDao.Properties.Type.in((Collection<?>) arrayList), new WhereCondition[0]);
        }
        HashSet hashSet = null;
        int i2 = 0;
        int i3 = 0;
        NotificationType notificationType = null;
        for (Chatter chatter : where.orderDesc(ChatterDao.Properties.LastMessageID).listLazy()) {
            ChatterID fromDatabaseObject = ChatterID.fromDatabaseObject(this.userManager.getUserID(), chatter);
            if (fromDatabaseObject != null) {
                if (hashSet == null) {
                    hashSet = new HashSet();
                }
                if (hashSet.size() < 3) {
                    hashSet.add(chatter.getId());
                    if (!this.chatterSources.containsKey(chatter.getId())) {
                        this.chatterSources.put(chatter.getId(), new ChatterNameRetriever(fromDatabaseObject, this, (Executor) null));
                    }
                }
                NotificationType notificationType2 = fromDatabaseObject.getChatterType().getNotificationType();
                if (notificationType == null || notificationType2.compareTo(notificationType) > 0) {
                    notificationType = notificationType2;
                }
                i3 += chatter.getUnreadCount();
                i2++;
            }
            NotificationType notificationType3 = notificationType;
            i3 = i3;
            i2 = i2;
            hashSet = hashSet;
            notificationType = notificationType3;
        }
        this.totalUnreadCount.set(i3);
        this.totalSourcesCount.set(i2);
        this.mostImportantNotificationType.set(notificationType);
        Iterator<Map.Entry<Long, ChatterNameRetriever>> it2 = this.chatterSources.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry next = it2.next();
            if (hashSet == null || (!hashSet.contains(next.getKey()))) {
                ((ChatterNameRetriever) next.getValue()).dispose();
                it2.remove();
            }
        }
    }

    public void addFreshMessage(@Nonnull Chatter chatter) {
        ChatterID.ChatterType chatterType;
        boolean z = true;
        Long id = chatter.getId();
        if (id != null) {
            int i = this.maskEnabled.get();
            if (i == 0) {
                z = false;
            } else if (i != 7 && (((chatterType = ChatterID.ChatterType.VALUES[chatter.getType()]) != ChatterID.ChatterType.User || (i & 4) == 0) && ((chatterType != ChatterID.ChatterType.Group || (i & 2) == 0) && (chatterType != ChatterID.ChatterType.Local || (i & 1) == 0)))) {
                z = false;
            }
            if (z) {
                synchronized (this.freshMessageCountsLock) {
                    Integer num = this.freshMessageCounts.get(id);
                    this.freshMessageCounts.put(id, Integer.valueOf((num != null ? num.intValue() : 0) + 1));
                }
                return;
            }
            synchronized (this.freshMessageCountsLock) {
                this.freshMessageCounts.remove(id);
            }
        }
    }

    @Nullable
    public Intent captureNotify(UnreadNotificationInfo unreadNotificationInfo, Intent intent) {
        NotifyCapture notifyCapture2;
        synchronized (this.notifyCaptureLock) {
            notifyCapture2 = this.notifyCapture != null ? (NotifyCapture) this.notifyCapture.get() : null;
        }
        Debug.Printf("NotifyCapture: capture = %s", notifyCapture2);
        if (notifyCapture2 != null) {
            return notifyCapture2.onGetNotifyCaptureIntent(unreadNotificationInfo, intent);
        }
        return null;
    }

    public void clearFreshMessages(@Nonnull Chatter chatter) {
        Long id = chatter.getId();
        if (id != null) {
            synchronized (this.freshMessageCountsLock) {
                this.freshMessageCounts.remove(id);
            }
        }
    }

    public void clearNotifyCapture(@Nullable NotifyCapture notifyCapture2) {
        synchronized (this.notifyCaptureLock) {
            if (this.notifyCapture != null && this.notifyCapture.get() == notifyCapture2) {
                this.notifyCapture = null;
                updateUnreadNotifications();
            }
        }
    }

    public Subscribable<Boolean, UnreadNotifications> getUnreadNotifications() {
        return this.unreadNotificationInfoPool;
    }

    public void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        this.updateExecutor.execute(this.updateNotificationDataRunnable);
    }

    @EventHandler
    public void onGlobalPreferencesChanged(GlobalOptions.GlobalOptionsChangedEvent globalOptionsChangedEvent) {
        if (globalOptionsChangedEvent.preferences != null) {
            updateTypesFromPreferences(globalOptionsChangedEvent.preferences);
        }
    }

    public void setNotifyCapture(@Nullable NotifyCapture notifyCapture2) {
        synchronized (this.notifyCaptureLock) {
            this.notifyCapture = new WeakReference<>(notifyCapture2);
            updateUnreadNotifications();
        }
    }

    /* access modifiers changed from: package-private */
    public void updateUnreadNotifications() {
        this.unreadNotificationInfoPool.requestUpdate(unreadNotificationKey);
    }
}
