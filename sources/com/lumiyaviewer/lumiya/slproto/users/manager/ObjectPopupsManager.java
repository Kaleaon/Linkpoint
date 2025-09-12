// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            SubscribableList, UserManager, UnreadNotificationManager

public class ObjectPopupsManager
{
    public static interface ObjectPopupListener
    {

        public abstract void onNewObjectPopup(SLChatEvent slchatevent);

        public abstract void onObjectPopupCountChanged(int i);
    }


    private static final int MAX_POPUPS = 99;
    private SLChatEvent displayedPopupEvent;
    private final AtomicInteger freshPopupsCount = new AtomicInteger(0);
    private SLChatEvent lastEvent;
    private final Object listenerLock = new Object();
    private WeakReference objectPopupListener;
    private Executor objectPopupListenerExecutor;
    private final SubscribableList objectPopups = new SubscribableList();
    private boolean popupAnimated;
    private final Set popupWatchers = Collections.newSetFromMap(new WeakHashMap());
    private final AtomicInteger unreadPopupCount = new AtomicInteger(0);
    private final UserManager userManager;

    ObjectPopupsManager(UserManager usermanager)
    {
        objectPopupListener = null;
        objectPopupListenerExecutor = null;
        displayedPopupEvent = null;
        lastEvent = null;
        popupAnimated = false;
        userManager = usermanager;
    }

