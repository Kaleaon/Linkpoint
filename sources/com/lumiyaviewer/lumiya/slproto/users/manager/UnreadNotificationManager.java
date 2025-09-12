// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import android.content.Intent;
import android.content.SharedPreferences;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.dao.ChatMessageDao;
import com.lumiyaviewer.lumiya.dao.Chatter;
import com.lumiyaviewer.lumiya.dao.ChatterDao;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.ui.notify.NotificationChannels;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UserManager, UnreadNotificationInfo, ObjectPopupsManager, UnreadNotifications

public class UnreadNotificationManager
    implements com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
{
    public static interface NotifyCapture
    {

        public abstract Intent onGetNotifyCaptureIntent(UnreadNotificationInfo unreadnotificationinfo, Intent intent);
    }


    private static final long FRESH_MESSAGES_NOTIFICATION_INTERVAL = 3000L;
    private static final int MASK_ENABLED_ALL = 7;
    private static final int MASK_ENABLED_GROUP = 2;
    private static final int MASK_ENABLED_IM = 4;
    private static final int MASK_ENABLED_LOCAL = 1;
    private static final int MAX_CHATTERS_PER_NOTIFICATION = 3;
    private static final int MAX_MESSAGES_PER_NOTIFICATION = 3;
    public static final Boolean unreadNotificationKey;
    private final ChatMessageDao chatMessageDao;
    private final ChatterDao chatterDao;
    private final Map chatterSources = new ConcurrentHashMap(4, 0.75F, 2);
    private final UnreadNotificationInfo emptyNotification;
    private final Map freshMessageCounts = new HashMap();
    private final Object freshMessageCountsLock = new Object();
    private final AtomicLong lastFreshMessageNotification = new AtomicLong(0L);
    private final AtomicInteger maskEnabled = new AtomicInteger(7);
    private final AtomicReference mostImportantNotificationType = new AtomicReference();
    private WeakReference notifyCapture;
    private final Object notifyCaptureLock = new Object();
    private final AtomicInteger totalSourcesCount = new AtomicInteger(0);
    private final AtomicInteger totalUnreadCount = new AtomicInteger(0);
    private final SubscriptionPool unreadNotificationInfoPool = new SubscriptionPool();
    private final Runnable updateChatterDataRunnable = new Runnable() {

        final UnreadNotificationManager this$0;

        public void run()
        {
            UnreadNotificationManager._2D_wrap1(UnreadNotificationManager.this);
            UnreadNotificationManager._2D_get2(UnreadNotificationManager.this).execute(UnreadNotificationManager._2D_get3(UnreadNotificationManager.this));
        }

            
            {
                this$0 = UnreadNotificationManager.this;
                super();
            }
    };
    private final Executor updateExecutor;
    private final Runnable updateNotificationDataRunnable = new Runnable() {

        final UnreadNotificationManager this$0;

        public void run()
        {
            UnreadNotificationManager._2D_get0(UnreadNotificationManager.this).onResultData(UnreadNotificationManager.unreadNotificationKey, UnreadNotificationManager._2D_wrap0(UnreadNotificationManager.this));
        }

            
            {
                this$0 = UnreadNotificationManager.this;
                super();
            }
    };
    private final UserManager userManager;

    static SubscriptionPool _2D_get0(UnreadNotificationManager unreadnotificationmanager)
    {
        return unreadnotificationmanager.unreadNotificationInfoPool;
    }

    static Runnable _2D_get1(UnreadNotificationManager unreadnotificationmanager)
    {
        return unreadnotificationmanager.updateChatterDataRunnable;
    }

    static Executor _2D_get2(UnreadNotificationManager unreadnotificationmanager)
    {
        return unreadnotificationmanager.updateExecutor;
    }

    static Runnable _2D_get3(UnreadNotificationManager unreadnotificationmanager)
    {
        return unreadnotificationmanager.updateNotificationDataRunnable;
    }

    static UnreadNotifications _2D_wrap0(UnreadNotificationManager unreadnotificationmanager)
    {
        return unreadnotificationmanager.getUnreadNotification();
    }

    static void _2D_wrap1(UnreadNotificationManager unreadnotificationmanager)
    {
        unreadnotificationmanager.updateUnreadChatterData();
    }

    public UnreadNotificationManager(UserManager usermanager, DaoSession daosession)
    {
        notifyCapture = null;
        userManager = usermanager;
        chatterDao = daosession.getChatterDao();
        chatMessageDao = daosession.getChatMessageDao();
        updateExecutor = usermanager.getDatabaseRunOnceExecutor();
        emptyNotification = UnreadNotificationInfo.create(usermanager.getUserID(), 0, null, null, 0, null, null, UnreadNotificationInfo.ObjectPopupNotification.create(0, 0, null));
        unreadNotificationInfoPool.attachRequestHandler(new SimpleRequestHandler() {

            final UnreadNotificationManager this$0;

            public void onRequest(Boolean boolean1)
            {
                UnreadNotificationManager._2D_get2(UnreadNotificationManager.this).execute(UnreadNotificationManager._2D_get1(UnreadNotificationManager.this));
            }

            public volatile void onRequest(Object obj)
            {
                onRequest((Boolean)obj);
            }

            
            {
                this$0 = UnreadNotificationManager.this;
                super();
            }
        });
        updateTypesFromPreferences(LumiyaApp.getDefaultSharedPreferences());
        EventBus.getInstance().subscribe(this);
    }

    private UnreadNotifications getUnreadNotification()
    {
        Object obj;
        Object obj1;
        Object obj2;
        HashMap hashmap;
        com.google.common.collect.ImmutableMap.Builder builder;
        NotificationType notificationtype1;
        Object obj7;
        int i;
        int j;
        int k;
        int i1;
        int k1;
        boolean flag;
        Object obj3;
        NotificationType notificationtype;
        Iterator iterator;
        Iterator iterator1;
        Object obj8;
        if (System.currentTimeMillis() >= lastFreshMessageNotification.get() + 3000L)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        builder = ImmutableMap.builder();
        iterator = NotificationType.VALUES.iterator();
_L16:
        if (!iterator.hasNext()) goto _L2; else goto _L1
_L1:
        notificationtype1 = (NotificationType)iterator.next();
        j = 0;
        obj2 = null;
        if (chatterSources.isEmpty())
        {
            break MISSING_BLOCK_LABEL_979;
        }
        hashmap = new HashMap();
        iterator1 = chatterSources.entrySet().iterator();
        i = 0;
        k = 0;
        obj = null;
        obj2 = null;
        j = 0;
        obj1 = null;
_L15:
        if (!iterator1.hasNext()) goto _L4; else goto _L3
_L3:
        obj3 = (java.util.Map.Entry)iterator1.next();
        obj7 = (ChatterNameRetriever)((java.util.Map.Entry) (obj3)).getValue();
        obj8 = ((ChatterNameRetriever) (obj7)).chatterID;
        notificationtype = ((ChatterID) (obj8)).getChatterType().getNotificationType();
        if (notificationtype != notificationtype1) goto _L6; else goto _L5
_L5:
        if (((ChatterID) (obj8)).getChatterType() != com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Local && ((ChatterNameRetriever) (obj7)).getResolvedName() == null) goto _L8; else goto _L7
_L7:
        obj3 = (Chatter)chatterDao.load((Long)((java.util.Map.Entry) (obj3)).getKey());
        if (obj3 == null) goto _L6; else goto _L9
_L9:
        i1 = ((Chatter) (obj3)).getUnreadCount();
        k += i1;
        obj7 = UnreadNotificationInfo.UnreadMessageSource.create(((ChatterID) (obj8)), ((ChatterNameRetriever) (obj7)).getResolvedName(), null, i1);
        hashmap.put(((Chatter) (obj3)).getId(), obj7);
        if (obj2 == null || notificationtype.compareTo(((Enum) (obj2))) > 0)
        {
            obj2 = notificationtype;
        }
        i1 = i;
        if (!flag) goto _L11; else goto _L10
_L10:
        obj7 = freshMessageCountsLock;
        obj7;
        JVM INSTR monitorenter ;
        obj8 = (Integer)freshMessageCounts.remove(((Chatter) (obj3)).getId());
        if (obj8 == null) goto _L13; else goto _L12
_L12:
        k1 = ((Integer) (obj8)).intValue();
_L18:
        obj7;
        JVM INSTR monitorexit ;
        i += k1;
        i1 = i;
        if (k1 == 0) goto _L11; else goto _L14
_L14:
        Object obj4;
        if (obj1 == null && (j ^ 1) != 0)
        {
            obj4 = ((Chatter) (obj3)).getId();
        } else
        {
            obj4 = obj1;
            if (obj1 != null)
            {
                obj4 = null;
                j = 1;
            }
        }
        if (obj == null || notificationtype.compareTo(((Enum) (obj))) > 0)
        {
            i1 = j;
            obj = obj4;
            obj1 = notificationtype;
            j = i;
            i = i1;
        } else
        {
            obj1 = obj;
            i1 = i;
            i = j;
            obj = obj4;
            j = i1;
        }
_L17:
        i1 = j;
        obj4 = obj1;
        j = i;
        obj1 = obj;
        obj = obj4;
        i = i1;
          goto _L15
_L8:
        obj4 = obj;
        i1 = i;
        i = j;
        obj = obj1;
        obj1 = obj4;
        j = i1;
        break MISSING_BLOCK_LABEL_396;
        obj;
        throw obj;
_L4:
        Object obj5;
        ArrayList arraylist;
        Iterator iterator2;
        if (hashmap.size() <= 1)
        {
            j = 3;
        } else
        {
            j = 1;
        }
        arraylist = new ArrayList(hashmap.size());
        iterator2 = hashmap.entrySet().iterator();
        obj5 = null;
        Object obj6;
        for (; iterator2.hasNext(); arraylist.add(obj6))
        {
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator2.next();
            obj6 = new LinkedList();
            k1 = ((UnreadNotificationInfo.UnreadMessageSource)entry.getValue()).unreadMessagesCount();
            i1 = k1;
            if (k1 > j)
            {
                i1 = j;
            }
            Iterator iterator3 = chatMessageDao.queryBuilder().where(com.lumiyaviewer.lumiya.dao.ChatMessageDao.Properties.ChatterID.eq(entry.getKey()), new WhereCondition[0]).orderDesc(new Property[] {
                com.lumiyaviewer.lumiya.dao.ChatMessageDao.Properties.Id
            }).limit(i1).list().iterator();
            do
            {
                if (!iterator3.hasNext())
                {
                    break;
                }
                SLChatEvent slchatevent = SLChatEvent.loadFromDatabaseObject((ChatMessage)iterator3.next(), userManager.getUserID());
                if (slchatevent != null)
                {
                    ((List) (obj6)).add(0, slchatevent);
                }
            } while (true);
            obj6 = ((UnreadNotificationInfo.UnreadMessageSource)entry.getValue()).withMessages(((List) (obj6)));
            if (obj1 != null && ((Long)entry.getKey()).equals(obj1))
            {
                obj5 = obj6;
            }
        }

        obj1 = arraylist;
        j = i;
        i = k;
_L19:
        UnreadNotificationInfo.ObjectPopupNotification objectpopupnotification = userManager.getObjectPopupsManager().getNotification(flag);
        if (flag && (j != 0 || objectpopupnotification.freshObjectPopupsCount() != 0))
        {
            lastFreshMessageNotification.set(System.currentTimeMillis());
        }
        i1 = 0;
        int l = i1;
        if (obj1 != null)
        {
            l = i1;
            if (!((List) (obj1)).isEmpty())
            {
                l = 1;
            }
        }
        boolean flag1;
        if (i == 0 && l != 0 && j == 0)
        {
            flag1 = objectpopupnotification.isEmpty();
        } else
        {
            flag1 = false;
        }
        if (!flag1)
        {
            builder.put(notificationtype1, UnreadNotificationInfo.create(userManager.getUserID(), i, ((List) (obj1)), ((NotificationType) (obj2)), j, ((NotificationType) (obj)), ((UnreadNotificationInfo.UnreadMessageSource) (obj5)), objectpopupnotification));
        }
          goto _L16
_L2:
        return UnreadNotifications.create(userManager.getUserID(), builder.build());
_L11:
        i = j;
        obj5 = obj;
        j = i1;
        obj = obj1;
        obj1 = obj5;
          goto _L17
_L13:
        k1 = 0;
          goto _L18
_L6:
        obj5 = obj;
        int j1 = i;
        i = j;
        obj = obj1;
        obj1 = obj5;
        j = j1;
          goto _L17
        i = 0;
        obj = null;
        obj1 = null;
        obj5 = null;
          goto _L19
    }

    private void setEnabledMask(int i)
    {
        if (maskEnabled.getAndSet(i) != i)
        {
            updateUnreadNotifications();
        }
    }

    private void updateTypesFromPreferences(SharedPreferences sharedpreferences)
    {
        int j = 0;
        if (!NotificationChannels.getInstance().areNotificationsSystemControlled()) goto _L2; else goto _L1
_L1:
        j = 7;
_L4:
        setEnabledMask(j);
        return;
_L2:
        if (sharedpreferences.getBoolean(NotificationType.LocalChat.getEnableKey(), true))
        {
            j = 1;
        }
        int i = j;
        if (sharedpreferences.getBoolean(NotificationType.Group.getEnableKey(), true))
        {
            i = j | 2;
        }
        j = i;
        if (sharedpreferences.getBoolean(NotificationType.Private.getEnableKey(), true))
        {
            j = i | 4;
        }
        if (true) goto _L4; else goto _L3
_L3:
    }

    private void updateUnreadChatterData()
    {
        int i = maskEnabled.get();
        if (i != 0) goto _L2; else goto _L1
_L1:
        totalUnreadCount.set(0);
        totalSourcesCount.set(0);
        for (Iterator iterator = chatterSources.entrySet().iterator(); iterator.hasNext(); iterator.remove())
        {
            ((ChatterNameRetriever)((java.util.Map.Entry)iterator.next()).getValue()).dispose();
        }

        mostImportantNotificationType.set(null);
_L5:
        return;
_L2:
        Object obj;
        Object obj1;
        Iterator iterator1;
        int j;
        obj1 = chatterDao.queryBuilder().where(com.lumiyaviewer.lumiya.dao.ChatterDao.Properties.UnreadCount.gt(Integer.valueOf(0)), new WhereCondition[] {
            com.lumiyaviewer.lumiya.dao.ChatterDao.Properties.Muted.notEq(Boolean.valueOf(true))
        });
        obj = obj1;
        if (i != 7)
        {
            obj = new ArrayList(3);
            if ((i & 1) != 0)
            {
                ((List) (obj)).add(Integer.valueOf(com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Local.ordinal()));
            }
            if ((i & 2) != 0)
            {
                ((List) (obj)).add(Integer.valueOf(com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Group.ordinal()));
            }
            if ((i & 4) != 0)
            {
                ((List) (obj)).add(Integer.valueOf(com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.User.ordinal()));
            }
            obj = ((QueryBuilder) (obj1)).where(com.lumiyaviewer.lumiya.dao.ChatterDao.Properties.Type.in(((java.util.Collection) (obj))), new WhereCondition[0]);
        }
        iterator1 = ((QueryBuilder) (obj)).orderDesc(new Property[] {
            com.lumiyaviewer.lumiya.dao.ChatterDao.Properties.LastMessageID
        }).listLazy().iterator();
        obj1 = null;
        j = 0;
        i = 0;
        obj = null;
_L4:
        Object obj2;
        Chatter chatter;
label0:
        {
            if (!iterator1.hasNext())
            {
                break; /* Loop/switch isn't completed */
            }
            chatter = (Chatter)iterator1.next();
            Object obj3 = ChatterID.fromDatabaseObject(userManager.getUserID(), chatter);
            if (obj3 == null)
            {
                break MISSING_BLOCK_LABEL_560;
            }
            obj2 = obj1;
            if (obj1 == null)
            {
                obj2 = new HashSet();
            }
            if (((Set) (obj2)).size() < 3)
            {
                ((Set) (obj2)).add(chatter.getId());
                if (!chatterSources.containsKey(chatter.getId()))
                {
                    chatterSources.put(chatter.getId(), new ChatterNameRetriever(((ChatterID) (obj3)), this, null));
                }
            }
            obj3 = ((ChatterID) (obj3)).getChatterType().getNotificationType();
            if (obj != null)
            {
                obj1 = obj;
                if (((NotificationType) (obj3)).compareTo(((Enum) (obj))) <= 0)
                {
                    break label0;
                }
            }
            obj1 = obj3;
        }
        int j1 = chatter.getUnreadCount();
        obj = obj1;
        obj1 = obj2;
        int k = j + 1;
        j = i + j1;
        i = k;
_L6:
        int l = j;
        j = i;
        i = l;
        if (true) goto _L4; else goto _L3
_L3:
        totalUnreadCount.set(i);
        totalSourcesCount.set(j);
        mostImportantNotificationType.set(obj);
        obj = chatterSources.entrySet().iterator();
        while (((Iterator) (obj)).hasNext()) 
        {
            java.util.Map.Entry entry = (java.util.Map.Entry)((Iterator) (obj)).next();
            if (obj1 == null || ((Set) (obj1)).contains(entry.getKey()) ^ true)
            {
                ((ChatterNameRetriever)entry.getValue()).dispose();
                ((Iterator) (obj)).remove();
            }
        }
          goto _L5
        int i1 = i;
        i = j;
        j = i1;
          goto _L6
    }

    public void addFreshMessage(Chatter chatter)
    {
        Object obj;
        boolean flag;
        flag = true;
        obj = chatter.getId();
        if (obj == null) goto _L2; else goto _L1
_L1:
        int j = maskEnabled.get();
        if (j == 0) goto _L4; else goto _L3
_L3:
        if (j != 7) goto _L6; else goto _L5
_L5:
        int i = ((flag) ? 1 : 0);
_L13:
        if (i == 0) goto _L8; else goto _L7
_L7:
        chatter = ((Chatter) (freshMessageCountsLock));
        chatter;
        JVM INSTR monitorenter ;
        Integer integer = (Integer)freshMessageCounts.get(obj);
        if (integer == null) goto _L10; else goto _L9
_L9:
        i = integer.intValue();
_L11:
        freshMessageCounts.put(obj, Integer.valueOf(i + 1));
        chatter;
        JVM INSTR monitorexit ;
_L2:
        return;
_L6:
        chatter = com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.VALUES[chatter.getType()];
        if (chatter == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.User)
        {
            i = ((flag) ? 1 : 0);
            if ((j & 4) != 0)
            {
                continue; /* Loop/switch isn't completed */
            }
        }
        if (chatter == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Group)
        {
            i = ((flag) ? 1 : 0);
            if ((j & 2) != 0)
            {
                continue; /* Loop/switch isn't completed */
            }
        }
        if (chatter == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Local)
        {
            i = ((flag) ? 1 : 0);
            if ((j & 1) != 0)
            {
                continue; /* Loop/switch isn't completed */
            }
        }
        i = 0;
        continue; /* Loop/switch isn't completed */
_L10:
        i = 0;
          goto _L11
        obj;
        throw obj;
_L8:
        chatter = ((Chatter) (freshMessageCountsLock));
        chatter;
        JVM INSTR monitorenter ;
        freshMessageCounts.remove(obj);
        chatter;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
_L4:
        i = 0;
        if (true) goto _L13; else goto _L12
_L12:
    }

    public Intent captureNotify(UnreadNotificationInfo unreadnotificationinfo, Intent intent)
    {
        Object obj = notifyCaptureLock;
        obj;
        JVM INSTR monitorenter ;
        if (notifyCapture == null) goto _L2; else goto _L1
_L1:
        NotifyCapture notifycapture = (NotifyCapture)notifyCapture.get();
_L4:
        obj;
        JVM INSTR monitorexit ;
        Debug.Printf("NotifyCapture: capture = %s", new Object[] {
            notifycapture
        });
        if (notifycapture != null)
        {
            return notifycapture.onGetNotifyCaptureIntent(unreadnotificationinfo, intent);
        } else
        {
            return null;
        }
        unreadnotificationinfo;
        throw unreadnotificationinfo;
_L2:
        notifycapture = null;
        if (true) goto _L4; else goto _L3
_L3:
    }

    public void clearFreshMessages(Chatter chatter)
    {
        Long long1 = chatter.getId();
        if (long1 == null) goto _L2; else goto _L1
_L1:
        chatter = ((Chatter) (freshMessageCountsLock));
        chatter;
        JVM INSTR monitorenter ;
        freshMessageCounts.remove(long1);
        chatter;
        JVM INSTR monitorexit ;
_L2:
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void clearNotifyCapture(NotifyCapture notifycapture)
    {
        Object obj = notifyCaptureLock;
        obj;
        JVM INSTR monitorenter ;
        if (notifyCapture != null && notifyCapture.get() == notifycapture)
        {
            notifyCapture = null;
            updateUnreadNotifications();
        }
        obj;
        JVM INSTR monitorexit ;
        return;
        notifycapture;
        throw notifycapture;
    }

    public Subscribable getUnreadNotifications()
    {
        return unreadNotificationInfoPool;
    }

    public void onChatterNameUpdated(ChatterNameRetriever chatternameretriever)
    {
        updateExecutor.execute(updateNotificationDataRunnable);
    }

    public void onGlobalPreferencesChanged(com.lumiyaviewer.lumiya.GlobalOptions.GlobalOptionsChangedEvent globaloptionschangedevent)
    {
        if (globaloptionschangedevent.preferences != null)
        {
            updateTypesFromPreferences(globaloptionschangedevent.preferences);
        }
    }

    public void setNotifyCapture(NotifyCapture notifycapture)
    {
        Object obj = notifyCaptureLock;
        obj;
        JVM INSTR monitorenter ;
        notifyCapture = new WeakReference(notifycapture);
        updateUnreadNotifications();
        obj;
        JVM INSTR monitorexit ;
        return;
        notifycapture;
        throw notifycapture;
    }

    void updateUnreadNotifications()
    {
        unreadNotificationInfoPool.requestUpdate(unreadNotificationKey);
    }

    static 
    {
        unreadNotificationKey = Boolean.FALSE;
    }
}
