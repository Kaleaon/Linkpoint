// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import de.greenrobot.dao.AbstractDao;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.Debug;
import android.content.Intent;
import java.util.Set;
import de.greenrobot.dao.query.QueryBuilder;
import java.util.HashSet;
import java.util.Collection;
import com.lumiyaviewer.lumiya.ui.notify.NotificationChannels;
import android.content.SharedPreferences;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.WhereCondition;
import java.util.LinkedList;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.dao.Chatter;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import java.util.List;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;
import com.lumiyaviewer.lumiya.dao.ChatterDao;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.dao.ChatMessageDao;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;

public class UnreadNotificationManager implements OnChatterNameUpdated
{
    private static final long FRESH_MESSAGES_NOTIFICATION_INTERVAL = 3000L;
    private static final int MASK_ENABLED_ALL = 7;
    private static final int MASK_ENABLED_GROUP = 2;
    private static final int MASK_ENABLED_IM = 4;
    private static final int MASK_ENABLED_LOCAL = 1;
    private static final int MAX_CHATTERS_PER_NOTIFICATION = 3;
    private static final int MAX_MESSAGES_PER_NOTIFICATION = 3;
    public static final Boolean unreadNotificationKey;
    @Nonnull
    private final ChatMessageDao chatMessageDao;
    @Nonnull
    private final ChatterDao chatterDao;
    private final Map<Long, ChatterNameRetriever> chatterSources;
    private final UnreadNotificationInfo emptyNotification;
    private final Map<Long, Integer> freshMessageCounts;
    private final Object freshMessageCountsLock;
    private final AtomicLong lastFreshMessageNotification;
    private final AtomicInteger maskEnabled;
    private final AtomicReference<NotificationType> mostImportantNotificationType;
    @Nullable
    private WeakReference<NotifyCapture> notifyCapture;
    private final Object notifyCaptureLock;
    private final AtomicInteger totalSourcesCount;
    private final AtomicInteger totalUnreadCount;
    private final SubscriptionPool<Boolean, UnreadNotifications> unreadNotificationInfoPool;
    private final Runnable updateChatterDataRunnable;
    private final Executor updateExecutor;
    private final Runnable updateNotificationDataRunnable;
    @Nonnull
    private final UserManager userManager;
    
    static {
        unreadNotificationKey = Boolean.FALSE;
    }
    
    public UnreadNotificationManager(@Nonnull final UserManager userManager, @Nonnull final DaoSession daoSession) {
        this.chatterSources = new ConcurrentHashMap<Long, ChatterNameRetriever>(4, 0.75f, 2);
        this.maskEnabled = new AtomicInteger(7);
        this.totalUnreadCount = new AtomicInteger(0);
        this.totalSourcesCount = new AtomicInteger(0);
        this.mostImportantNotificationType = new AtomicReference<NotificationType>();
        this.freshMessageCounts = new HashMap<Long, Integer>();
        this.freshMessageCountsLock = new Object();
        this.lastFreshMessageNotification = new AtomicLong(0L);
        this.notifyCaptureLock = new Object();
        this.notifyCapture = null;
        this.updateChatterDataRunnable = new Runnable() {
            @Override
            public void run() {
                UnreadNotificationManager.this.updateUnreadChatterData();
                UnreadNotificationManager.this.updateExecutor.execute(UnreadNotificationManager.this.updateNotificationDataRunnable);
            }
        };
        this.updateNotificationDataRunnable = new Runnable() {
            @Override
            public void run() {
                UnreadNotificationManager.this.unreadNotificationInfoPool.onResultData(UnreadNotificationManager.unreadNotificationKey, UnreadNotificationManager.this.getUnreadNotification());
            }
        };
        this.unreadNotificationInfoPool = new SubscriptionPool<Boolean, UnreadNotifications>();
        this.userManager = userManager;
        this.chatterDao = daoSession.getChatterDao();
        this.chatMessageDao = daoSession.getChatMessageDao();
        this.updateExecutor = userManager.getDatabaseRunOnceExecutor();
        this.emptyNotification = UnreadNotificationInfo.create(userManager.getUserID(), 0, null, null, 0, null, null, UnreadNotificationInfo.ObjectPopupNotification.create(0, 0, null));
        this.unreadNotificationInfoPool.attachRequestHandler(new SimpleRequestHandler<Boolean>() {
            @Override
            public void onRequest(@Nonnull final Boolean b) {
                UnreadNotificationManager.this.updateExecutor.execute(UnreadNotificationManager.this.updateChatterDataRunnable);
            }
        });
        this.updateTypesFromPreferences(LumiyaApp.getDefaultSharedPreferences());
        EventBus.getInstance().subscribe(this);
    }
    