    private void addObjectPopupInternal(SLChatEvent slchatevent)
    {
        objectPopups.add(0, slchatevent);
        while (objectPopups.size() > 99) 
        {
            try
            {
                objectPopups.remove(objectPopups.size() - 1);
            }
            // Misplaced declaration of an exception variable
            catch (SLChatEvent slchatevent)
            {
                Debug.Warning(slchatevent);
            }
        }
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_ObjectPopupsManager_3306(ObjectPopupListener objectpopuplistener, int i)
    {
        objectpopuplistener.onObjectPopupCountChanged(i);
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_ObjectPopupsManager_3571(ObjectPopupListener objectpopuplistener, SLChatEvent slchatevent)
    {
        objectpopuplistener.onNewObjectPopup(slchatevent);
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_ObjectPopupsManager_6669(ObjectPopupListener objectpopuplistener, int i)
    {
        objectpopuplistener.onObjectPopupCountChanged(i);
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_ObjectPopupsManager_7628(ObjectPopupListener objectpopuplistener)
    {
        objectpopuplistener.onNewObjectPopup(null);
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_ObjectPopupsManager_8558(ObjectPopupListener objectpopuplistener)
    {
        objectpopuplistener.onObjectPopupCountChanged(0);
        objectpopuplistener.onNewObjectPopup(null);
    }

    private void notifyCountUpdated()
    {
        Object obj = null;
        Object obj1 = listenerLock;
        obj1;
        JVM INSTR monitorenter ;
        if (objectPopupListener == null) goto _L2; else goto _L1
_L1:
        Executor executor;
        obj = (ObjectPopupListener)objectPopupListener.get();
        executor = objectPopupListenerExecutor;
_L4:
        obj1;
        JVM INSTR monitorexit ;
        int i;
        if (obj != null)
        {
            i = objectPopups.size();
            if (executor == null)
            {
                break; /* Loop/switch isn't completed */
            }
            executor.execute(new _2D_.Lambda.gJtxV6TiuzFNXMR7_2D_6og75a4tFE._cls4(i, obj));
        }
        return;
_L2:
        executor = null;
        if (true) goto _L4; else goto _L3
        obj;
        throw obj;
_L3:
        ((ObjectPopupListener) (obj)).onObjectPopupCountChanged(i);
        return;
    }

    void addObjectPopup(SLChatEvent slchatevent)
    {
        ObjectPopupListener objectpopuplistener;
        boolean flag;
        flag = false;
        objectpopuplistener = null;
        Object obj = listenerLock;
        obj;
        JVM INSTR monitorenter ;
        if (objectPopupListener == null) goto _L2; else goto _L1
_L1:
        ObjectPopupListener objectpopuplistener1;
        Executor executor1;
        objectpopuplistener1 = (ObjectPopupListener)objectPopupListener.get();
        executor1 = objectPopupListenerExecutor;
        Executor executor;
        int i;
        objectpopuplistener = objectpopuplistener1;
        i = ((flag) ? 1 : 0);
        executor = executor1;
        if (objectpopuplistener1 == null)
        {
            break MISSING_BLOCK_LABEL_90;
        }
        objectpopuplistener = objectpopuplistener1;
        i = ((flag) ? 1 : 0);
        executor = executor1;
        if (displayedPopupEvent != null)
        {
            break MISSING_BLOCK_LABEL_90;
        }
        displayedPopupEvent = slchatevent;
        popupAnimated = false;
        i = 1;
        executor = executor1;
        objectpopuplistener = objectpopuplistener1;
_L4:
        if (!(popupWatchers.isEmpty() ^ true))
        {
            freshPopupsCount.incrementAndGet();
            unreadPopupCount.incrementAndGet();
            lastEvent = slchatevent;
        }
        obj;
        JVM INSTR monitorexit ;
        if (i == 0)
        {
            addObjectPopupInternal(slchatevent);
            i = objectPopups.size();
            if (objectpopuplistener != null)
            {
                if (executor != null)
                {
                    executor.execute(new _2D_.Lambda.gJtxV6TiuzFNXMR7_2D_6og75a4tFE._cls3(i, objectpopuplistener));
                } else
                {
                    objectpopuplistener.onObjectPopupCountChanged(i);
                }
            }
        } else
        if (executor != null)
        {
            executor.execute(new _2D_.Lambda.gJtxV6TiuzFNXMR7_2D_6og75a4tFE._cls2(objectpopuplistener, slchatevent));
        } else
        {
            objectpopuplistener.onNewObjectPopup(slchatevent);
        }
        userManager.getUnreadNotificationManager().updateUnreadNotifications();
        return;
_L2:
        executor = null;
        i = ((flag) ? 1 : 0);
        if (true) goto _L4; else goto _L3
_L3:
        slchatevent;
        throw slchatevent;
    }

    public void addPopupWatcher(Object obj)
    {
        Object obj1 = listenerLock;
        obj1;
        JVM INSTR monitorenter ;
        popupWatchers.add(obj);
        freshPopupsCount.set(0);
        unreadPopupCount.set(0);
        obj1;
        JVM INSTR monitorexit ;
        userManager.getUnreadNotificationManager().updateUnreadNotifications();
        return;
        obj;
        throw obj;
    }

    public void cancelObjectPopup(SLChatEvent slchatevent)
    {
        Object obj = listenerLock;
        obj;
        JVM INSTR monitorenter ;
        if (slchatevent == null) goto _L2; else goto _L1
_L1:
        if (slchatevent != displayedPopupEvent) goto _L2; else goto _L3
_L3:
        displayedPopupEvent = null;
        if (objectPopupListener == null) goto _L5; else goto _L4
_L4:
        ObjectPopupListener objectpopuplistener = (ObjectPopupListener)objectPopupListener.get();
_L6:
        Executor executor = objectPopupListenerExecutor;
_L7:
        if (slchatevent == lastEvent)
        {
            lastEvent = null;
        }
        obj;
        JVM INSTR monitorexit ;
        if (objectpopuplistener != null)
        {
            if (executor != null)
            {
                executor.execute(new _2D_.Lambda.gJtxV6TiuzFNXMR7_2D_6og75a4tFE(objectpopuplistener));
            } else
            {
                objectpopuplistener.onNewObjectPopup(null);
            }
        }
        if (objectPopups.remove(slchatevent))
        {
            notifyCountUpdated();
        }
        userManager.getUnreadNotificationManager().updateUnreadNotifications();
        return;
_L5:
        objectpopuplistener = null;
          goto _L6
_L2:
        objectpopuplistener = null;
        executor = null;
          goto _L7
        slchatevent;
        throw slchatevent;
          goto _L6
    }

    void clearObjectPopups()
    {
        Object obj1 = listenerLock;
        obj1;
        JVM INSTR monitorenter ;
        Object obj;
        displayedPopupEvent = null;
        if (objectPopupListener == null)
        {
            break MISSING_BLOCK_LABEL_98;
        }
        obj = (ObjectPopupListener)objectPopupListener.get();
_L1:
        Executor executor;
        executor = objectPopupListenerExecutor;
        unreadPopupCount.set(0);
        freshPopupsCount.set(0);
        lastEvent = null;
        obj1;
        JVM INSTR monitorexit ;
        objectPopups.clear();
        if (obj != null)
        {
            if (executor != null)
            {
                executor.execute(new _2D_.Lambda.gJtxV6TiuzFNXMR7_2D_6og75a4tFE._cls1(obj));
            } else
            {
                ((ObjectPopupListener) (obj)).onObjectPopupCountChanged(0);
                ((ObjectPopupListener) (obj)).onNewObjectPopup(null);
            }
        }
        userManager.getUnreadNotificationManager().updateUnreadNotifications();
        return;
        obj = null;
          goto _L1
        obj;
        throw obj;
    }

    public void dismissDisplayedObjectPopup(SLChatEvent slchatevent)
    {
        Object obj = listenerLock;
        obj;
        JVM INSTR monitorenter ;
        if (slchatevent != displayedPopupEvent) goto _L2; else goto _L1
_L1:
        displayedPopupEvent = null;
_L3:
        freshPopupsCount.set(0);
        unreadPopupCount.set(0);
        obj;
        JVM INSTR monitorexit ;
        if (slchatevent != null)
        {
            addObjectPopupInternal(slchatevent);
            notifyCountUpdated();
        }
        userManager.getUnreadNotificationManager().updateUnreadNotifications();
        return;
_L2:
        if (slchatevent != null)
        {
            break MISSING_BLOCK_LABEL_84;
        }
        slchatevent = displayedPopupEvent;
        displayedPopupEvent = null;
          goto _L3
        slchatevent;
        throw slchatevent;
        slchatevent = null;
          goto _L3
    }

    public SLChatEvent getDisplayedObjectPopup()
    {
        Object obj = listenerLock;
        obj;
        JVM INSTR monitorenter ;
        SLChatEvent slchatevent = displayedPopupEvent;
        obj;
        JVM INSTR monitorexit ;
        return slchatevent;
        Exception exception;
        exception;
        throw exception;
    }

    UnreadNotificationInfo.ObjectPopupNotification getNotification(boolean flag)
    {
        Object obj1 = listenerLock;
        obj1;
        JVM INSTR monitorenter ;
        int j = unreadPopupCount.get();
        if (!flag) goto _L2; else goto _L1
_L1:
        int i = freshPopupsCount.getAndSet(0);
_L4:
        Object obj = null;
        if (lastEvent instanceof SLChatTextEvent)
        {
            obj = UnreadNotificationInfo.ObjectPopupMessage.create(lastEvent.getSource().getSourceName(userManager), ((SLChatTextEvent)lastEvent).getRawText());
        }
        obj = UnreadNotificationInfo.ObjectPopupNotification.create(i, j, ((UnreadNotificationInfo.ObjectPopupMessage) (obj)));
        obj1;
        JVM INSTR monitorexit ;
        return ((UnreadNotificationInfo.ObjectPopupNotification) (obj));
_L2:
        i = 0;
        if (true) goto _L4; else goto _L3
_L3:
        Exception exception;
        exception;
        throw exception;
    }

    public int getObjectPopupCount()
    {
        return objectPopups.size();
    }

    public SubscribableList getObjectPopups()
    {
        return objectPopups;
    }

    public boolean mustAnimatePopup(SLChatEvent slchatevent)
    {
        boolean flag = false;
        Object obj = listenerLock;
        obj;
        JVM INSTR monitorenter ;
        if (slchatevent == displayedPopupEvent)
        {
            flag = popupAnimated ^ true;
            popupAnimated = true;
        }
        obj;
        JVM INSTR monitorexit ;
        return flag;
        slchatevent;
        throw slchatevent;
    }

    public void removeObjectPopupListener(ObjectPopupListener objectpopuplistener)
    {
        Object obj = listenerLock;
        obj;
        JVM INSTR monitorenter ;
        if (objectPopupListener != null && objectPopupListener.get() == objectpopuplistener)
        {
            objectPopupListener = null;
            objectPopupListenerExecutor = null;
        }
        obj;
        JVM INSTR monitorexit ;
        return;
        objectpopuplistener;
        throw objectpopuplistener;
    }

    public void removePopupWatcher(Object obj)
    {
        Object obj1 = listenerLock;
        obj1;
        JVM INSTR monitorenter ;
        popupWatchers.remove(obj);
        obj1;
        JVM INSTR monitorexit ;
        return;
        obj;
        throw obj;
    }

    public void setObjectPopupListener(ObjectPopupListener objectpopuplistener, Executor executor)
    {
        Object obj = listenerLock;
        obj;
        JVM INSTR monitorenter ;
        objectPopupListener = new WeakReference(objectpopuplistener);
        objectPopupListenerExecutor = executor;
        obj;
        JVM INSTR monitorexit ;
        return;
        objectpopuplistener;
        throw objectpopuplistener;
    }
}