    @Nonnull
    private UnreadNotifications getUnreadNotification() {
        boolean b = false;
        Object id;
        Integer n;
        int intValue;
        int n2 = 0;
        int unreadCount = 0;
        Object obj = null;
        int n3 = 0;
        Map.Entry<K, ChatterNameRetriever> o = null;
        NotificationType notificationType;
        Chatter chatter;
        ImmutableMap.Builder<Chatter, UnreadNotificationInfo> builder = null;
        Iterator<Object> iterator;
        NotificationType notificationType2 = null;
        int n4 = 0;
        NotificationType o2 = null;
        int n5 = 0;
        NotificationType notificationType3 = null;
        List<UnreadNotificationInfo.UnreadMessageSource> list = null;
        UnreadNotificationInfo.UnreadMessageSource unreadMessageSource = null;
        HashMap hashMap = null;
        Iterator<Object> iterator2 = null;
        int n6 = 0;
        Object create;
        ChatterID chatterID;
        int n7;
        ArrayList list2;
        Iterator iterator3;
        UnreadNotificationInfo.UnreadMessageSource unreadMessageSource2;
        Map.Entry<Long, V> entry;
        LinkedList<SLChatEvent> list3;
        int unreadMessagesCount;
        Iterator<Object> iterator4;
        SLChatEvent loadFromDatabaseObject;
        UnreadNotificationInfo.UnreadMessageSource withMessages;
        Map.Entry<K, ChatterNameRetriever> entry2;
        UnreadNotificationInfo.ObjectPopupNotification notification;
        int n8;
        Label_0402:Label_0033_Outer:
        while (true) {
            Label_0967: {
                while (true) {
                    Label_0813: {
                    Label_0434_Outer:
                        while (true) {
                            Label_0281: {
                            Label_0461_Outer:
                                while (true) {
                                    while (true) {
                                        Label_0114: {
                                            Label_0020: {
                                                if (System.currentTimeMillis() >= this.lastFreshMessageNotification.get() + 3000L) {
                                                    b = true;
                                                    break Label_0020;
                                                }
                                                Label_0429: {
                                                    break Label_0429;
                                                    while (true) {
                                                        while (true) {
                                                            Label_0990: {
                                                            Label_0490:
                                                                while (true) {
                                                                    Label_0472: {
                                                                        synchronized (this.freshMessageCountsLock) {
                                                                            n = this.freshMessageCounts.remove(((Chatter)id).getId());
                                                                            if (n == null) {
                                                                                break Label_0990;
                                                                            }
                                                                            intValue = n;
                                                                            monitorexit(this.freshMessageCountsLock);
                                                                            n2 = (unreadCount = n2 + intValue);
                                                                            if (intValue == 0) {
                                                                                break Label_0967;
                                                                            }
                                                                            if (obj != null || (n3 ^ 0x1) == 0x0) {
                                                                                break Label_0472;
                                                                            }
                                                                            id = ((Chatter)id).getId();
                                                                            if (o == null || notificationType.compareTo((NotificationType)o) > 0) {
                                                                                unreadCount = n3;
                                                                                chatter = (Chatter)id;
                                                                                obj = notificationType;
                                                                                n3 = n2;
                                                                                n2 = unreadCount;
                                                                                unreadCount = n3;
                                                                                id = obj;
                                                                                n3 = n2;
                                                                                obj = chatter;
                                                                                n2 = unreadCount;
                                                                                break Label_0114;
                                                                            }
                                                                            break Label_0490;
                                                                            id = obj;
                                                                            obj = o;
                                                                            unreadCount = n2;
                                                                            n2 = n3;
                                                                            chatter = (Chatter)id;
                                                                            n3 = unreadCount;
                                                                            continue Label_0402;
                                                                            b = false;
                                                                            break;
                                                                            break Label_0281;
                                                                        }
                                                                    }
                                                                    if ((id = obj) != null) {
                                                                        id = null;
                                                                        n3 = 1;
                                                                        continue Label_0434_Outer;
                                                                    }
                                                                    continue Label_0434_Outer;
                                                                }
                                                                obj = o;
                                                                unreadCount = n2;
                                                                n2 = n3;
                                                                chatter = (Chatter)id;
                                                                n3 = unreadCount;
                                                                continue Label_0402;
                                                            }
                                                            intValue = 0;
                                                            continue Label_0434_Outer;
                                                        }
                                                    }
                                                }
                                            }
                                            builder = ImmutableMap.builder();
                                            iterator = NotificationType.VALUES.iterator();
                                            if (!iterator.hasNext()) {
                                                return UnreadNotifications.create(this.userManager.getUserID(), (ImmutableMap<NotificationType, UnreadNotificationInfo>)builder.build());
                                            }
                                            notificationType2 = iterator.next();
                                            n4 = 0;
                                            o2 = null;
                                            if (this.chatterSources.isEmpty()) {
                                                n5 = 0;
                                                notificationType3 = null;
                                                list = null;
                                                unreadMessageSource = null;
                                                break Label_0813;
                                            }
                                            hashMap = new HashMap();
                                            iterator2 = this.chatterSources.entrySet().iterator();
                                            n2 = 0;
                                            n6 = 0;
                                            o = null;
                                            o2 = null;
                                            n3 = 0;
                                            obj = null;
                                        }
                                        if (iterator2.hasNext()) {
                                            id = iterator2.next();
                                            create = ((Map.Entry<K, ChatterNameRetriever>)id).getValue();
                                            chatterID = ((ChatterNameRetriever)create).chatterID;
                                            notificationType = chatterID.getChatterType().getNotificationType();
                                            if (notificationType == notificationType2) {
                                                if (chatterID.getChatterType() != ChatterID.ChatterType.Local && ((ChatterNameRetriever)create).getResolvedName() == null) {
                                                    continue Label_0461_Outer;
                                                }
                                                id = ((AbstractDao<Object, Long>)this.chatterDao).load((Long)((Map.Entry)id).getKey());
                                                if (id != null) {
                                                    unreadCount = ((Chatter)id).getUnreadCount();
                                                    n6 += unreadCount;
                                                    create = UnreadNotificationInfo.UnreadMessageSource.create(chatterID, ((ChatterNameRetriever)create).getResolvedName(), null, unreadCount);
                                                    hashMap.put(((Chatter)id).getId(), create);
                                                    if (o2 == null || notificationType.compareTo(o2) > 0) {
                                                        o2 = notificationType;
                                                        break Label_0281;
                                                    }
                                                    continue Label_0033_Outer;
                                                }
                                            }
                                            unreadCount = n3;
                                            id = o;
                                            n3 = n2;
                                            n2 = unreadCount;
                                            chatter = (Chatter)obj;
                                            obj = id;
                                            continue Label_0402;
                                        }
                                        break;
                                    }
                                    break;
                                }
                                if (hashMap.size() <= 1) {
                                    n7 = 3;
                                }
                                else {
                                    n7 = 1;
                                }
                                list2 = new ArrayList<UnreadNotificationInfo.UnreadMessageSource>(hashMap.size());
                                iterator3 = hashMap.entrySet().iterator();
                                unreadMessageSource2 = null;
                                while (iterator3.hasNext()) {
                                    entry = (Map.Entry<Long, V>)iterator3.next();
                                    list3 = new LinkedList<SLChatEvent>();
                                    if ((unreadMessagesCount = ((UnreadNotificationInfo.UnreadMessageSource)entry.getValue()).unreadMessagesCount()) > n7) {
                                        unreadMessagesCount = n7;
                                    }
                                    iterator4 = ((AbstractDao<ChatMessage, K>)this.chatMessageDao).queryBuilder().where(ChatMessageDao.Properties.ChatterID.eq(entry.getKey()), new WhereCondition[0]).orderDesc(ChatMessageDao.Properties.Id).limit(unreadMessagesCount).list().iterator();
                                    while (iterator4.hasNext()) {
                                        loadFromDatabaseObject = SLChatEvent.loadFromDatabaseObject(iterator4.next(), this.userManager.getUserID());
                                        if (loadFromDatabaseObject != null) {
                                            list3.add(0, loadFromDatabaseObject);
                                        }
                                    }
                                    withMessages = ((UnreadNotificationInfo.UnreadMessageSource)entry.getValue()).withMessages(list3);
                                    if (obj != null && entry.getKey().equals(obj)) {
                                        unreadMessageSource2 = withMessages;
                                    }
                                    list2.add(withMessages);
                                }
                                list = (List<UnreadNotificationInfo.UnreadMessageSource>)list2;
                                entry2 = o;
                                unreadMessageSource = unreadMessageSource2;
                                notificationType3 = (NotificationType)entry2;
                                n4 = n2;
                                n5 = n6;
                                break Label_0813;
                            }
                            unreadCount = n2;
                            if (b) {
                                continue Label_0434_Outer;
                            }
                            break;
                        }
                        break Label_0967;
                    }
                    notification = this.userManager.getObjectPopupsManager().getNotification(b);
                    if (b && (n4 != 0 || notification.freshObjectPopupsCount() != 0)) {
                        this.lastFreshMessageNotification.set(System.currentTimeMillis());
                    }
                    unreadCount = (n8 = 0);
                    if (list != null) {
                        n8 = unreadCount;
                        if (!list.isEmpty()) {
                            n8 = 1;
                        }
                    }
                    if (n5 != 0 || n8 == 0 || n4 != 0 || !notification.isEmpty()) {
                        builder.put((Chatter)notificationType2, UnreadNotificationInfo.create(this.userManager.getUserID(), n5, list, o2, n4, notificationType3, unreadMessageSource, notification));
                    }
                    continue;
                }
            }
            n2 = n3;
            id = o;
            n3 = unreadCount;
            chatter = (Chatter)obj;
            obj = id;
            continue Label_0402;
        }
    }
    
    private void setEnabledMask(final int newValue) {
        if (this.maskEnabled.getAndSet(newValue) != newValue) {
            this.updateUnreadNotifications();
        }
    }
    
    private void updateTypesFromPreferences(final SharedPreferences sharedPreferences) {
        boolean b = false;
        int enabledMask;
        if (NotificationChannels.getInstance().areNotificationsSystemControlled()) {
            enabledMask = 7;
        }
        else {
            if (sharedPreferences.getBoolean(NotificationType.LocalChat.getEnableKey(), true)) {
                b = true;
            }
            int n = b ? 1 : 0;
            if (sharedPreferences.getBoolean(NotificationType.Group.getEnableKey(), true)) {
                n = ((b ? 1 : 0) | 0x2);
            }
            enabledMask = n;
            if (sharedPreferences.getBoolean(NotificationType.Private.getEnableKey(), true)) {
                enabledMask = (n | 0x4);
            }
        }
        this.setEnabledMask(enabledMask);
    }
    
    private void updateUnreadChatterData() {
        final int value = this.maskEnabled.get();
        if (value == 0) {
            this.totalUnreadCount.set(0);
            this.totalSourcesCount.set(0);
            final Iterator<Map.Entry<Long, ChatterNameRetriever>> iterator = this.chatterSources.entrySet().iterator();
            while (iterator.hasNext()) {
                ((Map.Entry<K, ChatterNameRetriever>)iterator.next()).getValue().dispose();
                iterator.remove();
            }
            this.mostImportantNotificationType.set(null);
        }
        else {
            QueryBuilder<Chatter> queryBuilder = ((AbstractDao<Chatter, K>)this.chatterDao).queryBuilder().where(ChatterDao.Properties.UnreadCount.gt(0), ChatterDao.Properties.Muted.notEq(true));
            if (value != 7) {
                final ArrayList list = new ArrayList(3);
                if ((value & 0x1) != 0x0) {
                    list.add(ChatterID.ChatterType.Local.ordinal());
                }
                if ((value & 0x2) != 0x0) {
                    list.add(ChatterID.ChatterType.Group.ordinal());
                }
                if ((value & 0x4) != 0x0) {
                    list.add(ChatterID.ChatterType.User.ordinal());
                }
                queryBuilder = queryBuilder.where(ChatterDao.Properties.Type.in(list), new WhereCondition[0]);
            }
            final Iterator<Object> iterator2 = queryBuilder.orderDesc(ChatterDao.Properties.LastMessageID).listLazy().iterator();
            Set set = null;
            int newValue = 0;
            int newValue2 = 0;
            NotificationType notificationType = null;
            while (iterator2.hasNext()) {
                final Chatter chatter = iterator2.next();
                final ChatterID fromDatabaseObject = ChatterID.fromDatabaseObject(this.userManager.getUserID(), chatter);
                int n2;
                int n3;
                if (fromDatabaseObject != null) {
                    Set set2;
                    if ((set2 = set) == null) {
                        set2 = new HashSet();
                    }
                    if (set2.size() < 3) {
                        set2.add(chatter.getId());
                        if (!this.chatterSources.containsKey(chatter.getId())) {
                            this.chatterSources.put(chatter.getId(), new ChatterNameRetriever(fromDatabaseObject, (ChatterNameRetriever.OnChatterNameUpdated)this, null));
                        }
                    }
                    final NotificationType notificationType2 = fromDatabaseObject.getChatterType().getNotificationType();
                    NotificationType notificationType3 = null;
                    Label_0412: {
                        if (notificationType != null) {
                            notificationType3 = notificationType;
                            if (notificationType2.compareTo(notificationType) <= 0) {
                                break Label_0412;
                            }
                        }
                        notificationType3 = notificationType2;
                    }
                    final int unreadCount = chatter.getUnreadCount();
                    notificationType = notificationType3;
                    set = set2;
                    final int n = newValue + 1;
                    n2 = newValue2 + unreadCount;
                    n3 = n;
                }
                else {
                    final int n4 = newValue;
                    n2 = newValue2;
                    n3 = n4;
                }
                final int n5 = n2;
                newValue = n3;
                newValue2 = n5;
            }
            this.totalUnreadCount.set(newValue2);
            this.totalSourcesCount.set(newValue);
            this.mostImportantNotificationType.set(notificationType);
            final Iterator<Map.Entry<Long, ChatterNameRetriever>> iterator3 = this.chatterSources.entrySet().iterator();
            while (iterator3.hasNext()) {
                final Map.Entry<Object, V> entry = (Map.Entry<Object, V>)iterator3.next();
                if (set == null || (set.contains(entry.getKey()) ^ true)) {
                    ((ChatterNameRetriever)entry.getValue()).dispose();
                    iterator3.remove();
                }
            }
        }
    }
    
    public void addFreshMessage(@Nonnull Chatter freshMessageCountsLock) {
        final int n = 1;
        final Long id = freshMessageCountsLock.getId();
        if (id == null) {
            return;
        }
        final int value = this.maskEnabled.get();
        int intValue;
        Integer n2;
        Block_10_Outer:Label_0155_Outer:Label_0121_Outer:
        while (true) {
            Label_0200: {
                if (value == 0) {
                    break Label_0200;
                }
                Label_0095: {
                    if (value != 7) {
                        break Label_0095;
                    }
                    intValue = n;
                    if (intValue == 0) {
                        break Label_0095;
                    }
                    freshMessageCountsLock = (Chatter)this.freshMessageCountsLock;
                    synchronized (freshMessageCountsLock) {
                        n2 = this.freshMessageCounts.get(id);
                        if (n2 != null) {
                            intValue = n2;
                        }
                        else {
                            intValue = 0;
                        }
                        this.freshMessageCounts.put(id, intValue + 1);
                        return;
                        while (true) {
                        Block_9:
                            while (true) {
                            Label_0138:
                                while (true) {
                                    intValue = n;
                                    iftrue(Label_0035:)((value & 0x2) != 0x0);
                                    break Label_0138;
                                    intValue = 0;
                                    continue Block_10_Outer;
                                    freshMessageCountsLock = (Chatter)ChatterID.ChatterType.VALUES[freshMessageCountsLock.getType()];
                                    iftrue(Label_0121:)(freshMessageCountsLock != ChatterID.ChatterType.User);
                                    break Block_9;
                                    iftrue(Label_0138:)(freshMessageCountsLock != ChatterID.ChatterType.Group);
                                    continue Label_0155_Outer;
                                }
                                iftrue(Label_0155:)(freshMessageCountsLock != ChatterID.ChatterType.Local);
                                intValue = n;
                                iftrue(Label_0035:)((value & 0x1) != 0x0);
                                continue Label_0121_Outer;
                            }
                            intValue = n;
                            iftrue(Label_0035:)((value & 0x4) != 0x0);
                            continue;
                        }
                    }
                }
                synchronized (this.freshMessageCountsLock) {
                    this.freshMessageCounts.remove(id);
                    return;
                }
            }
            intValue = 0;
            continue;
        }
    }
    
    @Nullable
    public Intent captureNotify(final UnreadNotificationInfo unreadNotificationInfo, final Intent intent) {
        while (true) {
            while (true) {
                Label_0065: {
                    synchronized (this.notifyCaptureLock) {
                        if (this.notifyCapture == null) {
                            break Label_0065;
                        }
                        final NotifyCapture notifyCapture = this.notifyCapture.get();
                        monitorexit(this.notifyCaptureLock);
                        Debug.Printf("NotifyCapture: capture = %s", notifyCapture);
                        if (notifyCapture != null) {
                            return notifyCapture.onGetNotifyCaptureIntent(unreadNotificationInfo, intent);
                        }
                    }
                    break;
                }
                final NotifyCapture notifyCapture = null;
                continue;
            }
        }
        return null;
    }
    
    public void clearFreshMessages(@Nonnull final Chatter chatter) {
        final Long id = chatter.getId();
        if (id == null) {
            return;
        }
        synchronized (this.freshMessageCountsLock) {
            this.freshMessageCounts.remove(id);
        }
    }
    
    public void clearNotifyCapture(@Nullable final NotifyCapture notifyCapture) {
        synchronized (this.notifyCaptureLock) {
            if (this.notifyCapture != null && this.notifyCapture.get() == notifyCapture) {
                this.notifyCapture = null;
                this.updateUnreadNotifications();
            }
        }
    }
    
    public Subscribable<Boolean, UnreadNotifications> getUnreadNotifications() {
        return this.unreadNotificationInfoPool;
    }
    
    @Override
    public void onChatterNameUpdated(final ChatterNameRetriever chatterNameRetriever) {
        this.updateExecutor.execute(this.updateNotificationDataRunnable);
    }
    
    @EventHandler
    public void onGlobalPreferencesChanged(final GlobalOptions.GlobalOptionsChangedEvent globalOptionsChangedEvent) {
        if (globalOptionsChangedEvent.preferences != null) {
            this.updateTypesFromPreferences(globalOptionsChangedEvent.preferences);
        }
    }
    
    public void setNotifyCapture(@Nullable final NotifyCapture referent) {
        synchronized (this.notifyCaptureLock) {
            this.notifyCapture = new WeakReference<NotifyCapture>(referent);
            this.updateUnreadNotifications();
        }
    }
    
    void updateUnreadNotifications() {
        this.unreadNotificationInfoPool.requestUpdate(UnreadNotificationManager.unreadNotificationKey);
    }
    
    public interface NotifyCapture
    {
        @Nullable
        Intent onGetNotifyCaptureIntent(@Nonnull final UnreadNotificationInfo p0, final Intent p1);
    }
}
